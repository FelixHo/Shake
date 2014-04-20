package com.shake.app.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.shake.app.Define;
import com.shake.app.R;
import com.shake.app.utils.ImageTools;
import com.shake.app.view.ZoomAbleImageView;

public class CheckPhotoActivity extends Activity {

	private ZoomAbleImageView img;
	
	public final static String IMAGE_PATH = "IMAGE_PATH";
	
	private String path;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_check_photo);
		path = getIntent().getStringExtra(IMAGE_PATH);
		setupView();
		initView();
	}
	
	private void setupView()
	{
		img = (ZoomAbleImageView)findViewById(R.id.zoomAbleImageView);
	}
	
	private void initView()
	{
		Bitmap bp = ImageTools.loadBitmap(this, path, Define.WIDTH_PX);		
		Bitmap photo = ImageTools.getCorrectOrientationBitmap(path, bp);
		bp=null;
		img.setImageBitmap(photo);
	}
	
	@Override
	public void onBackPressed()
	{
		finish();
		overridePendingTransition(0,android.R.anim.fade_out);
	}
	

}
