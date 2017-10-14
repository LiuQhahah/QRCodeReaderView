package com.example.qr_readerexample.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.qr_readerexample.R;
import com.example.qr_readerexample.adpter.CommonAdapter;
import com.example.qr_readerexample.adpter.MultiItemTypeAdapter;
import com.example.qr_readerexample.adpter.ViewHolder;
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

/**
 * 文件描述：历史数据描述
 * Created by Administrator on 2017/10/11.
 */

public class HistoryFragment extends BaseFragment {

    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_group)
    TextView tvGroup;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;
    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout smartRefreshLayout;


    private CommonAdapter<String> adapter;
    private List<String> datas=new ArrayList<>();

    public static Fragment newInstance(String status){
        HistoryFragment fragment  = new HistoryFragment();
        Bundle args = new Bundle();
        args.putString("status",status);
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
        datas.add("");
        datas.add("");
        datas.add("");
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter = new CommonAdapter<String>(
                getActivity(),R.layout.item_heat,datas) {
            @Override
            protected void convert(ViewHolder viewHolder, String s, int position) {
                //下拉到最后一个List上，显示分割线？？？
              /*  if (position==datas.size()-1){
                    viewHolder.setVisible(R.id.divider,false);
                }else{
                    viewHolder.setVisible(R.id.divider,true);
                }*/
            }
        });

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
                        refreshlayout.finishRefresh();
                    }
                },1000);
            }
        });

        adapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                startActivity(new Intent(getActivity(),DataTrendActivity.class));
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });

    }
    @OnClick({R.id.tv_time,R.id.tv_group})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.tv_time:
                selectDate();
                break;
            case R.id.tv_group:
                selectSortDialog(getResources().getStringArray(R.array.device_selector));
                break;
        }
    }

    private void selectSortDialog(String[] strs) {
        CommSigleSelectDialog commSigleSelectDialog = new CommSigleSelectDialog(getActivity());
        commSigleSelectDialog.setValue(strs);
        commSigleSelectDialog.setShowCount(3);
        commSigleSelectDialog.setWrap(true);
        commSigleSelectDialog.setOnSelectListener(new CommSigleSelectDialog.OnSelectListener() {
            @Override
            public void onSelect(String str,int value) {
                tvGroup.setText(str);
            }
        });
        commSigleSelectDialog.show();
    }
    private void selectDate() {
        SelectDateDialog selectDateDialog=new SelectDateDialog(getActivity());
        selectDateDialog.setOnSelectedDateListener(new SelectDateDialog.OnSelectedDateListener() {
            @Override
            public void selectedDate(int year, int month, int day) {
                String mon,da,birthday;
                mon = month < 10 ? "0" + month : String.valueOf(month);
                da = day < 10 ? "0" + day : String.valueOf(day);
                birthday = year + "-" + mon + "-" + da;
                tvTime.setText(birthday);


            }
        });
        selectDateDialog.show();
    }



}
