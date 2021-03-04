package com.zwwl.moduleb;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;


import com.yc.api.RouteImpl;
import com.zwwl.moduleinterface.IShowDialogManager;

@RouteImpl(IShowDialogManager.class)
public class ShowDialogImpl implements IShowDialogManager {
    @Override
    public void showDialog(Context context, final CallBack callBack) {
        new AlertDialog.Builder(context)
                .setMessage("这个是一个弹窗")
                .setTitle("标题")
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        callBack.select(false);
                    }
                })
                .create()
                .show();
    }
}
