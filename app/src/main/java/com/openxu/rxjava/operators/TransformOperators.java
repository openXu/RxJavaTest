package com.openxu.rxjava.operators;

import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.observables.GroupedObservable;

/**
 * author : openXu
 * created time : 16/6/6 下午7:34
 * blog : http://blog.csdn.net/xmxkf
 * github : http://blog.csdn.net/xmxkf
 * class name : TransformOperators
 *
 * discription :变换操作符
 *
 * 对Observable发射的数据执行变换操作的各种操作符:
 * map( ) — 对序列的每一项都应用一个函数来变换Observable发射的数据序列
 * flatMap( ), concatMap( ), and flatMapIterable( ) — 将Observable发射的数据集合变换为Observables集合，然后将这些Observable发射的数据平坦化的放进一个单独的Observable
 * switchMap( ) — 将Observable发射的数据集合变换为Observables集合，然后只发射这些Observables最近发射的数据
 * scan( ) — 对Observable发射的每一项数据应用一个函数，然后按顺序依次发射每一个值
 * groupBy( ) — 将Observable分拆为Observable集合，将原始Observable发射的数据按Key分组，每一个Observable发射一组不同的数据
 * buffer( ) — 它定期从Observable收集数据到一个集合，然后把这些数据集合打包发射，而不是一次发射一个
 * window( ) — 定期将来自Observable的数据分拆成一些Observable窗口，然后发射这些窗口，而不是每次发射一项
 * cast( ) — 在发射之前强制将Observable发射的所有数据转换为指定类型
 */
public class TransformOperators  extends OperatorsBase{


    /**
     * 定期收集Observable的数据放进一个数据包裹，然后发射这些数据包裹，而不是一次发射一个值。
     */
    private void op_Buffer(TextView textView){
        //一组缓存3个数据
        Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                .buffer(3)
                .subscribe(i -> Log.d(TAG, "1buffer-count:" + i));
        //每隔三个数据缓存2个数据
        Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                .buffer(2, 3)
                .subscribe(i -> Log.d(TAG, "1buffer-count&skip:" + i));

        Observable.interval(1, TimeUnit.SECONDS).
                buffer(3, TimeUnit.SECONDS)
                .subscribe(i -> Log.d(TAG, "2buffer-count:" + i));
        Observable.interval(1, TimeUnit.SECONDS).
                buffer(2, 3, TimeUnit.SECONDS)
                .subscribe(i -> Log.d(TAG, "2buffer-count&skip:" + i));
    }

    /**
     * FlatMap将一个发射数据的Observable变换为多个Observables，
     * 然后将它们发射的数据合并后放进一个单独的Observable
     */
    private void op_FlatMap(TextView textView){
        //将发射的数据都加上flat map的前缀
        Observable.just(1, 2, 3, 4, 5)
                .flatMap(integer -> Observable.just("flat map:" + integer))
                .subscribe(i -> Log.d(TAG, i));
        //会输出n个n数字
        Observable.just(1, 2, 3, 4)
                .flatMapIterable(
                        integer -> {
                            ArrayList<Integer> s = new ArrayList<>();
                            for (int i = 0; i < integer; i++) {
                                s.add(integer);
                            }
                            return s;
                        }
                )
                .subscribe(i -> Log.d(TAG, "flatMapIterable:" + i));
    }

    /**
     * GroupBy操作符将原始Observable发射的数据按照key来拆分成一些小的Observable，
     * 然后这些小的Observable分别发射其所包含的的数据，类似于sql里面的groupBy。
     * 在使用中，我们需要提供一个生成key的规则，所有key相同的数据会包含在同一个小的Observable种。
     */
    private void op_GroupBy(TextView textView){

        // groupBy(Func1)：Func1是对数据分组（确定key）
        Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9)
                .groupBy(new Func1<Integer, String>() {
                    @Override
                    public String call(Integer integer) {
                        //按照奇数和偶数分组
                        return integer % 2 == 0 ? "偶数" : "奇数";
                    }
                }).subscribe(new Action1<GroupedObservable<String, Integer>>() {
            @Override
            public void call(GroupedObservable<String, Integer> groupedObservable) {
//                groupedObservable.count()
//                        .subscribe(integer -> Log.v(TAG, "key" + groupedObservable.getKey() + " contains:" + integer + " numbers"));
                groupedObservable.subscribe(value->Log.v(TAG, "key" + groupedObservable.getKey() + " value:"+value));
            }
        });

