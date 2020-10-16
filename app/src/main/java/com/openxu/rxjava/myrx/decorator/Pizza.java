package com.openxu.rxjava.myrx.decorator;

/**
 * Author: openXu
 * Time: 2020/10/16 17:48
 * class: Pizza
 * Description:
 */
public abstract class Pizza {
    protected String name;
    public String getName() {
        return name;
    }
    public abstract double getPrice();
}
