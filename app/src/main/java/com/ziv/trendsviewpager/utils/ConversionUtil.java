package com.ziv.trendsviewpager.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore.Images;
import android.util.TypedValue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ConversionUtil {
	private static final String TAG = "ConversionUtil";

	// px, dp, dip, sp之间的区别
	// px:pixel，即像素，1px代表屏幕上的一个物理的像素点。但px单位不被建议使用。因为同样像素大小的图片在不同手机显示的实际大小可能不同。要用到px的情况是需要画1像素表格线或阴影线的时候，如果用其他单位画则会显得模糊。
	// dp: device independent
	// pixel。最常用也是最难理解的尺寸单位。与像素密度密切相关。Android系统定义了四种像素密度：低（120dpi）、中（160dpi）、高（240dpi）和超高（320dpi），它们对应的dp到px的系数分别为0.75、1、1.5和2，这个系数乘以dp长度就是像素数。例如界面上有一个长度为“80dp”的图片，那么它在240dpi的手机上实际显示为80x1.5=120px，在320dpi的手机上实际显示为80x2=160px。如果你拿这两部手机放在一起对比，会发现这个图片的物理尺寸“差不多”，这就是使用dp作为单位的效果。
	// sp: Scale-independent
	// Pixel，即与缩放无关的抽象像素。sp和dp很类似但唯一的区别是，Android系统允许用户自定义文字尺寸大小（小、正常、大、超大等等），当文字尺寸是“正常”时，1sp=1dp=0.00625英寸，而当文字尺寸是“大”或“超大”时，1sp>1dp=0.00625英寸。类似我们在windows里调整字体尺寸以后的效果——窗口大小不变，只有文字大小改变。
	/**
	 * dp转px
	 * 
	 */
	public static int dp2px(Context context, float dpVal) {
		int result = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, context.getResources().getDisplayMetrics());
		return result;
	}

	/**
	 * sp转px
	 */
	public static int sp2px(Context context, float spVal) {
		int result = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spVal, context.getResources().getDisplayMetrics());
		return result;
	}

	/**
	 * px转dp
	 * 
	 */
	public static int px2dp(Context context, float pxVal) {
		final float scale = context.getResources().getDisplayMetrics().density;
		int result = (int) (pxVal / scale);
		return result;
	}

	/**
	 * px转sp
	 */
	public static float px2sp(Context context, float pxVal) {
		int result = (int) (pxVal / context.getResources().getDisplayMetrics().scaledDensity);
		return result;
	}

	/**
	 * InputStream to byte[]
	 * 
	 * @param in
	 * @return
	 */
	public static byte[] inputStreamToBytes(InputStream in) {
		if (in == null) {
			printLog("inputStreamToBytes---> InputStream is null");
			return null;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] result = null;
		byte[] buffer = new byte[1024];
		int num;
		try {
			while ((num = in.read(buffer)) != -1) {
				out.write(buffer, 0, num);
			}
			out.flush();
			result = out.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * InputStream to byte[]
	 * 
	 * @param is
	 * @return
	 */
	public static byte[] inputStream2Bytes(InputStream is) {
		if (is == null) {
			printLog("inputStream2Bytes---> InputStream is null");
			return null;
		}
		String str = "";
		byte[] readByte = new byte[1024];
		try {
			while ((is.read(readByte, 0, 1024)) != -1) {
				str += new String(readByte).trim();
			}
			return str.getBytes();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * inputStream to Bitmap
	 * 
	 * @param is
	 * @return
	 */
	public static Bitmap inputStream2Bitmap(InputStream is) {
		if (is == null) {
			printLog("inputStream2Bitmap---> InputStream is null");
			return null;
		}
		return BitmapFactory.decodeStream(is);
	}

	/**
	 * InputStream 2 drawable
	 * 
	 * @param is
	 * @return
	 */
	public static Drawable inputStream2Drawable(InputStream is) {
		if (is == null) {
			printLog("inputStream2Drawable---> InputStream is null");
			return null;
		}
		Bitmap bitmap = inputStream2Bitmap(is);
		return bitmap2Drawable(bitmap);
	}

	/**
	 * byte[] to drawable
	 * 
	 * @param b
	 * @return
	 */
	public static Drawable bytes2Drawable(byte[] b) {
		if (b == null || b.length == 0) {
			printLog("bytes2Drawable---> b==null || b.length==0");
			return null;
		}
		Bitmap bitmap = bytes2Bitmap(b);
		return bitmap2Drawable(bitmap);
	}

	/**
	 * byte[] to InputStream
	 * 
	 * @param b
	 * @return
	 */
	public static InputStream byte2InputStream(byte[] b) {
		ByteArrayInputStream bais = new ByteArrayInputStream(b);
		return bais;
	}

	/**
	 * byte[] to Bitmap
	 * 
	 * @param b
	 * @return
	 */
	public static Bitmap bytes2Bitmap(byte[] b) {
		if (b.length != 0) {
			return BitmapFactory.decodeByteArray(b, 0, b.length);
		}
		return null;
	}

	/**
	 * bitmap to InputStream
	 * 
	 * @param bitmap
	 * @return
	 */
	public static InputStream bitmap2InputStream(Bitmap bitmap) {
		if (BitmapUtil.isEmty(bitmap)) {
			printLog("bitmap2InputStream---> bitmap is null");
			return null;
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		InputStream is = new ByteArrayInputStream(baos.toByteArray());
		return is;
	}

	/**
	 * Bitmap to byte[]
	 * 
	 * @param bm
	 * @return
	 */
	public static byte[] bitmap2Bytes(Bitmap bitmap) {
		if (BitmapUtil.isEmty(bitmap)) {
			printLog("bitmap2Bytes---> bitmap is null");
			return null;
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}

	/**
	 * bitmap to drawable
	 * 
	 * @param bitmap
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static Drawable bitmap2Drawable(Bitmap bitmap) {
		if (BitmapUtil.isEmty(bitmap)) {
			printLog("bitmap2Drawable---> bitmap is null");
			return null;
		}
		BitmapDrawable bd = new BitmapDrawable(bitmap);
		Drawable d = (Drawable) bd;
		return d;
	}

	/**
	 * drawable 2 InputStream
	 * 
	 * @param drawable
	 * @return
	 */
	public static InputStream drawable2InputStream(Drawable drawable) {
		if (drawable == null) {
			printLog("drawable2InputStream---> drawable is null");
			return null;
		}
		Bitmap bitmap = drawable2Bitmap(drawable);
		return bitmap2InputStream(bitmap);
	}

	/**
	 * drawable to byte[]
	 * 
	 * @param drawable
	 * @return
	 */
	public static byte[] drawable2Bytes(Drawable drawable) {
		if (drawable == null) {
			printLog("drawable2Bytes---> drawable is null");
			return null;
		}
		Bitmap bitmap = drawable2Bitmap(drawable);
		return bitmap2Bytes(bitmap);
	}

	/**
	 * drawable to bitmap
	 * 
	 * @param drawable
	 * @return
	 */
	public static Bitmap drawable2Bitmap(Drawable drawable) {
		if (drawable == null) {
			printLog("drawable2Bitmap---> drawable is null");
			return null;
		}
		Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
				drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
		drawable.draw(canvas);
		return bitmap;
	}

	/**
	 * Uri transform path(/mnt/storage/emulated/0" transform to "/sdcard")
	 * 
	 * @param context
	 * @param fileUrl
	 * @return
	 */
	@SuppressLint("SdCardPath")
	public static String uri2Path(Context context, Uri fileUrl) {
		String fileName = null;
		Uri filePathUri = fileUrl;
		if (fileUrl != null) {
			if (fileUrl.getScheme().toString().compareTo("content") == 0) {
				Cursor cursor = context.getContentResolver().query(Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
				if (cursor != null && cursor.moveToFirst()) {
					int column_index = cursor.getColumnIndexOrThrow(Images.Media.DATA);
					fileName = cursor.getString(column_index);
					if (fileName.startsWith("/mnt/storage/emulated/0")) {
						fileName = fileName.replace("/mnt/storage/emulated/0", "/sdcard");
					}
					cursor.close();
				}
			} else if (fileUrl.getScheme().compareTo("file") == 0) {
				fileName = filePathUri.toString();
				fileName = filePathUri.toString().replace("file://", "");
			}
		}
		return fileName;
	}

	/**
	 * path transform Uri("content://media/external/images/media/")
	 * 
	 * @param activity
	 * @param path
	 * @return
	 */
	public static Uri path2Uri(Activity activity, String path) {
		Uri uri = Uri.parse("content://media/external/images/media/");
		path = uri.getEncodedPath();
		if (path != null) {
			path = Uri.decode(path);
			ContentResolver cr = activity.getContentResolver();
			StringBuffer buff = new StringBuffer();
			buff.append("(").append(Images.ImageColumns.DATA).append("=").append("'" + path + "'").append(")");
			Cursor cur = cr.query(Images.Media.EXTERNAL_CONTENT_URI, new String[] { Images.ImageColumns._ID }, buff.toString(), null, null);
			int index = 0;
			for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
				index = cur.getColumnIndex(Images.ImageColumns._ID);
				index = cur.getInt(index);
			}
			cur.close();
			if (index == 0) {
			} else {
				Uri uri_temp = Uri.parse("content://media/external/images/media/" + index);
				printLog("path2Uri " + uri_temp.toString());
				if (uri_temp != null) {
					uri = uri_temp;
				}
			}
		}
		return uri;
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
