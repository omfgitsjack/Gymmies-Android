package com.example.gymmies;

import static com.example.gymmies.CommonUtilities.SERVER_URL;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import android.support.v4.app.FragmentActivity;
import com.example.gymmies.utilities.JSONParser;

@SuppressLint("ValidFragment")
public class Group extends FragmentActivity implements OnClickListener{
	
	// UI variables
	TextView activityname, description, location, owner, groupname, date, time;
	Button join2,invite2;
	LinearLayout my_layout;
	AlertDialog alertdialog;
	Context ctx;
	
	// Logical variables
	int totalparticipants;
	String gid;
	String allparticipants[][];
	int availabletimes[] = new int[3];
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group);
		Bundle extras = getIntent().getExtras();
		gid = extras.getString("gid");
		ctx = this;
		join2 = (Button)findViewById(R.id.btjoin3);
		join2.setOnClickListener(this);
		join2.setVisibility(View.INVISIBLE);
		if (extras.getBoolean("isinvited")){
			join2.setVisibility(View.VISIBLE);
		}
		System.out.println("GIDIS:"+gid);
		new getDetails().execute(gid);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.group, menu);
		return true;
	}

	public String[] getParticipant(String pid){
		// Loop through allparticipants
		for (int i = 0;i<totalparticipants;i++){
			if(allparticipants[i][9].equals(pid)){
					return allparticipants[i];
			}
		}
		return null;
	}
	
	class getDetails extends AsyncTask<String, String, String> {
		JSONParser jParser = new JSONParser();
		ProgressDialog pDialog;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Group.this);
			pDialog.setMessage("Loading details... ");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
 	    }
		
	     @Override
	    protected String doInBackground(final String... params) {
	    	 int success;
		     // Prepare parameters to be sent
		     List<NameValuePair> params2 = new ArrayList<NameValuePair>();
		     params2.add(new BasicNameValuePair("gid", params[0]));
		     params2.add(new BasicNameValuePair("uid", CommonUtilities.getCURRENT_USERID()));
		     try {
			     final JSONObject json = jParser.makeHttpRequest(SERVER_URL + "/schedule/getgroup.php", "POST",
			                     params2);
			    //System.out.println(json);
		    	 success = json.getInt("success");
		    	 if (success == 1) {
		    		 runOnUiThread(new Runnable() {
						@Override
						public void run() {
							try {
								int j = 0;
								
								// Initialize UI Items
								activityname = (TextView) findViewById(R.id.tvactivityname2);
								activityname.setText(json.getString("activityname"));
								description = (TextView) findViewById(R.id.tvdescription2);
								description.setText(json.getString("description"));
								date = (TextView) findViewById(R.id.tvdate2);
								date.setText(json.getString("date"));
								location = (TextView) findViewById(R.id.tvlocation2);
								location.setText(json.getString("location"));
								description = (TextView) findViewById(R.id.tvdescription2);
								description.setText(json.getString("description"));
								groupname = (TextView) findViewById(R.id.tvgroupname3);
								groupname.setText(json.getString("owner")+"'s Group");
								date = (TextView) findViewById(R.id.tvdate2);
								date.setText(json.getString("date"));
								time = (TextView) findViewById(R.id.tvtime2);
								String starthour1 = json.getString("starthour");
								if (starthour1.length()==1){
									starthour1 = "0"+starthour1; 
								}
								String startminute1 = json.getString("startminute");
								if (startminute1.length()==1){
									startminute1 = "0"+startminute1; 
								}
								String endhour1 = json.getString("endhour");
								if (endhour1.length()==1){
									endhour1 = "0"+endhour1; 
								}
								String endminute1 = json.getString("endminute");
								if (endminute1.length()==1){
									endminute1 = "0"+endminute1; 
								}
								time.setText(starthour1+":"+startminute1+" - "+endhour1+":"+endminute1);
								// store the time the group is going.
								availabletimes[0] = Integer.parseInt(json.getString("starthour")+json.getString("startminute"));
								availabletimes[1] = Integer.parseInt(json.getString("endhour")+json.getString("endminute"));
								availabletimes[2] = json.getInt("tid");
								if (json.getString("isprivate").equals("0")){
									if (join2.getVisibility()!=View.VISIBLE){
										join2.setVisibility(View.VISIBLE);
									}
								}
								
								JSONArray participants = json.getJSONArray("participants");
								my_layout = (LinearLayout) findViewById(R.id.llgroupparticipants2);
								// availabletimes
								totalparticipants = json.getInt("numrows");
								allparticipants = new String[totalparticipants][11];
								int friendgoing = 0;
								int nonfriendgoing = 0;
								int totalgoing = friendgoing+nonfriendgoing;
								
								// Setting up button for invite friends, leave group which are dependent upon whether the current user is a member of this group.
								Button invitefriends = (Button) findViewById(R.id.btinvitefriends);
								invitefriends.setVisibility(View.INVISIBLE);
								invitefriends.setOnClickListener(new View.OnClickListener() {
									        @Override
											public void onClick(View v) {
									        	Intent invfriends = new Intent(ctx, FriendInvite.class);
												invfriends.putExtra("gid", gid);
												startActivity(invfriends);
									        }
									    });
								Button leaveGroup = (Button) findViewById(R.id.btleavegroup);
								leaveGroup.setVisibility(View.INVISIBLE);
								leaveGroup.setOnClickListener(new View.OnClickListener() {
							        @Override
									public void onClick(View v) {
							        	new leaveGroup().execute(gid);
							        	finish();
							        }
							    });
								System.out.println(totalparticipants);
								// LOOP through all the times available.
								for (int i = 0; i<totalparticipants;i++){
									// Get the individual participants for a particular time session.
									JSONObject participant = participants.getJSONObject(i);
									// This implies that the user is part of the group.
									
									if (participant.getString("uid").equals(CommonUtilities.getCURRENT_USERID())){
										join2.setVisibility(View.INVISIBLE);
										invitefriends.setVisibility(View.VISIBLE);
										leaveGroup.setVisibility(View.VISIBLE);
									} 
									if (participant.getBoolean("isfriend")){
										friendgoing++;
									} else {
										nonfriendgoing++;
									}
									allparticipants[i][0] = participant.getString("starthour");
									allparticipants[i][1] = participant.getString("startminute");
									allparticipants[i][2] = participant.getString("endhour");
									allparticipants[i][3] = participant.getString("endminute");
									allparticipants[i][4] = participant.getString("amountofguests");
									allparticipants[i][5] = participant.getString("location");
									allparticipants[i][6] = participant.getString("name");
									allparticipants[i][7] = participant.getString("uid");
									allparticipants[i][8]= String.valueOf(participant.getBoolean("isfriend"));
									allparticipants[i][9]= participant.getString("pid");
									allparticipants[i][10]= participant.getString("gid");
									totalgoing++;
								}
								TextView rowtextView4 = new TextView(ctx);
								rowtextView4.setText("    Total going: "+totalgoing);
								my_layout.addView(rowtextView4);
								TextView rowtextView2 = new TextView(ctx);
								rowtextView2.setText("    Friends going: "+friendgoing);
								my_layout.addView(rowtextView2);
								TextView rowtextView3 = new TextView(ctx);
								rowtextView3.setText("    non-Friends going: "+nonfriendgoing);
								my_layout.addView(rowtextView3);
								
								// Loop through all participants.
								int count = 0;
								for (int jj = 0; jj<totalparticipants;jj++){	
									final TextView partdetails = new TextView(ctx);
									partdetails.setText("    "+allparticipants[jj][6]);
									partdetails.setId(Integer.parseInt(allparticipants[jj][9]));
									// If the participant is a friend of the current user, then we indicate that.
									if (allparticipants[jj][8].equals("true")){
										partdetails.setCompoundDrawablesWithIntrinsicBounds(R.drawable.isfriend, 0, 0, 0);
									} else {
										// Align the text..
										partdetails.setText("       "+allparticipants[jj][6]);
									}
									// Set on click listener to display a dialog.
									partdetails.setOnLongClickListener(new View.OnLongClickListener() {		
										@Override
										public boolean onLongClick(View v) {
											AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ctx);
											final String[] details = getParticipant(Integer.toString(v.getId()));
											Boolean incJoin = false;
											int total = 2;

											// Count how many trues we have.
											String[] options = new String[] {"Message","Profile"};
											dialogBuilder.setTitle("Options:");
											dialogBuilder.setItems(options, new DialogInterface.OnClickListener() {
								    			@Override
												public void onClick(DialogInterface dialog, int item) {
								    				switch (item) {
								    				case 0:
								    					Toast.makeText(getApplicationContext(),
								    							"you choose Message", Toast.LENGTH_SHORT).show();
								    					Intent iMessage = new Intent(
								    							"com.example.gymmies.FriendMessage");
								    					// This only works assuming the firstname contains no spaces.
								    					iMessage.putExtra("friendname",
								    							details[6].split(" ")[0]);
								    					startActivity(iMessage);
								    					break;

								    				case 1:
								    					Toast.makeText(getApplicationContext(),
								    							"you choose Profile", Toast.LENGTH_SHORT).show();
								    					Intent iProfile = new Intent(
								    							"com.example.gymmies.Friendprofile");
								    					iProfile.putExtra("friendname", details[6].split(" ")[0]);
								    					startActivity(iProfile);
								    					break;
								    				}
								    				alertdialog.dismiss();
								    			}
								    		});
											alertdialog = dialogBuilder.create();
											alertdialog.show();
											
											return false;
										}
									});
									partdetails.setOnClickListener(new View.OnClickListener() {
								        @Override
										public void onClick(View v) {
								            // On click display a dialog that displays more details.
								        	// This is designed to avoid cluttering.
								        	AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ctx);
								        	// Set title to be the participant's name
								        	// Getting the row and column of allparticipants through the id of the view.
								        	final String[] details = getParticipant(Integer.toString(v.getId()));
								        	
								        	// Set Title.
								        	// Format the hour and times
											String starthour1 = details[0];
											if (starthour1.length()==1){
												starthour1 = "0"+starthour1; 
											}
											String startminute1 = details[1];
											if (startminute1.length()==1){
												startminute1 = "0"+startminute1; 
											}
											String endhour1 = details[2];
											if (endhour1.length()==1){
												endhour1 = "0"+endhour1; 
											}
											String endminute1 = details[3];
											if (endminute1.length()==1){
												endminute1 = "0"+endminute1; 
											}
											dialogBuilder.setTitle(details[6]);
											dialogBuilder.setMessage(
								        			"Duration: "+starthour1+":"+startminute1+
								        			" - "+endhour1+":"+endminute1+"\nGuests: "+details[4]+
								        			"\nMeeting Location: "+details[5]);
								        	// If friends,
								        	alertdialog = dialogBuilder.create();
								        	alertdialog.show();
								        }
								    });
									my_layout.addView(partdetails);
								}
					    	 } catch (Exception e) {
					             e.printStackTrace();
					    	 }	
						}
					});
		     
		     } // If success
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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()){
			case R.id.btjoin3:{
				System.out.println("Joined from onclick outside");
				join();
				break;
			}
		}
	}
	
	class leaveGroup extends AsyncTask<String, String, String> {
		String fname, lname, uid;
		ProgressDialog pDialog;
		JSONParser jParser = new JSONParser();

		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Group.this);
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
	
	public void join(){
		DialogFragment newFragment = new TimePickerFragment();
	    // Send in the gid.
		Bundle args = new Bundle();
		args.putString("gid", gid);
		newFragment.setArguments(args);
		newFragment.show(getSupportFragmentManager(), "custom_dialog");
	}
	
	public class TimePickerFragment extends DialogFragment implements View.OnClickListener{
		public Activity act;
		public Dialog dialog;
		public Button cancel, join;
		public TimePicker ttp1, ttp2;
		EditText etlocation,etamountofguests;
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
			ScrollView view = (ScrollView) inflater.inflate(R.layout.custom_dialog, container);
			getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
			join = (Button) view.findViewById(R.id.btn_join);
		    cancel = (Button) view.findViewById(R.id.btn_cancel);
		    join.setOnClickListener(this);
		    cancel.setOnClickListener(this);
		    ttp1 = (TimePicker) view.findViewById(R.id.tpstarttime3);
			ttp1.setIs24HourView(true);
			ttp2 = (TimePicker) view.findViewById(R.id.tpendtime3);
			ttp2.setIs24HourView(true);
			etlocation = (EditText)view.findViewById(R.id.etmeetinglocation2);
			etamountofguests = (EditText)view.findViewById(R.id.etnumberofguests2);
			System.out.println("Started new timepicker fragment");
			return view;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
		    case R.id.btn_join:
		    	System.out.println("Clicked join");
		    	// Format times so that we can compare it to ensure the selected start/end time is within timesessions.
		    	int starthour = ttp1.getCurrentHour();
	        	String strstarthour = Integer.toString(starthour);
	        	if (strstarthour.length()==1){
	        		strstarthour="0"+strstarthour;
	        	}
	        	int startminute = ttp1.getCurrentMinute();
	        	String strstartminute = Integer.toString(startminute);
	        	if (strstartminute.length()==1){
	        		strstartminute="0"+strstartminute;
	        	}
	        	int endhour = ttp2.getCurrentHour();
	        	String strendhour = Integer.toString(endhour);
	        	if (strendhour.length()==1){
	        		strendhour="0"+strendhour;
	        	}
	        	int endminute = ttp2.getCurrentMinute();
	        	String strendminute = Integer.toString(endminute);
	        	if (strendminute.length()==1){
	        		strendminute="0"+strendminute;
	        	}
	        	int tid = -1; // Initialize to negative.
	        	boolean outofrange = true;

        		// If the time is within the specified time, and the start time < end time.
        		
        		int starttime = Integer.parseInt(strstarthour+strstartminute);
        		int endtime = Integer.parseInt(strendhour+strendminute);

        		if ((starttime >= availabletimes[0] && endtime <= availabletimes[1])
        				&& (starttime<endtime)){
        			// If it satisfies these conditions then the user indicated time lies within this availabletime frame.
        			outofrange = false;
        			tid = availabletimes[2];
        		}
	        	
	        	// If the times satisfy at least one available time.
	        	if (outofrange == false){
	        		System.out.println("IS IN RANGE");
	        		// Params needed:
	   	    	 	// $tid, $uid, $activityname, $year, $month, $day, $starthour, 
	   	    	 	// $startminute, $endhour, $endminute, $amountofguests, $location, $private, $lookingforpeople, $gid
	        		String strlocation = etlocation.getText().toString();
	        		if (strlocation.equals("")){
	        			strlocation = "n/a";
	        		}
	        		String stramountofguests = etamountofguests.getText().toString();
	        		String amountofguests;
	        		if (stramountofguests.matches("[0-9]+") || stramountofguests.equals("")){
	        			System.out.println("AMOUNT OF GUEST MATCHES");
	        			// If it only contains numbers, then we accept it.
	        			if (stramountofguests.equals(""))
	        				amountofguests = "0";
	        			else
	        				amountofguests = stramountofguests;
	        			// Get the gid, by getting the participant.
	        			System.out.println("PREEXEC ASYNCTASK!");
	        			// Start asynctask to send data into servers.
	            		new joinGroup().execute(
	            				Integer.toString(tid), 
	            				CommonUtilities.getCURRENT_USERID(), 
	            				activityname.getText().toString(),
	            				date.getText().toString().split("-")[0],
	            				date.getText().toString().split("-")[1],
	            				date.getText().toString().split("-")[2],
	            				Integer.toString(starthour),
	            				Integer.toString(startminute),
	            				Integer.toString(endhour),
	            				Integer.toString(endminute),
	            				amountofguests,
	            				strlocation,
	            				getArguments().getString("gid")
	            				);
	            		System.out.println("POSTEXEC ASYBNCTASK");
	    		    	dismiss();
	        		} else {
	        			// It contains characters.
	        			Toast.makeText(getApplicationContext(), "Error: amount of guests must be a number",
	                            Toast.LENGTH_LONG).show();
	        		}
	        		
	        	} else {
	        		Toast.makeText(getApplicationContext(), "Time selected isn't available",
	                        Toast.LENGTH_LONG).show();
	        	}
		    	break;
		    case R.id.btn_cancel:
		    	dismiss();
		    	break;
		    default:
		    	break;
			}
		}
		
		class joinGroup extends AsyncTask<String, String, String> {
			JSONParser jParser = new JSONParser();
			ProgressDialog pDialog;
			
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				pDialog = new ProgressDialog(Group.this);
				pDialog.setMessage("Joining Group...");
				pDialog.setIndeterminate(false);
				pDialog.setCancelable(true);
				pDialog.show();
	 	    }
			
		     @Override
		    protected String doInBackground(final String... params) {
		    	 int success;
		    	 // Prepare parameters to be sent
			     List<NameValuePair> params2 = new ArrayList<NameValuePair>();
			     params2.add(new BasicNameValuePair("tid", params[0]));
			     params2.add(new BasicNameValuePair("uid", params[1]));
			     params2.add(new BasicNameValuePair("activityname", params[2]));
			     params2.add(new BasicNameValuePair("year", params[3]));
			     params2.add(new BasicNameValuePair("month", params[4]));
			     params2.add(new BasicNameValuePair("day", params[5]));
			     params2.add(new BasicNameValuePair("starthour", params[6]));
			     params2.add(new BasicNameValuePair("startminute", params[7]));
			     params2.add(new BasicNameValuePair("endhour", params[8]));
			     params2.add(new BasicNameValuePair("endminute", params[9]));
			     params2.add(new BasicNameValuePair("amountofguests", params[10]));
			     params2.add(new BasicNameValuePair("location", params[11]));
			     params2.add(new BasicNameValuePair("gid", params[12]));
			     try {
				     final JSONObject json = jParser.makeHttpRequest(SERVER_URL + "/schedule/joinactivity.php", "POST",
				                     params2);
			    	 
			     } catch (Exception e) {
			             e.printStackTrace();
			     }
			     return null;
		     }
		
		     @Override
			protected void onPostExecute(String msg) {
		    	 pDialog.dismiss();;
		     }
		 }

	}

}
