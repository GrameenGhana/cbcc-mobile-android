package org.grameenfoundation.poc;

import org.digitalcampus.mobile.learningGF.R;
import org.digitalcampus.oppia.application.DbHelper;
import org.digitalcampus.oppia.application.MobileLearning;
import org.digitalcampus.oppia.utils.UIUtils;
import org.grameenfoundation.cch.model.POCSections;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class PostnatalCareCounsellingTopicsActivity extends BaseActivity {

	private ListView listView_counselling;
//	private Context mContext;
	private DbHelper dbh;
	private Long start_time;
	private Long end_time;
	private JSONObject json;
	private ArrayList<POCSections> list;
	private String first_file;
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_postnatal_care_counselling);
        getSupportActionBar().setTitle("Point of Care");
        getSupportActionBar().setSubtitle("PNC Counselling");
	    mContext=PostnatalCareCounsellingTopicsActivity.this;
	    dbh=new DbHelper(mContext);
		list=new ArrayList<POCSections>();
		list=dbh.getPocSections("PNC Counselling");
		listView_counselling=(ListView) findViewById(R.id.listView_counsellingTopics);
		ListAdapter adapter=new ListAdapter(mContext, list);
		listView_counselling.setAdapter(adapter);
	    start_time=System.currentTimeMillis();
	    json=new JSONObject();
	    try {
			json.put("page", "PNC Counselling");
			json.put("section", MobileLearning.CCH_COUNSELLING);
			json.put("ver", dbh.getVersionNumber(mContext));
			json.put("battery", dbh.getBatteryStatus(mContext));
			json.put("device", dbh.getDeviceName());
			json.put("imei", dbh.getDeviceImei(mContext));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	    /*
	    String[] items={"Breast Problems","Complication Readiness & Newborn Dangers",
	    				"Family Planning in the Postpartum Period","Home Care for the Infant",
	    				"Immunisation Schedule for Infants","Infant Feeding","Kangaroo Mother Care at Home",
	    				"Keeping Baby Warm & Breastfeeding on the Way to the Hospital",
	    				"Malaria Prevention","Nutrition Requirements for the Mother",
	    				"Postpartum Exercises","Psychosocial Support","Rest & Activity",
	    				"Self-Care & Hygiene","Sexual Relations & Safer Sex","TT Immunisation Schedule",
	    				"Treating Local Infections at Home","When to Return for Care",
	    				"Treating Some Dehydration with ORS ","Treatment of Uncomplicated Malaria in Adolescents and Adults",
	    				"Treatment of Bloody Diarrhoea with Benzylpenicillin & Gentamicin"};
	    ListAdapter adapter=new ListAdapter(mContext,items);
	    listView_counselling.setAdapter(adapter);*/
	    listView_counselling.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				File folder = new File(list.get(position).getSectionUrl());
				if (folder.exists()) {
					File[] listOfFiles = folder.listFiles();
					for (int i = 0; i < listOfFiles.length; i++) {
						String filename = listOfFiles[i].getName();
						int pos = filename.lastIndexOf(".");
						if (pos > 0) {
							filename = filename.substring(0, pos);
						}
						if (filename.endsWith("1")) {
							first_file = filename;
						}
					}
					Intent intent;
					intent = new Intent(mContext, POCDynamicActivity.class);
					intent.putExtra("shortname", list.get(position).getSectionShortname());
					intent.putExtra("link", list.get(position).getSectionShortname() + File.separator + first_file);
					startActivity(intent);
					overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_right);
				} else {
					UIUtils.showAlert(mContext, "Alert", "Click on load content to proceed");
				}
			}
	    	
	    });
	}
	class ListAdapter extends BaseAdapter{
		Context mContext;
		ArrayList<POCSections> items;
		public LayoutInflater minflater;
		public ListAdapter(Context mContext,ArrayList<POCSections>items){
			this.mContext=mContext;
			this.items=items;
			minflater = LayoutInflater.from(mContext);
		}
		@Override
		public int getCount() {
			return items.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if( convertView == null ){
				convertView = minflater.inflate(R.layout.other_listview_single,parent, false);
			}
			ImageView image=(ImageView) convertView.findViewById(R.id.imageView1);
			File file=new File(items.get(position).getSectionUrl());
			if(file.exists()){
				image.setImageDrawable(getResources().getDrawable(R.drawable.ic_special_bullet));
			}else{
				image.setImageDrawable(getResources().getDrawable(R.drawable.ic_special_bullet_downloaded));
			}
			TextView text=(TextView) convertView.findViewById(R.id.textView_otherCategory);
			text.setText(items.get(position).getSectionName());

			return convertView;
		}

	}
	public void onBackPressed()
	{
	    end_time=System.currentTimeMillis();
		dbh.insertCCHLog("Point of Care", json.toString(), start_time.toString(), end_time.toString());
		finish();
	}
}
