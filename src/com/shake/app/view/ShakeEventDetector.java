package com.shake.app.view;

import com.shake.app.HomeApp;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
/**
 * Shake动作检测监听器
 */
public class ShakeEventDetector implements SensorEventListener {


  private static SensorManager mSensorManager;
  
  private static ShakeEventDetector mShakeDector = new ShakeEventDetector();
  
  /**x y z三个方向的加速度delta值之和的最小值用于判断手机是否处于静止平放**/
  private static final int MIN_FORCE = 10;
  
  /**至少三次方向变化才算一次手势碰撞**/
  private static final int MIN_DIRECTION_CHANGE = 3;

  /**三次方向变化的间隔时间的最大值,识别是否人为碰撞**/
  private static final int MAX_PAUSE_BETHWEEN_DIRECTION_CHANGE = 200;

  /**达到识别要求的最大总时间，用于过滤普通动作与碰撞动作**/
  private static final int MAX_TOTAL_DURATION_OF_SHAKE = 150;//400

  /**第一次触发的时间,即第一次动的时间**/
  private long mFirstDirectionChangeTime = 0;

  /**最后一次触发的时间**/
  private long mLastDirectionChangeTime;

  /**记录方向改变的次数**/
  private int mDirectionChangeCount = 0;

  /** 最近一次X轴的位置 水平左->右**/
  private float lastX = 0;

  /**最近一次Y轴的位置  水平下->上**/
  private float lastY = 0;

  /**最近一次Z轴的位置  垂直 下->上**/
  private float lastZ = 0;

  private static OnShakeListener mShakeListener;

  /**记录上次识别的时间,用于过滤频繁的请求**/
  private long lastActTime = 0;
  
  public interface OnShakeListener {
	  
    void onShake();
  }

  @Override
  public void onSensorChanged(SensorEvent se) {
	  
    float x = se.values[0];
    float y = se.values[1];
    float z = se.values[2];

    float totalMovement = Math.abs(x + y + z - lastX - lastY - lastZ);

    if (totalMovement > MIN_FORCE) {

    	
      long now = System.currentTimeMillis();

      if (mFirstDirectionChangeTime == 0)//记录首次触发时间 
      {
        mFirstDirectionChangeTime = now;
        mLastDirectionChangeTime = now;
      }

      long lastChangeWasAgo = now - mLastDirectionChangeTime;
      if (lastChangeWasAgo < MAX_PAUSE_BETHWEEN_DIRECTION_CHANGE) {

        mLastDirectionChangeTime = now;
        mDirectionChangeCount++;

        //记录最新的传感值
        lastX = x;
        lastY = y;
        lastZ = z;

        if (mDirectionChangeCount >= MIN_DIRECTION_CHANGE) {

          long totalDuration = now - mFirstDirectionChangeTime;
          
          if (totalDuration < MAX_TOTAL_DURATION_OF_SHAKE) {
        	  if(now - lastActTime >2000)//两次请求不低于2秒间隔
        	  {
        		  lastActTime = now;
        		  if(mShakeListener!=null)
        		  {
        			  mShakeListener.onShake();
        			  Log.d("传感器:", "Sucess ! totalMovement="+totalMovement+"   totalDuration="+totalDuration);
        		  }
        		  else
        		  {
        			  Log.d("传感器:", "mShakeListener is NULL!!!!");
        		  }
        		 
        	  }else
        	  {
        		  Log.d("传感器:", "两次时间间隔太短!!");
        		  resetShakeParameters();
        	  }
          }
        }

      } else {
        resetShakeParameters();
      }
    }
  }
  
  /**
   * 重置所有参数
   */
  private void resetShakeParameters() {
    mFirstDirectionChangeTime = 0;
    mDirectionChangeCount = 0;
    mLastDirectionChangeTime = 0;
    lastX = 0;
    lastY = 0;
    lastZ = 0;
  }

  public static void start(OnShakeListener listener)
  {
	  mShakeListener = listener;
	  mSensorManager = (SensorManager) HomeApp.getMyApplication().getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
	  mSensorManager.registerListener(mShakeDector, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_UI);
	  Log.d("传感器:", "ShakeDector is working.");
  }
  public static void stop()
  {
	  if(mSensorManager!=null)
	  {
		  mSensorManager.unregisterListener(mShakeDector);
		  Log.d("传感器:", "ShakeDector has unregistered.");
	  }
	  else
	  {
		  Log.d("传感器:","SensorManager is null!!!");
	  }
  }
  
  @Override
  public void onAccuracyChanged(Sensor sensor, int accuracy) {
  }

}