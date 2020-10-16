package com.openxu.rxjava.myrx.mapop;

/**
 * 事件变换
 */
public interface Function<T,R> {
    R apply(T t);
}
