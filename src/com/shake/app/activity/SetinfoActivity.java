package com.shake.app.activity;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.shake.app.Define;
import com.shake.app.HomeApp;
import com.shake.app.R;
import com.shake.app.fragment.CardFragment;
import com.shake.app.utils.FileUtil;
import com.shake.app.utils.ImageTools;
import com.shake.app.utils.MyActivityManager;
import com.shake.app.utils.MySharedPreferences;
import com.shake.app.utils.MyToast;
import com.shake.app.utils.MyWindowManager;

public class SetinfoActivity extends Activity {

	private int mYear = 0;
	
	private int mMonth = 0;
	
	private int mDay = 0;
	
	private EditText birth;
	
	private EditText name;
	
	private EditText mobile;
	
	private EditText email;
	
	private EditText profile;
	
	private EditText homeSite;
	
	private Button commit;
	
	private ImageView avatar;
	
	public DatePickerDialog datePicker;
	
	public Calendar mCalendar ;

	private static final int TAKE_PICTURE = 0;
	
	private static final int CHOOSE_PICTURE = 1;
	
	private String avatarPath="";
	
	private boolean isEdit = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyWindowManager.setFullScreenWithNoTitle(this);
		setContentView(R.layout.activity_setinfo);
		isEdit = getIntent().getBooleanExtra(CardFragment.EDIT_REQUEST_KEY, false);
		initDate();
		setupView();
		initView();
	}
	
	private void setupView()
	{
		birth = (EditText)findViewById(R.id.welcome_edittext_birth);
		name  = (EditText)findViewById(R.id.welcome_edittext_name);
		mobile = (EditText)findViewById(R.id.welcome_edittext_mobile);
		email = (EditText)findViewById(R.id.welcome_edittext_mail);
		profile = (EditText)findViewById(R.id.welcome_edittext_profile);
		commit = (Button)findViewById(R.id.welcome_button_commit);
		avatar = (ImageView)findViewById(R.id.welcome_imageview_avatar);
		homeSite = (EditText)findViewById(R.id.welcome_edittext_homesite);
	}
	
	private void initView()
	{
		birth.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				if(event.getAction() ==MotionEvent.ACTION_DOWN)
				{
					datePicker = new DatePickerDialog(SetinfoActivity.this, new OnDateSetListener() 
					{
						
						@Override
						public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth) 
						{
							mYear = year;
							mMonth = monthOfYear;
							mDay = dayOfMonth;
							birth.setText(year+"年"+(monthOfYear+1)+"月"+dayOfMonth+"日");						
						}
					}, mYear, mMonth, mDay);
					datePicker.show();	
				}
				
				return false;
			}
		});
		
		commit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(checkComplete())
				{
					String birthday ="";
					
					String pro = "";
					
					String site = "";
					
					MySharedPreferences.SaveShared(Define.CONFINFO, Define.USER_INFO_NAME_KEY, name.getText().toString().trim(),false);
					
					MySharedPreferences.SaveShared(Define.CONFINFO, Define.USER_INFO_MOBILE_KEY, mobile.getText().toString().trim(),false);
					
					MySharedPreferences.SaveShared(Define.CONFINFO, Define.USER_INFO_EMAIL_KEY, email.getText().toString().trim(),false);
					
					if(!birth.getText().toString().trim().equals(""))
					{
						birthday = birth.getText().toString();
					}
					MySharedPreferences.SaveShared(Define.CONFINFO, Define.USER_INFO_BIRTH_KEY,birthday,false);
					
					if(!profile.getText().toString().trim().equals(""))
					{
						pro = profile.getText().toString().trim();
					}
					MySharedPreferences.SaveShared(Define.CONFINFO, Define.USER_INFO_PROFILE_KEY,pro,false);

					if(!homeSite.getText().toString().trim().equals(""))
					{
						site = homeSite.getText().toString().trim();
					}
					MySharedPreferences.SaveShared(Define.CONFINFO, Define.USER_INFO_HOMESITE_KEY,site,false);

					MySharedPreferences.SaveShared(Define.CONFINFO, Define.USER_INFO_AVATAR_KEY, avatarPath,false);
					
					if(isEdit)
					{
						setResult(RESULT_OK);
						finish();
					}
					else
					{
						MyActivityManager.jump(SetinfoActivity.this, MainActivity.class);
						finish();
					}
					
				}
			}
		});
		
		avatar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showPicturePicker(SetinfoActivity.this);
			}
		});
		if(isEdit)
		{
			commit.setText("编辑完成");
		}
		name.setText(MySharedPreferences.getShared(Define.CONFINFO, Define.USER_INFO_NAME_KEY, false));
		mobile.setText(MySharedPreferences.getShared(Define.CONFINFO, Define.USER_INFO_MOBILE_KEY, false));
		email.setText(MySharedPreferences.getShared(Define.CONFINFO, Define.USER_INFO_EMAIL_KEY, false));
		birth.setText(MySharedPreferences.getShared(Define.CONFINFO, Define.USER_INFO_BIRTH_KEY, false));
		profile.setText(MySharedPreferences.getShared(Define.CONFINFO, Define.USER_INFO_PROFILE_KEY, false));
		homeSite.setText(MySharedPreferences.getShared(Define.CONFINFO, Define.USER_INFO_HOMESITE_KEY, false));
		avatarPath = MySharedPreferences.getShared(Define.CONFINFO, Define.USER_INFO_AVATAR_KEY, false);
		ImageLoader.getInstance().displayImage(Uri.fromFile(new File(avatarPath)).toString(), avatar);
	}
	
	/**
	 * 初始化时间值
	 * @author Felix
	 */
	private void initDate()
	{
		mCalendar = Calendar.getInstance(Locale.CHINA);
		mCalendar.setTime(new Date());
		mYear = mCalendar.get(Calendar.YEAR);
		mMonth = mCalendar.get(Calendar.MONTH);
		mDay = mCalendar.get(Calendar.DAY_OF_MONTH);
	}
	
	/**
	 * 检查信息完整性和合法性
	 * 
	 */
	
	private boolean checkComplete()
	{
		if(name.getText().toString().trim().equals(""))
		{
			MyToast.alert("请输入姓名");
			name.requestFocus();
			return false;
		}
		if(mobile.getText().toString().trim().equals(""))
		{
			MyToast.alert("请输入手机号码");
			mobile.requestFocus();
			return false;
		}
		if(!isMobileValid(mobile.getText().toString().trim()))
		{
			MyToast.alert("请输入合法的手机号码");
			mobile.requestFocus();
			return false;
		}
		if(email.getText().toString().trim().equals(""))
		{
			MyToast.alert("请输入邮箱地址");
			email.requestFocus();
			return false;
		}
		if(!isEmailValid(email.getText().toString().trim()))
		{
			MyToast.alert("请输入合法的邮箱地址");
			email.requestFocus();
			return false;
		}
		return true;
	}
	
	/**
	 * 检查是否为合法Email地址
	 * @param email
	 * @return 是否合法
	 */
	public static boolean isEmailValid(String email) {
	    boolean isValid = false;

	    String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
	    CharSequence inputStr = email;

	    Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
	    Matcher matcher = pattern.matcher(inputStr);
	    if (matcher.matches()) {
	        isValid = true;
	    }
	    return isValid;
	}
	
	/**
	 * 检查是否为合法手机号码
	 * @param mobile
	 * @return 是否合法
	 */
	public static boolean isMobileValid(String mobile) {
	    boolean isValid = false;

	    String expression = "1[0-9]{10}";
	    CharSequence inputStr = mobile;

	    Pattern pattern = Pattern.compile(expression);
	    Matcher matcher = pattern.matcher(inputStr);
	    if (matcher.matches()) {
	        isValid = true;
	    }
	    return isValid;
	}

	
	  /**
	   * 获取图片来源选择框
	   * @param context
	   */
	  public void showPicturePicker(Context context){
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle("图片来源");
			builder.setNegativeButton("取消", null);
			builder.setItems(new String[]{"拍照","相册"}, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case TAKE_PICTURE:
						Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
						Uri imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),"temp_image"));
						//指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
						openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
						startActivityForResult(openCameraIntent, TAKE_PICTURE);
						break;
						
					case CHOOSE_PICTURE:
						Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
						openAlbumIntent.setType("image/*");
						startActivityForResult(openAlbumIntent, CHOOSE_PICTURE);
						break;

					default:
						break;
					}
				}
			});
			builder.create().show();
		}
	  
	    /**
	     * 照片处理结果返回
	     */
	    @Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			super.onActivityResult(requestCode, resultCode, data);
			if (resultCode == RESULT_OK) {
				switch (requestCode) {
				case TAKE_PICTURE:
				{
					new getPicFromCameraTask().execute();	
					break;
					}
					
				case CHOOSE_PICTURE:
				{
					new getPicFromAlbum().execute(data);
					break;
				}
				default:break;
				}
			}
		}
	    
	    /**
	     * 从相册获取图片异步线程
	     * @author Felix
	     */
	    class getPicFromAlbum extends AsyncTask<Intent, String, Bitmap>
	    {

			@Override
			protected Bitmap doInBackground(Intent... params) {
				
				Intent data = params[0];
				
				if (data != null) {
					Uri originalUri = data.getData();
					
					String[] proj = { MediaStore.Images.Media.DATA };
					
					ContentResolver mContentResolver = SetinfoActivity.this.getContentResolver();
					
					Cursor cursor = mContentResolver.query(originalUri, proj, null, null, null);

					if (cursor != null) {
						int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

						cursor.moveToFirst();

						String path = cursor.getString(column_index);
						Bitmap photo = ImageTools.decodeBitmapFromFileInSampleSize(path, Define.WIDTH_PX/2, Define.HEIGHT_PX/2);	
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
				              int w = photo.getWidth();
				              int h = photo.getHeight();
				              Matrix mtx = new Matrix();
				              mtx.preRotate(rotate);

				              photo = Bitmap.createBitmap(photo, 0, 0, w, h, mtx, false);
				              photo = photo.copy(Bitmap.Config.RGB_565, true);
				           }
						} catch (IOException e) {
							e.printStackTrace();
						}
						
//						String savePicPath = HomeApp.getMyApplication().getPicPath() + "local_user_avatar.png";
						
						String savePicPath= ImageTools.savePhotoToSDCard(photo, HomeApp.getMyApplication().getPicPath() ,"local_user_avatar_"+String.valueOf(System.currentTimeMillis())+".png", 150);
//						FileUtil.copyFile(path, savePicPath);
						
						avatarPath = savePicPath;
						
						return photo;
					}
				}
				return null;
			}
			
			@Override    
		     protected void onPostExecute(Bitmap result) {    
		           super.onPostExecute(result);
		           if(result != null)
		           avatar.setImageBitmap(result);
		     }	    	
	    }
	    /**
	     * 获取相机图片异步线程
	     * @author Felix
	     */
	    class getPicFromCameraTask extends AsyncTask<Void, String, Bitmap>
	    {

			@Override
			protected Bitmap doInBackground(Void...params) {
				//将保存在本地的图片取出并缩小后显示在界面上
				//由于Bitmap内存占用较大，这里需要回收内存，否则会报out of memory异常
				Bitmap bitmapFromCamera = ImageTools
				.decodeBitmapFromFileInSampleSize
				(Environment.getExternalStorageDirectory()+"/temp_image", Define.WIDTH_PX/2, Define.HEIGHT_PX/2);
				
				//调整照片的方向
				ExifInterface exif;
				try {
					exif = new ExifInterface(Environment.getExternalStorageDirectory()+"/temp_image");
				
		          int exifOrientation = exif.getAttributeInt(
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
		              int w = bitmapFromCamera.getWidth();
		              int h = bitmapFromCamera.getHeight();
		              Matrix mtx = new Matrix();
		              mtx.preRotate(rotate);

		             bitmapFromCamera = Bitmap.createBitmap(bitmapFromCamera, 0, 0, w, h, mtx, false);
		             bitmapFromCamera = bitmapFromCamera.copy(Bitmap.Config.RGB_565, true);
		           }
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				String pic_path = ImageTools.savePhotoToSDCard(bitmapFromCamera, HomeApp.getMyApplication().getPicPath() ,"local_user_avatar_"+String.valueOf(System.currentTimeMillis())+".png", 150);
				
				avatarPath =pic_path;

				return bitmapFromCamera;
			}
			 @Override    
		     protected void onPostExecute(Bitmap result) {    
		           super.onPostExecute(result);
		           
		           avatar.setImageBitmap(result);
		     } 
	    }  
      
}
