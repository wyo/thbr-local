package com.xyxy.webviewtest;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.webkit.WebView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		WebView webView = (WebView)findViewById(R.id.WebView01);
		
		webView.loadUrl("http://192.168.1.107:8046/default.aspx");
		
		webView.addJavascriptInterface(new JavaScriptinterface(this), "android");
		
		webView.getSettings().setJavaScriptEnabled(true);
		
		//webView.loa
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
