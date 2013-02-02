package com.xyxy.zxingtest;

/*import java.util.Timer;
import java.util.TimerTask;*/

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.os.Bundle;
import android.app.Activity;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {
	private SurfaceView sfvCamera;
	private SFHCamera sfhCamera;
	private ImageView imgView;
	private View centerView;
	private TextView txtScanResult;
	/*private Timer mTimer;
	private MyTimerTask mTimerTask;*/
	
	final static int width = 480;
	final static int height = 320;
	int dstLeft, dstTop, dstWidth, dstHeight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		this.setTitle("ZXingTest");
		
		imgView = (ImageView)findViewById(R.id.ImageView01);
		txtScanResult = (TextView)findViewById(R.id.txtScanResult);
		centerView = (View)findViewById(R.id.centerView);
		sfvCamera = (SurfaceView)findViewById(R.id.sfvCamera);
		sfhCamera = new SFHCamera(sfvCamera.getHolder(), width, height, previewCallback);
		
		/*mTimer = new Timer();
		mTimerTask = new MyTimerTask();
		mTimer.schedule(mTimerTask, 0, 80);*/
	}
	
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		
		if(dstLeft == 0){
			dstLeft = centerView.getLeft() * width/
					getWindowManager().getDefaultDisplay().getWidth();
			
			dstTop = centerView.getTop() * height/
					getWindowManager().getDefaultDisplay().getHeight();
			
			dstWidth = (centerView.getRight() - centerView.getLeft()) * width/
					getWindowManager().getDefaultDisplay().getWidth();
			
			dstHeight = (centerView.getBottom() - centerView.getTop()) * height/
					getWindowManager().getDefaultDisplay().getHeight();
		}
		
		sfhCamera.AutoFocusAndPreviewCallback();
		return super.onTouchEvent(event);
	}



/*	class MyTimerTask extends TimerTask{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(dstLeft == 0){
				dstLeft = centerView.getLeft() * width/
						getWindowManager().getDefaultDisplay().getWidth();
				
				dstTop = centerView.getTop() * height/
						getWindowManager().getDefaultDisplay().getHeight();
				
				dstWidth = (centerView.getRight() - centerView.getLeft()) * width/
						getWindowManager().getDefaultDisplay().getWidth();
				
				dstHeight = (centerView.getBottom() - centerView.getTop()) * height/
						getWindowManager().getDefaultDisplay().getHeight();
			}
			
			sfhCamera.AutoFocusAndPreviewCallback();
		}
		
	}
*/
	private Camera.PreviewCallback previewCallback = new PreviewCallback() {
		
		@Override
		public void onPreviewFrame(byte[] data, Camera camera) {
			// TODO Auto-generated method stub
			PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(
					data, width, height, dstLeft, dstTop, dstWidth, dstHeight, true);
			
			byte[] byteBitmap = source.renderCroppedGreyscaleBitmap();
			
			Bitmap mBitmap =BitmapFactory.decodeByteArray(byteBitmap, 0, byteBitmap.length); 
			
			imgView.setImageBitmap(mBitmap);
			
			BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
			
			MultiFormatReader reader = new MultiFormatReader();
			
			try{
				Result result = reader.decode(bitmap);
				String strResult = "BarcodeFormat: " + result.getBarcodeFormat().toString() + 
						" text: " + result.getText();
				txtScanResult.setText(strResult);
			}catch(Exception e){
				txtScanResult.setText("Scanning...");
			}
		}
	};
}
