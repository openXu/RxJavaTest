package com.openxu.rxjava.operators;

import android.util.Log;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import rx.Notification;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.schedulers.TimeInterval;
import rx.schedulers.Timestamped;

/**
 * author : openXu
 * created time : 16/6/6 下午7:34
 * blog : http://blog.csdn.net/xmxkf
 * github : http://blog.csdn.net/xmxkf
 * class name : HelpOperators
 *
 * discription : 辅助操作符
 *
 * 这个页面列出了很多用于Observable的辅助操作符
 *
 * materialize( ) — 将Observable转换成一个通知列表convert an Observable into a list of Notifications
 * dematerialize( ) — 将上面的结果逆转回一个Observable
 * timestamp( ) — 给Observable发射的每个数据项添加一个时间戳
 * serialize( ) — 强制Observable按次序发射数据并且要求功能是完好的
 * cache( ) — 记住Observable发射的数据序列并发射相同的数据序列给后续的订阅者
 * observeOn( ) — 指定观察者观察Observable的调度器
 * subscribeOn( ) — 指定Observable执行任务的调度器
 * doOnEach( ) — 注册一个动作，对Observable发射的每个数据项使用
 * doOnCompleted( ) — 注册一个动作，对正常完成的Observable使用
 * doOnError( ) — 注册一个动作，对发生错误的Observable使用
 * doOnTerminate( ) — 注册一个动作，对完成的Observable使用，无论是否发生错误
 * doOnSubscribe( ) — 注册一个动作，在观察者订阅时使用
 * doOnUnsubscribe( ) — 注册一个动作，在观察者取消订阅时使用
 * finallyDo( ) — 注册一个动作，在Observable完成时使用
 * delay( ) — 延时发射Observable的结果
 * delaySubscription( ) — 延时处理订阅请求
 * timeInterval( ) — 定期发射数据
 * using( ) — 创建一个只在Observable生命周期存在的资源
 * single( ) — 强制返回单个数据，否则抛出异常
 * singleOrDefault( ) — 如果Observable完成时返回了单个数据，就返回它，否则返回默认数据
 * toFuture( ), toIterable( ), toList( ) — 将Observable转换为其它对象或数据结构
 */
public class HelpOperators extends OperatorsBase {


