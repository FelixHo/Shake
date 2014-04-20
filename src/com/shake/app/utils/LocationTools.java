package com.shake.app.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.shake.app.HomeApp;

public class LocationTools {

	public interface MyLocationListener
	{
		/**
		 * 返回获取的地理坐标数组,依次为纬度、经度
		 * @param location
		 */
		public void onReceive(String[] location);
	}
	
	private static LocationClient mLocationClient = new LocationClient(HomeApp.getMyApplication().getApplicationContext());;
	
	private static BDLocationListener bdListener = null;
	/**
	 * 执行获取地理坐标线程
	 */
	public static void toGetLocation(final MyLocationListener listener)
	{
		mLocationClient.setAK("vsBt2GPR3dUyZ3fTYvXOatAQ");	
		mLocationClient.unRegisterLocationListener(bdListener);
		mLocationClient.registerLocationListener(bdListener = new BDLocationListener() {
			
			@Override
			public void onReceivePoi(BDLocation poiLocation) {}
			
			@Override
			public void onReceiveLocation(BDLocation location) {
				Log.d("定位返回结果:","返回码:"+location.getLocType()+" 卫星数:"+location.getSatelliteNumber());
				//lat 纬度    lon经度
//				MyToast.alert("返回码:"+location.getLocType()+" 卫星数:"+location.getSatelliteNumber());
				String[] result = {Double.toString(location.getLatitude()),Double.toString(location.getLongitude())};
				if(listener!=null)
				{
					listener.onReceive(result);
				}
			}
		});
		setOption();
		if(!mLocationClient.isStarted())
		{
			mLocationClient.start();
		}
		else
		{
			mLocationClient.requestLocation();
		}
	}
	
	/**
	 * 配置定位项
	 */
	public static void setOption()
	{
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);
		option.setCoorType("bd09ll");//返回的定位结果是百度经纬度，默认值gcj02
		option.setServiceName("com.baidu.location.service_v2.9");
		option.setProdName("Shake");
		option.setScanSpan(900);
		option.setPriority(LocationClientOption.GpsFirst);
		option.disableCache(true);
		mLocationClient.setLocOption(option);
	}
	
	/**
	 * 关闭定位
	 */
	public static void stop()
	{
		Log.i("百度定位:", "stop");
		if(mLocationClient!=null&&mLocationClient.isStarted())
		{
			mLocationClient.stop();
		}
	}
	
	/**
	 * 检查是否打开了GPS
	 * @param c
	 */
	public static void checkGPSOption(Context c)
	{
		final LocationManager manager = (LocationManager)HomeApp.getMyApplication().getApplicationContext().getSystemService( Context.LOCATION_SERVICE );

	    if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
	        showOpenGPS(c);
	    }
	}
	
	/**
	 * 弹出打开GPS建议
	 * @param c
	 */
	private static void showOpenGPS(final Context c) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(c);
		builder.setMessage("为了定位更精准,建议打开GPS,是否现在打开?")
				.setCancelable(false)
				.setPositiveButton("是", new DialogInterface.OnClickListener() 
				{
					public void onClick(DialogInterface dialog, int id)
					{
						
						c.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
					}
				}).setNegativeButton("否", new DialogInterface.OnClickListener() 
				{
					public void onClick(DialogInterface dialog, int id) 
					{
						dialog.cancel();
					}
				});
		final AlertDialog alert = builder.create();
		alert.show();
	}

}
