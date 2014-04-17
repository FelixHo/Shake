package com.shake.app.utils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.widget.Toast;

import com.shake.app.HomeApp;

public final class ImageTools {
	 
		private static int count = 0;

		/**
		 * 保存图片到本地相册
		 * @author 俊浩
		 * 
		 */
		public static void savePic(final Context context,Bitmap bmp,String name)
		{
			if(HomeApp.getMyApplication().isSDCardMounted())
			{
				if(bmp!=null)
				{				
					final String uri=MediaStore.Images.Media.insertImage(context.getContentResolver(), bmp,name,"Shake");
							
					Log.i("图片保存路径", uri);
					if(uri!=null)
					{
						new Handler().post(new Runnable() {
							
							@Override
							public void run() {
								Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);				    
							    mediaScanIntent.setData(Uri.parse(uri));
							    context.sendBroadcast(mediaScanIntent);
							    context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"+ Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES))));
							    Toast.makeText(context, "成功保存!",Toast.LENGTH_SHORT).show();
							}
						});					
					}
					else
					{
						Toast.makeText(context, "图片保存失败!"+uri, Toast.LENGTH_LONG).show();
					}			
				}				
			}
			else
			{
				Toast.makeText(context, "请插入存储卡!", Toast.LENGTH_SHORT).show();
			}		
		}
		/**
		 * 压缩bitmap到指定字节流大小
		 * @param image 原图
		 * @param targetInKB 目标大小
		 * @return 压缩比 .之所以返回压缩比而不是返回位图是因为返回位图是毫无意义的，位图的像素不会因为压缩而减少
		 * 所以返回的位图不会有任何变化，压缩比的作用是用于产生对应目标大小的字节流，字节流对应的大小才是真实的文件大小
		 * 要减少位图的大小即占用内存的大小需要的不是压缩图片质量，而是减少像素即裁剪图片的尺寸，而需要控制文件的大小需要的
		 * 是降低图片的质量 
		 * @author 俊浩
		 */
		public static int compressImage(Bitmap image,int targetKB) {

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
			int options = 95;
			Log.i("压缩前大小:",baos.toByteArray().length/1024+"KB");
			while ( baos.toByteArray().length / 1024>targetKB) {	//循环判断如果压缩后图片是否大于100kb,大于继续压缩		
				baos.reset();//重置baos即清空baos
				image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
				Log.i("压缩后大小:",baos.toByteArray().length/1024+"KB");
				options-=5;			
			}
			Log.i("压缩后最终大小:",baos.toByteArray().length/1024+"KB");
			return options;
		}

		/**
		 * 
		 * @param imgPath
		 * @param targetW
		 * @param targetH
		 * @return
		 */
		public static Bitmap decodeBitmapFromFileInSampleSize(String imgPath,int targetW,int targetH)
		{
			// Get the dimensions of the bitmap
		    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		    bmOptions.inJustDecodeBounds = true;
		    BitmapFactory.decodeFile(imgPath, bmOptions);
		 // Determine how much to scale down the image
		    
		  
		 // Decode the image file into a Bitmap sized to fill the View
		    bmOptions.inJustDecodeBounds = false;
		    bmOptions.inSampleSize = calculateInSampleSize(bmOptions, targetW, targetH);
		    bmOptions.inPurgeable = true;
		   return BitmapFactory.decodeFile(imgPath, bmOptions);
		}
		
		public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
			// Raw height and width of image
			final int height = options.outHeight;
			final int width = options.outWidth;
			int inSampleSize = 1;

			if (height > reqHeight || width > reqWidth) {
				if (width > height) {
					inSampleSize = Math.round((float) height / (float) reqHeight);
				} else {
					inSampleSize = Math.round((float) width / (float) reqWidth);
				}

				// This offers some additional logic in case the image has a strange
				// aspect ratio. For example, a panorama may have a much larger
				// width than height. In these cases the total pixels might still
				// end up being too large to fit comfortably in memory, so we should
				// be more aggressive with sample down the image (=larger
				// inSampleSize).

				final float totalPixels = width * height;

				// Anything more than 2x the requested pixels we'll sample down
				// further.
				final float totalReqPixelsCap = reqWidth * reqHeight * 2;

				while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
					inSampleSize++;
				}
			}
			 Log.i("scaleFactor", "W:"+width+" H:"+height+"scale: "+inSampleSize+"");
			return inSampleSize;
		}
		/**
		 * 
		 * @param contentUri
		 * @return
		 */
		
		public static String getRealPathFromURI(Uri contentUri) {
	        String[] proj = { MediaColumns.DATA };
	        Cursor cursor = HomeApp.getMyApplication().getContentResolver().query(contentUri, proj, null, null, null);
	        int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
	        cursor.moveToFirst();
	        return cursor.getString(column_index);
	    }
		

		/**
		 * Transfer drawable to bitmap
		 * 
		 * @param drawable
		 * @return
		 */
		public static Bitmap drawableToBitmap(Drawable drawable) {
			int w = drawable.getIntrinsicWidth();
			int h = drawable.getIntrinsicHeight();

			Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
					: Bitmap.Config.RGB_565;
			Bitmap bitmap = Bitmap.createBitmap(w, h, config);
			Canvas canvas = new Canvas(bitmap);
			drawable.setBounds(0, 0, w, h);
			drawable.draw(canvas);
			return bitmap;
		}

		/**
		 * Bitmap to drawable
		 * 
		 * @param bitmap
		 * @return
		 */
		@SuppressWarnings("deprecation")
		public static Drawable bitmapToDrawable(Bitmap bitmap) {
			return new BitmapDrawable(bitmap);
		}

		/**
		 * Input stream to bitmap
		 * 
		 * @param inputStream
		 * @return
		 * @throws Exception
		 */
		public static Bitmap inputStreamToBitmap(InputStream inputStream)
				throws Exception {
			return BitmapFactory.decodeStream(inputStream);
		}

		/**
		 * Byte transfer to bitmap
		 * 
		 * @param byteArray
		 * @return
		 */
		public static Bitmap byteToBitmap(byte[] byteArray) {
			if (byteArray.length != 0) {
				return BitmapFactory
						.decodeByteArray(byteArray, 0, byteArray.length);
			} else {
				return null;
			}
		}

		/**
		 * Byte transfer to drawable
		 * 
		 * @param byteArray
		 * @return
		 */
		public static Drawable byteToDrawable(byte[] byteArray) {
			ByteArrayInputStream ins = null;
			if (byteArray != null) {
				ins = new ByteArrayInputStream(byteArray);
			}
			return Drawable.createFromStream(ins, null);
		}

		/**
		 * Bitmap transfer to bytes
		 * 
		 * @param byteArray
		 * @return
		 */
		public static byte[] bitmapToBytes(Bitmap bm) {
			byte[] bytes = null;
			if (bm != null) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
				bytes = baos.toByteArray();
			}
			return bytes;
		}

		/**
		 * Drawable transfer to bytes
		 * 
		 * @param drawable
		 * @return
		 */
		public static byte[] drawableToBytes(Drawable drawable) {
			BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
			Bitmap bitmap = bitmapDrawable.getBitmap();
			byte[] bytes = bitmapToBytes(bitmap);
			;
			return bytes;
		}

		/**
		 * Base64 to byte[]
//		 */
//		public static byte[] base64ToBytes(String base64) throws IOException {
//			byte[] bytes = Base64.decode(base64);
//			return bytes;
//		}
	//