    /**
     * RxJava的实现是 delay和delaySubscription
     */
    private void op_Delay(TextView textView){
        Observable<Integer> obs = Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                for(int i =0;i<5;i++){
                    if(i>2){
                        subscriber.onError(new Throwable("VALUE TO MAX"));
                    }
                    subscriber.onNext(i);
                }
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.computation());
        
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        /*
         * Delay操作符让原始Observable在发射每项数据之前都暂停一段指定的时间段。
         * 效果是Observable发射的数据项在时间上向前整体平移了一个增量
         *
         * 注意：delay不会平移onError通知，它会立即将这个通知传递给订阅者，同时丢弃任何待发射的onNext通知。
         * 然而它会平移一个onCompleted通知。
         */
        Log.v(TAG, "delay start:" + sdf.format(new Date()));
        obs.delay(2, TimeUnit.SECONDS)
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        Log.v(TAG, "delay onCompleted" + sdf.format(new Date()));
                    }
                    @Override
                    public void onError(Throwable e) {
                        Log.v(TAG, "delay onError"+e.getMessage());
                    }
                    @Override
                    public void onNext(Integer integer) {
                        Log.v(TAG, "delay onNext:" + sdf.format(new Date())+"->"+integer);
                    }
                });

        /*
         * delaySubscription:延迟订阅原始Observable
         */
        Log.v(TAG, "delaySubscription start:" + sdf.format(new Date()));
        obs.delaySubscription(2, TimeUnit.SECONDS)
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        Log.v(TAG, "delaySubscription onCompleted" + sdf.format(new Date()));
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.v(TAG, "delaySubscription onError"+e.getMessage());
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.v(TAG, "delaySubscription onNext:" + sdf.format(new Date())+"->"+integer);
                    }
                });


    }

    /**
     *  Do操作符就是给Observable的生命周期的各个阶段加上一系列的回调监听，
     *  当Observable执行到这个阶段的时候，这些回调就会被触发。
     *  在Rxjava实现了很多的doxxx操作符
     */
    private void op_Do(TextView textView){

        Log.v(TAG,"doOnNext------------------------");
        Observable.just(1, 2, 3)
                //只有onNext的时候才会被触发
                .doOnNext(new Action1<Integer>() {
                    @Override
                    public void call(Integer item) {
                        Log.v(TAG,"-->doOneNext: " + item);
                    }
                }).subscribe(new Subscriber<Integer>() {
            @Override
            public void onNext(Integer item) {
                Log.v(TAG,"Next: " + item);
            }
            @Override
            public void onError(Throwable error) {
                Log.v(TAG,"Error: " + error.getMessage());
            }
            @Override
            public void onCompleted() {
                Log.v(TAG,"Sequence complete.");
            }
        });

        Log.v(TAG,"doOnEach,doOnError------------------------");
        Observable.just(1, 2, 3)
                //Observable每发射一个数据的时候就会触发这个回调，不仅包括onNext还包括onError和onCompleted
                .doOnEach(new Action1<Notification<? super Integer>>() {
                    @Override
                    public void call(Notification<? super Integer> notification) {
                        Log.v(TAG,"-->doOnEach: " +notification.getKind()+":"+ notification.getValue());
                        if(Integer.parseInt(notification.getValue().toString()) > 1 ) {
                            throw new RuntimeException( "Item exceeds maximum value" );
                        }
                    }
                })
                //Observable异常终止调用onError时会被调用
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.v(TAG,"-->doOnError: "+throwable.getMessage() );
                    }
                })
                .subscribe(new Subscriber<Integer>() {
            @Override
            public void onNext(Integer item) {
                Log.v(TAG,"Next: " + item);
            }
            @Override
            public void onError(Throwable error) {
                Log.v(TAG,"Error: " + error.getMessage());
            }
            @Override
            public void onCompleted() {
                Log.v(TAG,"Sequence complete.");
            }
        });


        Log.v(TAG,"doxxx------------------------");
        Observable.just(1, 2, 3)
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        Log.v(TAG,"-->doOnCompleted:正常完成onCompleted");  //数据序列发送完毕回调
                    }
                })
                .doOnSubscribe(() -> Log.v(TAG,"-->doOnSubscribe:被订阅"))   //被订阅时回调
                //反订阅（取消订阅）时回调。当一个Observable通过OnError或者OnCompleted结束的时候，会反订阅所有的Subscriber
                .doOnUnsubscribe(() -> Log.v(TAG,"-->doOnUnsubscribe:反订阅"))
                //Observable终止之前会被调用，无论是正常还是异常终止
                .doOnTerminate(() -> Log.v(TAG,"-->doOnTerminate:终止之前"))
                //Observable终止之后会被调用，无论是正常还是异常终止
                .finallyDo(() -> Log.v(TAG,"-->finallyDo:终止之后"))
                .subscribe(new Subscriber<Integer>() {
            @Override
            public void onNext(Integer item) {
                Log.v(TAG,"Next: " + item);
            }
            @Override
            public void onError(Throwable error) {
                Log.v(TAG,"Error: " + error.getMessage());
            }
            @Override
            public void onCompleted() {
                Log.v(TAG,"Sequence complete.");
            }
        });


    }

    /**
     * Meterialize操作符将OnNext/OnError/OnComplete都转化为一个Notification对象并按照原来的顺序发射出来，
     * 而DeMeterialize则是执行相反的过程
     */
    private void op_Materialize_Dematerialize(TextView textView){
        Observable<Integer> obs = Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                for(int i = 0;i<3; i++){
                    subscriber.onNext(i);
                }
                subscriber.onCompleted();
            }
        });

        Log.v(TAG, "materialize-----------");
        obs.materialize()
                .subscribe(new Subscriber<Notification<Integer>>() {
                   @Override
                    public void onCompleted() {
                       Log.v(TAG,"Sequence complete.");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.v(TAG,"onError:"+e.getMessage());
                    }
                    //将所有的消息封装成Notification后再发射出去
                    @Override
                    public void onNext(Notification<Integer> integerNotification) {
                        Log.v(TAG,"onNext:"+integerNotification.getKind()+":"+integerNotification.getValue());
                    }
                });

        Log.v(TAG, "dematerialize-----------");
        obs.materialize()
            //将Notification逆转为普通消息发射
           .dematerialize()
           .subscribe(integer->Log.v(TAG, "deMeterialize:"+integer));
    }


    /**
     * SubscribeOn：指定Observable自身在哪个调度器上执行（即在那个线程上运行）
     * ObserveOn： 指定一个观察者在哪个调度器上观察这个Observable
     */
    private void op_ObserveOn_SubscribeOn(TextView textView){
        Observable<Integer> obs = Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                Log.v(TAG, "on subscrib:" + Thread.currentThread().getName());
                subscriber.onNext(1);
                subscriber.onCompleted();
            }
        });

        //在新建子线程中执行，在主线程中观察
        obs.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(i ->  Log.v(TAG, "mainThread-onNext:" + Thread.currentThread().getName()));

        obs.delaySubscription(2, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.computation())  //用于计算任务，如事件循环或和回调处理
                .observeOn(Schedulers.immediate())      //在当前线程立即开始执行任务
                .subscribe(i ->  Log.v(TAG, "immediate-onNext:" + Thread.currentThread().getName()));
    }

    /**
     *
     */
    private void op_Serialize(TextView textView){

    }

    /**
     *
     */
    private void op_Subscribe(TextView textView){

    }


    /**
     *  TimeInterval操作符拦截原始Observable发射的数据项，替换为两个连续发射物之间流逝的时间长度
     */
    private void op_TimeInterval(TextView textView){
        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                for (int i = 0; i <= 3; i++) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    subscriber.onNext(i);
                }
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.newThread())
                .timeInterval()
                .subscribe(new Subscriber<TimeInterval<Integer>>() {
                    @Override
                    public void onCompleted() {
                        Log.v(TAG, "onCompleted");
                    }
                    @Override
                    public void onError(Throwable e) {
                        Log.v(TAG, "onError:"+e.getMessage());
                    }
                    @Override
                    public void onNext(TimeInterval<Integer> integerTimeInterval) {
                        Log.v(TAG, "onNext:"+integerTimeInterval.getValue()+
                                "-"+integerTimeInterval.getIntervalInMilliseconds());
                    }
                });
    }

    /**
     * 如果原始Observable过了指定的一段时长没有发射任何数据，
     * Timeout操作符会以一个onError通知终止这个Observable，
     * 或者继续一个备用的Observable。
     */
    private void op_Timeout(TextView textView){
        Observable<Integer> obs = Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                for (int i = 0; i <= 3; i++) {
                    try {
                        Thread.sleep(i * 100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    subscriber.onNext(i);
                }
                subscriber.onCompleted();
            }
        });

        //发射数据时间间隔超过200ms超时
        obs.timeout(200, TimeUnit.MILLISECONDS)
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        Log.v(TAG, "onCompleted");
                    }
                    @Override
                    public void onError(Throwable e) {
                        Log.v(TAG, "onError:"+e);
                    }
                    @Override
                    public void onNext(Integer integer) {
                        Log.v(TAG, "onNext:"+integer);
                    }
        });

        //发射数据时间间隔超过200ms超时,超时后开启备用Observable
        obs.timeout(200, TimeUnit.MILLISECONDS, Observable.just(10,20))
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        Log.v(TAG, "onCompleted");
                    }
                    @Override
                    public void onError(Throwable e) {
                        Log.v(TAG, "onError:"+e);
                    }
                    @Override
                    public void onNext(Integer integer) {
                        Log.v(TAG, "onNext:"+integer);
                    }
                });
    }

    /**
     * 把Observable发射的数据重新包装了一下，将数据发射的时间打包一起发射出去，
     * 这样观察者不仅能得到数据，还能得到数据的发射时间
     */
    private void op_Timestamp(TextView textView){
        Observable.just(1,2,3)
                .timestamp()
                .subscribe(new Subscriber<Timestamped<Integer>>() {
                    @Override
                    public void onCompleted() {
                        Log.v(TAG, "onCompleted");
                    }
                    @Override
                    public void onError(Throwable e) {
                        Log.v(TAG, "onError:"+e.getMessage());
                    }
                    @Override
                    public void onNext(Timestamped<Integer> integerTimestamped) {
                        Log.v(TAG, "onNext:"+integerTimestamped.getValue()+
                                ",time:"+integerTimestamped.getTimestampMillis());
                    }
                });
    }

    class MyObject{
        public void release(){
            Log.v(TAG, "object resource released");
        }
    }
    /**
     * Using操作符指示Observable创建一个只在它的生命周期内存在的资源，
     * 当Observable终止时这个资源会被自动释放。
     */
    private void op_Using(TextView textView){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        Observable<Long> obs = Observable.using(
                //一个用于 创建一次性资源的工厂函数
                new Func0<MyObject>() {
                    @Override
                    public MyObject call() {
                        return new MyObject();
                    }
                }
                //一个用于创建Observable的工厂函数，这个函数返回的Observable就是最终被观察的Observable
                , new Func1<MyObject, Observable<Long>>() {
                    @Override
                    public Observable<Long> call(MyObject obj) {
                        //创建一个Observable，3s之后发射一个简单的数字0
                        return Observable.timer(3000,TimeUnit.MILLISECONDS);
                    }
                }
                //一个用于释放资源的函数，当Func2返回的Observable执行完毕之后会被调用
                ,new Action1<MyObject>(){
                    @Override
                    public void call(MyObject o) {
                       o.release();
                    }
                }
        );

        Subscriber subscriber = new Subscriber<Long>() {
            @Override
            public void onCompleted() {
                Log.v(TAG, "onCompleted:" + sdf.format(new Date()));
            }
            @Override
            public void onError(Throwable e) {
                Log.v(TAG, "onError:"+e.getMessage());
            }
            @Override
            public void onNext(Long l) {
                Log.v(TAG, "onNext:"+l);
            }
        };

        Log.v(TAG, "start:" + sdf.format(new Date()));
        obs.subscribe(subscriber);
    }

    /**
     * 将Observable转换为另一个对象或数据结构
     */
    private void op_To(TextView textView){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        //toList:阻塞等待原Observable发射完毕后，将发射的数据转换成List发射出去
        Log.v(TAG, "toList start:" + sdf.format(new Date()));
        Observable.interval(1000,TimeUnit.MILLISECONDS)
                .take(3)
                .toList()
                .subscribe(new Subscriber<List<Long>>() {
                    @Override
                    public void onCompleted() {
                        Log.v(TAG, "onCompleted");
                    }
                    @Override
                    public void onError(Throwable e) {
                        Log.v(TAG, "onError:" + e.getMessage());
                    }
                    @Override
                    public void onNext(List<Long> longs) {
                        Log.v(TAG, "onNext:" + longs+" ->"+ sdf.format(new Date()));
                    }
                });

        Observable.just(2,4,1,3)
                .delaySubscription(5, TimeUnit.SECONDS)  //延迟5s订阅
                .toSortedList()
                .subscribe(new Action1<List<Integer>>() {
                    @Override
                    public void call(List<Integer> integers) {
                        Log.v(TAG, "toSortedList onNext:" + integers);
                    }
                });

        Observable.just(2,4,1,3)
                .delaySubscription(7, TimeUnit.SECONDS)  //延迟5s订阅
                .toMultimap(new Func1<Integer, String>() {
                    //生成map的key
                    @Override
                    public String call(Integer integer) {
                        return integer % 2 == 0 ? "偶" : "奇";
                    }
                }, new Func1<Integer, String>() {
                    //转换原始数据项到Map存储的值（默认数据项本身就是值）
                    @Override
                    public String call(Integer integer) {
                        return integer%2==0?"偶"+integer : "奇"+integer;
                    }
                })
                .subscribe(new Action1<Map<String, Collection<String>>>() {
                    @Override
                    public void call(Map<String, Collection<String>> stringCollectionMap) {
                        Collection<String> o = stringCollectionMap.get("偶");
                        Collection<String> j = stringCollectionMap.get("奇");
                        Log.v(TAG, "toMultimap onNext:" + o);
                        Log.v(TAG, "toMultimap onNext:" + j);
                    }
                });


    }


}
