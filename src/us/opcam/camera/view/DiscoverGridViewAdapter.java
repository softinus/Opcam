package us.opcam.camera.view;

import java.util.ArrayList;

import us.opcam.camera.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;


class ViewHolder
{
	TextView txtName;
	TextView txtDate;
	ImageView imageView;
	ProgressBar progressBar;
}

public class DiscoverGridViewAdapter extends BaseAdapter
{
	Context mContext;
	DisplayImageOptions options;
	ImageLoader loader;
	ArrayList<String> arrImages= new ArrayList<String>();
	ArrayList<String> arrDate= new ArrayList<String>();
	ArrayList<String> arrNames= new ArrayList<String>();
	
	public DiscoverGridViewAdapter(Context _context, ImageLoader _loader
			,ArrayList<String> _arrImages, DisplayImageOptions _options
			,ArrayList<String> _arrName, ArrayList<String> _arrDate)
	{
		this.mContext= _context;
		this.loader= _loader;
		this.arrImages= _arrImages;
		this.options= _options;
		this.arrDate= _arrDate;
		this.arrNames= _arrName;
	}
	@Override
	public int getCount()
	{
		return arrImages.size();
	}

	@Override
	public Object getItem(int position)
	{
		return null;
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		final ViewHolder holder;
		View view = convertView;
		if (view == null)
		{
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
			
			view = inflater.inflate(R.layout.item_dicsover_grid, parent, false);
			holder = new ViewHolder();
			assert view != null;
			holder.imageView = (ImageView) view.findViewById(R.id.image);
			holder.progressBar = (ProgressBar) view.findViewById(R.id.progress);
			holder.txtDate = (TextView) view.findViewById(R.id.text_date);
			holder.txtName = (TextView) view.findViewById(R.id.text_name);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		holder.txtName.setText(arrNames.get(position));
		holder.txtDate.setText(arrDate.get(position));

		// 이미지 출력과 그에 대한 각종 콜백 함수들... (이미지로더 오픈소스)
		loader.displayImage(arrImages.get(position), holder.imageView, options, new SimpleImageLoadingListener()
		{
									 @Override
									 public void onLoadingStarted(String imageUri, View view)
									 {
										 holder.imageView.setVisibility(View.INVISIBLE);
										 holder.progressBar.setProgress(0);
										 holder.progressBar.setVisibility(View.VISIBLE);
									 }

									 @Override
									 public void onLoadingFailed(String imageUri, View view,
											 FailReason failReason) {
										 holder.imageView.setVisibility(View.VISIBLE);
										 holder.progressBar.setVisibility(View.GONE);
									 }

									 @Override
									 public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage)
									 {
										 holder.imageView.setVisibility(View.VISIBLE);
										 holder.progressBar.setVisibility(View.GONE);
									 }
		 }, new ImageLoadingProgressListener() {
			 @Override
			 public void onProgressUpdate(String imageUri, View view, int current,
					 int total) {
				 holder.progressBar.setProgress(Math.round(100.0f * current / total));
			 }
		 }
		);

		return view;
	}
}