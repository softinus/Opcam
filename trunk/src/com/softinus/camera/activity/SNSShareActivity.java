package com.softinus.camera.activity;

import com.softinus.camera.R;
import com.softinus.camera.R.layout;
import com.softinus.camera.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class SNSShareActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_snsshare);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.snsshare, menu);
		return true;
	}

}
