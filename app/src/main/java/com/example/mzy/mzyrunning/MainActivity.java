package com.example.mzy.mzyrunning;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private Button bt1;
    private Context mContext;

    SearchDialog searchDialog ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bt1 = (Button) findViewById(R.id.button);
        mContext = MainActivity.this;
    }


    Handler uihandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    Log.d("uihandler", "handleMessage: 匹配成功");
                    uihandler.removeCallbacks(runnable);
                    searchDialog.cancel();
                    Intent intent = new Intent(mContext, ThirdActivity.class);
                    mContext.startActivity(intent);
                    break;
            }
        }
    };

    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            //要做的事情
            httpGet();
            uihandler.postDelayed(this, 2000);
        }
    };


    public void start(View view){

        show_anim(view);
        uihandler.postDelayed(runnable, 2000);

    }


    public void show_anim(View view){
        searchDialog = new SearchDialog(MainActivity.this, uihandler, runnable);
        //设置背景透明
        searchDialog.getWindow().setBackgroundDrawable(new ColorDrawable());
        searchDialog.show();
    }

    private String url = "http://192.168.43.25:8888/";

    public void httpGet(){

        Log.d("run", "httpGet: ");
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .get()
                .url(url + "okhttpmzy/servlet/OkHttpServlet")
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                Log.d("http", "onFailure: 回调失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String result = response.body().string();

                Log.d("response", "onResponse() returned: " +  result);
                ResponseThread responseThread = new ResponseThread(result);
                responseThread.start();
            }
        });
    }

    class ResponseThread extends Thread{
        private String content;
        private int contentInt;
        public ResponseThread(String content){
            this.content = content;
        }

        @Override
        public void run() {
            super.run();
            contentInt = Integer.parseInt(content);

            if(contentInt % 10 == 0){
                uihandler.sendEmptyMessage(1);
            }
            Log.d("ResponseThread", "执行子线程 ");
        }
    }
}
