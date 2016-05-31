package org.grameenfoundation.cch.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.Reference;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.apache.http.client.ClientProtocolException;
import org.digitalcampus.mobile.learningGF.R;
import org.digitalcampus.oppia.application.DbHelper;
import org.digitalcampus.oppia.application.MobileLearning;
import org.grameenfoundation.cch.model.References;
import org.grameenfoundation.cch.tasks.DownloadReferencesTask;
import org.grameenfoundation.cch.tasks.ReferenceLoaderTask;
import org.grameenfoundation.poc.BaseActivity;
import org.json.JSONException;
import org.json.JSONObject;

import com.bugsense.trace.BugSenseHandler;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class ReferencesDownloadActivity extends BaseActivity{

	private ListView referenceList;
	private ArrayList<String>  fileLongName;
	private String[] files;
	private boolean isComplete;
	private File myDirectory;
	private SharedPreferences prefs;
	private Long start_time;
	private Long end_time;
	private DbHelper dbh;
	private JSONObject data;
	private TextView txt1;
	/** Called when the activity is first created. */
	private ListAdapter myAdapter;
	private ArrayList<String> fileSizeList;
	private TextView textview_instruct;
	private ArrayList<References> references;
	private Button button_load;

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_references_download);
        getSupportActionBar().setTitle("Learning Center");
        getSupportActionBar().setSubtitle("Download References");
		myDirectory  = new File(Environment.getExternalStorageDirectory(), "references");
	    referenceList=(ListView) findViewById(R.id.tag_list);
	    dbh=new DbHelper(ReferencesDownloadActivity.this);
		files=myDirectory.list();
	    start_time=System.currentTimeMillis();
	    textview_instruct=(TextView) findViewById(R.id.textView_instruct);
	    textview_instruct.setText("Select download button to download References");
		button_load=(Button) findViewById(R.id.button_load);
		prefs = PreferenceManager.getDefaultSharedPreferences(ReferencesDownloadActivity.this);

	    if(!myDirectory.exists()){
	    	 myDirectory.mkdirs();
	    }else{
	    	 File[] checkFiles = myDirectory.listFiles();
	    	 for(File f:checkFiles){
	    		 if(f.getName().equals("FPFlipchart.pdf")){
	    			 f.delete();
	    		 }else if(f.getName().equals("National Safe Motherhood Service Protocol.pdf")){
	    			 f.delete();
	    		 }
    		 }
	    }
	  	references=new ArrayList<References>();
		references= dbh.getReferences();
		myAdapter=new ListAdapter(ReferencesDownloadActivity.this,references,myDirectory);
    	referenceList.setAdapter(myAdapter);

    	 this.registerForContextMenu(referenceList);
    	 referenceList.setOnItemClickListener(new OnItemClickListener(){

		@Override
		public void onItemClick(AdapterView<?> parent, View view,
				int position, long id) {
			String path =myDirectory.getAbsolutePath()+"/"+references.get(position).getReference_shortname()+".pdf";
			openPdfIntent(path);
			
		}		
	});
		button_load.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				ReferenceLoaderTask task=new ReferenceLoaderTask(ReferencesDownloadActivity.this);
				task.execute(getResources().getString(R.string.serverDefaultAddress)+ File.separator+MobileLearning.POC_SERVER_DOWNLOAD_PATH+"allreferences");
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						references= dbh.getReferences();
						myAdapter=new ListAdapter(ReferencesDownloadActivity.this,references,myDirectory);
						referenceList.setAdapter(myAdapter);
					}
				});
			}
		});
	}
	
	private void openPdfIntent(String path) 
	{
	    	 File file=new File(path);
	    	 if(file.exists()){
	    	 Uri uri  = Uri.fromFile(file);
	    	 Intent intentUrl = new Intent(Intent.ACTION_VIEW);
	    	 intentUrl.setDataAndType(uri, "application/pdf");
	    	 intentUrl.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

	 	    try {
	    	 startActivity(intentUrl);
	 	    }
	    catch (ActivityNotFoundException e) 
	    {
	      e.printStackTrace();
	      Crouton.makeText(ReferencesDownloadActivity.this, "No application available to view PDF", Style.ALERT).show();
	    }
	    	 }else{
    		 Crouton.makeText(ReferencesDownloadActivity.this, "Download the file!", Style.ALERT).show();
	    	 }
	}
	

	class ListAdapter extends BaseAdapter{
		Context mContext;
		ArrayList<References> listItems;
		File directory;
		 public LayoutInflater minflater;
		
		
		public ListAdapter(Context mContext, ArrayList<References> listItems, File directory){
		this.mContext=mContext;
		this.listItems=listItems;
		this.directory=directory;
		 minflater = LayoutInflater.from(mContext);
		}
		@Override
		public int getCount() {
			return listItems.size();
		}

		@Override
		public Object getItem(int position) {
			return listItems.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    View rowView = inflater.inflate(R.layout.references_download_listview_single, parent, false);
			
			 TextView text=(TextView) rowView.findViewById(R.id.textView_referenceName);
			 text.setText(listItems.get(position).getReference_name());
			 final LinearLayout values=(LinearLayout) rowView.findViewById(R.id.Linearlayout_values);
			 final ImageButton image=(ImageButton) rowView.findViewById(R.id.imageButton_download);
			 //final TextView txt1 = new TextView(mContext);
			 txt1 =(TextView) rowView.findViewById(R.id.textView1);
			 txt1.setText(listItems.get(position).getReference_size());

			 File file = new File(directory, listItems.get(position).getReference_shortname()+".pdf");
			 if(file.exists()){

					image.setVisibility(View.GONE);
			 }else{		
				 image.setImageResource(R.drawable.ic_download);
				 image.setEnabled(true);
				 image.setOnClickListener(new OnClickListener(){
					 
						@Override
						public void onClick(View v) {
							if(dbh.isOnline(ReferencesDownloadActivity.this)){
								try {
									DownloadReferencesTask task=new DownloadReferencesTask(ReferencesDownloadActivity.this,listItems.get(position).getReference_shortname()+".pdf");
									task.execute();

								} catch (Exception e) {
									e.printStackTrace();
								}
							}else if(!dbh.isOnline(ReferencesDownloadActivity.this)){
								AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
										ReferencesDownloadActivity.this);
									alertDialogBuilder.setTitle("Connectivity Error");
										alertDialogBuilder
										.setMessage(getResources().getString(R.string.error_connection_required))
											.setCancelable(false)
											.setIcon(R.drawable.ic_error)
											.setPositiveButton("OK",new DialogInterface.OnClickListener() {
												public void onClick(DialogInterface dialog,int id) {
													dialog.cancel();
												}
											  });
											
									AlertDialog alertDialog = alertDialogBuilder.create();
									alertDialog.show();
							}
					
								
						}
						 
					 });
				
			 }
			    return rowView;
		}
		
	}
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
	                                ContextMenuInfo menuInfo) {
	    super.onCreateContextMenu(menu, v, menuInfo);
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.context_menu, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
	    final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	    switch (item.getItemId()) {
	        case R.id.option1:
	        	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						ReferencesDownloadActivity.this);
					alertDialogBuilder.setTitle("Delete Confirmation");
					alertDialogBuilder
						.setMessage("You are about to delete this file. Proceed?")
						.setCancelable(false)
						.setIcon(R.drawable.ic_error)
						.setPositiveButton("No",new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int id) {
								dialog.cancel();
							}
						  })
						.setNegativeButton("Yes",new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int id) {
								File filetodelete=new File(myDirectory,(String.valueOf(references.get(((int)info.position)))));
					        	filetodelete.delete();
					        	Intent intent=new Intent(Intent.ACTION_MAIN);
								intent.setClass(ReferencesDownloadActivity.this,ReferencesDownloadActivity.class);
								intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
								finish();
								startActivity(intent);
							
			}
    	});
					AlertDialog alertDialog = alertDialogBuilder.create();
					alertDialog.show();
	        	
	            return true;
	        default:
	            return super.onContextItemSelected(item);
	    }
	}
	
	public void onBackPressed()
	{
		 end_time=System.currentTimeMillis();
		 data=new JSONObject();
		    try {
		    	data.put("page", "Reference Download");
		    	data.put("ver", dbh.getVersionNumber(ReferencesDownloadActivity.this));
		    	data.put("battery", dbh.getBatteryStatus(ReferencesDownloadActivity.this));
		    	data.put("device", dbh.getDeviceName());
				data.put("imei", dbh.getDeviceImei(ReferencesDownloadActivity.this));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		dbh.insertCCHLog("Learning Center", data.toString(), start_time.toString(), end_time.toString());
		finish();
	}


}
