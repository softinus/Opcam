package us.opcam.camera.activity;

import java.io.File;
import java.util.ArrayList;

import us.opcam.camera.R;
import us.opcam.camera.util.GalleryAdapter;
import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;

import com.origamilabs.library.views.StaggeredGridView;

public class GalleryActivity extends Activity 
{
	

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gallery);
		
		StaggeredGridView gridView = (StaggeredGridView) this.findViewById(R.id.staggeredGridView1);
		
		int margin = getResources().getDimensionPixelSize(R.dimen.grid_margin);		
		gridView.setItemMargin(margin); // set the GridView margin		
		gridView.setPadding(margin, 0, margin, 0); // have the margin on the sides as well 
		
		File dir=new File(Environment.getExternalStorageDirectory(),"/opcam");  
        int count=dir.list().length;   
        String[] fileNames = dir.list();
        ArrayList<String> arrStrPath= new ArrayList<String>();
        
        for(String name : fileNames)
        {
        	arrStrPath.add( "/sdcard/opcam/" + name );	// ¿Ã∏ß ∏∏µÍ
        }
        
        String[] filePaths= arrStrPath.toArray(new String[arrStrPath.size()]);        
		GalleryAdapter adapter = new GalleryAdapter(GalleryActivity.this, R.id.imageView1, filePaths);
		
		gridView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.gallery, menu);
		return true;
	}

}
