package com.softinus.camera.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.softinus.camera.R;

public class GalleryAdapter extends ArrayAdapter<String> 
{

	//private ImageLoader mLoader;
	Context mContext;

	public GalleryAdapter(Context context, int textViewResourceId,
			String[] objects) 
	{
		super(context, textViewResourceId, objects);
		//mLoader = new ImageLoader(context);
		mContext= context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{

		ViewHolder holder;

		if (convertView == null)	// 처음없을때
		{
			LayoutInflater layoutInflator = LayoutInflater.from(getContext());
			convertView = layoutInflator.inflate(R.layout.row_staggered_demo, null);
			
			holder = new ViewHolder();
			holder.imageView = (ScaleImageView) convertView .findViewById(R.id.imageView1);
			convertView.setTag(holder);
		}

		holder = (ViewHolder) convertView.getTag();
		
		String strURI= getItem(position);		
		Bitmap bmp = BitmapFactory.decodeFile(strURI);
		
		if(bmp == null)
		{
			 toast("abnormal path. [" + strURI +"]");
		}
		else
		{
			//toast(strURI);
			holder.imageView.setImageBitmap(bmp);
		}
        
       
		//mLoader.DisplayImage(getItem(position), holder.imageView);

		return convertView;
	}

	static class ViewHolder
	{
		ScaleImageView imageView;
	}
	
	public void toast(String msg)
    {
        Toast.makeText (mContext, msg, Toast.LENGTH_SHORT).show ();
    }
}
