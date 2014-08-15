package us.opcam.camera.view;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import com.actionbarsherlock.app.SherlockFragment;

import us.opcam.camera.R;
import us.opcam.camera.activity.CameraPreview2;
import us.opcam.camera.activity.LocalImageZoomPagerActivity;
import us.opcam.camera.util.ImageItem;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

public class LocalGalleryFragment extends SherlockFragment
{
	
	private GridView gridView;
	private LocalGridViewAdapter customGridAdapter;
	
	ArrayList<ImageItem> arrImages= null;

	public LocalGalleryFragment()
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
		Log.d("LocalGallery", "=========onStart1");
		//setContentView(R.layout.activity_gallery);
		
		arrImages= getData();	// 찍은 사진 데이터들을 얻어와서
		
		// 데이터가 아무것도 없으면
		if(arrImages == null || arrImages.isEmpty()) 
		{
			super.onStart();
			
			toast("No pictures on opcam folder");
			getActivity().finish();
			return;
		}
		
		
		Collections.reverse(arrImages);	// 최근 찍은 것이 위로 가게
		
		gridView = (GridView) getView().findViewById(R.id.grid_view);
		customGridAdapter = new LocalGridViewAdapter(getActivity(), R.layout.item_local_grid, arrImages);
		gridView.setAdapter(customGridAdapter);

		gridView.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> parent, View v, int position, long id)
			{
				//String currUri= arrImages.get(position).getUri4Share();
				
                //Intent intent = new Intent(getApplicationContext(), ImageZoomActivity.class);
				//intent.putExtra("uri", currUri);
				Intent intent = new Intent(getActivity().getApplicationContext(), LocalImageZoomPagerActivity.class);
                intent.putParcelableArrayListExtra("uri_list", getParcelData());
                intent.putExtra("selected_position", position);
                startActivityForResult(intent, 0);
			}

		});
		Log.d("LocalGallery", "=========onStart2");
		
		super.onStart();		
	}

	@Override
	public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item)
	{

		switch (item.getItemId())
		{
		case R.id.actionbar_camera:
			GotoCamera();
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}

	
	// 이미지 리스트 중 Uri만을 추려낸다.
	private ArrayList<Uri> getParcelData()
	{
		final ArrayList<Uri> arrRes= new ArrayList<Uri>();
		
		for(ImageItem item : arrImages)
			arrRes.add( Uri.parse( item.getUri4Share() ) );
		
		return arrRes;
	}
	
	

	// 해당 폴더 안에 있는 각 이미지를 가져온다.
	private ArrayList<ImageItem> getData()
	{
		final ArrayList<ImageItem> imageItems = new ArrayList<ImageItem>();
				
		File dir=new File(Environment.getExternalStorageDirectory(), "/opcam");
		
		if(!dir.exists())
		{
			return null;
		}
		
		File[] fileLists= dir.listFiles(new FilenameFilter()
		{
		    public boolean accept(File directory, String fileName)
		    {
		        return fileName.endsWith(".jpg");
		    }
		});
		
		// 파일 수정 날짜별로 소팅
		Arrays.sort(fileLists, new Comparator<File>()
		{
		    public int compare(File f1, File f2)
		    {
		        return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
		    }
	    });
		
		ArrayList<String> arrFileNames= new ArrayList<String>();
		for(File file : fileLists)
		{
			int pos= file.toString().lastIndexOf("/");
			arrFileNames.add( file.toString().substring(pos+1,file.toString().length()) );
		}
		
		
		
		for(String name : arrFileNames)
		{
			File file=new File(Environment.getExternalStorageDirectory(),"/opcam/"+name);
			
			Uri strURI2= Uri.fromFile(file);
			
			String strPath1= "/sdcard/opcam/" + name;	// 출력용 경로
			String strPath2= strURI2.toString();	// 공유용 경로
			
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 8;
			Bitmap bmp = BitmapFactory.decodeFile(strPath1, options);
			
			if(bmp == null)
				toast("abnormal path. [" + strPath1 +"]");
			else
				imageItems.add(new ImageItem(bmp, name, strPath1, strPath2));
		}
		return imageItems;	
	}
	

	
	


	
	private void GotoCamera()
	{
		Intent cameraIntent = new Intent( getActivity(), CameraPreview2.class );
		startActivity(cameraIntent);
	}

	
	public void toast(String msg)
    {
        Toast.makeText (getActivity().getApplicationContext(), msg, Toast.LENGTH_SHORT).show ();
    }
}
