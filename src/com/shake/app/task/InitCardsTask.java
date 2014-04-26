package com.shake.app.task;

import java.util.ArrayList;

import android.content.Context;
import android.os.AsyncTask;

import com.shake.app.HomeApp;
import com.shake.app.db.CardDBManager;
import com.shake.app.model.Card;

/**
 * 异步线程读取数据库的名片录
 * @author Felix
 */
public class InitCardsTask extends AsyncTask<Context, Void, ArrayList<Card>> {

	private OnTaskListener listener;
	
	private CardDBManager cardDB;
	
	private ArrayList<Card> mCards;
	
	private Context mContext;
	
	public interface OnTaskListener
	{
		public void onTaskFinished(ArrayList<Card> cards);
	}
	public void setOnTaskListener(OnTaskListener listener) {
		this.listener = listener;
	}
	
	@Override
	protected ArrayList<Card> doInBackground(Context... params) {
		
		mContext = params[0];
		if(HomeApp.getCardList()!=null)
		{
			return HomeApp.getCardList();
		}
		
		cardDB = new CardDBManager(mContext);
		
		mCards = cardDB.queryAll();
		
		return mCards;
	}

	@Override
	protected void onPostExecute(ArrayList<Card> result) {
		super.onPostExecute(result);
		this.listener.onTaskFinished(result);
		
		cardDB.closeDB();
	}
}
