package com.example.gymmies;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.gymmies.Friends.removefriend;
import com.example.gymmies.R.string;
import com.example.gymmies.utilities.JSONParser;

@SuppressLint("ValidFragment")
public class Schedule extends FragmentActivity {
	String activities[];
	String favourites[];
	Context context;
	Bundle extras;
	PagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;
	ArrayList<String> start = new ArrayList<String>();
	public ListView lv;
	ArrayList<String> activitiesList = new ArrayList<String>();
	ArrayList<String> favouriteList = new ArrayList<String>();
	ArrayAdapter<String> contactAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_swipe);
		setTitle("Gymmies");
		extras = getIntent().getExtras();
		context = this;

		// Set onclick listener for
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		// Set onclick listener for Join button

	}

	@SuppressLint("ValidFragment")
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fragmentManager) {
			super(fragmentManager);
		}

		@SuppressLint("ValidFragment")
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			ListFragment lf;
			if (position == 0) {
				lf = new activityList();
			} else if (position == 1){
				lf = new favActivityList();
			} else {
				lf = new joinedActivityList();
			}
			return lf;
		}

		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return ("activity list").toUpperCase(l);
			case 1:
				return ("favourites list").toUpperCase(l);
			case 2:
				return ("Joined Activities").toUpperCase(l);			}
			return null;
		}
	}

	public class joinedActivityList extends ListFragment implements OnItemClickListener{
		// ArrayLists for activityfeed list.
		ArrayList<String> activityfeedList = new ArrayList<String>(); 
		ArrayList<String> actfeedstart = new ArrayList<String>();
		ArrayList<String> idList = new ArrayList<String>();
		String gid[];
		ArrayList<String> preferencesList = new ArrayList<String>();
		ArrayAdapter<String> actfeedAdapter;
		ListView actlistview;
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			actlistview = getListView();
			actfeedstart.add(" ");
			actfeedAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, actfeedstart);
			setListAdapter(actfeedAdapter);
			new activityfeed().execute();
		}

		public void onListItemClick(ListView l, View v, int position, long id) {
			Intent intent = new Intent(v.getContext(), Group.class);
			intent.putExtra("gid", gid[position]);
			startActivity(intent);
		}
		
		class activityfeed extends AsyncTask<String, String, String> {
			ProgressDialog pDialog;
			JSONParser jParser = new JSONParser();

			protected void onPreExecute() {
				super.onPreExecute();
				pDialog = new ProgressDialog(getActivity());
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
				params2.add(new BasicNameValuePair("uid", CommonUtilities.getCURRENT_USERID()));
				// Send request
				JSONObject json = jParser
						.makeHttpRequest(
								"http://www.thejackyiu.com/utsc_gymmies_server/schedule/getusergroup.php",
								"POST", params2);
				try {
					success = json.getInt("success");
					if (success == 1) {
						JSONArray groups = json.getJSONArray("groups");
						activityfeedList.clear();
						for (int i = 0; i <json.getInt("numrows");i++){
							JSONObject group = groups.getJSONObject(i);
							
							activityfeedList.add(group.getString("activityname")+"\n"+group.getString("activitydate")+"\n"+group.getString("activitytime"));
							idList.add(group.getString("gid"));	
						}
						
						gid = idList
								.toArray(new String[idList.size()]);
						
						actfeedstart.clear();
						actfeedstart.addAll(activityfeedList);
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
		
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			
		}
	}
	
	public class favActivityList extends ListFragment implements OnItemClickListener{

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);	
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			lv = getListView();
			start.add(" ");
			contactAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(),
					android.R.layout.simple_list_item_1, start);
			SwipeDismissListViewTouchListener touchListener = new SwipeDismissListViewTouchListener(
					lv, new SwipeDismissListViewTouchListener.OnDismissCallback() {
						@Override
						public void onDismiss(ListView listView,
								final int[] reverseSortedPositions) {
							AlertDialog.Builder adRemove = new AlertDialog.Builder(
									Schedule.this);
							adRemove.setTitle("Remove Preference");
							adRemove.setMessage("Do you really want to remove this activity from your favourites?");
							adRemove.setPositiveButton("YES",
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog,
												int which) {
											for (final int position : reverseSortedPositions) {
												System.out.println("Removing:"+contactAdapter
														.getItem(position));
												new removePreference().execute(contactAdapter
														.getItem(position));
												favouriteList.remove(position);
												contactAdapter
														.remove(contactAdapter
																.getItem(position));
												
											}

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
			
			setListAdapter(contactAdapter);
			//lv.setAdapter(contactAdapter);
			lv.setOnTouchListener(touchListener);
			new getPreferences().execute();
		}

		public void onListItemClick(ListView l, View v, int position, long id) {
			Toast.makeText(getActivity().getBaseContext(),
					"Item clicked: " + favourites[position], Toast.LENGTH_LONG)
					.show();
			super.onListItemClick(l, v, position, id);
			DateFormat dateFormat = new SimpleDateFormat("yyyy-M-dd");
			// Get current date time with Date()
			Date date = new Date();
			String month = dateFormat.format(date).split("-")[1];
			// Minus one from month, because it counts from 0. and there's no
			// month 0
			int month1 = Integer.parseInt(month);
			if (month1 == 12) {
				month1 = 11;
			} else {
				month1 = month1 - 1;
			}
			String formateddate = dateFormat.format(date).split("-")[0] + "-"
					+ Integer.toString(month1) + "-"
					+ dateFormat.format(date).split("-")[2];

			String act = activities[position];
			Intent intent = new Intent(v.getContext(), CalendarView.class);
			intent.putExtra("activityname", favourites[position]);
			intent.putExtra("date", formateddate);
			startActivity(intent);
		}
		
		class getPreferences extends AsyncTask<String, String, String> {
			ProgressDialog pDialog;
			JSONParser jParser = new JSONParser();

			protected void onPreExecute() {
				super.onPreExecute();
				pDialog = new ProgressDialog(getActivity());
				pDialog.setMessage("Retrieving your favourites...");
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
				params2.add(new BasicNameValuePair("uid", CommonUtilities
						.getCURRENT_USERID()));
				// Send request
				JSONObject json = jParser
						.makeHttpRequest(
								"http://www.thejackyiu.com/utsc_gymmies_server/schedule/getpreferences.php",
								"POST", params2);
				try {
					success = json.getInt("success");
					if (success == 1) {
						JSONArray a = json.getJSONArray("activities");
						favouriteList.clear();
						for (int i = 0; i < json.getInt("totalactivities"); i++) {
							JSONObject b = a.getJSONObject(i);
							favouriteList.add(b.getString("activityname"));
						}
						favourites = favouriteList
								.toArray(new String[favouriteList.size()]);
						
						//start.clear();
						//start.addAll(activitiesList);
						
						runOnUiThread(new Runnable() {
							public void run() {
								contactAdapter.clear();
								contactAdapter.addAll(favouriteList);
								contactAdapter.notifyDataSetChanged();
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

		class removePreference extends AsyncTask<String, String, String>{
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
				params2.add(new BasicNameValuePair("uid", CommonUtilities
						.getCURRENT_USERID()));
				params2.add(new BasicNameValuePair("activityname", params[0]));
				// Send request
				JSONObject json = jParser
						.makeHttpRequest(
								"http://www.thejackyiu.com/utsc_gymmies_server/schedule/removepreference.php",
								"POST", params2);		
				return null;
			}

			protected void onPostExecute(String msg) {
			}
		}
		
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			
		}
	}

	@SuppressLint("ValidFragment")
	public class activityList extends ListFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			// Call server-side script to retrieve activities
			new getActivityList().execute();
		}
		
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			ListView lv = getListView();
			lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
				@Override
				public boolean onItemLongClick(AdapterView<?> av, View v,
						final int pos, long id) {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							context);
					builder.setTitle("Add to favourites");
					builder.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									// User clicked OK button
									new savePreference().execute(Integer
											.toString(pos));
								}
							});
					builder.setNegativeButton("CANCEL",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									// User clicked OK button
									dialog.dismiss();
								}
							});
					final Dialog dialog = builder.create();
					dialog.show();

					return onLongListItemClick(v, pos, id);
				}
			});
		}

		class savePreference extends AsyncTask<String, String, String> {
			ProgressDialog pDialog;
			JSONParser jParser = new JSONParser();

			protected void onPreExecute() {
				super.onPreExecute();
				pDialog = new ProgressDialog(getActivity());
				pDialog.setMessage("Favouriting...");
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
				params2.add(new BasicNameValuePair("uid", CommonUtilities
						.getCURRENT_USERID()));
				params2.add(new BasicNameValuePair("activityname",
						activities[Integer.parseInt(params[0])]));
				// Send request
				JSONObject json = jParser
						.makeHttpRequest(
								"http://www.thejackyiu.com/utsc_gymmies_server/schedule/updatepreferences.php",
								"POST", params2);
				favouriteList.add(activities[Integer.parseInt(params[0])]);
				favourites = favouriteList
						.toArray(new String[favouriteList.size()]);
				runOnUiThread(new Runnable() {
					public void run() {
						contactAdapter.clear();
						contactAdapter.addAll(favouriteList);
						contactAdapter.notifyDataSetChanged();
					}
				});
				return null;
			}

			protected void onPostExecute(String msg) {
				pDialog.dismiss();
			}
		}

		protected boolean onLongListItemClick(View v, int pos, long id) {
			// Log.i(TAG, "onLongListItemClick id=" + id);
			return true;
		}

		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {
			super.onListItemClick(l, v, position, id);
			DateFormat dateFormat = new SimpleDateFormat("yyyy-M-dd");
			// Get current date time with Date()
			Date date = new Date();
			String month = dateFormat.format(date).split("-")[1];
			// Minus one from month, because it counts from 0. and there's no
			// month 0
			int month1 = Integer.parseInt(month);
			if (month1 == 12) {
				month1 = 11;
			} else {
				month1 = month1 - 1;
			}
			String formateddate = dateFormat.format(date).split("-")[0] + "-"
					+ Integer.toString(month1) + "-"
					+ dateFormat.format(date).split("-")[2];

			String act = activities[position];
			Intent intent = new Intent(v.getContext(), CalendarView.class);
			intent.putExtra("activityname", activities[position]);
			intent.putExtra("date", formateddate);
			startActivity(intent);
		}

		class getActivityList extends AsyncTask<String, String, String> {
			ProgressDialog pDialog;
			JSONParser jParser = new JSONParser();
			ArrayList<String> activitiesList = new ArrayList<String>();

			protected void onPreExecute() {
				super.onPreExecute();
				pDialog = new ProgressDialog(getActivity());
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
				JSONObject json = jParser
						.makeHttpRequest(
								"http://www.thejackyiu.com/utsc_gymmies_server/schedule/getactivities.php",
								"POST", params2);
				System.out.println("pass2");
				try {
					success = json.getInt("success");
					if (success == 1) {
						JSONArray a = json.getJSONArray("activities");
						for (int i = 0; i < json.getInt("totalactivities"); i++) {
							JSONObject b = a.getJSONObject(i);
							activitiesList.add(b.getString("activityname"));
						}
						activities = activitiesList
								.toArray(new String[activitiesList.size()]);
						runOnUiThread(new Runnable() {
							public void run() {
								setListAdapter(new ArrayAdapter<String>(
										getActivity(),
										android.R.layout.simple_list_item_1,
										activities));
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