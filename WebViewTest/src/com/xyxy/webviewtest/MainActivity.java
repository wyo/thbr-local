package com.xyxy.webviewtest;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends Activity {

	private List<String> listBarcode= new ArrayList<String>();
	
	private Intent intent;
	
	private ImageButton goBackward;
	private ImageButton goForward;
	private ImageButton go;
	private EditText editUri;
	private ProgressBar progressBar;
	private MyThread myThread;
	
	private WebView webView;
	
//	volatile boolean flag = false;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		editUri = (EditText)findViewById(R.id.editUri);
		goBackward = (ImageButton)findViewById(R.id.backward);
		goForward = (ImageButton)findViewById(R.id.forward);
		go = (ImageButton)findViewById(R.id.go);
		progressBar = (ProgressBar)findViewById(R.id.progressBar);
		
		webView = (WebView)findViewById(R.id.WebView01);
		
		editUri.setText("http://192.168.1.107:8046/default.aspx");
		
		//确定按钮，前往指定的uri
		go.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String strUri = editUri.getText().toString();
				webView.loadUrl(strUri);
				Toast.makeText(MainActivity.this, "正在载入："+ strUri, Toast.LENGTH_LONG).show();
			}
		});
		
		//后退，返回上一页
		goBackward.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(webView.canGoBack()){
					webView.goBack();
					editUri.setText(webView.getUrl().toString());
				}
			}
		});
		
		//前进，前进到下一页
		goForward.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(webView.canGoForward()){
					webView.goForward();
					editUri.setText(webView.getUrl().toString());
				}
			}
		});
		
		webView.setWebViewClient(new MyWebViewClient());
		
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setAllowFileAccess(true);
		
		webView.setWebChromeClient(new WebChromeClient(){

			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				// TODO Auto-generated method stub
				super.onProgressChanged(view, newProgress);
				setTitle("页面加载中，请稍候……" + newProgress + "%");
				//setProgress(newProgress * 100);
				/*if(newProgress == 0){
					progressBar.setVisibility(View.VISIBLE);
				}*/
				progressBar.setProgress(newProgress);
				progressBar.postInvalidate();
				if(newProgress == 100){
					setTitle(R.string.app_name);
				}
			}
			
		});
		
		
		webView.loadUrl("http://192.168.1.107:8046/default.aspx");
		
		webView.addJavascriptInterface(new JavaInterfaceClass(), "android");
		
		intent = new Intent(MainActivity.this,ZBarTest.class);
		
	}
	
	//重写WebViewClient类，由WebView自己来处理uri跳转
	private class MyWebViewClient extends WebViewClient{

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			// TODO Auto-generated method stub
			view.loadUrl(url);
			editUri.setText(url);
			return true;
		}
		
	}
	
	//重写返回键功能，返回上一页
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		
		if((keyCode == KeyEvent.KEYCODE_BACK)&&(webView.canGoBack())){
			webView.goBack();
			editUri.setText(webView.getUrl().toString());
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	//通过intent启动二维码扫描程序
	private void showCamera(){
		try{
			startActivityForResult(intent, 0);
		}catch(Exception e){
			Log.e("MainActivity", e.getMessage());
			Toast.makeText(MainActivity.this, 
					"Camera Method Not Found", Toast.LENGTH_LONG).show();
		}
		
		
	}
	
	
	//通过intent返回ZBarTest activity中的扫描结果
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(resultCode == RESULT_OK){
			listBarcode = data.getStringArrayListExtra("Result");
			synchronized (myThread) {
				myThread.notifyAll();
			}
			/*if(flag){
				Log.e("MainActivity", "true");
			}*/
			Iterator<String> iterator = listBarcode.iterator();
			while(iterator.hasNext()){
				String s = iterator.next();
				Log.e("MainActivity", s);
			}
		}
		
	}
	
	class MyThread extends Thread{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			MainActivity.this.showCamera();
		}
	}
	
	//定义javascript类来接收js事件调用的函数
	class JavaInterfaceClass{
		public List<String> showToast(){
			
			myThread = new MyThread();
			
			myThread.start();
			
			synchronized (myThread) {
				try {
					myThread.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			/*synchronized (listBarcode) {
					try {
						listBarcode.wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				if(!flag){
					Log.e("MainActivity", "false");
			}*/
			return listBarcode;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		
		menu.add(1, Menu.FIRST + 1, 1, "退出");
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		
		if(item.getItemId() == (Menu.FIRST + 1)){
			finish();
		}
		
		return true;
	}
	
}


