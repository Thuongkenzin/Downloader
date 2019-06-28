package com.example.downloader;

import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
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

    private static final int PERMISSION_STORAGE_CODE = 1000;


    EditText urlText;
    Button mDownloadBtn;
    Button mViewBtn;

    long longId;
    private BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID,-1);
            if(longId == id){
                Toast.makeText(MainActivity.this, "Download Completed", Toast.LENGTH_SHORT).show();
            }

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        urlText = findViewById(R.id.urlText);
        mDownloadBtn = findViewById(R.id.downloadURL);

        mViewBtn = findViewById(R.id.view_download);

        registerReceiver(onDownloadComplete,new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        mDownloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                        //permission denied, request it
                        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        //show popup for runtime permission
                        requestPermissions(permissions,PERMISSION_STORAGE_CODE);

                    }else
                    {//permission already granted, perform download
                        startDownloading();

                    }
                }else {
                    startDownloading();
                }

            }
        });

        mViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);
                startActivity(intent);
            }
        });
    }

    private void startDownloading(){
        //String url = urlText.getText().toString();
        //String url = "https://www.nasa.gov/images/content/206402main_jsc2007e113280_hires.jpg";
        //String url = "http://speedtest.ftp.otenet.gr/files/test10Mb.db";
        String url ="https://vnso-qt-3-tf-mp3-s1-zmp3.zadn.vn/0fa1213a097de023b96c/2819329265115630931?authen=exp=1561715854~acl=/0fa1213a097de023b96c/*~hmac=00dd47c0d384789cab5aa1ae97291dba&filename=Sao-Em-Vo-Tinh-Jack-Liam.mp3";

        //create download request
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        // allow types of network to download files
        //request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        //String filename = url.substring(url.lastIndexOf('/')+1);

        //set title download notification
       request.setDescription("Downloading file...");

      request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

       // request.setDestinationInExternalFilesDir(this, Environment.DIRECTORY_DOWNLOADS,DownloadManager.COLUMN_TITLE);
       //request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,DownloadManager.COLUMN_LOCAL_FILENAME);

      // Log.v("class","URI: "+);


        //get download service and enque
        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        longId = downloadManager.enqueue(request);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSION_STORAGE_CODE:
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //permission grant from popup, perform download
                    startDownloading();
                }else {
                    //show error message
                    Toast.makeText(this, "Permission Denied...!", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(onDownloadComplete);
    }
}
