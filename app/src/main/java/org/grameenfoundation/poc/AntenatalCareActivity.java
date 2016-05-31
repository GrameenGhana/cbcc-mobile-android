package org.grameenfoundation.poc;

import org.digitalcampus.mobile.learningGF.R;
import org.digitalcampus.oppia.application.DbHelper;
import org.digitalcampus.oppia.application.MobileLearning;
import org.digitalcampus.oppia.utils.UIUtils;
import org.grameenfoundation.adapters.AntenatalCareBaseAdapter;


import org.grameenfoundation.cch.model.POCSections;
import org.grameenfoundation.cch.tasks.POCContentLoaderTask;
import org.grameenfoundation.cch.tasks.POCRefreshContentTask;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;

public class AntenatalCareActivity extends BaseActivity implements OnItemClickListener{

	private ListView listView_ancMenu;
//	private Context mContext;
	private DbHelper dbh;
	private Long start_time;
	private Long end_time;
	private JSONObject json;
	private Button button_load;
	private Button button_refresh;
	private ArrayList<POCSections> list;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_antenatal_care);
	    mContext=AntenatalCareActivity.this;
	    dbh=new DbHelper(mContext);
	    start_time=System.currentTimeMillis();
		button_load=(Button) findViewById(R.id.button_load);
		button_load.setVisibility(View.VISIBLE);
		button_refresh=(Button) findViewById(R.id.button_refresh);
		button_refresh.setVisibility(View.VISIBLE);
        getSupportActionBar().setTitle("Point of Care");
        getSupportActionBar().setSubtitle("Antenatal Care");
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setIcon(R.drawable.app_icon);
	    json=new JSONObject();
	    try {
			json.put("page", "Antenatal Care");
			json.put("section", "");
			json.put("ver", dbh.getVersionNumber(mContext));
			json.put("battery", dbh.getBatteryStatus(mContext));
			json.put("device", dbh.getDeviceName());
			json.put("imei", dbh.getDeviceImei(mContext));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	    listView_ancMenu=(ListView) findViewById(R.id.listView_antenatalCare);
	    listView_ancMenu.setOnItemClickListener(this);
	    int[] images={R.drawable.ic_diagnostic,R.drawable.ic_counselling,R.drawable.ic_calculator,R.drawable.ic_references};
	    String[] category={"Diagnostic Tool","Counselling per Trimester","Calculators","References"};
	    AntenatalCareBaseAdapter adapter=new AntenatalCareBaseAdapter(mContext,images,category);
	    listView_ancMenu.setAdapter(adapter);
		button_load.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(dbh.isOnline()){
					POCContentLoaderTask task=new POCContentLoaderTask(mContext,"ANC");
					task.execute(getResources().getString(R.string.serverDefaultAddress)+ File.separator+MobileLearning.POC_SERVER_DOWNLOAD_PATH+"alluploadsanc");
				}else{
					UIUtils.showAlert(mContext, "Alert", "Check your internet connection and try again");
				}

			}
		});
		button_refresh.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(dbh.isOnline()){
					POCRefreshContentTask task=new POCRefreshContentTask(mContext,"ANC");
					task.execute(getResources().getString(R.string.serverDefaultAddress)+File.separator+MobileLearning.POC_SERVER_DOWNLOAD_PATH+"alluploadsanc");
				}else{
					UIUtils.showAlert(mContext, "Alert", "Check your internet connection and try again");
				}

			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent;
		switch(position){
		
		case 0:
			list=new ArrayList<POCSections>();
			list=dbh.getPocSections("ANC Diagnostic");
			if(list.size()<=0){
				UIUtils.showAlert(mContext, "Alert", "Click on load content to proceed");
			}else {
				intent = new Intent(mContext, DiagnosticToolActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_right);
			}
			break;
		case 1:
			list=new ArrayList<POCSections>();
			list=dbh.getPocSections("ANC Counselling");
			if(list.size()<=0){
				UIUtils.showAlert(mContext, "Alert", "Click on load content to proceed");
			}else {
				intent = new Intent(mContext, CounsellingPerTrimesterActivtiy.class);
				startActivity(intent);
				overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_right);
			}
			break;
		case 2:
			intent=new Intent(mContext, CalculatorsMenuActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_right);
			break;
		case 3:
			list=new ArrayList<POCSections>();
			list=dbh.getPocSections("ANC References");
			if(list.size()<=0){
				UIUtils.showAlert(mContext, "Alert", "Click on load content to proceed");
			}else {
				intent = new Intent(mContext, ReferencesMenuActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_right);
			}
			break;
		}
		
		
	}
	public void onBackPressed()
	{
	    end_time=System.currentTimeMillis();
		//dbh.insertCCHLog("Point of Care", "Antenatal Care", start_time.toString(), end_time.toString());
	    dbh.insertCCHLog("Point of Care", json.toString(), start_time.toString(), end_time.toString());
		finish();
	}

}
