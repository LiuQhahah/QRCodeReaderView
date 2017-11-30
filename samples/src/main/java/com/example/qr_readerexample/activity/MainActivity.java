package com.example.qr_readerexample.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.qr_readerexample.App;
import com.example.qr_readerexample.R;
import com.example.qr_readerexample.TcpServer;
import com.example.qr_readerexample.base.BaseActivity;
import com.example.qr_readerexample.base.BaseFragment;
import com.example.qr_readerexample.utils.StatusBarUtil;
import com.example.qr_readerexample.utils.ToastHelper;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindString;
import butterknife.BindView;
import db.DataBaseHelper;


public class MainActivity extends BaseActivity {


   /* private MFPPush push; // Push client
    private MFPPushNotificationListener notificationListener; // Notification listener to handle a push sent to the phone
*/
    @BindString(R.string.app_exit)
    String app_exit;
    @BindView(R.id.main_tab_real_data)
    View tabRealData;
    @BindView(R.id.main_tab_history_data)
    View tabHisData;
    @BindView(R.id.main_tab_qr)
    View tabQr;


    private final int[] mTabIcons = new int[]{R.drawable.tab_main_home_selector,
            R.drawable.tab_main_data_selector, R.drawable.tab_main_energy_selector};

    private long mExitTime;
    // 当前fragment的index
    private int currentTabIndex = 0;
    private View[] mTabs;
    private static final String TAG = "MainActivity";
    public static long endTime;
    static DataBaseHelper myDb;

    private RealDataFragment realdataFragment;
    private HistoryFragment historyFragment;
    private QRFragment qrFragment;


    private static TcpServer tcpServer = null;
    private static final int PORT = 9999;
    ExecutorService exec = Executors.newCachedThreadPool();
    private List<Activity> activityList;
    private static App instance;

    private static String sensordata;


    private static String recvdate = null;

    protected BaseFragment currentFragment;

    private static final int MY_PERMISSION_REQUEST_CAMERA = 0;

    private MyBroadcastReceiver myBroadcastReceiver = new MyBroadcastReceiver();
    private final MyHandler myHandler = new MyHandler(this);

    @SuppressLint("StaticFieldLeak")
    public static Context context;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initViewsAndEvents(Bundle savedInstanceState) {
        StatusBarUtil.darkMode(this);

        context = this;


        //registerDevice();


        /**
         * push  set
         */
        // initialize core SDK with IBM Bluemix application Region, TODO: Update region if not using Bluemix US SOUTH
        //BMSClient.getInstance().initialize(this, BMSClient.REGION_US_SOUTH);

        // Grabs push client sdk instance
/*        push = MFPPush.getInstance();
        // Initialize Push client
        // You can find your App Guid and Client Secret by navigating to the Configure section of your Push dashboard, click Mobile Options (Upper Right Hand Corner)
        // TODO: Please replace <APP_GUID> and <CLIENT_SECRET> with a valid App GUID and Client Secret from the Push dashboard Mobile Options
        push.initialize(this, "e7043b0c-d330-43c3-8de5-3dbee9ff1dd6", "3e2cdf7b-b15c-4d51-b38a-44dfee258476");

        // Create notification listener and enable pop up notification when a message is received
        notificationListener = new MFPPushNotificationListener() {
            @Override
            public void onReceive(final MFPSimplePushNotification message) {
                Log.i(TAG, "Received a Push Notification: " + message.toString());
                runOnUiThread(new Runnable() {
                    public void run() {
                        new android.app.AlertDialog.Builder(MainActivity.this)
                                .setTitle("Received a Push Notification")
                                .setMessage(message.getAlert())
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                    }
                                })
                                .show();
                    }
                });
            }
        };*/



        //registerDevice();

        //绑定广播接收，准备接收来自IOT2040的数据
        bindReceiver();

        Log.i(TAG, "app_exit " + app_exit);

        endTime = System.currentTimeMillis(); //获取结束时间
        Log.i(TAG, "程序运行时间： " + (endTime - SplashActivity.startTime) + "ms");

        realdataFragment = new RealDataFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.main_content, realdataFragment).commitAllowingStateLoss();
        currentFragment = realdataFragment;
        currentTabIndex = 0;

        initTabs();
        changeFragment(currentTabIndex);


        startTCP();


