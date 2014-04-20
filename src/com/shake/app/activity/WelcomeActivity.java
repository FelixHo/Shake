package com.shake.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.shake.app.HomeApp;
import com.shake.app.R;
import com.shake.app.utils.MyActivityManager;
import com.shake.app.utils.MyWindowManager;

public class WelcomeActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyWindowManager.setFullScreenWithNoTitle(this);
		setContentView(R.layout.activity_welcome);
		MyActivityManager.getInstance().add(this);
		
		//2秒后离开启动画面。
		new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
			@Override
			public void run() {
				if (HomeApp.getMyApplication().isFirstLaunch()) 
				{
					Intent introIntent = new Intent(WelcomeActivity.this, SetinfoActivity.class);
					startActivity(introIntent);
					overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
				} 
				else
				{
					//测试用
//					Intent introIntent = new Intent(WelcomeActivity.this, SetinfoActivity.class);
//					startActivity(introIntent);
//					overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
					
					HomeApp.getMyApplication().markLaunched();//标记应用已经启动过
					
					Intent mainIntent = new Intent(WelcomeActivity.this, MainActivity.class);
					startActivity(mainIntent);
					overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

				}
				finish();
			}
		}, 2000);
	}


}
