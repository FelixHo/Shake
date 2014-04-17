package com.shake.app.activity;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.widget.GridView;
import com.shake.app.R;
import com.shake.app.adapter.ChildAdapter;

public class ShowPhotosActivity extends Activity {

	/**
	 * Intent传递参数KEY
	 */
	public final static String SHOW_PHOTO_DATA_KEY = "SHOW_PHOTO_DATA_KEY";
	
	private List<String> photoPaths;
	
	private GridView grid;
	
	private ChildAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_photo);
		photoPaths = getIntent().getStringArrayListExtra(SHOW_PHOTO_DATA_KEY);		
		setupView();		
		initView();
		
	}
	
	private void setupView()
	{
		grid = (GridView)findViewById(R.id.show_photo_child_gridview);
	}
	
	private void initView()
	{
		adapter = new ChildAdapter(this, photoPaths);
		grid.setAdapter(adapter);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
		overridePendingTransition(0,R.anim.scale_out);
	}	

}
