package com.openxu.rxjava.myrx.mapop;


import com.openxu.rxjava.myrx.Observable;
import com.openxu.rxjava.myrx.ObservableSource;

/**
 * 被观察者的装饰类
 * T : 上游事件类型
 * U ：下游事件类型
 */
public abstract class AbstractObservableWithUpStream<T,U> extends Observable<U> {

    protected final ObservableSource<T> source;

    public AbstractObservableWithUpStream(ObservableSource<T> source) {
        this.source = source;
    }
}
