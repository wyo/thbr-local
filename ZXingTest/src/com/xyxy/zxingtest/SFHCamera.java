package com.xyxy.zxingtest;

import java.io.IOException;

import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;

public class SFHCamera implements Callback {
	
	private SurfaceHolder holder = null;
	private Camera mCamera;
	private int width, height;
	private Camera.PreviewCallback previewCallback;
	
	public SFHCamera(SurfaceHolder holder, int width, int height, Camera.PreviewCallback previewCallback)
	{
		this.holder = holder;
		this.holder.addCallback(this);
		this.holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		this.width = width;
		this.height = height;
		this.previewCallback = previewCallback;
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		Log.e("Camera", "SurfaceChanged");

	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		mCamera = Camera.open();
		
		Camera.Parameters params = mCamera.getParameters();
		params.setPreviewSize(width, height);
		mCamera.setParameters(params);
		
		try{
			mCamera.setPreviewDisplay(holder);
			Log.e("Camera", "SurfaceCreated");
		}catch (IOException e){
			mCamera.release();
			mCamera = null;
		}
		
		mCamera.startPreview();

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		
		if(mCamera != null)
		{
			mCamera.stopPreview();
		}
		mCamera.release();
		mCamera = null;
		Log.e("Camera", "SurfaceDestroyed");

	}
	
	public void AutoFocusAndPreviewCallback(){
		if(mCamera != null){
			mCamera.autoFocus(mAutofocusCallback);
		}
	}
	
	private Camera.AutoFocusCallback mAutofocusCallback = new AutoFocusCallback() {
		
		@Override
		public void onAutoFocus(boolean success, Camera camera) {
			// TODO Auto-generated method stub
			if(success){
				mCamera.setOneShotPreviewCallback(previewCallback);
			}
		}
	};
}
