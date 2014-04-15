package com.shake.app.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import com.shake.app.R;
import com.shake.app.fragment.CardFragment;
import com.shake.app.fragment.MenuFragment;
import com.shake.app.libs.slidingmenu.SlidingFragmentActivity;
import com.shake.app.libs.slidingmenu.SlidingMenu;
import com.shake.app.utils.MyActivityManager;
import com.shake.app.utils.MyToast;

public class MainActivity extends SlidingFragmentActivity {

	private Fragment mContent;
	
	public SlidingMenu sm;
	
	private long exitTime = 0;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		MyActivityManager.getInstance().add(this);
		if (savedInstanceState != null) 
		{
			mContent = getSupportFragmentManager().getFragment(	savedInstanceState, "mContent");
		}
		if(mContent==null)
		{
			mContent = new CardFragment();			
		}
		getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.main_content_frame, mContent)
		.commit();
		initSlidingMenu();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		getSupportFragmentManager().putFragment(outState, "mContent", mContent);
	}
	
	/**
	 * 侧拉菜单自动切换状态
	 * 
	 * @author Felix
	 */
	public void toggleMenu() {
		if (sm != null)
			sm.toggle();
	}

	/**
	 * 根据返回键切换slidingmenu
	 * 
	 * @author Felix
	 */
	@Override
	public void onBackPressed() {
		if((System.currentTimeMillis()-exitTime) > 2000){  
            exitTime = System.currentTimeMillis();   
            MyToast.alert("再按一次退出程序");
        } else {
            MyActivityManager.getInstance().exit();
        }
		return;
	}

	/**
	 * 初始化侧拉菜单
	 * 
	 * @author Felix
	 */
	private void initSlidingMenu() {
		setBehindContentView(R.layout.sliding_menu);
		getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.menu_frame, new MenuFragment())
		.commit();
		sm = getSlidingMenu();
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		sm.setShadowWidthRes(R.dimen.shadow_width);
		sm.setShadowDrawable(R.drawable.sliding_menu_shadow);
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		sm.setBehindScrollScale(0.35f);
		sm.setFadeDegree(0.5f);
	}

	/**
	 * 切换页面内容
	 * 
	 * @param fragment
	 *            要切换的页面fragment
	 * @author Felix
	 */
	public void switchContent(final Fragment fragment) {
		mContent = fragment;
		getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.main_content_frame, fragment)
		.commit();
		Handler h = new Handler();
		h.postDelayed(new Runnable() {
			public void run() {
				getSlidingMenu().showContent();
			}
		}, 50);
	}
}
