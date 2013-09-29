package com.example.gymmies;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.gymmies.SwipeDismissListViewTouchListener;

import android.app.Activity;
import android.app.AlertDialog;
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

import com.example.gymmies.Home.getNewsfeed;
import com.example.gymmies.utilities.JSONParser;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;


	public class Groups extends Activity implements OnItemClickListener{
		ArrayList<String> start = new ArrayList<String>();
		ArrayList<String> contactNameList = new ArrayList<String>();
		ArrayList<String> contactIdList = new ArrayList<String>();
		ArrayAdapter<String> contactAdapter;
		static String[] contactMenu;
		static String[] idMenu;
		private ListView lv;
		EditText inputSearch;
		PullToRefreshListView pullToRefreshView;
		
		/** Called when the activity is first created. */
		@Override
		public void onCreate(Bundle savedInstanceState) {

			super.onCreate(savedInstanceState);
			setContentView(R.layout.groups);
			
			pullToRefreshView = (PullToRefreshListView) findViewById(R.id.pull_to_refresh_listview2);
			pullToRefreshView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			    @Override
			    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
			        // Do work to refresh the list here.
			        new getGroupList().execute();
			    }
			});
			
			lv = pullToRefreshView.getRefreshableView();
			inputSearch = (EditText) findViewById(R.id.inputSearch3);
			start.add(" ");
			contactAdapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, start);
			SwipeDismissListViewTouchListener touchListener = new SwipeDismissListViewTouchListener(
					lv, new SwipeDismissListViewTouchListener.OnDismissCallback() {
						@Override
						public void onDismiss(ListView listView,
								final int[] reverseSortedPositions) {
							AlertDialog.Builder adRemove = new AlertDialog.Builder(
									Groups.this);
							adRemove.setTitle("Remove Group");
							adRemove.setMessage("your really want to leave this group?");
							adRemove.setPositiveButton("YES",
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog,
												int which) {
											for (final int position : reverseSortedPositions) {
												contactAdapter
														.remove(contactAdapter
																.getItem(position));
												new leaveGroup().execute(idMenu[position]);
											}

											Toast.makeText(getApplicationContext(),
													"Left Group",
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
					Groups.this.contactAdapter.getFilter().filter(cs);
				}

				public void beforeTextChanged(CharSequence arg0, int arg1,
						int arg2, int arg3) {
					// TODO Auto-generated method stub
				}

				public void afterTextChanged(Editable arg0) {
					// TODO Auto-generated method stub
				}
			});

			new getGroupList().execute();
			// if you want your array
		}

		@Override
		public void onItemClick(AdapterView<?> arg0, View v, int position,
				long id) {
			// TODO Auto-generated method stub
			Intent i = new Intent(this, Group.class);
			i.putExtra("gid", Groups.idMenu[position]);
			startActivity(i);
		}
		
		class getGroupList extends AsyncTask<String, String, String> {
			JSONParser jParser = new JSONParser();

			protected void onPreExecute() {
				super.onPreExecute();
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
								"http://www.thejackyiu.com/utsc_gymmies_server/schedule/getusergroup.php",
								"POST", params2);
				try {
					success = json.getInt("success");
					if (success == 1) {
						contactNameList.clear();
						contactIdList.clear();
						JSONArray groups = json.getJSONArray("groups");
						for (int i = 0; i <json.getInt("numrows");i++){
							JSONObject group = groups.getJSONObject(i);
							contactNameList.add(group.getString("name")+"'s Group");
							contactIdList.add(group.getString("gid"));	
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
				pullToRefreshView.onRefreshComplete();
			}
		}
		
		class leaveGroup extends AsyncTask<String, String, String> {
			String fname, lname, uid;
			ProgressDialog pDialog;
			JSONParser jParser = new JSONParser();

			protected void onPreExecute() {
				super.onPreExecute();
				pDialog = new ProgressDialog(Groups.this);
				pDialog.setMessage("Leaving Group");
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
				params2.add(new BasicNameValuePair("gid", params[0]));
				// Send request
				JSONObject json = jParser
						.makeHttpRequest(
								"http://www.thejackyiu.com/utsc_gymmies_server/schedule/leavegroup.php",
								"POST", params2);
				return null;
			}

			protected void onPostExecute(String msg) {
				pDialog.dismiss();
				
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
