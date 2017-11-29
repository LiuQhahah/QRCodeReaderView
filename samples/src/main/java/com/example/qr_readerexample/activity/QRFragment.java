package com.example.qr_readerexample.activity;


import android.Manifest;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;
import com.example.qr_readerexample.PointsOverlayView;
import com.example.qr_readerexample.R;
import com.example.qr_readerexample.base.BaseFragment;

import butterknife.BindView;

import static com.example.qr_readerexample.R.id.points_overlay_view;
import static com.example.qr_readerexample.R.id.qrdecoderview;
import static com.example.qr_readerexample.activity.MainActivity.myDb;

/**
 * 文件描述：
 * Created by Administrator on 2017/10/11.
 */

public class QRFragment extends BaseFragment implements
        ActivityCompat.OnRequestPermissionsResultCallback, QRCodeReaderView.OnQRCodeReadListener {

    @BindView(R.id.tv_name)
    TextView tv_title;


    //最大值 ,最小值 ,平均值
    @BindView(R.id.tv_max_value)
    TextView tvMaxValue;
    @BindView(R.id.tv_min_value)
    TextView tvMinValue;
    @BindView(R.id.tv_aver_value)
    TextView tvAverValue;
    @BindView(R.id.tv_new_value)
    TextView tvNewValue;


    private static final int MY_PERMISSION_REQUEST_CAMERA = 0;

    @BindView(R.id.re_qr)
    ViewGroup reQR;


    @BindView(R.id.device_parameter)
    TextView tv_device_parameter;

    @BindView(qrdecoderview)
    QRCodeReaderView qrCodeReaderView;
    @BindView(points_overlay_view)
    PointsOverlayView pointsOverlayView;


    @Override
    protected int getContentViewID() {
        return R.layout.content_qr;
    }

    @Override
    protected void initViewsAndEvents(View rootView, Bundle savedInstanceState) {

        initQRCodeReaderView();

      /*  if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            tv_title.setText("现场扫描");
            initQRCodeReaderView();
        } else {

            *//*ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSION_REQUEST_CAMERA);*//*
            //requestCameraPermission();
        }*/


    }




    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA)) {
            Snackbar.make(reQR, "Camera access is required to display the camera preview.",
                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{
                            Manifest.permission.CAMERA
                    }, MY_PERMISSION_REQUEST_CAMERA);
                }
            }).show();
        } else {
            Snackbar.make(reQR, "Permission is not available. Requesting camera permission.",
                    Snackbar.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.CAMERA
            }, MY_PERMISSION_REQUEST_CAMERA);
        }
    }


    public void initQRCodeReaderView() {


        qrCodeReaderView.setVisibility(View.VISIBLE);
        qrCodeReaderView.setAutofocusInterval(2000L);
        qrCodeReaderView.setOnQRCodeReadListener(this);
        qrCodeReaderView.setBackCamera();
        qrCodeReaderView.startCamera();
    }

    @Override
    public void onQRCodeRead(String devicename, PointF[] points) {


        //数据表格实现设备与参数信息
        tv_device_parameter.setText(devicename);


        tvMaxValue.setText("最大值：" + myDb.getMaxValue(devicename));
        tvMinValue.setText("最小值：" + myDb.getMinValue(devicename));
        tvAverValue.setText("平均值：" + myDb.getAverValue(devicename));
        tvNewValue.setText("最新值:" + myDb.getNewestValue(devicename));

        pointsOverlayView.setPoints(points);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPause() {
        qrCodeReaderView.stopCamera();
        super.onPause();
    }

    @Override
    public void onResume() {
        qrCodeReaderView.startCamera();
        super.onResume();
    }
}
