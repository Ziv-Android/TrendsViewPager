package com.ziv.trendsviewpager.utils;

import android.os.Environment;

import java.io.File;

public class StorageUtil {
	/**
	 * Check the SD card
	 * 
	 * @return
	 */
	public static boolean checkSDCardAvailable() {
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}
	
	/**
	 * get external storage path
	 * 
	 * @return Environment.getExternalStorageDirectory();
	 */
	public static File getExternalStorage() {
		return Environment.getExternalStorageDirectory();
	}
	
	/**
	 * get external storage absolute path
	 * 
	 * @return
	 */
	public static String getExternalStoragePath() {
		printLog("getExternalStoragePictureDirectory = "+ Environment.getExternalStorageDirectory().getAbsolutePath());
		return Environment.getExternalStorageDirectory().getAbsolutePath();
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
