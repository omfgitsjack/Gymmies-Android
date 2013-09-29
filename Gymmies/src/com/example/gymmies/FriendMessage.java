package com.example.gymmies;


import java.text.SimpleDateFormat;
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

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

public class FriendMessage extends Activity {
	// Items for the chat box
	Button editProfile, sendMessage;
	TextView viewmessagebox, username;
	EditText sendmessagebox;
	Chat chat;
	String firstname;
	String msg;
	String userId;
	static Connection connection;
	JSONParser jParser;
	
	// Name of friend
	//String friendname;
	private final static String url_gethistory = CommonUtilities.LOCALHOSTSCRIPTS_URL + "/gethistory.php";
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM, h:mm a");
	Context ctx;
	// Keep the current notificationID so that if we get more messages, we can update the currently sent 
	int notifID;
	// Checks if the activity is hidden or stopped, if it is, then notifications will be made for every message.
	int activitystopped;
	
	
	protected void onPause(){
		super.onStop();
		activitystopped = 1;
	}
	
	protected void onResume(){
		super.onResume();
		activitystopped = 0;
	}

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Setup friendname and notifID and current context and JSONParser
		Bundle extras = getIntent().getExtras();
		final String friendname = extras.getString("friendname");
		final int notifID = messagesetup.notifid++;
		ctx = this;
		jParser = new JSONParser();
		activitystopped = 0; // Activity is not stopped.
		
		// UI Elements
		setContentView(R.layout.friendmessage);
		TextView name = (TextView) findViewById(R.id.tvName);
		name.setText(friendname);
		final ScrollView sv = (ScrollView) findViewById(R.id.svfriendmessage);
		sv.post(new Runnable(){
			public void run(){
				//sv.scrollTo(0, sv.getBottom());
				sv.fullScroll(View.FOCUS_DOWN);
			}
		});
		sendmessagebox = (EditText) findViewById(R.id.typemessagebox1);
		viewmessagebox = (TextView) findViewById(R.id.messagedisplaybox1);
		sendMessage = (Button) findViewById(R.id.bSend1);
		
