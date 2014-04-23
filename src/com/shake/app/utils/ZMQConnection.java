package com.shake.app.utils;

import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.PollItem;
import org.zeromq.ZMQ.Poller;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZMsg;

import zmq.Msg;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.StrictMode;
import android.text.format.DateUtils;
import android.util.Log;

public class ZMQConnection {

	private static ZMQConnection mConnection = null;
	
	private  ZContext ctx = null;
	
	private  Socket mSocket = null;
	
	private  String socketID = null;
	
	private  String connURL = null;
	
	private  static boolean doTask = true;
	
	private static boolean isRunning = false;
	
	private ZMQConnectionLisener listener = null;
	
	private static ZMQTask task = null;
	
	private static ZMQSendTask sender = null;
	
	public static long lastActTime = 0;
	
	private long timeout = 15000;
	
	private static boolean isSending = false;
	
	public static boolean hasReturn = false;
	
	public interface ZMQConnectionLisener
	{
		public void onMessage(ZMQConnection mConnection,Socket socket,ZMsg resvMsg);
		
		public void onSendTimeOut(ZMQConnection mConnection);
	}
	
	private ZMQConnection()
	{
		
	}
	
	public static ZMQConnection getInstance(String _url,String _socketID)
	{
		
		if(mConnection==null||mConnection.ctx.getSockets().size()==0)
		{
			
			if (android.os.Build.VERSION.SDK_INT > 9) 
			{
			    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			    StrictMode.setThreadPolicy(policy);
			}
			mConnection = new ZMQConnection();
		
			mConnection.ctx = new ZContext();
			
			mConnection.mSocket = mConnection.ctx.createSocket(ZMQ.DEALER);
			
			mConnection.socketID = _socketID+"-at-"+MyDateUtils.getCurrentDate(null);//动态更换socketid
			
			mConnection.connURL = _url;
			
			mConnection.mSocket.setIdentity(mConnection.socketID.getBytes());
			try
			{
				mConnection.mSocket.connect(mConnection.connURL);
				Log.d("ZMQTask","init ZMQConnection....socket id:"+mConnection.socketID);
			}
			catch (Exception e)
			{
				Log.e("ZMQTask","init ZMQConnection failed!!!");
				e.printStackTrace();
				doTask = false;
				isRunning = false;
				mConnection.ctx.close();
				mConnection.ctx.destroySocket(mConnection.mSocket);
				mConnection.ctx.destroy();
				mConnection=null;
				return null;
			}
		}
		return mConnection;
	}
	
	/**
	 * 打开连接
	 */
	public void open()
	{
		
		if(!isRunning)
		{
			if(mConnection==null)
			{
				throw new NullPointerException("ZMQConnection has not init");
			}
			else
			{
				task = mConnection.new ZMQTask();
				task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			}
//			task.execute();
		}
		else
		{
			Log.i("ZMQTask", "there is a task already start...");
		}
        
	}
	
	/**
	 * 关闭当前socket连接,结束守护线程轮询
	 */
	public void closeSocket()
	{
		if(isRunning)
		{
			doTask = false;
		}
		isRunning = false;
	}
	
	public static void closeZMQ()
	{
		if(mConnection!=null)
		{
			mConnection.ctx.close();
			mConnection.ctx.destroy();
			Log.d("ZMQTask","ZMQ already close");
		}
	}
	
	/**
	 * 发送数据
	 * @param data
	 * @param claseSocketAfterSend 发送后是否马上关闭socket
	 * @return
	 */
	public void send(String data,boolean claseSocketAfterSend )
	{
		
		sender =mConnection.new ZMQSendTask();
		String flag = claseSocketAfterSend?"1":"0";
		String[] params = {data,flag};
		sender.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,params);//解决只能运行一个AsyncTask的问题
	}
	
	public void setConnectionListener(ZMQConnectionLisener listener) {
		this.listener = listener;
	}
	
	/**
	 * 设置send操作的超时时间 默认15秒
	 * @param timeout
	 */
	public void setTimeout(long timeout) {
		mConnection.timeout = timeout;
	}
	/**
	 * ZMQ守护线程
	 * @author Felix
	 *
	 */
	class ZMQTask extends AsyncTask<Void, ZMsg, Void>
	{
		@Override
		protected Void doInBackground(Void... params) {
			Log.d("ZMQTask", "open...");
			doTask = true;
			isRunning = true;

			PollItem[] items = new PollItem[] { new PollItem(mConnection.mSocket, Poller.POLLIN) };
			lastActTime = System.currentTimeMillis();
			while(doTask)
			{
//				Log.d("ZMQTask","isSending:"+isSending);
//				Log.d("ZMQTask","timeDis:"+(System.currentTimeMillis()-lastActTime));
				if((isSending||!hasReturn)&&System.currentTimeMillis()-lastActTime>mConnection.timeout)
				{
					ZMsg resvMsg = null;
					publishProgress(resvMsg);
					isSending = false;
				}
				int rc =ZMQ.poll(items, 200);
				if(rc!=0)
				Log.d("ZMQTask-rc",rc+"");
				
				if (items[0].isReadable()) 
				{				
					hasReturn =true;
//					ZMsg resvMsg = ZMsg.recvMsg(mConnection.mSocket,ZMQ.DONTWAIT);
					ZMsg resvMsg = ZMsg.recvMsg(mConnection.mSocket);
					Log.d("ZMQTask", "receive");
					publishProgress(resvMsg);
					
				}
				
			}
//			mConnection.ctx.close();
//			mConnection.mSocket.setReceiveTimeOut(value)
//			Log.d("ZMQTask", "close...");
			mConnection.ctx.destroySocket(mConnection.mSocket);
			Log.d("ZMQTask", "destroy Socket...");
//			mConnection.ctx.destroy();
//			Log.d("ZMQTask","ctz destory");
			
			return null;
		}

		@Override
		protected void onProgressUpdate(ZMsg... values) {
			super.onProgressUpdate(values);
			if(mConnection.listener!=null&&values[0]!=null)
			{
				mConnection.listener.onMessage(mConnection,mConnection.mSocket,values[0]);
			}
			if(mConnection.listener!=null&&values[0]==null)
			{
				mConnection.listener.onSendTimeOut(mConnection);
			}
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if(task!=null)
			{
				task.cancel(true);
			}
		}
	}
	
	/**
	 * 发送操作线程
	 * @author Felix
	 *
	 */
	class ZMQSendTask extends AsyncTask<String, String, Void>
	{

		@Override
		protected Void doInBackground(final String... params) {
			mConnection.mSocket.setSendTimeOut(10000);
			lastActTime = System.currentTimeMillis();
			isSending = true;
			hasReturn = false;
			Log.d("ZMQTask",params[0]+"  is sending to the server");
			if(isRunning)
			{
				if(mConnection.mSocket.send(params[0]))
					{
						Log.d("ZMQTask","send success!!!....");
						isSending = false;
					}
				else
				{
						Log.e("ZMQTask","send failed~~may be time out~~~");
					isSending = false;
				}
				if(params[1].equals("1"))
				{
					mConnection.closeSocket();
				}
			}
			else
			{
				isSending = false;				
				publishProgress(params);
				
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(final String... params) {
			super.onProgressUpdate(params);
			Log.i("ZMQTask","is not running...now retry....0.5s later");
			open();
			new Handler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					if(params[1].equals("1"))
					{
						send(params[0],true);
					}
					else
					{
						send(params[0],false);
					}
				}
			}, 500);
		}
		
	}
}