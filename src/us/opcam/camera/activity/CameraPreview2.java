package us.opcam.camera.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import us.opcam.camera.R;
import us.opcam.camera.util.SPUtil;
import us.opcam.camera.view.CameraView;
import android.app.Activity;
import android.app.ProgressDialog;
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
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

import com.aviary.android.feather.FeatherActivity;
import com.aviary.android.feather.library.Constants;
import com.parse.ParseAnalytics;
import com.parse.ParseFile;
import com.parse.ParseObject;

public class CameraPreview2 extends Activity implements OnClickListener
{   
	private LinearLayout mCoverLayout;
	private LinearLayout mCameraLayout;
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
	private boolean bSkipEffect= false;
	private boolean bSkipShare= false;
	
	private Uri uCurrentPhoto= null;
	
	private ProgressDialog LoadingDL;	// 프로그레스 다이어로그
	

	//SharedPreferences prefs= null;	// shared preference
    
	// 로딩 프로그레스 부분을 모두 핸들러로 관리한다.
	public Handler LoadingHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			if(msg.what==0)
			{
				LoadingDL.hide();
			}
			if(msg.what==1)
			{
				LoadingDL.setMessage("Loading pictures...");
		        LoadingDL.show();
			}
			if(msg.what==2)
			{
				LoadingDL.setMessage("Loading...");
		        LoadingDL.show();
			}
		}
	};
		
    @Override
	protected void onCreate(Bundle savedInstanceState)
    {
		ParseAnalytics.trackAppOpened(getIntent());
		
    	mContext= this.getApplicationContext();
    	LoadingDL = new ProgressDialog(this);
    	
    	String myID= SPUtil.getString(mContext, us.opcam.camera.util.Constants.Extra.MY_EMAIL);
    	
    	if(myID == null)
    	{
    		Intent intent= new Intent(CameraPreview2.this, SignActivity.class);
        	startActivity(intent);
        	finish();
    	}
    	
    	
    	
    	
    	
        super.onCreate(savedInstanceState);
        
        
    	
    	LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
    	 
    	LocationListener locationListener = new LocationListener() {
    	    @Override
    	    public void onStatusChanged(String provider, int status, Bundle extras) {
    	    }
    	     
    	    @Override
    	    public void onProviderEnabled(String provider) {
    	    }
    	     
    	    @Override
    	    public void onProviderDisabled(String provider) {
    	    }
    	     
    	    @Override
    	    public void onLocationChanged(Location location) {
    	        Log.d("Location", location.toString());
    	        Toast.makeText(mContext, "Location"+ location.toString(), Toast.LENGTH_SHORT).show();
    	        
    	    }
    	};	 
    	//LocationProvider locationProvider = LocationManager.NETWORK_PROVIDER;
    	// 또는 GPS 데이터를 이용하고자 한다면..
    	// LocationProvider locationProvider = LocationManager.GPS_PROVIDER;
    	 
    	//locationManager.requestLocationUpdates(locationProvider, 0, 0, locationListener);	
    	locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    	
    	
    	
        
        //prefs= PreferenceManager.getDefaultSharedPreferences(mContext);
        
        
        mPicture1= new PictureCallback()
        {
			@Override
			public void onPictureTaken(byte[] data, Camera camera)
			{					
				// 뒤 카메라 메모리를 제거한다.
				mCView1.ReleaseCamera();
				mCameraLayout.removeAllViews();
				mCoverLayout.removeAllViews();
				
				// 이미지를 세팅한다.
				mImgPreview1.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 0.5f));
				mImgPreview2.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 0.5f));
		        mBitmap1 = BitmapFactory.decodeByteArray(data, 0, data.length);
				
				// 비트맵 리샘플링, 로테이트 후 다시 적용.
		        mBitmap1= ImageComposeMatrixDevide2(mBitmap1, 90, 1000,1580, true);
		        //mBitmap1= ImageComposeMatrix(mBitmap1, true, 2, 90);		
				mImgPreview1.setImageBitmap(mBitmap1);
				//mImgPreview1.setScaleType(ImageView.ScaleType.CENTER); 
				mImgPreview1.setBackgroundColor(Color.BLACK);
				
				mImgPreview1.setVisibility(View.VISIBLE);
				mImgPreview2.setVisibility(View.INVISIBLE);
				
				mCoverLayout.addView(mImgPreview1);
				mCoverLayout.addView(mImgPreview2);
				
				// 두번째 앞 카메라를 연다. 
		        mCView2= new CameraView(mContext, true);
		        mCView2.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 1.0f));        
		        mCameraLayout.addView(mCView2);
		        
		        
		        
		        ShutButton.setBackgroundResource(R.drawable.btn_camera_down);
		        ShutButton.setText("");
		        ShutButton.setTag("ME");
				ShutButton.setEnabled(true);
				
				LoadingHandler.sendEmptyMessage(0);
			}
    	};
    	
    	mPicture2= new PictureCallback()
        {
			@Override
			public void onPictureTaken(byte[] data, Camera camera) 
			{	
				// 뒤 카메라 메모리를 제거한다.
				mCView2.ReleaseCamera();
				mCameraLayout.removeAllViews();
				
				// 이미지를 세팅한다.
				mImgPreview1.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 0.5f));
				mImgPreview2.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 0.5f));
		        mBitmap2 = BitmapFactory.decodeByteArray(data, 0, data.length);
				
				// 비트맵 리샘플링, 로테이트 후 다시 적용.
		        mBitmap2= ImageComposeMatrixDevide2(mBitmap2, -90, 1000,1580, false);	
				mImgPreview2.setImageBitmap(mBitmap2); 
				mImgPreview2.setBackgroundColor(Color.BLACK);
				
				mImgPreview2.setVisibility(View.VISIBLE);
				
				ShutButton.setEnabled(false);
				
	            bSkipEffect= SPUtil.getBoolean(mContext, "process_skip_effect_step"); // 키값, 디폴트값
	            bSkipShare= SPUtil.getBoolean(mContext, "process_skip_share_step"); // 키값, 디폴트값
	            
	            uCurrentPhoto= SaveBitmapFile();
	            
	            	
	            
	            //GotoAviary(uCurrentPhoto);
	            if(bSkipEffect)	// effect 생략,
	            {	 
            		ShareToOpcamServer(null);	// 찍은 후 바로 올린다.
	            	if(!bSkipShare)	// share 생략안하면
	            	{
	            		GotoShareActivity(uCurrentPhoto, null);
	            	}
	            	else
	            	{
	            		//GotoAviary(uCurrentPhoto);
	            	}
	            }
	            else
	            {
	            	// 사진 합쳐서 저장하고 Aviary 사진 편집으로 넘어감.
	            	GotoAviary(uCurrentPhoto);	            	
	            }
				
				
				// 사진을 다시 찍을 수 있도록 초기화
				RefreshAllView();
				
				LoadingHandler.sendEmptyMessage(0);
			}
    	};
        
        // Hide the window title.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.activity_camera2);
        mCoverLayout = (LinearLayout) findViewById(R.id.cover_layout);	// 위 커버
        mCameraLayout = (LinearLayout) findViewById(R.id.camera_layout);// 카메라
        //mCView= (CameraView) findViewById(R.id.camera_preview1);
        
        mImgPreview1= new ImageView(this);
        mImgPreview2= new ImageView(this);
        
        this.RefreshAllView();
    }
    
	@Override
	protected void onResume()
	{
    	LoadingHandler.sendEmptyMessage(0);
    	
		super.onResume();
	}

	/**
	 * opcam server upload
	 */
	public void ShareToOpcamServer(Uri UploadTarget)
	{
		Uri uriForUpload= null;
		
		if(UploadTarget == null) // 지정 업로드 타겟이 없는데
		{
			if(uCurrentPhoto == null)	// 찍은 타겟도 없으면 안됨.
				return;
			else
				uriForUpload= uCurrentPhoto;	// 찍은걸로 올림.
		}
		else
			uriForUpload= UploadTarget;	// 지정이 있으면 찍은 것 대신에 지정 URI로 올림.	
		
		
		
		Bitmap bmp = null;
		try {
			bmp = Images.Media.getBitmap(getContentResolver(), uriForUpload);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ByteArrayOutputStream stream = new ByteArrayOutputStream() ;  
		bmp.compress( CompressFormat.JPEG, 100, stream) ;  
        byte[] byteArray = stream.toByteArray() ;  
		
		ParseFile file = new ParseFile("photo.jpg", byteArray);
		file.saveInBackground();
		
		ParseObject picApplication = new ParseObject("Pictures");
		picApplication.put("Name", SPUtil.getString(this, us.opcam.camera.util.Constants.Extra.MY_NICK));
		picApplication.put("Picture", file);
		picApplication.saveInBackground();
	}

	@Override
	public void onClick(View v)
	{
		if(v.getId() == R.id.btn_click)
		{
			if(ShutButton.getTag().equals("YOU"))
			{
				LoadingHandler.sendEmptyMessage(2);
				mCView1.mCamera.takePicture(null, null, mPicture1);
				//ShutButton.setText("Loading...");
				ShutButton.setEnabled(false);
			}
			else if(ShutButton.getTag().equals("ME"))
			{	
				LoadingHandler.sendEmptyMessage(2);
				mCView2.mCamera.takePicture(null, null, mPicture2);
				
				//ShutButton.setText("Loading...");
				ShutButton.setEnabled(false);
				
			}
		}
		else if(v.getId() == R.id.btn_setting)
		{
			Intent intent= new Intent(CameraPreview2.this, SettingsActivity.class);
			startActivity(intent);
			
			
		}
		else if(v.getId() == R.id.btn_gallery)
		{
			LoadingHandler.sendEmptyMessage(1);
			Intent intent= new Intent(CameraPreview2.this, GalleryTabsActivity.class);
			startActivityForResult(intent, us.opcam.camera.util.Constants.REQ.CAMERA_TO_GALLERY);
		}
	}



	private void RefreshAllView()
	{
		mCameraLayout.removeAllViews();
		mCoverLayout.removeAllViews();
		
        mCView1= new CameraView(this, false);	// 후면 카메라로 100% 리니어.
        mCView1.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 1.0f));        
        mCameraLayout.addView(mCView1);
        
        mImgPreview1= new ImageView(this);	// 이미지 50%
        mImgPreview1.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 0.5f));
        mImgPreview1.setBackgroundColor(Color.BLACK);
        mImgPreview1.setVisibility(View.INVISIBLE);

        mImgPreview2= new ImageView(this);	// 커버 50%
        mImgPreview2.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 0.5f));
        mImgPreview2.setBackgroundColor(Color.BLACK);
        mImgPreview2.setVisibility(View.VISIBLE);
        
        mCoverLayout.addView(mImgPreview1);
        mCoverLayout.addView(mImgPreview2);        
        
        ShutButton= (Button) findViewById(R.id.btn_click);        
        ShutButton.setOnClickListener(this);
        
        SettingButton= (Button) findViewById(R.id.btn_setting);
        SettingButton.setOnClickListener(this);
        
        GalleryButton= (Button) findViewById(R.id.btn_gallery);
        GalleryButton.setOnClickListener(this);
        
        ShutButton.setBackgroundResource(R.drawable.btn_camera_up);
        ShutButton.setText("");
        ShutButton.setTag("YOU");
        ShutButton.setEnabled(true);
		
		
	}
	
	// 합쳐서 사진으로 저장함.
	private Uri SaveBitmapFile()
	{
		Uri uRes= null;
		
		Bitmap mBitmapCopy1 = mBitmap1.copy(Config.ARGB_8888, true);
		Bitmap mBitmapCopy2 = mBitmap2.copy(Config.ARGB_8888, true);
		
		Calendar cal= Calendar.getInstance();
		//SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMdd");
		String strDate= SDF.format(cal.getTime());
		
		String strSaveFileName= "opcam_"+strDate+".jpg";
		
		File dir=new File(Environment.getExternalStorageDirectory(),"/opcam/");
		if(!dir.exists())
		{
			dir.mkdir();
		}
		int nCount= dir.list().length + 1;
		
//		int nCount= SPUtil.getInt(mContext, "count");		
//		if(nCount == -1)
//		{
//			SPUtil.putInt(mContext, "count", 1);
//			nCount= 0;
//		}
//		++nCount;
//		SPUtil.putInt(mContext, "count", nCount);
		strSaveFileName= "opcam_"+ nCount +"_"+ strDate +".jpg";
		
		
//		while(true)	// 파일을 opcam_20140428_1.jpg 식으로 저장함.
//		{
//			++nCount;
//			String strCount= nCount+"";
//			strSaveFileName= "opcam_"+ strDate +"_"+ strCount +".jpg";
//			
//			File file= new File("sdcard/opcam/" + strSaveFileName);
//			if(!file.exists())	// 해당 파일 이름이 없으면 저장함.
//				break;
//		}
		
		
		if( CombineAndSaveImage(mBitmapCopy1, mBitmapCopy2, true, strSaveFileName) )
		{
			Toast.makeText(this, "Save file successfully.", Toast.LENGTH_SHORT).show();
			
			File savefile=new File(Environment.getExternalStorageDirectory(),"/opcam/"+strSaveFileName);			
			uRes= Uri.fromFile(savefile);
		}
		mBitmapCopy1= null;
		mBitmapCopy2= null;
		mBitmap1= null;
		mBitmap2= null;
		
		return uRes;
	}
	
	// 해당 Uri를 Aviary로 전송함.
	private void GotoAviary(Uri uPath)
	{
		Intent newIntent = new Intent( this, FeatherActivity.class );
		newIntent.setData( uPath );
		newIntent.putExtra( Constants.EXTRA_IN_API_KEY_SECRET, "f5d04d92e56534ce" );
		startActivityForResult( newIntent, us.opcam.camera.util.Constants.REQ.CAMERA_TO_AVIARY );
	}

	private void GotoShareActivity(Uri mImageUri, Bundle extra)
	{
		Intent shareIntent = new Intent( this, SNSShareActivity.class );
		shareIntent.putExtra("contants", extra);
		shareIntent.putExtra("path", mImageUri);
		startActivity(shareIntent);
	}
	
	private void OverwriteBitmap(Uri modifyPhoto)
	{
    	try
    	{
			Bitmap bmpModify= Images.Media.getBitmap(getContentResolver(), modifyPhoto);
			File pCurrentFile= new File(uCurrentPhoto.getPath());
			
    		FileOutputStream fos = new FileOutputStream(pCurrentFile);
    		bmpModify.compress(CompressFormat.JPEG, 100, fos);

    		fos.close();
    		bmpModify.recycle();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch(Exception e) {
    		e.printStackTrace();
		}
	}
	
	@Override
	public void onActivityResult( int requestCode, int resultCode, Intent data )
	{
	    if( resultCode == RESULT_OK )
	    {
	        switch( requestCode ) 
	        {
	            case us.opcam.camera.util.Constants.REQ.CAMERA_TO_AVIARY:
	                // output image path
	                Uri mImageUri = data.getData();
	                Bundle extra = data.getExtras();
	                    if( null != extra ) 
	                    {
	                        // image has been changed by the user?
	                        boolean changed = extra.getBoolean( Constants.EXTRA_OUT_BITMAP_CHANGED );
	                      
	                        if(changed)
	                        	OverwriteBitmap(mImageUri);
	                        
	                        ShareToOpcamServer(mImageUri);	// Aviary 편집 된거 바로 올린다.
	                        
	                        if(!bSkipShare)	// share를 스킵하지 않으면
	                        	GotoShareActivity(uCurrentPhoto, extra);
	                    }
	                break;
	            case us.opcam.camera.util.Constants.REQ.CAMERA_TO_GALLERY:
	            	
	            	if(data == null)	// 삭제할 것이 없으면 그냥 넘어감.
	    				return;
	    			
	    			int Status= data.getIntExtra(us.opcam.camera.util.Constants.Extra.REFRESH_DISCOVER, -1);
	    			if(Status == -1)
	    			{
	    				us.opcam.camera.util.Constants.bRefreshed= false;
	    				
	    				LoadingHandler.sendEmptyMessage(1);
	    				Intent intent= new Intent(CameraPreview2.this, GalleryTabsActivity.class);
	    				startActivityForResult(intent, us.opcam.camera.util.Constants.REQ.CAMERA_TO_GALLERY);
	    			}

	                
	            	break;
	        }
	    }
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
	 * 이미지 조절3
	 * @param _targetBitmap : 고칠 이미지
	 * @param nWid : 수정될 가로 사이즈
	 * @param nHei : 수정될 세로 사이즈
	 * @param _nRotate : 회전 각도
	 * @return
	 */
    private Bitmap ImageComposeMatrixDevide2(Bitmap _targetBitmap,  int _nRotate, int nWid, int nHei, boolean bCropUpperSide)
    {
    	Bitmap modifyBitmap= null;
    	
		// create a matrix for the manipulation 
		Matrix matrix_Rotate = new Matrix();
		Matrix matrix_Scale = new Matrix();
		matrix_Rotate.postRotate(_nRotate);
		
		int width = _targetBitmap.getWidth(); 
		int height = _targetBitmap.getHeight();		
				 
		modifyBitmap= Bitmap.createBitmap(_targetBitmap, 0, 0, width, height, matrix_Rotate, true);
		
		int width2 = modifyBitmap.getWidth(); 
		int height2 = modifyBitmap.getHeight();
		if(bCropUpperSide)		
			modifyBitmap= Bitmap.createBitmap(modifyBitmap, 0, 0, width2, height2/2);
		else
			modifyBitmap= Bitmap.createBitmap(modifyBitmap, 0, height2/2, width2, height2/2);
		 
		
		// calculate the scale 
		float scaleWidth = ((float) nWid) / width2; 
		float scaleHeight = ((float) nHei) / height2;     		 

		// resize the bit map 
		matrix_Scale.postScale(scaleWidth, scaleHeight); 
		modifyBitmap= Bitmap.createBitmap(modifyBitmap, 0, 0, width2, height2/2, matrix_Scale, true);		
		 
		return modifyBitmap;
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
    		int nResolution= Integer.parseInt( SPUtil.getString(mContext, "option_resolutions_list_titles", "1") );
    		FileOutputStream fos = new FileOutputStream(file);
    		
    		int nQuality= 85;
    		switch(nResolution)
    		{
    		case 0:
    			nQuality= 70;
    			break;
    		case 1:
    			nQuality= 85;
    			break;
    		case 2:
    			nQuality= 100;
    			break;    			
    		}
    		bitmap.compress(CompressFormat.JPEG, nQuality, fos);

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
    

    

}


