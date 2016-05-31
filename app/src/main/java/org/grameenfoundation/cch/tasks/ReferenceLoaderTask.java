package org.grameenfoundation.cch.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.digitalcampus.oppia.application.DbHelper;
import org.digitalcampus.oppia.application.MobileLearning;
import org.grameenfoundation.cch.activity.ReferencesDownloadActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * Created by Software Developer on 26-May-16.
 */
public class ReferenceLoaderTask extends AsyncTask<String, String, String> {
    private Context ctx;
    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    private DbHelper dbh;
    private ProgressDialog mProgressDialog;
    private JSONArray contentArray;


    public ReferenceLoaderTask(Context ctx) {
        this.ctx = ctx;
        this.dbh = new DbHelper(ctx);

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
        mProgressDialog.setMessage("References loaded!");

        JSONObject jObj;
        try {
            jObj = new JSONObject();
            contentArray =new JSONArray(unused);
            for(int i=0;i<contentArray.length();i++) {
                jObj = contentArray.getJSONObject(i);
                dbh.insertReference(jObj.getString("reference_desc"),
                        jObj.getString("shortname"),
                        MobileLearning.REFERENCES_ROOT + jObj.getString("shortname"),
                        jObj.getString("size"));
            }
            Intent intent=new Intent(Intent.ACTION_MAIN);
            intent.setClass(ctx,ReferencesDownloadActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            //ctx.finish();
            ctx.startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Toast.makeText(ctx,"References loaded",Toast.LENGTH_LONG).show();
        mProgressDialog.dismiss();
    }



}
