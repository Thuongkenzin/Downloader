package com.example.downloader;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.renderscript.ScriptGroup;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;

import java.net.URL;

public class DownloadTask {
    private static final String TAG = "DownloadTask";
    private Context context;
    private String downloadUrl;
    private String downloadFileName;

    public DownloadTask(Context context, String downloadUrl){
        this.context = context;
        this.downloadUrl = downloadUrl;
        this.downloadFileName= downloadUrl.substring(downloadUrl.lastIndexOf('/')+1);
        new DownloadingTask().execute();
    }

    private class DownloadingTask extends AsyncTask<Void,Void,Void>{
        File apkStorage;
        File outputFile;

        @Override
        protected void onPostExecute(Void aVoid) {

            Toast.makeText(context, "Download Complete", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                URL url = new URL(downloadUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                if (connection.getResponseCode() !=HttpURLConnection.HTTP_OK){
                    Log.e(TAG, "Server return HTTP " + connection.getResponseCode() + " " + connection.getResponseMessage());
                }

                if (new CheckForSDCard().isSDCardPresent()) {
                    apkStorage = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)  , "/Android_Download");
                }

                if(!apkStorage.exists()){
                    apkStorage.mkdir();
                    Log.e(TAG,"Directory created.");
                }

                outputFile = new File(apkStorage, downloadFileName);

                if(!outputFile.exists()){
                    outputFile.createNewFile();
                    Log.e(TAG, "File Created");
                }

                FileOutputStream fos = new FileOutputStream(outputFile);

                InputStream is = connection.getInputStream();

                byte[] buffer = new byte[1024];
                int count = 0;
                while((count = is.read(buffer)) > 0){
                    fos.write(buffer,0,count);
                }

            //close all connection to avoid memory leak
                fos.close();
                is.close();


            } catch (Exception e) {
                e.printStackTrace();
                outputFile = null;
                Log.e(TAG," Download Error Exception " +e.getMessage());
            }
            return null;
        }

    }
}
