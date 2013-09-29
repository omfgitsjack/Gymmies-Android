package com.example.gymmies;

import static com.example.gymmies.CommonUtilities.EXTRA_MESSAGE;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.Message;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.gymmies.utilities.JSONParser;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TabHost.TabSpec;

public class Home extends Activity implements OnItemClickListener {

	Intent menu, profile;
	// Items for the chat box
	Button editProfile, sendMessage;
	TextView viewmessagebox, username;
	EditText sendmessagebox;
	ChatManager chatmanager;
	Chat chat;
	String firstname;
	String msg;
	static Connection connection;
	Context ctx;

	ListView lv;
	PullToRefreshListView pullToRefreshView;
	
	// ArrayLists for newsfeed list.
	ArrayList<String> start = new ArrayList<String>();
	ArrayList<String> newsfeedList = new ArrayList<String>();
	ArrayList<String> idList = new ArrayList<String>();
	String gid[];
	ArrayAdapter<String> adapter;

	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.newsfeed);
		// Changing the name of the user.
		Intent intent = getIntent();
		firstname = intent.getStringExtra("firstname");
		ctx = this;
		
		// Set a listener to be invoked when the list should be refreshed.
		pullToRefreshView = (PullToRefreshListView) findViewById(R.id.pull_to_refresh_listview);
		pullToRefreshView.setOnRefreshListener(new OnRefreshListener<ListView>() {
		    @Override
		    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		        // Do work to refresh the list here.
		        new getNewsfeed().execute();
		    }
		});
		
		lv = pullToRefreshView.getRefreshableView();
		
		newsfeedList.add(" ");
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, newsfeedList);
		//lv = (ListView) findViewById(R.id.newsfeedview);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(this);
		new getNewsfeed().execute();
		// Connect the the chat server so that the user will be displayed as online.
		new messagesetup.init().execute(getApplicationContext());
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int position,
			long id) {
		// TODO Auto-generated method stub
		Intent i = new Intent(this, Group.class);
		i.putExtra("gid", gid[position]);
		startActivity(i);
	}
	
	class getNewsfeed extends AsyncTask<String, String, String> {
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
			params2.add(new BasicNameValuePair("uid", CommonUtilities.getCURRENT_USERID()));
			// Send request
			JSONObject json = jParser
					.makeHttpRequest(
							"http://www.thejackyiu.com/utsc_gymmies_server/schedule/getnewsfeed.php",
							"POST", params2);
			try {
				success = json.getInt("numrows");
				if (success>0) {
					JSONArray listofnews = json.getJSONArray("records");
					newsfeedList.clear();
					for (int i = 0; i <json.getInt("numrows");i++){
						JSONObject item = listofnews.getJSONObject(i);
						newsfeedList.add(item.getString("name")+"\n"+item.getString("activityname")+"\n"+item.getString("date")+"\n"+item.getString("starttime")+" - "+item.getString("endtime"));
						idList.add(item.getString("gid"));	
					}
					gid = idList.toArray(new String[idList.size()]);
					//start.clear();
					//start.addAll(newsfeedList);
					runOnUiThread(new Runnable() {
						public void run() {
							adapter.notifyDataSetChanged();
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
			pullToRefreshView.onRefreshComplete();
		}
	}
	
	/**
	 * Broadcast Receiver to receive notifications from our server.
	 * 
	 */
	public final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("message...");
			String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
			System.out.println(newMessage);
			if (intent.getExtras().getString("uid") == CommonUtilities
					.getCURRENT_USERID()) {
				// If the message is for this CURRENTLY logged on user
				Toast.makeText(ctx,
						intent.getExtras().getString("message"), Toast.LENGTH_LONG)
						.show();
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