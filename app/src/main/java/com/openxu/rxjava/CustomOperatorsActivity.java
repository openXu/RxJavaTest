package com.openxu.rxjava;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * RxJava －－（三.操作符原理&自定义操作符）
 *
 * create()
 * lift()
 * compose()
 */
public class CustomOperatorsActivity extends AppCompatActivity {
    private Context mContext;
    private String TAG = "CustomOperatorsActivity";

    @Bind(R.id.btn_1)
    Button btn1;
    @Bind(R.id.btn_2)
    Button btn2;
    @Bind(R.id.btn_3)
    Button btn3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_operators);
        mContext = this;
        ButterKnife.bind(this);
    }


    @OnClick({R.id.btn_1, R.id.btn_2, R.id.btn_3})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_1:
                CustomCreate();
                break;
            case R.id.btn_2:
                CustomList();
                break;
            case R.id.btn_3:
                CustomCompose();
                break;
        }
    }

    private void CustomCreate(){
        //每隔指定的时间间隔发射一个整数数组中的数据，而不是整数序列
        int[] datas = new int[]{2,5,3,1,7,4,8,3,2};
        int sleepTime = 1000;
        Observable<Integer> obs = Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                try{
                    for(int data : datas){
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onNext(data);
                        }else{
                            return;
                        }
                        Thread.sleep(sleepTime);
                    }
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onCompleted();
                    }
                }catch (Exception e){
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onError(e);
                    }
                }
            }
        });
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        Log.v(TAG, "start time:" + sdf.format(new Date()));
        obs.subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Subscriber<Integer>() {
                @Override
                public void onCompleted() {
                    Log.v(TAG, "onCompleted");

                }
                @Override
                public void onError(Throwable e) {
                    Log.v(TAG, "onError:"+e.getMessage());
                }
                @Override
                public void onNext(Integer integer) {
                    Log.v(TAG, "onNext:"+integer+" time:"+sdf.format(new Date()));
                }
            });
        /*
        输出：
        start time:16:16:49
        onNext:2 time:16:16:49
        onNext:5 time:16:16:50
        onNext:3 time:16:16:51
        onNext:1 time:16:16:52
        onNext:7 time:16:16:53
        onNext:4 time:16:16:54
        onNext:8 time:16:16:55
        onNext:3 time:16:16:56
        onNext:2 time:16:16:57
        onCompleted
         */

    }


    private void CustomList(){
        //使用map操作符，将int转换成String
        Observable.just(1,2,3)
                .map(new Func1<Integer, String>() {
                    @Override
                    public String call(Integer integer) {
                        return "map:"+integer;
                    }
                })
                .subscribe(str->Log.v(TAG, "onNext:"+str));



        //自定义“map”操作符
        Observable.just(1,2,3)
                .lift(new Observable.Operator<String, Integer>() {
                    @Override
                    public Subscriber<? super Integer> call(final Subscriber<? super String> subscriber) {
                        return new Subscriber<Integer>() {
                            @Override
                            public void onNext(Integer integer) {
                                if(!subscriber.isUnsubscribed()) {
                                    subscriber.onNext("CustomMap：" + integer);
                                }
                            }
                            @Override
                            public void onCompleted() {
                                if(!subscriber.isUnsubscribed()) {
                                    subscriber.onCompleted();
                                }
                            }
                            @Override
                            public void onError(Throwable e) {
                                if(!subscriber.isUnsubscribed()) {
                                    subscriber.onError(e);
                                }
                            }
                        };
                    }
                }).subscribe(str->Log.v(TAG, "onNext:"+str));;
        /*
        输出：
        onNext:map:1
        onNext:map:2
        onNext:map:3
        onNext:CustomMap：1
        onNext:CustomMap：2
        onNext:CustomMap：3
         */

    }


    private void CustomCompose(){
        //形式1
        /*observable1
                .lift(new Observable.Operator)
                .map(new Func1)
                .subscribe(subscriber1);
        observable2
                .lift(new Observable.Operator)
                .map(new Func1)
                .subscribe(subscriber2);
        observable3
                .lift(new Observable.Operator)
                .map(new Func1)
                .subscribe(subscriber3);
        observable4
                .lift(new Observable.Operator)
                .map(new Func1)
                .subscribe(subscriber4);*/

        //形式2
        /*transAll(observable1).subscribe(subscriber1);
        transAll(observable2).subscribe(subscriber2);
        transAll(observable3).subscribe(subscriber3);
        transAll(observable4).subscribe(subscriber4);*/

        //形式3
        /*Observable.Transformer transAll = new AllTransformer();
        observable1.compose(transAll).subscribe(subscriber1);
        observable2.compose(transAll).subscribe(subscriber2);
        observable3.compose(transAll).subscribe(subscriber3);
        observable4.compose(transAll).subscribe(subscriber4);*/

        Observable.Transformer mapAll = new Observable.Transformer<Integer, String>(){
            @Override
            public Observable<String> call(Observable<Integer> observable) {
                return observable.map(integer->{return "map1-"+integer;})
                        .map(str->{return "map2-"+str;})
                        .map(str->{return "map3-"+str;})
                        .map(str->{return "map4-"+str;});
            }
        };
        Observable.just(1,2).compose(mapAll).subscribe(str->Log.v(TAG, ""+str));
        Observable.just(3,4).compose(mapAll).subscribe(str->Log.v(TAG, ""+str));
        Observable.just(5,6).compose(mapAll).subscribe(str->Log.v(TAG, ""+str));
        Observable.just(7,8).compose(mapAll).subscribe(str->Log.v(TAG, ""+str));
        /*
        输出：
        map4-map3-map2-map1-1
        map4-map3-map2-map1-2
        map4-map3-map2-map1-3
        map4-map3-map2-map1-4
        map4-map3-map2-map1-5
        map4-map3-map2-map1-6
        map4-map3-map2-map1-7
        map4-map3-map2-map1-8
         */

    }
    /*private Observable transAll(Observable observable) {
        return observable
                .lift(new Observable.Operator)
                .map(new Func1);
    }*/
    /*public class AllTransformer implements Observable.Transformer<Integer, String> {
        @Override
        public Observable<String> call(Observable<Integer> observable) {
            return observable
                    .lift(new Observable.Operator)
                    .map(new Func1);
        }
    }*/


}
