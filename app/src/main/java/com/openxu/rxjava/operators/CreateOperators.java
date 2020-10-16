package com.openxu.rxjava.operators;

import android.util.Log;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;

/**
 * author : openXu
 * created time : 16/6/6 下午7:11
 * blog : http://blog.csdn.net/xmxkf
 * github : http://blog.csdn.net/xmxkf
 * class name : CreateOperators
 *
 * discription : 创建操作符
 *
 * 创建Observable的各种方法：
 * just( ) — 将一个或多个对象转换成发射这个或这些对象的一个Observable
 * from( ) — 将一个Iterable, 一个Future, 或者一个数组转换成一个Observable
 * repeat( ) — 创建一个重复发射指定数据或数据序列的Observable
 * repeatWhen( ) — 创建一个重复发射指定数据或数据序列的Observable，它依赖于另一个Observable发射的数据
 * create( ) — 使用一个函数从头创建一个Observable
 * defer( ) — 只有当订阅者订阅才创建Observable；为每个订阅创建一个新的Observable
 * range( ) — 创建一个发射指定范围的整数序列的Observable
 * interval( ) — 创建一个按照给定的时间间隔发射整数序列的Observable
 * timer( ) — 创建一个在给定的延时之后发射单个数据的Observable
 * empty( ) — 创建一个什么都不做直接通知完成的Observable
 * error( ) — 创建一个什么都不做直接通知错误的Observable
 * never( ) — 创建一个不发射任何数据的Observable
 */
public class CreateOperators extends OperatorsBase {

    /**
     * ①. 使用Create操作符从头开始创建一个Observable，给这个操作符传递一个接受观察者作为参数的函数，编写这个函数让它的行为表现为一个Observable：恰当的调用观察者的onNext，onError和onCompleted方法；
     * ②. 一个形式正确的有限Observable必须尝试调用观察者的onCompleted正好一次或者它的onError正好一次，而且此后不能再调用观察者的任何其它方法；
     * ③.  建议你在传递给create方法的函数中检查观察者的isUnsubscribed状态，以便在没有观察者的时候，让你的Observable停止发射数据或者做昂贵的运算；
     * ④.  create方法默认不在任何特定的调度器上执行。
     */
    private void op_Create(TextView textView){
        //订阅者
        Subscriber subscriber= new Subscriber<Integer>() {
            @Override
            public void onNext(Integer item) {
                Log.d(TAG, "Next: " + item);
            }
            @Override
            public void onError(Throwable error) {
                Log.d(TAG, "Error: " + error.getMessage());
            }
            @Override
            public void onCompleted() {
                Log.d(TAG, "Sequence complete.");
            }
        };
        //create方法默认不在任何特定的调度器上执行。
        Observable observable = Observable.create(new Observable.OnSubscribe<Integer>() {
            //当Observable.subscribe被调用时（有订阅者时）执行call方法
            @Override
            public void call(Subscriber<? super Integer> observer) {
                try {
                    //检查观察者的isUnsubscribed状态，以便在没有观察者的时候，让Observable停止发射数据或者做昂贵的运算
                    for (int i = 1; i < 5; i++) {
                        if(i == 4){
                            //取消订阅 (Unsubscribing),调用这个方法表示你不关心当前订阅的Observable了，
                            //因此Observable可以选择停止发射新的数据项（如果没有其它观察者订阅）。
                            subscriber.unsubscribe();
                        }
                        if (!observer.isUnsubscribed()) {
                            observer.onNext(i);
                        }
                    }
                    if (!observer.isUnsubscribed()) {
                        observer.onCompleted();
                    }
                } catch (Exception e) {
                    observer.onError(e);
                }
            }
        } );
        //订阅
        observable.subscribe(subscriber);
    }

    private Observable<Date> deferObservable;
    private Observable<Date> justObservable;

    /**
     * Defer操作符只有当观察者订阅时才创建一个新的Observable对象，
     * 每个观察者订阅的时候都会得到一个新的（不是同一个）Observable对象，
     * 以确保Observable包含最新的数据
     */
    private void op_Defer(TextView textView){
        if(deferObservable == null) {
            deferObservable = Observable.defer(() -> Observable.just(new Date()));
        }
        deferObservable.subscribe(date ->  {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            Log.v(TAG, "defer:" + sdf.format(date));
        });
        if(justObservable == null){
            justObservable = Observable.just(new Date());
        }
        justObservable.subscribe(date ->  {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            Log.v(TAG, "just:" + sdf.format(date));
        });
    }

    /**
     * 这三个操作符生成的Observable行为非常特殊和受限。
     * 测试的时候很有用，有时候也用于结合其它的Observables，或者作为其它需要Observable的操作符的参数
     */
    private void op_Empty_Never_Throw(TextView textView){
        //enpty默认实现call，只调用onCompleted：public void call(Subscriber<? super Object> child) {child.onCompleted();}
        Observable.empty().subscribe(new Subscriber<Object>() {
            @Override
            public void onNext(Object item) {
                Log.d(TAG, "Enpty：Next: " + item);
            }
            @Override
            public void onError(Throwable error) {
                Log.d(TAG, "Enpty：Error: " + error.getMessage());
            }
            @Override
            public void onCompleted() {
                Log.d(TAG, "Enpty：Sequence complete.");
            }
        });

        //Never:创建一个不发射数据也不终止的Observable（不会调用订阅者的任何方法）
        Observable.never().subscribe(new Subscriber<Object>() {
            @Override
            public void onNext(Object item) {
                Log.d(TAG, "Nerver：Next: " + item);
            }
            @Override
            public void onError(Throwable error) {
                Log.d(TAG, "Nerver：Error: " + error.getMessage());
            }
            @Override
            public void onCompleted() {
                Log.d(TAG, "Nerver：Sequence complete.");
            }
        });

        //Error:创建一个不发射数据以一个错误终止的Observable（只会调用onError）
        Observable.error(new Throwable("just call onError")).subscribe(new Subscriber<Object>() {
            @Override
            public void onNext(Object item) {
                Log.d(TAG, "Error：Next: " + item);
            }
            @Override
            public void onError(Throwable error) {
                Log.d(TAG, "Error：Error: " + error.getMessage());
            }
            @Override
            public void onCompleted() {
                Log.d(TAG, "Error：Sequence complete.");
            }
        });
    }

