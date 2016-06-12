package com.openxu.rxjava.operators;

import android.util.Log;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * author : openXu
 * created time : 16/6/6 下午7:34
 * blog : http://blog.csdn.net/xmxkf
 * github : http://blog.csdn.net/xmxkf
 * class name : FilterOperators
 *
 * discription :过滤操作符
 *
 * 这个页面展示的操作符可用于过滤和选择Observable发射的数据序列。

 * filter( ) — 过滤数据
 * takeLast( ) — 只发射最后的N项数据
 * last( ) — 只发射最后的一项数据
 * lastOrDefault( ) — 只发射最后的一项数据，如果Observable为空就发射默认值
 * takeLastBuffer( ) — 将最后的N项数据当做单个数据发射
 * skip( ) — 跳过开始的N项数据
 * skipLast( ) — 跳过最后的N项数据
 * take( ) — 只发射开始的N项数据
 * first( ) and takeFirst( ) — 只发射第一项数据，或者满足某种条件的第一项数据
 * firstOrDefault( ) — 只发射第一项数据，如果Observable为空就发射默认值
 * elementAt( ) — 发射第N项数据
 * elementAtOrDefault( ) — 发射第N项数据，如果Observable数据少于N项就发射默认值
 * sample( ) or throttleLast( ) — 定期发射Observable最近的数据
 * throttleFirst( ) — 定期发射Observable发射的第一项数据
 * throttleWithTimeout( ) or debounce( ) — 只有当Observable在指定的时间后还没有发射数据时，才发射一个数据
 * timeout( ) — 如果在一个指定的时间段后还没发射数据，就发射一个异常
 * distinct( ) — 过滤掉重复数据
 * distinctUntilChanged( ) — 过滤掉连续重复的数据
 * ofType( ) — 只发射指定类型的数据
 * ignoreElements( ) — 丢弃所有的正常数据，只发射错误或完成通知
 */
public class FilterOperators extends OperatorsBase{


    /**
     *
     */
    private void op_Debounce(TextView textView){
        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                for (int i = 0; i < 10; i++) {
                    int sleep = 100;
                    if (i % 3 == 0) {
                        sleep = 300;
                    }
                    try {
                        Thread.sleep(sleep);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    subscriber.onNext(i);
                }
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.computation())
          .throttleWithTimeout(200, TimeUnit.MILLISECONDS)
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(new Subscriber<Integer>() {
              @Override
              public void onCompleted() {
                  Log.d(TAG, "onCompleted:");
              }

              @Override
              public void onError(Throwable e) {
                  Log.d(TAG, "onError:");
              }
              @Override
              public void onNext(Integer integer) {
                  Log.d(TAG, "onNext:"+integer);
              }
          });

/*


                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(i -> Log.d(TAG, "throttleWithTimeout:" + i));
*/



    }

