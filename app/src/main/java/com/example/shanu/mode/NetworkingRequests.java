package com.example.shanu.mode;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class NetworkingRequests extends Application{

    public static HttpURLConnection connection;

    public HttpURLConnection getConnection (){
        return  connection;
    }
}