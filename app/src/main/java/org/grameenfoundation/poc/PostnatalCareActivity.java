package org.grameenfoundation.poc;

import org.digitalcampus.mobile.learningGF.R;
import org.digitalcampus.oppia.application.DbHelper;
import org.grameenfoundation.adapters.AntenatalCareBaseAdapter;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class PostnatalCareActivity extends BaseActivity implements AnimationListener, OnItemClickListener{

//	Context mContext;
	private ListView listView_postnatal;
	private ImageButton imageButton_baby;
	private ImageButton imageButton_mother;
	private LinearLayout linearLayout_counselling;
	private LinearLayout linearLayout_diagnosticDetails;
	private LinearLayout linearLayout_diagnostic;
	private LinearLayout linearLayout_quickReads;
	private Animation slide_up;
	private Animation slide_down;
	private DbHelper dbh;
	private Long start_time;
	private Long end_time;
	private JSONObject json;
	private ListView listView_ancMenu;

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    mContext = PostnatalCareActivity.this;
	    setContentView(R.layout.activity_antenatal_care);
        getSupportActionBar().setTitle("Point of Care");
        getSupportActionBar().setSubtitle("Postnatal Care");

	    dbh=new DbHelper(mContext);
	    json=new JSONObject();
	    try {
			json.put("page", "Postnatal Care");
			json.put("section", "");
			json.put("ver", dbh.getVersionNumber(mContext));
			json.put("battery", dbh.getBatteryStatus(mContext));
			json.put("device", dbh.getDeviceName());
			json.put("imei", dbh.getDeviceImei(mContext));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	    start_time=System.currentTimeMillis();
		listView_ancMenu=(ListView) findViewById(R.id.listView_antenatalCare);
		listView_ancMenu.setOnItemClickListener(this);
		int[] images={R.drawable.baby,R.drawable.mother,R.drawable.ic_counselling,R.drawable.ic_calculator,R.drawable.ic_references};
		String[] category={"Diagnostic Tool Baby","Diagnostic Tool Mother","Counselling Topics","References"};
		AntenatalCareBaseAdapter adapter=new AntenatalCareBaseAdapter(mContext,images,category);
		listView_ancMenu.setAdapter(adapter);
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
		Intent intent;
		switch(i){

			case 0:
				intent=new Intent(mContext, PostnatalCareSectionActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_right);
				break;
			case 1:
				intent=new Intent(mContext, PostnatalCareMotherDiagnosticToolActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_right);
				break;
			case 2:
				intent=new Intent(mContext, PostnatalCareCounsellingTopicsActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_right);
				break;
			case 3:
				intent=new Intent(mContext, QuickReadsMenuActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_right);
				break;
		}
	}


	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onAnimationEnd(Animation animation) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub
		
	}
	
	public void onBackPressed()
	{
	    end_time=System.currentTimeMillis();
		dbh.insertCCHLog("Point of Care", json.toString(), start_time.toString(), end_time.toString());
		finish();
	}
}
