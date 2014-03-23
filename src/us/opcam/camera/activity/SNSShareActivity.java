package us.opcam.camera.activity;

import org.json.JSONObject;

import us.opcam.camera.R;

import us.opcam.camera.util.StoryLink;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;

import com.androidquery.AQuery;
import com.androidquery.auth.FacebookHandle;

public class SNSShareActivity extends Activity
{
	Uri mImagePath;
	ImageView IMG_share;
	
	private FacebookHandle handle;
	private final int ACTIVITY_SSO= 1000;
	//private static String PERMISSIONS = "read_stream,read_friendlists,manage_friendlists,manage_notifications,publish_stream,publish_checkins,offline_access,user_photos,user_likes,user_groups,friends_photos";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_snsshare);
		
		IMG_share= (ImageView) findViewById(R.id.img_for_share);
		
		Intent intent = getIntent();//인텐트  받아오고
		
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
		handle= new FacebookHandle(this, "193443224183217", "publish_stream");
		
		AQuery aq= new AQuery(v);
		String url= "https://graph.facebook.com/me/feed";
		aq.auth(handle).progress(R.id.ProgressBar).ajax(url, JSONObject.class, this, "facebookCb");
		
	}
	

//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data)
//	{
//		super.onActivityResult(requestCode, resultCode, data);
//		
//		Bundle extra= data.getExtras();
//		Uri imgPath= extra.getParcelable("path");
//		
//		Log.d("SNSShareActivity::onActivityResult", imgPath.toString());
//		
//		IMG_share.setImageURI(imgPath);
//	}
	
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
