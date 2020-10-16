package com.openxu.rxjava.myrx;

/**
 * Author: openXu
 * Time: 2020/10/16 15:33
 * class: ObservableOnSubscribe
 * Description: 用来绑定被观察者和事件发射器
 */
public interface ObservableOnSubscribe<T> {
    void subscribe(Emitter<T> emitter);
}
