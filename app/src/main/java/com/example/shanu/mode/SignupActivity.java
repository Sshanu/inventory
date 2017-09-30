package com.example.shanu.mode;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class SignupActivity extends AppCompatActivity {


    private Toolbar mToolbar;

    private EditText nameText;
    private EditText phoneText;
    private EditText passwordText;
    private EditText emailText;
    private EditText shopNameText;
    private EditText shopAddressText;

    private String name;
    private String phone;
    private String password;
    private String email;
    private String shopName;
    private String shopAddress;

    public SharedPreferences sessionPref;
    public SharedPreferences.Editor prefEditor;
    public final String SESSION_PREF_NAME = "MyCookieStore";
    public final String SESSION_PREF_SESSIONID = "session";
    public String COOKIES_HEADER = "Set-Cookie";

    SessionManager session;

    private boolean EMAIL_NOT_ENTERED = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        session = new SessionManager(getApplicationContext());

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Button SignupButton = (Button) findViewById(R.id.btn_signup);
        TextView LoginTxtView = (TextView) findViewById(R.id.link_login);
        LoginTxtView.setText("Already have an account ? Sign In here");


        nameText = (EditText) findViewById(R.id.input_name);
        phoneText = (EditText) findViewById(R.id.input_phone);
        passwordText = (EditText) findViewById(R.id.input_password);
        emailText = (EditText) findViewById(R.id.input_email);
        shopNameText = (EditText) findViewById(R.id.input_shop_name);
        shopAddressText = (EditText) findViewById(R.id.input_shop_address);

        SignupButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                if(validateFormSuccess()) {


                    name = nameText.getText().toString();
                    phone = phoneText.getText().toString();
                    password = passwordText.getText().toString();
                    email = emailText.getText().toString();
                    shopName = shopNameText.getText().toString();
                    shopAddress = shopAddressText.getText().toString();

                    if (email.equalsIgnoreCase("")){
                        EMAIL_NOT_ENTERED = true;
                    }

                    new SendPostRequest().execute();
                }
                else {
                    Toast.makeText(getApplicationContext(),"Some error occurred, Please try again.", Toast.LENGTH_LONG).show();
                }
            }
            });

    }

    public class SendPostRequest extends AsyncTask<String, Void, String> {

        protected void onPreExecute(){}

        protected String doInBackground(String... arg0) {

            try {

                URL url = new URL("http://b112da2f.ngrok.io/signup"); // here is our URL path

                JSONObject postDataParams = new JSONObject();


                postDataParams.put("inputName", name);
                postDataParams.put("inputPhone", phone);
                postDataParams.put("inputPassword", password);
                if(EMAIL_NOT_ENTERED==false){
                    postDataParams.put("inputEmail", email);
                }
                else {
                    postDataParams.put("inputEmail", " ");
                }
                postDataParams.put("inputShopName", shopName);
                postDataParams.put("inputShopAddress", shopAddress);


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

                Map<String, List<String>> headerFields = conn.getHeaderFields();
                List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);
                if (cookiesHeader != null) {
                    for (String cookie : cookiesHeader) {
                        String sessionid = HttpCookie.parse(cookie).get(0).toString();
                        Log.e("Signup session id:",sessionid);
                        prefEditor.putString(SESSION_PREF_SESSIONID, sessionid);
                        prefEditor.commit();
                        Log.e("Signup attemp2fix:",HttpCookie.parse(cookie).toString());
                    }
                }



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
            Toast.makeText(getApplicationContext(), result,
                    Toast.LENGTH_LONG).show();
            Log.e("response is :",result.toString());

            session.createLoginSession(phone);
            Intent intent = new Intent(SignupActivity.this, NavigationActivity.class);
            startActivity(intent);
            finish();

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


    private Boolean validateFormSuccess(){
        if(nameText.getText().toString().equalsIgnoreCase("") || nameText.getText().toString().length() >63 ){
            Toast.makeText(getApplicationContext(),"Please enter your full name (Maximum 63 Characters)", Toast.LENGTH_LONG).show();
            return false;
        }

        if(phoneText.getText().toString().length() <= 9 || phoneText.getText().toString().length() > 10){
            Toast.makeText(getApplicationContext(),"Please enter only 10 digits without country code or 0",Toast.LENGTH_LONG).show();
            return false;
        }

        if(passwordText.getText().toString().length() <= 5 || passwordText.getText().toString().length() > 60){
            Toast.makeText(getApplicationContext(),"Range of length of password is between 6-60",Toast.LENGTH_LONG).show();
            return false;
        }

        if(!TextUtils.isEmpty(emailText.getText().toString()) && !Patterns.EMAIL_ADDRESS.matcher(emailText.getText().toString()).matches()){
            Toast.makeText(getApplicationContext(),"Enter a valid email or leave it empty.",Toast.LENGTH_LONG).show();
            return false;
        }

        if(shopNameText.getText().toString().length() > 43){
            Toast.makeText(getApplicationContext(),"Enter shop name with less than 44 characters",Toast.LENGTH_LONG).show();
            return false;
        }

        if(shopAddressText.getText().toString().length() > 400){
            Toast.makeText(getApplicationContext(),"Enter shop address with less than 400 characters",Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }
    public void OpenLoginFunction(View view) {
        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
