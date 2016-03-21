package com.example.ljd.asynctask;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private ProgressBar mPb;
    private Button mButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPb = (ProgressBar)findViewById(R.id.progress_bar);
        mButton = (Button)findViewById(R.id.button);
        mButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        new DownloadAsyncTask().execute();
    }

    class DownloadAsyncTask extends AsyncTask<Void,Integer,Boolean>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mPb.setMax(5 * 1000);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            for (int i=0;i<=5000;i++){
                publishProgress(i);
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return true;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            mPb.setProgress(values[0]);

        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean){
                Toast.makeText(MainActivity.this,"下载完成",Toast.LENGTH_SHORT).show();
            }
        }

    }
}
