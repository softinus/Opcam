package com.raimsoft.camera;

import java.io.IOException;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


@SuppressLint("NewApi")
class CameraView extends SurfaceView implements SurfaceHolder.Callback
{
    SurfaceHolder mHolder;
    Camera mCamera;
    
    static boolean bAlreadyCreated= false;
    boolean bFrontCamera= false;
    
    public CameraView(Context context)
    {
        super(context);
        
        // SurfaceHolder.Callback을 설정함으로써 Surface가 생성/소멸되었음을
        // 알 수 있습니다.
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }
    
    public CameraView(Context context, boolean _bFrontCamera)
    {
        super(context);
        
        // SurfaceHolder.Callback을 설정함으로써 Surface가 생성/소멸되었음을
        // 알 수 있습니다.
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        
        this.bFrontCamera= _bFrontCamera;
    }

    /**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public CameraView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
        // SurfaceHolder.Callback을 설정함으로써 Surface가 생성/소멸되었음을
        // 알 수 있습니다.
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public CameraView(Context context, AttributeSet attrs) {
		super(context, attrs);
        // SurfaceHolder.Callback을 설정함으로써 Surface가 생성/소멸되었음을
        // 알 수 있습니다.
        mHolder = getHolder();  
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}
	
	public void surfaceCreated(SurfaceHolder holder)
    {
//		if(bAlreadyCreated)
//			return;
//		else
//			bAlreadyCreated= true;
		
        // Surface가 생성되었다면, 카메라의 인스턴스를 받아온 후 카메라의
        // Preview 를 표시할 위치를 설정합니다.
    	
		if(bFrontCamera)	// 앞뒤구분..
		{
			mCamera= openFrontFacingCameraGingerbread();
		}
		else
		{
			mCamera= openBackFacingCameraGingerbread();			
		}
		
    	
    	
    	mCamera.setDisplayOrientation(90);
    	
        try
        {
           mCamera.setPreviewDisplay(mHolder);
        } catch (IOException exception)
        {
            mCamera.release();
            mCamera = null;
            // TODO: add more exception handling logic here
        }
    }
    
    /**
     * 카메라 닫음
     */
    void ReleaseCamera()
    {
    	if (mCamera != null)
    	{
    		mCamera.stopPreview();
    		mCamera.release();
    		mCamera = null;
        }
    	
    }

    public void surfaceDestroyed(SurfaceHolder holder)
    {
        // 다른 화면으로 돌아가면, Surface가 소멸됩니다. 따라서 카메라의 Preview도 
        // 중지해야 합니다. 카메라는 공유할 수 있는 자원이 아니기에, 사용하지 않을
        // 경우 -액티비티가 일시정지 상태가 된 경우 등 - 자원을 반환해야합니다.
        mCamera.stopPreview();
        mCamera = null;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h)
    {
        // 표시할 영역의 크기를 알았으므로 해당 크기로 Preview를 시작합니다.
        Camera.Parameters parameters = mCamera.getParameters();
        //parameters.set("orientation", "portrait");
        //parameters.setPreviewSize(480, 800);
        //parameters.setPreviewSize(1080, 1920);
        //mCamera.setParameters(parameters);
        mCamera.startPreview();
    }
    
   
    
    private Camera openFrontFacingCameraGingerbread()
    {
        Camera cam = null;
        try
        {
            cam = Camera.open( GetFrontCameraIdx() );
        } catch (RuntimeException e) {
            Log.e("CAMERA", "Camera failed to open: " + e.getLocalizedMessage());
        }
     
        return cam;
    }
    
    private Camera openBackFacingCameraGingerbread()
    {
        Camera cam = null;
        try
        {
            cam = Camera.open( GetBackCameraIdx() );
        } catch (RuntimeException e) {
            Log.e("CAMERA", "Camera failed to open: " + e.getLocalizedMessage());
        }
     
        return cam;
    }
	
	private int GetFrontCameraIdx()
    {
    	int cameraCount = 0;
        Camera cam = null;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();
        for ( int camIdx = 0; camIdx < cameraCount; camIdx++ )
        {
            Camera.getCameraInfo( camIdx, cameraInfo );
            if ( cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT )
            //if ( cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK )
            {
            	return camIdx;
            }
        }
        return 0;
    }
    
	private int GetBackCameraIdx()
    {
    	int cameraCount = 0;
        Camera cam = null;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();
        for ( int camIdx = 0; camIdx < cameraCount; camIdx++ )
        {
            Camera.getCameraInfo( camIdx, cameraInfo );
            //if ( cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT )
            if ( cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK )
            {
            	return camIdx;
            }
        }
        return 0;
    }
}