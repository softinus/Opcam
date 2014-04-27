package us.opcam.camera.activity;

import java.io.File;
import java.util.ArrayList;

import us.opcam.camera.R;
import us.opcam.camera.view.HackyViewPager;
import us.opcam.camera.view.PhotoPagerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.aviary.android.feather.FeatherActivity;
import com.aviary.android.feather.library.Constants;

public class ImageZoomPagerActivity extends Activity
{
	ArrayList<Uri> data= null;
	ViewPager mViewPager= null;
	int mCurrPos= 0;
	
	PhotoPagerAdapter mAdapter= null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_image_zoom2);
        mViewPager = (HackyViewPager) findViewById(R.id.view_pager);
		setContentView(mViewPager);
				
		Intent intent = getIntent();	//����Ʈ  �޾ƿ���		
		Bundle extra= intent.getExtras();
		
		data= extra.getParcelableArrayList("uri_list");
		mCurrPos= extra.getInt("selected_position");

		mAdapter= new PhotoPagerAdapter(this, data);
		mViewPager.setAdapter(mAdapter);
		mViewPager.setCurrentItem(mCurrPos);
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
		{
			int nForDeletePos= mViewPager.getCurrentItem();
			String strURIpath= mAdapter.data.get(nForDeletePos).toString();
			int pos= strURIpath.lastIndexOf("/");
			String strFileName= strURIpath.substring(pos, strURIpath.length());
			String strNewFilePath= "/sdcard/opcam" + strFileName;
			
			new File( strNewFilePath ).delete();	// ���� �����

			if (nForDeletePos == mViewPager.getCurrentItem())
			{
                if(nForDeletePos == (data.size()-1)) {
                	mViewPager.setCurrentItem(nForDeletePos-1);
                } else if (nForDeletePos == 0){
                	mViewPager.setCurrentItem(1);
                }
            }
			mViewPager.removeViewAt(nForDeletePos);
			mAdapter.data.remove(nForDeletePos);	// �� ����.
			mAdapter.notifyDataSetChanged();
			
			if( mAdapter.data.isEmpty() )
				finish();
		}			
		break;

		case R.id.actionbar_edit:
			GotoAviary();
			break;
			
		case R.id.actionbar_share:
			GotoShareActivity();
			break;

		default:
			return false;
		}
		return true;
	}
	
	
	// �ش� Uri�� Aviary�� ������.
	private void GotoAviary()
	{
		Intent newIntent = new Intent( this, FeatherActivity.class );
		newIntent.setData( data.get(mViewPager.getCurrentItem()) );
		newIntent.putExtra( Constants.EXTRA_IN_API_KEY_SECRET, "f5d04d92e56534ce" );
		startActivityForResult( newIntent, 1 );
	}

	private void GotoShareActivity()
	{
		Intent shareIntent = new Intent( this, SNSShareActivity.class );
		//shareIntent.putExtra("contants", null);
		shareIntent.putExtra("path", data.get(mViewPager.getCurrentItem()));
		startActivity(shareIntent);
	}

}
