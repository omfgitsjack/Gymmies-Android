package com.example.gymmies;

import static com.example.gymmies.CommonUtilities.SERVER_URL;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.gymmies.utilities.JSONParser;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;


public class CalendarView extends Activity {

	public Calendar month;
	public CalendarAdapter adapter;
	public Handler mhandler;
	public ArrayList<String> items; // container to store some random calendar items
	public String activityname;
	Context ctx;
	
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.calendar);
	    ctx = this;
	    month = Calendar.getInstance();
	    onNewIntent(getIntent());
	    Bundle extras = getIntent().getExtras();
	    activityname = extras.getString("activityname");
	    
	    items = new ArrayList<String>();
	    adapter = new CalendarAdapter(this, month);
	    
	    GridView gridview = (GridView) findViewById(R.id.gridview1);
	    gridview.setAdapter(adapter);
	    
	    new updateAvailableDates().execute(activityname ,Integer.toString(month.get(Calendar.YEAR)), Integer.toString(month.get(Calendar.MONTH)+1));
	    
	    TextView title  = (TextView) findViewById(R.id.title);
	    title.setText(android.text.format.DateFormat.format("MMMM yyyy", month));
	    
	    TextView previous  = (TextView) findViewById(R.id.previous);
	    previous.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(month.get(Calendar.MONTH)== month.getActualMinimum(Calendar.MONTH)) {				
					month.set((month.get(Calendar.YEAR)-1),month.getActualMaximum(Calendar.MONTH),1);
				} else {
					month.set(Calendar.MONTH,month.get(Calendar.MONTH)-1);
				}
				refreshCalendar();
			}
		});
	    
	    TextView next  = (TextView) findViewById(R.id.next);
	    next.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(month.get(Calendar.MONTH)== month.getActualMaximum(Calendar.MONTH)) {				
					month.set((month.get(Calendar.YEAR)+1),month.getActualMinimum(Calendar.MONTH),1);
				} else {
					month.set(Calendar.MONTH,month.get(Calendar.MONTH)+1);
				}
				refreshCalendar();
				
			}
		});
	    
	    // When user clicks on a date.
		gridview.setOnItemClickListener(new OnItemClickListener() {
		    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		    	TextView date = (TextView)v.findViewById(R.id.date);
		        if(date instanceof TextView && !date.getText().equals("")) {
		        	// IF the specfied date is the date when the activity actually takes place (as indicated), we will start new intent.
		        	System.out.println("datelength of "+date.getText()+ ":"+date.getText().length());
		        	String thedate = date.getText().toString();
		        	if (thedate.length()==1){
		        		thedate = "0"+thedate;
		        	}
		        	if(date.getText().length()>0 && items!=null && items.contains(thedate)) {        	
		        		Intent intent = new Intent(ctx, CalendarActivity.class);
			        	// return chosen date as string format 
			        	intent.putExtra("date", android.text.format.DateFormat.format("yyyy-MM", month)+"-"+thedate);
			        	intent.putExtra("activityname", activityname);
			        	startActivity(intent);
		            } else {
		            	//Indicate that the date selected isn't available for the given activity.
		            	Toast.makeText(getApplicationContext(),
								"Select a date with a dot", Toast.LENGTH_LONG)
								.show();
		            }
		        	
		        }
		        
		    }
		});
	}
	
	public void refreshCalendar()
	{
		TextView title  = (TextView) findViewById(R.id.title);
		
		adapter.refreshDays();
		adapter.notifyDataSetChanged();
		System.out.println("asyntask params:"+activityname+" "+Integer.toString(month.get(Calendar.YEAR))+" "+Integer.toString(month.get(Calendar.MONTH)));
		new updateAvailableDates().execute(activityname,Integer.toString(month.get(Calendar.YEAR)), Integer.toString(month.get(Calendar.MONTH)+1));			
		
		title.setText(android.text.format.DateFormat.format("MMMM yyyy", month));
	}
	
	public void onNewIntent(Intent intent) {
		String date = intent.getStringExtra("date");
		String[] dateArr = date.split("-"); // date format is yyyy-mm-dd
		month.set(Integer.parseInt(dateArr[0]), Integer.parseInt(dateArr[1]), Integer.parseInt(dateArr[2]));
	}
	
	
	class updateAvailableDates extends AsyncTask<String, String, String> {
		JSONParser jParser = new JSONParser();
		protected void onPreExecute() {
			super.onPreExecute();
 	    }
		
	     @Override
	    protected String doInBackground(String... params) {
		     final int success;
		     System.out.println("executing async task preexec");
		     // Prepare parameters to be sent
		     List<NameValuePair> params2 = new ArrayList<NameValuePair>();
		     System.out.println("Params inside ASync:"+params[0]+" "+params[1]+" "+params[2]);
		     params2.add(new BasicNameValuePair("activityname", params[0]));
		     params2.add(new BasicNameValuePair("year", params[1]));
		     params2.add(new BasicNameValuePair("month", params[2]));
		     try {
			     final JSONObject json = jParser.makeHttpRequest(SERVER_URL + "/schedule/getmonth.php", "POST",
			                     params2);
		    	 success = json.getInt("success");
		    	 runOnUiThread(new Runnable() {
						public void run() {
							items.clear();
						
							try{
								if (success == 1) {
						    		 // Indicate which days are available through updating the item array.
						    		 JSONArray a = json.getJSONArray("dates");
						    		 for (int i=0;i<a.length();i++){
						    			 items.add(a.getJSONObject(i).getString("day"));
						    		 }
						    	 }
			    	 

								adapter.setItems(items);
								adapter.notifyDataSetChanged();
							} catch (Exception j){
								j.printStackTrace();
							}
						}
					});
	    		 
		     } catch (Exception e) {
		             e.printStackTrace();
		     }
		     return null;
	     }
	
	     protected void onPostExecute(String msg) {
	     }
	 }
}