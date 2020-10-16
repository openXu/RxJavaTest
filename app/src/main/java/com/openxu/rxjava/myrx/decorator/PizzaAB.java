package com.openxu.rxjava.myrx.decorator;

/**
 * Author: openXu
 * Time: 2020/10/16 17:51
 * class: PizzaA
 * Description:
 */
public class PizzaAB extends BasePizza{

    @Override
    public double getPrice() {
        return super.getPrice()+8+12;
    }

    @Override
    public String getName() {
        return super.getName() + "+火腿"+"+鸡蛋";
    }
}
