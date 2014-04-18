package com.shake.app.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;

import com.shake.app.HomeApp;
import com.shake.app.R;
import com.shake.app.activity.MainActivity;
import com.shake.app.activity.ShowPhotosActivity;
import com.shake.app.adapter.GroupAdapter;
import com.shake.app.model.ImageBean;
import com.shake.app.task.InitImageGroupTask;
import com.shake.app.task.InitImageGroupTask.OnTaskListener;
import com.shake.app.utils.MyToast;

public class ImageFragment extends Fragment {
	

	private InitImageGroupTask task;
	
	private GridView groupGrid;
	
	private View layout; 
	
	private ProgressDialog dialog;
	
	private HashMap<String,List<String>> mGroupMap;
	
	private List<ImageBean> groupList ;
	
	private GroupAdapter adapter;
	
	private Button menuBtn;
	
	public ImageFragment() {
		
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		
		layout = inflater.inflate(R.layout.fragment_image, null);
		
		return layout;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		setupView();
		initView();
		
		task = new InitImageGroupTask();
		
		task.setOnTaskListener(new OnTaskListener() {
			
			@Override
			public void onTaskStart() {
				if(HomeApp.getImageGroupMap()==null)
				{
					dialog = ProgressDialog.show(getActivity(),null,"正在读取相册...", true,false);
					dialog.show();
				}
				
			}
			
			@Override
			public void onTaskFinished(HashMap<String, List<String>> group) {
				
				if(group!=null)
				{
					mGroupMap = group;
					groupList = getGroupData(mGroupMap);
					adapter.refresh(groupList);	
				}
				
				if(dialog!=null&&dialog.isShowing())
				{
					dialog.dismiss();
				}
			}
			
			@Override
			public void onTaskFail(String reason) {
				MyToast.alert(reason);
			}
		});
		task.execute(getActivity());
	}

	private void setupView()
	{
		groupGrid = (GridView)layout.findViewById(R.id.image_frag_gridview);
		menuBtn = (Button)layout.findViewById(R.id.image_frag_menu_button);
	}
	
	private void initView()
	{
		adapter = new GroupAdapter(getActivity(), groupList);
		groupGrid.setAdapter(adapter);
		groupGrid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) 
			{
				List<String> photoPaths = mGroupMap.get(groupList.get(position).getFolderName());
				Intent intent = new Intent(getActivity(), ShowPhotosActivity.class);
				intent.putStringArrayListExtra(ShowPhotosActivity.SHOW_PHOTO_DATA_KEY, (ArrayList<String>)photoPaths);
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.scale_in, android.R.anim.fade_out);
			}
		});
		
		menuBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				((MainActivity)getActivity()).toggleMenu();
			}
		});
	}
	
	/**
	 * 获得GroupView的数据源，以文件夹分类的图片册
	 * @param mGroupMap 通过InitImageGroupTask获得的GroupMap
	 * @return 数据源List
	 */
	private List<ImageBean> getGroupData(HashMap<String, List<String>> mGroupMap){
		
		if(mGroupMap==null||mGroupMap.size() == 0){
			
			return null;
		}
		
		List<ImageBean> list = new ArrayList<ImageBean>();
		
		Iterator<Map.Entry<String, List<String>>> it = mGroupMap.entrySet().iterator();
		
		while (it.hasNext()) {
			
			Map.Entry<String, List<String>> entry = it.next();
			
			ImageBean mImageBean = new ImageBean();
			
			String key = entry.getKey();
			
			List<String> value = entry.getValue();
			
			mImageBean.setFolderName(key);
			
			mImageBean.setImageCounts(value.size());
			
			mImageBean.setTopImagePath(value.get(0));//以第一张图为封面
			
			list.add(mImageBean);
		}
		
		return list;		
	}
}
