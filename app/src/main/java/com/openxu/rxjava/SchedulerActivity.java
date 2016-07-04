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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheduler);
        TAG = "SchedulerActivity";
        mContext = this;
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_1, R.id.btn_2, R.id.btn_3})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_1:
                scheduler1();
                break;
            case R.id.btn_2:
                moreSubscribeOn();
                break;
            case R.id.btn_3:
                observeOn();
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

        //多次subscribeOn的伪代码
        /*
        ...
        //第3个subscribeOn产生的新线程
        new Thread(){
            @Override
            public void run() {
                Subscriber s1 = new Subscriber();
                //第2个subscribeOn产生的新线程
                new Thread(){
                    @Override
                    public void run() {
                        Subscriber s2 = new Subscriber();
                        //第1个subscribeOn产生的新线程
                        new Thread(){
                            @Override
                            public void run() {
                                Subscriber<T> s3 = new Subscriber<T>(subscriber) {
                                    @Override
                                    public void onNext(T t) {
                                        subscriber.onNext(t);
                                    }
                                    ...
                                };
                                //①. 最后一个新观察者订阅原始Observable
                                原始Observable.subscribe(s3);
                                //②. 原始Observable将在此线程中发射数据

                                //③. 最后一个新的观察者s3接受数据

                                //④. s3收到数据后，直接发送给s2，s2收到数据后传给s1,...最后目标观察者收到数据
                            }
                        }.start();
                    }
                }.start();
            }
        }.start();
        */
    }


    private void observeOn() {
        /*
        Observable.just(100)
                .map(integer -> {return "map1-"+integer;}) //主线程中接受数据
                .observeOn(Schedulers.io())          //①. 切换
                .map(integer -> {return "map2-"+integer;}) //io线程中接受数据，由①决定
                .observeOn(Schedulers.computation()) //②. 切换
                .map(integer -> {return "map3-"+integer;}) //computation线程中接受数据，由②决定
                .observeOn(AndroidSchedulers.mainThread()) //③. 切换
                .map(integer -> {return "map4-"+integer;}) //主线程中接受数据，由③决定
                .subscribe(str -> logThread(str, Thread.currentThread())); //主线程中接受数据，由③决定
           */
        /*
         输出：
         onNext:map4-map3-map2-map1-100 -main
         */


        Observable.just(100)
                .subscribeOn(Schedulers.computation())     //Computation线程中发射数据
                .map(integer -> {return "map1-"+integer;}) //Computation线程中接受数据
                .observeOn(Schedulers.io())          //②. 切换
                .map(integer -> {return "map2-"+integer;}) //io线程中接受数据，由②决定
                .observeOn(Schedulers.newThread())   //③. 切换
                .map(integer -> {return "map3-"+integer;}) //newThread线程中接受数据，由③决定
                .observeOn(AndroidSchedulers.mainThread()) //④. 切换
                .delay(1000, TimeUnit.MILLISECONDS)        //主线程中接受数据，由④决定
                .subscribe(str -> logThread(str, Thread.currentThread()));  //Computation线程中接受数据，由④决定

        /*
            说明：最后目标观察者将在Computation线程中接受数据，这取决于delay操作符，
            delay操作符是在Computation线程中执行的，执行完后就会将数据发送给目标观察者。
            而他上面的observeOn将决定于delay产生的代理观察者在主线程中接受数据
         */


        /*
         输出：
         onNext:map3-map2-map1-100 -RxComputationScheduler-3
         */

    }




}
