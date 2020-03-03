package com.ycbjie.yclivedatabus.eventbus;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ycbjie.yclivedatabus.R;
import com.ycbjie.yclivedatabus.base.BaseFragment;
import com.ycbjie.yclivedatabus.base.MainAdapter;
import com.ycbjie.yclivedatabus.constant.Constant;
import com.yccx.livebuslib.utils.BusLogUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class TestEventFragment extends BaseFragment {

    private List<String> lists = new ArrayList<>();
    private MainAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BusLogUtils.d("接收消息--TestEventFragment------onCreate----");
        EventBusUtils.register(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //EventBusUtils.register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBusUtils.unregister(this);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainThreadTestEvent(EventMessage event) {
        BusLogUtils.d("接收消息--TestEventFragment---逗比---yc_bus-------------------");
        if (event!=null && event.getFlag()!=null){
            String flag = event.getFlag();
            Object newText = event.getEvent();
            switch (flag){
                case Constant.EVENT_BUS3:
                    BusLogUtils.d("接收消息--TestEventFragment------yc_bus----"+newText);
                    break;
                default:
                    break;
            }
        }
    }

}
