package com.shake.app.utils;

import android.app.Activity;
import android.content.Context;
import android.view.Window;
import android.view.WindowManager;

public class MyWindowManager {

	public static void setFullScreenWithNoTitle(Context c)
	{
		((Activity)c).requestWindowFeature(Window.FEATURE_NO_TITLE);
        ((Activity)c).getWindow().setFlags
        (WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}
}
