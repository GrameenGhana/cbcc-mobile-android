package org.grameenfoundation.cch.tasks;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.digitalcampus.mobile.learningGF.R;
import org.digitalcampus.oppia.application.DbHelper;
import org.digitalcampus.oppia.application.MobileLearning;
import org.grameenfoundation.cch.model.POCSections;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class POCRefreshContentTask extends AsyncTask<String, String, String> {
	private Context ctx;
	public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
	private DbHelper dbh;
	 private ProgressDialog mProgressDialog;
	private String subsection;
	private JSONArray contentArray;
	@Override

	protected void onPreExecute() {
		super.onPreExecute();
		mProgressDialog = new ProgressDialog(ctx);
		mProgressDialog.setMessage("Downloading content..");
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.setCancelable(false);
		mProgressDialog.show();
	}
	
	public POCRefreshContentTask(Context ctx,String subsection) {
		this.ctx = ctx;
		this.dbh = new DbHelper(ctx);
		this.subsection=subsection;
	}
	
	 protected String doInBackground(String ... urls) {

         String response = "";
         for (String url : urls) {

             DefaultHttpClient client = new DefaultHttpClient();
            // System.out.println(url);
             HttpGet httpGet = new HttpGet(url);
			 System.out.println(url);
             try {
                 HttpResponse execute = client.execute(httpGet);
                 InputStream content = execute.getEntity().getContent();

                 BufferedReader buffer = new BufferedReader(
                         new InputStreamReader(content));
                 String s = "";
                 while ((s = buffer.readLine()) != null) {
                     response += s;
                 }

             } catch (Exception e) {
                 e.printStackTrace();
             }
         }
         return response;
     }
	protected void onProgressUpdate(String... progress) {
		Log.d("ANDRO_ASYNC",progress[0]);
		mProgressDialog.setProgress(Integer.parseInt(progress[0]));
	}
		@Override
		protected void onPostExecute(String unused) {
			mProgressDialog.setMessage("Content successfully downloaded!");
			dbh.alterPOCSection();
			dbh.alterPOCSectionUpdate();
			ArrayList<POCSections> allContent=new ArrayList<POCSections>();
			DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
			allContent=dbh.getPocSubSection(subsection);
			JSONObject jObj;
			try {
				jObj = new JSONObject();
				contentArray =new JSONArray(unused);
				ArrayList<POCSections> refreshContent=new ArrayList<POCSections>();
				POCSections ps = null;
				if(contentArray.length()>allContent.size()){
					for(int i=0;i<contentArray.length();i++){
						jObj=contentArray.getJSONObject(i);
					dbh.insertPocSection(jObj.getString("name_of_section"), 
							jObj.getString("shortname"), 
							MobileLearning.POC_ROOT+jObj.getString("shortname"),
							jObj.getString("sub_section"),
							jObj.getString("section_url"),
							jObj.getString("updated_at"));
					}
					DownloadPOCContentTask task=new DownloadPOCContentTask(ctx,subsection);
					task.execute();
				}else if(contentArray.length()==allContent.size()){

					for(int i=0;i<contentArray.length();i++){
						jObj=contentArray.getJSONObject(i);
						System.out.println(jObj.toString());
						DateTime refreshDate = formatter.parseDateTime(jObj.getString("updated_at"));
						DateTime oldDate = formatter.parseDateTime(allContent.get(i).getUpdatedAt());
							if(oldDate.isBefore(refreshDate)){
								System.out.println(refreshDate.toString());
								jObj=contentArray.getJSONObject(i);
								System.out.println(jObj.toString());
								ps=new POCSections();

								ps.setSectionName(jObj.getString("name_of_section"));
								ps.setSectionShortname(jObj.getString("shortname"));
								ps.setSectionUrl(MobileLearning.POC_ROOT+jObj.getString("shortname"));
								ps.setSubSection(jObj.getString("sub_section"));
								ps.setUpdated(jObj.getString("updated_at"));
								refreshContent.add(ps);

							}
					}
					RefreshPOCContentTask task=new RefreshPOCContentTask(ctx,refreshContent);
					task.execute();
				}
			
			} catch (JSONException e) {
				e.printStackTrace();
			}
			mProgressDialog.dismiss();
		}
	

		
}
