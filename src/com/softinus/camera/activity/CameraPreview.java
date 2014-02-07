package com.softinus.camera.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

import com.softinus.camera.R;

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
    private Button SettingButton;
    private Button GalleryButton;
    
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
				// 뒤 카메라 메모리를 제거한다.
				mCView1.ReleaseCamera();
				mLayout.removeAllViews();
				
				// 이미지를 세팅한다.
				mImgPreview1.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 0.5f));
		        mBitmap1 = BitmapFactory.decodeByteArray(data, 0, data.length);
				
				// 비트맵 리샘플링, 로테이트 후 다시 적용.
		        mBitmap1= ImageComposeMatrix(mBitmap1, 1000, 1000, 90);
		        //mBitmap1= ImageComposeMatrix(mBitmap1, true, 2, 90);		
				mImgPreview1.setImageBitmap(mBitmap1);
				//mImgPreview1.setScaleType(ImageView.ScaleType.CENTER); 
				mImgPreview1.setBackgroundColor(Color.BLACK);
				mLayout.addView(mImgPreview1);
				
				// 두번째 앞 카메라를 연다. 
		        mCView2= new CameraView(mContext, true);
		        mCView2.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 0.5f));        
		        mLayout.addView(mCView2);
		        
		        ShutButton.setBackgroundResource(R.drawable.btn_camera_down);
		        ShutButton.setText("");
		        ShutButton.setTag("ME");
				ShutButton.setEnabled(true);
			}
    	};
    	
    	mPicture2= new PictureCallback()
        {
			@Override
			public void onPictureTaken(byte[] data, Camera camera) 
			{	
				// 앞 카메라 메모리를 제거한다.
				mCView2.ReleaseCamera();
				//mLayout.removeAllViews();
				mLayout.removeView(mCView2);
				
				// 이미지를 세팅한다.
				mImgPreview2.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 0.5f));
				mBitmap2= BitmapFactory.decodeByteArray(data, 0, data.length);
				
				// 비트맵 리샘플링, 로테이트 후 다시 적용.
				mBitmap2= ImageComposeMatrix(mBitmap2, 1000, 1000, -90);
				mImgPreview2.setImageBitmap(mBitmap2);
				mImgPreview2.setBackgroundColor(Color.BLACK);
				//mImgPreview2.setScaleType(ImageView.ScaleType.CENTER);
		        mLayout.addView(mImgPreview2);
		        
		        ShutButton.setText("");
				ShutButton.setTag("Save -->");
				ShutButton.setEnabled(true);
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
        
        mImgPreview2= new ImageView(this);
        mCView1.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 0.5f));
        mImgPreview2.setBackgroundColor(Color.BLACK);
        mLayout.addView(mImgPreview2);        
        
        ShutButton= (Button) findViewById(R.id.btn_click);        
        ShutButton.setOnClickListener(this);
        
        SettingButton= (Button) findViewById(R.id.btn_setting);
        SettingButton.setOnClickListener(this);
        
        GalleryButton= (Button) findViewById(R.id.btn_gallery);
        GalleryButton.setOnClickListener(this);
        
        ShutButton.setBackgroundResource(R.drawable.btn_camera_up);
        ShutButton.setText("");
        ShutButton.setTag("YOU");
    }
    
    /**
     * 이미지 조절
     * @param _targetBitmap : 대상 비트맵
     * @param _nScale : 사이즈 마이너스 배율
     * @param _nRotate : 회전
     * @return
     */
    private Bitmap ImageComposeMatrix(Bitmap _targetBitmap, boolean _bScale, int _nScale, int _nRotate)
    {
		// create a matrix for the manipulation 
		Matrix matrix = new Matrix(); 
		
		int width = _targetBitmap.getWidth(); 
		int height = _targetBitmap.getHeight(); 
		
    	if(_bScale)
    	{
    		int newWidth = width/_nScale;
    		int newHeight = height/_nScale;
    		 
    		// calculate the scale - in this case = 0.4f 
    		float scaleWidth = ((float) newWidth) / width; 
    		float scaleHeight = ((float) newHeight) / height;     		 

    		// resize the bit map 
    		matrix.postScale(scaleWidth, scaleHeight); 
    	}
		
		// rotate the Bitmap 
		matrix.postRotate(_nRotate); 
		 
		// recreate the new Bitmap 
		Bitmap resizedBitmap = Bitmap.createBitmap(_targetBitmap, 0, 0, 
		width, height, matrix, true); 
		 
		return resizedBitmap;
    }
    
	/**
	 * 이미지 조절2
	 * @param _targetBitmap : 고칠 이미지
	 * @param nWid : 수정될 가로 사이즈
	 * @param nHei : 수정될 세로 사이즈
	 * @param _nRotate : 회전 각도
	 * @return
	 */
    private Bitmap ImageComposeMatrix(Bitmap _targetBitmap, int nWid, int nHei, int _nRotate)
    {
		// create a matrix for the manipulation 
		Matrix matrix = new Matrix(); 
		
		int width = _targetBitmap.getWidth(); 
		int height = _targetBitmap.getHeight(); 
		
		// calculate the scale 
		float scaleWidth = ((float) nWid) / width; 
		float scaleHeight = ((float) nHei) / height;     		 

		// resize the bit map 
		matrix.postScale(scaleWidth, scaleHeight); 
		
		// rotate the Bitmap 
		matrix.postRotate(_nRotate); 
		 
		// recreate the new Bitmap 
		Bitmap resizedBitmap = Bitmap.createBitmap(_targetBitmap, 0, 0, 
		width, height, matrix, true); 
		 
		return resizedBitmap;
    }
    
    
    /**
     * 이미지 합친 후 저장
     * @param first : 첫번째 이미지
     * @param second : 두번째 이미지
     * @param isVerticalMode : 세로 모드인지
     * @param saveFileName : 저장할 이름 확장자 포함
     */
    private boolean CombineAndSaveImage(Bitmap first, Bitmap second, boolean isVerticalMode, String saveFileName)
    {
    	Options option = new Options();
    	option.inDither = true;
    	option.inPurgeable = true;

    	Bitmap bitmap = null;
    	if(isVerticalMode)
    		bitmap = Bitmap.createScaledBitmap(first, first.getWidth(), first.getHeight()+second.getHeight(), true);
    	else
    		bitmap = Bitmap.createScaledBitmap(first, first.getWidth()+second.getWidth(), first.getHeight(), true);

    	Paint p = new Paint();
    	p.setDither(true);
    	p.setFlags(Paint.ANTI_ALIAS_FLAG);

    	Canvas c = new Canvas(bitmap);
    	c.drawBitmap(first, 0, 0, p);
    	if(isVerticalMode)
    		c.drawBitmap(second, 0, first.getHeight(), p);
    	else
    		c.drawBitmap(second, first.getWidth(), 0, p);

    	first.recycle();
    	second.recycle();
    	
    	
    	String path = Environment.getExternalStorageDirectory().getAbsolutePath();
    	File dir = new File(path, "Opcam");    	
    	
    	if(!dir.exists())
    		dir.mkdir();
    	
    	
    	File file = new File(dir + "/" + saveFileName);

    	try
    	{
    		FileOutputStream fos = new FileOutputStream(file);
    		bitmap.compress(CompressFormat.JPEG, 100, fos);

    		fos.close();
    		bitmap.recycle();
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    		return false;
		}
    	return true;
    }
    

    

	@Override
	public void onClick(View v)
	{
		if(v.getId() == R.id.btn_click)
		{
			if(ShutButton.getTag().equals("YOU"))
			{
				mCView1.mCamera.takePicture(null, null, mPicture1);
				ShutButton.setText("Loading...");
				ShutButton.setEnabled(false);
			}
			else if(ShutButton.getTag().equals("ME"))
			{	
				mCView2.mCamera.takePicture(null, null, mPicture2);
				
				ShutButton.setText("Loading...");
				ShutButton.setEnabled(false);
				
			}
			else
			{
				ShutButton.setText("Loading...");
				ShutButton.setEnabled(false);
				
				Bitmap mBitmapCopy1 = mBitmap1.copy(Config.ARGB_8888, true);
				Bitmap mBitmapCopy2 = mBitmap2.copy(Config.ARGB_8888, true);
				
				Calendar cal= Calendar.getInstance();
				SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMddHHmmssSSS");
				String strDate= SDF.format(cal.getTime());
				
				if( CombineAndSaveImage(mBitmapCopy1, mBitmapCopy2, true, "opcam_"+strDate+".jpg") )
				{
					Toast.makeText(this, "사진 저장 완료.", Toast.LENGTH_SHORT).show();				
				}
				else
					Toast.makeText(this, "사진 저장 실패.", Toast.LENGTH_SHORT).show();
				
				mBitmapCopy1= null;
				mBitmapCopy2= null;
				mBitmap1= null;
				mBitmap2= null;
				
				// 레이아웃 삭제.
				mLayout.removeAllViews();
				
				mCView1= new CameraView(this, false);
		        mCView1.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 0.5f));        
		        mLayout.addView(mCView1);
				
		        mImgPreview2= new ImageView(this);
		        mCView1.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 0.5f));
		        mImgPreview2.setBackgroundColor(Color.BLACK);
		        mLayout.addView(mImgPreview2);
		        
		        ShutButton.setText("");
		        ShutButton.setTag("YOU");
		        ShutButton.setEnabled(true);
			}
		}
		else if(v.getId() == R.id.btn_setting)
		{
			Intent intent= new Intent(CameraPreview.this, SettingsActivity.class);
			startActivity(intent);
		}
		else if(v.getId() == R.id.btn_gallery)
		{
			Intent intent= new Intent(CameraPreview.this, GalleryActivity.class);
			startActivity(intent);
		}
	}

}
