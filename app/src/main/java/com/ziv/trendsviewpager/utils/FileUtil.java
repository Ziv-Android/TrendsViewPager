package com.ziv.trendsviewpager.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class FileUtil {
	private static final String TAG = "FileUtil";
	private static final FileComparator sFileComparator = new FileComparator();
	public static final String IMAGE_JPG = ".jpg";
	public static final String IMAGE_JPEG = ".jpeg";
	@SuppressLint("SdCardPath")
	public static final String SYSTEM_PHOTO_PATH = "/sdcard/DCIM/Camera/";

	/**
	 * file is exist and the file is not Directory
	 * 
	 * @param path
	 * @return
	 */
	public static boolean isFileExist(String path) {
		File file = new File(path);
		return file.exists() && !file.isDirectory();
	}
	
	/**
	 * file is exist and the file is Directory
	 * 
	 * @param dirPath
	 * @return
	 */
	public static boolean isDirExist(String dirPath) {
		File file = new File(dirPath);
		return file.exists() && file.isDirectory();
	}
	
	public static boolean isPathExist(String path) {
		if (StringUtil.isEmptyString(path)) {
			printLog("isPathExist path is null");
			return false;
		}
		return new File(path).exists();
	}

	/**
	 * Create Directory For Path
	 * 
	 * @param path
	 * @return
	 */
	public static boolean createDirectory(String path) {
		if (TextUtils.isEmpty(path)) {
			printLog("createDirectory path is empty");
			return false;
		}
		File dirFile = new File(path);
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			return dirFile.mkdirs();
		}
		return true;
	}

	/**
	 * assert_path resources copy into Local SD card
	 * 
	 * @param assertPathDir
	 * @param dirPath
	 */
	public static void copyAssertDirToLocalIfNeed(Context context,String assertPathDir,String dirPath,boolean isCover) {
		File pictureDir = new File(dirPath);
		if (!pictureDir.exists() || !pictureDir.isDirectory()) {
			pictureDir.mkdirs();
		}
		try {
			String[] fileNames = context.getAssets().list(assertPathDir);
			if (fileNames.length == 0)
				return;
			for (int i = 0; i < fileNames.length; i++) {
				File file = new File(dirPath+File.separator + fileNames[i]);
				if (file.exists() && file.isFile() && !isCover) {
					printLog("copyAssertDirToLocalIfNeed " + file.getName() + " exists");
					continue;
				}
				InputStream is = context.getAssets()
						.open(assertPathDir + File.separator + fileNames[i]);
				int size = is.available();
				byte[] buffer = new byte[size];
				is.read(buffer);
				is.close();
				String mypath = dirPath+File.separator + fileNames[i];
				FileOutputStream fop = new FileOutputStream(mypath);
				fop.write(buffer);
				fop.flush();
				fop.close();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void copyAssertFileToLocalIfNeed(Context context,String assertPath, String strOutFileName,boolean isCover) throws IOException {
		File file = new File(strOutFileName);
		if (file.exists() && file.isFile() && !isCover) {
			printLog("copyAssertFileToLocalIfNeed " + file.getName() + " exists");
			return;
		}
        InputStream myInput;  
        OutputStream myOutput = new FileOutputStream(strOutFileName);  
        myInput = context.getAssets().open(assertPath);  
        byte[] buffer = new byte[1024];  
        int length = myInput.read(buffer);
        while(length > 0)
        {
            myOutput.write(buffer, 0, length); 
            length = myInput.read(buffer);
        }
        myOutput.flush();  
        myInput.close();  
        myOutput.close();        
    }
	
	
	public static void deleteFile(String path) {
		if(StringUtil.isEmptyString(path)){
			printLog("deleteFile path is null");
			return;
		}
		File file = new File(path);
		deleteFile(file);
	}
	
	
	/**
	 * delete file by File
	 * 
	 * @param file
	 */
	public static void deleteFile(File file) {
		if (null == file) {
			printLog("deleteFile file is null");
			return;
		}
		
		if(!file.exists() || !file.isFile()){
			printLog("deleteFile file is not exists or file is dir!");
			return;
		}

		file.delete();
	}
	
	
	public static void clearDir(String path,boolean isDeleteThisDir) {
		if(StringUtil.isEmptyString(path)){
			printLog("clearDir path is null");
			return;
		}
		File file = new File(path);
		clearDir(file,isDeleteThisDir);
	}

	/**
	 * Delete the dir in SD card
	 * 
	 * @param dirPath 
	 * @param isDeleteThisDir
	 *   
	 */
	public static void clearDir(File dirFile,boolean isDeleteThisDir) {
		if (!StorageUtil.checkSDCardAvailable() || dirFile == null ) {
			printLog("clearDir dirFile is null");
			return;
		}
		try {
			if (dirFile.isDirectory()) {// 处理目录
				File files[] = dirFile.listFiles();
				for (int i = 0; i < files.length; i++) {
					clearDir(files[i], true);
				}
			}
			if (isDeleteThisDir) {
				if (!dirFile.isDirectory()) {// 如果是文件，删除
					dirFile.delete();
				} else {// 目录
					if (dirFile.listFiles().length == 0) {// 目录下没有文件或者目录，删除
						dirFile.delete();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * a specified path whether file Or directory is exist
	 * 
	 * @param path
	 * @return
	 */
	/**
	 * get System Camera Photo Path
	 * 
	 * @return String
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getSystemCameraPhotoPath() {
		printLog("genSystemCameraPhotoPath SYSTEM_PHOTO_PATH ---> "+ SYSTEM_PHOTO_PATH);
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		createDirectory(SYSTEM_PHOTO_PATH);
		return SYSTEM_PHOTO_PATH + "IMAGE_" + timeStamp + IMAGE_JPG;
	}

	/**
	 * get the file name if file is exist
	 * 
	 * @param path
	 * @return
	 */
	public static String getFileName(String path) {
		File file = new File(path);
		if (file.exists()) {
			return file.getName();
		}
		return null;
	}

	/**
	 * The imageList according to the specified path
	 * 
	 * @param path
	 * @return List<String>
	 */
	public static List<String> loadChildFiles(String dir) {
		if (StringUtil.isEmptyString(dir)) {
			printLog("loadImages---> path is null");
			return null;
		}
		List<String> fileList = new ArrayList<String>();
		File file = new File(dir);
		if (!file.exists()) {
			return fileList;
		}
		File[] files = file.listFiles();
		List<File> allFiles = new ArrayList<File>();
		for (File img : files) {
			allFiles.add(img);
		}
		Collections.sort(allFiles, sFileComparator);
		for (File img : allFiles) {
			fileList.add(img.getAbsolutePath());
		}
		return fileList;
	}

	/**
	 * put the file into byte[]
	 * 
	 * @param fileName
	 * @return byte[]
	 */
	public static byte[] readFile(String path) {
		if (StringUtil.isEmptyString(path) && isPathExist(path)) {
			printLog("readFile path is null");
			return null;
		}
		try {
			FileInputStream fis = new FileInputStream(path);
			int length = fis.available();
			byte[] buffer = new byte[length];
			fis.read(buffer);
			fis.close();
			return buffer;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * save the file to specify path if file is exist
	 * 
	 * @param filePath
	 * @param data
	 */
	public static void saveFile(String filePath, byte[] data) {
		if (data == null || data.length == 0) {
			printLog("saveFile data is null");
			return;
		}
		try {
			File file = new File(filePath);
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(data);
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static class FileComparator implements Comparator<File> {
		public int compare(File file1, File file2) {
			if (file1.lastModified() < file2.lastModified()) {
				return -1;
			} else {
				return 1;
			}
		}
	}
	
	/**
	 * print log
	 * 
	 * @param logStr
	 */
	public static void printLog(String logStr) {
		STLog.printLog(logStr);
	}
}
