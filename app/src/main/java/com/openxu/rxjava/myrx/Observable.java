package com.openxu.rxjava.myrx;

import com.openxu.rxjava.myrx.mapop.Function;
import com.openxu.rxjava.myrx.mapop.ObservableMap;

/**
 * Author: openXu
 * Time: 2020/10/16 15:25
 * class: Observable
 * Description: 被观察者对外暴露的类，可以用它创建不同类型的被观察者
 */
public abstract class Observable<T> implements ObservableSource<T>{
    @Override
    public void subscribe(Observer<T> observer) {
        /**
         * 被某个观察者订阅后，按道理我应该可以在这里给它发消息（数据）了
         * 但是问题来了，我应该给它发什么数据？我的数据应该让程序员自己提供，
         * 所以应该把订阅方法暴露出去方便程序员处理数据并发送数据给观察者。
         *
         *
         */
//        observer.onSubscribe();   //订阅开始
//        observer.onNext(t);       //发送数据
        subscribeActual(observer);
    }

    protected abstract void subscribeActual(Observer<T> observer);

    public static <T> Observable<T> create(ObservableOnSubscribe<T> source){
        return new ObservableCreate(source);
    }

    //map操作
    public <U> Observable map(Function<T,U> function){
        return new ObservableMap(this,function);
    }

}
