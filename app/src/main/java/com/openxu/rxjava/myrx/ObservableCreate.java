package com.openxu.rxjava.myrx;

/**
 * Author: openXu
 * Time: 2020/10/16 15:42
 * class: ObservableCreate
 * Description: 具体的被观察者实现
 */
public class ObservableCreate<T> extends Observable {

    ObservableOnSubscribe<T> source;

    public ObservableCreate(ObservableOnSubscribe<T> source) {
        this.source = source;
    }

    @Override
    protected void subscribeActual(Observer observer) {
        observer.onSubscribe();   //订阅开始
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