//		/**
//		 * Byte[] to base64
//		 */
//		public static String bytesTobase64(byte[] bytes) {
//			String base64 = Base64.encode(bytes);
//			return base64;
//		}

		/**
		 * Create reflection images
		 * 
		 * @param bitmap
		 * @return
		 */
		public static Bitmap createReflectionImageWithOrigin(Bitmap bitmap) {
			final int reflectionGap = 4;
			int w = bitmap.getWidth();
			int h = bitmap.getHeight();

			Matrix matrix = new Matrix();
			matrix.preScale(1, -1);

			Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, h / 2, w,
					h / 2, matrix, false);

			Bitmap bitmapWithReflection = Bitmap.createBitmap(w, (h + h / 2),
					Config.ARGB_8888);

			Canvas canvas = new Canvas(bitmapWithReflection);
			canvas.drawBitmap(bitmap, 0, 0, null);
			Paint deafalutPaint = new Paint();
			canvas.drawRect(0, h, w, h + reflectionGap, deafalutPaint);

			canvas.drawBitmap(reflectionImage, 0, h + reflectionGap, null);

			Paint paint = new Paint();
			LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0,
					bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff,
					0x00ffffff, TileMode.CLAMP);
			paint.setShader(shader);
			// Set the Transfer mode to be porter duff and destination in
			paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
			// Draw a rectangle using the paint with our linear gradient
			canvas.drawRect(0, h, w, bitmapWithReflection.getHeight()
					+ reflectionGap, paint);

			return bitmapWithReflection;
		}

		/**
		 * Get rounded corner images
		 * 
		 * @param bitmap
		 * @param roundPx
		 *            5 10
		 * @return
		 */
		public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx,int width, int height) {
			
//			int w = bitmap.getWidth();
//			int h = bitmap.getHeight();
			
			bitmap = zoomBitmap(bitmap,width,height);
			
			Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
			Canvas canvas = new Canvas(output);
			final int color = 0xff424242;
			final Paint paint = new Paint();
			final Rect rect = new Rect(0, 0, width, height);
			final RectF rectF = new RectF(rect);
			paint.setAntiAlias(true);
			canvas.drawARGB(0, 0, 0, 0);
			paint.setColor(color);
			canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
			canvas.drawBitmap(bitmap, rect, rect, paint);

			return output;
		}
		
		 /**
	     * 　为指定图片增加阴影
	     * 
	     * @param map　图片
	     * @param radius　阴影的半径
	     * @return
	     */
	    public static Bitmap drawShadow(Bitmap map, int radius) {
	        if (map == null)
	            return null;

	        BlurMaskFilter blurFilter = new BlurMaskFilter(radius, BlurMaskFilter.Blur.OUTER);
	        Paint shadowPaint = new Paint();
	        shadowPaint.setMaskFilter(blurFilter);

	        int[] offsetXY = new int[2];
	        Bitmap shadowImage = map.extractAlpha(shadowPaint, offsetXY);
	        shadowImage = shadowImage.copy(Config.ARGB_8888, true);
	        Canvas c = new Canvas(shadowImage);
	        c.drawBitmap(map, -offsetXY[0], -offsetXY[1], null);
	        return shadowImage;
	    }

		/**
		 * Resize the bitmap
		 * 
		 * @param bitmap
		 * @param width
		 * @param height
		 * @return
		 */
		public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
			int w = bitmap.getWidth();
			int h = bitmap.getHeight();
			Matrix matrix = new Matrix();
			float scaleWidth = ((float) width / w);
			float scaleHeight = ((float) height / h);
			matrix.postScale(scaleWidth, scaleHeight);
			Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
			return newbmp;
		}

		/**
		 * Resize the drawable
		 * @param drawable
		 * @param w
		 * @param h
		 * @return
		 */
		@SuppressWarnings("deprecation")
		public static Drawable zoomDrawable(Drawable drawable, int w, int h) {
			int width = drawable.getIntrinsicWidth();
			int height = drawable.getIntrinsicHeight();
			Bitmap oldbmp = drawableToBitmap(drawable);
			Matrix matrix = new Matrix();
			float sx = ((float) w / width);
			float sy = ((float) h / height);
			matrix.postScale(sx, sy);
			Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height,
					matrix, true);
			return new BitmapDrawable(newbmp);
		}
		
		/**
		 * Get images from SD card by path and the name of image
		 * @param photoName
		 * @return
		 */
		public static Bitmap getPhotoFromSDCard(String path,String photoName){
			Bitmap photoBitmap = BitmapFactory.decodeFile(path + "/" +photoName +".png");
			if (photoBitmap == null) {
				return null;
			}else {
				return photoBitmap;
			}
		}
		
		/**
		 * Check the SD card 
		 * @return
		 */
		public static boolean checkSDCardAvailable(){
			return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
		}
		
		/**
		 * Get image from SD card by path and the name of image
		 * @param fileName
		 * @return
		 */
		public static boolean findPhotoFromSDCard(String path,String photoName){
			boolean flag = false;
			
			if (checkSDCardAvailable()) {
				File dir = new File(path);
				if (dir.exists()) {
					File folders = new File(path);
					File photoFile[] = folders.listFiles();
					for (int i = 0; i < photoFile.length; i++) {
						String fileName = photoFile[i].getName().split("\\.")[0];
						if (fileName.equals(photoName)) {
							flag = true;
						}
					}
				}else {
					flag = false;
				}
//				File file = new File(path + "/" + photoName  + ".jpg" );
//				if (file.exists()) {
//					flag = true;
//				}else {
//					flag = false;
//				}
				
			}else {
				flag = false;
			}
			return flag;
		}
		
		/**
		 * Save image to the SD card 
		 * @param photoBitmap
		 * @param photoName
		 * @param path
		 * @return absolute path
		 */
		public static String savePhotoToSDCard(Bitmap photoBitmap,String path,String photoName,int targetKB){
			File dir = new File(path);
			if (!dir.exists()){
				dir.mkdirs();
			}
			
			File photoFile = new File(path , photoName);
			FileOutputStream fileOutputStream = null;
			try {
				if(photoFile!=null&&photoFile.exists()){
					photoFile.delete();
				}
				fileOutputStream = new FileOutputStream(photoFile);
				if (photoBitmap != null) {
					if (photoBitmap.compress(Bitmap.CompressFormat.JPEG, compressImage(photoBitmap, targetKB), fileOutputStream)) {
						fileOutputStream.flush();
					}
				}
			} catch (FileNotFoundException e) {
				photoFile.delete();
				e.printStackTrace();
			} catch (IOException e) {
				photoFile.delete();
				e.printStackTrace();
			} finally{
				try {
					fileOutputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return photoFile.getAbsolutePath();
		}
		
		/**
		 * Delete the image from SD card
		 * @param context
		 * @param path
		 * file:///sdcard/temp.jpg
		 */
		public static void deleteAllPhoto(String path){
			if (checkSDCardAvailable()) {
				File folder = new File(path);
				File[] files = folder.listFiles();
				for (int i = 0; i < files.length; i++) {
					files[i].delete();
				}
			}
		}
		
		public static void deletePhotoAtPathAndName(String path,String fileName){
			if (checkSDCardAvailable()) {
				File folder = new File(path);
				File[] files = folder.listFiles();
				for (int i = 0; i < files.length; i++) {
					if (files[i].getName().split("\\.")[0].equals(fileName)) {
						files[i].delete();
					}
				}
			}
		}
		
		/**
		 * 返回图片字节
		 * @param data
		 * @return 字节数
		 */
		
		 @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
		    public static int getBitmapSizeInByte(Bitmap data) {
		        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR1) {
		            return data.getRowBytes() * data.getHeight();
		        } else {
		            return data.getByteCount();
		        }
		    }
		 
			public static int computeSampleSize(BitmapFactory.Options options, int target) {
				int w = options.outWidth;
				int h = options.outHeight;
				float candidateW = w / target;
				float candidateH = h / target;
				float candidate = Math.max(candidateW, candidateH);
				candidate = (float) (candidate + 0.5);
			
				if (candidate < 1.0)
					return 1;
		
				return (int) candidate;
			}

			public static Bitmap loadBitmap(Context c, String fileName, int width) {
				ParcelFileDescriptor pfd;
				try {
					pfd = c.getContentResolver().openFileDescriptor(Uri.parse("file://" + fileName), "r");
				} catch (IOException ex) {
					return null;
				}
				java.io.FileDescriptor fd = pfd.getFileDescriptor();
				BitmapFactory.Options options = new BitmapFactory.Options();
				// options.inTempStorage = new byte[10 * 1024];
				// 先指定原始大小
				options.inSampleSize = 1;
				// 只进行大小判断
				options.inJustDecodeBounds = true;
				// 调用此方法得到options得到图片的大小
				BitmapFactory.decodeFileDescriptor(fd, null, options);
				// 我们的目标是在400pixel的画面上显示。
				// 所以需要调用computeSampleSize得到图片缩放的比例
				options.inSampleSize = computeSampleSize(options, width);
				// OK,我们得到了缩放的比例，现在开始正式读入BitMap数据
				options.inJustDecodeBounds = false;
				options.inDither = false;
				options.inPreferredConfig = Bitmap.Config.ARGB_8888;

				// 根据options参数，减少所需要的内存
				try {
					Bitmap sourceBitmap = BitmapFactory.decodeFileDescriptor(fd, null, options);
					if (count < 4) {
						count++;
					} else {
						count = 0;
						System.gc();
					}
					return sourceBitmap;
				} catch (OutOfMemoryError e) {
					Log.i("loadBitmap","oom");

					return loadBitmap(c, fileName, (int) (width * 0.5));
				}

			}
			
			public static boolean saveBitmap(String bitName, Bitmap bitmap) {
				try {
					File temp = File.createTempFile("temp", ".png", new File(StringUtil.getNameDelLastPath(bitName)));
					FileOutputStream fOut = null;
					try {
						fOut = new FileOutputStream(temp);
					} catch (FileNotFoundException e) {
					}
					bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
					fOut.flush();
					fOut.close();
					if (temp.exists()) {
						File f = new File(bitName);
						if (f.exists()) {
							f.delete();
						}
						FileUtil.moveFile(temp.getAbsolutePath(), bitName);
					}

					return true;

				} catch (IOException e) {
					e.printStackTrace();
				}

				return false;
			}
			
			/**
			 * 返回指定路径指定大小的一个图的drawable
			 * @param context
			 * @param path
			 * @param targetWidth
			 * @param targetHeight
			 * @return
			 */
			public static Drawable  getResizeDrawableByPath(Context context,String path, int targetWidth,int targetHeight)
			{
				Drawable d = Drawable.createFromPath(path);
				Bitmap b = ((BitmapDrawable)d).getBitmap();
			    Bitmap bitmapResized = Bitmap.createScaledBitmap(b, targetWidth, targetHeight, false);
				Drawable result = new BitmapDrawable(context.getResources(), bitmapResized);
				b=null;
				bitmapResized = null;
				return result;
			}
			
			/**
			 * 缩放drawable大小
			 * @param context
			 * @param drawable
			 * @param w
			 * @param h
			 * @return
			 */
			public static Drawable resizeDrawable(Context context,Drawable drawable,int w,int h)
			{
				Bitmap b = ((BitmapDrawable)drawable).getBitmap();
			    Bitmap bitmapResized = Bitmap.createScaledBitmap(b, w, h, false);
				Drawable result = new BitmapDrawable(context.getResources(), bitmapResized);
				b=null;
				bitmapResized = null;
				return result;
			}
			/**
			 * 返回方向正确的bitmap
			 * @param src 图片路径
			 * @param bitmap 源图片
			 * @return
			 */
			public static Bitmap rotateBitmap(String src, Bitmap bitmap) {
		        try {
		            int orientation = getExifOrientation(src);
		            
		            if (orientation == 1) {
		                return bitmap;
		            }
		            
		            Matrix matrix = new Matrix();
		            switch (orientation) {
		            case 2:
		                matrix.setScale(-1, 1);
		                break;
		            case 3:
		                matrix.setRotate(180);
		                break;
		            case 4:
		                matrix.setRotate(180);
		                matrix.postScale(-1, 1);
		                break;
		            case 5:
		                matrix.setRotate(90);
		                matrix.postScale(-1, 1);
		                break;
		            case 6:
		                matrix.setRotate(90);
		                break;
		            case 7:
		                matrix.setRotate(-90);
		                matrix.postScale(-1, 1);
		                break;
		            case 8:
		                matrix.setRotate(-90);
		                break;
		            default:
		                return bitmap;
		            }
		            
		            try {
		                Bitmap oriented = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
		                bitmap=null;
		                return oriented;
		            } catch (OutOfMemoryError e) {
		                e.printStackTrace();
		                return bitmap;
		            }
		        } catch (IOException e) {
		            e.printStackTrace();
		        } 
		        
		        return bitmap;
		    }
		    
		    private static int getExifOrientation(String src) throws IOException {
		        int orientation = 1;
		        
		        try {
		            /**
		             * if your are targeting only api level >= 5
		             * ExifInterface exif = new ExifInterface(src);
		             * orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
		             */
		            if (Build.VERSION.SDK_INT >= 5) {
		                Class<?> exifClass = Class.forName("android.media.ExifInterface");
		                Constructor<?> exifConstructor = exifClass.getConstructor(new Class[] { String.class });
		                Object exifInstance = exifConstructor.newInstance(new Object[] { src });
		                Method getAttributeInt = exifClass.getMethod("getAttributeInt", new Class[] { String.class, int.class });
		                Field tagOrientationField = exifClass.getField("TAG_ORIENTATION");
		                String tagOrientation = (String) tagOrientationField.get(null);
		                orientation = (Integer) getAttributeInt.invoke(exifInstance, new Object[] { tagOrientation, 1});
		            }
		        } catch (ClassNotFoundException e) {
		            e.printStackTrace();
		        } catch (SecurityException e) {
		            e.printStackTrace();
		        } catch (NoSuchMethodException e) {
		            e.printStackTrace();
		        } catch (IllegalArgumentException e) {
		            e.printStackTrace();
		        } catch (InstantiationException e) {
		            e.printStackTrace();
		        } catch (IllegalAccessException e) {
		            e.printStackTrace();
		        } catch (InvocationTargetException e) {
		            e.printStackTrace();
		        } catch (NoSuchFieldException e) {
		            e.printStackTrace();
		        }
		        
		        return orientation;
		    }
		    /**
		     * 返回方向正确的bitmap 方法2
		     * @param path
		     * @param srcBitmap
		     * @return
		     */
		    public static Bitmap getCorrectOrientationBitmap(String path,Bitmap srcBitmap)
		    {
		    	//调整照片的方向
				ExifInterface exif1;
				try {
					exif1 = new ExifInterface(path);
				
		          int exifOrientation = exif1.getAttributeInt(
		          ExifInterface.TAG_ORIENTATION,
		          ExifInterface.ORIENTATION_NORMAL);

		          int rotate = 0;

		          switch (exifOrientation) 
		         {

		          case ExifInterface.ORIENTATION_ROTATE_90:
		          rotate = 90;
		          break; 

		          case ExifInterface.ORIENTATION_ROTATE_180:
		          rotate = 180;
		          break;

		          case ExifInterface.ORIENTATION_ROTATE_270:
		          rotate = 270;
		          break;
		         }
		          if (rotate != 0) 
		          {
		              int w = srcBitmap.getWidth();
		              int h = srcBitmap.getHeight();
		              Matrix mtx = new Matrix();
		              mtx.preRotate(rotate);

		              srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, w, h, mtx, false);
		           }
				} catch (IOException e) {
					e.printStackTrace();
				}
				return srcBitmap;
		    }
}
