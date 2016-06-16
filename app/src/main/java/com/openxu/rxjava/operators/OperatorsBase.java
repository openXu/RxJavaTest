package com.openxu.rxjava.operators;

import java.util.ArrayList;
import java.util.List;

/**
 * author : openXu
 * created time : 16/6/6 下午7:19
 * blog : http://blog.csdn.net/xmxkf
 * github : http://blog.csdn.net/xmxkf
 * class name : OperatorsBase
 * discription :
 */
public class OperatorsBase {

    protected String TAG = "OperatorsBase";


    private static List<String> list;

    static {
        list = new ArrayList<>();
        list.add("com.openxu.rxjava.operators.CreateOperators");
        list.add("com.openxu.rxjava.operators.TransformOperators");
        list.add("com.openxu.rxjava.operators.FilterOperators");
        list.add("com.openxu.rxjava.operators.JoinOperators");
        list.add("com.openxu.rxjava.operators.ErrorOperators");
        list.add("com.openxu.rxjava.operators.HelpOperators");
        list.add("com.openxu.rxjava.operators.BoolOperators");
        list.add("com.openxu.rxjava.operators.MathOperators");
        list.add("com.openxu.rxjava.operators.ConnectOperators");
    }

    public OperatorsBase(){
        TAG = getClass().getSimpleName();
    }


    public static OperatorsBase getOperators(int group){
        try {
            Class clazz = OperatorsBase.class.getClassLoader().loadClass(list.get(group));
            OperatorsBase base = (OperatorsBase) clazz.getConstructor().newInstance();
            return base;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }



}
