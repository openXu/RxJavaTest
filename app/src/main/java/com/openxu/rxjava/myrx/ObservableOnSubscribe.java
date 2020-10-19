package com.openxu.rxjava.myrx;

/**
 * Author: openXu
 * Time: 2020/10/16 15:33
 * class: ObservableOnSubscribe
 * Description: Observable被订阅后应该做的事情，用于处理业务数据，并发射数据给观察者
 */
public interface ObservableOnSubscribe<T> {
    void subscribe(Emitter<T> emitter);
}