        if (!(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSION_REQUEST_CAMERA);
        }


    }

    /**
     * Called when the register device button is pressed.
     * Attempts to register the device with your push service on Bluemix.
     * If successful, the push client sdk begins listening to the notification listener.
     * Also includes the example option of UserID association with the registration for very targeted Push notifications.
     *
     */
   /* public void registerDevice() {

        // Checks for null in case registration has failed previously
        if(push==null){
            push = MFPPush.getInstance();
        }

        // Make register button unclickable during registration and show registering text

        Log.i(TAG, "Registering for notifications");


        // Creates response listener to handle the response when a device is registered.
        MFPPushResponseListener registrationResponselistener = new MFPPushResponseListener<String>() {
            @Override
            public void onSuccess(String response) {
                // Split response and convert to JSON object to display User ID confirmation from the backend
                String[] resp = response.split("Text: ");
                //String[] resp = response.split(" : ");
                try {
                    JSONObject responseJSON = new JSONObject(resp[1]);
                   Log.i (TAG,"Device Registered Successfully with USER ID " + responseJSON.getString("userId"));
//                   Toast.makeText(context,"Device Registered Successfully with USER ID " + responseJSON.getString("userId"),Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.i(TAG, "Successfully registered for push notifications, " + response);
                // Start listening to notification listener now that registration has succeeded
                push.listen(notificationListener);

            }

            @Override
            public void onFailure(MFPPushException exception) {
                String errLog = "Error registering for push notifications: ";
                String errMessage = exception.getErrorMessage();
                int statusCode = exception.getStatusCode();

                // Set error log based on response code and error message
                if(statusCode == 401){
                    errLog += "Cannot authenticate successfully with Bluemix Push instance, ensure your CLIENT SECRET was set correctly.";
                } else if(statusCode == 404 && errMessage.contains("Push GCM Configuration")){
                    errLog += "Push GCM Configuration does not exist, ensure you have configured GCM Push credentials on your Bluemix Push dashboard correctly.";
                } else if(statusCode == 404 && errMessage.contains("PushApplication")){
                    errLog += "Cannot find Bluemix Push instance, ensure your APPLICATION ID was set correctly and your phone can successfully connect to the internet.";
                } else if(statusCode >= 500){
                    errLog += "Bluemix and/or your Push instance seem to be having problems, please try again later.";
                }

                Log.e(TAG,errLog);
                // make push null since registration failed
                push = null;
            }
        };

        // Attempt to register device using response listener created above
        // Include unique sample user Id instead of Sample UserId in order to send targeted push notifications to specific users


        push.registerDeviceWithUserId("Sample UserID",registrationResponselistener);
    }

*/

    // If the device has been registered previously, hold push notifications when the app is paused
    @Override
    protected void onPause() {
        super.onPause();

        /*if (push != null) {
            push.hold();
        }*/
    }

    // If the device has been registered previously, ensure the client sdk is still using the notification listener from onCreate when app is resumed
    @Override
    protected void onResume() {
        super.onResume();
      /*  if (push != null) {
            push.listen(notificationListener);
        }*/
    }


    private void startTCP() {
        Log.i(TAG, "ip address is :" + getHostIP() + "\n  PORT = 9999");
        tcpServer = new TcpServer(PORT);
        exec.execute(tcpServer);
    }

    /**
     * 获取ip地址
     *
     * @return
     */
    public String getHostIP() {

        String hostIp = null;
        try {
            Enumeration nis = NetworkInterface.getNetworkInterfaces();
            InetAddress ia = null;
            while (nis.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) nis.nextElement();
                Enumeration<InetAddress> ias = ni.getInetAddresses();
                while (ias.hasMoreElements()) {
                    ia = ias.nextElement();
                    if (ia instanceof Inet6Address) {
                        continue;// skip ipv6
                    }
                    String ip = ia.getHostAddress();
                    if (!"127.0.0.1".equals(ip)) {
                        hostIp = ia.getHostAddress();
                        break;
                    }
                }
            }
        } catch (SocketException e) {
            Log.i(TAG, "SocketException");
            e.printStackTrace();
        }
        Log.i(TAG, "IP ADDRESS :" + hostIp);
        return hostIp;

    }

    private void bindReceiver() {
        IntentFilter intentFilter = new IntentFilter("tcpServerReceiver");
        registerReceiver(myBroadcastReceiver, intentFilter);
    }

    private class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String mAction = intent.getAction();
            switch (mAction) {
                case "tcpServerReceiver":
                    String msg = intent.getStringExtra("tcpServerReceiver");

                    Message message = Message.obtain();
                    message.what = 1;
                    message.obj = msg;
                    myHandler.sendMessage(message);
                    break;
            }
        }
    }

    /**
     * 初始化底部4个导航按钮
     */

    private void initTabs() {
        myDb = new DataBaseHelper(this);

        mTabs = new View[]{tabRealData, tabHisData, tabQr};
        String[] mTabTitles = getResources().getStringArray(R.array.main_tab_titles);
        boolean booleen = mTabs[0].isSelected();
        mTabs[0].setSelected(true);

        for (int i = 0; i < mTabs.length; i++) {
            initTab(i, mTabTitles[i], mTabIcons[i]);
        }

    }


    private class MyHandler extends android.os.Handler {
        private final WeakReference<MainActivity> mActivity;

        MyHandler(MainActivity activity) {
            mActivity = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = mActivity.get();
            if (activity != null) {
                switch (msg.what) {
                    case 1:
                        recvdate = msg.obj.toString();

                        String devidename = recvdate.substring(0, 2);
                        Log.i(TAG, "device name :" + devidename);
                        switch (devidename) {
                            case "A0":
                                sensordata = recvdate.substring(2, 3);
                                //向数据库添加数据
                                AddData(sensordata, "A0");
                                //tvRecvData.setText("设备A0的数据:"+sensordata);
                                Log.i(TAG, "A0 data is :" + sensordata);
                                break;
                            case "A1":
                                sensordata = recvdate.substring(2, 4).trim();
                                //tvRecvData.setText("设备A1的数据:"+sensordata);
                                //向数据库添加数据
                                AddData(sensordata, "A1");
                                Log.i(TAG, "A1 data is :" + sensordata + "; length is " + recvdate.length());

                                break;
                            default:
                                break;

                        }
                        break;
                }
            }
        }
    }


    private static void AddData(String data, String devicename) {
        String systime = getsystime();
        boolean isInserted = myDb.insertData(systime, data, devicename);
        if (isInserted)
            Log.i(TAG, "Data Inserted time :" + systime + ", data :" + data + ", devicename:" + devicename);
        else
            Log.i(TAG, "Data not inserted");
    }

    private static String getsystime() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(new Date());
    }

    /**
     * 初始化单个导航布局
     *
     * @param position  位置
     * @param mTabTitle 标题
     * @param mTabIcon  按钮Res
     */
    private void initTab(final int position, String mTabTitle, int mTabIcon) {
        View tab = mTabs[position];

        ImageView tab_icon_iv = (ImageView) tab.findViewById(R.id.tab_icon_iv);
        TextView tab_title_tv = (TextView) tab.findViewById(R.id.tab_title_tv);

        tab_icon_iv.setImageResource(mTabIcon);
        tab_title_tv.setText(mTabTitle);
        mTabs[position].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                changeFragment(position);
            }
        });

    }

    private void changeFragment(int position) {
        for (int i = 0; i < mTabs.length; i++) {
            mTabs[i].setSelected(i == position);
        }
        switch (position) {
            case 0:
                if (realdataFragment == null) {
                    realdataFragment = new RealDataFragment();
                }
                addOrShowFragment(getSupportFragmentManager().beginTransaction(), realdataFragment);
                break;
            case 1:
                if (historyFragment == null) {
                    historyFragment = new HistoryFragment();
                }
                addOrShowFragment(getSupportFragmentManager().beginTransaction(), historyFragment);
                break;
            case 2:
                if (qrFragment == null) {
                    qrFragment = new QRFragment();
                }
                addOrShowFragment(getSupportFragmentManager().beginTransaction(), qrFragment);
                break;

        }
        currentTabIndex = position;
    }


    /**
     * 添加或者显示 fragment
     *
     * @param transaction
     * @param fragment
     */
    protected void addOrShowFragment(FragmentTransaction transaction, Fragment fragment) {
        if (currentFragment == fragment)
            return;

        if (!fragment.isAdded()) { // 如果当前fragment未被添加，则添加到Fragment管理器中
            transaction.hide(currentFragment).add(R.id.main_content, fragment).commitAllowingStateLoss();
        } else {
            transaction.hide(currentFragment).show(fragment).commitAllowingStateLoss();
        }
        currentFragment = (BaseFragment) fragment;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if (System.currentTimeMillis() - mExitTime > 2000) {
                ToastHelper.shortToast(this, app_exit);
                mExitTime = System.currentTimeMillis();
                return true;
            } else {
                this.finish();
                /*App.getInstance().exit();
                System.exit(0);*/
                //android.os.Process.killProcess(android.os.Process.myPid());
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
