package us.opcam.camera.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.aviary.android.feather.FeatherActivity;
import com.aviary.android.feather.library.Constants;

import uk.co.senab.photoview.PhotoViewAttacher;
import us.opcam.camera.R;
import us.opcam.camera.view.SystemUiHider;
import android.R.mipmap;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore.Images;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class ImageZoomActivity extends Activity implements OnClickListener
{
	/**
	 * Whether or not the system UI should be auto-hidden after
	 * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
	 */
	private static final boolean AUTO_HIDE = true;

	/**
	 * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
	 * user interaction before hiding the system UI.
	 */
	private static final int AUTO_HIDE_DELAY_MILLIS = 2500;

	/**
	 * If set, will toggle the system UI visibility upon interaction. Otherwise,
	 * will show the system UI visibility upon interaction.
	 */
	private static final boolean TOGGLE_ON_CLICK = true;

	/**
	 * The flags to pass to {@link SystemUiHider#getInstance}.
	 */
	private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

	/**
	 * The instance of the {@link SystemUiHider} for this activity.
	 */
	private SystemUiHider mSystemUiHider;	
	private PhotoViewAttacher mAttacher;
	private String mCurrFilePath= "";
	ImageView mContentImageView= null;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_image_zoom);

		final View controlsView = findViewById(R.id.fullscreen_content_controls);
		final View contentView = findViewById(R.id.fullscreen_content);
		
		Intent intent = getIntent();	//인텐트  받아오고		
		Bundle extra= intent.getExtras();
		mCurrFilePath= extra.getString("uri");
		Uri imageUri= Uri.parse(mCurrFilePath);
		
		setTitle(mCurrFilePath);
		
		mContentImageView= (ImageView) contentView;
		mContentImageView.setImageURI(imageUri);
		
	    // Attach a PhotoViewAttacher, which takes care of all of the zooming functionality.
	    mAttacher = new PhotoViewAttacher(mContentImageView);
		

		// Set up an instance of SystemUiHider to control the system UI for
		// this activity.
		mSystemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
		mSystemUiHider.setup();
		mSystemUiHider.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener()
		{
					// Cached values.
					int mControlsHeight;
					int mShortAnimTime;

					@Override
					@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
					public void onVisibilityChange(boolean visible)
					{
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2)
						{
							// If the ViewPropertyAnimator API is available
							// (Honeycomb MR2 and later), use it to animate the
							// in-layout UI controls at the bottom of the
							// screen.
							if (mControlsHeight == 0) {
								mControlsHeight = controlsView.getHeight();
							}
							if (mShortAnimTime == 0) {
								mShortAnimTime = getResources().getInteger(
										android.R.integer.config_shortAnimTime);
							}
							controlsView
									.animate()
									.translationY(visible ? 0 : mControlsHeight)
									.setDuration(mShortAnimTime);
						} else {
							// If the ViewPropertyAnimator APIs aren't
							// available, simply show or hide the in-layout UI
							// controls.
							controlsView.setVisibility(visible ? View.VISIBLE
									: View.GONE);
						}

						if (visible && AUTO_HIDE) {
							// Schedule a hide().
							delayedHide(AUTO_HIDE_DELAY_MILLIS);
						}
					}
				});

		// Set up the user interaction to manually show or hide the system UI.
		contentView.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				if (TOGGLE_ON_CLICK)
				{
					mSystemUiHider.toggle();
				} else {
					mSystemUiHider.show();
				}
			}
		});

		// Upon interacting with UI controls, delay any scheduled hide()
		// operations to prevent the jarring behavior of controls going away
		// while interacting with the UI.
		findViewById(R.id.btn_effect).setOnTouchListener(mDelayHideTouchListener);
		findViewById(R.id.btn_share).setOnTouchListener(mDelayHideTouchListener);
		
		findViewById(R.id.btn_effect).setOnClickListener(this);
		findViewById(R.id.btn_share).setOnClickListener(this);
	}
	
	

	@Override
	protected void onPostCreate(Bundle savedInstanceState)
	{
		super.onPostCreate(savedInstanceState);

		// Trigger the initial hide() shortly after the activity has been
		// created, to briefly hint to the user that UI controls
		// are available.
		delayedHide(100);
	}

	/**
	 * Touch listener to use for in-layout UI controls to delay hiding the
	 * system UI. This is to prevent the jarring behavior of controls going away
	 * while interacting with activity UI.
	 */
	View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener()
	{
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent)
		{
			if (AUTO_HIDE)
				delayedHide(AUTO_HIDE_DELAY_MILLIS);	// 3초 더 풀스크린 딜레이.
			
			return false;
		}
	};

	@Override
	public void onClick(View v)
	{
		if(v.getId() == R.id.btn_effect)
		{
			//alert("Effect button");
			GotoAviary(Uri.parse(mCurrFilePath));
		}
//		else if(v.getId() == R.id.btn_delete)
//		{
//			alert("Delete button");
//			File file = new File(mCurrFilePath);
//			boolean deleted = file.delete();
//			
//			if(deleted)
//			{
//				alert("Delete complete.");
//				finish();
//			}
//			else
//			{
//				alert("Delete failed.");
//				finish();
//			}
//		}	
		else if(v.getId() == R.id.btn_share)
		{
			GotoShareActivity(Uri.parse(mCurrFilePath));
		}
	}
	
	Handler mHideHandler = new Handler();
	Runnable mHideRunnable = new Runnable()
	{
		@Override
		public void run()
		{
			mSystemUiHider.hide();
		}
	};
	
	private void GotoShareActivity(Uri mImageUri)
	{
		Intent shareIntent = new Intent( this, SNSShareActivity.class );
		shareIntent.putExtra("path", mImageUri);
		startActivity(shareIntent);
	}
	
	// 해당 Uri를 Aviary로 전송함.
	private void GotoAviary(Uri uPath)
	{
		Intent newIntent = new Intent( this, FeatherActivity.class );
		newIntent.setData( uPath );
		newIntent.putExtra( Constants.EXTRA_IN_API_KEY_SECRET, "f5d04d92e56534ce" );
		startActivityForResult( newIntent, 1 );
	}

	/**
	 * Schedules a call to hide() in [delay] milliseconds, canceling any
	 * previously scheduled calls.
	 */
	private void delayedHide(int delayMillis)
	{
		mHideHandler.removeCallbacks(mHideRunnable);
		mHideHandler.postDelayed(mHideRunnable, delayMillis);
	}
	
	
	
	// from Aviary
	@Override
	public void onActivityResult( int requestCode, int resultCode, Intent data )
	{
	    if( resultCode == RESULT_OK )
	    {
	        switch( requestCode ) 
	        {
	            case 1:
	                // output image path
	                Uri uri = data.getData();
	                Bundle extra = data.getExtras();
	                    if( null != extra ) 
	                    {
	                        // image has been changed by the user?
	                        boolean changed = extra.getBoolean( Constants.EXTRA_OUT_BITMAP_CHANGED );
	                      
	                        if(changed)	// 변화가 있으면...
	                        {
	                        	OverwriteBitmap(uri);
	                        	mContentImageView.setImageURI(uri);
	                        }
	                    }
	                break;
	        }
	    }
	}
	
	private void OverwriteBitmap(Uri modifyPhoto)
	{
    	try
    	{
			Bitmap bmpModify= Images.Media.getBitmap(getContentResolver(), modifyPhoto);	// Aviary에서 변화된 사진
			File pCurrentFile= new File(mCurrFilePath);	// 현재 사진.
			
    		FileOutputStream fos = new FileOutputStream(pCurrentFile);
    		bmpModify.compress(CompressFormat.JPEG, 90, fos);

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

	
	
	private void alert(String message)
	{
		new AlertDialog.Builder(this)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setTitle(R.string.app_name)
			.setMessage(message)
			.setPositiveButton(android.R.string.ok, null)
			.create().show();
	}



}
