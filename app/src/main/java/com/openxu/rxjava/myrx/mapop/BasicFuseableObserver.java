package com.openxu.rxjava.myrx.mapop;


import com.openxu.rxjava.myrx.Observer;

public abstract class BasicFuseableObserver<T,U> implements Observer<T> {

    protected final Observer<U> actual;

    //参数actual就是下游的observer
    public BasicFuseableObserver(Observer<U> actual) {
        this.actual = actual;
    }

    @Override
    public void onSubscribe() {
    }
    @Override
    public void onError(Throwable e) {
    }
    @Override
    public void onCompleted() {
    }
}