		System.out.println("THe person we're talking to is: "+friendname+"@"+CommonUtilities.LOCALHOST_URL);
		// Setup chat
		chat = messagesetup.chatmanager.createChat(friendname+"@localhost", new MessageListener() {
			final String from = friendname;
			// Initialize Message Listener for the first time.
			int nummsg = 0;
			// Setup InboxStyle 
			 NotificationCompat.InboxStyle prevNotifStyle = new NotificationCompat.InboxStyle()
			.setBigContentTitle("Message: "+from);
			 // Setup Notification
			 NotificationCompat.Builder prevNotif = new NotificationCompat.Builder(ctx)
			.setContentIntent(PendingIntent.getActivity( 
		    	    ctx, 
		    	    notifID,
		    	    new Intent(ctx, FriendMessage.class).putExtra("friendname", from).putExtra("notifID",notifID),
		    	    PendingIntent.FLAG_CANCEL_CURRENT
		    	))
			.setSmallIcon(R.drawable.message)
			.setContentTitle("Message: "+from)
			.setContentText("expand")
			.setLargeIcon(BitmapFactory.decodeResource(ctx.getResources(), 
                                R.drawable.big_message));
			 
			@Override
			public void processMessage(Chat chat, Message message) {
				System.out.println("received "+message.getBody()+" from:"+chat.getParticipant());
				final String body = message.getBody(); 
				if (activitystopped == 1){
					prevNotifStyle.addLine(from+": "+body); 
					prevNotif.setStyle(prevNotifStyle); 
					prevNotif.setNumber(++nummsg);
					Notification notif = prevNotif.build();
					notif.flags |= Notification.FLAG_AUTO_CANCEL;
					messagesetup.notificationManager.notify(notifID, notif);
				}
				
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						SpannableStringBuilder builder = new SpannableStringBuilder();
						builder.append("["+sdf.format(System.currentTimeMillis())+"]");
						SpannableString redSpannable= new SpannableString(from);
						redSpannable.setSpan(new ForegroundColorSpan(Color.RED), 0, from.length(), 0);
						builder.append(redSpannable);
						builder.append("\n\t"+body+"\n");
						viewmessagebox.append(builder);
						// Automatically scroll to bottom.
						sv.post(new Runnable(){
							public void run(){
								//sv.scrollTo(0, sv.getBottom());
								sv.fullScroll(View.FOCUS_DOWN);
							}
						});
					}
				});
			}
		});
		
		// Load out history.
		new getHistory().execute(friendname);
		// Messaging kit
		
		sendMessage.setOnClickListener(new OnClickListener() {
			String body;
			@Override
			public void onClick(View view) {
				System.out.println("Send button clicked");
				final String body = sendmessagebox.getText().toString();
				try {
					System.out.println(body);
					
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// Formatting the name to enhance readibility
							SpannableStringBuilder builder = new SpannableStringBuilder();
							builder.append("["+sdf.format(System.currentTimeMillis())+"]");
							SpannableString blueSpannable= new SpannableString(CommonUtilities.firstname);
							blueSpannable.setSpan(new ForegroundColorSpan(Color.BLUE), 0, CommonUtilities.firstname.length(), 0);
							builder.append(blueSpannable);
							builder.append(":\n\t"+body + "\n");
							viewmessagebox.append(builder);
							// Automatically scroll to bottom.
							sv.post(new Runnable(){
								public void run(){
									//sv.scrollTo(0, sv.getBottom());
									sv.fullScroll(View.FOCUS_DOWN);
								}
							});
						}});
					// Send the actual message.
					chat.sendMessage(body);
					sendmessagebox.setText("");
				} catch (Exception a) {
					a.printStackTrace();
				}
			}    
		});
	}
	
	 class getHistory extends AsyncTask<String, String, String> {
		String from, body, time;
		ArrayList<String> msg1 = new ArrayList<String>();
		String msg2[];
		JSONObject json;
		ProgressDialog pDialog;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(FriendMessage.this);
			pDialog.setMessage("Loading History");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			String friendname = params[0];
			try {
				List<NameValuePair> params2 = new ArrayList<NameValuePair>();
				params2.add(new BasicNameValuePair("fromJID", CommonUtilities.firstname+"@localhost"));
				params2.add(new BasicNameValuePair("toJID", friendname+"@localhost"));
				System.out.println("Params are: "+CommonUtilities.firstname+"@localhost"+"\t"+friendname+"@localhost");
				json = jParser.makeHttpRequest(url_gethistory, "POST",
						params2);
				if (json.getInt("success")==1){
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							JSONArray json2;
							try {
								json2 = json.getJSONArray("chathistory");
								for (int i = 0; i < json2.length(); i++){
									// Retrieve data from server
									JSONObject row = json2.getJSONObject(i);
									time = sdf.format(row.getLong("sentDate"));
									from = row.getString("fromJID").split("@localhost")[0];
									body = row.getString("body");
									// Setup text formatting
									SpannableStringBuilder builder = new SpannableStringBuilder();
									builder.append("["+time+"] ");
									if (from.equals(CommonUtilities.firstname)){
										// If it was the user who sent the text, we color it blue
										SpannableString blueSpannable= new SpannableString(from);
										blueSpannable.setSpan(new ForegroundColorSpan(Color.BLUE), 0, from.length(), 0);
										builder.append(blueSpannable);
									} else {
										// It was the friend who sent this message.
										SpannableString redSpannable= new SpannableString(from);
										redSpannable.setSpan(new ForegroundColorSpan(Color.RED), 0, from.length(), 0);
										builder.append(redSpannable);
									}
									builder.append(":\n\t"+body + "\n");
									viewmessagebox.append(builder);
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});
				} 
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String msg) {
			pDialog.dismiss();
		}
	}
}