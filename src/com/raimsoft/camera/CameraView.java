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
        
        // SurfaceHolder.Callback�� ���������ν� Surface�� ����/�Ҹ�Ǿ�����
        // �� �� �ֽ��ϴ�.
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }
    
    public CameraView(Context context, boolean _bFrontCamera)
    {
        super(context);
        
        // SurfaceHolder.Callback�� ���������ν� Surface�� ����/�Ҹ�Ǿ�����
        // �� �� �ֽ��ϴ�.
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
        // SurfaceHolder.Callback�� ���������ν� Surface�� ����/�Ҹ�Ǿ�����
        // �� �� �ֽ��ϴ�.
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
        // SurfaceHolder.Callback�� ���������ν� Surface�� ����/�Ҹ�Ǿ�����
        // �� �� �ֽ��ϴ�.
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
		
        // Surface�� �����Ǿ��ٸ�, ī�޶��� �ν��Ͻ��� �޾ƿ� �� ī�޶���
        // Preview �� ǥ���� ��ġ�� �����մϴ�.
    	
		if(bFrontCamera)	// �յڱ���..
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
     * ī�޶� ����
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
        // �ٸ� ȭ������ ���ư���, Surface�� �Ҹ�˴ϴ�. ���� ī�޶��� Preview�� 
        // �����ؾ� �մϴ�. ī�޶�� ������ �� �ִ� �ڿ��� �ƴϱ⿡, ������� ����
        // ��� -��Ƽ��Ƽ�� �Ͻ����� ���°� �� ��� �� - �ڿ��� ��ȯ�ؾ��մϴ�.
        mCamera.stopPreview();
        mCamera = null;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h)
    {
        // ǥ���� ������ ũ�⸦ �˾����Ƿ� �ش� ũ��� Preview�� �����մϴ�.
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