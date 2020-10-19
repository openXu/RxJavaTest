package com.openxu.rxjava.myrx;

/**
 * Author: openXu
 * Time: 2020/10/16 14:19
 * class: ObservableSource
 * Description: 被观察者抽象，提供订阅接口
 */
public interface ObservableSource<T> {


    //订阅，绑定Observable与Observer的联系，相当于add(observer)
    void subscribe(Observer<? super T> observer);

}
