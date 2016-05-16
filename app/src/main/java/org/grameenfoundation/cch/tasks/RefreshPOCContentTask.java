package org.grameenfoundation.cch.tasks;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.digitalcampus.mobile.learningGF.R;
import org.digitalcampus.oppia.application.DbHelper;
import org.digitalcampus.oppia.application.MobileLearning;
import org.grameenfoundation.cch.model.POCSections;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

//usually, subclasses of AsyncTask are declared inside the activity class.
//that way, you can easily modify the UI thread from here
public class RefreshPOCContentTask extends AsyncTask<String, Integer, String> {

 private static Context context;
 private PowerManager.WakeLock mWakeLock;
private File downloadDirectory;
private DbHelper db;
ArrayList<POCSections> sections;

ProgressDialog mProgressDialog;

 public RefreshPOCContentTask(Context context, ArrayList<POCSections> ps) {
     this.context = context;
     db=new DbHelper(context);
     sections=new ArrayList<POCSections>();
     this.sections=ps;


//instantiate it within the onCreate method
mProgressDialog = new ProgressDialog(context);
mProgressDialog.setMessage("Downloading Content, Please wait...");
mProgressDialog.setIndeterminate(true);
mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
mProgressDialog.setCancelable(false);

}
 @Override
 protected void onPreExecute() {
     super.onPreExecute();
     // take CPU lock to prevent CPU from going off if the user 
     // presses the power button during download
     PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
     mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
          getClass().getName());
     mWakeLock.acquire();
     mProgressDialog.show();
 }

 @SuppressWarnings("resource")
@Override
 protected String doInBackground(String... sUrl) {
	 downloadDirectory  = new File(MobileLearning.POC_ROOT);
     if(!downloadDirectory.exists()){
    	 downloadDirectory.mkdirs();
     }
     InputStream input = null;
     OutputStream output = null;
     HttpURLConnection connection = null;
    // sections=db.getPocSections();
     try {
    	 for(int i=0;i<sections.size();i++){
         URL url = new URL(context.getResources().getString(R.string.serverDefaultAddress)+File.separator+MobileLearning.POC_SERVER_CONTENT_DOWNLOAD_PATH+URLEncoder.encode(sections.get(i).getSectionShortname()+".zip"));
        // System.out.println(context.getResources().getString(R.string.serverDefaultAddress)+File.separator+MobileLearning.POC_SERVER_CONTENT_DOWNLOAD_PATH+sections.get(i).getSectionShortname()+".zip");
         connection = (HttpURLConnection) url.openConnection();
         connection.connect();

         // expect HTTP 200 OK, so we don't mistakenly save error report
         // instead of the file
         if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
             return "Server returned HTTP " + connection.getResponseCode()
                     + " " + connection.getResponseMessage();
         }

         // this will be useful to display download percentage
         // might be -1: server did not report the length
         int fileLength = connection.getContentLength();

         // download the file
         input = connection.getInputStream();
         output = new FileOutputStream(downloadDirectory+File.separator+sections.get(i).section_shortname+".zip");

         byte data[] = new byte[4096];
         long total = 0;
         int count;
         while ((count = input.read(data)) != -1) {
             // allow canceling with back button
             if (isCancelled()) {
                 input.close();
                 return null;
             }
             total += count;
             // publishing the progress....
             if (fileLength > 0) // only if total length is known
                 publishProgress((int) (total * 100 / fileLength));
             output.write(data, 0, count);
         }
        
    	 }
     } catch (Exception e) {
         return e.toString();
     } finally {
         try {
             if (output != null)
                 output.close();
             if (input != null)
                 input.close();
             
         } catch (IOException ignored) {
         }

         if (connection != null)
             connection.disconnect();
     }
     return null;
 }
 @Override
 protected void onProgressUpdate(Integer... progress) {
     super.onProgressUpdate(progress);
     // if we get here, length is known, now set indeterminate to false
     mProgressDialog.setIndeterminate(false);
     mProgressDialog.setMax(100);
     mProgressDialog.setProgress(progress[0]);
 }
 @Override
 protected void onPostExecute(String result) {
     mWakeLock.release();
     mProgressDialog.dismiss();
     if (result != null){
    	 System.out.println(result);
         Toast.makeText(context,"Download error: "+result, Toast.LENGTH_LONG).show();
     } else{
         Toast.makeText(context,"File downloaded", Toast.LENGTH_SHORT).show();
       try {
        	 for(int i=0;i<sections.size();i++){
        		 File zipLocation=new File(MobileLearning.POC_ROOT+File.separator+sections.get(i).getSectionShortname()+".zip");
                 File newFile=new File(sections.get(i).getSectionUrl());
                 if(!newFile.exists()){
                	 newFile.mkdirs();
                 }else if(newFile.exists()){
                	 newFile.delete();
                 }
                 unzip(zipLocation, newFile);		
        	 }
        	
		} catch (IOException e) {
			e.printStackTrace();
		}
     }
     
 }
 
 public static void unzip(File zipFile, File targetDirectory) throws IOException {
	    ZipInputStream zis = new ZipInputStream(
	            new BufferedInputStream(new FileInputStream(zipFile)));
	    try {
	    	ProgressDialog newProgress=new ProgressDialog(context);
	    	newProgress.setMessage("Installing content, Please wait...");
	    	newProgress.setIndeterminate(true);
	    	newProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	    	newProgress.setCancelable(false);
	        ZipEntry ze;
	        int count;
	        byte[] buffer = new byte[8192];
	        while ((ze = zis.getNextEntry()) != null) {
	        	System.out.println(zipFile);
	            File file = new File(targetDirectory, ze.getName());
	            File dir = ze.isDirectory() ? file : file.getParentFile();
	            if (!dir.isDirectory() && !dir.mkdirs())
	                throw new FileNotFoundException("Failed to ensure directory: " +
	                        dir.getAbsolutePath());
	            if (ze.isDirectory())
	                continue;
	            FileOutputStream fout = new FileOutputStream(file);
	            try {
	                while ((count = zis.read(buffer)) != -1)
	                    fout.write(buffer, 0, count);
	            } finally {
	                fout.close();
	                Log.d("POC", "Dismissing");
	                newProgress.dismiss();
	            }
	            /* if time should be restored as well
	            long time = ze.getTime();
	            if (time > 0)
	                file.setLastModified(time);
	            */
	        }
	    } finally {
	        zis.close();
	        Log.d("POC", "Finished, deleting file");
	        zipFile.delete();
	    }
	}
}