package com.example.gymmies;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.gymmies.SwipeDismissListViewTouchListener;
import com.example.gymmies.utilities.JSONParser;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Friends extends Activity implements OnItemClickListener {
	ArrayList<String> start = new ArrayList<String>();
	ArrayList<String> contactNameList = new ArrayList<String>();
	ArrayList<String> contactIdList = new ArrayList<String>();
	ArrayAdapter<String> contactAdapter;
	static String[] contactMenu;
	static String[] idMenu;
	private ListView lv;
	EditText inputSearch;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Search bar initialize
		setContentView(R.layout.friends);
		lv = (ListView) findViewById(R.id.list);
		inputSearch = (EditText) findViewById(R.id.inputSearch);
		start.add(" ");
		contactAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, start);
		SwipeDismissListViewTouchListener touchListener = new SwipeDismissListViewTouchListener(
				lv, new SwipeDismissListViewTouchListener.OnDismissCallback() {
					@Override
					public void onDismiss(ListView listView,
							final int[] reverseSortedPositions) {
						AlertDialog.Builder adRemove = new AlertDialog.Builder(
								Friends.this);
						adRemove.setTitle("Remove Friend");
						adRemove.setMessage("your really want to Remove this friend?");
						adRemove.setPositiveButton("YES",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										for (final int position : reverseSortedPositions) {
											contactAdapter
													.remove(contactAdapter
															.getItem(position));
											new removefriend().execute(idMenu[position]);
										}

										Toast.makeText(getApplicationContext(),
												"Friend removed",
												Toast.LENGTH_LONG).show();
									}
								});
						adRemove.setNegativeButton("NO",
								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int which) {

									}
								});
						adRemove.create();
						adRemove.show();

						contactAdapter.notifyDataSetChanged(); 
					}
				});
		lv.setOnTouchListener(touchListener);
		lv.setAdapter(contactAdapter);
		lv.setOnItemClickListener(this);
		
		
		// contact search function
		inputSearch.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence cs, int arg1, int arg2,
					int arg3) {
				// When user changed the Text
				Friends.this.contactAdapter.getFilter().filter(cs);
			}

			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub
			}

			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
			}
		});

		new getFriendList().execute();
		// if you want your array
	}

	class removefriend extends AsyncTask<String, String, String>{
		JSONParser jParser = new JSONParser();
		protected String doInBackground(String... params) {
			List<NameValuePair> params2 = new ArrayList<NameValuePair>();
			params2.add(new BasicNameValuePair("uid", CommonUtilities
					.getCURRENT_USERID()));
			params2.add(new BasicNameValuePair("fid", params[0]));
			// Send request
			JSONObject json = jParser
					.makeHttpRequest(
							"http://www.thejackyiu.com/utsc_gymmies_server/friendlist/removefriend.php",
							"POST", params2);
			return null;
		}
		
	}
	
	
	// Async task that retrieves the friend list.
	class getFriendList extends AsyncTask<String, String, String> {
		String fname, lname, uid;
		ProgressDialog pDialog;
		JSONParser jParser = new JSONParser();

		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Friends.this);
			pDialog.setMessage("Retrieving friends");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();

		}

		@Override
		protected String doInBackground(String... params) {
			int success;
			// Prepare parameters to be sent
			List<NameValuePair> params2 = new ArrayList<NameValuePair>();
			params2.add(new BasicNameValuePair("uid", CommonUtilities
					.getCURRENT_USERID()));
			// Send request
			JSONObject json = jParser
					.makeHttpRequest(
							"http://www.thejackyiu.com/utsc_gymmies_server/friendlist/retrievefriends.php",
							"POST", params2);
			System.out.println("pass2");
			try {
				success = json.getInt("success");
				if (success == 1) {

					// Save the UID of the currently logged in person.
					JSONArray json2 = json.getJSONArray("friendlist");
					// retrieve first element, gives us an object.
					JSONObject obj = json2.getJSONObject(0);
					int totalfriends = obj.getInt("totalfriends");
					// We now loop to retrieve the data from the user
					for (int i = 1; i <= totalfriends; i++) {
						// Cast the integer into a string.
						Integer j = new Integer(i);
						// We need to retrieve the JSONObject but it's first
						// stored in an array.
						JSONArray friendarray = obj.getJSONArray(j.toString());
						JSONObject friendobject = friendarray.getJSONObject(0);
						fname = friendobject.getString("firstname");
						lname = friendobject.getString("lastname");
						uid = friendobject.getString("uid");
						contactNameList.add(fname);
						contactIdList.add(uid);
					}
					contactMenu = contactNameList
							.toArray(new String[contactNameList.size()]);
					idMenu = contactIdList
							.toArray(new String[contactIdList.size()]);
					start.clear();
					start.addAll(contactNameList);

					// After store everything into datastructure, put the code
					// inside run() and loop again and display the friendlist.
					runOnUiThread(new Runnable() {
						public void run() {
							contactAdapter.notifyDataSetChanged();
							// Here, loop through the data structure and setup
							// the friend list.

						}
					});
				} else {
					// Not successful
					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(getApplicationContext(),
									"You don't have any friends ):",
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
		}
	}

	AlertDialog alertDialog;

	public void onItemClick(AdapterView<?> arg0, View v, final int position,
			long id) {
		String[] activity = { "Message", "Profile" };
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setTitle("Options :");
		dialogBuilder.setItems(activity, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				switch (item) {
				case 0:
					Toast.makeText(getApplicationContext(),
							"you choose Message", Toast.LENGTH_SHORT).show();
					Intent iMessage = new Intent(
							"com.example.gymmies.FriendMessage");
					iMessage.putExtra("friendname",
							Friends.contactMenu[position]);
					startActivity(iMessage);
					break;

				case 1:
					Toast.makeText(getApplicationContext(),
							"you choose Profile", Toast.LENGTH_SHORT).show();
					Intent iProfile = new Intent(
							"com.example.gymmies.Friendprofile");
					iProfile.putExtra("friendname", Friends.contactMenu[position]);
					iProfile.putExtra("fid", Friends.idMenu[position]);
					startActivity(iProfile);
					break;
				}
				alertDialog.dismiss();
			}
		});
		alertDialog = dialogBuilder.create();
		alertDialog.show();

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