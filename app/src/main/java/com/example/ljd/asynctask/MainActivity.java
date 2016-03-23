package com.example.ljd.asynctask;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private DownloadAsyncTask mDownloadAsyncTask;

    private Button mButton;
    private String[] path = {
            "http://msoftdl.360.cn/mobilesafe/shouji360/360safesis/360MobileSafe_6.2.3.1060.apk",
            "http://dlsw.baidu.com/sw-search-sp/soft/7b/33461/freeime.1406862029.exe",
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mButton = (Button)findViewById(R.id.button);
        mButton.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        if (mDownloadAsyncTask != null){
            mDownloadAsyncTask.cancel(true);
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {

        mDownloadAsyncTask = new DownloadAsyncTask();
        mDownloadAsyncTask.execute(path);
    }



    class DownloadAsyncTask extends AsyncTask<String,Integer,Boolean>{

        private ProgressDialog mPBar;
        private int fileSize;     //下载的文件大小
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mPBar = new ProgressDialog(MainActivity.this);
            mPBar.setProgressNumberFormat("%1d KB/%2d KB");
            mPBar.setTitle("下载");
            mPBar.setMessage("正在下载，请稍后...");
            mPBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mPBar.setCancelable(false);
            mPBar.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            //下载图片

            for (int i=0;i<params.length;i++){
                try{
                    if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                        URL url = new URL(params[i]);
                        HttpURLConnection conn =  (HttpURLConnection) url.openConnection();
                        //设置超时时间
                        conn.setConnectTimeout(5000);
                        //获取下载文件的大小
                        fileSize = conn.getContentLength();
                        InputStream is = conn.getInputStream();
                        //获取文件名称
                        String fileName = path[i].substring(path[i].lastIndexOf("/") + 1);
                        File file = new File(Environment.getExternalStorageDirectory(), fileName);
                        FileOutputStream fos = new FileOutputStream(file);
                        BufferedInputStream bis = new BufferedInputStream(is);
                        byte[] buffer = new byte[1024];
                        int len ;
                        int total = 0;
                        while((len =bis.read(buffer))!=-1){
                            fos.write(buffer, 0, len);
                            total += len;
                            publishProgress(total);
                            fos.flush();
                        }
                        fos.close();
                        bis.close();
                        is.close();
                    }
                    else{
                        return false;
                    }
                }catch (IOException e){
                    e.printStackTrace();
                    return false;
                }
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            mPBar.dismiss();
            if (aBoolean){
                Toast.makeText(MainActivity.this,"下载完成",Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this,"下载失败",Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            mPBar.setMax(fileSize / 1024);
            mPBar.setProgress(values[0]/1024);
        }
    }

}
