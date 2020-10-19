package com.openxu.rxjava.myrx;

/**
 * Author: openXu
 * Time: 2020/10/16 15:42
 * class: ObservableCreate
 * Description: 具体的被观察者实现
 */
public class ObservableCreate<T> extends Observable<T> {

    ObservableOnSubscribe<T> source;

    public ObservableCreate(ObservableOnSubscribe<T> source) {
        this.source = source;
    }

    @Override
    protected void subscribeActual(Observer<? super T> observer) {
        observer.onSubscribe();   //订阅回调
        //暴露给程序员编写业务代码，并发送数据给观察者
        source.subscribe(new CreateEmitter(observer));
    }

    static final class CreateEmitter<T> implements Emitter<T>{
        final Observer<T> observer;
        public CreateEmitter(Observer<T> observer) {
            this.observer = observer;
        }
        @Override
        public void onNext(T t) {
            observer.onNext(t);
        }
        @Override
        public void onError(Throwable e) {
            observer.onError(e);
        }
        @Override
        public void onCompleted() {
            observer.onCompleted();
        }
    }


}


