package com.example.shanu.mode;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;

import android.view.View;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;

import android.widget.ImageView;



import android.speech.tts.TextToSpeech;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.example.shanu.mode.SelectImageActivity;

import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;

import static android.R.attr.fragment;
import static java.lang.Boolean.FALSE;

public class TestActivity extends AppCompatActivity {

    private static final int REQUEST_SELECT_IMAGE = 0;
    //The button to select an image
    private Button mButtonSelectImage;

    //The URI of the image selected to detect.
    private Uri mImageUri;
    // The image selected to detect.
    private Bitmap mBitmap;

    private ProgressBar mProgressBar;
    private String encodedImage;
    private Button mTestButton;
    private CookieManager cmrCookieMan;

    public SharedPreferences sessionPref;
    public SharedPreferences.Editor prefEditor;
    public final String SESSION_PREF_NAME = "MyCookieStore";
    public final String SESSION_PREF_SESSIONID = "session";
    public String COOKIES_HEADER = "Set-Cookie";
    public String SESSION_ID ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);


        mTestButton = (Button) findViewById(R.id.testButton);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

//        cmrCookieMan = new CookieManager(new MyCookieStore(this), CookiePolicy.ACCEPT_ALL);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] {Manifest.permission.CAMERA}, 1);
            }
        }

        mProgressBar.setVisibility(View.GONE);
        mTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;

                intent = new Intent(TestActivity.this, SelectImageActivity.class);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivityForResult(intent, REQUEST_SELECT_IMAGE);
            }
        });


    }

    // Called when image selection is done.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("DescribeActivity", "onActivityResult");
        switch (requestCode) {
            case REQUEST_SELECT_IMAGE:
                if (resultCode == Activity.RESULT_OK) {
                    // If image is selected successfully, set the image URI and bitmap.
                    mImageUri = data.getData();

                    mBitmap = ImageHelper.loadSizeLimitedBitmapFromUri(
                            mImageUri, getContentResolver());
                    if (mBitmap != null) {
                        // Show the image on screen.
                        ImageView imageView = (ImageView) findViewById(R.id.selectedImage);
                        imageView.setImageBitmap(mBitmap);

                        // Add detection log.
                        Log.d("DescribeActivity", "Image: " + mImageUri + " resized to " + mBitmap.getWidth()
                                + "x" + mBitmap.getHeight());

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] imageBytes = baos.toByteArray();
                        encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

                        Log.d(" Encoding done", encodedImage);
                        new SendPostRequest().execute();

                    }
                    else
                    {
                        Log.d(" FAILED", "Image");
                    }
                }
                break;
            default:
                break;
        }
    }
    public class SendPostRequest extends AsyncTask<String, Void, String> {

        protected void onPreExecute(){
            mProgressBar.setVisibility(View.VISIBLE);
            Toast.makeText(getApplicationContext(), "Please wait !",
                    Toast.LENGTH_LONG).show();
            mTestButton.setClickable(false);
        }

        protected String doInBackground(String... arg0) {

            try {

                URL url = new URL(" http://3bbee033.ngrok.io/test-image"); // here is your URL path

                JSONObject postDataParams = new JSONObject();
                postDataParams.put("inputTestImageBase64EncodedString", encodedImage);
                Log.e("params",postDataParams.toString());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                sessionPref = getSharedPreferences(SESSION_PREF_NAME, MODE_PRIVATE);
                SESSION_ID = sessionPref.getString(SESSION_PREF_SESSIONID, null);
                Log.e("SessPref's sessionid",SESSION_ID);
                Log.e("SessPref's sessionid2",HttpCookie.parse(SESSION_ID).toString());
                conn.setRequestProperty("Cookie",SESSION_ID);

                //Log.e("After setting -",conn.getHeaderField("session"));
//              Log.e("before http conn ",conn.getHeaderField("Set-Cookie").toString());

//              private SharedPreferences spe;
//              SharedPreferences prefs = getSharedPreferences("MyCookieStore",MODE_PRIVATE);
//              String prefStoredCookie = prefs.getString("session_cookie",null);

//              SharedPreferences.Editor editor = getSharedPreferences("MyCookieStore2",MODE_PRIVATE).edit();*/

//              conn.setRequestProperty("Set-Cookie",);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();

                int responseCode=conn.getResponseCode();

//                java.net.CookieManager msCookieManager = new java.net.CookieManager();
//
//                Log.e("Response returned - ", String.valueOf(responseCode));
//                Map<String, List<String>> headerFields = conn.getHeaderFields();
//                List<String> cookiesHeader = headerFields.get("Set-Cookie");
////                if(cookiesHeader!=null ) {
//                    Log.e("Cookies returned - ", headerFields.toString());
////                }
//
//                if (cookiesHeader != null) {
//                    for (String cookie : cookiesHeader) {
//                        msCookieManager.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
//                    }
//                }


//                CookieHandler.setDefault(cmrCookieMan);
//                Log.e("Cookies in custom - ",cmrCookieMan.getCookieStore().getCookies().toString());



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

            mProgressBar.setVisibility(View.GONE);
            Log.e("response is :",result.toString());
            Intent data = new Intent();
            try {
                JSONObject jResult = new JSONObject(result);
                String flag = jResult.getString("status");
                if (flag.equalsIgnoreCase("Success"))
                {
                    data.setData(Uri.parse(result));
                    setResult(RESULT_OK, data);
                    finish();

                }
                else if(flag.equalsIgnoreCase("Failed")){

                    Toast.makeText(getApplicationContext(), "Unexpected Error Occurred, Try again.",
                            Toast.LENGTH_LONG).show();
                    mTestButton.setClickable(true);
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()){

            String key= itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }
}
