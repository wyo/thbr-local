package com.xyxy.webviewtest;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;
import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ZBarTest extends Activity implements SurfaceHolder.Callback{

	private Camera mCamera;
	private SurfaceHolder holder;
	private SurfaceView surfaceView;
	private Handler autoFocusHandler;
	
	ImageScanner scanner; //调用ZBar ImageScanner
	
	//private String result = "";
	
	private List<String> listResult = new ArrayList<String>();
	
	private boolean barcodeSacnned = false;
	private boolean previewing = true;
	
	private Button btnRescan;
	
	private TextView txtResult;
	
	private Button btnCancle;
	
	private Button btnCommit;
	
	private Intent intent;
	
	static {
        System.loadLibrary("iconv");
    } 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.preview_main);
		
		surfaceView = (SurfaceView)findViewById(R.id.surfaceView01);
		txtResult = (TextView)findViewById(R.id.txtResult);
		btnRescan = (Button)findViewById(R.id.Rescan);
		btnCancle = (Button)findViewById(R.id.btnCancle);
		btnCommit = (Button)findViewById(R.id.btnCommit);
		
		holder = surfaceView.getHolder();
		
		autoFocusHandler = new Handler();
		mCamera = ZBarTest.getCameraInstance();
		
		holder.addCallback(this);
		
		scanner = new ImageScanner();
		scanner.setConfig(0, Config.X_DENSITY, 3);
		scanner.setConfig(0, Config.Y_DENSITY, 3);
		
		intent = new Intent();
		listResult.clear();
		
		btnRescan.setOnClickListener(new OnClickListener() { //重新启动
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(barcodeSacnned){
					barcodeSacnned = false;
					txtResult.setText("Scanning...");
					mCamera.setPreviewCallback(previewCB);
					mCamera.startPreview();
					previewing = true;
					mCamera.autoFocus(autoFocusCB);
				}
			}
		});
		
		btnCancle.setOnClickListener(new OnClickListener() {//取消
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				cancle();
			}
		});
		
		btnCommit.setOnClickListener(new OnClickListener() {//确认返回
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				intent.putStringArrayListExtra("Result", (ArrayList<String>) listResult);
				ZBarTest.this.setResult(RESULT_OK, intent);
				onPause();
				finish();
			}
		});
		
	}
	
	private void cancle(){
		listResult.clear();
		intent.putStringArrayListExtra("Result", (ArrayList<String>) listResult);
		ZBarTest.this.setResult(RESULT_OK, intent);
		onPause();
		finish();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		releaseCamera();
		super.onPause();
	}
	

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		
		if(keyCode == KeyEvent.KEYCODE_BACK){
			cancle();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		releaseCamera();
		super.onDestroy();
	}

	//a safe way to init camera
	private static Camera getCameraInstance(){
		
		Camera c = null;
		
		try{
			c = Camera.open();
		}catch (Exception e){
			
		}
		
		return c;
		
	}
	
	private void releaseCamera(){
		if(mCamera != null){
			autoFocusHandler.removeCallbacks(doAutoFocus);
			previewing = false;
			mCamera.setPreviewCallback(null);
			mCamera.release();
			mCamera = null;
		}
	}
	
	private Runnable doAutoFocus = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(previewing)
			{
				mCamera.autoFocus(autoFocusCB);
			}
		}
	};
	
	AutoFocusCallback autoFocusCB = new AutoFocusCallback() {
		
		@Override
		public void onAutoFocus(boolean success, Camera camera) {
			// TODO Auto-generated method stub
				autoFocusHandler.postDelayed(doAutoFocus, 500);
		}
	};
	
	PreviewCallback previewCB = new PreviewCallback() {
		
		@Override
		public void onPreviewFrame(byte[] data, Camera camera) {
			// TODO Auto-generated method stub
			Camera.Parameters params = mCamera.getParameters();
			Size size = params.getPreviewSize();
			
			Image barcode = new Image(size.width, size.height, "Y800");
			barcode.setData(data);
			
			int scanResult = scanner.scanImage(barcode);
			
			if(scanResult != 0){
				previewing = false;
				mCamera.setPreviewCallback(null);
				mCamera.stopPreview();
				
				StringBuilder string = new StringBuilder();
				string.append("");
				SymbolSet syms = scanner.getResults();
				//result = scanner.getResults().toString();
				for(Symbol sym : syms){
					txtResult.setText("Barcode: " + sym.getData());
					string.append(sym.getData());
					barcodeSacnned = true;
				}
				
				listResult.add(string.toString());
				
				
				/*intent.putExtra("Result", result);
				ZBarTest.this.setResult(RESULT_OK, intent);*/
				//finish();
				
			}
		}
	};
	
	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		if(holder.getSurface() == null){
			return;
		}
		
		try{
			mCamera.stopPreview();
		}catch(Exception e){
			
		}
		
		try{
			mCamera.setDisplayOrientation(90);
			mCamera.setPreviewDisplay(holder);
			mCamera.setPreviewCallback(previewCB);
			mCamera.startPreview();
			mCamera.autoFocus(autoFocusCB);
		}catch(Exception e){
			Log.e("ZBar", "Error int starting camera preview:" + e.getMessage());
		}
	}

	
	
	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		try{
			mCamera.setPreviewDisplay(holder);
			//mCamera.startPreview();
		}catch (Exception e){
			Log.e("ZBar", "Error in setting preview:" + e.getMessage());
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		this.releaseCamera();
	}
	
}
