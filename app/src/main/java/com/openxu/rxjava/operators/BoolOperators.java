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
 * class name : BoolOperators
 *
 * discription :条件和布尔操作
 *
 * 这个页面的操作符可用于根据条件发射或变换Observables，或者对它们做布尔运算：

 条件操作符

 amb( ) — 给定多个Observable，只让第一个发射数据的Observable发射全部数据
 defaultIfEmpty( ) — 发射来自原始Observable的数据，如果原始Observable没有发射数据，就发射一个默认数据
 (rxjava-computation-expressions) doWhile( ) — 发射原始Observable的数据序列，然后重复发射这个序列直到不满足这个条件为止
 (rxjava-computation-expressions) ifThen( ) — 只有当某个条件为真时才发射原始Observable的数据序列，否则发射一个空的或默认的序列
 skipUntil( ) — 丢弃原始Observable发射的数据，直到第二个Observable发射了一个数据，然后发射原始Observable的剩余数据
 skipWhile( ) — 丢弃原始Observable发射的数据，直到一个特定的条件为假，然后发射原始Observable剩余的数据
 (rxjava-computation-expressions) switchCase( ) — 基于一个计算结果，发射一个指定Observable的数据序列
 takeUntil( ) — 发射来自原始Observable的数据，直到第二个Observable发射了一个数据或一个通知
 takeWhile( ) and takeWhileWithIndex( ) — 发射原始Observable的数据，直到一个特定的条件为真，然后跳过剩余的数据
 (rxjava-computation-expressions) whileDo( ) — if a condition is true, emit the source Observable's sequence and then repeat the sequence as long as the condition remains true如果满足一个条件，发射原始Observable的数据，然后重复发射直到不满足这个条件为止
 (rxjava-computation-expressions) — 表示这个操作符当前是可选包 rxjava-computation-expressions 的一部分，还没有包含在标准RxJava的操作符集合里

 布尔操作符

 all( ) — 判断是否所有的数据项都满足某个条件
 contains( ) — 判断Observable是否会发射一个指定的值
 exists( ) and isEmpty( ) — 判断Observable是否发射了一个值
 sequenceEqual( ) — test the equality of the sequences emitted by two Observables判断两个Observables发射的序列是否相等
 *
 *
 */
 public class BoolOperators extends OperatorsBase {

    /**
     * all: 判定是否Observable发射的所有数据都满足某个条件
     *
     * All返回一个只发射一个单个布尔值的Observable，
     * 如果原始Observable正常终止并且每一项数据都满足条件，就返回true；
     * 如果原始Observable的任何一项数据不满足条件就返回False。
     */
    private void op_All(TextView textView){

        Observable.just(1,2,3,4)
                .all(new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer integer) {
                        Log.v(TAG, ""+integer);
                        return integer<3;    //判断是不是发射的所有数据都小于3
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

    /**
     * Amb:给定两个或多个Observables，它只发射首先发射数据或通知的那个Observable的所有数据
     */
    private void op_Amb(TextView textView){
        //Observable.amb(o1,o2)和o1.ambWith(o2)是等价的
        Observable.amb(
                //第一个Observable延迟1秒发射数据
                Observable.just(1,2,3).delay(1,TimeUnit.SECONDS),
                Observable.just(4,5,6))
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
                        Log.v(TAG, "onNext:"+integer);
                    }
                });
        /*
        输出：
        onNext:4
        onNext:5
        onNext:6
        onCompleted
         */
    }


    /**
     * Contains:判定一个Observable是否发射一个特定的值
     *
     * 给Contains传一个指定的值，如果原始Observable发射了那个值，
     * 它返回的Observable将发射true，否则发射false。
     */
    private void op_Contains_isEmpty_exists(TextView textView){
        //Contains:判定一个Observable是否发射一个特定的值
        Observable.just(4,5,6)
                .contains(4)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        Log.v(TAG, "contains(4):"+aBoolean);
                    }
                });
        //isEmpty:判定原始Observable是否没有发射任何数据
        Observable.just(4,5,6)
                .isEmpty()
                .subscribe(aBoolean->Log.v(TAG, "isEmpty():"+aBoolean));
        //exists操作符，它通过一个谓词函数测试原始Observable发射的数据，
        // 只要任何一项满足条件就返回一个发射true的Observable，
        // 否则返回一个发射false的Observable。
        Observable.just(4,5,6)
                .exists(new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer integer) {
                        return integer<5;
                    }
                })
                .subscribe(aBoolean->Log.v(TAG, "exists():"+aBoolean));
        /*
        输出：
        contains(4):true
        isEmpty():false
        exists():true
         */
    }

    /**
     * DefaultIfEmpty:发射来自原始Observable的值，如果原始Observable没有发射任何值，就发射一个默认值
     */
    private void op_DefaultIfEmpty(TextView textView){

        Observable.empty()
                .defaultIfEmpty(10)
                .subscribe(integer->Log.v(TAG, "defaultIfEmpty():"+integer));
        /*
        输出：
        defaultIfEmpty():10
         */
    }


    /**
     * SequenceEqual:判定两个Observables是否发射相同的数据序列。
     * 传递两个Observable给SequenceEqual操作符，它会比较两个Observable的发射物，
     * 如果两个序列是相同的（相同的数据，相同的顺序，相同的终止状态），它就发射true，否则发射false。
     *
     * 它还有一个版本接受第三个参数，可以传递一个函数用于比较两个数据项是否相同。
     *
     Javadoc: sequenceEqual(Observable,Observable)
     Javadoc: sequenceEqual(Observable,Observable,Func2)
     */
    private void op_SequenceEqual(TextView textView){
        Observable.sequenceEqual(
                //第一个Observable延迟1秒发射数据
                Observable.just(4,5,6).delay(1,TimeUnit.SECONDS),
                Observable.just(4,5,6))
                .subscribe(aBoolean -> Log.v(TAG, "sequenceEqual:"+aBoolean));
        /*
        输出：
        sequenceEqual:true
         */
    }


    /**
     * SkipUntil:丢弃原始Observable发射的数据，直到第二个Observable发射了一项数据
     *
     * SkipUntil订阅原始的Observable，但是忽略它的发射物，
     * 直到第二个Observable发射了一项数据那一刻，它开始发射原始Observable。
     */
    private void op_SkipUntil(TextView textView){
        Observable.interval(1, TimeUnit.SECONDS)
                .take(6)
                .skipUntil(Observable.just(10).delay(3,TimeUnit.SECONDS))
                .subscribe(aBoolean -> Log.v(TAG, "skipUntil:"+aBoolean));

        /*
        输出：
        skipUntil:3
        skipUntil:4
        skipUntil:5
         */
    }

    /**
     * SkipWhile:丢弃Observable发射的数据，直到一个指定的条件不成立
     *
     * SkipWhile订阅原始的Observable，但是忽略它的发射物，
     * 直到你指定的某个条件变为false的那一刻，它开始发射原始Observable。
     */
    private void op_SkipWhile(TextView textView){
        Observable.interval(1, TimeUnit.SECONDS)
                .take(6)
                .skipWhile(new Func1<Long, Boolean>() {
                    @Override
                    public Boolean call(Long aLong) {
                        return aLong<3;   //舍弃原Observable发射的数据，直到发射的数据>=3，才继续发射
                    }
                })
                .subscribe(aBoolean -> Log.v(TAG, "SkipWhile:"+aBoolean));

        /*
        输出：
        SkipWhile:3
        SkipWhile:4
        SkipWhile:5
         */
    }

    /**
     * TakeUntil：当第二个Observable发射了一项数据或者终止时，丢弃原始Observable发射的任何数据
     *
     * TakeUntil订阅并开始发射原始Observable，它还监视你提供的第二个Observable。
     * 如果第二个Observable发射了一项数据或者发射了一个终止通知，
     * TakeUntil返回的Observable会停止发射原始Observable并终止。
     *
     * 还有一个版本的takeUntil，不在RxJava 1.0.0版中，
     * 它使用一个谓词函数而不是第二个Observable来判定是否需要终止发射数据，它的行为类似于takeWhile。
     * Javadoc: takeUntil(Func1))
     */
    private void op_TakeUntil(TextView textView){
        //3s后takeUntil的参数Observable发射数据，停止原始Observable
        Observable.interval(1, TimeUnit.SECONDS)
                .takeUntil(Observable.timer(3, TimeUnit.SECONDS))
                .subscribe(aBoolean -> Log.v(TAG, "TakeUntil:"+aBoolean));

        /*
        输出：
        TakeUntil:0
        TakeUntil:1
         */
    }

    /**
     * TakeWhile：发射Observable发射的数据，直到一个指定的条件不成立
     *
     * TakeWhile发射原始Observable，直到你指定的某个条件不成立的那一刻，
     * 它停止发射原始Observable，并终止自己的Observable。
     */
    private void op_TakeWhile(TextView textView){
        Observable.interval(1, TimeUnit.SECONDS)
                .takeWhile(new Func1<Long, Boolean>() {
                    @Override
                    public Boolean call(Long aLong) {
                        return aLong<3;
                    }
                })
                .subscribe(aBoolean -> Log.v(TAG, "TakeWhile:"+aBoolean));

        /*
        输出：
        TakeWhile:0
        TakeWhile:1
        TakeWhile:2
         */
    }



}