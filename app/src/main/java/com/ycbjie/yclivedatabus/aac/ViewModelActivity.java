package com.ycbjie.yclivedatabus.aac;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.OnLifecycleEvent;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.ycbjie.yclivedatabus.R;
import com.yccx.livebuslib.utils.BusLogUtils;

import java.util.ArrayList;
import java.util.List;

public class ViewModelActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BusLogUtils.d("------AppCompatActivity onCreate() called");
        testLifecycle();
    }

    @Override
    protected void onStop() {
        super.onStop();
        BusLogUtils.d("------AppCompatActivity onStop() called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        BusLogUtils.d("------AppCompatActivity onResume() called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BusLogUtils.d("------AppCompatActivity onDestroy() called");
    }

    private void testLifecycle() {
        MyViewModel model = ViewModelProviders.of(this).get(MyViewModel.class);
        model.getUsers().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(@Nullable List<User> users) {
                if (users!=null && users.size()>0){
                    int age = users.get(0).getAge();
                    String name = users.get(0).getName();
                    BusLogUtils.d("--ViewModel-------------onChanged------"+age + "------"+name);
                }
            }
        });
    }

    public class MyViewModel extends ViewModel {
        private MutableLiveData<List<User>> users;
        public LiveData<List<User>> getUsers() {
            if (users == null) {
                users = new MutableLiveData<List<User>>();
                loadUsers();
            }
            return users;
        }

        private void loadUsers() {
            // Do an asynchronous operation to fetch users.
            User user = new User();
            user.setAge(26);
            user.setName("yangchong");
            List<User> list = new ArrayList<>();
            list.add(user);
            users.setValue(list);
        }
    }

    private class User{
        private int age;
        private String name;

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

}
