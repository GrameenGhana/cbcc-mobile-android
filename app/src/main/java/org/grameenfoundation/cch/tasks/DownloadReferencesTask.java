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
import org.grameenfoundation.cch.activity.ReferencesDownloadActivity;
import org.grameenfoundation.cch.model.POCSections;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

//usually, subclasses of AsyncTask are declared inside the activity class.
//that way, you can easily modify the UI thread from here
public class DownloadReferencesTask extends AsyncTask<String, Integer, String> {

    private static Context context;
    private PowerManager.WakeLock mWakeLock;
    private File downloadDirectory;
    private DbHelper db;
    private String file;


    ProgressDialog mProgressDialog;
    private int i;

    public DownloadReferencesTask(Context context, String file) {
        this.context = context;
        db=new DbHelper(context);
        this.file=file;



//instantiate it within the onCreate method
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage("Downloading Content, Please wait...");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(true);

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
        downloadDirectory  = new File(MobileLearning.REFERENCES_ROOT);
        i=0;
        if(!downloadDirectory.exists()){
            downloadDirectory.mkdirs();
        }
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;

        try {
                URL url = new URL(context.getResources().getString(R.string.serverDefaultAddress)+File.separator+MobileLearning.POC_SERVER_REFERENCE_DOWNLOAD_PATH+URLEncoder.encode(file));
                 System.out.println(context.getResources().getString(R.string.serverDefaultAddress)+File.separator+MobileLearning.POC_SERVER_REFERENCE_DOWNLOAD_PATH+file);
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
                output = new FileOutputStream(downloadDirectory+File.separator+file);
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressDialog.setMessage("Downloading: "+file);
                    }
                });
                //
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
        Intent intent=new Intent(Intent.ACTION_MAIN);
        intent.setClass(context,ReferencesDownloadActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        context.startActivity(intent);
        if (result != null){
            System.out.println(result);
            Toast.makeText(context,"Download error: "+result, Toast.LENGTH_LONG).show();
        } else{
            Toast.makeText(context,"File downloaded", Toast.LENGTH_SHORT).show();

        }

    }


}