package us.opcam.camera.activity;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import us.opcam.camera.R;
import us.opcam.camera.util.ImageItem;
import us.opcam.camera.view.GridViewAdapter;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

public class GalleryActivity extends Activity 
{
	private GridView gridView;
	private GridViewAdapter customGridAdapter;
	
	ArrayList<ImageItem> arrImages= null;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gallery);
		
		arrImages= getData();	// 찍은 사진 데이터들을 얻어와서
		
		// 데이터가 아무것도 없으면
		if(arrImages == null || arrImages.isEmpty()) 
		{
			toast("No pictures on opcam folder");
			finish();
			return;
		}
		
		
		Collections.reverse(arrImages);	// 최근 찍은 것이 위로 가게
		
		gridView = (GridView) findViewById(R.id.grid_view);
		customGridAdapter = new GridViewAdapter(this, R.layout.row_grid, arrImages);
		gridView.setAdapter(customGridAdapter);

		gridView.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> parent, View v, int position, long id)
			{
				//String currUri= arrImages.get(position).getUri4Share();
				
                //Intent intent = new Intent(getApplicationContext(), ImageZoomActivity.class);
				//intent.putExtra("uri", currUri);
				Intent intent = new Intent(getApplicationContext(), ImageZoomPagerActivity.class);
                intent.putParcelableArrayListExtra("uri_list", getParcelData());
                intent.putExtra("selected_position", position);
                startActivityForResult(intent, 0);
			}

		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.gallery, menu);
		return true;
	}
	
	// 이미지 리스트 중 Uri만을 추려낸다.
	private ArrayList<Uri> getParcelData()
	{
		final ArrayList<Uri> arrRes= new ArrayList<Uri>();
		
		for(ImageItem item : arrImages)
			arrRes.add( Uri.parse( item.getUri4Share() ) );
		
		return arrRes;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(requestCode != 0)
			return;
		
		arrImages= getData();	// 찍은 사진 데이터들을 얻어와서
		
		// 데이터가 아무것도 없으면
		if(arrImages == null || arrImages.isEmpty())
		{
			toast("No pictures on opcam folder");
			finish();
			return;
		}
		
		
		Collections.reverse(arrImages);	// 최근 찍은 것이 위로 가게
		
		gridView = (GridView) findViewById(R.id.grid_view);
		customGridAdapter = new GridViewAdapter(this, R.layout.row_grid, arrImages);
		gridView.setAdapter(customGridAdapter);
		
		super.onActivityResult(requestCode, resultCode, data);
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
		
		File[] fileLists= dir.listFiles(new FilenameFilter() {
		    public boolean accept(File directory, String fileName) {
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
	

	
	public void toast(String msg)
    {
        Toast.makeText (this.getApplicationContext(), msg, Toast.LENGTH_SHORT).show ();
    }

}
