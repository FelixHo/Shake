package com.shake.app.utils;
import android.widget.Toast;
import com.shake.app.HomeApp;
/**
 * quick toast
 * @author Felix
 *
 */
public class MyToast {	
	
	/**
	 * 提示语
	 * @param message 内容
	 * @author Felix
	 */
	public static void alert(String message)
	{
		Toast toast = Toast.makeText(HomeApp.getMyApplication().getApplicationContext(), message, Toast.LENGTH_SHORT);	    
	    toast.show();
	 }

}
