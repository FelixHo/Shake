package com.shake.app;

import com.shake.app.utils.CommonUtils;


public class Define {
	
	 public static final float DENSITY = HomeApp.getMyApplication().getResources().getDisplayMetrics().density;
	 
	 public static final int WIDTH_PX = HomeApp.getMyApplication().getResources().getDisplayMetrics().widthPixels;
	
	 public static final int HEIGHT_PX = HomeApp.getMyApplication().getResources().getDisplayMetrics().heightPixels;
	
	 public static final boolean DEBUG = false;
	 
	/**
	 * 本程序 Shared Preferences 文件名。
	 */
	public static final String CONFINFO = "HomeAppSharedPreferences";
	
	/**
	 * 标记程序是否第一次启动。
	 */
	public static final String APP_IS_FIRST_LAUNCH_KEY = "APP_IS_FIRST_LAUNCH_KEY";
	
	/**
	 * 用户名片信息
	 */
	
	public static final String USER_INFO_NAME_KEY = "USER_INFO_NAME_KEY";
	
	public static final String USER_INFO_MOBILE_KEY = "USER_INFO_MOBILE_KEY";

	public static final String USER_INFO_EMAIL_KEY = "USER_INFO_EMAIL_KEY";

	public static final String USER_INFO_BIRTH_KEY = "USER_INFO_BIRTH_KEY";

	public static final String USER_INFO_PROFILE_KEY = "USER_INFO_PROFILE_KEY";
	
	public static final String USER_INFO_AVATAR_KEY = "USER_INFO_AVATAR_KEY";
	
	public static final String USER_INFO_HOMESITE_KEY = "USER_INFO_HOMESITE_KEY";
	

	/**
	 * 存储路径
	 */
	public static final String[] INTERNAL_STORAGE_PATHS = new String[]{"/mnt/", "/emmc/"};

	public static final String CACHE_PATH = "shake/app/cache/";
	
	public static final String PIC_PATH = "shake/app/pic/";
	
	public static final String MUSIC_PATH = "shake/app/music/";
	
	public static final String SERVER_URL = "tcp://192.168.145.1:5571";
	
	/**MAC地址**/
	public static final String MAC_ADDRESS = CommonUtils.getMACIgnoreWIFI(HomeApp.getMyApplication());
}
