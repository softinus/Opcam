package us.opcam.camera.view;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import us.opcam.camera.R;
import us.opcam.camera.activity.URLImagePagerActivity;
import us.opcam.camera.util.Constants;
import us.opcam.camera.util.SPUtil;
import us.opcam.camera.util.Constants.Extra;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.kakao.authorization.AuthorizationResult.RESULT_CODE;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class DiscoverGalleryFragment extends SherlockFragment
{
	//private GridView gridView;
	//private LocalGridViewAdapter customGridAdapter;
	protected AbsListView listView;
	private DiscoverGridViewAdapter customGridAdapter;
	
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	//ArrayList<ImageItem> arrImages= null;
	static ArrayList<String> arrImages= new ArrayList<String>();
	final static ArrayList<String> arrUploadDate= new ArrayList<String>();
	final static ArrayList<String> arrUploadName= new ArrayList<String>();
	
	DisplayImageOptions options;
	

	public DiscoverGalleryFragment()
	{
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{

		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{		
		return inflater.inflate(R.layout.fragment_discover_gallery, container, false);
	}
	
	@Override
	public void onStart()
	{
		//setHasOptionsMenu(true);
		Log.d("====Discover", "=========onStart1");
		arrImages= getData();	// ���� ���� �����͵��� ���ͼ�
		Log.d("====Discover", "=========onStart2");
		
		// �����Ͱ� �ƹ��͵� ������
		if(arrImages == null || arrImages.isEmpty()) 
		{
			super.onStart();
			toast("No pictures on opcam server");
			getActivity().finish();
			return;
		}
				
		// �̹��� �ɼ�.
		options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.img_round_progress)
		.showImageForEmptyUri(R.drawable.img_empty)
		.showImageOnFail(R.drawable.img_empty)
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.considerExifParams(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();
		
		listView = (GridView) getView().findViewById(R.id.frag_discover_gridview); // ����Ʈ�� ���̵�
		
		// �������� �� �̹��� ����Ʈ ���� ����.
		customGridAdapter= new DiscoverGridViewAdapter(this.getActivity().getApplicationContext(), imageLoader, arrImages, options, arrUploadName, arrUploadDate);
		((GridView) listView).setAdapter(customGridAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				//TODO : �� �ʿ��� �� �̹��� ������� Ŭ������ ���� �̺�Ʈ �Է�.
				startImagePagerActivity(position);
			}
		});
		Log.d("====Discover", "=========onStart3");

		super.onStart();
	}
	
	
	private void startImagePagerActivity(int position)
	{
		// discover pager�� �̹��� ����Ʈ�� �������� �Բ� �ѱ�.
		Intent intent = new Intent(getActivity().getApplicationContext(), URLImagePagerActivity.class);
		
		// convert ArrayList<String> to String[]
		String[] arrURLs= new String[arrImages.size()];
		arrURLs= arrImages.toArray(arrURLs);
		
		intent.putExtra(Extra.IMAGES, arrURLs);
		intent.putExtra(Extra.IMAGES_AUTHOR, arrUploadName);
		intent.putExtra(Extra.IMAGES_CREATE_DATE, arrUploadDate);
		intent.putExtra(Extra.IMAGE_POSITION, position);
		//startActivity(intent);
		startActivityForResult(intent, Constants.REQ.DISCOVER_TO_PICTURE);
	}
	
//	private void startImagePagerActivity(int position)
//	{
//		Intent intent = new Intent(this, ImagePagerActivity.class);
//		intent.putExtra(Extra.IMAGES, imageUrls);
//		intent.putExtra(Extra.IMAGE_POSITION, position);
//		startActivity(intent);
//	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == Constants.REQ.DISCOVER_TO_PICTURE)	// image���� �Ѿ������...
		{
			if(data == null)	// ������ ���� ������ �׳� �Ѿ.
				return;
			
			int forDeleteIdx= data.getIntExtra(Constants.Extra.DEL_POS_SERVER, -1);
			
			DeleteFromServer(forDeleteIdx);
			arrImages.remove(forDeleteIdx);
			customGridAdapter.notifyDataSetChanged();
		}
		
	}
	
	// server���� �ش��ϴ� ������ �����Ѵ�.
	protected void DeleteFromServer(int pos)
	{
		if(pos== -1)
			return;
				
		//TODO
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Pictures");	// Pictures ������ ���̺� �߿���
		query.orderByDescending("createdAt");
		query.setSkip(pos);
		query.getFirstInBackground(new GetCallback<ParseObject>()
		{
			@Override
			public void done(ParseObject arg0, ParseException arg1)
			{
				if(arg1==null)
				{
					String name= (String) arg0.get("Name");
					if(name.equals(SPUtil.getString(getActivity(), Constants.Extra.MY_NICK)))
						arg0.deleteInBackground();
				}
				
			}
		});
	}
	

	// Parse �����κ��� �������� url ��ε��� �����´�.
	protected ArrayList<String> getData()
	{
		if(Constants.bRefreshed)	// �̹� �������� �Ǿ�����
			return arrImages;
		
		arrUploadDate.clear();
		arrUploadName.clear();
		final ArrayList<String> arrPicList= new ArrayList<String>();
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Pictures");	// Pictures ������ ���̺� �߿���
		//query.whereEqualTo("Name", "Anonymous");	// �̸��� Anonymous�� �͸� �����´�.
		try
		{
			List<ParseObject> picList= query.find();
            for(ParseObject p : picList)
            {
            	ParseFile file= (ParseFile) p.get("Picture");	// Pictures ���̺� ���� Picture file.
            	Date date= p.getCreatedAt();		// �� ������ ������ ��¥
            	String name= p.getString("Name");	// �� ������ �ø� ���� �̸�.
            	if(file != null)
            	{
            		arrPicList.add(file.getUrl());
            		
            		SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
            		arrUploadDate.add(format.format(date));
            		arrUploadName.add(name);
            	}
            }
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		{
			Collections.reverse(arrPicList);	// �ֱ� ���� ���� ���� ����
			Collections.reverse(arrUploadDate);	
			Collections.reverse(arrUploadName);	
		}
		Constants.bRefreshed= true;
		return arrPicList;
	}
	
	
	public void toast(String msg)
    {
        Toast.makeText (getActivity().getApplicationContext(), msg, Toast.LENGTH_SHORT).show ();
    }
	
}