package com.example.shanu.mode;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.shanu.mode.data.InventoryDbHelper;
import com.example.shanu.mode.data.StockContract;
import com.example.shanu.mode.data.StockItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

import static android.Manifest.permission.CAMERA;

public class DetailsActivity extends AppCompatActivity {

    private static final String LOG_TAG = DetailsActivity.class.getCanonicalName();
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private static final int PERMISSION_REQUEST_CODE = 200;
    private static final int REQUEST_SELECT_IMAGE = 0;

    //The URI of the image selected to detect.
    private Uri mImageUri;
    // The image selected to detect.
    private Bitmap mBitmap;

    public SharedPreferences sessionPref;
    public SharedPreferences.Editor prefEditor;
    public final String SESSION_PREF_NAME = "MyCookieStore";
    public final String SESSION_PREF_SESSIONID = "session";
    public String COOKIES_HEADER = "Set-Cookie";
    public String SESSION_ID ;

    public int NO_OF_IMAGES = 0;


    private InventoryDbHelper dbHelper;
    private View view;
    EditText nameEdit;
    EditText priceEdit;
    EditText quantityEdit;
    EditText manufacturingEdit;
    EditText expiryEdit;
    EditText supplierEmailEdit;
    long currentItemId;
    ImageButton decreaseQuantity;
    ImageButton increaseQuantity;
    Button imageBtn1;
    Button imageBtn2;
    Button imageBtn3;
    Button imageBtn4;
    Button imageBtn5;
    Button submitBtn;
    ImageView imageView1;
    ImageView imageView2;
    ImageView imageView3;
    ImageView imageView4;
    ImageView imageView5;
    public String encodedImage1;
    public String encodedImage2;
    public String encodedImage3;
    public String encodedImage4;
    public String encodedImage5;

    public String productName;
    public String productPrice;
    public String productQuantity;
    public String productMFG;
    public String productEXP;

    Uri actualUri;
    private static final int PICK_IMAGE_REQUEST = 0;
    Boolean infoItemHasChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_details);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        nameEdit = (EditText) findViewById(R.id.product_name_edit);
        priceEdit = (EditText) findViewById(R.id.price_edit);
        quantityEdit = (EditText) findViewById(R.id.quantity_edit);
        manufacturingEdit = (EditText) findViewById(R.id.manufaturing_edit);
        expiryEdit = (EditText) findViewById(R.id.expiry_edit);
        decreaseQuantity = (ImageButton) findViewById(R.id.decrease_quantity);
        increaseQuantity = (ImageButton) findViewById(R.id.increase_quantity);



        imageBtn1 = (Button) findViewById(R.id.select_image1);
        imageBtn2 = (Button) findViewById(R.id.select_image2);
        imageBtn3 = (Button) findViewById(R.id.select_image3);
        imageBtn4 = (Button) findViewById(R.id.select_image4);
        imageBtn5 = (Button) findViewById(R.id.select_image5);
        submitBtn = (Button) findViewById(R.id.submit_button);

        imageView1 = (ImageView) findViewById(R.id.image_view1);
        imageView2 = (ImageView) findViewById(R.id.image_view2);
        imageView3 = (ImageView) findViewById(R.id.image_view3);
        imageView4 = (ImageView) findViewById(R.id.image_view4);
        imageView5 = (ImageView) findViewById(R.id.image_view5);




        decreaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subtractOneToQuantity();
                infoItemHasChanged = true;
            }
        });

        increaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sumOneToQuantity();
                infoItemHasChanged = true;
            }
        });

        imageBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;

                intent = new Intent(DetailsActivity.this, SelectImageActivity.class);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivityForResult(intent, 1);
            }
        });
        imageBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;

                intent = new Intent(DetailsActivity.this, SelectImageActivity.class);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivityForResult(intent, 2);
            }
        });
        imageBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;

                intent = new Intent(DetailsActivity.this, SelectImageActivity.class);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivityForResult(intent, 3);
            }
        });
        imageBtn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;

                intent = new Intent(DetailsActivity.this, SelectImageActivity.class);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivityForResult(intent, 4);
            }
        });
        imageBtn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;

                intent = new Intent(DetailsActivity.this, SelectImageActivity.class);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivityForResult(intent, 5);
            }
        });
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAndShowAlertDialog();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (!infoItemHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };
        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void subtractOneToQuantity() {
        String previousValueString = quantityEdit.getText().toString();
        int previousValue;
        if (previousValueString.isEmpty()) {
            return;
        } else if (previousValueString.equals("0")) {
            return;
        } else {
            previousValue = Integer.parseInt(previousValueString);
            quantityEdit.setText(String.valueOf(previousValue - 1));
        }
    }

    private void sumOneToQuantity() {
        String previousValueString = quantityEdit.getText().toString();
        int previousValue;
        if (previousValueString.isEmpty()) {
            previousValue = 0;
        } else {
            previousValue = Integer.parseInt(previousValueString);
        }
        quantityEdit.setText(String.valueOf(previousValue + 1));
    }


    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {


                    boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (cameraAccepted)
                        Snackbar.make(view, "Permission Granted, Now you can access location data and camera.", Snackbar.LENGTH_LONG).show();
                    else {

                        Snackbar.make(view, "Permission Denied, You cannot access location data and camera.", Snackbar.LENGTH_LONG).show();
                    }
                }


                break;
        }
    }


    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(DetailsActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }




