package com.ycbjie.yclivedatabus.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * <pre>
 *     @author yangchong
 *     blog  : https://github.com/yangchong211
 *     time  : 2019/7/20
 *     desc  : fragment的父类
 *     revise: 注意，该类具有懒加载
 * </pre>
 */
public abstract class BaseFragment extends BaseLazyFragment {


    private View view;
    protected Activity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if(view==null){
            view = inflater.inflate(getViewId(), container , false);
            view.setClickable(true);
        }
        return view;
    }

    protected abstract int getViewId();

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initListener();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * 初始化View的代码写在这个方法中
     * @param view              view
     */
    public abstract void initView(View view);

    /**
     * 初始化监听器的代码写在这个方法中
     */
    public abstract void initListener();

}
