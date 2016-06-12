package com.openxu.rxjava.operators;

import android.util.Log;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.functions.Func3;
import rx.schedulers.Schedulers;

/**
 * author : openXu
 * created time : 16/6/6 下午7:34
 * blog : http://blog.csdn.net/xmxkf
 * github : http://blog.csdn.net/xmxkf
 * class name : TransformOperators
 *
 * discription : 结合操作符
 *
 * 这个页面展示的操作符可用于组合多个Observables。

 * startWith( ) — 在数据序列的开头增加一项数据
 * merge( ) — 将多个Observable合并为一个
 * mergeDelayError( ) — 合并多个Observables，让没有错误的Observable都完成后再发射错误通知
 * zip( ) — 使用一个函数组合多个Observable发射的数据集合，然后再发射这个结果
 * and( ), then( ), and when( ) — (rxjava-joins) 通过模式和计划组合多个Observables发射的数据集合
 * combineLatest( ) — 当两个Observables中的任何一个发射了一个数据时，通过一个指定的函数组合每个Observable发射的最新数据（一共两个数据），然后发射这个函数的结果
 * join( ) and groupJoin( ) — 无论何时，如果一个Observable发射了一个数据项，只要在另一个Observable发射的数据项定义的时间窗口内，就将两个Observable发射的数据合并发射
 * switchOnNext( ) — 将一个发射Observables的Observable转换成另一个Observable，后者发射这些Observables最近发射的数据
 *
 * (rxjava-joins) — 表示这个操作符当前是可选的rxjava-joins包的一部分，还没有包含在标准的RxJava操作符集合里
 */
public class JoinOperators extends OperatorsBase {

