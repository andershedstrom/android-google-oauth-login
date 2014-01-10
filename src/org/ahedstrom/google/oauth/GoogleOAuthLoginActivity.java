package org.ahedstrom.google.oauth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class GoogleOAuthLoginActivity extends Activity {
	
	private final static String TAG = "GoogleOAuthLoginActivity";
	
	public static final String EXTRA_IN_SCOPE = "scope";
	public static final String EXTRA_IN_REDIRECT_URI = "redirectUri";
	public static final String EXTRA_IN_CLIENT_ID = "clientId";
	
	public static final String EXTRA_OUT_ACCESS_TOKEN = "accessToken";
	public static final String EXTRA_OUT_EXPIRES_IN = "expiresIn";
	public static final String EXTRA_OUT_TOKEN_TYPE = "tokenType";
	public static final String EXTRA_OUT_REFRESH_TOKEN = "refreshToken";
	
	public static final int RESULT_OK = 1;
	public static final int RESULT_FAILURE = -1;

	private final static String urlTemplate = "https://accounts.google.com/o/oauth2/auth?scope=%s&redirect_uri=%s&response_type=code&client_id=%s";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_google_oauth_login);
		initWebView();
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void initWebView() {
		String url = createUrl();
		
		WebView webView = (WebView) findViewById(R.id.oauthWebView);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebChromeClient(new WebChromeClient(){

			@Override
			public void onReceivedTitle(WebView view, String title) {
				Log.d(TAG, "onReceivedTitle -> " + title);
				super.onReceivedTitle(view, title);
				if (title != null && title.contains("Success code=")) {
					view.loadUrl("about:blank");
					String[] split = title.split("=");
					if (split.length == 2) {
						String code = split[1];
						Log.d(TAG, "code -> " + code);
						
						AsyncHttpClient client = new AsyncHttpClient();
						RequestParams params = new RequestParams();
						params.put("code", code);
						params.put("client_id", getStringExtra(getIntent(), EXTRA_IN_CLIENT_ID));
						params.put("redirect_uri", getStringExtra(getIntent(), EXTRA_IN_REDIRECT_URI));
						params.put("grant_type", "authorization_code");
						
						client.post("https://accounts.google.com/o/oauth2/token", params, new JsonHttpResponseHandler(){
							@Override
							public void onSuccess(JSONObject json) {
								try {
									Log.d(TAG, "onSuccess -> " + json.toString());
									Intent data = new Intent();
									data.putExtra(EXTRA_OUT_ACCESS_TOKEN, json.getString("access_token"));
									data.putExtra(EXTRA_OUT_REFRESH_TOKEN, json.getString("refresh_token"));
									data.putExtra(EXTRA_OUT_EXPIRES_IN, json.getString("expires_in"));
									data.putExtra(EXTRA_OUT_TOKEN_TYPE, json.getString("token_type"));
									setResult(RESULT_OK, data);
								} catch (JSONException e) {
									Log.e(TAG, "onSuccess -> " + json.toString(), e);
									setResult(RESULT_FAILURE);
								} finally {
									finish();
								}
							}
							@Override
							public void onFailure(Throwable t) {
								handleFailure(t, "");
							}
							@Override
							public void onFailure(Throwable t, JSONArray jsonArray) {
								handleFailure(t, jsonArray.toString());
							}
							@Override
							public void onFailure(Throwable t, JSONObject jsonObject) {
								handleFailure(t, jsonObject.toString());
							}
							@Override
							public void onFailure(Throwable t, String str) {
								handleFailure(t, str);
							}
							private void handleFailure(Throwable t, String str) {
								Log.e(TAG, str, t);
								setResult(RESULT_FAILURE);
								finish();
							}
						});
					}
				}
			}
			
		});
		webView.setWebViewClient(new WebViewClient(){
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				Log.d(TAG, "shouldOverrideUrlLoading -> " + url);
				view.loadUrl(url);
				return true;
			}
		});
		
		webView.loadUrl(url);
	}

	private String createUrl() {
		Intent intent = getIntent();
		return String.format(urlTemplate, 
				getStringExtra(intent, EXTRA_IN_SCOPE, ""),
				getStringExtra(intent, EXTRA_IN_REDIRECT_URI),
				getStringExtra(intent, EXTRA_IN_CLIENT_ID));
	}

	@Override
	public Intent getIntent() {
		Intent intent = super.getIntent();
		if (intent == null) {
			throw new IllegalArgumentException("The activity must by started with an Intent");			
		}
		return intent;
	}
	
	private String getStringExtra(Intent intent, String name, String defaultValue) {
		String value = intent.getStringExtra(name);
		return value != null ? value : defaultValue;
	}

	private String getStringExtra(Intent intent, String name) {
		String value = intent.getStringExtra(name);
		if (value == null) {
			throw new IllegalArgumentException("Missing required extra data: " + name);			
		}
		return value;
	}
}
