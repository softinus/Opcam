package us.opcam.camera.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import us.opcam.camera.R;
import us.opcam.camera.view.HackyViewPager;
import us.opcam.camera.view.PhotoPagerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images;
import android.support.v4.view.ViewPager;
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
				
		Intent intent = getIntent();	//인텐트  받아오고		
		Bundle extra= intent.getExtras();
		
		data= extra.getParcelableArrayList("uri_list");
		mCurrPos= extra.getInt("selected_position");

		mAdapter= new PhotoPagerAdapter(this, data);
		mViewPager.setAdapter(mAdapter);
		mViewPager.setCurrentItem(mCurrPos);
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
			// 경로를 URI에서 sdcard 경로로 바꿔줌.
			
			new File( strNewFilePath ).delete();	// 파일 지우고

			mViewPager.setAdapter(null);
			mAdapter.data.remove(nForDeletePos);
			mViewPager.setAdapter(mAdapter);	// 어댑터 리셋
			
			if(nForDeletePos >= 1)
			{
				mViewPager.setCurrentItem(nForDeletePos-1, true);
			}
			else if (nForDeletePos == 0)
			{
				mViewPager.setCurrentItem(1, true);
			}
			
			if( mAdapter.data.isEmpty() )
				finish();
		}			
		break;

		case R.id.actionbar_edit:
		{
			GotoAviary();
		}
		break;
			
		case R.id.actionbar_share:
		{
			GotoShareActivity();
		}
		break;

		default:
			return false;
		}
		return true;
	}
	
	// aviary에서 넘어올 때
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if( resultCode == RESULT_OK )
	    {
	        switch( requestCode ) 
	        {
	            case 1:
	                // output image path
	                Uri mImageUri = data.getData();
	                Bundle extra = data.getExtras();
	                
                    if( null != extra ) 
                    {
                        // image has been changed by the user?
                        boolean changed = extra.getBoolean( Constants.EXTRA_OUT_BITMAP_CHANGED );
                        
                        if(changed)
                        {
                        	OverwriteBitmap(mImageUri);
                        	
                			mViewPager.setAdapter(null);
                			mViewPager.setAdapter(mAdapter);	// 어댑터 리셋
                			mViewPager.setCurrentItem(mCurrPos);
                        }
                    }
	                break;
	        }
	    }
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void OverwriteBitmap(Uri modifyPhoto)
	{
    	try
    	{
			Bitmap bmpModify= Images.Media.getBitmap(getContentResolver(), modifyPhoto);
			File pCurrentFile= new File(data.get(mCurrPos).getPath());
			
    		FileOutputStream fos = new FileOutputStream(pCurrentFile);
    		bmpModify.compress(CompressFormat.JPEG, 100, fos);

    		fos.close();
    		bmpModify.recycle();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch(Exception e) {
    		e.printStackTrace();
		}
	}
	
	
	
	// 해당 Uri를 Aviary로 전송함.
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
