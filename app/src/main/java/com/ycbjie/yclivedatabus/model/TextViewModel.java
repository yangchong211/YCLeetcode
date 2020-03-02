package com.ycbjie.yclivedatabus.model;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

public class TextViewModel extends ViewModel {

    /**
     * LiveData是抽象类，MutableLiveData是具体实现类
     */
    private MutableLiveData<String> mCurrentText;

    public MutableLiveData<String> getCurrentText() {
        if (mCurrentText == null) {
            mCurrentText = new MutableLiveData<>();
        }
        return mCurrentText;
    }

}
