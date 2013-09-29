package com.example.gymmies;

import static com.example.gymmies.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static com.example.gymmies.CommonUtilities.EXTRA_MESSAGE;
import static com.example.gymmies.CommonUtilities.SENDER_ID;
import static com.example.gymmies.CommonUtilities.SERVER_URL;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.gymmies.utilities.JSONParser;
import com.example.gymmies.utilities.ServerUtilities;
import com.google.android.gcm.GCMRegistrar;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;



public class Login extends Activity implements OnClickListener {
	// UI widgets
	Button login;
	Button signup;
	TextView logo;
	EditText username, password;
	ToggleButton showHide;
	TextView mDisplay;
	// ProgressDialog
	ProgressDialog pDialog;
	// URL for login.php
	private final static String url_login = SERVER_URL + "/login/login.php";
	// JSONParser
	JSONParser jParser = new JSONParser();
	private static final String TAG_SUCCESS = "success";
	String gcmid;
	
	
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.login);
		super.onCreate(savedInstanceState);
		// Get the gcmid
		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);
		registerReceiver(mHandleMessageReceiver, new IntentFilter(
				DISPLAY_MESSAGE_ACTION));
		gcmid = GCMRegistrar.getRegistrationId(this);
		if (gcmid == "") {
			GCMRegistrar.register(this, SENDER_ID);
		}
		
		initialize();
	}

	private void initialize() {
		login = (Button) findViewById(R.id.bLogin);
		signup = (Button) findViewById(R.id.bSignup);
		logo = (TextView) findViewById(R.id.tvLogo);
		username = (EditText) findViewById(R.id.etUsername);
		password = (EditText) findViewById(R.id.etPassword);
		showHide = (ToggleButton) findViewById(R.id.toggleButton1);
		login.setOnClickListener(this);
		signup.setOnClickListener(this);
		showHide.setOnClickListener(this);
	}

	public void onClick(View view) {
		switch (view.getId()) {

		case R.id.toggleButton1:
			if (showHide.isChecked()) {
				password.setInputType(InputType.TYPE_CLASS_TEXT);
			} else
				password.setInputType(InputType.TYPE_CLASS_TEXT
						| InputType.TYPE_TEXT_VARIATION_PASSWORD);

			break;
		case R.id.bSignup:
			Intent register = new Intent("com.example.gymmies.SignUp");
			startActivity(register);
			break;
		case R.id.bLogin:
			
			System.out.println("Got into login");
			// Retrieve GCM RegID
			gcmid = GCMRegistrar.getRegistrationId(this);
			System.out.println("");
			System.out.println("gcmidis" + gcmid);

			// Execute the logintask.
			new LoginTask().execute();
		}
	}

	class LoginTask extends AsyncTask<String, String, String> {
		protected void onPreExecute() {
			super.onPreExecute();
			System.out.println("executing async task preexec");
			pDialog = new ProgressDialog(Login.this);
			pDialog.setMessage("Logging in...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			int success;

			// Prepare parameters to be sent
			List<NameValuePair> params2 = new ArrayList<NameValuePair>();
			params2.add(new BasicNameValuePair("username", username.getText()
					.toString()));
			params2.add(new BasicNameValuePair("password", password.getText()
					.toString()));
			System.out.println("USERNAME:" + username.getText().toString());
			System.out.println("USERNAME:" + password.getText().toString());
			System.out.println("USERNAME:" + gcmid);
			params2.add(new BasicNameValuePair("gcmid", gcmid));
			// check if username exists by making HTTP GET request
			System.out.println("yea");
			JSONObject json = jParser.makeHttpRequest(url_login, "POST",
					params2);
			Log.d("user login", json.toString());
			System.out.println("yea2");
			try {
				success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					// Ssave the UID of the currently logged in person.
					CommonUtilities.setCURRENT_USERID(json.getString("uid"));
					CommonUtilities.username = json.getString("username");
					CommonUtilities.firstname = json.getString("firstname");
					CommonUtilities.lastname = json.getString("lastname");
					CommonUtilities.gender = json.getString("gender");
					
					
					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(getApplicationContext(),
									"Successful Login", Toast.LENGTH_LONG)
									.show();
						}
					});
					// Redirect to home menu
					System.out.println("SUCCESS!"); 
					Intent regsuccess = new Intent(
							"com.example.gymmies.Tabhost");
					regsuccess.putExtra("firstname",
							json.getString("firstname"));
					// Store user variables
					CommonUtilities.setCURRENT_USERID(json.getString("uid"));
					startActivity(regsuccess);
				} else {
					// Not successful
					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(getApplicationContext(),
									"Incorrect Username/Password",
									Toast.LENGTH_LONG).show();
						}
					});
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(String msg) {
			pDialog.dismiss();
			// Registration successful, indicate success using a toast
		}
	}

	// Broadcast receiver
	public final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
			if (intent.getExtras().getString("uid") == CommonUtilities
					.getCURRENT_USERID()) {
				// If the message is for this CURRENTLY logged on user
				System.out.println("boo");
				System.out.println(intent.getExtras().getString("message"));
			}
		}
	};

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// Handle the back button
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);

			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}
}
