package us.opcam.camera.activity;

import us.opcam.camera.R;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;

public class SNSShareActivity extends Activity
{
	ImageView IMG_share;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_snsshare);
		
		IMG_share= (ImageView) findViewById(R.id.img_for_share);
		
		Intent intent = getIntent();//인텐트  받아오고
		
		Bundle extra= intent.getExtras();
		Uri imgPath= extra.getParcelable("path");
		
		Log.d("SNSShareActivity::onActivityResult", imgPath.toString());
		
		IMG_share.setImageURI(imgPath);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.snsshare, menu);
		return true;
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

}
