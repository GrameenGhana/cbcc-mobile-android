package org.digitalcampus.oppia.activity;

import java.util.ArrayList;

import org.digitalcampus.mobile.learningGF.R;
import org.digitalcampus.oppia.application.DbHelper;
import org.digitalcampus.oppia.model.User;
import org.digitalcampus.oppia.service.TrackerService;
import org.grameenfoundation.cch.activity.AchievementCenterActivity;
import org.grameenfoundation.cch.activity.LearningCenterMenuActivity;
import org.grameenfoundation.cch.activity.StayingWellActivity;
import org.grameenfoundation.cch.activity.UserRegistrationActivity;
import org.grameenfoundation.poc.PointOfCareActivity;
import org.grameenfoundation.schedulers.UserDetailsUpdateService;
import org.grameenfoundation.cch.model.RoutineActivity;
import org.grameenfoundation.cch.model.RoutineActivityDetails;
import org.grameenfoundation.cch.utils.CCHTimeUtil;
import org.joda.time.DateTime;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MainScreenActivity extends AppCompatActivity  implements OnSharedPreferenceChangeListener {

	private static Context mContext;
	private static TextView status;
	public static final String TAG = MainScreenActivity.class.getSimpleName();

	SectionsPagerAdapter mSectionsPagerAdapter;
    static ViewPager mViewPager;
	private static DbHelper dbh;
	private SharedPreferences prefs;
	private LinearLayout planner;
	private LinearLayout poc;
	private LinearLayout achievements;
	private LinearLayout stayingWell;
	private LinearLayout learning;
	private Animation slide_up;
	private CCHTimeUtil timeUtils;
	private DateTime today;
	private String name;
	private RadioGroup reminder;
	private RadioButton weekRadioButton;
	private RadioButton dayRadioButton;
	public static Dialog dialog ;
	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_new_mainscreen);

	    mContext=MainScreenActivity.this;
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setIcon(R.drawable.app_icon);
        getSupportActionBar().setTitle("Welcome");
        getSupportActionBar().setSubtitle("Home Page");
		dbh = new DbHelper(getApplicationContext());
		timeUtils=new CCHTimeUtil();
		today=new DateTime();
		 try{
			dbh.alterTables();
			dbh.alterCourseTable();
			dbh.updateDateDefault();
			dbh.alterUserFaciityTable();
			dbh.alterUserTableDistrict();
			dbh.alterUserTableForSubdistrict();
			dbh.alterUserTableForZone();
			dbh.alterUserTable(); 
			dbh.alterCourseTableGroup();
			dbh.alterUserTableDistrict();
			dbh.alterPOCSection();
			dbh.alterPOCSectionUpdate();
			prefs = PreferenceManager.getDefaultSharedPreferences(this);
			name=prefs.getString("first_name", "name");
			if(dbh.isOnline()){
				Intent service = new Intent(this, UserDetailsUpdateService.class);
				this.startService(service);
			}
		 }catch(Exception e){
			 e.printStackTrace();
		 }
		prefs.registerOnSharedPreferenceChangeListener(this);
		PreferenceManager.setDefaultValues(this, R.xml.prefs, false);
		
		Intent service = new Intent(this, TrackerService.class);
		Bundle tb = new Bundle();
		tb.putBoolean("backgroundData", true);
		service.putExtras(tb);
		this.startService(service);

		planner=(LinearLayout) findViewById(R.id.planner);
		poc=(LinearLayout) findViewById(R.id.poc);
		learning=(LinearLayout) findViewById(R.id.learning);
		stayingWell=(LinearLayout) findViewById(R.id.stayingWell);
		achievements=(LinearLayout) findViewById(R.id.achievements);

		planner.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(mContext, UserRegistrationActivity.class);
				startActivity(intent);
				 overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_right);
				
			}
		});
		
		poc.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(mContext, PointOfCareActivity.class);
				startActivity(intent);
				 overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_right);
				
			}
		});
		learning.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), LearningCenterMenuActivity.class);
	            startActivity(intent);	
	            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_right);
				
			}
		});
		stayingWell.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), StayingWellActivity.class);
				startActivity(intent);
				 overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_right);
				
			}
		});
		achievements.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), AchievementCenterActivity.class);
	            startActivity(intent);	
	            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_right);
				
			}
		});
	    mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager2);
        
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);
        
        try 
	    {
			if (!(getIntent().getStringExtra("FRAGMENT_IDX")).isEmpty()) {	
				int page = Integer.parseInt(getIntent().getStringExtra("FRAGMENT_IDX"));
				mViewPager.setCurrentItem(page, true);	
			}				
		} catch (NullPointerException e) { Log.e(TAG,"Trying to switch panes failed :("); }
	  
	}
	
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
                super(fm);
        }
        
        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position) {
                Fragment fragment = null;
                if(position==0 ){
                	 fragment= new EventsSummary();   
                }else if(position==1){
                	 fragment= new RoutineActivityDetails(mViewPager);
                }
                return fragment;
        }

        @Override
        public int getCount() {
                return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "OBJECT " + (position + 1);
        }
}
	
	 public static class EventsSummary extends Fragment {
		 View rootView;
		 private TextView event_number;
		 private TextView textView_eventsClickHere;
		 private TextView textView_eventTargetsNumber;
		 private TextView textView_clickHere;
		 private TextView textView_routinesNumber;
		 private TextView textView_routinesClickHere;
		 private TextView tv8;
		 String due_date;

		 private SharedPreferences prefs;
		 private String name;
		 private String user_first_name;

		 private ArrayList<User> firstName;
		private int numactivities;
		 public EventsSummary(){
			 
		 }

		 public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			 	rootView=inflater.inflate(R.layout.events_pager_layout,null,false);
			 	prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
			    name=prefs.getString("first_name", "name");
			    dbh=new DbHelper(getActivity());
			    firstName=dbh.getUserFirstName(name);
			    status=(TextView) rootView.findViewById(R.id.textView_status);

			    try{
		    if(firstName.size()>0 &&firstName!=null){
			    	user_first_name=firstName.get(0).getFirstname();
			    }else if(firstName.size()==0&&firstName!=null){
			    	user_first_name.equals(" ");
			    
			    }
		    }catch(Exception e){
		    e.printStackTrace();	
		    }
		    	status.setText("Good "+dbh.getTime()+", "+user_first_name+"!");


				/* Routine Info */
				ArrayList<RoutineActivity> todos = new ArrayList<RoutineActivity>();
				todos=dbh.getSWRoutineActivities();
				if(todos!=null){
					numactivities = todos.size();
				}else{
					numactivities=0;
				}
			    textView_routinesNumber = (TextView) rootView.findViewById(R.id.textView_routinesNumber);
				textView_routinesNumber.setText(String.valueOf(numactivities));
			    tv8 = (TextView) rootView.findViewById(R.id.textView8);
			    tv8.setText(" activity(ies) this "+dbh.getTime()+".");
				
			    textView_routinesClickHere = (TextView) rootView.findViewById(R.id.textView_routinesClickHere);
			    textView_routinesClickHere.setOnClickListener(new OnClickListener(){

			    	@Override
					public void onClick(View v) {
						if(numactivities > 0){
							mViewPager.setCurrentItem(2, true);
						} else {
							 Toast.makeText(getActivity(), "No activities for this "+dbh.getTime(),Toast.LENGTH_SHORT).show();
						}
					}
				});
			    
			    return rootView;
		 }
	 }
	 

	 	 
	 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
			getMenuInflater().inflate(R.menu.activity_home, menu);
			return true;
		}

		
		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			int itemId = item.getItemId();
			if (itemId == R.id.menu_settings) {
				Intent i = new Intent(this, PrefsActivity.class);
				startActivity(i);
				return true;
			} else if (itemId == R.id.menu_about) {
				startActivity(new Intent(this, AboutActivity.class));
				return true;
			} else if (itemId == R.id.menu_help) {
				startActivity(new Intent(this, HelpActivity.class));
				return true;
			} else if (itemId == R.id.menu_logout) {
				logout();
				return true;
			} else if (itemId == R.id.menu_sync) {
				Intent service = new Intent(this, TrackerService.class);
				Bundle tb = new Bundle();
				tb.putBoolean("backgroundData", true);
				service.putExtras(tb);
				this.startService(service);
				
				return true;
			}
			
			return true;
		}

		private void logout() {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setCancelable(false);
			builder.setTitle(R.string.logout);
			builder.setMessage(R.string.logout_confirm);
			builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
																									
					DbHelper db = new DbHelper(MainScreenActivity.this);
					db.onLogout();
					db.close();
					
					// restart the app
					MainScreenActivity.this.startActivity(new Intent(MainScreenActivity.this, StartUpActivity.class));
					MainScreenActivity.this.finish();
				}
			});
			builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					return; // do nothing
				}
			});
			builder.show();
		}
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
			Log.d(TAG, key + " changed");
			if(key.equalsIgnoreCase(getString(R.string.prefs_server))){
				Editor editor = sharedPreferences.edit();
				if(!sharedPreferences.getString(getString(R.string.prefs_server), "").endsWith("/")){
					String newServer = sharedPreferences.getString(getString(R.string.prefs_server), "").trim()+"/";
					editor.putString(getString(R.string.prefs_server), newServer);
			    	editor.commit();
				}
			}
			if(key.equalsIgnoreCase(getString(R.string.prefs_points)) || key.equalsIgnoreCase(getString(R.string.prefs_badges))){
				supportInvalidateOptionsMenu();
			}
		}
	
	private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
	private long mBackPressed;
	
	@Override
	public void onBackPressed()
	{
	    if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) 
	    { 
	        super.onBackPressed(); 
	        return;
	    }
	    else { Toast.makeText(this, "Press back button again to exit", Toast.LENGTH_SHORT).show(); }

	    mBackPressed = System.currentTimeMillis();
	}
	public boolean isOnline() {
		 boolean haveConnectedWifi = false;
		    boolean haveConnectedMobile = false;

		    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		    NetworkInfo[] netInfo = cm.getAllNetworkInfo();
		    for (NetworkInfo ni : netInfo) {
		        if (ni.getTypeName().equalsIgnoreCase("WIFI"))
		            if (ni.isConnected())
		                haveConnectedWifi = true;
		        if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
		            if (ni.isConnected())
		                haveConnectedMobile = true;
		    }
		    return haveConnectedWifi || haveConnectedMobile;
	}
}
