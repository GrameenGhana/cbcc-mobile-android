package org.grameenfoundation.poc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.digitalcampus.mobile.learningGF.R;
import org.digitalcampus.oppia.application.DbHelper;
import org.digitalcampus.oppia.application.MobileLearning;
import org.grameenfoundation.cch.caldroid.CaldroidFragment;
import org.grameenfoundation.cch.caldroid.CaldroidListener;
import org.grameenfoundation.cch.model.WebAppInterface;
import org.grameenfoundation.cch.utils.CalendarViewScrollable;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONException;
import org.json.JSONObject;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.Toast;
import android.widget.CalendarView.OnDateChangeListener;
import android.widget.TextView;

public class EstimateTrimester extends BaseActivity {

	private static CalendarView calendarView_calendar;
	private Button button_calculate;
	private Button button_proceed;
	private TextView textView_estimatedWeeks;
	private TextView textView_estimatedTrimester;
	private static String newDate;
	private static Calendar cal;
	private WebView myWebView;
	private ViewPager mPager;
	private TextView textView_selectedDate;
	private DbHelper dbh;
	private Long start_time;
	private Long end_time;
	private DatePicker calendar;
	private String estimated_due_date;
	private TextView textView_estimatedDueDate;
	private JSONObject json;
	private Context mContext;
	private SimpleDateFormat formatter2;

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_estimate_trimester);

	    mContext=EstimateTrimester.this;
        getSupportActionBar().setTitle("Point of Care");
        getSupportActionBar().setSubtitle("Trimester & Due Date Calculator");
	    start_time=System.currentTimeMillis();
	    dbh=new DbHelper(EstimateTrimester.this);
		calendar= (DatePicker) findViewById(R.id.datePicker);
	    json=new JSONObject();
	    try {
			json.put("page", "Trimester & Due Date Calculator");
			json.put("section", MobileLearning.CCH_COUNSELLING);
			json.put("ver", dbh.getVersionNumber(mContext));
			json.put("battery", dbh.getBatteryStatus(mContext));
			json.put("device", dbh.getDeviceName());
			json.put("imei", dbh.getDeviceImei(mContext));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	    textView_selectedDate=(TextView) findViewById(R.id.textView_selectedDate);
	    textView_estimatedDueDate=(TextView) findViewById(R.id.textView_estimatedDueDate);
		formatter2  = new SimpleDateFormat("dd-MMM-yyyy");
		newDate=formatter2.format(getDateFromDatePicker(calendar));
	    
	    /*final CaldroidFragment caldroidFragment = new CaldroidFragment();
	    Bundle args = new Bundle();
	    Calendar cal = Calendar.getInstance();
	    args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
	    args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
	    caldroidFragment.setArguments(args);

	    FragmentTransaction t = getSupportFragmentManager().beginTransaction();
	    t.replace(R.id.fragment1, caldroidFragment);
	    t.commit();
	    
	    final CaldroidListener listener = new CaldroidListener() {
	    	SimpleDateFormat formatter  = new SimpleDateFormat("dd/MM/yyyy");
	    	SimpleDateFormat formatter2  = new SimpleDateFormat("dd-MMM-yyyy");
	        @Override
	        public void onSelectDate(Date date, View view) {
	        	newDate=formatter.format(date);
	        	caldroidFragment.refreshView();
	        	Date today=new Date();
	        	if(isDateAfter(formatter.format(date),formatter.format(today))==true){
	        		Crouton.makeText(EstimateTrimester.this, "Select a date in the past", Style.ALERT).show();	
	        		button_calculate.setVisibility(View.GONE);
	        		button_proceed.setVisibility(View.GONE);
	        	}else{
	        		button_calculate.setVisibility(View.VISIBLE);
	        		textView_selectedDate.setText("You selected: "+formatter2.format(date));
	        		button_proceed.setVisibility(View.VISIBLE);
	        	}
	        	//caldroidFragment.setBackgroundResourceForDate(R.color.WhileWaitingForTransport, date);
	        	//caldroidFragment.setTextColorForDate(R.color.White, date);
	        	//caldroidFragment.refreshView();
	        }
	    };

	    caldroidFragment.setCaldroidListener(listener);*/
	
	    button_calculate=(Button) findViewById(R.id.button_calculate);
	    button_calculate.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(newDate!=null){
					DateTime todaysdate = new DateTime();
					DateTime selectedDate=new DateTime(calendar.getYear(), calendar.getMonth()+1, calendar.getDayOfMonth(), 0, 0);
					if(selectedDate.isAfter(todaysdate)){
						Crouton.makeText(EstimateTrimester.this, "Select a date in the past", Style.ALERT).show();
					}else{
						daysBetween();
						calculateDueDate();
					}

				}else{
					Crouton.makeText(EstimateTrimester.this, "Please select a date", Style.ALERT).show();	
				}
			}
	    	
	    });
	    
	    button_proceed=(Button) findViewById(R.id.button_proceed);
	    button_proceed.setVisibility(View.GONE);
	    textView_estimatedWeeks=(TextView) findViewById(R.id.textView_estimatedWeeks);
	    textView_estimatedTrimester=(TextView) findViewById(R.id.textView_estimatedTrimester);
	    
	   // button_calculate.setVisibility(View.GONE);
	//	button_proceed.setVisibility(View.GONE);
	}
	
	public void daysBetween(){
		try {
			SimpleDateFormat dfDate = new SimpleDateFormat("dd/MM/yyyy");
			DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy");
			newDate = formatter2.format(getDateFromDatePicker(calendar));
			DateTime todaysdate = new DateTime();
			System.out.println(todaysdate.toString(formatter));
			DateTime selectedDate = new DateTime(calendar.getYear(), calendar.getMonth()+1, calendar.getDayOfMonth(), 0, 0);

			long diff = Days.daysBetween(selectedDate, todaysdate).getDays();
			int days = (int) (diff / (1000 * 60 * 60 * 24));
			System.out.println(String.valueOf(diff));
			if (diff < 90) {
				textView_estimatedTrimester.setText("1st Trimester");
			} else if (diff > 89 && diff < 180) {
				textView_estimatedTrimester.setText("2nd Trimester");
			} else if (diff > 180) {
				textView_estimatedTrimester.setText("3rd Trimester");
			}
		/*
		if(days<1){
			textView_estimatedTrimester.setTextColor(Color.RED);
			textView_estimatedTrimester.setText("Please select a date in the past to proceed");
		}*/
			if (diff > 300) {
				textView_estimatedTrimester.setTextColor(Color.RED);
				textView_estimatedTrimester.setText("Your client should have delivered by now");
			}

			double weeks = Math.ceil(diff / 7);
			if (weeks > 1) {
				String week_value = String.format("%.0f", weeks);
				textView_estimatedWeeks.setText(week_value + " weeks");
			} else {
				if (weeks == 1) {
					String week_value = String.format("%.0f", weeks);
					textView_estimatedWeeks.setText(week_value + " week");
				} else {
					if (weeks < 1) {
						textView_estimatedWeeks.setText("Less than a week");
					}
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public void calculateDueDate(){

		DateTimeFormatter formatter = DateTimeFormat.forPattern("dd-MM-yyyy");
		DateTime selectedDate=new DateTime(calendar.getYear(), calendar.getMonth()+1, calendar.getDayOfMonth(), 0, 0);

		 selectedDate.plusDays(280);
    	// cal.add(Calendar.DATE,280);
    	 estimated_due_date=selectedDate.toString(formatter);
    	 textView_estimatedDueDate.setText(estimated_due_date);
	}
	public static java.util.Date getDateFromDatePicker(DatePicker datePicker){
		int day = datePicker.getDayOfMonth();
		int month = datePicker.getMonth();
		int year =  datePicker.getYear();

		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month, day);

		return calendar.getTime();
	}
	public static boolean isDateAfter(String startDate,String endDate)
    {
	 Date start_date = null;
	 Date end_date = null;
	long starDateAsTimestamp = 0;
	long endDateTimestamp = 0;
	try {
		if(startDate==null){
		System.out.println("Enter a valid date!");
		}else{
		start_date = new SimpleDateFormat("dd/MM/yyyy").parse(startDate);
		 starDateAsTimestamp = start_date.getTime();
		}
		if(endDate==null){
			System.out.println("Enter a valid date!");
		}else{
		end_date = new SimpleDateFormat("dd/MM/yyyy").parse(endDate);
		endDateTimestamp = end_date.getTime();
		}
	} catch (ParseException e) {
		e.printStackTrace();
	}
	
	
	 long getRidOfTime = 1000 * 60 * 60 * 24;
	 long startDateAsTimestampWithoutTime = starDateAsTimestamp / getRidOfTime;
	 long endDateTimestampWithoutTime = endDateTimestamp / getRidOfTime;

	 if (startDateAsTimestampWithoutTime > endDateTimestampWithoutTime) {
	    return true;
	 } else {
	    return false;
	 }
    }
	public void onBackPressed()
	{
	    end_time=System.currentTimeMillis();
		//dbh.insertCCHLog("Point of Care", "Trimester Calculator", start_time.toString(), end_time.toString());
	    dbh.insertCCHLog("Point of Care", json.toString(), start_time.toString(), end_time.toString());
		finish();
	}
	
	
	}

