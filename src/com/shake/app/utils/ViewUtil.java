package com.shake.app.utils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import com.shake.app.Define;

public final class ViewUtil {

	/**
	 * 从 dp 转换成 px 。
	 * @param dp
	 * @param context
	 * @return
	 */

	public static int convertDpToPixel(int dp, Context context) {
	
		float px = dp  * Define.DENSITY;
		return Float.valueOf(px).intValue();
	}
	
	/**
	 * 从 dp 转换成 px 。
	 * @param dp
	 * @return
	 */
	public static int dp(int dp) 
	{

		float px = dp * Define.DENSITY;
		return Float.valueOf(px).intValue();
	}

	/**
	 * 从 px 转换成 dp 。
	 * @param px
	 * @param context
	 * @return
	 */
	public static int convertPixelsToDp(int px, Context context) {
	
		float dp = px /  Define.DENSITY;
		return Float.valueOf(dp).intValue();
	}

	/**
	 * 把 RelativeLayout 内的 View 调整位置。
	 * @param view
	 * @param xPosition
	 * @param yPosition
	 */
	public static void resetViewToPosition(View view, int xPosition, int yPosition) {
		view.clearAnimation();
		ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams)view.getLayoutParams();
		params.setMargins(xPosition, yPosition, -xPosition, -yPosition);
		view.setLayoutParams(params);
	}

	/**
	 * 获得 RelativeLayout 内的 View 左上角的 X 坐标。
	 * @param view
	 * @return
	 */
	public static int getViewXPosition(View view) {
		ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
		return params.leftMargin;
	}

	/**
	 * 获得 RelativeLayout 内的 View 左上角的 Y 坐标。
	 * @param view
	 * @return
	 */
	public static int getViewYPosition(View view) {
		ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
		return params.topMargin;
	}

	/**
	 * 通过动画移动 RelativeLayout 内的 View 到新的位置。
	 * @param view
	 * @param xPosition
	 * @param yPosition
	 * @param duration 动画的时间（毫秒）。
	 */
	public static void animateViewToPosition(final View view, final int xPosition, final int yPosition, int duration) {
		view.clearAnimation();
		final int currentX = getViewXPosition(view);
		final int currentY = getViewYPosition(view);
		TranslateAnimation translateAnimation = new TranslateAnimation(0, xPosition - currentX, 0, yPosition - currentY);
		translateAnimation.setInterpolator(new DecelerateInterpolator(1.0f));

		translateAnimation.setAnimationListener(new Animation.AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				view.clearAnimation();
				resetViewToPosition(view, xPosition, yPosition);
			}
		});
		translateAnimation.setDuration(duration);
		translateAnimation.reset();
		translateAnimation.setFillAfter(true);

		view.startAnimation(translateAnimation);
	}

}
