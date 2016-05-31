package org.grameenfoundation.cch.tasks;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.digitalcampus.oppia.application.DbHelper;
import org.digitalcampus.oppia.application.MobileLearning;
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

public class POCContentLoaderTask extends AsyncTask<String, String, String> {
	private Context ctx;
	private String subsection;
	public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
	private DbHelper dbh;
	 private ProgressDialog mProgressDialog;
	private JSONArray contentArray;
	 
	
	public POCContentLoaderTask(Context ctx, String subsection) {
		this.ctx = ctx;
		this.dbh = new DbHelper(ctx);
		this.subsection=subsection;
	}
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		mProgressDialog = new ProgressDialog(ctx);
		mProgressDialog.setMessage("Downloading content..");
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.setCancelable(false);
		mProgressDialog.show();
	}
	 protected String doInBackground(String ... urls) {
         String response = "";
         for (String url : urls) {
             DefaultHttpClient client = new DefaultHttpClient();
             System.out.println(url);
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
		protected void onProgressUpdate(String... progress) {
			 Log.d("ANDRO_ASYNC",progress[0]);
			 mProgressDialog.setProgress(Integer.parseInt(progress[0]));
		}

		@Override
		protected void onPostExecute(String unused) {
			System.out.println(unused);
			mProgressDialog.setMessage("Content successfully downloaded!");
			dbh.deletePOC(subsection);
			dbh.alterPOCSection();
			dbh.alterPOCSectionUpdate();
			JSONObject jObj;
			try {
				jObj = new JSONObject();
				contentArray =new JSONArray(unused);
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
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			mProgressDialog.dismiss();
		}
	

		
}
