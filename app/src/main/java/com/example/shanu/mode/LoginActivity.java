package com.example.shanu.mode;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import java.io.BufferedReader;
import java.io.BufferedWriter;

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

import org.json.JSONObject;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;


import com.example.shanu.mode.data.MyCookieStore2;

import javax.net.ssl.HttpsURLConnection;


public class LoginActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private EditText phoneText;
    private EditText passwordText;

    private String phone;
    private String password;
//    private CookieManager cmrCookieMan;

    public SharedPreferences sessionPref;
    public SharedPreferences.Editor prefEditor;
    public final String SESSION_PREF_NAME = "MyCookieStore";
    public final String SESSION_PREF_SESSIONID = "session";
    public String COOKIES_HEADER = "Set-Cookie";

    SessionManager session ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        session = new SessionManager(getApplicationContext());
        if( session.isLoggedIn()) {
            Intent intent = new Intent(LoginActivity.this, NavigationActivity.class);

            /*
            cmrCookieMan = new CookieManager(new MyCookieStore(this), CookiePolicy.ACCEPT_ALL);
            CookieHandler.setDefault(cmrCookieMan);
            cmrCookieMan = new CookieManager(new MyCookieStore2(this), CookiePolicy.ACCEPT_ORIGINAL_SERVER);
            CookieHandler.setDefault(cmrCookieMan);
            */
            CookieManager cookieManager = new CookieManager();
            CookieHandler.setDefault(cookieManager);
            startActivity(intent);
            finish();
        }
        /*
        cmrCookieMan = new CookieManager(new MyCookieStore(this), CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cmrCookieMan);
        cmrCookieMan.getCookieStore().removeAll();
        cmrCookieMan = new CookieManager(new MyCookieStore2(this), CookiePolicy.ACCEPT_ORIGINAL_SERVER);
        CookieHandler.setDefault(cmrCookieMan);
        cmrCookieMan.getCookieStore().removeAll();
        */
        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);

        session = new SessionManager(getApplicationContext());

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Button loginButton = (Button) findViewById(R.id.btn_login);
        TextView txtview = (TextView) findViewById(R.id.link_signup);
        txtview.setText("No Account yet ? Create One.");


        loginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                phoneText = (EditText)findViewById(R.id.input_phone);
                passwordText = (EditText)findViewById(R.id.input_password);

                phone = phoneText.getText().toString();
                password = passwordText.getText().toString();
//                Log.e("Custom Cookies Before-",cmrCookieMan.getCookieStore().getCookies().toString());
                new SendPostRequest().execute();


            }

        });

    }

    public class SendPostRequest extends AsyncTask<String, Void, String> {

        protected void onPreExecute(){}

        protected String doInBackground(String... arg0) {

            try {

                URL url = new URL("http://b112da2f.ngrok.io/signin"); // here is your URL path

                JSONObject postDataParams = new JSONObject();
                postDataParams.put("inputPhone", phone);
                postDataParams.put("inputPassword", password);
                Log.e("params",postDataParams.toString());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();

                int responseCode=conn.getResponseCode();

                sessionPref = getSharedPreferences(SESSION_PREF_NAME, MODE_PRIVATE);
                prefEditor = sessionPref.edit();

                Map<String, List<String>> headerFields = conn.getHeaderFields();
                List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);
                if (cookiesHeader != null) {
                    for (String cookie : cookiesHeader) {
                        String normieSessionId = cookie;
                        Log.e("session id 1:",normieSessionId);
//                        String sessionid = HttpCookie.parse(cookie).toString();
//                        Log.e("session id 2 string:",sessionid);
                        prefEditor.putString(SESSION_PREF_SESSIONID, normieSessionId);
                        prefEditor.commit();
//                        Log.e("attempt2fix:",HttpCookie.parse(cookie).toString());
                    }
                }

//                if (cookiesHeader != null) {
//                    for (String cookie : cookiesHeader) {
//                        msCookieManager.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
//                    }
//                }
//                Log.e("Cookies - ",msCookieManager.getCookieStore().getCookies().toString());
//                Log.e("Custom Cookies After - ",cmrCookieMan.getCookieStore().getCookies().toString());
//                Toast.makeText(getApplicationContext(), msCookieManager.getCookieStore().getCookies().toString(),
//                        Toast.LENGTH_LONG).show();

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
            Log.e("response is :",result);
            if(result.contains("success")) {
                Toast.makeText(getApplicationContext(), "Login Successful",
                        Toast.LENGTH_LONG).show();
                session.createLoginSession(phone);
                Intent intent = new Intent(LoginActivity.this, NavigationActivity.class);
                startActivity(intent);
                finish();
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Some Error Occurred, Try Again",
                        Toast.LENGTH_LONG).show();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void OpenSignUpFunction(View view) {
        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
        startActivity(intent);
        finish();
    }

}
