package com.example.gymmies;

import static com.example.gymmies.CommonUtilities.MESSAGINGSERVER_URL;
import static com.example.gymmies.CommonUtilities.SERVER_URL;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gymmies.utilities.AlertDialogManager;
import com.example.gymmies.utilities.JSONParser;

public class SignUp extends Activity implements View.OnClickListener,
		OnCheckedChangeListener {
	// UI Variables that are needed
	TextView tvusername, tvpassword, tvrpassword, tvgender, tvlastname,
			tvfirstname, tvactivity;
	EditText etusername, etpassword, etrpassword, etlastname, etfirstname;
	Button signup;
	RadioGroup selectionGender;
	RadioButton male, female;
	CheckBox basketball, volleyball, badmintion, workout, dance, footie;
	LinearLayout ll;
	// Other variables, including asyncTasks, progress dialogs, username,
	// gender, usercheck.php url
	int success;
	// Asyntask
	AsyncTask<Void, Void, Void> mRegisterTask;
	// Alert dialog manager
	AlertDialogManager alert = new AlertDialogManager();
	// Dialog to show when executing a thread.
	private ProgressDialog pDialog;
	String username;
	String gender = "m";
	private static final String url_registeruser = SERVER_URL
			+ "/login/registeruser.php";
	private static final String url_messagingregisteruser = MESSAGINGSERVER_URL
			+ "/plugins/userService/userservice";
	private static final String url_addpreference = SERVER_URL
			+ "/schedule/updatepreferences.php";
	
	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	// JSONParser
	JSONParser jParser = new JSONParser();
	String[] activities;
	ArrayList<String> selectedcb = new ArrayList<String>();
	
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.sign);
		super.onCreate(savedInstanceState);
		initialize();
		signup.setOnClickListener(this);
		ll = (LinearLayout) findViewById(R.id.boxarea);
		new getActivityList().execute();
	}

	private void initialize() {
		tvusername = (TextView) findViewById(R.id.tvUsername);
		tvpassword = (TextView) findViewById(R.id.tvPassword1);
		tvrpassword = (TextView) findViewById(R.id.tvPassword2);
		tvlastname = (TextView) findViewById(R.id.tvLastname);
		tvfirstname = (TextView) findViewById(R.id.tvFirstname);
		tvgender = (TextView) findViewById(R.id.tvGender);
		tvactivity = (TextView) findViewById(R.id.tvActivity);
		etusername = (EditText) findViewById(R.id.etUsername);
		etpassword = (EditText) findViewById(R.id.etPassword1);
		etrpassword = (EditText) findViewById(R.id.etPassword2);
		etlastname = (EditText) findViewById(R.id.etLastname);
		etfirstname = (EditText) findViewById(R.id.etfirstname);
		selectionGender = (RadioGroup) findViewById(R.id.rgGender);
		male = (RadioButton) findViewById(R.id.rbMale);
		female = (RadioButton) findViewById(R.id.rbFemale);
		signup = (Button) findViewById(R.id.bSignup);
		selectionGender.setOnCheckedChangeListener(this);
		signup = (Button) findViewById(R.id.bSignup);
		success = 0;
	}

	// Handlers for text-based input
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.bCancel: {
			Intent retlogin = new Intent("com.example.gymmies.Login");
			startActivity(retlogin);
			break;
		}
		case R.id.bSignup: {
			String username = etusername.getText().toString();
			String lastname = etlastname.getText().toString();
			String firstname = etfirstname.getText().toString();
			String firstPassword = etpassword.getText().toString();
			String secondPassword = etrpassword.getText().toString();
			if (username.length() == 0) {
				tvusername.setText("Username : You can't leave this empty!");
				tvusername.setTextColor(Color.RED);
			} else if (username.length() > 20) {
				tvusername.setText("Username : Keep it under 20 characters");
				tvusername.setTextColor(Color.RED);
			} else {
				tvusername.setText("Username : good");
				tvusername.setTextColor(Color.BLACK);
			}
			if (lastname.length() == 0) {
				tvlastname.setText("Lastname : you can't leave this empty!");
				tvlastname.setTextColor(Color.RED);
			} else if (lastname.length() > 20) {
				tvlastname.setText("Lastname : Keep it under 20 characters");
				tvlastname.setTextColor(Color.RED);
			} else {
				tvlastname.setText("Lastname : good");
				tvlastname.setTextColor(Color.BLACK);
			}
			if (firstname.length() == 0) {
				tvfirstname.setText("Firstname : you can't leave this empty!");
				tvfirstname.setTextColor(Color.RED);
			} else if (firstname.length() > 20) {
				tvfirstname.setText("Firstname : Keep it under 20 characters");
				tvfirstname.setTextColor(Color.RED);
			} else {
				tvfirstname.setText("Firstname : good");
				tvfirstname.setTextColor(Color.BLACK);
			}
			if (firstPassword.length() == 0) {
				tvpassword.setText("Password : you can't leave this empty!");
				tvpassword.setTextColor(Color.RED);
			} else {
				tvpassword.setText("Password : good");
				tvpassword.setTextColor(Color.BLACK);
			}
			if (secondPassword.length() == 0) {
				tvrpassword
						.setText("Re-enter Password : you can't leave this empty!");
				tvrpassword.setTextColor(Color.RED);
			} else if (secondPassword.length() > 20) {
				tvrpassword
						.setText("Re-enter Password : Keep it under 20 characters");
				tvrpassword.setTextColor(Color.RED);
			} else {
				tvrpassword.setText("Re-enter Password : good");
				tvrpassword.setTextColor(Color.BLACK);
			}
			if (!secondPassword.equals(firstPassword)) {
				tvrpassword
						.setText("Re-enter Password : two passwords don't match ! try again?");
				tvrpassword.setTextColor(Color.RED);
			}
			if (username.length() != 0 && lastname.length() != 0
					&& firstname.length() != 0 && firstPassword.length() != 0
					&& secondPassword.equals(firstPassword)) {
				// Query db to check if username exists via a background
				// asyncthread
				new checkUsername().execute();
			}
		}

		}
	}

	class getActivityList extends AsyncTask<String, String, String> {
		String fname, lname, uid;
		ProgressDialog pDialog;
		JSONParser jParser = new JSONParser();

		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(SignUp.this);
			pDialog.setMessage("Retrieving activities");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
			System.out.println("LOGz");
		}

		@Override
		protected String doInBackground(String... params) {
			int success;
			// Prepare parameters to be sent
			System.out.println("log1");
			List<NameValuePair> params2 = new ArrayList<NameValuePair>();
			params2.add(new BasicNameValuePair("a", "a"));
			System.out.println("log2");
			// Send request
			final JSONObject json = jParser
					.makeHttpRequest(
							"http://www.thejackyiu.com/utsc_gymmies_server/schedule/getactivities.php",
							"POST", params2);
			System.out.println("pass2");
			try {
				success = json.getInt("success");
				if (success == 1) {
					runOnUiThread(new Runnable() {
						public void run() {
							try {
								JSONArray a;
								a = json.getJSONArray("activities");
								activities = new String[json
										.getInt("totalactivities")];
								for (int i = 0; i < json.getInt("totalactivities"); i++) {
									JSONObject b = a.getJSONObject(i);
									
									String activityname = b
											.getString("activityname");
									activities[i] = activityname;
									selectedcb.add("0"); // 0 indicates false.
									
									// Now dynamically create checkbox, with the
									// listeners.
									final CheckBox cb = new CheckBox(
											getApplicationContext());
									cb.setId(i);
									cb.setText(activityname);
									cb.setOnClickListener(new View.OnClickListener() {
										public void onClick(View v) {
											if (cb.isChecked()) {
												cb.setChecked(true);
												selectedcb.set(cb.getId(), "1");
											} else {
												cb.setChecked(false);
												selectedcb.set(cb.getId(), "0");
											}
										}
									});
									
									ll.addView(cb);
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});
				} else {
					// No activities should never happen.
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(String msg) {
			pDialog.dismiss();
		}
	}

	// AsyncTask to check if username exists
	class checkUsername extends AsyncTask<String, String, String> {
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(SignUp.this);
			pDialog.setMessage("Signing up...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			int success;
			System.out.println("executing async task preexec");
			// Prepare parameters to be sent
			List<NameValuePair> params2 = new ArrayList<NameValuePair>();
			params2.add(new BasicNameValuePair("username", etusername.getText()
					.toString()));
			params2.add(new BasicNameValuePair("firstname", etfirstname
					.getText().toString()));
			params2.add(new BasicNameValuePair("lastname", etlastname.getText()
					.toString()));
			params2.add(new BasicNameValuePair("password", etpassword.getText()
					.toString()));
			params2.add(new BasicNameValuePair("gender", gender));
			// check if username exists by making HTTP GET request
			System.out.println("URL " + url_registeruser);
			JSONObject json = jParser.makeHttpRequest(url_registeruser, "POST",
					params2);
			Log.d("user registration", json.toString());
			// json success tag
			try {
				success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					System.out.println("SUCCESS!");
					// Register user on MESSAGING SERVER, Comment out until
					// 'redirect them to start page' when developing.
					List<NameValuePair> params3 = new ArrayList<NameValuePair>();
					params3.add(new BasicNameValuePair("type", "add"));
					params3.add(new BasicNameValuePair("secret", "7byfhw8A"));
					params3.add(new BasicNameValuePair("username", etusername
							.getText().toString()));
					params3.add(new BasicNameValuePair("password", etpassword
							.getText().toString()));
					params3.add(new BasicNameValuePair("name", etfirstname
							.getText().toString()));
					JSONObject json2 = jParser.makeHttpRequest(
							url_messagingregisteruser, "GET", params3);
					for (int i = 0;i<selectedcb.size();i++){
						if (selectedcb.get(i).equals("1")){
							final List<NameValuePair> params4 = new ArrayList<NameValuePair>();
							params4.add(new BasicNameValuePair("uid",json.getString("uid")));
							params4.add(new BasicNameValuePair("activityname",activities[i]));
							JSONObject json3 = jParser.makeHttpRequest(url_addpreference, "POST", params4);
						}
					}
					
					// Redirect them to start page
					Intent regsuccess = new Intent("com.example.gymmies.Login");
					startActivity(regsuccess);
				} else {
					tvusername
							.setText("Username: This username already exists.");
					tvusername.setTextColor(Color.RED);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(String msg) {
			pDialog.dismiss();
			// Registration successful, display a dialog that informs them of
			// this
			Toast.makeText(getApplicationContext(), "Successful Registration",
					Toast.LENGTH_LONG).show();
		}
	}

	public void onCheckedChanged(RadioGroup groupGender, int checkedId) {
		if (checkedId == female.getId()) {
			Toast.makeText(getApplicationContext(), "you choose Female",
					Toast.LENGTH_SHORT).show();
		}
		if (checkedId == male.getId()) {
			Toast.makeText(getApplicationContext(), "you choose Male",
					Toast.LENGTH_SHORT).show();
		}
	}
}