//    public void tryToOpenImageSelector() {
//        if (ContextCompat.checkSelfPermission(this,
//                Manifest.permission.READ_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(this,
//                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
//                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
//            return;
//        }
//        openImageSelector();
//    }
//
//    private void openImageSelector() {
//        Intent intent;
//        if (Build.VERSION.SDK_INT < 19) {
//            intent = new Intent(Intent.ACTION_GET_CONTENT);
//        } else {
//            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//            intent.addCategory(Intent.CATEGORY_OPENABLE);
//        }
//        intent.setType("image/*");
//        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
//    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
//        // The ACTION_OPEN_DOCUMENT intent was sent with the request code READ_REQUEST_CODE.
//        // If the request code seen here doesn't match, it's the response to some other intent,
//        // and the below code shouldn't run at all.
//
//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
//            // The document selected by the user won't be returned in the intent.
//            // Instead, a URI to that document will be contained in the return intent
//            // provided to this method as a parameter.  Pull that uri using "resultData.getData()"
//
//            if (resultData != null) {
//                actualUri = resultData.getData();
//                imageView1.setImageURI(actualUri);
//                imageView1.invalidate();
//            }
//        }
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("DescribeActivity", "onActivityResult");
        switch (requestCode) {
            case 1:
                if (resultCode == Activity.RESULT_OK) {
                    // If image is selected successfully, set the image URI and bitmap.
                    mImageUri = data.getData();

                    mBitmap = ImageHelper.loadSizeLimitedBitmapFromUri(
                            mImageUri, getContentResolver());
                    if (mBitmap != null) {
                        // Show the image on screen
                        imageView1.setImageBitmap(mBitmap);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] imageBytes = baos.toByteArray();
                        encodedImage1 = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                    }
                }break;
            case 2:
                if (resultCode == Activity.RESULT_OK) {
                    // If image is selected successfully, set the image URI and bitmap.
                    mImageUri = data.getData();

                    mBitmap = ImageHelper.loadSizeLimitedBitmapFromUri(
                            mImageUri, getContentResolver());
                    if (mBitmap != null) {
                        // Show the image on screen
                        imageView2.setImageBitmap(mBitmap);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] imageBytes = baos.toByteArray();
                        encodedImage2 = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                    }
                }break;
            case 3:
                if (resultCode == Activity.RESULT_OK) {
                    // If image is selected successfully, set the image URI and bitmap.
                    mImageUri = data.getData();

                    mBitmap = ImageHelper.loadSizeLimitedBitmapFromUri(
                            mImageUri, getContentResolver());
                    if (mBitmap != null) {
                        // Show the image on screen
                        imageView3.setImageBitmap(mBitmap);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] imageBytes = baos.toByteArray();
                        encodedImage3 = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                    }
                }
                break;
            case 4:
                if (resultCode == Activity.RESULT_OK) {
                    // If image is selected successfully, set the image URI and bitmap.
                    mImageUri = data.getData();

                    mBitmap = ImageHelper.loadSizeLimitedBitmapFromUri(
                            mImageUri, getContentResolver());
                    if (mBitmap != null) {
                        // Show the image on screen
                        imageView4.setImageBitmap(mBitmap);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] imageBytes = baos.toByteArray();
                        encodedImage4 = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                    }
                }
                break;
            case 5:
                if (resultCode == Activity.RESULT_OK) {
                    // If image is selected successfully, set the image URI and bitmap.
                    mImageUri = data.getData();

                    mBitmap = ImageHelper.loadSizeLimitedBitmapFromUri(
                            mImageUri, getContentResolver());
                    if (mBitmap != null) {
                        // Show the image on screen
                        imageView5.setImageBitmap(mBitmap);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] imageBytes = baos.toByteArray();
                        encodedImage5 = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                    }
                }
                break;
        }
    }

    private void createAndShowAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Inventory Addition ?");
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //TODO
                prepareImagesForSending();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //TODO
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void prepareImagesForSending()
    {
        if(imageView1.getDrawable() != null)
            NO_OF_IMAGES++;
        if(imageView2.getDrawable() != null)
            NO_OF_IMAGES++;
        if(imageView3.getDrawable() != null)
            NO_OF_IMAGES++;
        if(imageView4.getDrawable() != null)
            NO_OF_IMAGES++;
        if(imageView5.getDrawable() != null)
            NO_OF_IMAGES++;

        productName = nameEdit.getText().toString();
        productPrice = priceEdit.getText().toString();
        productQuantity = priceEdit.getText().toString();
        productMFG = manufacturingEdit.getText().toString();
        productEXP = expiryEdit.getText().toString();

        new SendPostRequest().execute();
    }


    public class SendPostRequest extends AsyncTask<String, Void, String> {

        protected void onPreExecute(){
//            mProgressBar.setVisibility(View.VISIBLE);
            Toast.makeText(getApplicationContext(), "Please wait !",
                    Toast.LENGTH_LONG).show();
//            submitBtn.setClickable(false);
        }

        protected String doInBackground(String... arg0) {

            try {

                URL url = new URL(" http://3bbee033.ngrok.io/update-inventory"); // here is your URL path

                JSONObject postDataParams = new JSONObject();

//                nameEdit = (EditText) findViewById(R.id.product_name_edit);
//                priceEdit = (EditText) findViewById(R.id.price_edit);
//                quantityEdit = (EditText) findViewById(R.id.quantity_edit);
//                manufacturingEdit = (EditText) findViewById(R.id.manufaturing_edit);
//                expiryEdit = (EditText) findViewById(R.id.expiry_edit);


                postDataParams.put("inputItemName", productName);
                postDataParams.put("inputPrice", productPrice);
                postDataParams.put("inputQuantity", productQuantity);
                postDataParams.put("inputMfgDate", productMFG);
                postDataParams.put("inputExpDate", productEXP);
                postDataParams.put("inputNumberOfImages", NO_OF_IMAGES);



                if(NO_OF_IMAGES>=1)
                    postDataParams.put("image1", encodedImage1);
                if(NO_OF_IMAGES>=2)
                    postDataParams.put("image2", encodedImage2);
                if(NO_OF_IMAGES>=3)
                    postDataParams.put("image3", encodedImage3);
                if(NO_OF_IMAGES>=4)
                    postDataParams.put("image4", encodedImage4);
                if(NO_OF_IMAGES>=5)
                    postDataParams.put("image5", encodedImage5);

//                postDataParams.put("inputTestImageBase64EncodedString", encodedImage);
//                Log.e("params",postDataParams.toString());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(150000 /* milliseconds */);
                conn.setConnectTimeout(150000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                sessionPref = getSharedPreferences(SESSION_PREF_NAME, MODE_PRIVATE);
                SESSION_ID = sessionPref.getString(SESSION_PREF_SESSIONID, null);
                Log.e("SessPref's sessionid",SESSION_ID);
                Log.e("SessPref's sessionid2", HttpCookie.parse(SESSION_ID).toString());
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

            //mProgressBar.setVisibility(View.GONE);
            Log.e("response is :",result.toString());
            Intent data = new Intent();
            try {
                JSONObject jResult = new JSONObject(result);
                String flag = jResult.getString("status");
                if (flag.equalsIgnoreCase("Success"))
                {
                    Toast.makeText(getApplicationContext(), "Successfully Added.",
                            Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(DetailsActivity.this, NavigationActivity.class);
                    startActivity(intent);

                }
                else if(flag.equalsIgnoreCase("Failed")){

                    Toast.makeText(getApplicationContext(), "Unexpected Error Occurred, Try again.",
                            Toast.LENGTH_LONG).show();
                    submitBtn.setClickable(true);
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
