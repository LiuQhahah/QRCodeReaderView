package com.example.qr_readerexample.activity;


import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.qr_readerexample.R;
import com.example.qr_readerexample.base.BaseFragment;
import com.example.qr_readerexample.dialog.CommSigleSelectDialog;
import com.example.qr_readerexample.dialog.SelectDateDialog;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

import static android.content.ContentValues.TAG;
import static com.example.qr_readerexample.activity.MainActivity.myDb;

/**
 * 文件描述：历史数据描述
 * Created by Administrator on 2017/10/11.
 */

public class HistoryFragment extends BaseFragment {


    //设置选择的时间，设备名以及参数号
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_device)
    TextView tvDevice;
    @BindView(R.id.tv_parameter)
    TextView tvParameter;



    @BindView(R.id.device_parameter)
    TextView tv_device_parameter;

    //下拉刷新控件
    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout smartRefreshLayout;


    //最大值 ,最小值 ,平均值
    @BindView(R.id.tv_max_value)
    TextView tvMaxValue;
    @BindView(R.id.tv_min_value)
    TextView tvMinValue;
    @BindView(R.id.tv_aver_value)
    TextView tvAverValue;
    @BindView(R.id.tv_new_value)
    TextView tvNewValue;


    @BindView(R.id.tv_line_name)
    @Nullable
    TextView tvLineTitle;

    @BindView(R.id.lineView)
    @Nullable
    LineChartView lineChartView;

    @BindView(R.id.tv_name)
    TextView tvName;

    @BindView(R.id.re_history)
    RelativeLayout relativeLayout;


    public static Fragment newInstance(String status) {
        HistoryFragment fragment = new HistoryFragment();
        Bundle args = new Bundle();
        args.putString("status", status);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getContentViewID() {
        //返回对应的layout
        return R.layout.content_history_data;
    }

    @Override
    protected void initViewsAndEvents(View rootView, Bundle savedInstanceState) {

        tvName.setText("历史数据");



        smartRefreshLayout.setEnableLoadmore(false);
        smartRefreshLayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {

            }

            @Override
            public void onRefresh(final RefreshLayout refreshlayout) {
                refreshlayout.getLayout().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (tvTime.getText() != null && tvDevice.getText() != null && tvParameter.getText() != null) {

                            String device_parameter = tvDevice.getText()+""+tvParameter.getText();
                            tvLineTitle.setText("近50个数据折线图 " + " 设备：" + tvDevice.getText() + "， 参数：" +
                                    tvParameter.getText());

                            //数据表格实现设备与参数信息
                            tv_device_parameter.setText("设备："+tvDevice.getText()+",参数："+tvParameter.getText());


                            tvMaxValue.setText("最大值："+myDb.getMaxValue(device_parameter));
                            tvMinValue.setText("最小值："+myDb.getMinValue(device_parameter));
                            tvAverValue.setText("平均值："+myDb.getAverValue(device_parameter));
                            tvNewValue.setText("最新值:"+myDb.getNewestValue(device_parameter));
                            drawLine();

                        }
                        refreshlayout.finishRefresh();
                    }
                }, 1000);
            }
        });


    }

    @OnClick({R.id.tv_time, R.id.tv_device, R.id.tv_parameter})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_time:
                selectDate();
                break;
            case R.id.tv_device:
                selectSortDialog(tvDevice, getResources().getStringArray(R.array.device_selector));
                break;
            case R.id.tv_parameter:
                selectSortDialog(tvParameter, getResources().getStringArray(R.array.data_parameter_selector));

                break;
        }
    }

    private void drawLine() {
        //存储时间轴
        String[] lineLabels = new String[50];
        int numberOfPoints = lineLabels.length;
        ValueShape shape = ValueShape.CIRCLE;
        int[] randomNumbersTab = new int[numberOfPoints];

        //设置数据的数值信息
        Cursor res = myDb.getOneHistoryValue(tvDevice.getText() + "" + tvParameter.getText());
        int j = 0;
        while (res.moveToNext()) {
            randomNumbersTab[j] = Integer.valueOf(res.getString(0));
            Log.i(TAG, "getString ()" + res.getString(0));
            j++;
        }

        List<Line> lines = new ArrayList<Line>();
        List<AxisValue> axisValues = new ArrayList<AxisValue>();

        List<PointValue> values = new ArrayList<PointValue>();
        for (int i = 0; i < numberOfPoints; ++i) {
            values.add(new PointValue(i, randomNumbersTab[i]));

            Log.i(TAG,"randomNumbersTab:"+randomNumbersTab[i]);
            Line line = new Line(values);
            line.setColor(R.color.WhiteAndGreen);
            line.setShape(shape);
            line.setPointRadius(3);
            line.setStrokeWidth(1);
            line.setCubic(false);
            line.setFilled(false);
            line.setHasLabels(false);
            line.setHasLabelsOnlyForSelected(true);
            line.setHasLines(true);
            line.setHasPoints(true);
            lines.add(line);

        }


        LineChartData data = new LineChartData(lines);


        Cursor oneHistoryTime = myDb.getOneHistoryTime(tvDevice.getText() + "" + tvParameter.getText());
        int ii = 0;
        while (oneHistoryTime.moveToNext()) {
            String time = oneHistoryTime.getString(0);
            String a[] = time.split(" ");
            //将a[1]时分秒传给横坐标，a[0]位年月日
            lineLabels[ii] = a[1];
            tvTime.setText(a[0]);
            Log.i(TAG, "date [" + ii + "]:" + lineLabels[ii]);
            axisValues.add(new AxisValue(ii).setLabel(lineLabels[ii]));
            ii++;
        }

        Axis axisX = new Axis(axisValues).setMaxLabelChars(5);
        axisX.setTextColor(getResources().getColor(R.color.color_969696))
                .setTextSize(10).setLineColor(getResources().getColor(R.color.color_e6e6e6))
        ;
        data.setAxisXBottom(axisX);
        Axis axisY = new Axis().setHasLines(true).setHasSeparationLine(false).setMaxLabelChars(3);
        axisY.setTextColor(getResources().getColor(R.color.color_969696));
        axisY.setTextSize(10);
        data.setAxisYLeft(axisY);
        data.setBaseValue(Float.NEGATIVE_INFINITY);


        lineChartView.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        lineChartView.setValueSelectionEnabled(true);//点击折线点可以显示label
        lineChartView.setLineChartData(data);
        // Reset viewport height range to (0,100)
        lineChartView.setViewportCalculationEnabled(false);
        //让布局能够水平滑动要设置setCurrentViewport比setMaximumViewport小
        final Viewport v = new Viewport(lineChartView.getMaximumViewport());
        v.bottom = 0;
        v.top = 105;
        v.left = 0;
        v.right = numberOfPoints - 1 + 0.5f;
        lineChartView.setMaximumViewport(v);
        v.left = 0;
        v.right = Math.min(6, numberOfPoints - 1 + 0.5f);
        lineChartView.setCurrentViewport(v);
    }


    private void selectSortDialog(final TextView textView, String[] strs) {
        CommSigleSelectDialog commSigleSelectDialog = new CommSigleSelectDialog(getActivity());
        commSigleSelectDialog.setValue(strs);
        commSigleSelectDialog.setShowCount(3);
        commSigleSelectDialog.setWrap(true);
        commSigleSelectDialog.setOnSelectListener(new CommSigleSelectDialog.OnSelectListener() {
            @Override
            public void onSelect(String str, int value) {
                textView.setText(str);

            }
        });
        commSigleSelectDialog.show();
    }

    private void selectDate() {
        SelectDateDialog selectDateDialog = new SelectDateDialog(getActivity());
        selectDateDialog.setOnSelectedDateListener(new SelectDateDialog.OnSelectedDateListener() {
            @Override
            public void selectedDate(int year, int month, int day) {
                String mon, da, birthday;
                mon = month < 10 ? "0" + month : String.valueOf(month);
                da = day < 10 ? "0" + day : String.valueOf(day);
                birthday = year + "-" + mon + "-" + da;
                tvTime.setText(birthday);


            }
        });
        selectDateDialog.show();
    }


}
