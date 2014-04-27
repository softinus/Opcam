package us.opcam.camera.activity;

import java.util.ArrayList;

import us.opcam.camera.R;
import us.opcam.camera.view.HackyViewPager;
import us.opcam.camera.view.PhotoPagerAdapter;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

public class ImageZoomPagerActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_image_zoom2);
        ViewPager mViewPager = (HackyViewPager) findViewById(R.id.view_pager);
		setContentView(mViewPager);
				
		Intent intent = getIntent();	//인텐트  받아오고		
		Bundle extra= intent.getExtras();
		ArrayList<Uri> data= extra.getParcelableArrayList("uri_list");
		int pos= extra.getInt("selected_position");

		mViewPager.setAdapter(new PhotoPagerAdapter(this, data));
		mViewPager.setCurrentItem(pos);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.image_zoom, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case android.R.id.home:
			break;

		case R.id.actionbar_delete:
			break;

		case R.id.actionbar_edit:
			break;
			
		case R.id.actionbar_share:
			break;

		default:
			return false;
		}
		return true;
	}

}
