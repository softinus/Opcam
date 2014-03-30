package us.opcam.camera.activity;

import java.io.File;
import java.util.ArrayList;

import us.opcam.camera.R;
import us.opcam.camera.util.ImageItem;
import us.opcam.camera.view.GridViewAdapter;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
		
		gridView = (GridView) findViewById(R.id.grid_view);
		customGridAdapter = new GridViewAdapter(this, R.layout.row_grid, arrImages);
		gridView.setAdapter(customGridAdapter);

		gridView.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> parent, View v, int position, long id)
			{
				String currUri= arrImages.get(position).getUri();
				
                Intent intent = new Intent(getApplicationContext(), ImageZoomActivity.class);
                intent.putExtra("uri", currUri);
                startActivity(intent);
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
	
	private ArrayList<ImageItem> getData()
	{
		final ArrayList<ImageItem> imageItems = new ArrayList<ImageItem>();
		
		File dir=new File(Environment.getExternalStorageDirectory(), "/opcam");  
		int count= dir.list().length;   
		String[] fileNames = dir.list();
		
		for(String name : fileNames)
		{
			String strURI= "/sdcard/opcam/" + name;	// 이름 만듦
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 8;
			Bitmap bmp = BitmapFactory.decodeFile(strURI, options);
			
			if(bmp == null)
					toast("abnormal path. [" + strURI +"]");
			else
				imageItems.add(new ImageItem(bmp, name, strURI));
		}
		return imageItems;	
	}
	
	public void toast(String msg)
    {
        Toast.makeText (this.getApplicationContext(), msg, Toast.LENGTH_SHORT).show ();
    }

}
