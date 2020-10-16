package com.openxu.rxjava.myrx;

/**
 * Author: openXu
 * Time: 2020/10/16 14:13
 * class: Observer
 * Description: 抽象观察者
 */
public interface Observer<T> {

    //接受消息  update()
    void onNext(T t);
    //建立关联，用这个api来通知
    void onSubscribe();
    //异常
    void onError(Throwable e);
    //接受消息完成
    void onCompleted();


}
