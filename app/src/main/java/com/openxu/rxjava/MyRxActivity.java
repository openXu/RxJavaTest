package com.openxu.rxjava;

import android.os.Bundle;
import android.util.Log;


import com.openxu.rxjava.R;
import com.openxu.rxjava.databinding.ActivityMainBinding;
import com.openxu.rxjava.myrx.Emitter;
import com.openxu.rxjava.myrx.Observable;
import com.openxu.rxjava.myrx.ObservableOnSubscribe;
import com.openxu.rxjava.myrx.Observer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

/**
 * Author: openXu
 * Time: 2020/10/16 16:17
 * class: MyRxActivity
 * Description:
 */
public class MyRxActivity extends AppCompatActivity {

    private String TAG = "MyRxActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Activity中通过DataBindingUtil.setContentView()设置布局获取绑定类实例对象(代替原来的setContentView(getContentView(id));)
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_myrx);

        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(Emitter<String> emitter) {
                emitter.onNext("第一条事件");
            }
        }).subscribe(new Observer<String>() {
            @Override
            public void onNext(String msg) {
                Log.v(TAG, "观察者收到消息："+msg);
            }
            @Override
            public void onSubscribe() {

            }
            @Override
            public void onError(Throwable e) {

            }
            @Override
            public void onCompleted() {

            }
        });

    }

}