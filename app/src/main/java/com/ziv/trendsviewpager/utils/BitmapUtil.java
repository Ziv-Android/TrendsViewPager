package com.ziv.trendsviewpager.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class BitmapUtil {

	private static Matrix mMatrix = new Matrix();
	private static final String TAG = "BitmapUtil";

	private static Matrix getMatrix() {
		mMatrix.reset();
		return mMatrix;
	}

	/**
	 * scale bitmap which parameter x or y is 1080
	 * 
	 * @param data
	 * @return
	 */
	public static Bitmap createScaleBitmapFromData(byte[] data, int width, int height) {
		if (data == null || data.length == 0) {
			printLog("createScaleBitmapFromData data==null||data.length==0");
			return null;
		}
		Options opts = new Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(data, 0, data.length, opts);
		int scaleX = opts.outWidth / width;
		int scaleY = opts.outHeight / height;
		int scale = scaleX > scaleY ? scaleX : scaleY;
		if (scale < 1)
			scale = 1;
		opts.inJustDecodeBounds = false;
		opts.inSampleSize = scale;
		return BitmapFactory.decodeByteArray(data, 0, data.length, opts);
	}

	/**
	 * create bitmap by resources but the density will same as resources
	 * 
	 * @param resources
	 * @param id
	 * @return
	 */
	public static Bitmap creatBitmapFromResource(Resources resources, int id) {
		TypedValue value = new TypedValue();
		resources.openRawResource(id, value);
		Options opts = new Options();
		opts.inTargetDensity = value.density;
		printLog("  creatBitmapFromResource value.density=" + value.density);

		return BitmapFactory.decodeResource(resources, id, opts);
	}

	/**
	 * create bitmap by path
	 * 
	 * @param path
	 * @return Bitmap
	 */
	public static Bitmap createBitmapFromPath(String path) {
		File imgFile = new File(path);
		if (imgFile.exists()) {
			Bitmap bmp = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
			return bmp;
		} else {
			printLog("  createBitmapFromPath imgFile is not exist");
		}
		return null;
	}

	/**
	 * create source bitmap by Resource which size is specified(thumb nail
	 * bitmap)
	 * 
	 * @param id
	 *            Resources id
	 * @param width
	 *            bitmap width
	 * @param height
	 *            bitmap height
	 * @return Bitmap
	 */
	public static Bitmap createImageThumbnail(Resources resources, int id, int width, int height) {
		Bitmap bitmap = null;
		Options options = new Options();
		options.inJustDecodeBounds = true;
		bitmap = BitmapFactory.decodeResource(resources, id);
		options.inJustDecodeBounds = false;
		int h = options.outHeight;
		int w = options.outWidth;
		int beWidth = w / width;
		int beHeight = h / height;
		int be = 1;
		if (beWidth < beHeight) {
			be = beWidth;
		} else {
			be = beHeight;
		}
		if (be <= 0) {
			be = 1;
		}
		options.inSampleSize = be;
		BitmapFactory.decodeResource(resources, id, options);
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		return bitmap;
	}

	/**
	 * load source bitmap by image path which size is specified
	 * 
	 * @param path
	 *            image path
	 * @param width
	 *            bitmap width
	 * @param height
	 *            bitmap height
	 * @return Bitmap
	 */
	public static Bitmap createImageThumbnail(String imagePath, int width, int height) {
		if (TextUtils.isEmpty(imagePath)) {
			printLog("createImageThumbnail imagePath cannot be empty");
			return null;
		}
		Bitmap bitmap = null;
		Options options = new Options();
		options.inJustDecodeBounds = true;
		bitmap = BitmapFactory.decodeFile(imagePath, options);
		options.inJustDecodeBounds = false;
		int h = options.outHeight;
		int w = options.outWidth;
		int beWidth = w / width;
		int beHeight = h / height;
		int be = 1;
		if (beWidth < beHeight) {
			be = beWidth;
		} else {
			be = beHeight;
		}
		if (be <= 0) {
			be = 1;
		}
		options.inSampleSize = be;
		bitmap = BitmapFactory.decodeFile(imagePath, options);
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		return bitmap;
	}

	/**
	 * Create reflection images
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Bitmap createReflectionImage(Bitmap bitmap) {
		if (isEmty(bitmap)) {
			printLog("createReflectionImageWithOrigin bitmap is null");
			return null;
		}
		final int reflectionGap = 4;
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);
		Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, h / 2, w, h / 2, matrix, false);
		Bitmap bitmapWithReflection = Bitmap.createBitmap(w, (h + h / 2), Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmapWithReflection);
		canvas.drawBitmap(bitmap, 0, 0, null);
		Paint deafalutPaint = new Paint();
		canvas.drawRect(0, h, w, h + reflectionGap, deafalutPaint);
		canvas.drawBitmap(reflectionImage, 0, h + reflectionGap, null);
		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0, bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff,
				0x00ffffff, TileMode.CLAMP);
		paint.setShader(shader);
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		canvas.drawRect(0, h, w, bitmapWithReflection.getHeight() + reflectionGap, paint);
		return bitmapWithReflection;
	}

	/**
	 * 压缩图片
	 */
	public static Bitmap compressImage(String srcPath) {
		Options options = new Options();
		options.inJustDecodeBounds = true;// 只读边,不读内容
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, options);
		int sampleSize = 1;
		int w = options.outWidth;
		int h = options.outHeight;
		if (w < 0 || h < 0) { // 不是图片文件直接返回
			return null;
		}
		int requestH = 224; // 需求最大高度
		int requestW = 224; // 需求最大宽度
		while ((h / sampleSize > requestH) || (w / sampleSize > requestW)) {
			sampleSize = sampleSize << 1;
		}
		options.inJustDecodeBounds = false; // 不再只读边
		options.inSampleSize = sampleSize;// 设置采样率大小
		bitmap = BitmapFactory.decodeFile(srcPath, options);
		return bitmap;
	}
	
	/**
	 * Correct image direction ,it mainly use for Front-facing camera;
	 * 
	 * @param srcPath
	 * @param outFile
	 */
	public static void compressImage(String srcPath, String outFile) {
		if (TextUtils.isEmpty(srcPath)) {
			printLog("compressImage srcPath cannot be null");
			return;
		}
		int degree = getPictureDegree(srcPath);
		final int DEF_MIN = 480;
		final int DEF_MAX = 1280;
		Options newOpts = new Options();
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap;
		BitmapFactory.decodeFile(srcPath, newOpts);
		int picMin = Math.min(newOpts.outWidth, newOpts.outHeight);
		int picMax = Math.max(newOpts.outWidth, newOpts.outHeight);
		float picShap = 1.0f * picMin / picMax;
		float defShap = 1.0f * DEF_MIN / DEF_MAX;
		float scale = 1f;
		if (picMax < DEF_MAX) {

		} else if (picShap > defShap) {
			scale = 1.0f * DEF_MAX / picMax;

		} else if (picMin > DEF_MIN) {
			scale = 1.0f * DEF_MIN / picMin;
		}
		int scaledWidth = (int) (scale * newOpts.outWidth);
		int scaledHeight = (int) (scale * newOpts.outHeight);
		newOpts.inSampleSize = (int) ((scale == 1 ? 1 : 2) / scale);
		newOpts.inJustDecodeBounds = false;
		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		if (scale != 1) {
			Matrix matrix = new Matrix();
			scale = (float) scaledWidth / bitmap.getWidth();
			matrix.setScale(scale, scale);
			Bitmap bitmapNew = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
			bitmap.recycle();
			bitmap = bitmapNew;
		}
		int quality;
		if (scaledHeight * scaledWidth > (1280 * 720)) {
			quality = 30;
		} else {
			quality = 80 - (int) ((float) scaledHeight * scaledWidth / (1280 * 720) * 50);
		}
		try {
			bitmap = getRotateBitmap(bitmap, degree);
			bitmap.compress(CompressFormat.JPEG, quality, new FileOutputStream(new File(outFile)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		bitmap.recycle();
		System.gc();
	}

	/**
	 * 计算 BitmapFactpry 的 inSimpleSize的值的方法
	 * 
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static int calculateInSampleSize(Options options, int reqWidth, int reqHeight) {
		if (reqWidth == 0 || reqHeight == 0) {
			return 1;
		}
		// 获取图片原生的宽和高
		final int height = options.outHeight;
		final int width = options.outWidth;
		Log.d(TAG, "origin, w= " + width + " h=" + height);
		int inSampleSize = 1;

		// 如果原生的宽高大于请求的宽高,那么将原生的宽和高都置为原来的一半
		if (height > reqHeight || width > reqWidth) {
			final int halfHeight = height / 2;
			final int halfWidth = width / 2;
			// 主要计算逻辑
			// Calculate the largest inSampleSize value that is a power of 2 and
			// keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
				inSampleSize *= 2;
			}
		}
		printLog("sampleSize:" + inSampleSize);
		return inSampleSize;
	}

	/**
	 * 从路径加载
	 * 
	 * @param path
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static Bitmap decodeSampledBitmapFromPath(String path, int reqWidth, int reqHeight) {
		// 设置inJustDecodeBounds = true ,表示先不加载图片
		final Options options = new Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		// 调用方法计算合适的 inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
		// inJustDecodeBounds 置为 false 真正开始加载图片
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(path, options);
	}

	/**
	 * 从资源加载
	 * 
	 * @param res
	 * @param resId
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
		// 设置inJustDecodeBounds = true ,表示先不加载图片
		final Options options = new Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);
		// 调用方法计算合适的 inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
		// inJustDecodeBounds 置为 false 真正开始加载图片
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(res, resId, options);
	}

	/**
	 * get zoom drawable
	 * 
	 * @param drawable
	 * @param w
	 * @param h
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static Drawable getZoomDrawable(Drawable drawable, int w, int h) {
		if (drawable == null) {
			printLog("getZoomDrawable drawable is null");
			return null;
		}
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		Bitmap oldbmp = ConversionUtil.drawable2Bitmap(drawable);
		Matrix matrix = new Matrix();
		float sx = ((float) w / width);
		float sy = ((float) h / height);
		matrix.postScale(sx, sy);
		Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height, matrix, true);
		return new BitmapDrawable(newbmp);
	}

	/**
	 * The Drawable resource files into Bitmap images
	 *
	 * @param drawable
	 *            {@link Drawable} to be converted
	 * @return {@link Bitmap} object
	 */
	public static Bitmap getBitmapWithDrawable(Drawable drawable) {
		if (drawable == null) {
			printLog("getBitmapWithDrawable drawable is null");
			return null;
		}
		if (drawable instanceof BitmapDrawable)
			return ((BitmapDrawable) drawable).getBitmap();

		Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
		drawable.draw(canvas);
		return bitmap;
	}

	/**
	 * get the bitmap after convert,main use for Front-facing camera
	 * 
	 * @param bitmap
	 * @return
	 */
	public Bitmap getBitmapAfterReverse(Bitmap bitmap) {
		if (isEmty(bitmap)) {
			printLog("getBitmapAfterConvert bmp is null");
		}
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Matrix matrix = new Matrix();
		matrix.postScale(-1, 1);
		Bitmap convertBmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
		return convertBmp;
	}

	/**
	 * crop capture the middle part of the bitmap
	 * 
	 * @param sourceBitmap
	 *            sourceBitmap
	 * @param edgeLength
	 *            Hope to get part of the side length of square,To a minimum
	 *            length is edgeLength bitmap
	 * @return Bitmap
	 */
	public static Bitmap getCenterSquareScaleBitmap(Bitmap sourceBitmap, int edgeLength) {
		if (null == sourceBitmap || edgeLength <= 0) {
			printLog("getCenterSquareScaleBitmap sourceBitmap is null or edgeLength<=0");
			return null;
		}
		Bitmap result = sourceBitmap;
		int widthOrg = sourceBitmap.getWidth();
		int heightOrg = sourceBitmap.getHeight();
		if (widthOrg > edgeLength && heightOrg > edgeLength) {
			int longerEdge = (int) (edgeLength * Math.max(widthOrg, heightOrg) / Math.min(widthOrg, heightOrg));
			int scaledWidth = widthOrg > heightOrg ? longerEdge : edgeLength;
			int scaledHeight = widthOrg > heightOrg ? edgeLength : longerEdge;
			Bitmap scaledBitmap;
			try {
				scaledBitmap = Bitmap.createScaledBitmap(sourceBitmap, scaledWidth, scaledHeight, true);
				int xTopLeft = (scaledWidth - edgeLength) / 2;
				int yTopLeft = (scaledHeight - edgeLength) / 2;
				result = Bitmap.createBitmap(scaledBitmap, xTopLeft, yTopLeft, edgeLength, edgeLength);
				scaledBitmap.recycle();
			} catch (Exception e) {
				return null;
			}
		}
		return result;
	}

	/**
	 * Convert images into a round shape,bigger bitmap Effect will be more
	 * obvious
	 * 
	 * @param sourceBitmap
	 *            source bitmap
	 * @return Bitmap
	 */
	public static Bitmap getRoundBitmap(Bitmap sourceBitmap) {
		if (isEmty(sourceBitmap)) {
			printLog("getRoundBitmap sourceBitmap is null");
			return null;
		}
		int width = sourceBitmap.getWidth();
		int height = sourceBitmap.getHeight();
		float roundPx;
		float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
		if (width <= height) {
			roundPx = width / 2 - 5;
			top = 0;
			bottom = width;
			left = 0;
			right = width;
			height = width;
			dst_left = 0;
			dst_top = 0;
			dst_right = width;
			dst_bottom = width;
		} else {
			roundPx = height / 2 - 5;
			float clip = (width - height) / 2;
			left = clip;
			right = width - clip;
			top = 0;
			bottom = height;
			width = height;
			dst_left = 0;
			dst_top = 0;
			dst_right = height;
			dst_bottom = height;
		}
		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);
		final Rect dst = new Rect((int) dst_left, (int) dst_top, (int) dst_right, (int) dst_bottom);
		final RectF rectF = new RectF(dst_left + 3, dst_top + 3, dst_right - 3, dst_bottom - 3);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(sourceBitmap, src, dst, paint);
		return output;
	}

	/**
	 * get Rounded Corner Bitmap with color
	 * 
	 * @param sourceBitmap
	 * @param faceRect
	 * @param color
	 * @return
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap sourceBitmap, Rect faceRect, int color) {
		if (isEmty(sourceBitmap)) {
			printLog("getRoundedCornerBitmap sourceBitmap is null");
			return null;
		}
		Bitmap outBitmap = Bitmap.createBitmap(sourceBitmap.getWidth(), sourceBitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(outBitmap);
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, sourceBitmap.getWidth(), sourceBitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPX = (faceRect.right - faceRect.left) / 2.0f;
		final float roundPY = (faceRect.bottom - faceRect.top) / 2.0f;
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPX, roundPY, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(sourceBitmap, rect, rect, paint);
		return outBitmap;
	}

	/**
	 * According to a square cut images(bitmap)
	 * 
	 * @param sourceBitmap
	 *            source bitmap
	 * @return
	 */
	public static Bitmap getCropBitmap(Bitmap sourceBitmap) {
		if (isEmty(sourceBitmap)) {
			printLog("getCropBitmap sourceBitmap is null");
			return null;
		}
		int w = sourceBitmap.getWidth();
		int h = sourceBitmap.getHeight();
		int wh = w > h ? h : w;
		int retX = w > h ? (w - h) / 2 : 0;
		int retY = w > h ? 0 : (h - w) / 2;
		return Bitmap.createBitmap(sourceBitmap, retX, retY, wh, wh, null, false);
	}

	/**
	 * crop bitmap by specified rectangle
	 * 
	 * @param sourceBitmap
	 *            sourceBitmap
	 * @param orginRect
	 *            rect
	 * @return
	 */
	public static Bitmap getCropBitmap(Bitmap sourceBitmap, Rect orginRect) {
		if (isEmty(sourceBitmap)) {
			printLog("getCropBitmap sourceBitmap is null");
			return null;
		}
		return getCropBitmap(sourceBitmap, orginRect, 2, 2);
	}

	public static Bitmap getCropBitmap(Bitmap sourceBitmap, Rect orginRect, float scaleX, float scaleY) {
		if (sourceBitmap == null || sourceBitmap.isRecycled()) {
			return null;
		}
		Rect rect = getScaleRect(orginRect, scaleX, scaleY, sourceBitmap.getWidth(), sourceBitmap.getHeight());
		return Bitmap.createBitmap(sourceBitmap, rect.left, rect.top, rect.width(), rect.height());
	}

	/**
	 * 
	 * @param rect
	 * @param scaleX
	 * @param scaleY
	 * @param maxW
	 * @param maxH
	 * @return
	 */
	public static Rect getScaleRect(Rect rect, float scaleX, float scaleY, int maxW, int maxH) {
		Rect resultRect = new Rect();
		int left = (int) (rect.left - rect.width() * (scaleX - 1) / 2);
		int right = (int) (rect.right + rect.width() * (scaleX - 1) / 2);
		int bottom = (int) (rect.bottom + rect.height() * (scaleY - 1) / 2);
		int top = (int) (rect.top - rect.height() * (scaleY - 1) / 2);
		resultRect.left = left > 0 ? left : 0;
		resultRect.right = right > maxW ? maxW : right;
		resultRect.bottom = bottom > maxH ? maxH : bottom;
		resultRect.top = top > 0 ? top : 0;
		return resultRect;
	}

	/**
	 * change bitmap size by scale
	 * 
	 * @param sourceBitmap
	 *            source bitmap
	 * @param scale
	 *            scale size
	 * @return Bitmap
	 */
	public static Bitmap getScaleBitmap(Bitmap sourceBitmap, float scale) {
		if (isEmty(sourceBitmap)) {
			printLog("getScaleBitmap sourceBitmap is null");
			return null;
		}
		return getScaleBitmap(sourceBitmap, scale, true);
	}

	public static Bitmap getScaleBitmap(Bitmap bitmap, float scale, boolean recycle) {
		if (scale == 1f) {
			printLog("getScaleBitmap scale == 1f");
			return bitmap;
		}
		Matrix matrix = getMatrix();
		matrix.setScale(scale, scale);
		Bitmap bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		if (recycle && bitmap != null && !bitmap.equals(bmp) && !bitmap.isRecycled()) {
			bitmap.recycle();
			bitmap = null;
		}
		return bmp;
	}

	/**
	 * change bitmap size by fixed size
	 * 
	 * @param bitmap
	 *            source bitmap
	 * @param newWidth
	 *            set bitmap width
	 * @param newHeight
	 *            set bitmap height
	 * @return Bitmap
	 */
	public static Bitmap getScaleBitmap(Bitmap sourceBitmap, int newWidth, int newHeight) {
		if (isEmty(sourceBitmap)) {
			printLog("getCropBitmap sourceBitmap is null");
			return null;
		}
		int w = sourceBitmap.getWidth();
		int h = sourceBitmap.getHeight();
		float sw = (float) newWidth / w;
		float sh = (float) newHeight / h;
		float scale = sw < sh ? sw : sh;
		return getScaleBitmap(sourceBitmap, scale);
	}

	/**
	 * return bitmap of 2M or 5M
	 * 
	 * @param bitmap
	 * @param size
	 * @return
	 */
	public static Bitmap getResizeBitmap(Bitmap bitmap, int size) {
		if (isEmty(bitmap)) {
			printLog("getResizeBitmap bitmap is null");
			return null;
		}
		double maxSize = size * 1e6;
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		float ratio = (float) Math.sqrt(maxSize / (w * h));
		if (ratio >= 1) {
			return bitmap;
		}
		return BitmapUtil.getRotateAndScaleBitmap(bitmap, 0, ratio);
	}

	/**
	 * return Media image orientation
	 * 
	 * @param context
	 * @param photoUri
	 * @return
	 */
	public static int getOrientationFromMedia(Context context, Uri photoUri) {
		String[] imgs = { MediaStore.Images.Media.ORIENTATION };
		Cursor cursor = context.getContentResolver().query(photoUri, imgs, null, null, null);
		cursor.moveToFirst();
		int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.ORIENTATION);
		int roate = cursor.getInt(index);
		try {
			cursor.close();
		} catch (Exception e) {

		}
		return roate;
	}


	/**
	 * Bitmap
	 * 
	 * @param b
	 * @param rotateDegree
	 * @return
	 */
	public static Bitmap getRotateBitmap(Bitmap bitmap, float rotateDegree) {
		if (isEmty(bitmap)) {
			printLog("getRotateBitmap bitmap is null");
			return null;
		}
		Matrix matrix = new Matrix();
		matrix.postRotate((float) rotateDegree);
		Bitmap rotaBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
		return rotaBitmap;
	}

	/**
	 * Rotating Picture
	 * 
	 * @param angle
	 * @param bitmap
	 * @return Bitmap
	 */
	public static Bitmap getRotateAndScaleBitmap(Bitmap bitmap, int angle, float scale) {
		if (isEmty(bitmap)) {
			printLog("getRotateAndScaleBitmap bitmap is null");
			return null;
		}
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		matrix.postScale(scale, scale);
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return resizedBitmap;
	}

	/**
	 * Reads the image attributes: rotation Angle
	 * 
	 * @param path
	 *            The absolute path to the image
	 * @return degree
	 */
	public static int getPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
			Log.d(TAG, "orientation=" + orientation);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
				degree = 0;
				break;
			case ExifInterface.ORIENTATION_FLIP_VERTICAL:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_TRANSPOSE:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_TRANSVERSE:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return degree;
	}

	/**
	 * Return a horizontal Bitmap image
	 * 
	 * @param bitmap
	 *            source bitmap
	 * @return Bitmap
	 */
	public static Bitmap getMirrorHorizontalBitmap(Bitmap sourceBitmap) {
		if (isEmty(sourceBitmap)) {
			printLog("getMirrorHorizontalBitmap sourceBitmap is null");
			return null;
		}
		return getMirrorHorizontalBitmap(sourceBitmap, true);
	}

	public static Bitmap getMirrorHorizontalBitmap(Bitmap sourceBitmap, boolean recycle) {
		if (isEmty(sourceBitmap)) {
			printLog("getMirrorHorizontalBitmap sourceBitmap is null");
			return null;
		}
		Matrix matrix = getMatrix();
		matrix.setScale(-1, 1);
		matrix.postTranslate(sourceBitmap.getWidth(), 0);
		Bitmap bmp = Bitmap.createBitmap(sourceBitmap, 0, 0, sourceBitmap.getWidth(), sourceBitmap.getHeight(), matrix, true);
		if (recycle && sourceBitmap != null && !sourceBitmap.isRecycled()) {
			sourceBitmap.recycle();
			sourceBitmap = null;
		}
		return bmp;
	}

	/**
	 * Return a vertical Bitmap image
	 * 
	 * @param sourceBitmap
	 *            source bitmap
	 * @return Bitmap
	 */
	public static Bitmap getMirrorVerticalBitmap(Bitmap sourceBitmap) {
		if (isEmty(sourceBitmap)) {
			printLog("getMirrorVerticalBitmap sourceBitmap is null");
			return null;
		}
		return getMirrorVerticalBitmap(sourceBitmap, true);
	}

	public static Bitmap getMirrorVerticalBitmap(Bitmap sourceBitmap, boolean recycle) {
		if (isEmty(sourceBitmap)) {
			printLog("getMirrorVerticalBitmap sourceBitmap is null");
			return null;
		}
		Matrix matrix = getMatrix();
		matrix.setScale(1, -1);
		matrix.postTranslate(0, sourceBitmap.getHeight());
		Bitmap bmp = Bitmap.createBitmap(sourceBitmap, 0, 0, sourceBitmap.getWidth(), sourceBitmap.getHeight(), matrix, true);
		if (recycle && sourceBitmap != null && !sourceBitmap.equals(bmp) && !sourceBitmap.isRecycled()) {
			sourceBitmap.recycle();
			sourceBitmap = null;
		}
		return bmp;
	}

	/**
	 * get Screen shot Bitmap
	 * 
	 * @param sourceBitmap
	 * @param id
	 * @return
	 */
	public static Bitmap getScreenshotBitmap(Context context, Bitmap sourceBitmap, int id) {
		if (isEmty(sourceBitmap)) {
			printLog("getScreenshotBitmap bitmap is null");
			return null;
		}
		Bitmap screenshot = Bitmap.createBitmap(sourceBitmap.getWidth(), sourceBitmap.getHeight(), Config.ARGB_8888);
		Bitmap content = BitmapFactory.decodeResource(context.getResources(), id);
		Canvas canvas = new Canvas(screenshot);
		canvas.drawBitmap(content, (sourceBitmap.getWidth() - content.getWidth()) / 2, (sourceBitmap.getHeight() - content.getHeight()) / 2,
				new Paint());
		canvas.drawBitmap(sourceBitmap, 0, 0, new Paint());
		canvas.save();
		canvas.restore();
		return screenshot;
	}

	/**
	 * status bar is exist
	 * 
	 * @param activity
	 * @return
	 */
	public static Bitmap snapShotWithStatusBar(Activity activity) {
		View view = activity.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap bmp = view.getDrawingCache();
		int width = ScreenUtil.getScreenWidth(activity);
		int height = ScreenUtil.getScreenHeight(activity);
		Bitmap bp = null;
		bp = Bitmap.createBitmap(bmp, 0, 0, width, height);
		view.destroyDrawingCache();
		return bp;

	}

	/**
	 * status bar is not exist
	 * 
	 * @param activity
	 * @return
	 */
	public static Bitmap snapShotWithoutStatusBar(Activity activity) {
		View view = activity.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap bmp = view.getDrawingCache();
		Rect frame = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;
		int width = ScreenUtil.getScreenWidth(activity);
		int height = ScreenUtil.getScreenHeight(activity);
		Bitmap bp = null;
		bp = Bitmap.createBitmap(bmp, 0, statusBarHeight, width, height - statusBarHeight);
		view.destroyDrawingCache();
		return bp;
	}

	/**
	 * save images after Rotating
	 * 
	 * @param path
	 * @param data
	 * @param rotation
	 */
	public static void saveJpegData(String path, byte[] data, int rotation) {
		if (data == null || data.length == 0) {
			printLog("saveJpegData data==null||data.length==0");
			return;
		}
		try {
			Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
			Matrix matrix = new Matrix();
			matrix.reset();
			matrix.postRotate(rotation);
			bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
			File file = new File(path);
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
			bmp.compress(CompressFormat.JPEG, 100, bos);
			bos.flush();
			bos.close();
			recycle(bmp);
			data = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * save Bitmap To Storage,run in UI Thread
	 * 
	 * @param path
	 *            saved path
	 * @param bitmap
	 *            saved bitmap
	 * @return
	 */
	public static boolean saveBitmap(String path, Bitmap bitmap) {
		if (isEmty(bitmap)) {
			printLog("saveBitmapToStorage bitmap is null");
			return false;
		}
		FileOutputStream fileoutputstream = null;
		try {
			fileoutputstream = new FileOutputStream(path);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		bitmap.compress(CompressFormat.JPEG, 100, fileoutputstream);
		try {
			if (null != fileoutputstream) {
				fileoutputstream.close();
			}
			return true;
		} catch (Exception exception4) {
			Log.e(TAG, "saveImageToStorage error");
			return false;
		}
	}

	/**
	 * The image real-time updates to the mobile phone in the gallery
	 * 
	 * @param context
	 * @param bmp
	 * @param absPath
	 */
	public static void saveImageToGallery(Context context, Bitmap bmp, String absPath) {
		if (isEmty(bmp)) {
			printLog("saveImageToGallery bitmap is null");
			return;
		}
		File appDir = new File(absPath);
		if (!appDir.exists() && !appDir.isDirectory()) {
			appDir.mkdir();
		}
		String fileName = "IMAGE_" + System.currentTimeMillis() + ".jpg";
		File file = new File(appDir, fileName);
		try {
			FileOutputStream fos = new FileOutputStream(file);
			bmp.compress(CompressFormat.JPEG, 100, fos);
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(absPath + fileName))));
	}

	/**
	 * save Image To MediaStore
	 * 
	 * @param imagePath
	 * @param l
	 * @param orientation
	 * @param l1
	 * @param location
	 * @param contentresolver
	 * @return
	 */
	public static Uri savetImageToMediaStore(String imagePath, long l, int orientation, long l1, Location location, ContentResolver contentresolver) {
		String fileName = imagePath.substring(imagePath.lastIndexOf("/"), imagePath.lastIndexOf("."));
		ContentValues contentvalues = new ContentValues(9);
		contentvalues.put("title", fileName);
		contentvalues.put("_display_name", (new StringBuilder()).append(fileName).append(".jpg").toString());
		contentvalues.put("datetaken", Long.valueOf(l));
		contentvalues.put("mime_type", "image/jpeg");
		contentvalues.put("orientation", Integer.valueOf(orientation));
		contentvalues.put("_data", imagePath);
		contentvalues.put("_size", Long.valueOf(l1));
		if (location != null) {
			contentvalues.put("latitude", Double.valueOf(location.getLatitude()));
			contentvalues.put("longitude", Double.valueOf(location.getLongitude()));
		}
		Uri uri = contentresolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentvalues);
		if (uri == null) {
			Log.e(TAG, "Failed to write MediaStore");
			uri = null;
		}
		return uri;
	}

	/**
	 * recycle bitmap
	 * 
	 * @param bitmap
	 */
	public static void recycle(Bitmap bitmap) {
		if (null == bitmap || bitmap.isRecycled()) {
			return;
		}
		bitmap.recycle();
		bitmap = null;
	}

	/**
	 * whether bitmap is null or is recycled
	 * 
	 * @param bitmap
	 */
	public static boolean isEmty(Bitmap bitmap) {
		if (bitmap == null || bitmap.isRecycled()) {
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
		if (STLog.DEBUG) {
			Log.d(TAG, logStr);
		}
	}
}
