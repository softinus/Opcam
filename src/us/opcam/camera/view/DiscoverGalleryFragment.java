package us.opcam.camera.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import us.opcam.camera.R;
import us.opcam.camera.util.ImageItem;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class DiscoverGalleryFragment extends Fragment
{
	private GridView gridView;
	private GridViewAdapter customGridAdapter;
	
	ArrayList<ImageItem> arrImages= null;

	public DiscoverGalleryFragment()
	{
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{		
		return inflater.inflate(R.layout.fragment_local_gallery, container, false);
	}
	
	@Override
	public void onStart()
	{
		//setContentView(R.layout.activity_gallery);
		
		arrImages= getData();	// 찍은 사진 데이터들을 얻어와서
		
		// 데이터가 아무것도 없으면
		if(arrImages == null || arrImages.isEmpty()) 
		{
			toast("No pictures on opcam folder");
			getActivity().finish();
			return;
		}
		
		
//		Collections.reverse(arrImages);	// 최근 찍은 것이 위로 가게
//		
//		gridView = (GridView) getView().findViewById(R.id.grid_view);
//		customGridAdapter = new GridViewAdapter(getActivity(), R.layout.row_grid, arrImages);
//		gridView.setAdapter(customGridAdapter);

		
		
//		gridView.setOnItemClickListener(new OnItemClickListener()
//		{
//			public void onItemClick(AdapterView<?> parent, View v, int position, long id)
//			{
//				//String currUri= arrImages.get(position).getUri4Share();
//				
//                //Intent intent = new Intent(getApplicationContext(), ImageZoomActivity.class);
//				//intent.putExtra("uri", currUri);
//				Intent intent = new Intent(getActivity().getApplicationContext(), ImageZoomPagerActivity.class);
//                intent.putParcelableArrayListExtra("uri_list", getParcelData());
//                intent.putExtra("selected_position", position);
//                startActivityForResult(intent, 0);
//			}
//
//		});
		
		super.onStart();		
	}
	
	protected ArrayList<ImageItem> getData()
	{
		final ArrayList<ImageItem> arrPicList= new ArrayList<ImageItem>();
		
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
            		ImageItem item= new ImageItem(file.getUrl());
            		arrPicList.add(item);		             	
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
	
//	// 이미지 리스트 중 Uri만을 추려낸다.
//	private ArrayList<Uri> getParcelData()
//	{
//		final ArrayList<Uri> arrRes= new ArrayList<Uri>();
//		
//		for(ImageItem item : arrImages)
//			arrRes.add( Uri.parse( item.getUri4Share() ) );
//		
//		return arrRes;
//	}
}