        // groupBy(Func1,Func1)：Func1是对数据分组（确定key），Func2发射每个数据，在这里面可以对原始数据做处理
        Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9)
                .groupBy(new Func1<Integer, String>() {
                    @Override
                    public String call(Integer integer) {
                        //按照奇数和偶数分组
                        return integer % 2 == 0 ? "偶数" : "奇数";
                    }
                }, new Func1<Integer, String>() {
                    @Override
                    public String call(Integer integer) {
                        //在数字前面加上说明，如果不加这个参数，最后发射的数据就是原始整数
                        return (integer % 2 == 0 ? "偶数" : "奇数")+integer;
                    }
                }).subscribe(new Action1<GroupedObservable<String, String>>() {
            @Override
            public void call(GroupedObservable<String, String> groupedObservable) {
//                groupedObservable.count()
//                        .subscribe(integer -> Log.v(TAG, "key" + groupedObservable.getKey() + " contains:" + integer + " numbers"));
                groupedObservable.subscribe(value->Log.v(TAG, "key" + groupedObservable.getKey() + " value:"+value));
            }
        });
    }


    class Animal {
    }

    class Dog extends Animal {
    }

    /**
     * Map操作符对原始Observable发射的每一项数据应用一个你选择的函数，
     * 然后返回一个发射这些结果的Observable。
     *
     * cast操作符将原始Observable发射的每一项数据都强制转换为一个指定的类型，
     * 然后再发射数据，它是map的一个特殊版本
     */
    private void op_Map(TextView textView){
        String[] names = {"张三", "李四", "王二", "麻子"};
        //map
        Observable.from(names).map(new Func1<String, String>() {
            @Override
            public String call(String s) {
                //将原始Observable发射的每一项数据前面加上 “姓名：”
                return "姓名:"+s;
            }
        }).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                Log.v(TAG, s);
            }
        });

        //cast: 多态中可以将父类引用强转为子类对象
        //cast的强转只适用于多态，而不适用于String强转为Integer
        Animal animal = new Dog();  //多态
        Observable.just(animal)
                .cast(Dog.class)
                .subscribe(new Action1<Dog>() {
                   @Override
                   public void call(Dog dog) {
                       Log.v(TAG, "Cast ->" + dog);
                   }
               });

    }


    /**
     * Scan操作符对一个序列的数据应用一个函数，并将这个函数的结果发射出去，
     * 然后作为下个数据应用这个函数时候的第一个参数使用
     *
     * 可以传递一个种子值，新发射的第一项数据就是种子值
     */
    private void op_Scan(TextView textView){
        //会把原始数据的第一项当做新的第一项发射
        Observable.just(1, 2, 3, 4, 5)
            .scan(new Func2<Integer, Integer, Integer>() {
                @Override
                public Integer call(Integer sum, Integer item) {
                    Log.v(TAG, ">应用函数:" + sum+" ,"+item);
                    return sum + item;
                }
            }).subscribe(new Action1<Integer>() {
                @Override
                public void call(Integer integer) {
                    Log.v(TAG, "Next:" + integer);
                }
            });

        //scan将发射种子值3作为自己的第一项数据
        Observable.just(1, 2, 3, 4, 5)
                .scan(3, new Func2<Integer, Integer, Integer>() {
                    @Override
                    public Integer call(Integer sum, Integer item) {
                        Log.d(TAG, ">应用函数:" + sum+" ,"+item);
                        return sum + item;
                    }
                }).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                Log.d(TAG, "Next:" + integer);
            }
        });
    }

    /**
     * Window操作符类似于我们前面讲过的buffer，
     * 不同之处在于window发射的是一些小的Observable对象，由这些小的Observable对象来发射内部包含的数据。
     *
     * 同buffer一样，window不仅可以通过数目来分组还可以通过时间等规则来分组
     */
    private void op_Window(TextView textView){
        Observable.just(1, 2, 3, 4, 5, 6, 7)
                .window(3) //每次发射出一个包含三个整数的子Observable
                .subscribe(new Action1<Observable<Integer>>() {
                               @Override
                               public void call(Observable<Integer> integerObservable) {
                                   //每次发射一个子Observable
                                   Log.d(TAG,integerObservable+"");
                                   //订阅子Observable
                                   integerObservable.subscribe(new Action1<Integer>() {
                                       @Override
                                       public void call(Integer integer) {
                                           Log.d(TAG,"window:" + integer);
                                       }
                                   });
                               }
                         });

        Observable.just(1, 2, 3, 4, 5, 6, 7)
                .window(3, 2) //每次发射出一个包含三个整数的子Observable
                .subscribe(new Action1<Observable<Integer>>() {
                    @Override
                    public void call(Observable<Integer> integerObservable) {
                        Log.d(TAG,integerObservable+"");
                        //订阅子Observable
                        integerObservable.subscribe(new Action1<Integer>() {
                            @Override
                            public void call(Integer integer) {
                                Log.d(TAG,"windowSkip:" + integer);
                            }
                        });
                    }
                });

    }



}
