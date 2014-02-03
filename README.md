android-google-oauth-login
==========================

Simple Android Library project with a single activity that handles Google's OAuth login


## Usage ##


Fork this project and check it out. Add it as Library project to your existing Android project where you would like to use it.

Then, add the following to your main `AndroidManifest.xml`

```
...
<application ...>
 ...
 <activity android:name="org.ahedstrom.google.oauth.GoogleOAuthLoginActivity" />
</application>

```

Then from the activity where you want to log in add the following code:

```
...

private static final String GOOGLE_API_CLIENT_ID = "your client id";
private static final String GOOGLE_API_REDIRECT_URI = "your redirect uri";
private static final String GOOGLE_API_SCOPES = "<scopes seperated by spaces>";

private static final String SIGN_IN_REQUEST_CODE = "1";


@Override
protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	Intent intent = new Intent(this, GoogleOAuthLoginActivity.class);
	intent.putExtra(GoogleOAuthLoginActivity.EXTRA_IN_CLIENT_ID, GOOGLE_API_CLIENT_ID);
	intent.putExtra(GoogleOAuthLoginActivity.EXTRA_IN_REDIRECT_URI, GOOGLE_API_REDIRECT_URI);
	intent.putExtra(GoogleOAuthLoginActivity.EXTRA_IN_SCOPE, GOOGLE_API_SCOPES);
	startActivityForResult(intent, SIGN_IN_REQUEST_CODE);
}

@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	if (requestCode == SIGN_IN_REQUEST_CODE) {
		if (resultCode == GoogleOAuthLoginActivity.RESULT_OK) {
			data.getStringExtra(GoogleOAuthLoginActivity.EXTRA_OUT_ACCESS_TOKEN);
			data.getStringExtra(GoogleOAuthLoginActivity.EXTRA_OUT_REFRESH_TOKEN);
		} else {
			// do something else
		}
	}
}
		
```



