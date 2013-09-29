package com.example.gymmies;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class Volleyball extends ListActivity {

	String times[] = { "7:00", "7:30", "8:00", "8:30", "9:00", "9:30", "10:00",
			"10:30", "11:00", "11:30", "12:00", "12:30", "1:00", "1:30",
			"2:00", "2:30", "3:00", "3:30", "4:00", "4:30", "5:00", "5:30",
			"6:00", "6:30", "7:00", "7:30", "8:00", "8:30", "9:00", "9:30",
			"10:00" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setListAdapter(new ArrayAdapter<String>(Volleyball.this,
				android.R.layout.simple_list_item_1, times));
	}

	// @Override
	// protected void onListItemClick(ListView l, View v, int position, long id)
	// {
	// super.onListItemClick(l, v, position, id);
	// String act = times[position];
	// try {
	// Class ourClass = ourClass = Class.forName("com.example.yolo." + act);
	// Intent ourIntent = new Intent(Volleyball.this, ourClass);
	// startActivity(ourIntent);
	// } catch (ClassNotFoundException e) {
	// e.printStackTrace();
	// }
	// }
}
