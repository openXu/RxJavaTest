package com.openxu.rxjava.myrx.decorator;

/**
 * Author: openXu
 * Time: 2020/10/16 17:51
 * class: Decorator
 * Description:
 */
public abstract class Decorator extends Pizza{
    @Override
    public double getPrice() {
        return this.getPrice();
    }
    public void show(){
        System.out.println(getName()+"   "+getPrice());
    }
}
