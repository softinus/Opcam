package us.opcam.camera.view;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore.Images;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

public class PhotoPagerAdapter extends PagerAdapter
{
	ArrayList<Uri> data = new ArrayList<Uri>();
	Context mContext= null;
	
	public PhotoPagerAdapter(Context _context, ArrayList<Uri> _data)
	{
		this.mContext= _context;
		this.data= _data;
	}
	
	@Override
	public int getCount()
	{
		return data.size();
	}
	
	@Override
	public View instantiateItem(ViewGroup container, int position)
	{
		Log.d("test", "selected idx : "+position);
		
		PhotoView photoView = new PhotoView(container.getContext());
		Bitmap bmp = null;
		try
		{
			bmp = Images.Media.getBitmap(mContext.getContentResolver(), data.get(position));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		photoView.setImageBitmap(bmp);

		// Now just add PhotoView to ViewPager and return it
		container.addView(photoView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

		return photoView;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

	@Override
	public boolean isViewFromObject(View view, Object object)
	{
		return view == object;
	}
}
