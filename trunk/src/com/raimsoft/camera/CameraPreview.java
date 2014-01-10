package com.raimsoft.camera;

import android.app.Activity;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class CameraPreview extends Activity implements OnClickListener
{    
	private LinearLayout mLayout;
    private CameraView mCView1;
    private CameraView mCView2;
    private ImageView mImgPreview;
    
    private Button ShutButton;
	private PictureCallback mPicture;
	
//    private boolean bBackDone= false;
//    private boolean bFrontDone= false;
    
    @Override
	protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        // Hide the window title.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    
        // Create our Preview view and set it as the content of our activity.
        //mCView = new CameraView(this);
        //setContentView(mCView);
        
        setContentView(R.layout.main);
        mLayout = (LinearLayout) findViewById(R.id.camera_layout);
        //mCView= (CameraView) findViewById(R.id.camera_preview1);
        
        mCView1= new CameraView(this, false);        
        mCView1.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 0.5f));        
        mLayout.addView(mCView1);
        
        ShutButton= (Button) findViewById(R.id.btn_click);        
        ShutButton.setOnClickListener(this);
        ShutButton.setText("YOU");
    }

	@Override
	public void onClick(View v)
	{
		if(v.getId() == R.id.btn_click)
		{
			if(ShutButton.getText().equals("YOU"))
			{
				mCView1.mCamera.takePicture(null, null, mPicture);
				//bBackDone= true;
				

		//        aq.id(R.id.btn_click)
				 ShutButton.setText("ME");
				
				//cameraView.ReleaseCamera();
				SystemClock.sleep(1000);
			}
			else if(ShutButton.getText().equals("ME"))
			{
				//mCView.mCamera.takePicture(null, null, mPicture);
				ShutButton.setText("-->");
			}
			else
			{
			
			}
		}
	}

}
