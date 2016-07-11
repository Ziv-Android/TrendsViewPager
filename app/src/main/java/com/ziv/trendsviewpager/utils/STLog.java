package com.ziv.trendsviewpager.utils;

import android.util.Log;

public class STLog {

	public static boolean DEBUG = true;
	public static String TAG = "STUtils";
	
	static void e(Object object){
		e(TAG,object);
	}
	
	public static void e(String tag,Object object) {
		if (DEBUG) {
			if (null != object) {
				Log.e(tag, object.toString());
			} else {
				Log.e(tag, "null");
			}
		}
	}
	
	static void d(Object object){
		d(TAG,object);
	}
	
	public static void d(String tag,Object object) {
		if (DEBUG) {
			if (null != object) {
				Log.d(tag, object.toString());
			} else {
				Log.d(tag, "null");
			}
		}
	}
	
	static void w(Object object){
		w(TAG,object);
	}
	
	public static void w(String tag,Object object) {
		if (DEBUG) {
			if (null != object) {
				Log.w(tag, object.toString());
			} else {
				Log.w(tag, "null");
			}
		}
	}
	
	static void i(Object object){
		i(TAG,object);
	}
	
	
	static void i(String tag,Object object) {
		if (DEBUG) {
			if (null != object) {
				Log.i(TAG, object.toString());
			} else {
				Log.i(TAG, "null");
			}
		}
	}
	
	static void v(Object object){
		v(TAG,object);
	}
	
	public static void v(String tag,Object object) {
		if (DEBUG) {
			if (null != object) {
				Log.v(TAG, object.toString());
			} else {
				Log.v(TAG, "null");
			}
		}
	}
	
	public static void printLog(String logStr){
		if (STLog.DEBUG) {
			Log.d(TAG, logStr);
		}
	}
	
}
