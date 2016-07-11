package com.ziv.trendsviewpager.utils;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

	public static boolean isEmptyString(CharSequence str) {
		if (TextUtils.isEmpty(str) || str == null) {
			printLog("isEmtyString string is empty or null");
			return true;
		}
		return false;
	}

	/**
	 * print log
	 * 
	 * @param logStr
	 */
	public static void printLog(String logStr) {
		STLog.printLog(logStr);
	}

	public static String cutStringIfNeed(String soure, int cutlenth) {
		if (StringUtil.isEmptyString(soure) || cutlenth <= 0) {
			return "";
		}

		// replace blank
		if (soure != null) {
			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
			Matcher m = p.matcher(soure);
			soure = m.replaceAll("");
		}

		if (soure.length() <= cutlenth) {
			return soure;
		}
		return soure.substring(0, cutlenth);
	}
}
