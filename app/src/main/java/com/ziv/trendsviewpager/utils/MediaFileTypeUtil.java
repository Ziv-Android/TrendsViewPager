package com.ziv.trendsviewpager.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MediaFileTypeUtil {

	Class<?> mMediaFile;
	Class<?> mMediaFileType;
	Method getFileTypeMethod;
	Method isAudioFileTypeMethod;
	Method isVideoFileTypeMethod;
	Method isImageFileTypeMethod;
	String methodName = "getBoolean";
	String getFileType = "getFileType";

	String isAudioFileType = "isAudioFileType";
	String isVideoFileType = "isVideoFileType";
	String isImageFileType = "isImageFileType";

	private static Field fileType;
	private static MediaFileTypeUtil sJudgeMultiMediaType;

	private MediaFileTypeUtil() {
		initReflect();
	}

	public static MediaFileTypeUtil getInstance() {
		if (sJudgeMultiMediaType == null) {
			sJudgeMultiMediaType = new MediaFileTypeUtil();
		}
		return sJudgeMultiMediaType;
	}

	private void initReflect() {
		try {
			mMediaFile = Class.forName("android.media.MediaFile");
			mMediaFileType = Class.forName("android.media.MediaFile$MediaFileType");

			fileType = mMediaFileType.getField("fileType");

			getFileTypeMethod = mMediaFile.getMethod(getFileType, String.class);

			isAudioFileTypeMethod = mMediaFile.getMethod(isAudioFileType, int.class);
			isVideoFileTypeMethod = mMediaFile.getMethod(isVideoFileType, int.class);
			isImageFileTypeMethod = mMediaFile.getMethod(isImageFileType, int.class);

		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}

	}

	private int getMediaFileType(String path) {

		int type = 0;

		try {
			Object obj = getFileTypeMethod.invoke(mMediaFile, path);
			if (obj == null) {
				type = -1;
			} else {
				type = fileType.getInt(obj);
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		return type;
	}

	public boolean isAudioFile(String path) {
		boolean isAudioFile = false;
		try {
			isAudioFile = (Boolean) isAudioFileTypeMethod.invoke(mMediaFile, getMediaFileType(path));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return isAudioFile;
	}

	public boolean isVideoFile(String path) {
		boolean isVideoFile = false;
		try {
			isVideoFile = (Boolean) isVideoFileTypeMethod.invoke(mMediaFile, getMediaFileType(path));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return isVideoFile;
	}

	public boolean isImageFile(String path) {
		boolean isImageFile = false;
		try {
			isImageFile = (Boolean) isImageFileTypeMethod.invoke(mMediaFile, getMediaFileType(path));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return isImageFile;
	}

}