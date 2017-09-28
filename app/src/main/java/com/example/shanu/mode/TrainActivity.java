package com.example.shanu.mode;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.shanu.mode.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;


public class TrainActivity extends AppCompatActivity {

    public SharedPreferences sessionPref;
    public SharedPreferences.Editor prefEditor;
    public final String SESSION_PREF_NAME = "MyCookieStore";
    public final String SESSION_PREF_SESSIONID = "session";
    public String COOKIES_HEADER = "Set-Cookie";
    public String SESSION_ID ;

    public Button trainButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train);

        trainButton = (Button) findViewById(R.id.train_button);

        trainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SendPostRequest().execute();
            }
        });


    }
    public class SendPostRequest extends AsyncTask<String, Void, String> {

        protected void onPreExecute(){

            Toast.makeText(getApplicationContext(), "You'll be notified once the training is done.",
                    Toast.LENGTH_LONG).show();
            trainButton.setClickable(false);
        }

        protected String doInBackground(String... arg0) {

            try {

                URL url = new URL("http://3bbee033.ngrok.io/train"); // here is your URL path

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                sessionPref = getSharedPreferences(SESSION_PREF_NAME, MODE_PRIVATE);
                SESSION_ID = sessionPref.getString(SESSION_PREF_SESSIONID, null);
                Log.e("SessPref's sessionid",SESSION_ID);
                conn.setRequestProperty("Cookie", SESSION_ID);

                conn.connect();
//
//                OutputStream os = conn.getOutputStream();
//                BufferedWriter writer = new BufferedWriter(
//                        new OutputStreamWriter(os, "UTF-8"));
//
//                writer.flush();
//                writer.close();
//                os.close();

                int responseCode = conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    BufferedReader in=new BufferedReader(new
                            InputStreamReader(
                            conn.getInputStream()));

                    StringBuffer sb = new StringBuffer("");
                    String line="";

                    while((line = in.readLine()) != null) {

                        sb.append(line);
                        break;
                    }

                    in.close();
                    return sb.toString();

                }
                else {
                    return new String("false : "+responseCode);
                }
            }
            catch(Exception e){
                return new String("Exception: " + e.getMessage());
            }

        }

        @Override
        protected void onPostExecute(String result) {

            Log.e("response is :",result.toString());
            try {
                JSONObject jResult = new JSONObject(result);
                String flag = jResult.getString("status");
                if (flag.equalsIgnoreCase("Success"))
                {
                    Toast.makeText(getApplicationContext(), "Successfully trained.",
                            Toast.LENGTH_LONG).show();

                }
                else if(flag.equalsIgnoreCase("Failed")){

                    Toast.makeText(getApplicationContext(), "Unexpected Error Occurred, Try again.",
                            Toast.LENGTH_LONG).show();
                    trainButton.setClickable(true);
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

}
