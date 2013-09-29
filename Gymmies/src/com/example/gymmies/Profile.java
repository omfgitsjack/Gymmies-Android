package com.example.gymmies;

import static com.example.gymmies.CommonUtilities.SERVER_URL;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.gymmies.utilities.JSONParser;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

public class Profile extends Activity implements OnClickListener {
	TextView Birthday, Year, Program, Firstname, Lastname, Gender;
	int dGender;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.profile);
		super.onCreate(savedInstanceState);
		Button editBirthday = (Button) findViewById(R.id.bBirthday);
		Button editYear = (Button) findViewById(R.id.bYear);
		Button editProgram = (Button) findViewById(R.id.bProgram);
		Button editFirstname = (Button) findViewById(R.id.bFirstname);
		Button editLastname = (Button) findViewById(R.id.bLastname);
		Button editGender = (Button) findViewById(R.id.bGender);
		Button editActivity = (Button) findViewById(R.id.bActivity);
		Button cancel = (Button) findViewById(R.id.bCancel);
		Button save = (Button) findViewById(R.id.bSave);
		editBirthday.setOnClickListener(this);
		editYear.setOnClickListener(this);
		editProgram.setOnClickListener(this);
		editFirstname.setOnClickListener(this);
		editLastname.setOnClickListener(this);
		editGender.setOnClickListener(this);
		editActivity.setOnClickListener(this);
		cancel.setOnClickListener(this);
		save.setOnClickListener(this);
		Birthday = (TextView) findViewById(R.id.etBirthday);
		Year = (TextView) findViewById(R.id.etYear);
		Program = (TextView) findViewById(R.id.etProgram);
		Firstname = (TextView) findViewById(R.id.etFirstname);
		Lastname = (TextView) findViewById(R.id.etLastname);
		Gender = (TextView) findViewById(R.id.etGender);
		if (CommonUtilities.gender.equals("m")) {
			dGender = 0;
		} else if (CommonUtilities.gender.equals("f")) {
			dGender = 1;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bLastname:
			showLastname();
			break;
		case R.id.bFirstname:
			showFirstname();
			break;
		case R.id.bBirthday:
			showBirthday();
			break;
		case R.id.bYear:
			showYear();
			break;
		case R.id.bProgram:
			showProgram();
			break;
		case R.id.bGender:
			showGender();
			break;
		case R.id.bActivity:
			showActivity();
			break;
		case R.id.bCancel:
			onBackPressed();
			break;
		case R.id.bSave:

			AlertDialog.Builder adSave = new AlertDialog.Builder(Profile.this);
			adSave.setTitle("SAVE")
					.setMessage("save your profile")
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									onBackPressed();
									Toast.makeText(getApplicationContext(),
											"profile saved successfully",
											Toast.LENGTH_SHORT).show();
								}
							});
			adSave.create().show();

			break;
		}

	}

	private void showActivity() {
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		final String[] activity = { "Basketball", "Volleyball", "Badminton",
				"Work out" };
		final boolean[] dActivity = { true, false, true, false };
		dialogBuilder.setTitle("Activity");
		dialogBuilder.setMultiChoiceItems(activity, dActivity,
				new DialogInterface.OnMultiChoiceClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int item,
							boolean b) {
						Log.d("MainActivity",
								String.format("%s:%s", activity[item], b));
					}
				});
		dialogBuilder.setPositiveButton("Save",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Toast.makeText(getApplicationContext(),
								"Activity changed", Toast.LENGTH_SHORT).show();
					}
				});
		dialogBuilder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				});
		AlertDialog alertDialog = dialogBuilder.create();
		alertDialog.show();
	}

	AlertDialog alertDialog;

	private void showGender() {
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		String gend;

		final String[] activity = { "Male", "Female" };
		// CommonUtilities.gender;
		// final boolean[] selected = {true, false };
		dialogBuilder.setTitle("Gender");
		dialogBuilder.setSingleChoiceItems(activity, dGender,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int item) {
						switch (item) {
						case 0:
							new editgender().execute("m");
							Toast.makeText(getApplicationContext(),
									"you choose male", Toast.LENGTH_SHORT)
									.show();
							dGender = 0;
							break;
						case 1:
							Toast.makeText(getApplicationContext(),
									"you choose female", Toast.LENGTH_SHORT)
									.show();
							new editgender().execute("f");
							dGender = 1;
							break;

						}
						alertDialog.dismiss();
					}
				});
		dialogBuilder.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				});

		alertDialog = dialogBuilder.create();
		alertDialog.show();
	}

	private void showProgram() {
		AlertDialog.Builder adProgram = new AlertDialog.Builder(Profile.this);
		adProgram.setTitle("Edit Program");
		final EditText etProgram = new EditText(Profile.this);
		adProgram.setView(etProgram);
		adProgram.setMessage("your Program is : ");
		adProgram.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Program.setText(etProgram.getText().toString());
						Toast.makeText(
								getApplicationContext(),
								"Program set to : "
										+ etProgram.getText().toString(),
								Toast.LENGTH_LONG).show();
					}
				});
		adProgram.create();
		adProgram.show();

	}

	private void showYear() {
		AlertDialog.Builder adYear = new AlertDialog.Builder(Profile.this);
		adYear.setTitle("Edit Birthday");
		final EditText etYear = new EditText(Profile.this);
		adYear.setView(etYear);
		adYear.setMessage("your Year is : ");
		adYear.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Year.setText(etYear.getText().toString());
				Toast.makeText(getApplicationContext(),
						"Year set to : " + etYear.getText().toString(),
						Toast.LENGTH_LONG).show();
			}
		});
		adYear.create();
		adYear.show();

	}

	private void showBirthday() {
		AlertDialog.Builder adBirthday = new AlertDialog.Builder(Profile.this);
		adBirthday.setTitle("Edit Birthday");
		final EditText etBirthday = new EditText(Profile.this);
		adBirthday.setView(etBirthday);
		adBirthday.setMessage("your birthday is : ");
		adBirthday.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Birthday.setText(etBirthday.getText().toString());
						Toast.makeText(
								getApplicationContext(),
								"birthday set to : "
										+ etBirthday.getText().toString(),
								Toast.LENGTH_LONG).show();
					}
				});
		adBirthday.create();
		adBirthday.show();

	}

	private void showFirstname() {
		AlertDialog.Builder adFirstname = new AlertDialog.Builder(Profile.this);
		adFirstname.setTitle("Edit Firstname");
		final EditText etFirstname = new EditText(Profile.this);
		adFirstname.setView(etFirstname);
		adFirstname.setMessage("your Firstname is : "
				+ CommonUtilities.firstname);
		adFirstname.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						new editfirstname().execute(etFirstname.getText()
								.toString());
						Firstname.setText(etFirstname.getText().toString());
						Toast.makeText(
								getApplicationContext(),
								"Firstname set to : "
										+ etFirstname.getText().toString(),
								Toast.LENGTH_LONG).show();
					}
				});
		adFirstname.create();
		adFirstname.show();

	}

	private void showLastname() {
		AlertDialog.Builder adLastname = new AlertDialog.Builder(Profile.this);
		adLastname.setTitle("Edit Lastname");
		final EditText etLastname = new EditText(Profile.this);
		adLastname.setView(etLastname);
		adLastname.setMessage("your Lastname is : " + CommonUtilities.lastname);
		adLastname.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						new editfirstname().execute(etLastname.getText()
								.toString());
						Lastname.setText(etLastname.getText().toString());
						Toast.makeText(
								getApplicationContext(),
								"Lastname set to : "
										+ etLastname.getText().toString(),
								Toast.LENGTH_LONG).show();
					}
				});
		adLastname.create();
		adLastname.show();

	}

	/**
	 * Below are the asynctasks for saving different parts of the profile
	 * includes: editfirstname editlastname editgender Also includes:
	 * loadactivities
	 */

	// ProgressDialog
	ProgressDialog pDialog;
	// URL for login.php
	private final static String url_editfirstname = SERVER_URL
			+ "/profile/setfirstname.php";
	private final static String url_editlastname = SERVER_URL
			+ "/profile/setlastname.php";
	private final static String url_editgender = SERVER_URL
			+ "/profile/setgender.php";

	// JSONParser
	JSONParser jParser = new JSONParser();

	class editfirstname extends AsyncTask<String, String, String> {
		protected void onPreExecute(String name) {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... name) {
			int success;

			// Prepare parameters to be sent
			List<NameValuePair> params2 = new ArrayList<NameValuePair>();
			params2.add(new BasicNameValuePair("uid", CommonUtilities
					.getCURRENT_USERID()));
			params2.add(new BasicNameValuePair("firstname", name[0]));
			System.out.println("name:" + name[0]);
			// Send http request
			JSONObject json = jParser.makeHttpRequest(url_editfirstname,
					"POST", params2);

			return null;
		}

		@Override
		protected void onPostExecute(String msg) {
		}
	}

	class editlastname extends AsyncTask<String, String, String> {
		protected void onPreExecute(String name) {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... name) {
			int success;

			// Prepare parameters to be sent
			List<NameValuePair> params2 = new ArrayList<NameValuePair>();
			params2.add(new BasicNameValuePair("uid", CommonUtilities
					.getCURRENT_USERID()));
			params2.add(new BasicNameValuePair("lastname", name[0]));
			System.out.println("name:" + name[0]);
			// Send http request
			JSONObject json = jParser.makeHttpRequest(url_editlastname, "POST",
					params2);

			return null;
		}

		@Override
		protected void onPostExecute(String msg) {
			pDialog.dismiss();
		}
	}

	class editgender extends AsyncTask<String, String, String> {
		protected void onPreExecute(String gender) {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... gender) {
			int success;

			// Prepare parameters to be sent
			List<NameValuePair> params2 = new ArrayList<NameValuePair>();
			params2.add(new BasicNameValuePair("uid", CommonUtilities
					.getCURRENT_USERID()));
			params2.add(new BasicNameValuePair("firstname", gender[0]));
			System.out.println("gender:" + gender[0]);
			// Send http request
			JSONObject json = jParser.makeHttpRequest(url_editgender, "POST",
					params2);

			return null;
		}

		@Override
		protected void onPostExecute(String msg) {
			pDialog.dismiss();
		}
	}
}