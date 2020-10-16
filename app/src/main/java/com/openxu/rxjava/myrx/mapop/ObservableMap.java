package com.openxu.rxjava.myrx.mapop;


import com.openxu.rxjava.myrx.ObservableSource;
import com.openxu.rxjava.myrx.Observer;

public class ObservableMap<T,U> extends AbstractObservableWithUpStream<T,U> {

    final Function<T,U> function;

    public ObservableMap(ObservableSource<T> source, Function<T, U> function) {
        super(source);
        this.function = function;
    }
    //真实的功能
    @Override
    protected void subscribeActual(Observer observer) {
        source.subscribe(new MapObserver<>(observer,function));
    }

    static final class MapObserver<T,U> extends BasicFuseableObserver<T,U>{

        final Function<T,U> mapper;

        public MapObserver(Observer<U> actual, Function<T, U> mapper) {
            super(actual);
            this.mapper = mapper;
        }

        @Override
        public void onNext(T t) {
            U apply=mapper.apply(t);
            actual.onNext(apply);
        }
    }
}
