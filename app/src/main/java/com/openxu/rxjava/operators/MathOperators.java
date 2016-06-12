package com.openxu.rxjava.operators;

import android.util.Log;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 *  author : openXu
 * created time : 16/6/6 下午7:34
 * blog : http://blog.csdn.net/xmxkf
 * github : http://blog.csdn.net/xmxkf
 * class name : MathOperators
 *
 * discription :算数和聚合操作
 *
 * 本页展示的操作符用于对整个序列执行算法操作或其它操作，由于这些操作必须等待数据发射完成（通常也必须缓存这些数据），它们对于非常长或者无限的序列来说是危险的，不推荐使用。

 rxjava-math 模块的操作符
     averageInteger( ) — 求序列平均数并发射
     averageLong( ) — 求序列平均数并发射
     averageFloat( ) — 求序列平均数并发射
     averageDouble( ) — 求序列平均数并发射
     max( ) — 求序列最大值并发射
     maxBy( ) — 求最大key对应的值并发射
     min( ) — 求最小值并发射
     minBy( ) — 求最小Key对应的值并发射
     sumInteger( ) — 求和并发射
     sumLong( ) — 求和并发射
     sumFloat( ) — 求和并发射
     sumDouble( ) — 求和并发射

 其它聚合操作符
     concat( ) — 顺序连接多个Observables
     count( ) and countLong( ) — 计算数据项的个数并发射结果
     reduce( ) — 对序列使用reduce()函数并发射对吼的结果
     collect( ) — 将原始Observable发射的数据放到一个单一的可变的数据结构中，然后返回一个发射这个数据结构的Observable
     toList( ) — 收集原始Observable发射的所有数据到一个列表，然后返回这个列表
     toSortedList( ) — 收集原始Observable发射的所有数据到一个有序列表，然后返回这个列表
     toMap( ) — 将序列数据转换为一个Map，Map的key是根据一个函数计算的
     toMultiMap( ) — 将序列数据转换为一个列表，同时也是一个Map，Map的key是根据一个函数计算的
 *
 */
 public class MathOperators extends OperatorsBase {

    /**
     *
     */
    private void op_(TextView textView){

        Observable.just(1,2,3,4)
                .all(new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer integer) {
                        Log.v(TAG, ""+integer);
                        return integer<3;
                    }
                }).subscribe(new Subscriber<Boolean>() {
            @Override
            public void onCompleted() {
                Log.v(TAG, "onCompleted");
            }
            @Override
            public void onError(Throwable e) {
                Log.v(TAG, "onError:"+e.getMessage());
            }
            @Override
            public void onNext(Boolean aBoolean) {
                Log.v(TAG, "onNext:"+aBoolean);
            }
        });
        /*
        输出：
        1
        2
        3
        onNext:false
        onCompleted
         */
    }




}