package com.example.qr_readerexample;

import android.app.Activity;
import android.app.Application;
import android.os.SystemClock;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 文件描述：
 * Created by Administrator on 2017/10/3.
 */

public class App extends Application {
    //private static TcpServer tcpServer = null;
    private static final int PORT = 9999;
    ExecutorService exec = Executors.newCachedThreadPool();
    private List<Activity> activityList;
    private static App instance =new App();
    @Override
    public void onCreate() {
        super.onCreate();

        //

        // Don't do this! This is just so cold launches take some time
        SystemClock.sleep(TimeUnit.SECONDS.toMillis(3));
    }





    public static App getInstance() {
        return instance;
    }

    public void exit() {
        Iterator<Activity> iterator = activityList.iterator();
        while (iterator.hasNext()) {
            Activity activity = iterator.next();
            activity.finish();
        }
        activityList.clear();
    }
}