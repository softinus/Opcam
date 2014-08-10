/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package us.opcam.camera.activity;

import java.util.ArrayList;

import us.opcam.camera.R;
import us.opcam.camera.util.Constants.Extra;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class URLImagePagerActivity extends Activity
{
	
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	private static final String STATE_POSITION = "STATE_POSITION";
	DisplayImageOptions options;

	ViewPager pager;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.discover_image_pager);

		Bundle bundle = getIntent().getExtras();
		assert bundle != null;
		
		// intent로 받아온 bundle을 풀어서 가져온다.
		String[] imageUrls = bundle.getStringArray(Extra.IMAGES);
		ArrayList<String> imagesAuthor = bundle.getStringArrayList(Extra.IMAGES_AUTHOR);
		ArrayList<String> imagesCreateDate = bundle.getStringArrayList(Extra.IMAGES_CREATE_DATE);
		int pagerPosition = bundle.getInt(Extra.IMAGE_POSITION, 0);

		if (savedInstanceState != null)
		{
			pagerPosition = savedInstanceState.getInt(STATE_POSITION);
		}
		
		if(imageUrls == null)
			finish();

		options = new DisplayImageOptions.Builder()
			.showImageForEmptyUri(R.drawable.ic_empty)
			.showImageOnFail(R.drawable.ic_error)
			.resetViewBeforeLoading(true)
			.cacheOnDisk(true)
			.imageScaleType(ImageScaleType.EXACTLY)
			.bitmapConfig(Bitmap.Config.RGB_565)
			.considerExifParams(true)
			.displayer(new FadeInBitmapDisplayer(300))
			.build();

		// 어댑터에 가져온 정보들을 pager에서 띄울 수 있도록 넣어준다.
		pager = (ViewPager) findViewById(R.id.pager);
		pager.setAdapter(new ImagePagerAdapter(imageUrls, imagesAuthor, imagesCreateDate));
		pager.setCurrentItem(pagerPosition);
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		outState.putInt(STATE_POSITION, pager.getCurrentItem());
	}

	private class ImagePagerAdapter extends PagerAdapter
	{

		private String[] images;
		private ArrayList<String> arrName;
		private ArrayList<String> arrDate;
		
		private LayoutInflater inflater;

		ImagePagerAdapter(String[] images, ArrayList<String> arrNames, ArrayList<String> arrDate)
		{
			this.images = images;
			this.arrName= arrNames;
			this.arrDate= arrDate;
			inflater = getLayoutInflater();
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object)
		{
			container.removeView((View) object);
		}

		@Override
		public int getCount()
		{
			return images.length;
		}

		@Override
		public Object instantiateItem(ViewGroup view, int position)
		{
			View imageLayout = inflater.inflate(R.layout.item_discover_pager, view, false);
			assert(imageLayout != null);
			
			ImageView imageView = (ImageView) imageLayout.findViewById(R.id.image);
			TextView txtName = (TextView) imageLayout.findViewById(R.id.txt_name);
			TextView txtDate = (TextView) imageLayout.findViewById(R.id.txt_date);
			final TextView txtLoading = (TextView) imageLayout.findViewById(R.id.txt_loading);
			final ProgressBar progress = (ProgressBar) imageLayout.findViewById(R.id.loading);
			
			txtName.setText(arrName.get(position));	// 이미지 작성자 이름과 날짜를 띄워준다.
			txtDate.setText(arrDate.get(position));

			imageLoader.displayImage(images[position], imageView, options, new SimpleImageLoadingListener()
			{
				@Override
				public void onLoadingStarted(String imageUri, View view)
				{
					progress.setVisibility(View.VISIBLE);
					txtLoading.setVisibility(View.VISIBLE);
				}

				@Override
				public void onLoadingFailed(String imageUri, View view, FailReason failReason)
				{
					String message = null;
					switch (failReason.getType())
					{
						case IO_ERROR:
							message = "Input/Output error";
							break;
						case DECODING_ERROR:
							message = "Image can't be decoded";
							break;
						case NETWORK_DENIED:
							message = "Downloads are denied";
							break;
						case OUT_OF_MEMORY:
							message = "Out Of Memory error";
							break;
						case UNKNOWN:
							message = "Unknown error";
							break;
					}
					Toast.makeText(URLImagePagerActivity.this, message, Toast.LENGTH_SHORT).show();

					progress.setVisibility(View.GONE);
				}

				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage)
				{
					progress.setVisibility(View.GONE);
					txtLoading.setVisibility(View.GONE);
				}
			 }, new ImageLoadingProgressListener()
			 {
				 @Override
				 public void onProgressUpdate(String imageUri, View view, int current, int total)
				 {
					 double lfProgressPrecent= 100.0f * current / total;
					 progress.setProgress( (int) Math.round(lfProgressPrecent) );
					 txtLoading.setText(lfProgressPrecent+"%");
				 }
			 }
			);

			view.addView(imageLayout, 0);
			return imageLayout;
		}

		@Override
		public boolean isViewFromObject(View view, Object object)
		{
			return view.equals(object);
		}

		@Override
		public void restoreState(Parcelable state, ClassLoader loader)
		{
		}

		@Override
		public Parcelable saveState()
		{
			return null;
		}
	}
}