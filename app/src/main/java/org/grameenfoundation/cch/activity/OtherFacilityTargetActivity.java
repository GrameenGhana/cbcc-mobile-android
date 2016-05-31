	package org.grameenfoundation.cch.activity;

import java.util.ArrayList;

import org.digitalcampus.mobile.learningGF.R;
import org.digitalcampus.oppia.application.DbHelper;
import org.grameenfoundation.adapters.FacilityTargetAdapter;
import org.grameenfoundation.cch.model.FacilityTargets;
import org.grameenfoundation.cch.model.FamilyPlanningFacilityTargetActivity;
import org.grameenfoundation.poc.BaseActivity;
import org.joda.time.DateTime;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class OtherFacilityTargetActivity extends BaseActivity{

	private Context context;
	private DbHelper db;
	private DateTime currentDate;
	private ArrayList<FacilityTargets> facilityTargets;
	private FacilityTargetAdapter adapter;
	private ListView listView;
	private Button button_reminders;
	private CheckBox dailyFilter;
	private CheckBox weeklyFilter;
	private CheckBox monthly;
	private Button button_update;
	
	 public OtherFacilityTargetActivity(){

	 }
	 @Override
		public void onCreate(Bundle savedInstanceState) {
		 	super.onCreate(savedInstanceState);
		 	setContentView(R.layout.activity_facility_targets_view);
         	getSupportActionBar().setTitle("Planner");
         	getSupportActionBar().setSubtitle("Other Facility Targets");
		 	context=OtherFacilityTargetActivity.this;
		    db=new DbHelper(context);
		    currentDate=new DateTime();
		    listView=(ListView) findViewById(R.id.listView_targets);
		    facilityTargets=new ArrayList<FacilityTargets>();
		    dailyFilter=(CheckBox) findViewById(R.id.checkBox_daily);
		    weeklyFilter=(CheckBox) findViewById(R.id.checkBox_weekly);
		    monthly=(CheckBox) findViewById(R.id.checkBox_monthly);
		    facilityTargets=db.getTargetsForMonthViewAgeGroups(currentDate.toString("MMMM"),"50-60");
		    adapter=new FacilityTargetAdapter(context,facilityTargets);
		    listView.setAdapter(adapter);
		    
		    dailyFilter.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					  if(dailyFilter.isChecked()){
						  adapter.getFilter().filter(dailyFilter.getText().toString());
					  }else {
						  	facilityTargets=db.getTargetsForMonthViewAgeGroups(currentDate.toString("MMMM"),"50-60");
							adapter.updateAdapter(facilityTargets);
							listView.setAdapter(adapter);
					  }
				}
			});
		    weeklyFilter.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					 if(weeklyFilter.isChecked()){
						  adapter.getFilter().filter(weeklyFilter.getText().toString());
					  }else {
						  	facilityTargets=db.getTargetsForMonthViewAgeGroups(currentDate.toString("MMMM"),"50-60");
							adapter.updateAdapter(facilityTargets);
							listView.setAdapter(adapter);
					  }
				}
			});
		    
		    monthly.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					 if(monthly.isChecked()){
						  adapter.getFilter().filter(monthly.getText().toString());
					  }else {
						  	facilityTargets=db.getTargetsForMonthViewAgeGroups(currentDate.toString("MMMM"),"50-60");
							adapter.updateAdapter(facilityTargets);
							listView.setAdapter(adapter);
					  }
				}
			});
		    button_reminders=(Button) findViewById(R.id.button_reminders);
		    button_reminders.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent=new Intent(context,ReminderActivity.class);
					intent.putExtra("type", "50-60");
					startActivity(intent);
				}
			});
		    button_update=(Button) findViewById(R.id.button_update);
		    button_update.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent=new Intent(context,FacilityTargetBulkUpdateActivity.class);
					intent.putExtra("category", "Other");
					startActivity(intent);
				}
			});
	 }

	@Override
	public void onResume(){
		super.onResume();
		facilityTargets=db.getTargetsForMonthViewAgeGroups(currentDate.toString("MMMM"),"50-60");
		adapter.updateAdapter(facilityTargets);
		listView.setAdapter(adapter);
	}
}
