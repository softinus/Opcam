package us.opcam.camera.activity;

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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore.Images;
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
	
	//SharedPreferences prefs= null;	// shared preference
    
    @Override
	protected void onCreate(Bundle savedInstanceState)
    {
    	mContext= this.getApplicationContext();
    	String myID= SPUtil.getString(mContext, "my_id");
    	
    	if(myID == null)
    	{
    		Intent intent= new Intent(CameraPreview2.this, LoginActivity.class);
        	startActivity(intent);
    	}
    	
    	
    	
        super.onCreate(savedInstanceState);
        
        
        //prefs= PreferenceManager.getDefaultSharedPreferences(mContext);
        
        
        mPicture1= new PictureCallback()
        {
			@Override
			public void onPictureTaken(byte[] data, Camera camera)
			{					
				// �� ī�޶� �޸𸮸� �����Ѵ�.
				mCView1.ReleaseCamera();
				mCameraLayout.removeAllViews();
				mCoverLayout.removeAllViews();
				
				// �̹����� �����Ѵ�.
				mImgPreview1.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 0.5f));
				mImgPreview2.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 0.5f));
		        mBitmap1 = BitmapFactory.decodeByteArray(data, 0, data.length);
				
				// ��Ʈ�� �����ø�, ������Ʈ �� �ٽ� ����.
		        mBitmap1= ImageComposeMatrixDevide2(mBitmap1, 90, 1000,1580, true);
		        //mBitmap1= ImageComposeMatrix(mBitmap1, true, 2, 90);		
				mImgPreview1.setImageBitmap(mBitmap1);
				//mImgPreview1.setScaleType(ImageView.ScaleType.CENTER); 
				mImgPreview1.setBackgroundColor(Color.BLACK);
				
				mImgPreview1.setVisibility(View.VISIBLE);
				mImgPreview2.setVisibility(View.INVISIBLE);
				
				mCoverLayout.addView(mImgPreview1);
				mCoverLayout.addView(mImgPreview2);
				
				// �ι�° �� ī�޶� ����. 
		        mCView2= new CameraView(mContext, true);
		        mCView2.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 1.0f));        
		        mCameraLayout.addView(mCView2);
		        
		        
		        
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
				// �� ī�޶� �޸𸮸� �����Ѵ�.
				mCView2.ReleaseCamera();
				mCameraLayout.removeAllViews();
				
				// �̹����� �����Ѵ�.
				mImgPreview1.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 0.5f));
				mImgPreview2.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 0.5f));
		        mBitmap2 = BitmapFactory.decodeByteArray(data, 0, data.length);
				
				// ��Ʈ�� �����ø�, ������Ʈ �� �ٽ� ����.
		        mBitmap2= ImageComposeMatrixDevide2(mBitmap2, -90, 1000,1580, false);	
				mImgPreview2.setImageBitmap(mBitmap2); 
				mImgPreview2.setBackgroundColor(Color.BLACK);
				
				mImgPreview2.setVisibility(View.VISIBLE);
				
				ShutButton.setEnabled(false);
				
	            bSkipEffect= SPUtil.getBoolean(mContext, "process_skip_effect_step"); // Ű��, ����Ʈ��
	            bSkipShare= SPUtil.getBoolean(mContext, "process_skip_share_step"); // Ű��, ����Ʈ��
	            
	            uCurrentPhoto= SaveBitmapFile();
	            
	            //GotoAviary(uCurrentPhoto);
	            if(bSkipEffect)	// effect ����,
	            {
	            	if(!bSkipShare)	// share �����ϸ�
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
	            	// ���� ���ļ� �����ϰ� Aviary ���� �������� �Ѿ.
	            	GotoAviary(uCurrentPhoto);	            	
	            }
				
				
				// ������ �ٽ� ���� �� �ֵ��� �ʱ�ȭ
				RefreshAllView();
			}
    	};
        
        // Hide the window title.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.activity_camera2);
        mCoverLayout = (LinearLayout) findViewById(R.id.cover_layout);	// �� Ŀ��
        mCameraLayout = (LinearLayout) findViewById(R.id.camera_layout);// ī�޶�
        //mCView= (CameraView) findViewById(R.id.camera_preview1);
        
        mImgPreview1= new ImageView(this);
        mImgPreview2= new ImageView(this);
        
        this.RefreshAllView();
    }
    


	@Override
	public void onClick(View v)
	{
		if(v.getId() == R.id.btn_click)
		{
			if(ShutButton.getTag().equals("YOU"))
			{
				mCView1.mCamera.takePicture(null, null, mPicture1);
				//ShutButton.setText("Loading...");
				ShutButton.setEnabled(false);
			}
			else if(ShutButton.getTag().equals("ME"))
			{	
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
			Intent intent= new Intent(CameraPreview2.this, GalleryTabsActivity.class);
			startActivity(intent);
		}
	}



	private void RefreshAllView()
	{
		mCameraLayout.removeAllViews();
		mCoverLayout.removeAllViews();
		
        mCView1= new CameraView(this, false);	// �ĸ� ī�޶�� 100% ���Ͼ�.
        mCView1.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 1.0f));        
        mCameraLayout.addView(mCView1);
        
        mImgPreview1= new ImageView(this);	// �̹��� 50%
        mImgPreview1.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 0.5f));
        mImgPreview1.setBackgroundColor(Color.BLACK);
        mImgPreview1.setVisibility(View.INVISIBLE);

        mImgPreview2= new ImageView(this);	// Ŀ�� 50%
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
	
	// ���ļ� �������� ������.
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
		
		int nCount= SPUtil.getInt(mContext, "count");
		
		if(nCount == -1)
		{
			SPUtil.putInt(mContext, "count", 1);
			nCount= 0;
		}
		++nCount;
		SPUtil.putInt(mContext, "count", nCount);
		strSaveFileName= "opcam_"+ nCount +"_"+ strDate +".jpg";
		
		
