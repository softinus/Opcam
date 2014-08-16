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
import us.opcam.camera.util.Constants;
import us.opcam.camera.util.Constants.Extra;
import us.opcam.camera.util.SPUtil;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
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
public class URLImagePagerActivity extends SherlockActivity
{
	
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	private static final String STATE_POSITION = "STATE_POSITION";
	DisplayImageOptions options;

	ViewPager pager;
	ArrayList<String> imagesAuthor= null;
	ArrayList<String> imagesCreateDate= null;
	
	private boolean bThisPicMine= false;	// 이 사진이 나의 사진인고
	private int pagerPosition= -1; // 현재 사진의 포지션

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		if(bThisPicMine)	// 이 사진이 내 사진이면 
		{
			menu.add("Delete")
		    .setIcon(R.drawable.icon_delete)
		    .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		}

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.discover_image_pager);
		

		Bundle bundle = getIntent().getExtras();
		assert bundle != null;
		
		// intent로 받아온 bundle을 풀어서 가져온다.
		String[] imageUrls = bundle.getStringArray(Extra.IMAGES);
		imagesAuthor = bundle.getStringArrayList(Extra.IMAGES_AUTHOR);
		imagesCreateDate = bundle.getStringArrayList(Extra.IMAGES_CREATE_DATE);
		pagerPosition = bundle.getInt(Extra.IMAGE_POSITION, 0);	// 선택한 이미지 포지션

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
		OnPageChangeListener mPageChangeListener = new OnPageChangeListener() {
		    @Override
		    public void onPageScrollStateChanged(int arg0) {
		        // TODO Auto-generated method stub

		    }

		    @Override
		    public void onPageScrolled(int arg0, float arg1, int arg2) {
		        // TODO Auto-generated method stub

		    }

		    @Override
		    public void onPageSelected(int pos)
		    {
		    	CheckImageAuthorAndRefreshOptionsMenu(pos);
		    }

		};
		pager.setOnPageChangeListener(mPageChangeListener);
		CheckImageAuthorAndRefreshOptionsMenu(pagerPosition);	// 처음 create 할 때도 체크함.
	}
	
	private void CheckImageAuthorAndRefreshOptionsMenu(int pos)
	{
		String nick= SPUtil.getString(getApplicationContext(), Constants.Extra.MY_NICK);
		if(imagesAuthor.get(pos).equals(nick))	// 선택한 이미지가 내 것이라면...
			bThisPicMine= true;
		else
			bThisPicMine= false;
		
		invalidateOptionsMenu();
	}

	@Override
	public boolean onOptionsItemSelected(
			com.actionbarsherlock.view.MenuItem item)
	{
        //Toast.makeText(this, "Got click: " + item.toString(), Toast.LENGTH_SHORT).show();
		
		AlertDialog.Builder alert_confirm = new AlertDialog.Builder(this);
		alert_confirm.setMessage("Are you delete this picture?").setCancelable(false).setPositiveButton("Yes",
		new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        // 'YES'
				Intent intent = getIntent();
				intent.putExtra(Constants.Extra.DEL_POS_SERVER, pagerPosition);
				setResult(RESULT_OK,intent);
				finish();
		    }
		}).setNegativeButton("No",
		new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        // 'No'
		    return;
		    }
		});
		AlertDialog alert = alert_confirm.create();
		alert.show();
		

		
		return super.onOptionsItemSelected(item);
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