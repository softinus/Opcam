package us.opcam.camera.activity;

import java.io.FileNotFoundException;
import java.io.IOException;

import us.opcam.camera.R;
import us.opcam.camera.util.StoryLink;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;

public class SNSShareActivity extends Activity
{
	private Uri mImagePath;
	private ImageView IMG_share;
	private boolean bSharing= false;	// 공유중 상태
	
	private EditText EDT_share;
	
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
	public void ShareToKakaoStory(View v) throws NameNotFoundException
	{
		if(bSharing)	// 이미 공유중이면 안됨.
			return;
		
		bSharing= true;
		v.setEnabled(false);
		
		StoryLink storyLink = StoryLink.getLink(getApplicationContext());

		// check, intent is available.
		if (!storyLink.isAvailableIntent())
		{
			alert("Not installed KakaoStory.");
			return;
		}
		
		storyLink.openStoryLinkImageApp(this, mImagePath.toString());
		
		bSharing= false;
		v.setEnabled(true);
	}
	
	public void ShareToFacebook(View v) throws NameNotFoundException
	{	
		if(bSharing)	// 이미 공유중이면 안됨.
			return;
		
		bSharing= true;
		v.setEnabled(false);
		
		
		// start Facebook Login
	    Session.openActiveSession(this, true, new Session.StatusCallback()
	    { // callback when session changes state
	    @SuppressWarnings("deprecation")
		@Override
	    public void call(Session session, SessionState state, Exception exception)
	    {
	        if (session.isOpened())
	        {
	          postData();
	        }
	      }
	    });
	    
	    bSharing= false;
	    v.setEnabled(true);
	}
	
	
	
	/**
	 * This method used to post data on facebook.
	 */
	private void postData()
	{
		//alert(mImagePath.toString());
		
	    Session session = Session.getActiveSession();
	    if (session != null)
	    {
	    	Bitmap image= null;
			try {
				image = Images.Media.getBitmap(getContentResolver(), mImagePath);
			}
			catch (FileNotFoundException e)
			{
				alert(e.toString());
			}
			catch (IOException e)
			{
				alert(e.toString());
			}


//	        Request.Callback callback = new Request.Callback() {
//
//	            public void onCompleted(Response response)
//	            {
//	                FacebookRequestError error = response.getError();
//	                if (error != null)
//	                {
//	                	alert(error.toString());
//	                } else {
//	                	alert("Upload complete!");
//	                }
//	            }
//	        };
	        
	        Bundle params = new Bundle();
	        params.putParcelable("picture", image);
	        params.putString("message", EDT_share.getText().toString());
	        //params.putString("place", "1235456498726"); // place id of the image

	        Request request = new Request(session, "me/photos", params, HttpMethod.POST, new Request.Callback()
	        {
	            @Override    
	            public void onCompleted(Response response)
	            {
	                FacebookRequestError error = response.getError();
	                if (error != null)
	                {
	                	alert(error.toString());
	                } else {
	                	alert("Upload complete!");
	                }
	            }
	        });

	        RequestAsyncTask task = new RequestAsyncTask(request);
	        task.execute();

//	        Request request= Request.newUploadPhotoRequest(session, image, callback);
//	        request.executeAsync();
	    }
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		
//		Bundle extra= data.getExtras();
//		Uri imgPath= extra.getParcelable("path");
//		
//		Log.d("SNSShareActivity::onActivityResult", imgPath.toString());
//		
//		IMG_share.setImageURI(imgPath);
		
		Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
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