    /**
     * From操作符用来将某个对象转化为Observable对象，并且依次将其内容发射出去。
     * 这个类似于just，但是just会将这个对象整个发射出去。
     * 比如说一个含有10个数字的数组，使用from就会发射10次，每次发射一个数字，
     * 而使用just会发射一次来将整个的数组发射出去。
     */
    private void op_From(TextView textView){
        Integer[] items = { 0, 1, 2, 3, 4, 5 };
        Observable myObservable = Observable.from(items);
        myObservable.subscribe(
                new Action1<Integer>() {
                    @Override
                    public void call(Integer item) {
                        Log.d(TAG, item+"");
                    }
                },
                new Action1<Throwable>() {
                    @Override
                    public void call(Throwable error) {
                        Log.d(TAG,"Error encountered: " + error.getMessage());
                    }
                },
                new Action0() {
                    @Override
                    public void call() {
                        Log.d(TAG,"Sequence complete");
                    }
                }
        );
    }

    /**
     * Interval所创建的Observable对象每隔固定的时间发射一个数字。
     * 需要注意的是这个对象是运行在computation Scheduler,所以如果需要更新view，要在主线程中订阅。
     */
    private void op_Interval(TextView textView){
        //以秒为单位，每隔1秒发射一个数据
        Observable.interval(1, TimeUnit.SECONDS)
                //interva operates by default on the computation Scheduler,so observe on main Thread
                //如果需要更新view，要在主线程中订阅
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Long>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG,"onCompleted" );
                    }
                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG,"onError:" + e.getMessage());
                    }
                    @Override
                    public void onNext(Long aLong) {
                        Log.d(TAG,"interval:" + aLong);
                        textView.setText("Interval:"+aLong);
                    }
                });
    }

    /**
     * Just将单个数据转换为发射那个数据的Observable。
     * Just类似于From，但是From会将数组或Iterable的素具取出然后逐个发射，而Just只是简单的原样发射，将数组或Iterable当做单个数据。
     */
    private void op_Just(TextView textView){
        Observable.just(1, 2, 3)
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onNext(Integer item) {
                        Log.d(TAG,"Next: " + item);
                    }

                    @Override
                    public void onError(Throwable error) {
                        System.err.println("Error: " + error.getMessage());
                    }

                    @Override
                    public void onCompleted() {
                        Log.d(TAG,"Sequence complete.");
                    }
                });

    }


    /**
     * Range操作符发射一个范围内的有序整数序列，你可以指定范围的起始和长度。
     * 它接受两个参数，一个是范围的起始值，一个是范围的数据的数目。
     * 如果你将第二个参数设为0，将导致Observable不发射任何数据（如果设置为负数，会抛异常）。
     *
     * range默认不在任何特定的调度器上执行。有一个变体可以通过可选参数指定Scheduler。
     */
    private void op_Range(TextView textView){
        Observable.range(100, 6).subscribe(new Subscriber<Integer>() {
            @Override
            public void onNext(Integer item) {
                Log.d(TAG,"Next: " + item);
            }
            @Override
            public void onError(Throwable error) {
                System.err.println("Error: " + error.getMessage());
            }
            @Override
            public void onCompleted() {
                Log.d(TAG,"Sequence complete.");
            }
        });
    }

    /**
     * ①.  Repeat重复地发射数据。某些实现允许你重复的发射某个数据序列，还有一些允许你限制重复的次数。
     * ②.  它不是创建一个Observable，而是重复发射原始Observable的数据序列，这个序列或者是无限的，或者通过repeat(n)指定重复次数。
     * ③.  repeat操作符默认在trampoline调度器上执行。有一个变体可以通过可选参数指定Scheduler
     */
    private void op_Repeat(TextView textView){
        //重复5次发送数据1
        Observable.just(1).repeat(5).subscribe(new Subscriber<Integer>() {
            @Override
            public void onNext(Integer item) {
                Log.d(TAG,"Next: " + item);
            }
            @Override
            public void onError(Throwable error) {
                System.err.println("Error: " + error.getMessage());
            }
            @Override
            public void onCompleted() {
                Log.d(TAG,"Sequence complete.");
            }
        });
    }


    /**
     *Timer操作符创建一个在给定的时间段之后返回一个特殊值的Observable
     */
    private void op_Timer(TextView textView){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String startTime = sdf.format(new Date());
        Log.v(TAG, "startTime:" + startTime);
        Observable.timer(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Long>() {
                    @Override
                    public void onNext(Long item) {
                        //Timer创建的对象在2秒钟后发射了一个0
                        Log.d(TAG,"Next: " + item);
                        String endTime =  sdf.format(new Date());
                        textView.setText(startTime+":Timer："+endTime);
                        Log.v(TAG, "startTime:" + endTime);
                    }
                    @Override
                    public void onError(Throwable error) {
                        System.err.println("Error: " + error.getMessage());
                    }
                    @Override
                    public void onCompleted() {
                        Log.d(TAG,"Sequence complete.");
                    }
                });
    }

}
