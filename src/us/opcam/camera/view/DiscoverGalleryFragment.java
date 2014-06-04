package us.opcam.camera.view;

import java.util.ArrayList;
import java.util.List;

import us.opcam.camera.R;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class DiscoverGalleryFragment extends Fragment
{
	//private GridView gridView;
	//private LocalGridViewAdapter customGridAdapter;
	protected AbsListView listView;
	private DiscoverGridViewAdapter customGridAdapter;
	
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	//ArrayList<ImageItem> arrImages= null;
	ArrayList<String> arrImages= null;
	
	DisplayImageOptions options;
	

	public DiscoverGalleryFragment()
	{
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{		
		return inflater.inflate(R.layout.fragment_discover_gallery, container, false);
	}
	
	@Override
	public void onStart()
	{
		arrImages= getData();	// 찍은 사진 데이터들을 얻어와서
		
		// 데이터가 아무것도 없으면
		if(arrImages == null || arrImages.isEmpty()) 
		{
			super.onStart();
			toast("No pictures on opcam folder");
			getActivity().finish();
			return;
		}
		
		
		options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.ic_stub)
		.showImageForEmptyUri(R.drawable.ic_empty)
		.showImageOnFail(R.drawable.ic_error)
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.considerExifParams(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();
		
		listView = (GridView) getView().findViewById(R.id.gridview);
		customGridAdapter= new DiscoverGridViewAdapter(this.getActivity().getApplicationContext(), imageLoader, arrImages, options);
		((GridView) listView).setAdapter(customGridAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{			
				//startImagePagerActivity(position);
			}
		});
		
		

		super.onStart();
		
	}
	
//	private void startImagePagerActivity(int position)
//	{
//		Intent intent = new Intent(this, ImagePagerActivity.class);
//		intent.putExtra(Extra.IMAGES, imageUrls);
//		intent.putExtra(Extra.IMAGE_POSITION, position);
//		startActivity(intent);
//	}
	
	protected ArrayList<String> getData()
	{
		final ArrayList<String> arrPicList= new ArrayList<String>();
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Pictures");
		query.whereEqualTo("Name", "Anonymous");
		try
		{
			List<ParseObject> picList= query.find();
            for(ParseObject p : picList)
            {
            	ParseFile file= (ParseFile) p.get("Picture");
            	if(file != null)
            	{
            		//ImageItem item= new ImageItem(file.getUrl());
            		arrPicList.add(file.getUrl());		             	
            	}
            }
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		query.findInBackground(new FindCallback<ParseObject>()
//		{
//		    public void done(List<ParseObject> picList, ParseException e)
//		    {
//		        if (e == null)
//		        {
//		            Log.d("pictures", "Retrieved " + picList.size() + " pictures");
//		            
//		            for(ParseObject p : picList)
//		            {
//		            	ParseFile file= (ParseFile) p.get("Picture");
//		            	ImageItem item= new ImageItem( file.getUrl() );
//		            	arrPicList.add(item);		            	
//		            }
//		            
//		        } else {
//		            Log.d("pictures", "Error: " + e.getMessage());
//		        }
//		    }
//		});
		return arrPicList;
	}
	
	
	public void toast(String msg)
    {
        Toast.makeText (getActivity().getApplicationContext(), msg, Toast.LENGTH_SHORT).show ();
    }
	
}
