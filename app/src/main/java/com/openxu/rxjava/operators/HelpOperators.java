package com.openxu.rxjava.operators;

import android.util.Log;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import rx.Notification;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * author : openXu
 * created time : 16/6/6 下午7:34
 * blog : http://blog.csdn.net/xmxkf
 * github : http://blog.csdn.net/xmxkf
 * class name : TransformOperators
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

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        /*
         * Delay操作符让原始Observable在发射每项数据之前都暂停一段指定的时间段。
         * 效果是Observable发射的数据项在时间上向前整体平移了一个增量
         *
         * 注意：delay不会平移onError通知，它会立即将这个通知传递给订阅者，同时丢弃任何待发射的onNext通知。
         * 然而它会平移一个onCompleted通知。
         */
        Log.v(TAG, "start:" + sdf.format(new Date()));
        Observable.interval(100, TimeUnit.MILLISECONDS)
                .take(3)
                .subscribeOn(Schedulers.newThread())
                .delay(2, TimeUnit.SECONDS)
                .subscribe(new Action1<Long>() {
            @Override
            public void call(Long aLong) {
                Log.v(TAG, "delay onNext:" + sdf.format(new Date())+":"+aLong);
            }
        });

        /*
         * delaySubscription:延迟订阅原始Observable
         */
        Log.v(TAG, "start:" + sdf.format(new Date()));
        Observable.interval(100, TimeUnit.MILLISECONDS)
                .take(3)
                .subscribeOn(Schedulers.newThread())
                .delaySubscription(2, TimeUnit.SECONDS)
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        Log.v(TAG, "delaySubscription onNext:" + sdf.format(new Date())+":"+aLong);
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
                        if( (int)notification.getValue() > 1 ) {
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
                for(int i =0;i<5; i++){
                    if(i>2){
                        new RuntimeException("TO MAX VALUE");
                    }
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

                    @Override
                    public void onNext(Notification<Integer> integerNotification) {
                        Log.v(TAG,"onNext:"+integerNotification.getKind()+":"+integerNotification.getValue());
                    }
                });

        Log.v(TAG, "dematerialize-----------");
        obs.materialize()
                .dematerialize()
                .subscribe(i->Log.v(TAG, "deMeterialize:"+i));
    }


    /**
     *
     */
    private void op_OberveOn(TextView textView){

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
     *
     */
    private void op_SubscribeOn(TextView textView){

    }

    /**
     *
     */
    private void op_TimeInterval(TextView textView){

    }

    /**
     *
     */
    private void op_Timeout(TextView textView){

    }

    /**
     *
     */
    private void op_Timestamp(TextView textView){

    }

    /**
     *
     */
    private void op_Using(TextView textView){

    }

    /**
     *
     */
    private void op_To(TextView textView){

    }


}
