package com.example.qr_readerexample.activity;


import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.qr_readerexample.R;
import com.example.qr_readerexample.base.BaseFragment;
import com.example.qr_readerexample.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.LineChartView;

import static android.content.ContentValues.TAG;
import static com.example.qr_readerexample.activity.MainActivity.myDb;

/**
 * 文件描述：
 * Created by Administrator on 2017/10/11.
 */

public class RealDataFragment extends BaseFragment {

    @BindView(R.id.chart)
    ColumnChartView columnChartView;
    @BindView(R.id.lineView)
    LineChartView lineChartView;

    @BindView(R.id.scroll_view)
    ScrollView scrollView;

    @BindView(R.id.text)
    TextView tv_realdata;

    String[] chartLabels = {"A0", "A1"};
    String[] chartUnits = {" ", "%"};

    @Override
    protected int getContentViewID() {
        return R.layout.content_real_data;
    }

    @Override
    protected void initViewsAndEvents(View rootView, Bundle savedInstanceState) {

        CommonUtils.solveScrollConflict(lineChartView, scrollView);
        drawLine();
        drawChart();
        viewdata();
    }

    private void viewdata() {
        for (int i = 0; i < chartLabels.length; i++) {
            Cursor res = myDb.getRealData(chartLabels[i]);

            while (res.moveToNext()){
                tv_realdata.append( "  "+chartLabels[i]+" : "+res.getString(0)+ chartUnits[i]+"  ");

            }

        }
    }

    private void drawChart() {



        int[] chartColors = new int[]{getResources().getColor(R.color.color_FE5E63), getResources().getColor(R.color.color_6CABFA)};

        int numColumns = chartLabels.length;
        //columnChartView.setZoomEnabled(false);

        // Column can have many subcolumns, here by default I use 1 subcolumn in each of 8 columns.
        List<Column> columns = new ArrayList<Column>();
        List<SubcolumnValue> values;
        List<AxisValue> axisValues = new ArrayList<AxisValue>();
        for (int i = 0; i < numColumns; ++i) {
            values = new ArrayList<SubcolumnValue>();
            //int height = (int) (Math.random() * 50) + 5;
            float aver = myDb.aver(chartLabels[i]);
            values.add(new SubcolumnValue(aver, chartColors[i]).setLabel(aver +
                    chartUnits[i]));

            Column column = new Column(values);
            column.setHasLabels(true);
            // column.setHasLabelsOnlyForSelected(hasLabelForSelected);
            columns.add(column);
            axisValues.add(new AxisValue(i).setLabel(chartLabels[i]));
        }


        ColumnChartData data = new ColumnChartData(columns);

        // value.
        Axis axisx = new Axis(axisValues);
        axisx.setTextColor(getResources().getColor(R.color.color_323232));
        axisx.setTextSize(13);
        axisx.setHasLines(false);
        axisx.setHasSeparationLine(false);
        data.setAxisXBottom(axisx);
        data.setFillRatio(0.5f);
//        Axis axisY = new Axis().setHasLines(true);
//        data.setAxisYLeft(axisY);
        //data.setAxisXBottom(null);
        columnChartView.setInteractive(false);
        columnChartView.setColumnChartData(data);
        columnChartView.setViewportCalculationEnabled(false);
        final Viewport v = new Viewport(columnChartView.getMaximumViewport());
        v.bottom = 0;
        v.top = 65;
        v.left = -0.5f;
        v.right = numColumns - 1 + 0.5f;
        columnChartView.setMaximumViewport(v);
        columnChartView.setCurrentViewport(v);
    }


    private void drawLine() {
        String[] lineLabels = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0"};
        int[] chartColors = new int[]{getResources().getColor(R.color.color_FE5E63), getResources().getColor(R.color.color_6CABFA)};
        int maxNumberOfLines = 2;
        int numberOfPoints = lineLabels.length;
        ValueShape shape = ValueShape.CIRCLE;
        int[][] randomNumbersTab = new int[maxNumberOfLines][numberOfPoints];

        //设置数据的数值信息
        for (int i = 0; i < maxNumberOfLines; i++) {

            Cursor res = myDb.getValue(chartLabels[i], numberOfPoints);
            int j = 0;
            while (res.moveToNext()) {
                randomNumbersTab[i][j] = Integer.valueOf(res.getString(0));
                Log.i(TAG, "res.getCount():" + res.getCount() + "   numberOfPoints:" + numberOfPoints + " getString ()" + res.getString(0));
                j++;
            }
            //randomNumbersTab[i][j] = (float) Math.random() * 100f;

        }


        List<Line> lines = new ArrayList<Line>();
        List<AxisValue> axisValues = new ArrayList<AxisValue>();
        for (int i = 0; i < maxNumberOfLines; ++i) {
            List<PointValue> values = new ArrayList<PointValue>();
            for (int j = 0; j < numberOfPoints; ++j) {
                values.add(new PointValue(j, randomNumbersTab[i][j]));
            }
            Line line = new Line(values);
            line.setColor(chartColors[i]);
            line.setShape(shape);
            line.setPointRadius(3);
            line.setStrokeWidth(1);
            line.setCubic(false);
            line.setFilled(false);
            line.setHasLabels(false);
            line.setHasLabelsOnlyForSelected(true);
            line.setHasLines(true);
            line.setHasPoints(true);
            //line.setPointColor(R.color.transparent);
            //line.setHasGradientToTransparent(true);
//            if (pointsHaveDifferentColor){
//                line.setPointColor(ChartUtils.COLORS[(i + 1) % ChartUtils.COLORS.length]);
//            }
            lines.add(line);

        }


        LineChartData data = new LineChartData(lines);

        for (int i = 0; i < chartLabels.length; i++) {
            Cursor res = myDb.getTime(chartLabels[i], lineLabels.length);
            int j = 0;
            while (res.moveToNext()) {
                String time = res.getString(0);
                String a[] = time.split(" ");
                //将a[1]时分秒传给横坐标，a[0]位年月日
                lineLabels[j] = a[1];
                Log.i(TAG, "date [" + j + "]:" + lineLabels[j]);
                axisValues.add(new AxisValue(j).setLabel(lineLabels[j]));
                j++;
            }
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


        lineChartView.setZoomEnabled(false);
        lineChartView.setScrollEnabled(true);
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


}
