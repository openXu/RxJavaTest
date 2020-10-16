package com.openxu.rxjava.myrx.decorator;

/**
 * Author: openXu
 * Time: 2020/10/16 17:49
 * class: BasePizza
 * Description: 最基础的Pizza名称为Pizza，价格50
 */
public class BasePizza extends Pizza{

    @Override
    public String getName() {
        name = "Pizza";
        return super.getName();
    }

    @Override
    public double getPrice() {
        return 50.0;
    }
}
