package us.opcam.camera.activity;

import java.io.File;
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
	Uri mImagePath;
	ImageView IMG_share;
	
	//private Facebook facebook;
	private boolean isSharingData = false;
	
//	private FacebookHandle handle;
//	private final int ACTIVITY_SSO= 1000;
	//private static String PERMISSIONS = "read_stream,read_friendlists,manage_friendlists,manage_notifications,publish_stream,publish_checkins,offline_access,user_photos,user_likes,user_groups,friends_photos";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_snsshare);
		
		IMG_share= (ImageView) findViewById(R.id.img_for_share);
		
		Intent intent = getIntent();	//인텐트  받아오고		
		Bundle extra= intent.getExtras();
		mImagePath= extra.getParcelable("path");
		
		//Log.d("SNSShareActivity::onActivityResult", mImagePath.toString());
		
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
		StoryLink storyLink = StoryLink.getLink(getApplicationContext());

		// check, intent is available.
		if (!storyLink.isAvailableIntent())
		{
			alert("Not installed KakaoStory.");
			return;
		}
		
		storyLink.openStoryLinkImageApp(this, mImagePath.toString());
	}
	
	public void ShareToFacebook(View v) throws NameNotFoundException
	{
//		handle= new FacebookHandle(this, "193443224183217", "publish_stream");		
//		AQuery aq= new AQuery(v);
//		String url= "https://graph.facebook.com/me/feed";
//		aq.auth(handle).progress(R.id.ProgressBar).ajax(url, JSONObject.class, this, "facebookCb");
				
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
	          // make request to the /me API
//	          Request.executeMeRequestAsync(session, new Request.GraphUserCallback()
//	          { // callback after Graph API response with user object
//	            @Override
//	            public void onCompleted(GraphUser user, Response response)
//	            {
//	              if (user != null)
//	              {
//	                alert(user.getName());
//	              }
//	            }
//	          });
	        	
	        	// Check for publish permissions    
//	            List<String> permissions = session.getPermissions();
//	            if (!isSubsetOf(PERMISSIONS, permissions)) {
//	                pendingPublishReauthorization = true;
//	                Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(this, PERMISSIONS);
//	            session.requestNewPublishPermissions(newPermissionsRequest);
//	                return;
//	            }
	        	
//	        	byte[] ImageBytes= null;
//	    		File fin = new File(mImagePath.toString());
//	    		try {
//	    			ImageBytes= org.apache.commons.io.FileUtils.readFileToByteArray(fin);
//				} catch (IOException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
	    		

                
//	            Bundle postParams = new Bundle();
//	            postParams.putString("name", "name");
//	            postParams.putString("caption", "caption");
//	            postParams.putString("description", "description");
//	            //postParams.putString("link", "https://developers.facebook.com/android");
//	            //postParams.putString("picture", "https://raw.github.com/fbsamples/ios-3.x-howtos/master/Images/iossdk_logo.png");
//	            postParams.putByteArray("picture", bytePic);
//
//	            Request.Callback callback= new Request.Callback() {
//	                public void onCompleted(Response response) {
//	                    JSONObject graphResponse = response
//	                                               .getGraphObject()
//	                                               .getInnerJSONObject();
//	                    String postId = null;
//	                    try {
//	                        postId = graphResponse.getString("id");
//	                    } catch (JSONException e) {
//	                        Log.i("ShareActivity::", "JSON error "+ e.getMessage());
//	                    }
//	                    FacebookRequestError error = response.getError();
//	                    if (error != null) {
//	                        Toast.makeText(getApplicationContext()
//	                             .getApplicationContext(),
//	                             error.getErrorMessage(),
//	                             Toast.LENGTH_SHORT).show();
//	                        } else {
//	                            Toast.makeText(getApplicationContext()
//	                                 .getApplicationContext(), 
//	                                 postId,
//	                                 Toast.LENGTH_LONG).show();
//	                    }
//	                }
//	            };
//
//	            Request request = new Request(session, "me/feed", postParams, 
//	                                  HttpMethod.POST, callback);
//
//	            RequestAsyncTask task = new RequestAsyncTask(request);
//	            task.execute();
	        }
	      }
	    });
		
	}
	
//	private boolean isSubsetOf(Collection<String> subset, Collection<String> superset) {
//	    for (String string : subset) {
//	        if (!superset.contains(string)) {
//	            return false;
//	        }
//	    }
//	    return true;
//	}
	
	
	
	/**
	 * This method used to post data on facebook.
	 */
	private void postData()
	{
	    Session session = Session.getActiveSession();
	    if (session != null)
	    {
	    	Bitmap image= null;
			try {
				image = Images.Media.getBitmap(getContentResolver(), mImagePath);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


	        Request.Callback callback = new Request.Callback() {

	            public void onCompleted(Response response)
	            {
	                FacebookRequestError error = response.getError();
	                if (error != null)
	                {
	                } else {
	                	alert("Upload complete!");
	                }
	            }
	        };

	        //Request request = new Request(Session.getActiveSession(), "me/feed", postParams, HttpMethod.POST, callback);
	        //RequestAsyncTask task = new RequestAsyncTask(request);
	        //task.execute();
	        Request request= Request.newUploadPhotoRequest(session, image, callback);
	        request.executeAsync();
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
