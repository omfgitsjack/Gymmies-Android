package com.example.gymmies;

import static com.example.gymmies.CommonUtilities.SERVER_URL;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.example.gymmies.utilities.JSONParser;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TabHost.TabSpec;

public class Tabhost extends TabActivity {
	JSONParser jParser = new JSONParser();
	private final static String url_changepassword = SERVER_URL + "/profile/setpassword.php";
	
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.tabhost);

		Resources res = getResources();
		TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost);
		Intent intent = getIntent();

		Intent ihome = new Intent(this, Home.class);
		ihome.putExtra("firstname", intent.getStringExtra("firstname"));
		TabSpec tabSpec1 = tabHost.newTabSpec("tab1");
		tabSpec1.setIndicator("", res.getDrawable(R.drawable.tab_home));
		tabSpec1.setContent(ihome);
		tabHost.addTab(tabSpec1);

		Intent ifriends = new Intent(this, Friends.class);
		TabSpec specs2 = tabHost.newTabSpec("tab2");
		specs2.setContent(ifriends);
		specs2.setIndicator("", res.getDrawable(R.drawable.tab_friend));
		tabHost.addTab(specs2);

		Intent igroups = new Intent(this, Groups.class);
		TabSpec specs3 = tabHost.newTabSpec("tab3");
		specs3.setContent(igroups);
		specs3.setIndicator("", res.getDrawable(R.drawable.tab_group));
		tabHost.addTab(specs3);

		Intent ischedule = new Intent(this, Schedule.class);
		TabSpec specs4 = tabHost.newTabSpec("tab4");
		specs4.setContent(ischedule);
		specs4.setIndicator("", res.getDrawable(R.drawable.calendar));
		tabHost.addTab(specs4);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.homemenu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case R.id.account:
			showaccount();
			break;
		case R.id.setting:
			Intent profile = new Intent("com.example.gymmies.Profile");
			startActivity(profile);
			break;
		case R.id.logout:
			showLogout();
			break;
		}
		return true;
	}
	
	// Declare the edittexts outside.
	EditText etOldPassword, etNewPassword, etReNewPassword;
	
	private void showaccount() {
		AlertDialog.Builder adPassword = new AlertDialog.Builder(Tabhost.this);
		adPassword.setTitle("Change Password");
		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		final TextView tvOldPassword = new TextView(Tabhost.this);
		final TextView tvNewPassword = new TextView(Tabhost.this);
		final TextView tvReNewPassword = new TextView(Tabhost.this);
		tvOldPassword.setText(" Enter Your Old Password : ");
		tvNewPassword.setText(" Enter Your New Password : ");
		tvReNewPassword.setText(" Re-enter Your New Password : ");
		etOldPassword = new EditText(Tabhost.this);
		etNewPassword = new EditText(Tabhost.this);
		etReNewPassword = new EditText(Tabhost.this);
		layout.addView(tvOldPassword);
		layout.addView(etOldPassword);
		layout.addView(tvNewPassword);
		layout.addView(etNewPassword);
		layout.addView(tvReNewPassword);
		layout.addView(etReNewPassword);
		adPassword.setView(layout);
		adPassword.setPositiveButton("SAVE",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// Send data to server
						System.out.println("1."+etReNewPassword.getText());
						System.out.println("2."+etNewPassword.getText());
						if (etReNewPassword.getText().toString().equals(etNewPassword.getText().toString())){
							new changepassword().execute();
						} else {
							Toast.makeText(getApplicationContext(),
									"Passwords don't match", Toast.LENGTH_LONG)
									.show();
						}
					}
				});
		adPassword.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					}
				});
		adPassword.create();
		adPassword.show();

	}

	private void showLogout() {
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setTitle("Logout Confirm").setMessage(
				"are you sure you want to logout?");
		dialogBuilder.setPositiveButton("YES",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// Log the user out of the xmpp server
						new messagesetup.logout().execute();
						// Change the current details to nothing.
						CommonUtilities.setCURRENT_USERID("");
						CommonUtilities.firstname = "";
						CommonUtilities.lastname = "";
						CommonUtilities.gender = "";
						CommonUtilities.username = "";
						Intent login = new Intent("com.example.gymmies.Login");
						startActivity(login);
						Toast.makeText(getApplicationContext(),
								"Logout successfully", Toast.LENGTH_LONG)
								.show();
					}
				});
		dialogBuilder.setNegativeButton("NO",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Toast.makeText(getApplicationContext(), null,
								Toast.LENGTH_SHORT).show();
					}
				});
		AlertDialog alertDialog = dialogBuilder.create();
		alertDialog.show();
	}

	/**
	 * Class: changepassword
	 * Purpose: Checks if the oldpassword is correct, if it is it will update the current password.
	 * themselves as offline)
	 * 
	 * @author jack
	 * 
	 */
	class changepassword extends AsyncTask<String, String, String> {
		protected void onPreExecute() {
			super.onPreExecute();
		}

		protected String doInBackground(String... msg) {
				// Only if both passwords are the same then we keep going 
				try {
					List<NameValuePair> params2 = new ArrayList<NameValuePair>();
					params2.add(new BasicNameValuePair("uid", CommonUtilities.getCURRENT_USERID()));
					params2.add(new BasicNameValuePair("oldpassword", etOldPassword.getText()
							.toString()));
					params2.add(new BasicNameValuePair("newpassword", etNewPassword.getText()
							.toString()));
					// Send http request
					JSONObject json = jParser.makeHttpRequest(url_changepassword, "POST",
							params2);
					if (json.getInt("success")==1){
						runOnUiThread(new Runnable() {
							public void run() {
								Toast.makeText(getApplicationContext(),
										"You have successfully changed your password!", Toast.LENGTH_LONG)
										.show();
							}
						});
					} else {
						runOnUiThread(new Runnable() {
							public void run() {
								Toast.makeText(getApplicationContext(),
										"Incorrect old password", Toast.LENGTH_LONG)
										.show();
							}
						});
					}
				} catch (Exception a) {
					a.printStackTrace();
				}

				

			
			return null;
		}

		protected void onPostExecute(String msg) {
		}
	}

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
