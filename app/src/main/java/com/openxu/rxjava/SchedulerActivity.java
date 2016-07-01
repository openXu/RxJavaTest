package com.openxu.rxjava;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SchedulerActivity extends AppCompatActivity {
    private Context mContext;
    private String TAG;

    @Bind(R.id.btn_1)
    Button btn1;
    @Bind(R.id.btn_2)
    Button btn2;
    @Bind(R.id.btn_3)
    Button btn3;
    @Bind(R.id.btn_4)
    Button btn4;
    @Bind(R.id.btn_5)
    Button btn_5;
    @Bind(R.id.btn_6)
    Button btn_6;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheduler);
        TAG = "SchedulerActivity";
        mContext = this;
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_1, R.id.btn_2, R.id.btn_3, R.id.btn_4, R.id.btn_5, R.id.btn_6})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_1:
                scheduler1();
                break;
            case R.id.btn_2:
                moreSubscribeOn();
                break;
            case R.id.btn_3:
                break;
            case R.id.btn_4:
                break;
            case R.id.btn_5:
                break;
            case R.id.btn_6:
                break;
        }
    }

    private void logThread(Object obj, Thread thread){
        Log.v(TAG, "onNext:"+obj+" -"+Thread.currentThread().getName());
    }

    private void scheduler1(){

     /*   Observable.just(1,2,3)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(integer-> Log.v(TAG, "onNext:"+integer));
*/

        Observable.OnSubscribe onSub = new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                Log.v(TAG, "OnSubscribe -"+Thread.currentThread());
                subscriber.onNext(1);
                subscriber.onCompleted();
            }
        };
        Log.v(TAG, "--------------①-------------");
        Observable.create(onSub)
                .subscribe(integer->logThread(integer, Thread.currentThread()));
        Log.v(TAG, "--------------②-------------");
        Observable.create(onSub)
                .subscribeOn(Schedulers.io())
                .subscribe(integer->logThread(integer, Thread.currentThread()));
        Log.v(TAG, "--------------③-------------");
        Observable.create(onSub)
                .subscribeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.io())
                .subscribeOn(Schedulers.computation())
                .subscribe(integer->logThread(integer, Thread.currentThread()));
        Log.v(TAG, "--------------④-------------");
        Observable.create(onSub)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(integer->logThread(integer, Thread.currentThread()));
        Log.v(TAG, "--------------⑤-------------");
        Observable.create(onSub)
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .subscribe(integer->logThread(integer, Thread.currentThread()));
        Log.v(TAG, "--------------⑥-------------");
        Observable.interval(100, TimeUnit.MILLISECONDS)
                .take(1)
                .subscribe(integer->logThread(integer, Thread.currentThread()));
        /*
         输出：
         --------------①-------------
         OnSubscribe -Thread[main,5,main]
         onNext:1 -Thread[main,5,main]
         --------------②-------------
         OnSubscribe -Thread[RxIoScheduler-2,5,main]
         onNext:1 -Thread[RxIoScheduler-2,5,main]
         --------------③-------------
         OnSubscribe -Thread[RxNewThreadScheduler-1,5,main]
         onNext:1 -Thread[RxNewThreadScheduler-1,5,main]
         --------------④-------------
         OnSubscribe -Thread[RxNewThreadScheduler-2,5,main]
         onNext:1 -Thread[main,5,main]
         --------------⑤-------------
         OnSubscribe -Thread[RxNewThreadScheduler-4,5,main]
         onNext:1 -Thread[RxNewThreadScheduler-3,5,main]
         --------------⑥-------------
         onNext:0 -RxComputationScheduler-3
         */
    }


    //多次subscribeOn的情况，只有第一次起作用
    private void moreSubscribeOn(){
        Observable.OnSubscribe onSub = new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                Log.v(TAG, "OnSubscribe -"+Thread.currentThread());
                subscriber.onNext(1);
                subscriber.onCompleted();
            }
        };
        Observable.create(onSub)
                .subscribeOn(Schedulers.io())         //io线程
                .subscribeOn(Schedulers.newThread())  //无效的
                .subscribeOn(Schedulers.computation())//无效的
                .subscribe(integer->logThread(integer, Thread.currentThread()));
        /*
        输出：
         OnSubscribe -Thread[RxIoScheduler-2,5,main]
         onNext:1 -RxIoScheduler-2
         */
    }


}
