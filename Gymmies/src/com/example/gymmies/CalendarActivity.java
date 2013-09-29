package com.example.gymmies;

import static com.example.gymmies.CommonUtilities.SERVER_URL;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.gymmies.Schedule.activityList.savePreference;
import com.example.gymmies.utilities.JSONParser;

@SuppressLint("ValidFragment")
public class CalendarActivity extends FragmentActivity{
	TextView activityname, description, date, availability, location;
	CheckBox cb1, cb2;
	LinearLayout my_layout;
	static Context ctx;
	Context ctxnons;
	Boolean islookingformore, isprivateevent;
	TimePicker tp1, tp2;
	Bundle extras;
	// These two variables are used to store the availabletime and max available time sessions..
	int availabletimes[][];
	int totaltimes;
	// Used to store participants
	String allparticipants[][][];
	String allgroups[][][][];
	
	AlertDialog alertdialog;
	SectionsPagerAdapter mSectionsPagerAdapter;
	
	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_swipe);
		setTitle("Gymmies");
		extras = getIntent().getExtras();
		ctx = this;
		ctxnons = this;
		
		// Set onclick listener for 
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		// Set onclick listener for Join button
		
	}

	
	public class SectionsPagerAdapter extends FragmentPagerAdapter{

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			Fragment fragment;
			if (position == 0){
				fragment = new activityinformation();
				
			} else {
				fragment = new activityjoinpage();
			} 
			
			return fragment;
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return ("overview").toUpperCase(l);
			case 1:
				return ("pick time").toUpperCase(l);

			}
			return null;
		}
	}
	
	@SuppressLint("ValidFragment")
	public class activityjoinpage extends Fragment implements View.OnClickListener, OnCheckedChangeListener{
		Context ctx1;
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.activity_join,
					container, false);
			new initUI().execute();
			return rootView;
		}	
		
		class initUI extends AsyncTask<String, String, String> {	
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
	 	    }
			
		     @Override
		    protected String doInBackground(final String... params) {
		    	
		    	 runOnUiThread(new Runnable() {
						@Override
						public void run() {
		    	 init2();
						}});
			     return null;
		     }
		
		     @Override
			protected void onPostExecute(String msg) {
		     }
		 }
		
		public void init2(){
			tp1 = (TimePicker) findViewById(R.id.tpstarttime);
			tp1.setIs24HourView(true);
			tp2 = (TimePicker) findViewById(R.id.tpendtime);
			tp2.setIs24HourView(true);
			Button join = (Button)findViewById(R.id.btjoin);
			join.setOnClickListener(this);
			cb1 = (CheckBox) findViewById(R.id.cblookingformorepeople);
			cb1.setOnCheckedChangeListener(this);
			islookingformore = false;
			cb2 = (CheckBox) findViewById(R.id.cbprivateevent);
			cb2.setOnCheckedChangeListener(this);
			isprivateevent = false;
			my_layout = (LinearLayout) findViewById(R.id.llavailability2);
			for (int i = 0; i < totaltimes; i ++){
				int w = i;
				w++;
				final TextView rowTextView = new TextView(ctx);
				rowTextView.setText(w+". "+availabletimes[i][0]+" - "+availabletimes[i][1]);
				my_layout.addView(rowTextView);
			}
		}
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			// TODO Auto-generated method stub
			if (cb1.isChecked()==true){
				islookingformore = true;
			} else {
				islookingformore = false;
			}
			if (cb2.isChecked()==true){
				isprivateevent = true;
			} else {
				isprivateevent = false;
			}
			System.out.println("1. Looking for more: " + islookingformore);
			System.out.println("2. Private Event: " + isprivateevent);
		}
		
		@Override
		public void onClick(View v) {

			// TODO Auto-generated method stub
			switch (v.getId()) {
	        case R.id.btjoin:{
	        	// First we check if the current start and end selected hour is within the specified limit.
	        	int starthour = tp1.getCurrentHour();
	        	String strstarthour = Integer.toString(starthour);
	        	if (strstarthour.length()==1){
	        		strstarthour="0"+strstarthour;
	        	}
	        	int startminute = tp1.getCurrentMinute();
	        	String strstartminute = Integer.toString(startminute);
	        	if (strstartminute.length()==1){
	        		strstartminute="0"+strstartminute;
	        	}
	        	int endhour = tp2.getCurrentHour();
	        	String strendhour = Integer.toString(endhour);
	        	if (strendhour.length()==1){
	        		strendhour="0"+strendhour;
	        	}
	        	int endminute = tp2.getCurrentMinute();
	        	String strendminute = Integer.toString(endminute);
	        	if (strendminute.length()==1){
	        		strendminute="0"+strendminute;
	        	}
	        	int tid = -1; // Initialize to negative.
	        	boolean outofrange = true;
	        	for (int i = 0; i<totaltimes;i++){
	        		// If the time is within the specified time, and the start time < end time.
	        		
	        		int starttime = Integer.parseInt(strstarthour+strstartminute);
	        		int endtime = Integer.parseInt(strendhour+strendminute);
	        		System.out.println("1. start:"+starttime);
	        		System.out.println("2. end:"+endtime);
	        		if ((starttime >= availabletimes[i][0] && endtime <= availabletimes[i][1])
	        				&& (starttime<endtime)){
	        			// If it satisfies these conditions then the user indicated time lies within this availabletime frame.
	        			outofrange = false;
	        			tid = availabletimes[i][2];
	        			break;
	        		}
	        	}
	        	// If the times satisfy at least one available time.
	        	if (outofrange == false){
	        		// Params needed:
	   	    	 	// $tid, $uid, $activityname, $year, $month, $day, $starthour, 
	   	    	 	// $startminute, $endhour, $endminute, $amountofguests, $location, $private, $lookingforpeople
	        		EditText etlocation = (EditText)findViewById(R.id.etmeetinglocation);
	        		String strlocation = etlocation.getText().toString();
	        		if (strlocation.equals("")){
	        			strlocation = "n/a";
	        		}
	        		EditText etamountofguests = (EditText)findViewById(R.id.etnumberofguests);
	        		String stramountofguests = etamountofguests.getText().toString();
	        		String amountofguests;
	        		if (stramountofguests.matches("[0-9]+") || stramountofguests.equals("")){
	        			// If it only contains numbers, then we accept it.
	        			if (stramountofguests.equals(""))
	        				amountofguests = "0";
	        			else
	        				amountofguests = stramountofguests;
	        			String strisprivateevent, strislookingformore;
	        			if (isprivateevent){
	        				strisprivateevent = "1";
	        			} else {
	        				strisprivateevent = "0";
	        			}
	        			if (islookingformore){
	        				strislookingformore = "1";
	        			} else {
	        				strislookingformore = "0";
	        			}
	        			
	        			// Start asynctask to send data into servers.
	            		new newJoin().execute(
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
	            				strisprivateevent,
	            				strislookingformore
	            				);
	        		} else {
	        			// It contains characters.
	        			Toast.makeText(getApplicationContext(), "Error: amount of guests must be a number",
	                            Toast.LENGTH_LONG).show();
	        		}
	        		
	        	} else {
	        		Toast.makeText(getApplicationContext(), "Time selected isn't available",
	                        Toast.LENGTH_LONG).show();
	        	}
			}
		}
		}
		
		class newJoin extends AsyncTask<String, String, String> {
			JSONParser jParser = new JSONParser();
			ProgressDialog pDialog;
			
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				pDialog = new ProgressDialog(CalendarActivity.this);
				pDialog.setMessage("Processing...");
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
			     params2.add(new BasicNameValuePair("private", params[12]));
			     params2.add(new BasicNameValuePair("lookingforpeople", params[13]));
			     
			     try {
				     final JSONObject json = jParser.makeHttpRequest(SERVER_URL + "/schedule/createactivity.php", "POST",
				                     params2);
				     runOnUiThread(new Runnable() {
							public void run() {
								invitefriends(Integer.parseInt(params[12]));
						}});
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
	
	@SuppressLint("ValidFragment")
	public class activityinformation extends Fragment{
		@SuppressLint("ValidFragment")
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.activity_calendar,
					container, false);
			new getDetails().execute(extras.getString("activityname"), extras.getString("date"));
			return rootView;
		}
		
		class getDetails extends AsyncTask<String, String, String> {
			JSONParser jParser = new JSONParser();
			ProgressDialog pDialog;
			
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				pDialog = new ProgressDialog(CalendarActivity.this);
				pDialog.setMessage("Loading Data");
				pDialog.setIndeterminate(false);
				pDialog.setCancelable(true);
				pDialog.show();
	 	    }
			
		     @Override
		    protected String doInBackground(final String... params) {
		    	 int success;
			     // Prepare parameters to be sent
			     List<NameValuePair> params2 = new ArrayList<NameValuePair>();
			     params2.add(new BasicNameValuePair("activityname", params[0]));
			     params2.add(new BasicNameValuePair("year", params[1].split("-")[0]));
			     params2.add(new BasicNameValuePair("month", params[1].split("-")[1]));
			     params2.add(new BasicNameValuePair("day", params[1].split("-")[2]));
			     params2.add(new BasicNameValuePair("uid", CommonUtilities.getCURRENT_USERID()));
			     try {
				     final JSONObject json = jParser.makeHttpRequest(SERVER_URL + "/schedule/getthetime.php", "POST",
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
									activityname = (TextView) findViewById(R.id.tvactivityname);
									activityname.setText(params[0]);
									description = (TextView) findViewById(R.id.tvdescription);
									description.setText(json.getString("description"));
									date = (TextView) findViewById(R.id.tvdate);
									date.setText(json.getString("date"));
									location = (TextView) findViewById(R.id.tvlocation);
									location.setText(json.getString("location"));
									
									

									JSONArray a = json.getJSONArray("times");
									my_layout = (LinearLayout) findViewById(R.id.llavailability);
									// availabletimes
									totaltimes = json.getInt("numrows");
									availabletimes = new int[totaltimes][3];
									allparticipants = new String[totaltimes][][];
									allgroups = new String[totaltimes][][][];
									System.out.println("FirstcolLength:"+allgroups.length);
									// LOOP through all the times available.
									for (int i = 0; i<totaltimes;i++){
										final TextView rowTextView = new TextView(ctx);
										JSONObject b = a.getJSONObject(i);
										// Format the data such that it will be displayed in 24hours with 4 characters.
										String starthour = b.getString("starthour");
										if (starthour.length()==1){
											starthour = "0"+starthour; 
										}
										String startminute = b.getString("startminute");
										if (startminute.length()==1){
											startminute = "0"+startminute; 
										}
										String endhour = b.getString("endhour");
										if (endhour.length()==1){
											endhour = "0"+endhour; 
										}
										String endminute = b.getString("endminute");
										if (endminute.length()==1){
											endminute = "0"+endminute; 
										}
										int cd = i;
										cd++;
										rowTextView.setText(cd+". "+starthour+":"+startminute+" - "+
										endhour+":"+endminute);
										rowTextView.setTextColor(Color.parseColor("#33b5e5"));
										// Add view to the linearlayout.
										my_layout.addView(rowTextView);
										// Add time to our array.
										availabletimes[i][0] = Integer.parseInt(starthour.toString()+startminute.toString());
										availabletimes[i][1] = Integer.parseInt(endhour.toString()+endminute.toString());
										availabletimes[i][2] = b.getInt("tid");
										// Setup variables to count total going
										int friendgoing = 0;
										int nonfriendgoing = 0;
										int totalgoing = friendgoing+nonfriendgoing;
										int groupsgoing = 0;
										
										// Get the individual participants for a particular time session.
										JSONObject c = json.getJSONObject(Integer.toString(availabletimes[i][2]));
										JSONArray participants = c.getJSONArray("participants");
										// Get the groups 
										JSONArray groups = c.getJSONArray("groups");
										allgroups[i] = new String[c.getInt("numgrouprows")][][];
										// Loop through ev	ery group
										for (int k=0;k<c.getInt("numgrouprows");k++){
											JSONObject group = groups.getJSONObject(k);
												allgroups[i][k] = new String[13+group.getInt("numrows")][1];
												allgroups[i][k][0][0] = group.getString("gid");
												allgroups[i][k][1][0] = group.getString("totalparticipants");
												allgroups[i][k][2][0] = group.getString("year");
												allgroups[i][k][3][0] = group.getString("month");
												allgroups[i][k][4][0] = group.getString("day");
												allgroups[i][k][5][0] = group.getString("starthour");
												allgroups[i][k][6][0] = group.getString("startminute");
												allgroups[i][k][7][0] = group.getString("endhour");
												allgroups[i][k][8][0] = group.getString("endminute");
												allgroups[i][k][9][0] = group.getString("lookingformore");
												allgroups[i][k][10][0] = group.getString("isprivate");
												System.out.println(i+"."+k+". INSERTING INTO ARRAYS "+allgroups[i][k][10][0]);
												allgroups[i][k][11][0] = group.getString("numrows");
												allgroups[i][k][12][0] = group.getString("name");
												System.out.println(i+"."+k+".Group: "+allgroups[i][k][12][0]);
												groupsgoing++;
												// Now we store the participants of each group
												JSONArray groupparticipants = group.getJSONArray("participants");
												for (int kk=0;kk<group.getInt("numrows");kk++){
													int itemnum = kk + 13;
													JSONObject groupparticipant = groupparticipants.getJSONObject(kk);
													allgroups[i][k][itemnum] = new String[11];
													allgroups[i][k][itemnum][0] = groupparticipant.getString("starthour");
													allgroups[i][k][itemnum][1] = groupparticipant.getString("startminute");
													allgroups[i][k][itemnum][2] = groupparticipant.getString("endhour");
													allgroups[i][k][itemnum][3] = groupparticipant.getString("endminute");
													allgroups[i][k][itemnum][4] = groupparticipant.getString("amountofguests");
													allgroups[i][k][itemnum][5] = groupparticipant.getString("location");
													allgroups[i][k][itemnum][6] = groupparticipant.getString("name");
													allgroups[i][k][itemnum][7] = groupparticipant.getString("uid");
													allgroups[i][k][itemnum][8]= groupparticipant.getString("isfriend");
													allgroups[i][k][itemnum][9]= groupparticipant.getString("pid");
													allgroups[i][k][itemnum][10]= groupparticipant.getString("gid");
												}
											
										}
										allparticipants[i] = new String[c.getInt("numrows")][13];
										for (int pp = 0; pp<c.getInt("numrows");pp++){
											JSONObject participant = participants.getJSONObject(pp);
												if (participant.getBoolean("isfriend")){
													friendgoing++;
												} else {
													nonfriendgoing++;
												}
												// Store date so that we construct a list later.
												allparticipants[i][pp][0] = participant.getString("starthour");
												allparticipants[i][pp][1] = participant.getString("startminute");
												allparticipants[i][pp][2] = participant.getString("endhour");
												allparticipants[i][pp][3] = participant.getString("endminute");
												allparticipants[i][pp][4] = participant.getString("amountofguests");
												allparticipants[i][pp][5] = participant.getString("location");
												allparticipants[i][pp][6] = String.valueOf(participant.getString("isprivate"));
												allparticipants[i][pp][7] = String.valueOf(participant.getString("lookingformore"));
												allparticipants[i][pp][8] = participant.getString("name");
												allparticipants[i][pp][9] = participant.getString("uid");
												allparticipants[i][pp][10]= String.valueOf(participant.getBoolean("isfriend"));
												allparticipants[i][pp][11]= participant.getString("pid");
												allparticipants[i][pp][12]= participant.getString("gid");
												totalgoing++;
										}
										// After getting the totals, we display these under availability.
										TextView rowtextView5 = new TextView(ctx);
										rowtextView5.setText("    Groups going: "+groupsgoing);
										my_layout.addView(rowtextView5);
										TextView rowtextView4 = new TextView(ctx);
										rowtextView4.setText("    Total going: "+totalgoing);
										my_layout.addView(rowtextView4);
										TextView rowtextView2 = new TextView(ctx);
										rowtextView2.setText("    Friends going: "+friendgoing);
										my_layout.addView(rowtextView2);
										TextView rowtextView3 = new TextView(ctx);
										rowtextView3.setText("    non-Friends going: "+nonfriendgoing);
										my_layout.addView(rowtextView3);
										// After displaying the Times and the total count of all the people going, we now display the actual participants.
										
										// Get the linear layouts
										LinearLayout friendpartlayout = (LinearLayout)findViewById(R.id.llfriendparticipants);
										LinearLayout nonfriendpartlayout = (LinearLayout)findViewById(R.id.llnonfriendparticipants);
										LinearLayout grouppartlayout = (LinearLayout)findViewById(R.id.llgroupparticipants);
										
										final TextView timeperiod = new TextView(ctx);
										timeperiod.setText(rowTextView.getText());
										timeperiod.setTextColor(Color.parseColor("#33b5e5"));
										friendpartlayout.addView(timeperiod);
										final TextView timeperiod2 = new TextView(ctx);
										timeperiod2.setText(rowTextView.getText());
										timeperiod2.setTextColor(Color.parseColor("#33b5e5"));
										nonfriendpartlayout.addView(timeperiod2);
										final TextView timeperiod3 = new TextView(ctx);
										timeperiod3.setText(rowTextView.getText());
										timeperiod3.setTextColor(Color.parseColor("#33b5e5"));
										grouppartlayout.addView(timeperiod3);
										// Now we loop through each session.
										int count = 0;
										for (int jj=0;jj<c.getInt("numgrouprows");jj++){
											// We setup the UI to reflect all groups
											if (allgroups[i][jj][10][0].equals("0")){
												final TextView groupdetails = new TextView(ctx);
												groupdetails.setText("    "+allgroups[i][jj][12][0]+"'s Group");
												// Set id as gid
												groupdetails.setId(Integer.parseInt(allgroups[i][jj][0][0]));
												groupdetails.setOnLongClickListener(new View.OnLongClickListener() {
													
													@Override
													public boolean onLongClick(View v) {
														AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ctx);
														final String[][] details = getGroup(Integer.toString(v.getId()));
														Boolean incJoin = true;

														// Count how many trues we have.
														String[] options = new String[1];
														options[0] = "Join";
														dialogBuilder.setTitle("Options:");
														dialogBuilder.setItems(options, new DialogInterface.OnClickListener() {
											    			@Override
															public void onClick(DialogInterface dialog, int item) {
											    				switch (item) {
											    				case 0:
											    					Toast.makeText(getApplicationContext(),
											    							"you choose Join", Toast.LENGTH_SHORT).show();
											    					DialogFragment newFragment = new TimePickerFragment();
											    				    // Send in the gid.
											    					Bundle args = new Bundle();
											    					args.putString("gid", details[0][0]);
											    					newFragment.setArguments(args);
											    					newFragment.show(getSupportFragmentManager(), "custom_dialog");
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
												groupdetails.setOnClickListener(new View.OnClickListener() {
											        @Override
													public void onClick(View v) {
											            // On click display a dialog that displays more details.
											        	// This is designed to avoid cluttering.
											        	AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ctx);
											        	// Set title to be the participant's name
											        	// Getting the row and column of group through the id of the view.
											        	final String[][] details = getGroup(Integer.toString(v.getId()));
											        	
											        	// Set Title.
											        	// Format the hour and times
														String starthour1 = details[5][0];
														if (starthour1.length()==1){
															starthour1 = "0"+starthour1; 
														}
														String startminute1 = details[6][0];
														if (startminute1.length()==1){
															startminute1 = "0"+startminute1; 
														}
														String endhour1 = details[7][0];
														if (endhour1.length()==1){
															endhour1 = "0"+endhour1; 
														}
														String endminute1 = details[8][0];
														if (endminute1.length()==1){
															endminute1 = "0"+endminute1; 
														}
														String lookingforpeople = details[9][0];
														if (lookingforpeople.equals("1")){
															lookingforpeople = "yes";
														} else {
															lookingforpeople = "no";
														}
														String owner = details[12][0];
														dialogBuilder.setTitle(details[12][0]+"'s Group");
														dialogBuilder.setMessage(
											        			"Owner: "+owner+
																"\nDuration: "+starthour1+":"+startminute1+
											        			" - "+endhour1+":"+endminute1+"\nTotal Participants: "+details[1][0]+
											        			"\nLooking for people: "+lookingforpeople);
											        	// If friends,
											        	alertdialog = dialogBuilder.create();
											        	alertdialog.show();
											        }
											    });
												grouppartlayout.addView(groupdetails);
												// Loop through all participants and add each one to the view.
												for (int jjj=0; jjj<Integer.parseInt(allgroups[i][jj][11][0]);jjj++){
													int itemnum = jjj + 13;
													final TextView participant = new TextView(ctx);
													// Set text as name of participant.
													participant.setText("    "+"  "+allgroups[i][jj][itemnum][6]);
													// Set id as pid.
													participant.setId(Integer.parseInt(allgroups[i][jj][itemnum][9]));
													participant.setOnLongClickListener(new View.OnLongClickListener() {
														
														@Override
														public boolean onLongClick(View v) {
															AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ctx);
															final String[] details = getGroupParticipant(Integer.toString(v.getId()));
															// Count how many trues we have.
															String[] options = {"Message","Profile"};
															
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
													participant.setOnClickListener(new View.OnClickListener() {
												        @Override
														public void onClick(View v) {
												            // On click display a dialog that displays more details.
												        	// This is designed to avoid cluttering.
												        	AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ctx);
												        	// Set title to be the participant's name
												        	// Getting the row and column of allparticipants through the id of the view.
												        	String[] details = getGroupParticipant(Integer.toString(v.getId()));
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
															String lookingforpeople = details[7];
															if (lookingforpeople.equals("1")){
																lookingforpeople = "yes";
															} else {
																lookingforpeople = "no";
															}
															dialogBuilder.setTitle(details[6]);
															dialogBuilder.setMessage(
												        			"Duration: "+starthour1+":"+startminute1+
												        			" - "+endhour1+":"+endminute1+"\nGuests: "+details[4]+
												        			"\nMeeting Location: "+details[5]+"\nLooking for people: "+lookingforpeople);
												        	// If friends,
												        	alertdialog = dialogBuilder.create();
												        	alertdialog.show();
												        }
												    });
													// If friends indicate it as so.
													if (allgroups[i][jj][itemnum][8].equals("true")){
														participant.setCompoundDrawablesWithIntrinsicBounds(R.drawable.isfriend,0,0,0);
													} else {
														// Align the text.
														participant.setText("    "+"    "+allgroups[i][jj][itemnum][6]);
													}
													grouppartlayout.addView(participant);
												}
											}
										}
										for (int jj = 0; jj<c.getInt("numrows");jj++){	
											// Now we update the UI to reflect all individuals
											// If privacy turned off.
											if(allparticipants[i][jj][6].equals("0")){
												
												// If friends
												final TextView partdetails = new TextView(ctx);
												partdetails.setText("    "+allparticipants[i][jj][8]);
												// If the user is looking for more players, we will show a picture to indicate this.
												if (allparticipants[i][jj][7].equals("1"))
													partdetails.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lookingforfriend, 0, 0, 0);
												else 
													partdetails.setText("    "+"  "+allparticipants[i][jj][8]);
												// Set id as the pid.
												partdetails.setId(Integer.parseInt(allparticipants[i][jj][11]));
												// Set on click listener to display a dialog.
												partdetails.setOnLongClickListener(new View.OnLongClickListener() {
													
													@Override
													public boolean onLongClick(View v) {
														AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ctx);
														final String[] details = getParticipant(Integer.toString(v.getId()));
														Boolean incJoin = false;
														int total = 2;
														// If the participant indicates they want to find other players, others can choose to join.
														if (details[7].equals("1")){
															incJoin = true;
															total++;
														}

														// Count how many trues we have.
														String[] options = new String[total];
														int count = 0;
														options[count++] = "Message";
														options[count++] = "Profile";
														if (incJoin)
															options[count++] = "Join";
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
											    							details[8].split(" ")[0]);
											    					startActivity(iMessage);
											    					break;

											    				case 1:
											    					Toast.makeText(getApplicationContext(),
											    							"you choose Profile", Toast.LENGTH_SHORT).show();
											    					Intent iProfile = new Intent(
											    							"com.example.gymmies.Friendprofile");
											    					iProfile.putExtra("friendname", details[8].split(" ")[0]);
											    					startActivity(iProfile);
											    					break;
											    				case 2:
											    					Toast.makeText(getApplicationContext(),
											    							"you choose Join", Toast.LENGTH_SHORT).show();
											    					// Create new dialog.
											    					
											    					DialogFragment newFragment = new TimePickerFragment();
											    				    // Send in the gid.
											    					Bundle args = new Bundle();
											    					args.putString("gid", details[12]);
											    					newFragment.setArguments(args);

											    					
											    					newFragment.show(getSupportFragmentManager(), "custom_dialog");
											    				    
//											    					TimePickerDialog k = new TimePickerDialog(ctxnons, item, null, item, item, incJoin);
//											    					k.show();
											    					
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
														dialogBuilder.setTitle(details[8]);
														dialogBuilder.setMessage(
											        			"Duration: "+starthour1+":"+startminute1+
											        			" - "+endhour1+":"+endminute1+"\nGuests: "+details[4]+
											        			"\nMeeting Location: "+details[5]);
											        	// If friends,
											        	alertdialog = dialogBuilder.create();
											        	alertdialog.show();
											        }
											    });
												// If the participant is a friend of the current user, then we indicate that.
												if (allparticipants[i][jj][10].equals("true")){
													friendpartlayout.addView(partdetails);
												} else {
													nonfriendpartlayout.addView(partdetails);
												}
											}
										}
									}
									
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}	
								// Display a toast to provide instructions
								Toast.makeText(getApplicationContext(),
		    							"Click on a Name to view details\nHold on a name to view options", Toast.LENGTH_LONG).show();
								
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
			return view;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
		    case R.id.btn_join:
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
	        	for (int i = 0; i<totaltimes;i++){
	        		// If the time is within the specified time, and the start time < end time.
	        		
	        		int starttime = Integer.parseInt(strstarthour+strstartminute);
	        		int endtime = Integer.parseInt(strendhour+strendminute);
	        		System.out.println("1. selectedstart:"+starttime);
	        		System.out.println("2. selectedend:"+endtime);
	        		System.out.println("1. availablestart:"+availabletimes[i][0]);
	        		System.out.println("2. availableend:"+availabletimes[i][1]);
	        		
	        		if ((starttime >= availabletimes[i][0] && endtime <= availabletimes[i][1])
	        				&& (starttime<endtime)){
	        			// If it satisfies these conditions then the user indicated time lies within this availabletime frame.
	        			outofrange = false;
	        			tid = availabletimes[i][2];
	        			break;
	        		}
	        	}
	        	// If the times satisfy at least one available time.
	        	if (outofrange == false){
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
	        			// If it only contains numbers, then we accept it.
	        			if (stramountofguests.equals(""))
	        				amountofguests = "0";
	        			else
	        				amountofguests = stramountofguests;
	        			// Get the gid, by getting the participant.
	        			
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
				pDialog = new ProgressDialog(CalendarActivity.this);
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
				     runOnUiThread(new Runnable() {
						public void run() {
							invitefriends(Integer.parseInt(params[12]));
					}});
			    	 
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
	
	public String[] getParticipant(String pid){
		// Loop through allparticipants
		for (int i = 0;i<totaltimes;i++){
			for (int j=0;j<allparticipants[i].length;j++){
				if(allparticipants[i][j][11].equals(pid)){
					return allparticipants[i][j];
				}
			}
		}
		return null;
	}
	
	public String[][] getGroup(String gid){
		for (int i = 0;i<totaltimes;i++){
			for (int j=0;j<allgroups[i].length;j++){
				if(allgroups[i][j][0][0].equals(gid)){
					return allgroups[i][j];
				}
			}
		}
		return null;
	}
	
	public String[] getGroupParticipant(String pid){
		// Loop through allparticipants
		for (int i = 0;i<totaltimes;i++){
			for (int j=0;j<allgroups[i].length;j++){
				for (int k = 0; k<Integer.parseInt(allgroups[i][j][11][0]); k++){
					int itemnum = k + 13;
					if(allgroups[i][j][itemnum][9].equals(pid)){
						return allgroups[i][j][itemnum];
					}
				}
			}
		}
		return null;
	}
	
	
	
	public void invitefriends(final int gid){
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ctx);
		// Count how many trues we have.
		String[] options = new String[1];
		options[0] = "";
		dialogBuilder.setTitle("Do you want to invite your friends?");
		dialogBuilder.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int id) {
						Intent invfriends = new Intent(ctx, FriendInvite.class);
						invfriends.putExtra("gid", gid);
						startActivity(invfriends);
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
	}
}
