package com.example.qr_readerexample.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.qr_readerexample.App;
import com.example.qr_readerexample.R;
import com.example.qr_readerexample.base.BaseActivity;
import com.example.qr_readerexample.base.BaseFragment;
import com.example.qr_readerexample.utils.StatusBarUtil;
import com.example.qr_readerexample.utils.ToastHelper;

import butterknife.BindString;
import butterknife.BindView;
import db.DataBaseHelper;


public class MainActivity extends BaseActivity {


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



    protected BaseFragment currentFragment;





    @Override
    protected int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initViewsAndEvents(Bundle savedInstanceState) {
        StatusBarUtil.darkMode(this);

        Log.i(TAG,"app_exit "+app_exit);

        endTime = System.currentTimeMillis(); //获取结束时间
        Log.i(TAG, "程序运行时间： " + (endTime - SplashActivity.startTime) + "ms");

        realdataFragment = new RealDataFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.main_content, realdataFragment).commitAllowingStateLoss();
        currentFragment = realdataFragment;
        currentTabIndex = 0;

        initTabs();
        changeFragment(currentTabIndex);
    }


    /**
     * 初始化底部4个导航按钮
     */

    private void initTabs() {
        myDb = new DataBaseHelper(this);

        mTabs = new View[]{tabRealData, tabHisData,tabQr};
        String[] mTabTitles = getResources().getStringArray(R.array.main_tab_titles);
        boolean booleen =mTabs[0].isSelected();
        mTabs[0].setSelected(true);

        for (int i = 0; i < mTabs.length; i++) {
            initTab(i, mTabTitles[i], mTabIcons[i]);
        }

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
                App.getInstance().exit();
                System.exit(0);
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
