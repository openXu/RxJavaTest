package com.openxu.rxjava.operators;

import android.widget.TextView;


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
     * Average：计算原始Observable发射数字的平均值并发射它
     *
     * 这个操作符不包含在RxJava核心模块中，它属于不同的rxjava-math模块。
     * 它被实现为四个操作符：averageDouble, averageFloat, averageInteger, averageLong。
     */
    private void op_Average(TextView textView){


    }





}