    /**
     *  Distinct操作符的用处就是用来去重，所有重复的数据都会被过滤掉。
     *  还有一个操作符distinctUntilChanged,是用来过滤掉连续的重复数据
     */
    private void op_Distinct(TextView textView){
        //过滤所有的重复数据（比较原始数据）
        Observable.just(1, 2, 1, 1, 2, 3)
                .distinct()
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Log.d(TAG, "distinct:"+integer);
                    }
                });
        //过滤所有的重复数据（比较key）
        Observable.just(1, 2, 1, 1, 2, 3)
                .distinct(new Func1<Integer, String>() {
                    @Override
                    public String call(Integer integer) {
                        return integer%2==0?"偶":"奇";
                    }
                }).subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Log.d(TAG, "distinct(Func1):"+integer);
                    }
                });
        //过滤连续的重复数据（比较原始数据）
        Observable.just(1, 2, 1, 1, 2, 3)
                .distinctUntilChanged()
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Log.d(TAG, "distinctUntilChanged:"+integer);
                    }
                });
        //过滤连续的重复数据（比较key）
        Observable.just(1, 2, 1, 1, 2, 3)
                .distinctUntilChanged(new Func1<Integer, String>() {
                    @Override
                    public String call(Integer integer) {
                        return integer%2==0?"偶":"奇";
                    }
                }).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                Log.d(TAG, "distinctUntilChanged(Func1):"+integer);
            }
        });
    }

    /**
     * ElementAt：给它传递一个基于0的索引值，它会发射原始Observable数据序列对应索引位置的值，
     * 比如你传递给elementAt的值为5，那么它会发射第六项的数据。
     * 如果你传递的是一个负数，或者原始Observable的数据项数小于index+1，将会抛出一个IndexOutOfBoundsException异常。
     *
     * elementAtOrDefault：与elementAt的区别是，如果索引值大于数据项数，
     * 它会发射一个默认值（通过额外的参数指定），而不是抛出异常。
     * 但是如果你传递一个负数索引值，它仍然会抛出一个IndexOutOfBoundsException异常。
     */
    private void op_ElementAt(TextView textView){
        Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9)
               .elementAt(5)     //只发射索引值为5（0开始）的数据
               .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Log.d(TAG, "elementAt:"+integer);
                }
        });

        Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9)
                //只发射索引值为20（0开始）的数据，角标越界会发射默认值100
                .elementAtOrDefault(20, 100)
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Log.d(TAG, "elementAtOrDefault:"+integer);
                }
        });
    }


    class Person{

    }
    class Dog{

    }
    /**
     * Filter操作符使用你指定的一个谓词函数测试数据项，只有通过测试的数据才会被发射
     */
    private void op_Filter(TextView textView){
        Observable.just(1, 2, 3, 4, 5)
            .filter(new Func1<Integer, Boolean>() {
                @Override
                public Boolean call(Integer item) {
                    //只发射小于4的整数
                    return( item < 4 );
                }
            }).subscribe(new Action1<Integer>() {
                @Override
                public void call(Integer integer) {
                    Log.d(TAG, "Next: " + integer);
                }
            });


        Observable.just(new Person(), new Dog(), new Person(), new Dog(), new Dog())
                //只发射属于人类的数据
                .ofType(Person.class)
                .subscribe(new Action1<Person>() {
                    @Override
                    public void call(Person person) {
                        Log.d(TAG, "Next: " + person.getClass().getSimpleName());
                    }
                });
    }

    /**
     * 如果你只对Observable发射的第一项数据，或者满足某个条件的第一项数据感兴趣，你可以使用First操作符
     */
    private void op_First(TextView textView){
        //只发射第一个数据1
        Observable.just(1, 2, 3)
            .first()
            .subscribe(new Action1<Integer>() {
                @Override
                public void call(Integer integer) {
                    Log.d(TAG, "Next: " + integer);
                }
            });

        Observable.just(1, 2, 3)
                .first(new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer integer) {
                        //只发射第一个大于2的数据
                        return integer>2;
                    }
                }).subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Log.d(TAG, "Next: " + integer);
                    }
                });

        Observable.just(1, 2, 3)
                .firstOrDefault(10, new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer integer) {
                        //只发射第一个大于9的数据，如果没有发送默认值10
                        return integer>9;
                    }
                }).subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Log.d(TAG, "Next: " + integer);
                    }
                });


    }

    /**
     * IgnoreElements操作符抑制原始Observable发射的所有数据，只允许它的终止通知（onError或onCompleted）通过
     */
    private void op_IgnoreElements(TextView textView){
        //只会调用onCompleted或者onError
        Observable.just(1, 2, 3)
                .ignoreElements()
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onCompleted");
                    }
                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError:"+ e.getMessage());
                    }
                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG, "onNext:"+integer);
                    }
                });
    }

    /**
     * 只发射最后一项（或者满足某个条件的最后一项）数据
     */
    private void op_Last(TextView textView){
        //只发射最后一个数据
        Observable.just(1, 2, 3)
                .last()
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Log.d(TAG, "Next: " + integer);
                    }
                });

        Observable.just(1, 2, 3)
                .last(new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer integer) {
                        //只发射大于等于2的最后一个数据
                        return integer>=2;
                    }
                }).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                Log.d(TAG, "Next: " + integer);
            }
        });

        Observable.just(1, 2, 3)
                .lastOrDefault(10, new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer integer) {
                        //只发射大于9的最后一个数据，如果没有发送默认值10
                        return integer>9;
                    }
                }).subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Log.d(TAG, "Next: " + integer);
                    }
        });
    }

    /**
     *Sample (别名throttleLast)操作符定时查看一个Observable，然后发射自上次采样以来它最近发射的数据。
     ThrottleFirst操作符的功能类似，但不是发射采样期间的最近的数据，而是发射在那段时间内的第一项数据。
     */
    private void op_Sample(TextView textView){
        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                for (int i = 0; i <= 10; i++) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    subscriber.onNext(i);
                }
                subscriber.onCompleted();
            }
        }).sample(300, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Log.d(TAG, "sample: " + integer);
                    }
                });

        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                for (int i = 0; i <= 10; i++) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    subscriber.onNext(i);
                }
                subscriber.onCompleted();
            }
        }).throttleFirst(300, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Log.d(TAG, "throttleFirst: " + integer);
                    }
                });
    }

    /**
     * 忽略Observable发射的前N项数据，只保留之后的数据
     */
    private void op_Skip(TextView textView){
        Observable.just(0, 1, 2, 3, 4, 5)
                .skip(3)
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Log.d(TAG, "skip(int): " + integer);
                    }
                });

        //舍弃掉前1000ms内发射的数据，保留后面发射的数据
        Observable.interval(100, TimeUnit.MILLISECONDS)
                .skip(1000, TimeUnit.MILLISECONDS)
                .take(5)   //发射5条数据
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
                        Log.d(TAG,"skip(long, TimeUnit):" + aLong);
                    }
                });
    }

    /**
     * 忽略原始Observable发射的后N项数据，只保留之前的数据。
     * 注意：这个机制是这样实现的：延迟原始Observable发射的任何数据项，直到它发射了N项数据
     */
    private void op_SkipLast(TextView textView){
        Observable.just(0, 1, 2, 3, 4, 5)
                .skipLast(4)
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Log.d(TAG, "skipLast(int): " + integer);
                    }
                });
    }

    /**
     * 只发射前面的N项数据，然后发射完成通知，忽略剩余的数据
     */
    private void op_Take(TextView textView){
        //只发射前面3个数据
        Observable.just(1, 2, 3, 4, 5, 6, 7, 8)
                .take(3)
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Log.d(TAG,"take(int): " + integer);
                    }
                });

    }

    /**
     * 只发射原始Observable发射的后N项数据，忽略之前的数据
     */
    private void op_TakeLast(TextView textView){
        //只发射后面3个数据
        Observable.just(1, 2, 3, 4, 5, 6, 7, 8)
                .takeLast(3)
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Log.d(TAG,"takeLast(int): " + integer);
                    }
                });
        //只发射后面3个数据
        Observable.just(1, 2, 3, 4, 5, 6, 7, 8)
                .takeLastBuffer(3)
                .subscribe(new Action1<List<Integer>>() {
                    @Override
                    public void call(List<Integer> integers) {
                        Log.d(TAG,"takeLastBuffer(int): " + integers);
                    }
                });
    }



}
