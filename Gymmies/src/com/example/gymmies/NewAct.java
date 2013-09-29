package com.example.gymmies;

import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.Toast;
import android.widget.CalendarView.OnDateChangeListener;

public class NewAct extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.actvity_actlist);
		Bundle extras = getIntent().getExtras();
		String activity = extras.getString("activityname");
		System.out.println(activity);
		Toast.makeText(getApplicationContext(), activity, 0).show();
		CalendarView calendarView = (CalendarView) findViewById(R.id.calendarView1);
		calendarView.setShowWeekNumber(false);
		calendarView.setShownWeekCount(5);
		calendarView.setOnDateChangeListener(new OnDateChangeListener() {
		
			@Override
			public void onSelectedDayChange(CalendarView view, int year,
					int month, final int dayOfMonth) {

				HashMap<Integer, String> d = new HashMap<Integer, String>();
				d.put(0, "January");
				d.put(1, "February");
				d.put(2, "March");
				d.put(3, "April");
				d.put(4, "May");
				d.put(5, "June");
				d.put(6, "July");
				d.put(7, "August");
				d.put(8, "September");
				d.put(9, "October");
				d.put(10, "November");
				d.put(11, "December");

				String text = d.get(month) + " " + dayOfMonth;
				
				Toast.makeText(getApplicationContext(), "" + text, 0).show();
				Intent ourIntent = new Intent(NewAct.this, Volleyball.class);
				// Add in the date, time and activity selected.
				
				startActivity(ourIntent);
			}
		});
	}
}

// calendarView.setOnClickListener(new View.OnClickListener() {
//
// @Override
// public void onClick(View v) {
// // TODO Auto-generated method stub
//
// }
// });
