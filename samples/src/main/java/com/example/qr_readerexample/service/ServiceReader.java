package com.example.qr_readerexample.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Debug;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.qr_readerexample.utils.C;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import db.AllData;
import db.CloudantData;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by root on 17-12-14.
 */

public class ServiceReader extends Service {
    private List<String> temp,humidity;
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();
    private int maxSamples= 2000;

    private static final String TAG = "ServiceReader";
    private final String url = "https://fb2e1a82-8890-4143-810d-e5c79f44a611-bluemix.cloudant.com/" +
            "nodered/_all_docs?include_docs=true&limit=10";
    private Runnable readRunnable = new Runnable() { // http://docs.oracle.com/javase/8/docs/technotes/guides/concurrency/threadPrimitiveDeprecation.html
        @Override
        public void run() {
            // The service makes use of an explicit Thread instead of a Handler because with the Threat the code is executed more synchronously.
            // However the ViewGraphic is drew with a Handler because the drawing code must be executed in the UI thread.
            Thread thisThread = Thread.currentThread();
            while (readThread == thisThread) {
                getCloudantData();
                try {

                    //线程睡眠的时间，为图表中设置的间隔时间
                    Thread.sleep(1000);
/*					synchronized (this) {
						while (readThread == thisThread && threadSuspended)
							wait();
					}*/
                } catch (InterruptedException e) {
                    break;
                }

                // The Runnable can be suspended and resumed with the below code:
//				threadSuspended = !threadSuspended;
//				if (!threadSuspended)
//					notify();
            }
        }

/*		public synchronized void stop() {
			readThread = null;
			notify();
		}*/


    };


    @Override
    public void onCreate() {

        temp = new ArrayList<String>(maxSamples);
        humidity = new ArrayList<String>(maxSamples);
        super.onCreate();
    }

    private void getCloudantData(){
        Request request   = new Request.Builder()
                .url(url)
                .cacheControl(CacheControl.FORCE_NETWORK)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG,"okhttp is request error");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i(TAG,"okhttp is request success!");
                //获取服务器返回的json字符串
                String responseString = response.body().string();


                // 接收数据超过2000,则自动清零
                while (temp.size() >= maxSamples) {
                    temp.remove(temp.size()-1);
                    humidity.remove(humidity.size()-1);

                }
                //使用Gson 解析json字符串
                AllData allData = gson.fromJson(responseString, AllData.class);

                // 将"rows"信息将数据传输到CloudantData中，rows有100个数组组成
                List<CloudantData> cloudantDataList = allData.getRows();

                Log.i(TAG,"cloudantDataList size:"+cloudantDataList.size());
                for (int i= 0;i<cloudantDataList.size();i++){

                    //AddData(sensordata, "A0");
                    //final String temp = cloudantDataList.get(i).doc.payload.d.getTemp();
                    //final String humidity = cloudantDataList.get(i).doc.payload.d.getHumidity();
                    temp.add(cloudantDataList.get(i).doc.payload.d.getTemp());
                    humidity.add(cloudantDataList.get(i).doc.payload.d.getHumidity());
                    Log.i(TAG,"temp:"+temp+", humidity:"+humidity);
                }
            }
        });
    }



    private volatile Thread readThread = new Thread(readRunnable, C.readThread);

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
