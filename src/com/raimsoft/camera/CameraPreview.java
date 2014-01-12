package com.raimsoft.camera;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
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
    private Bitmap mBitmap1;
    private Bitmap mBitmap2;
    private ImageView mImgPreview1;
    private ImageView mImgPreview2;
    
    private Button ShutButton;
	private PictureCallback mPicture1;
	private PictureCallback mPicture2;
	
	private Context mContext;
//    private boolean bBackDone= false;
//    private boolean bFrontDone= false;
    
    @Override
	protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        mContext= this.getApplicationContext();
        
        
        mPicture1= new PictureCallback()
        {
			@Override
			public void onPictureTaken(byte[] data, Camera camera)
			{					
				// �� ī�޶� �޸𸮸� �����Ѵ�.
				mCView1.ReleaseCamera();
				mLayout.removeAllViews();
				
				// �̹����� �����Ѵ�.
				mImgPreview1.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 0.5f));        
		        mLayout.addView(mImgPreview1);
		        
		        mBitmap1 = BitmapFactory.decodeByteArray(data, 0, data.length);
				mImgPreview1.setImageBitmap(mBitmap1);				
				
				// �ι�° �� ī�޶� ����. 
		        mCView2= new CameraView(mContext, true);
		        mCView2.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 0.5f));        
		        mLayout.addView(mCView2);
			}
    	};
    	
    	mPicture2= new PictureCallback()
        {
			@Override
			public void onPictureTaken(byte[] data, Camera camera) 
			{	
				// �� ī�޶� �޸𸮸� �����Ѵ�.
				mCView2.ReleaseCamera();
				//mLayout.removeAllViews();
				mLayout.removeView(mCView2);
				
				// �̹����� �����Ѵ�.
//				mImgPreview1.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 0.5f));
//				Bitmap bitmap1 = BitmapFactory.decodeByteArray(mData1, 0, mData1.length);
//				mImgPreview1.setImageBitmap(bitmap1);	
//				mLayout.addView(mImgPreview1);
				
				// �̹����� �����Ѵ�.
				mImgPreview2.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 0.5f));
				mBitmap2= BitmapFactory.decodeByteArray(data, 0, data.length);
				mImgPreview2.setImageBitmap(mBitmap2);				        
		        mLayout.addView(mImgPreview2);
			}
    	};
        
        // Hide the window title.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    
        // Create our Preview view and set it as the content of our activity.
        //mCView = new CameraView(this);
        //setContentView(mCView);
        
        setContentView(R.layout.main);
        mLayout = (LinearLayout) findViewById(R.id.camera_layout);
        //mCView= (CameraView) findViewById(R.id.camera_preview1);
        
        mImgPreview1= new ImageView(this);
        mImgPreview2= new ImageView(this);
        
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
				mCView1.mCamera.takePicture(null, null, mPicture1);
				ShutButton.setText("ME");
			}
			else if(ShutButton.getText().equals("ME"))
			{	
				mCView2.mCamera.takePicture(null, null, mPicture2);
				ShutButton.setText("-->");
			}
			else
			{
			
			}
		}
	}

}
