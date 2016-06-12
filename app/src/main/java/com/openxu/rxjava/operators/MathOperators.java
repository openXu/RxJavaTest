package com.openxu.rxjava.operators;

import android.util.Log;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Action2;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.functions.Func2;

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
     * Average：计算原始Observable发射数字的平均值并发射它
     *
     * 这个操作符不包含在RxJava核心模块中，它属于不同的rxjava-math模块。
     * 它被实现为四个操作符：averageDouble, averageFloat, averageInteger, averageLong。
     */
    private void op_Average(TextView textView){
    }

    /**
     * Min:发射原始Observable的最小值.
     *
     * RxJava中，min属于rxjava-math模块。
     *
     * Max:发射原始Observable的最大值
     *
     * RxJava中，max属于rxjava-math模块。
     */
    private void op_Min_Max(TextView textView){
    }


    /**
     * Count:计算原始Observable发射物的数量，然后只发射这个值
     *
     * Count操作符将一个Observable转换成一个发射单个值的Observable，这个值表示原始Observable发射的数据的数量。
     * 如果原始Observable发生错误终止，Count不发射数据而是直接传递错误通知。
     * 如果原始Observable永远不终止，Count既不会发射数据也不会终止。
     *
     * RxJava的实现是count和countLong。
     */
    private void op_Count(TextView textView){

        Observable.from(new String[] { "one", "two", "three" })
                .count()
                .subscribe(integer->Log.v(TAG, "count:"+integer));

        Observable.from(new String[] { "one", "two", "three" })
                .countLong()
                .subscribe(aLong->Log.v(TAG, "countLong:"+aLong));

        /*
        输出：
        count:3
        countLong:3
         */

    }

    /**
     * Sum:计算Observable发射的数值的和并发射这个和
     *
     * Sum操作符操作一个发射数值的Observable，仅发射单个值：原始Observable所有数值的和。
     *
     * RxJava的实现是sumDouble, sumFloat, sumInteger, sumLong，
     * 它们不是RxJava核心模块的一部分，属于rxjava-math模块。
     */
    private void op_Sum(TextView textView){


    }

    /**
     * Concat:不交错的发射两个或多个Observable的发射物
     *
     * Concat操作符连接多个Observable的输出，就好像它们是一个Observable，
     * 第一个Observable发射的所有数据在第二个Observable发射的任何数据前面，以此类推。
     * 直到前面一个Observable终止，Concat才会订阅额外的一个Observable。
     *
     * 注意：因此，如果你尝试连接一个"热"Observable（这种Observable在创建后立即开始发射数据，即使没有订阅者）
     * ，Concat将不会看到也不会发射它之前发射的任何数据。
     *
     * 在ReactiveX的某些实现中有一种ConcatMap操作符（名字可能叫concat_all, concat_map,
     * concatMapObserver, for, forIn/for_in, mapcat, selectConcat或selectConcatObserver），
     * 他会变换原始Observable发射的数据到一个对应的Observable，然后再按观察和变换的顺序进行连接操作。
     *
     * StartWith操作符类似于Concat，但是它是插入到前面，而不是追加那些Observable的数据到原始Observable发射的数据序列。
     *
     * Merge操作符也差不多，它结合两个或多个Observable的发射物，但是数据可能交错，而Concat不会让多个Observable的发射物交错。
     *
     */
    private void op_Concat(TextView textView){

        //还有一个实例方法叫concatWith，这两者是等价的：Observable.concat(a,b)和a.concatWith(b)
        Observable.concat(
                Observable.interval(100,TimeUnit.MILLISECONDS).take(4),
                Observable.interval(200,TimeUnit.MILLISECONDS).take(5))
                .subscribe(aLong -> Log.v(TAG, "concat:"+aLong));

        /*
        输出：
        concat:0
        concat:1
        concat:2
        concat:3

        concat:0
        concat:1
        concat:2
        concat:3
        concat:4
         */
    }

    /**
     * Reduce:按顺序对Observable发射的每项数据应用一个函数并发射最终的值
     *
     * Reduce操作符对原始Observable发射数据的第一项应用一个函数，
     * 然后再将这个函数的返回值与第二项数据一起传递给函数，
     * 以此类推，持续这个过程知道原始Observable发射它的最后一项数据并终止，
     * 此时Reduce返回的Observable发射这个函数返回的最终值。
     *
     * 注意如果原始Observable没有发射任何数据，reduce抛出异常IllegalArgumentException。
     *
     * 在其它场景中，这种操作有时被称为累积，聚集，压缩，折叠，注射等。
     *
     * 还有一个版本的reduce额外接受一个种子参数。注意传递一个值为null的种子是合法的，
     * 但是与不传种子参数的行为是不同的。如果你传递了种子参数，并且原始Observable没有发射任何数据，
     * reduce操作符将发射这个种子值然后正常终止，而不是抛异常。
     *
     * 提示：不建议使用reduce收集发射的数据到一个可变的数据结构，那种场景你应该使用collect。
     *
     * collect与reduce类似，但它的目的是收集原始Observable发射的所有数据到一个可变的数据结构，
     * collect生成的这个Observable会发射这项数据。它需要两个参数：
     * 一个函数返回可变数据结构
     * 另一个函数，当传递给它这个数据结构和原始Observable发射的数据项时，适当地修改数据结构。
     * Javadoc: collect(Func0,Action2))
     */
    private void op_Reduce(TextView textView){

        Observable.just(1,2,3,4)
                .reduce(new Func2<Integer, Integer, Integer>() {
                    //integer为前面几项只和，integer2为当前发射的数据
                    @Override
                    public Integer call(Integer integer, Integer integer2) {
                        Log.v(TAG, "integer:"+integer+"  integer2:"+integer2);
                        return integer+integer2;
                    }
                }).subscribe(integer -> Log.v(TAG, "reduce:"+integer));
        /*
        输出：
        integer:1  integer2:2
        integer:3  integer2:3
        integer:6  integer2:4
        reduce:10
         */




    }


}
