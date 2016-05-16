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
import android.util.Log;

public class POCRefreshContentTask extends AsyncTask<String, String, String> {
	private Context ctx;
	public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
	private DbHelper dbh;
	 private ProgressDialog mProgressDialog;
	private JSONArray contentArray;
	 
	
	public POCRefreshContentTask(Context ctx) {
		this.ctx = ctx;
		this.dbh = new DbHelper(ctx);
	}
	
	 protected String doInBackground(String ... urls) {
         String response = "";
         for (String url : urls) {
             DefaultHttpClient client = new DefaultHttpClient();
            // System.out.println(url);
             HttpGet httpGet = new HttpGet(url);
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
		@Override
		protected void onPostExecute(String unused) {
			//dbh.deletePOC();
			dbh.alterPOCSection();
			dbh.alterPOCSectionUpdate();
			///sSystem.out.println(unused);
			ArrayList<POCSections> allContent=new ArrayList<POCSections>();
			DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
			allContent=dbh.getPocSections();
			JSONObject jObj;
			try {
				jObj = new JSONObject();
				contentArray =new JSONArray(unused);
				ArrayList<POCSections> refreshContent=new ArrayList<POCSections>();
				POCSections ps = null;
				if(contentArray.length()>allContent.size()){
					for(int i=0;i<contentArray.length();i++){
						jObj=contentArray.getJSONObject(i);
						jObj=contentArray.getJSONObject(i);
					dbh.insertPocSection(jObj.getString("name_of_section"), 
							jObj.getString("shortname"), 
							MobileLearning.POC_ROOT+jObj.getString("shortname"),
							jObj.getString("sub_section"),
							jObj.getString("section_url"),
							jObj.getString("updated_at"));
					}
					DownloadPOCContentTask task=new DownloadPOCContentTask(ctx);
					task.execute();
				}else if(contentArray.length()==allContent.size()){
					for(int i=0;i<contentArray.length();i++){
						jObj=contentArray.getJSONObject(i);
						System.out.println(jObj.toString());
						DateTime refreshDate = formatter.parseDateTime(jObj.getString("updated_at"));
						DateTime oldDate = formatter.parseDateTime(allContent.get(i).getUpdatedAt());
							if(oldDate.isBefore(refreshDate)){
								jObj=contentArray.getJSONObject(i);
								System.out.println(jObj.toString());
								ps=new POCSections();
								ps.setSectionName(jObj.getString("name_of_section"));
								ps.setSectionShortname(jObj.getString("shortname"));
								ps.setSectionUrl(MobileLearning.POC_ROOT+jObj.getString("shortname"));
								ps.setSubSection(jObj.getString("sub_section"));
								ps.setUpdated(jObj.getString("updated_at"));
								refreshContent.add(ps);
								RefreshPOCContentTask task=new RefreshPOCContentTask(ctx,refreshContent);
								task.execute();
							}
					}
				}
			
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		}
	

		
}