//		while(true)	// ������ opcam_20140428_1.jpg ������ ������.
//		{
//			++nCount;
//			String strCount= nCount+"";
//			strSaveFileName= "opcam_"+ strDate +"_"+ strCount +".jpg";
//			
//			File file= new File("sdcard/opcam/" + strSaveFileName);
//			if(!file.exists())	// �ش� ���� �̸��� ������ ������.
//				break;
//		}
		
		
		if( CombineAndSaveImage(mBitmapCopy1, mBitmapCopy2, true, strSaveFileName) )
		{
			Toast.makeText(this, "Save file successfully.", Toast.LENGTH_SHORT).show();
			
			File dir=new File(Environment.getExternalStorageDirectory(),"/opcam/"+strSaveFileName);			
			uRes= Uri.fromFile(dir);
		}
		mBitmapCopy1= null;
		mBitmapCopy2= null;
		mBitmap1= null;
		mBitmap2= null;
		
		return uRes;
	}
	
	// �ش� Uri�� Aviary�� ������.
	private void GotoAviary(Uri uPath)
	{
		Intent newIntent = new Intent( this, FeatherActivity.class );
		newIntent.setData( uPath );
		newIntent.putExtra( Constants.EXTRA_IN_API_KEY_SECRET, "f5d04d92e56534ce" );
		startActivityForResult( newIntent, 1 );
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
	            case 1:
	                // output image path
	                Uri mImageUri = data.getData();
	                Bundle extra = data.getExtras();
	                    if( null != extra ) 
	                    {
	                        // image has been changed by the user?
	                        boolean changed = extra.getBoolean( Constants.EXTRA_OUT_BITMAP_CHANGED );
	                      
	                        if(changed)
	                        	OverwriteBitmap(mImageUri);
	                        
	                        if(!bSkipShare)	// share�� ��ŵ���� ������
	                        	GotoShareActivity(uCurrentPhoto, extra);
	                    }
	                break;
	        }
	    }
	}




	
	
    
    /**
     * �̹��� ����
     * @param _targetBitmap : ��� ��Ʈ��
     * @param _nScale : ������ ���̳ʽ� ����
     * @param _nRotate : ȸ��
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
	 * �̹��� ����2
	 * @param _targetBitmap : ��ĥ �̹���
	 * @param nWid : ������ ���� ������
	 * @param nHei : ������ ���� ������
	 * @param _nRotate : ȸ�� ����
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
	 * �̹��� ����3
	 * @param _targetBitmap : ��ĥ �̹���
	 * @param nWid : ������ ���� ������
	 * @param nHei : ������ ���� ������
	 * @param _nRotate : ȸ�� ����
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
     * �̹��� ��ģ �� ����
     * @param first : ù��° �̹���
     * @param second : �ι�° �̹���
     * @param isVerticalMode : ���� �������
     * @param saveFileName : ������ �̸� Ȯ���� ����
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


