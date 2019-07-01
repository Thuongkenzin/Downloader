package com.example.downloader;

import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Paths;

public class MainActivity extends AppCompatActivity {

    EditText urlText;
    Button mDownloadBtn;
    Button mViewBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        urlText = findViewById(R.id.urlText);
        mDownloadBtn = findViewById(R.id.downloadURL);

        mViewBtn = findViewById(R.id.view_download);

        mDownloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String url = "http://speedtest.ftp.otenet.gr/files/test10Mb.db";
                if(isConnectingToInternet()) {
                    new DownloadTask(MainActivity.this, url);
                }else{
                    Toast.makeText(MainActivity.this, "There is no internet connection.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDownloadFolder();
            }
        });
    }

    private void startDownloading(){
        //String url = urlText.getText().toString();
        //String url = "https://www.nasa.gov/images/content/206402main_jsc2007e113280_hires.jpg";
        String url = "http://speedtest.ftp.otenet.gr/files/test10Mb.db";

        //create download request
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));


        //set title download notification
       request.setDescription("Downloading file...");


      request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);


    }



    private void openDownloadFolder(){
        if(new CheckForSDCard().isSDCardPresent()) {

            File apkStorage = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "/Android_Download");

            if(!apkStorage.exists()){
                Toast.makeText(this, "There is no directory", Toast.LENGTH_SHORT).show();
            }
            else {

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                Uri uri = Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/Android_Download");
                intent.setDataAndType(uri, "file/*");
                startActivity(Intent.createChooser(intent,"Open Download Folder"));
            }
        }
        else {
            Toast.makeText(MainActivity.this, "There is no SD Card.", Toast.LENGTH_SHORT).show();
        }


    }
    private boolean isConnectingToInternet(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netWorkInfo = connectivityManager.getActiveNetworkInfo();
        if(netWorkInfo!=null && netWorkInfo.isConnected())
            return true;
        else
            return false;
    }

}
