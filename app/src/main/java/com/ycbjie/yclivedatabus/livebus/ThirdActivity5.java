package com.ycbjie.yclivedatabus.livebus;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ycbjie.yclivedatabus.R;
import com.ycbjie.yclivedatabus.base.BasePagerAdapter;
import com.ycbjie.yclivedatabus.eventbus.SendEventActivity;
import com.ycbjie.yclivedatabus.eventbus.TestEventFragment;

import java.util.ArrayList;

public class ThirdActivity5 extends AppCompatActivity {


    private TabLayout mTab;
    private ViewPager mVp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_layout);
        initView();
        initTabLayout();
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ThirdActivity5.this,SendLiveBusActivity.class));
            }
        });
    }


    private void initView() {
        mTab = (TabLayout) findViewById(R.id.tab);
        mVp = (ViewPager) findViewById(R.id.vp);
    }


    private void initTabLayout() {
        ArrayList<String> mTitleList = new ArrayList<>();
        ArrayList<Fragment> mFragments = new ArrayList<>();
        mTitleList.add("逗比");
        mTitleList.add("文学");
        mTitleList.add("文化");
        mTitleList.add("生活");
        mTitleList.add("励志");
        mTitleList.add("小杨");
        mTitleList.add("故事");
        mTitleList.add("逗比");
        mTitleList.add("爱情");
        mTitleList.add("傻子");
        mFragments.add(new TestLiveBusFragment());
        mFragments.add(new TestLiveBusFragment());
        mFragments.add(new TestLiveBusFragment());
        mFragments.add(new TestLiveBusFragment());
        mFragments.add(new TestLiveBusFragment());
        mFragments.add(new TestLiveBusFragment());
        mFragments.add(new TestLiveBusFragment());
        mFragments.add(new TestLiveBusFragment());
        mFragments.add(new TestLiveBusFragment());
        mFragments.add(new TestLiveBusFragment());

        /*
         * 注意使用的是：getChildFragmentManager，
         * 这样setOffscreenPageLimit()就可以添加上，保留相邻2个实例，切换时不会卡
         * 但会内存溢出，在显示时加载数据
         */
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        BasePagerAdapter myAdapter = new BasePagerAdapter(supportFragmentManager,
                mFragments, mTitleList);
        mVp.setAdapter(myAdapter);
        // 左右预加载页面的个数
        mVp.setOffscreenPageLimit(mFragments.size());
        mTab.setTabMode(TabLayout.MODE_SCROLLABLE);
        mTab.setupWithViewPager(mVp);
        myAdapter.notifyDataSetChanged();
    }


}
