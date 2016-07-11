package com.ziv.trendsviewpager.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class SharedPreferenceUtil {
	public static String TAG = "SharedPreferenceUtil";
	/**
	 * Save filename in the inside of the mobile phone
	 */
	public static final String FILE_NAME = "share_data";

	/**
	 * Ways of preserving data, we need to save specific types of data, then
	 * according to different types of calls the save method
	 * 
	 * @param context
	 * @param key
	 *            The key of key/value pair
	 * @param object
	 *            (String,Integer,Boolean,Float,Long) The key of key/value pair
	 */
	public static void put(Context context, String key, Object object) {
		put(context, FILE_NAME, key, object);
	}

	public static void put(Context context, String fileName, String key, Object object) {
		if (null == object) {
			if (STLog.DEBUG) {
				Log.d(TAG, "object cannot be null");
			}
			return;
		}
		SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		if (object instanceof String) {
			editor.putString(key, (String) object);
		} else if (object instanceof Integer) {
			editor.putInt(key, (Integer) object);
		} else if (object instanceof Boolean) {
			editor.putBoolean(key, (Boolean) object);
		} else if (object instanceof Float) {
			editor.putFloat(key, (Float) object);
		} else if (object instanceof Long) {
			editor.putLong(key, (Long) object);
		} else {
			editor.putString(key, object.toString());
		}
		SharedPreferencesCompat.apply(editor);
	}

	/**
	 * Preserved by the method of data, we according to the default deserves to
	 * specific types of stored data, and then call the method that relative to
	 * get the value
	 * 
	 * @param context
	 * @param key
	 * @param defaultObject
	 *            What type of data cannot be NULL, what type of variable should
	 *            be
	 * @return
	 */
	public static Object get(Context context, String key, Object defaultObject) {
		return get(context, FILE_NAME, key, defaultObject);
	}

	public static Object get(Context context, String fileName, String key, Object defaultObject) {
		if (null == defaultObject) {
			if (STLog.DEBUG) {
				Log.d(TAG, "object cannot be null");
			}
			return null;
		}
		SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		if (defaultObject instanceof String) {
			String stringResult = sp.getString(key, (String) defaultObject);
			return stringResult;
		} else if (defaultObject instanceof Integer) {
			Integer integerResult = sp.getInt(key, (Integer) defaultObject);
			return integerResult;
		} else if (defaultObject instanceof Boolean) {
			Boolean booleanResult = sp.getBoolean(key, (Boolean) defaultObject);
			return booleanResult;
		} else if (defaultObject instanceof Float) {
			Float floatResult = sp.getFloat(key, (Float) defaultObject);
			return floatResult;
		} else if (defaultObject instanceof Long) {
			Long longResult = sp.getLong(key, (Long) defaultObject);
			return longResult;
		}

		return null;
	}

	/**
	 * Returns all key/value pair
	 * 
	 * @param context
	 * @return
	 */
	public static Map<String, ?> getAll(Context context) {
		return getAll(context, FILE_NAME);
	}

	public static Map<String, ?> getAll(Context context, String fileName) {
		SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		return sp.getAll();
	}

	/**
	 * Remove a key values have corresponding values
	 * 
	 * @param context
	 * @param key
	 */
	public static void remove(Context context, String key) {
		remove(context, FILE_NAME, key);
	}

	public static void remove(Context context, String fileName, String key) {
		SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.remove(key);
		SharedPreferencesCompat.apply(editor);
	}

	/**
	 * Clear all data
	 * 
	 * @param context
	 */
	public static void clear(Context context) {
		clear(context, FILE_NAME);
	}

	public static void clear(Context context, String fileName) {
		SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.clear();
		SharedPreferencesCompat.apply(editor);
	}

	/**
	 * Queries whether a key already exists
	 * 
	 * @param context
	 * @param key
	 * @return
	 */
	public static boolean contains(Context context, String key) {
		return contains(context, FILE_NAME, key);
	}

	public static boolean contains(Context context, String fileName, String key) {
		SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		boolean result = sp.contains(key);
		return result;
	}

	/**
	 * Aimed at the complex type storage
	 * 
	 * @param context
	 * @param key
	 * @param object
	 */
	public void setComplexObject(Context context, String key, Object object) {
		setComplexObject(context, FILE_NAME, key, object);
	}

	public void setComplexObject(Context context, String fileName, String key, Object object) {
		SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream out = null;
		try {
			out = new ObjectOutputStream(baos);
			out.writeObject(object);
			Log.d(TAG, "Base64:");
			String objectVal = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
			Editor editor = sp.edit();
			editor.putString(key, objectVal);
			Log.d(TAG, "Base64:" + objectVal);
			editor.commit();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (baos != null) {
					baos.close();
				}
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public <T> T getComplextObject(Context context, String key) {
		return getComplexObject(context, FILE_NAME, key);
	}

	@SuppressWarnings("unchecked")
	public <T> T getComplexObject(Context context, String fileName, String key) {
		SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		if (sp.contains(key)) {
			String objectVal = sp.getString(key, null);
			byte[] buffer = Base64.decode(objectVal, Base64.DEFAULT);
			ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
			ObjectInputStream ois = null;
			try {
				ois = new ObjectInputStream(bais);
				T t = (T) ois.readObject();
				return t;
			} catch (StreamCorruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} finally {
				try {
					if (bais != null) {
						bais.close();
					}
					if (ois != null) {
						ois.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	/**
	 * Create a solution to SharedPreferencesCompat. Apply method of a
	 * compatible class
	 */
	private static class SharedPreferencesCompat {
		private static final Method sApplyMethod = findApplyMethod();

		/**
		 * Reflection to find the apply method
		 * 
		 * @return
		 */
		@SuppressWarnings({ "unchecked", "rawtypes" })
		private static Method findApplyMethod() {
			try {
				Class clz = Editor.class;
				return clz.getMethod("apply");
			} catch (NoSuchMethodException e) {
			}

			return null;
		}

		/**
		 * If found, use the apply executed, or use the commit
		 * 
		 * @param editor
		 */
		public static void apply(Editor editor) {
			try {
				if (sApplyMethod != null) {
					sApplyMethod.invoke(editor);
					return;
				}
			} catch (IllegalArgumentException e) {
			} catch (IllegalAccessException e) {
			} catch (InvocationTargetException e) {
			}
			editor.commit();
		}
	}
}