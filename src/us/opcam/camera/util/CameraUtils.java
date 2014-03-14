package us.opcam.camera.util;

import android.annotation.SuppressLint;
import android.hardware.Camera;
import android.util.Log;

@SuppressLint("NewApi")
public class CameraUtils
{
	public Camera openFrontFacingCameraGingerbread()
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
    
    public Camera openBackFacingCameraGingerbread()
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
        return -1;
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
        return -1;
    }
}
