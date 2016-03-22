package com.example.ljd.asynctask;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView mImageView1;
    private ImageView mImageView2;
    private ImageView mImageView3;
    private ProgressDialog mProgressDialog;
    private DownloadAsyncTask mDownloadAsyncTask;

    private Button mButton;
    private String[] path = {
            "http://d.hiphotos.baidu.com/zhidao/pic/item/7c1ed21b0ef41bd5e6c559a057da81cb38db3dcb.jpg",
            "http://a.hiphotos.baidu.com/zhidao/pic/item/728da9773912b31bb82f07408418367adab4e11c.jpg",
            "http://pic.6188.com/upload_6188s/flashAll/s800/20120907/1346981960xNARbD.jpg"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mButton = (Button)findViewById(R.id.button);
        mButton.setOnClickListener(this);
        mImageView1 = (ImageView)findViewById(R.id.image1);
        mImageView2 = (ImageView)findViewById(R.id.image2);
        mImageView3 = (ImageView)findViewById(R.id.image3);
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

    /**
     * 获取下载到sd卡中下载的图片
     * @param i
     * @return
     */
    public Bitmap getLocalBitmap(int i) {
        try {
            File file = new File(Environment.getExternalStorageDirectory(), i+".jpg");
            FileInputStream fis = new FileInputStream(file);
            return BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    class DownloadAsyncTask extends AsyncTask<String,Integer,Boolean>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(MainActivity.this);
            mProgressDialog.setTitle("下载");
            mProgressDialog.setMessage("正在下载......");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            for (int i=0;i<params.length;i++){
                try{
                    if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                        URL url = new URL(params[i]);
                        HttpURLConnection conn =  (HttpURLConnection) url.openConnection();
                        conn.setConnectTimeout(5000);
                        InputStream is = conn.getInputStream();
                        File file = new File(Environment.getExternalStorageDirectory(), i+".jpg");
                        FileOutputStream fos = new FileOutputStream(file);
                        BufferedInputStream bis = new BufferedInputStream(is);
                        byte[] buffer = new byte[1024];
                        int len ;
                        while((len =bis.read(buffer))!=-1){
                            fos.write(buffer, 0, len);
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
            mProgressDialog.dismiss();
            mImageView1.setImageBitmap(getLocalBitmap(0));
            mImageView2.setImageBitmap(getLocalBitmap(1));
            mImageView3.setImageBitmap(getLocalBitmap(2));
            if (aBoolean){
                Toast.makeText(MainActivity.this,"下载完成",Toast.LENGTH_SHORT).show();
            }
        }
    }

}
