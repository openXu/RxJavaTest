package com.openxu.rxjava.myrx.decorator;

/**
 * Author: openXu
 * Time: 2020/10/16 17:51
 * class: PizzaA
 * Description:
 */
public class PizzaB extends BasePizza{

    @Override
    public double getPrice() {
        return super.getPrice()+8;
    }

    @Override
    public String getName() {
        return super.getName() + "+火腿";
    }
}
