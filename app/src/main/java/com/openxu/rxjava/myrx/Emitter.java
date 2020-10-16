package com.openxu.rxjava.myrx;

/**
 * Author: openXu
 * Time: 2020/10/16 15:30
 * class: Emitter
 * Description: 给Observer发送消息用的 发射器
 */
public interface Emitter<T> {

    void onNext(T t);
    void onError(Throwable e);
    void onCompleted();

}
