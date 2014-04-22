package com.shake.app;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.shake.app.model.Card;
import com.shake.app.model.Contact;
import com.shake.app.model.Song;
import com.shake.app.model.User;
import com.shake.app.utils.FileUtil;
import com.shake.app.utils.LocationTools;

public class HomeApp extends Application {	
	
	private String currentDirectory;
	
	private String internalStoragePath;
	
	private String picPath;
	
	private String musicPath;
	
	private String cachePath; 
	
	private static HomeApp ApplicationInstance = null;
	
	private static User localuser = null;
	
	private static ArrayList<Contact> contactsList = null;//联系人数据表
	
	private static ArrayList<Song> songList = null;//歌曲列表
	
	private static ArrayList<Card> cardList = null;//名片录
	
	/**
	 * HashMap<文件夹名称、文件夹下所有图片的地址>
	 */
	private static HashMap<String,List<String>> imageGroupMap = null;//本地图片分组数据

	public static ArrayList<Contact> getContactsList() {
		
		return contactsList;
	}
	
	public static void setContactsList(ArrayList<Contact> contactsList) {
		
		HomeApp.contactsList = contactsList;
	}


	public static HashMap<String, List<String>> getImageGroupMap() {
		return imageGroupMap;
	}

	public static void setImageGroupMap(HashMap<String, List<String>> imageGroupMap) {
		HomeApp.imageGroupMap = imageGroupMap;
	}

	public static ArrayList<Song> getSongList() {
		return songList;
	}

	public static void setSongList(ArrayList<Song> songList) {
		HomeApp.songList = songList;
	}

	public static ArrayList<Card> getCardList() {
		return cardList;
	}

	public static void setCardList(ArrayList<Card> cardList) {
		HomeApp.cardList = cardList;
	}

	/**
	 * 检查程序是否第一次启动。
	 * @author Felix
	 * @return 是否第一次运行
	 */
	public boolean isFirstLaunch() {
		
		SharedPreferences sharedPreferences = getSharedPreferences(Define.CONFINFO,MODE_PRIVATE);
		return sharedPreferences.getBoolean(Define.APP_IS_FIRST_LAUNCH_KEY, true);
	}


	/**
	 * 标记程序已经启动过。
	 * @author Felix
	 */
	public void markLaunched() {
		
		SharedPreferences sharedPreferences = getSharedPreferences(	Define.CONFINFO,MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putBoolean(Define.APP_IS_FIRST_LAUNCH_KEY, false);
		editor.commit();
	}


	/**
	 * 获取当前应用程序的 Application 对象。
	 * @author Felix
	 * @return 全局Application单类对象
	 */
	public static HomeApp getMyApplication() {
		return ApplicationInstance;
	}	

	/**
	 * 获取本地用户
	 * @author Felix
	 */
	public static User getLocalUser(){
		return localuser;
	}
	
	/**
	 * 配置本地用户
	 * @author Felix
	 */
	public static void setLocalUser(User user)
	{
		localuser=user;
	}
	
	/**
	 * 取图片路径
	 * @return
	 */
	public String getPicPath() {
		return picPath;
	}
	
	/**
	 * 设图片路径
	 * @param picPath
	 */
	public void setPicPath(String picPath) {
		this.picPath = picPath;
	}
	public String getMusicPath() {
		return musicPath;
	}

	public void setMusicPath(String musicPath) {
		this.musicPath = musicPath;
	}

	/**
	 * 判断 SDCard 是否存在
	 */
	public boolean isSDCardMounted() {
		return Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState());
	}
	
	/**
	 * 内部存储路径初始化
	 */
	private void initInternalStoragePath() {
		if (!isSDCardMounted()) {		
			return;
		}
		for (String path : Define.INTERNAL_STORAGE_PATHS) {
			if (FileUtil.isFileCanReadAndWrite(path)) {
				internalStoragePath = path;
				return;
			} else {
				File f = new File(path);
				if (f.isDirectory()) {			
					for (File file : f.listFiles()) {
						if (file != null
								&& file.isDirectory()
								&& !file.isHidden()
								&& FileUtil.isFileCanReadAndWrite(file
										.getPath())) {
							internalStoragePath = file.getPath();
							if (!internalStoragePath.endsWith(File.separator)) {
								internalStoragePath += File.separator;
							}
							return;
						}
					}
				}
			}
		}
	}

	/**
	 * 存储路径
	 */
	public void initStoragePath() {
		initInternalStoragePath();
		this.currentDirectory = this.getFilesDir().getAbsolutePath().concat(File.separator);
		this.cachePath = this.currentDirectory + Define.CACHE_PATH;
		this.picPath = this.currentDirectory + Define.PIC_PATH;
		this.musicPath = this.currentDirectory+Define.MUSIC_PATH;

		String storagePath = null;
		if (isSDCardMounted()) {
			storagePath = Environment.getExternalStorageDirectory()
					.getAbsolutePath();
		} else if (null != internalStoragePath) {
			storagePath = internalStoragePath;
		}

		if (null != storagePath) {
			if(Define.DEBUG){
				this.cachePath = storagePath.concat(File.separator) + Define.CACHE_PATH;
			}
			this.picPath = storagePath.concat(File.separator) + Define.PIC_PATH;
			this.musicPath = storagePath.concat(File.separator)+Define.MUSIC_PATH;
		}

		File cacheDir = new File(this.cachePath);
		if (!cacheDir.exists()) {
			cacheDir.mkdirs();
		}
		File picDir = new File(this.picPath);
		if (!picDir.exists()) {
			picDir.mkdirs();
		}
		File musicDir = new File(this.musicPath);
		if(!musicDir.exists())
		{
			musicDir.mkdirs();
		}
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		ApplicationInstance = this;		
		initStoragePath();
		initImageLoader(getApplicationContext());	
	}	
	
	/**
	 * 初始化imageloader
	 * @param context
	 * @author Felix
	 */
	
	public static void initImageLoader(Context context) {
		DisplayImageOptions options = new DisplayImageOptions.Builder()        
        .cacheInMemory(true)         
        .cacheOnDisc(true)
        .bitmapConfig(Bitmap.Config.RGB_565)
        .showImageOnFail(R.drawable.ic_error)
        .showImageOnLoading(R.drawable.empty_picture_144)
        .showImageForEmptyUri(R.drawable.ic_error)
        .build();
		
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
		.tasksProcessingOrder(QueueProcessingType.LIFO)
		.defaultDisplayImageOptions(options)
		.threadPoolSize(3)	
		.build();
		Log.d("cache存储路径",StorageUtils.getCacheDirectory(getMyApplication()).getAbsolutePath());
		ImageLoader.getInstance().init(config);		
		Log.i("initImageLoader", "初始化uil");
	}
	
	/**
	 * 返回一个没有cache的配置
	 * @return
	 */
	public static DisplayImageOptions getDisplayOptionsWithNoCache()
	{
		DisplayImageOptions options = new DisplayImageOptions.Builder()        
        .cacheInMemory(false)         
        .cacheOnDisc(false)
        .bitmapConfig(Bitmap.Config.RGB_565)
        .showImageOnFail(R.drawable.ic_error)
        .showImageOnLoading(R.drawable.empty_picture_144)
        .showImageForEmptyUri(R.drawable.ic_error)
        .build();
		return options;
	}
}
