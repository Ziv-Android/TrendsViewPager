package com.ziv.trendsviewpager.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

public class AppInfoUtil {
	private static final String TAG = "AppUtil";

	public static String getAppName(Context context) {
		if (context == null) {
			printLog("getAppName:context is null");
			return null;
		}
		PackageInfo packageInfo = getPackageInfo(context);
		if (packageInfo == null) {
			printLog("getAppName:NameNotFoundException");
			return null;
		}
		int labelRes = packageInfo.applicationInfo.labelRes;
		String appName = context.getResources().getString(labelRes);
		return appName;
	}

	/**
	 * get version name
	 * 
	 * @param context
	 * @return
	 */
	public static String getVersionName(Context context) {
		if (context == null) {
			printLog("getVersionName:context is null");
			return null;
		}
		PackageInfo packageInfo = getPackageInfo(context);
		if (packageInfo == null) {
			printLog("getPackageName:NameNotFoundException");
			return null;
		}
		return packageInfo.versionName;
	}

	/**
	 * get Version Code
	 * 
	 * @param context
	 * @return
	 */
	public static int getVersionCode(Context context) {
		if (context == null) {
			printLog("getVersionCode:context is null");
			return 0;
		}
		PackageInfo packageInfo = getPackageInfo(context);
		if (packageInfo == null) {
			printLog("getVersionCode:NameNotFoundException");
			return 0;
		}
		return packageInfo.versionCode;
	}

	/**
	 * judge the version of app is latestest version;
	 * 
	 * @param context
	 * @param versionName
	 * @return
	 */
	public static boolean isLatestVersion(Context context, String versionName) {
		if (versionName.equals(getVersionName(context))) {
			return true;
		}

		return false;
	}

	/**
	 * get Package Info
	 * 
	 * @param context
	 * @return
	 */
	public static PackageInfo getPackageInfo(Context context) {
		PackageManager packageManager = context.getPackageManager();
		PackageInfo packageInfo = null;
		try {
			packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return packageInfo;
	}

	/**
	 * print log
	 * 
	 * @param logStr
	 */
	public static void printLog(String logStr) {
		if (STLog.DEBUG) {
			Log.d(TAG, logStr);
		}
	}
}
