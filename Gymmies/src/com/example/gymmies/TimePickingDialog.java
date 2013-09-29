package com.example.gymmies;

import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

public class TimePickingDialog extends Activity implements android.view.View.OnClickListener{

	public Activity act;
	public Dialog dialog;
	public Button cancel, join;
	public TimePicker tp1, tp2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_time_picking_dialog);
		setTitle("Set join time");
	    setContentView(R.layout.custom_dialog);
	    join = (Button) findViewById(R.id.btn_join);
	    cancel = (Button) findViewById(R.id.btn_cancel);
	    join.setOnClickListener(this);
	    cancel.setOnClickListener(this);
	    tp1 = (TimePicker) findViewById(R.id.tpstarttime3);
		tp1.setIs24HourView(true);
		tp2 = (TimePicker) findViewById(R.id.tpendtime3);
		tp2.setIs24HourView(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.time_picking_dialog, menu);
		return true;
	}
	
	public void onClick(View v) {
		switch (v.getId()) {
		    case R.id.btn_join:
		    	Toast.makeText(act.getApplicationContext(), tp1.getCurrentHour()+":"+tp1.getCurrentMinute()+" - "+tp2.getCurrentHour()+":"+tp2.getCurrentMinute(),
		                Toast.LENGTH_LONG).show();
		    	break;
		    case R.id.btn_cancel:
		    	break;
		    default:
		    	break;
	    }
	}

}
