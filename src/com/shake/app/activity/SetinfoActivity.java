package com.shake.app.activity;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.widget.DatePicker;
import android.widget.EditText;

import com.shake.app.R;
import com.shake.app.utils.MyActivityManager;
import com.shake.app.utils.MyWindowManager;

public class SetinfoActivity extends Activity {

	private int mYear = 0;
	private int mMonth = 0;
	private int mDay = 0;
	private EditText birth;
	public DatePickerDialog datePicker;
	public Calendar mCalendar ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyWindowManager.setFullScreenWithNoTitle(this);
		setContentView(R.layout.activity_setinfo);
		MyActivityManager.getInstance().add(this);
		initDate();
		setupView();
		initView();
	}
	
	private void setupView()
	{
		birth = (EditText)findViewById(R.id.welcome_edittext_birth);
	}
	
	private void initView()
	{
		birth.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				if(event.getAction() ==MotionEvent.ACTION_DOWN)
				{
					datePicker = new DatePickerDialog(SetinfoActivity.this, new OnDateSetListener() 
					{
						
						@Override
						public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth) 
						{
							mYear = year;
							mMonth = monthOfYear;
							mDay = dayOfMonth;
							birth.setText(year+"年"+(monthOfYear+1)+"月"+dayOfMonth+"日");						
						}
					}, mYear, mMonth, mDay);
					datePicker.show();	
				}
				
				return false;
			}
		});
		
	}
	
	private void initDate()
	{
		mCalendar = Calendar.getInstance(Locale.CHINA);
		mCalendar.setTime(new Date());
		mYear = mCalendar.get(Calendar.YEAR);
		mMonth = mCalendar.get(Calendar.MONTH);
		mDay = mCalendar.get(Calendar.DAY_OF_MONTH);
	}

}