    private Observable<String> getObservable(String name){
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                if(name.contains("-")){
                    for (int i = 1; i <=3; i++) {
                        Log.v(TAG, name+i);
                        subscriber.onNext(name+i);
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    subscriber.onCompleted();
                }
            }
        }).subscribeOn(Schedulers.newThread());
    }

    /**
     * 当两个Observables中的任何一个发射了数据时，
     * 使用一个函数结合每个Observable发射的最近数据项，
     * 并且基于这个函数的结果发射数据。
     */
    private void op_CombineLatest(TextView textView){
        /*
         * combineLatest(Observable,Observable,Func2):
         * ①.能接受2~9个Observable作为参数，或者单个Observables列表作为参数；
         *    Func函数的作用就是将每个Observable最近发射的数据进行结合后发射出去；
         * ②.必须要所有的Observable都发射过数据，才能组合；
         */
        Observable.combineLatest(getObservable("one->"), getObservable("two->"), getObservable("three->"),
                new Func3<String, String, String,String>() {
            //使用一个函数结合它们最近发射的数据，然后发射这个函数的返回值
            @Override
            public String call(String str1, String str2, String str3) {
                return str1+","+str2+","+str3;
            }
        }).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                Log.v(TAG, "combineLatest:"+s);
            }
        });

    }

    /**
     *  如果一个Observable发射了一条数据，只要在另一个Observable发射的数据定义的时间窗口内，
     *  就结合两个Observable发射的数据，然后发射结合后的数据
     */
    private void op_Join(TextView textView){
        //目标Observable
        Observable<Integer> obs1 = Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                for (int i = 1; i < 5; i++) {
                    subscriber.onNext(i);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        //join
        Observable.just("srcObs-")
                .join(obs1,
                //接受从源Observable发射来的数据，并返回一个Observable，
                //这个Observable的生命周期决定了源Observable发射出来数据的有效期
                new Func1<String, Observable<Long>>() {
                    @Override
                    public Observable<Long> call(String s) {
                        return Observable.timer(3000, TimeUnit.MILLISECONDS);
                    }
                },
                //接受从目标Observable发射来的数据，并返回一个Observable，
                //这个Observable的生命周期决定了目标Observable发射出来数据的有效期
                new Func1<Integer, Observable<Long>>() {
                    @Override
                    public Observable<Long> call(Integer integer) {
                        return Observable.timer(2000, TimeUnit.MILLISECONDS);
                    }
                },
                //接收从源Observable和目标Observable发射来的数据，并返回最终组合完的数据
                new Func2<String,Integer,String>() {
                    @Override
                    public String call(String str1, Integer integer) {
                        return str1 + integer;
                    }
                })
        .subscribe(new Action1<String>() {
            @Override
            public void call(String o) {
                Log.v(TAG,"join:"+o);
            }
        });

        //groupJoin
        Observable.just("srcObs-").groupJoin(obs1,
                new Func1<String, Observable<Long>>() {
                    @Override
                    public Observable<Long> call(String s) {
                        return Observable.timer(3000, TimeUnit.MILLISECONDS);
                    }
                },
                new Func1<Integer, Observable<Long>>() {
                    @Override
                    public Observable<Long> call(Integer integer) {
                        return Observable.timer(2000, TimeUnit.MILLISECONDS);
                    }
                },
                new Func2<String,Observable<Integer>, Observable<String>>() {
                    @Override
                    public Observable<String> call(String s, Observable<Integer> integerObservable) {
                        return integerObservable.map(new Func1<Integer, String>() {
                            @Override
                            public String call(Integer integer) {
                                return s+integer;
                            }
                        });
                    }
                })
                .subscribe(new Action1<Observable<String>>() {
                    @Override
                    public void call(Observable<String> stringObservable) {
                        stringObservable.subscribe(new Action1<String>() {
                            @Override
                            public void call(String s) {
                                Log.v(TAG,"groupJoin:"+s);
                            }
                        });
                    }
                });
    }


    /**
     * 合并多个Observables的发射物
     */
    private void op_Merge(TextView textView){
        /*
         * merge:当其中一个Observable发生onError时，就会终止发射数据，并将OnError传递给观察者
         */
        Observable<Integer> odds = Observable.just(1, 3, 5);
        Observable<Integer> evens = Observable.just(2, 4, 6);
        Observable.merge(odds, evens)
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onNext(Integer item) {
                        Log.v(TAG, "merge Next: " + item);
                    }
                    @Override
                    public void onError(Throwable error) {
                        Log.e(TAG, "merge Error: " + error.getMessage());
                    }
                    @Override
                    public void onCompleted() {
                        Log.v(TAG, "merge Sequence complete.");
                    }
                });

        /*
         * mergeDelayError:当发生onError时，会等待其他Observable将数据发射完，然后才将onError发送个观察者
         */
        Observable.mergeDelayError(Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                for (int i = 0; i < 5; i++) {
                    if (i == 3) {
                        subscriber.onError(new Throwable("第一个发射onError了"));
                    }
                    subscriber.onNext(i);
                }
            }
        }), Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                for (int i = 10; i < 15; i++) {
                    subscriber.onNext(i);
                }
                subscriber.onCompleted();
            }
        })).subscribe(new Subscriber<Integer>() {
            @Override
            public void onNext(Integer item) {
                Log.v(TAG, "mergeDelayError Next: " + item);
            }
            @Override
            public void onError(Throwable error) {
                Log.e(TAG, "mergeDelayError Error: " + error.getMessage());
            }
            @Override
            public void onCompleted() {
                Log.v(TAG, "mergeDelayError Sequence complete.");
            }
        });

    }

    /**
     * 在Observable在发射数据之前先发射一个指定的数据序列
     */
    private void op_StartWith(TextView textView){
        /*
         * 插入一个Observable
         */
        Observable<Integer> obs1 = Observable.just(1, 2, 3);
        Observable<Integer> obs2 = Observable.just(4, 5, 6);
        obs1.startWith(obs2).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                Log.v(TAG, "onNext:"+integer);
            }
        });

        /*
         * 插入数据序列
         */
        Observable<String> obs3 = Observable.just("c","d","e");
        obs3.startWith("a", "b").subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                Log.v(TAG, "onNext:"+s);
            }
        });
    }

    /**
     * 将一个发射多个Observables的Observable转换成另一个单独的Observable
     * Switch订阅一个发射多个Observables的Observable。
     * 它每次观察那些Observables中的一个，
     * Switch返回的这个Observable取消订阅前一个发射数据的Observable，开始发射最近的Observable发射的数据。
     *
     * 注意：当原始Observable发射了一个新的Observable时（不是这个新的Observable发射了一条数据时），
     * 它将取消订阅之前的那个Observable。这意味着，
     * 在后来那个Observable产生之后到它开始发射数据之前的这段时间里，前一个Observable发射的数据将被丢弃
     */
    private void op_Switch(TextView textView){
        Observable.switchOnNext(Observable.create(
                new Observable.OnSubscribe<Observable<Long>>() {
                    @Override
                    public void call(Subscriber<? super Observable<Long>> subscriber) {
                        for (int i = 1; i < 3; i++) {
                            //每隔1s发射一个小Observable。小Observable每1s发射一个整数
                            //第一个小Observable将发射6个整数，第二个将发射3个整数
                            subscriber.onNext(Observable.interval(1000, TimeUnit.MILLISECONDS).take(i==1?6:3));
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
        )).subscribe(new Action1<Long>() {
            @Override
            public void call(Long s) {
                Log.v(TAG, "onNext:"+s);
            }
        });
    }

    /**
     * 通过一个函数将多个Observables的发射物结合到一起，基于这个函数的结果为每个结合体发射单个数据项。
     * 每个小Observable出一个数据，打包（zip）在一起后发射，数据不能重复打包
     */
    private void op_Zip(TextView textView){
        Observable obs1 = Observable.just(1,2,3,4);
        Observable obs2 = Observable.just(10,20,30,40);
        /*
         * zip(Observable,FuncN):
         * ①.能接受1~9个Observable作为参数，或者单个Observables列表作为参数；
         *    Func函数的作用就是从每个Observable中获取一个数据进行结合后发射出去；
         * ②.小Observable的每个数据只能组合一次，如果第二个小Observable发射数据的时候，
         *    第一个还没有发射，将要等待第一个发射数据后才能组合；
         */
        Observable.zip(obs1, obs2,
                new Func2<Integer, Integer, String>() {
                    //使用一个函数结合每个小Observable的一个数据（每个数据只能组合一次），然后发射这个函数的返回值
                    @Override
                    public String call(Integer int1, Integer int2) {
                        return int1+"-"+int2;
                    }
                }).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                Log.v(TAG, "zip:"+s);
            }
        });

        /*
         * zipWith(Observable,Func2):
         * ①.zipWith不是static的，必须由一个Observable对象调用
         * ②.如果要组合多个Observable，可以传递Iterable
         */
        obs1.zipWith(obs2, new Func2<Integer, Integer, String>() {
            //使用一个函数结合每个小Observable的一个数据（每个数据只能组合一次），然后发射这个函数的返回值
            @Override
            public String call(Integer int1, Integer int2) {
                return int1+"-"+int2;
            }
        }).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                Log.v(TAG, "zipWith:"+s);
            }
        });


    }




}
