package com.openxu.rxjava.myrx.decorator;

/**
 * Author: openXu
 * Time: 2020/10/16 17:51
 * class: PizzaC
 * Description:
 */
public class PizzaC extends BasePizza{

    private Pizza pizza;
    public PizzaC(Pizza pizza) {
        this.pizza = pizza;
    }
    @Override
    public double getPrice() {
        return pizza.getPrice()+12;
    }

    @Override
    public String getName() {
        return pizza.getName() + "+鸡蛋";
    }
}
