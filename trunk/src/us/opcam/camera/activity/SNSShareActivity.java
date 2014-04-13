package us.opcam.camera.activity;

import java.io.FileNotFoundException;
import java.io.IOException;

import us.opcam.camera.R;
import us.opcam.camera.util.StoryLink;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.entities.Photo;
import com.sromku.simple.fb.entities.Privacy;
import com.sromku.simple.fb.entities.Privacy.PrivacySettings;
import com.sromku.simple.fb.listeners.OnLoginListener;
import com.sromku.simple.fb.listeners.OnPublishListener;

public class SNSShareActivity extends Activity
{
	protected static final String TAG = SNSShareActivity.class.getName();
	
	private Uri mImagePath;
	private ImageView IMG_share;
	private boolean bSharing= false;	// 공유중 상태
	
	private EditText EDT_share;	
	
	private SimpleFacebook mSimpleFacebook;
	private ProgressDialog mProgress;
	
	
	@Override
	protected void onResume()
	{
		super.onResume();		
		mSimpleFacebook = SimpleFacebook.getInstance(this);
	}
	
	// Login listener
	private OnLoginListener mOnLoginListener = new OnLoginListener() {

		@Override
		public void onFail(String reason)
		{
			alert("Failed to login");
			Log.w(TAG, "Failed to login");
		}

		@Override
		public void onException(Throwable throwable)
		{
			alert("Exception: " + throwable.getMessage());
			Log.e(TAG, "Bad thing happened", throwable);
		}

		@Override
		public void onThinking()
		{
		}

		@Override
		public void onLogin() {
			//alert("You are logged in");
			postData();
		}

		@Override
		public void onNotAcceptingPermissions(Permission.Type type) {
			alert(String.format("You didn't accept %s permissions", type.name()));
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_snsshare);
		
		IMG_share= (ImageView) findViewById(R.id.img_for_share);
		
		Intent intent = getIntent();	//인텐트  받아오고		
		Bundle extra= intent.getExtras();
		mImagePath= extra.getParcelable("path");
		
		EDT_share= (EditText) findViewById(R.id.edt_for_share);
		
		IMG_share.setImageURI(mImagePath);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.snsshare, menu);
		return true;
	}
	
	/**
	 * share to kakaostory
	 * @param strImgPath
	 */
	public void ShareToKakao(View v) throws NameNotFoundException
	{
		if(bSharing)	// 이미 공유중이면 안됨.
			return;
		
		bSharing= true;
		
		// Image를 전송할 때 
		Intent intent = new Intent(Intent.ACTION_SEND); 
		intent.setType("image/png"); 
		intent.putExtra(Intent.EXTRA_STREAM, mImagePath); 
		intent.setPackage("com.kakao.talk");
		startActivity(intent);
		
//		StoryLink storyLink = StoryLink.getLink(getApplicationContext());
//
//		// check, intent is available.
//		if (!storyLink.isAvailableIntent())
//		{
//			alert("Not installed KakaoStory.");
//			return;
//		}		
//		storyLink.openStoryLinkImageApp(this, mImagePath.toString());
		
		bSharing= false;
	}
	
	public void ShareToFacebook(View v) throws NameNotFoundException
	{	
		if(bSharing)	// 이미 공유중이면 안됨.
			return;
		
		bSharing= true;
		mSimpleFacebook.login(mOnLoginListener);
	    bSharing= false;
	}
	
	
	
	/**
	 * This method used to post data on facebook.
	 */
	private void postData()
	{
		// take screenshot
		Bitmap bitmap = null;
		try {
			bitmap = Images.Media.getBitmap(getContentResolver(), mImagePath);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// set privacy
		Privacy privacy = new Privacy.Builder()
				.setPrivacySettings(PrivacySettings.SELF)
				.build();
		
		
		// create Photo instance and add some properties
		Photo photo = new Photo.Builder()
				.setImage(bitmap)
				.setName(EDT_share.getText().toString())
				.setPrivacy(privacy)
				.build();

		// publish
		mSimpleFacebook.publish(photo, new OnPublishListener()
		{
			@Override
			public void onFail(String reason)
			{
				hideDialog();
				Log.w(TAG, "Failed to publish");
				alert("Failed to publish" + reason.toString());
			}

			@Override
			public void onException(Throwable throwable)
			{
				hideDialog();
				Log.e(TAG, "Bad thing happened", throwable);
				alert("Bad thing happened" + throwable.toString());
			}

			@Override
			public void onThinking()
			{
				showDialog();
			}

			@Override
			public void onComplete(String id)
			{
				hideDialog();
				//alert("Published successfully. The new image id = " + id);
				alert("Published successfully.");
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		mSimpleFacebook.onActivityResult(this, requestCode, resultCode, data); 
	    super.onActivityResult(requestCode, resultCode, data);
	}
	
	
	private void toast(String message) {
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
	}
	
	private void showDialog() {
		mProgress = ProgressDialog.show(this, "Uploading...", "Waiting for Facebook", true);
	}

	private void hideDialog() {
		if (mProgress != null) {
			mProgress.hide();
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
