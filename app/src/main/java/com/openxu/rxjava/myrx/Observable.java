package com.openxu.rxjava.myrx;

import com.openxu.rxjava.myrx.mapop.Function;
import com.openxu.rxjava.myrx.mapop.ObservableMap;

/**
 * Author: openXu
 * Time: 2020/10/16 15:25
 * class: Observable
 * Description: 被观察者对外暴露的类，可以用它创建不同类型的被观察者对象
 */
public abstract class Observable<T> implements ObservableSource<T>{
    @Override
    public void subscribe(Observer<? super T> observer) {
        /**
         * 观察者订阅后，就可以在这里编写处理业务功能的代码，并将结果发送给观察者
         * 但是问题来了，我应该处理什么业务逻辑？给观察者发什么数据？这些功能应该由程序员提供
         * subscribe()方法的作用仅仅是订阅，它不应该处理具体的数据操作，所以需要将订阅之后的事情让子类自己实现
         */
        // observer.onSubscribe();   //订阅开始
        // 1. 处理功能，得到结果数据
        // T t = getData();
        // 2. 将结果数据发送给观察者
        // observer.onNext(t);       //发送数据
        subscribeActual(observer);
    }
    /**开闭原则（对修改封闭，对扩展开放）：真正的订阅方法实现，子类应该实现此方法*/
    protected abstract void subscribeActual(Observer<? super T> observer);

    public static <T> Observable<T> create(ObservableOnSubscribe<T> source){
        return new ObservableCreate(source);
    }

    //map操作
    public <U> Observable map(Function<T,U> function){
        return new ObservableMap(this,function);
    }

}
