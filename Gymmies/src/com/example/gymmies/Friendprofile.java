package com.example.gymmies;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.example.gymmies.utilities.JSONParser;

public class Friendprofile extends Activity implements OnItemClickListener, OnClickListener{

	Button favButton, friendbutton;
	
	ListView lview, actlistview;
	String preferences[], personname;
	
	// ArrayLists for user's favourite/preference list when clicking on fav button.
	ArrayList<String> preferencesList = new ArrayList<String>();
	ArrayList<String> start = new ArrayList<String>();
	
	// ArrayLists for activityfeed list.
	ArrayList<String> activityfeedList = new ArrayList<String>();
	ArrayList<String> actfeedstart = new ArrayList<String>();
	ArrayList<String> idList = new ArrayList<String>();
	String gid[];
	
	ArrayAdapter<String> adapter, actfeedAdapter;
	Context ctx;
	String personid;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.friendprofile);
		Bundle extras = getIntent().getExtras();
		
		personid = extras.getString("fid");
		personname = extras.getString("friendname");
		TextView name = (TextView) findViewById(R.id.tvprofilename);
		name.setText(personname);
		actfeedstart.add(" ");
		actfeedAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, actfeedstart);
		actlistview = (ListView) findViewById(R.id.actfeedview);
		actlistview.setAdapter(actfeedAdapter);
		actlistview.setOnItemClickListener(this);
		
		friendbutton = (Button) findViewById(R.id.FriendButton);
		friendbutton.setOnClickListener(this);
		favButton = (Button) findViewById(R.id.bFav);
		ctx = this;

		// add button listener
		favButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				// custom dialog
				final Dialog dialog = new Dialog(ctx);
				LayoutInflater li = (LayoutInflater) ctx
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View v = li.inflate(R.layout.custom, null, false);
				start.add("  ");
				adapter = new ArrayAdapter<String>(ctx,
						android.R.layout.simple_list_item_1, start);
				lview = (ListView) v.findViewById(R.id.listview6);
				lview.setAdapter(adapter);
				lview.setClickable(false);
				dialog.setContentView(v);
				dialog.setTitle(personname + "'s Favourites");
				
				dialog.show();
				new getPreferences().execute(personid);
			}
		});
		
		
		new activityfeed().execute(personid);
		new friendButton().execute(personid);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int position,
			long id) {
		// TODO Auto-generated method stub
		Intent i = new Intent(this, Group.class);
		i.putExtra("gid", gid[position]);
		startActivity(i);
	}
	
	class getPreferences extends AsyncTask<String, String, String> {
		ProgressDialog pDialog;
		JSONParser jParser = new JSONParser();

		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(ctx);
			pDialog.setMessage("Retrieving favourite activities...");
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
			params2.add(new BasicNameValuePair("uid", params[0]));
			// Send request
			JSONObject json = jParser
					.makeHttpRequest(
							"http://www.thejackyiu.com/utsc_gymmies_server/schedule/getpreferences.php",
							"POST", params2);
			try {
				success = json.getInt("success");
				if (success == 1) {
					JSONArray a = json.getJSONArray("activities");
					for (int i = 0; i < json.getInt("totalactivities"); i++) {
						JSONObject b = a.getJSONObject(i);
						preferencesList.add(b.getString("activityname"));
					}
					preferences = preferencesList
							.toArray(new String[preferencesList.size()]);
					start.clear();
					start.addAll(preferencesList);
					
					runOnUiThread(new Runnable() {
						public void run() {
//							contactAdapter.clear();
//							contactAdapter.addAll(preferencesList);
//							contactAdapter.notifyDataSetChanged();
//							adapter.clear();
//							adapter.addAll(preferencesList);
							adapter.notifyDataSetChanged();
							//lv.setAdapter(adapter);
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
	
	
	
	class activityfeed extends AsyncTask<String, String, String> {
		ProgressDialog pDialog;
		JSONParser jParser = new JSONParser();

		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(ctx);
			pDialog.setMessage("Retrieving activity feed...");
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
			params2.add(new BasicNameValuePair("uid", params[0]));
			// Send request
			JSONObject json = jParser
					.makeHttpRequest(
							"http://www.thejackyiu.com/utsc_gymmies_server/schedule/getusergroup.php",
							"POST", params2);
			try {
				success = json.getInt("success");
				if (success == 1) {
					JSONArray groups = json.getJSONArray("groups");
					for (int i = 0; i <json.getInt("numrows");i++){
						JSONObject group = groups.getJSONObject(i);
						
						activityfeedList.add(group.getString("activityname")+"\n"+group.getString("activitydate")+"\n"+group.getString("activitytime"));
						idList.add(group.getString("gid"));	
					}
					
					gid = idList
							.toArray(new String[idList.size()]);
					actfeedstart.clear();
					actfeedstart.addAll(activityfeedList);
					start.clear();
					start.addAll(preferencesList);
					
					runOnUiThread(new Runnable() {
						public void run() {
							actfeedAdapter.notifyDataSetChanged();
						}
					});
				} else {
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

	class friendButton extends AsyncTask<String, String, String> {
		ProgressDialog pDialog;
		JSONParser jParser = new JSONParser();

		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			int success;
			// Prepare parameters to be sent
			System.out.println("log1");
			List<NameValuePair> params2 = new ArrayList<NameValuePair>();
			params2.add(new BasicNameValuePair("fid", params[0]));
			params2.add(new BasicNameValuePair("uid", CommonUtilities.getCURRENT_USERID()));
			// Send request
			final JSONObject json = jParser
					.makeHttpRequest(
							"http://www.thejackyiu.com/utsc_gymmies_server/friendlist/friendbutton.php",
							"POST", params2);
			runOnUiThread(new Runnable() {
				public void run() {
					try {
						// Returns: status = 0 (Not friends, no request sent)
						//			status = 1 (Not friends, YOU sent a request)
						//			status = 2 (Not friends, Other person sent YOU a request)
						//			status = 3 (Friends)
						int status = json.getInt("status");
						friendbutton.setHint(Integer.toString(status));
						switch (status){
						case 0:{
							friendbutton.setText("Send Friend Request");
							break;
						}
						case 1:{
							friendbutton.setText("Friend request sent");
							break;
						}
						case 2:{
							friendbutton.setText("Accept friend Request");
							break;
						}
						case 3:{
							friendbutton.setText("Delete Friend");
							break;
						}
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});
			return null;
		}

		protected void onPostExecute(String msg) {
		}
	}
	
	class friendrequest extends AsyncTask<String, String, String> {
		JSONParser jParser = new JSONParser();

		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			int success;
			// Prepare parameters to be sent
			System.out.println("log1");
			List<NameValuePair> params2 = new ArrayList<NameValuePair>();
			params2.add(new BasicNameValuePair("fid", personid));
			params2.add(new BasicNameValuePair("uid", CommonUtilities.getCURRENT_USERID()));
			// Send request
			final JSONObject json = jParser
					.makeHttpRequest(
							"http://www.thejackyiu.com/utsc_gymmies_server/friendlist/friendrequest.php",
							"POST", params2);
			/* Returns - 	'success' = 0; 'message' = Already friends
							'success' = 0; 'message' = Already sent friend request
							'success' = 1; 'message' = Friendship created
							'success' = 1; 'message' = Friendship Requested
			 */
			runOnUiThread(new Runnable() {
				public void run() {
					try {
						Toast.makeText(getApplicationContext(), json.getString("message"),
						        Toast.LENGTH_LONG).show();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});

			return null;
		}

		protected void onPostExecute(String msg) {
		}
	}
	
	class removefriend extends AsyncTask<String, String, String> {
		JSONParser jParser = new JSONParser();

		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			int success;
			// Prepare parameters to be sent
			System.out.println("log1");
			List<NameValuePair> params2 = new ArrayList<NameValuePair>();
			params2.add(new BasicNameValuePair("fid", personid));
			params2.add(new BasicNameValuePair("uid", CommonUtilities.getCURRENT_USERID()));
			// Send request
			final JSONObject json = jParser
					.makeHttpRequest(
							"http://www.thejackyiu.com/utsc_gymmies_server/friendlist/removefriend.php",
							"POST", params2);
			/* Returns - 	'success' = 0; 'message' = Already friends
							'success' = 0; 'message' = Already sent friend request
							'success' = 1; 'message' = Friendship created
							'success' = 1; 'message' = Friendship Requested
			 */
			runOnUiThread(new Runnable() {
				public void run() {
					try {
						Toast.makeText(getApplicationContext(), json.getString("message"),
						        Toast.LENGTH_LONG).show();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});

			return null;
		}

		protected void onPostExecute(String msg) {
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		// Returns: status = 0 (Not friends, no request sent)
		//			status = 1 (Not friends, YOU sent a request)
		//			status = 2 (Not friends, Other person sent YOU a request)
		//			status = 3 (Friends)
		
		switch(v.getId()){
		case R.id.FriendButton:{
			switch (Integer.parseInt(friendbutton.getHint().toString())){
			case 0:{
				// Not friends, can only send a friend request.
				friendbutton.setText("Already requested as friend");
				friendbutton.setHint("1");
				new friendrequest().execute();
				break;
			}
			case 1:{
				
				// Does nothign, as the other person needs to accept.
				break;
			}
			case 2:{
				// Accept friend request
				friendbutton.setText("Delete Friend");
				friendbutton.setHint("3");
				new friendrequest().execute();
				break;
			}
			case 3:{
				
				AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ctx);
				dialogBuilder.setTitle("Are you sure?");
				dialogBuilder.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int id) {
								new removefriend().execute();
								friendbutton.setText("Add Friend");
								friendbutton.setHint("0");
								// Update friend list.
							}
						});
				dialogBuilder.setNegativeButton("NO",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int id) {
								// User clicked OK button
								dialog.dismiss();
					}
				});
				dialogBuilder.create().show();
				break;
			}
			}
		}
		}
	}
	
	
}
