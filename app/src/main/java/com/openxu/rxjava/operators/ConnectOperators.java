package com.openxu.rxjava.operators;

import android.util.Log;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.observables.ConnectableObservable;


/**
 *  author : openXu
 * created time : 16/6/6 下午7:34
 * blog : http://blog.csdn.net/xmxkf
 * github : http://blog.csdn.net/xmxkf
 * class name : ConnectOperators
 *
 * discription :连接操作
 *
 * 这一节解释ConnectableObservable 和它的子类以及它们的操作符：

 ConnectableObservable.connect( ) — 指示一个可连接的Observable开始发射数据
 Observable.publish( ) — 将一个Observable转换为一个可连接的Observable
 Observable.replay( ) — 确保所有的订阅者看到相同的数据序列，即使它们在Observable开始发射数据之后才订阅
 ConnectableObservable.refCount( ) — 让一个可连接的Observable表现得像一个普通的Observable

 一个可连接的Observable与普通的Observable差不多，除了这一点：
 可连接的Observable在被订阅时并不开始发射数据，只有在它的connect()被调用时才开始。
 用这种方法，你可以等所有的潜在订阅者都订阅了这个Observable之后才开始发射数据。
 */
 public class ConnectOperators extends OperatorsBase {

    /**
     *  Publish 操作符将普通的Observable转换为可连接的
     */
    private void op_Publish(TextView textView){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

        Observable<Long> obs = Observable.interval(1, TimeUnit.SECONDS).take(5);
        //使用publish操作符将普通Observable转换为可连接的Observable
        ConnectableObservable<Long> connectableObservable = obs.publish();
        //第一个订阅者订阅，不会开始发射数据
        connectableObservable.subscribe(new Subscriber<Long>() {
            @Override
            public void onCompleted() {
                Log.v(TAG, "1.onCompleted");
            }
            @Override
            public void onError(Throwable e) {
                Log.v(TAG, "1.onError");
            }
            @Override
            public void onNext(Long along) {
                Log.v(TAG, "1.onNext:"+along+"->time:"+ sdf.format(new Date()));
            }
        });
        //开始发射数据
        Log.v(TAG, "start time:" + sdf.format(new Date()));
        connectableObservable.connect();
        //第二个订阅者延迟2s订阅，这将导致丢失前面2s内发射的数据
        connectableObservable
                .delaySubscription(2, TimeUnit.SECONDS)
                .subscribe(new Subscriber<Long>() {
            @Override
            public void onCompleted() {
                Log.v(TAG, "2.onCompleted");
            }
            @Override
            public void onError(Throwable e) {
                Log.v(TAG, "2.onError");
            }
            @Override
            public void onNext(Long along) {
                Log.v(TAG, "2.onNext:"+along+"->time:"+ sdf.format(new Date()));
            }
        });

        /*
        输出：
        start time:23:01:30
        1.onNext:0->time:23:01:31
        1.onNext:1->time:23:01:32
        2.onNext:1->time:23:01:32
        1.onNext:2->time:23:01:33
        2.onNext:2->time:23:01:33
        1.onNext:3->time:23:01:34
        2.onNext:3->time:23:01:34
        1.onNext:4->time:23:01:35
        2.onNext:4->time:23:01:35
        1.onCompleted
        2.onCompleted
         */

    }


    /**
     *  Connect 让一个可连接的Observable开始发射数据给订阅者
     */
    private void op_Connect(TextView textView){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

        Observable<Long> obs = Observable.interval(1, TimeUnit.SECONDS);
        //使用publish操作符将普通Observable转换为可连接的Observable
        ConnectableObservable<Long> connectableObservable = obs.publish();
        //开始发射数据
        Subscription sub = connectableObservable.connect();
        //第二个订阅者延迟2s订阅，这将导致丢失前面2s内发射的数据
        connectableObservable
                .delaySubscription(3, TimeUnit.SECONDS)
                .subscribe(new Subscriber<Long>() {
                    @Override
                    public void onCompleted() {
                        Log.v(TAG, "onCompleted");
                    }
                    @Override
                    public void onError(Throwable e) {
                        Log.v(TAG, "onError");
                    }
                    @Override
                    public void onNext(Long along) {
                        Log.v(TAG, "onNext:"+along+"->time:"+ sdf.format(new Date()));
                    }
                });

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                //6s之后停止发射数据
                sub.unsubscribe();
            }
        },6000);

        /*
        输出：
        onNext:3->time:23:10:49
        onNext:4->time:23:10:50
        onNext:5->time:23:10:51
         */

    }


    /**
     *  RefCount 让一个可连接的Observable行为像普通的Observable
     */
    private void op_RefCount(TextView textView){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

        Observable<Long> obs = Observable.interval(1, TimeUnit.SECONDS).take(4);
        //使用publish操作符将普通Observable转换为可连接的Observable
        ConnectableObservable<Long> connectableObservable = obs.publish();
        //refCount：将ConnectableObservable转化为普通Observable
        Observable obsRefCount = connectableObservable.refCount();

        obs.subscribe(new Subscriber<Long>() {
            @Override
            public void onCompleted() {
                Log.v(TAG, "普通obs1：onCompleted");
            }
            @Override
            public void onError(Throwable e) {
                Log.v(TAG, "普通obs1：onError");
            }
            @Override
            public void onNext(Long along) {
                Log.v(TAG, "普通obs1：onNext:"+along+"->time:"+ sdf.format(new Date()));
            }
        });
        obs.delaySubscription(3, TimeUnit.SECONDS)
                .subscribe(new Subscriber<Long>() {
            @Override
            public void onCompleted() {
                Log.v(TAG, "普通obs2：onCompleted");
            }
            @Override
            public void onError(Throwable e) {
                Log.v(TAG, "普通obs2：onError");
            }
            @Override
            public void onNext(Long along) {
                Log.v(TAG, "普通obs2：onNext:"+along+"->time:"+ sdf.format(new Date()));
            }
        });

        obsRefCount.subscribe(new Subscriber<Long>() {
            @Override
            public void onCompleted() {
                Log.v(TAG, "obsRefCount1：onCompleted");
            }
            @Override
            public void onError(Throwable e) {
                Log.v(TAG, "obsRefCount1：onError");
            }
            @Override
            public void onNext(Long along) {
                Log.v(TAG, "obsRefCount1：onNext:"+along+"->time:"+ sdf.format(new Date()));
            }
        });
        obsRefCount.delaySubscription(3, TimeUnit.SECONDS)
                .subscribe(new Subscriber<Long>() {
                    @Override
                    public void onCompleted() {
                        Log.v(TAG, "obsRefCount2：onCompleted");
                    }
                    @Override
                    public void onError(Throwable e) {
                        Log.v(TAG, "obsRefCount2：onError");
                    }
                    @Override
                    public void onNext(Long along) {
                        Log.v(TAG, "obsRefCount2：onNext:"+along+"->time:"+ sdf.format(new Date()));
                    }
                });

        /*
        输出：
        普通obs1：onNext:0->time:23:28:28
        普通obs1：onNext:1->time:23:28:29
        普通obs1：onNext:2->time:23:28:30
        普通obs1：onNext:3->time:23:28:31
        普通obs1：onCompleted

        普通obs2：onNext:0->time:23:28:31
        普通obs2：onNext:1->time:23:28:32
        普通obs2：onNext:2->time:23:28:33
        普通obs2：onNext:3->time:23:28:34
        普通obs2：onCompleted

        obsRefCount1：onNext:0->time:23:28:28
        obsRefCount1：onNext:1->time:23:28:29
        obsRefCount1：onNext:2->time:23:28:30
        obsRefCount1：onNext:3->time:23:28:31
        obsRefCount1：onCompleted

        obsRefCount2：onNext:3->time:23:28:31
        obsRefCount2：onCompleted
         */
    }

    /**
     *  Replay：
     *  保证所有的观察者收到相同的数据序列，即使它们在Observable开始发射数据之后才订阅
     */
    private void op_Replay(TextView textView){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        Log.v(TAG, "start time:" + sdf.format(new Date()));

        //没有缓存的情况
        ConnectableObservable<Long> obs = Observable.interval(1, TimeUnit.SECONDS)
                .take(5)
                .publish();
        obs.connect();  //开始发射数据
        obs.delaySubscription(3, TimeUnit.SECONDS)
                .subscribe(aLong -> Log.v(TAG, "onNext:"+aLong+"->time:"+ sdf.format(new Date())));


        //缓存一个数据
        ConnectableObservable<Long> obs1 = Observable.interval(1, TimeUnit.SECONDS)
                .take(5)
                .replay(1);   //缓存1个数据
        obs1.connect();  //开始发射数据
        obs1.delaySubscription(3, TimeUnit.SECONDS)
                .subscribe(aLong -> Log.v(TAG,
                        "1.onNext:"+aLong+"->time:"+ sdf.format(new Date())));

        //缓存3s内发射的数据
        ConnectableObservable<Long> obs2 = Observable.interval(1, TimeUnit.SECONDS)
                .take(5)
                .replay(3, TimeUnit.SECONDS);   //缓存3s
        obs2.connect();  //开始发射数据
        obs2.delaySubscription(3, TimeUnit.SECONDS)
                .subscribe(aLong -> Log.v(TAG,
                        "2.onNext:"+aLong+"->time:"+ sdf.format(new Date())));

        /*
        输出：
        start time:14:25:51
        onNext:3->time:14:25:55
        onNext:4->time:14:25:56

        1.onNext:2->time:14:25:54
        1.onNext:3->time:14:25:55
        1.onNext:4->time:14:25:56

        2.onNext:0->time:14:25:54
        2.onNext:1->time:14:25:54
        2.onNext:2->time:14:25:54
        2.onNext:3->time:14:25:55
        2.onNext:4->time:14:25:56
         */

    }




}
