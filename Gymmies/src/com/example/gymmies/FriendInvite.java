package com.example.gymmies;

import static com.example.gymmies.CommonUtilities.SERVER_URL;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.gymmies.Friends.getFriendList;
import com.example.gymmies.Friends.removefriend;
import com.example.gymmies.utilities.JSONParser;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class FriendInvite extends Activity implements OnItemClickListener, OnClickListener{
	ArrayList<String> start = new ArrayList<String>();
	ArrayList<String> contactNameList = new ArrayList<String>();
	ArrayList<String> contactIdList = new ArrayList<String>();
	ArrayAdapter<String> contactAdapter;
	String[] contactMenu, idMenu, checkedMenu;
	ArrayList<String> ischecked = new ArrayList<String>();
	String gid;
	
	// UI Items
	private ListView lv;
	private Button btinvite;
	EditText inputSearch;
	AlertDialog alertDialog;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friend_invite);
		Bundle extras = getIntent().getExtras();
		gid = extras.getString("gid");
		Toast.makeText(getApplicationContext(),
				"gid:"+gid, Toast.LENGTH_SHORT).show();
		lv = (ListView) findViewById(R.id.list3);
		btinvite = (Button) findViewById(R.id.btfriendinvite);
		btinvite.setOnClickListener(this);
		inputSearch = (EditText) findViewById(R.id.inputSearch2);
		start.add(" ");
		contactAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_activated_1, start);
		lv.setAdapter(contactAdapter);
		lv.setOnItemClickListener(this);
		lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		
		
		// contact search function
		inputSearch.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence cs, int arg1, int arg2,
					int arg3) {
				// When user changed the Text
				FriendInvite.this.contactAdapter.getFilter().filter(cs);
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
		
	}
	
	// Async task that retrieves the friend list.
		class getFriendList extends AsyncTask<String, String, String> {
			String fname, lname, uid;
			ProgressDialog pDialog;
			JSONParser jParser = new JSONParser();

			protected void onPreExecute() {
				super.onPreExecute();
				pDialog = new ProgressDialog(FriendInvite.this);
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
							ischecked.add("0");
						}
						contactMenu = contactNameList
								.toArray(new String[contactNameList.size()]);
						idMenu = contactIdList
								.toArray(new String[contactIdList.size()]);
						checkedMenu = ischecked.toArray(new String[ischecked.size()]);
						
						

						// After store everything into datastructure, put the code
						// inside run() and loop again and display the friendlist.
						runOnUiThread(new Runnable() {
							public void run() {
								start.clear();
								start.addAll(contactNameList);
								contactAdapter.clear();
								contactAdapter.addAll(contactNameList);
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

		public void onItemClick(AdapterView<?> arg0, View v, final int position,
				long id) {
			Toast.makeText(getApplicationContext(),
					"Selected!", Toast.LENGTH_SHORT).show();
			if (checkedMenu[position].equals("0")){
				checkedMenu[position] = "1";
				lv.setItemChecked(position, true);
			} else {
				checkedMenu[position] = "0";
				lv.setItemChecked(position, false);
			}
		}
		
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.friend_invite, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btfriendinvite:
			// Collect all ids.
			for (int i = 0; i<checkedMenu.length;i++){
				if (checkedMenu[i].equals("1")){
					System.out.println("SENDING INVITATION GID IS:"+gid);
					new sendInvitation().execute(CommonUtilities.firstname, idMenu[i], gid);
				} 
			}
			finish();
			break;
		}
	}
	
	class sendInvitation extends AsyncTask<String, String, String> {
		JSONParser jParser = new JSONParser();
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
 	    }
		
	     @Override
	    protected String doInBackground(final String... params) {
	    	 int success;
		    
	    	 // Prepare parameters to be sent
		     List<NameValuePair> params2 = new ArrayList<NameValuePair>();
		     params2.add(new BasicNameValuePair("invitorname", params[0]));
		     params2.add(new BasicNameValuePair("uid", params[1]));
		     params2.add(new BasicNameValuePair("gid", params[2]));
		     
		     
		     try {
			     final JSONObject json = jParser.makeHttpRequest(SERVER_URL + "/schedule/invitefriend.php", "POST",
			                     params2);
		     } catch (Exception e) {
		             e.printStackTrace();
		     }
		     return null;
	     }
	
	     @Override
		protected void onPostExecute(String msg) {
	     }
	 }

}
