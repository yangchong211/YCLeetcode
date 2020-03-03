package com.ycbjie.yclivedatabus.livebus;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ycbjie.yclivedatabus.R;
import com.ycbjie.yclivedatabus.base.BaseFragment;
import com.ycbjie.yclivedatabus.base.MainAdapter;
import com.ycbjie.yclivedatabus.constant.Constant;
import com.yccx.livebuslib.event.LiveDataBus;

import java.util.ArrayList;
import java.util.List;

public class TestLiveBusFragment extends BaseFragment {

    private List<String> lists = new ArrayList<>();
    private MainAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("接收消息--TestLiveBusFragment------onCreate----");
        LiveDataBus.get()
                .with(Constant.LIVE_BUS1, String.class)
                .observe(this, new Observer<String>() {
                    @Override
                    public void onChanged(@Nullable String s) {
                        System.out.println("接收消息--TestLiveBusFragment------yc_bus----"+s);
                        //BusLogUtils.d("接收消息--TestLiveBusFragment------yc_bus----"+s);
                    }
                });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //System.out.println("接收消息--TestLiveBusFragment------onViewCreated----");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //System.out.println("接收消息--TestLiveBusFragment------onActivityCreated----");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    protected int getViewId() {
        return R.layout.base_recycler_view;
    }

    @Override
    public void initView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        adapter = new MainAdapter(lists, activity);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void initListener() {

    }

    @Override
    protected void loadData() {
        lists.clear();
        for(int a=0 ; a<50 ; a++){
            lists.add("这是第"+a+"条数据");
        }
        adapter.notifyDataSetChanged();
    }

}
