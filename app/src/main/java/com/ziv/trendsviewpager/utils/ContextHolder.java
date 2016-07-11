package com.ziv.trendsviewpager.utils;

import android.content.Context;

public class ContextHolder {
	private Context mContext;
	private static ContextHolder sHolder = null;
	
	public static void init(Context context) {
		sHolder = new ContextHolder(context);
	}
	
	private ContextHolder(Context context) {
		mContext = context;
	}

	public static void setContext(Context context) {
		sHolder.mContext = context;
	}
	
	public static Context getContext() {
		if (sHolder==null) {
			throw new RuntimeException("before using ContextHolder,you should init ContextHolder to get global variable");
		}
		return sHolder.mContext;
	}
	
}
