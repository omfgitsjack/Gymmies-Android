package com.example.gymmies;
import com.example.gymmies.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;


public class TimePickerDialog extends Dialog implements android.view.View.OnClickListener{
	public TimePickerDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public Activity act;
	public Dialog dialog;
	public Button cancel, join;
	public TimePicker tp1, tp2;
	
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
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
	public void onClick(View v) {
		switch (v.getId()) {
		    case R.id.btn_join:
		    	Toast.makeText(act.getApplicationContext(), tp1.getCurrentHour()+":"+tp1.getCurrentMinute()+" - "+tp2.getCurrentHour()+":"+tp2.getCurrentMinute(),
		                Toast.LENGTH_LONG).show();
		    	break;
		    case R.id.btn_cancel:
		    	dismiss();
		    	break;
		    default:
		    	break;
	    }
	}
	
}
