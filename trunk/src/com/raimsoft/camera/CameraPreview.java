package com.raimsoft.camera;

import android.app.Activity;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

public class CameraPreview extends Activity implements OnClickListener
{    
    private CameraView mCView;
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
        mCView= (CameraView) findViewById(R.id.camera_preview1);
        ShutButton= (Button) findViewById(R.id.btn_click);
        
        ShutButton.setOnClickListener(this);
        ShutButton.setText("YOU");
       // mCView= new CameraView(this);
    }

	@Override
	public void onClick(View v)
	{
		if(v.getId() == R.id.btn_click)
		{
			if(ShutButton.getText().equals("YOU"))
			{
				mCView.mCamera.takePicture(null, null, mPicture);
				//bBackDone= true;
				
				 ShutButton.setText("ME");
				
				//cameraView.ReleaseCamera();
				SystemClock.sleep(1000);
			}
			else
			{
				//mCView.mCamera.takePicture(null, null, mPicture);
				ShutButton.setText("-->");
			}
		}
	}

}
