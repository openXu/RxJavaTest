package com.openxu.rxjava;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 *
 * RxJava －－（一.初体验）
 */
public class FirstActivity extends AppCompatActivity {


    private Context mContext;

    @Bind(R.id.btn_1)
    Button btn1;
    @Bind(R.id.btn_2)
    Button btn2;
    @Bind(R.id.btn_3)
    Button btn3;
    @Bind(R.id.btn_4)
    Button btn4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        ButterKnife.bind(this);


    }


    @OnClick({R.id.btn_1, R.id.btn_2, R.id.btn_3, R.id.btn_4})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_1:
                rx_1();
                break;
            case R.id.btn_2:
                rx_2();
                break;
            case R.id.btn_3:
                rx_3();
                break;
            case R.id.btn_4:
                startActivity(new Intent(mContext, OperatorsActivity.class));
                break;
        }
    }

    private void showToast(String s){
        Toast.makeText(mContext, s, Toast.LENGTH_SHORT).show();
    }

    /**
     * 初步探索
     */
    private void rx_1() {

        //创建一个Observable对象很简单，直接调用Observable.create即可
        Observable<String> myObservable = Observable.create(
                new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> sub) {
                                Log.v("TAG", "call----"+Thread.currentThread().getName());
                                sub.onNext("Hello, RxAndroid!");
                                sub.onCompleted();

                    }
                }
        );
        //创建一个Subscriber来处理Observable对象发出的字符串
        Subscriber<String> mySubscriber = new Subscriber<String>() {
            @Override
            public void onNext(String s) {
                Log.v("TAG", "onNext----"+Thread.currentThread().getName());
                showToast(s);
            }
            @Override
            public void onCompleted() {
            }
            @Override
            public void onError(Throwable e) {
            }
        };

        /*
         * 通过subscribe函数就可以将我们定义的myObservable对象和mySubscriber对象关联起来;
         * 这样就完成了subscriber对observable的订阅
         * 一旦mySubscriber订阅了myObservable，myObservable就是调用mySubscriber对象的onNext和onComplete方法，mySubscriber就会打印出Hello World
         */
        myObservable.subscribe(mySubscriber);
    }

    /**
     * 代码简化
     */
    private void rx_2() {
        //①.使用RxJava提供的便捷函数来减少代码
        //创建Observable对象的代码可以简化为一行
        Observable<String> myObservable = Observable.just("Hello, RxAndroid!");
        //简化Subscriber：上面的例子中，我们其实并不关心OnComplete和OnError，
        //我们只需要在onNext的时候做一些处理，这时候就可以使用Action1类
        Action1<String> onNextAction = new Action1<String>() {
            @Override
            public void call(String s) {
                showToast(s);
            }
        };
        //subscribe方法有一个重载版本，接受三个Action1类型的参数，分别对应OnNext，OnComplete， OnError函数
        //myObservable.subscribe(onNextAction, onErrorAction, onCompleteAction);
        //这里我们并不关心onError和onComplete，所以只需要第一个参数就可以
        myObservable.subscribe(onNextAction);

        //②.上面的代码最终可以写成这样
        Observable.just("Hello, RxAndroid!")
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        showToast(s);
                    }
                });

        //③.使用java8的lambda可以使代码更简洁
        //关于Lambda语法可以看看这篇博客：http://blog.csdn.net/xmxkf/article/details/51532028
        Observable.just("Hello, RxAndroid!")
                .subscribe(s -> showToast(s));
    }

    /**
     * 变换
     */
    private void rx_3() {
        /*
         * ①.我想在Hello, RxAndroid!后面加上一段签名，你可能会想到去修改Observable对象：
         */
        Observable.just("Hello, RxAndroid! -openXu")
                .subscribe(s -> showToast(s));
        /*
         * 如果我的Observable对象被多个Subscriber订阅，但是我只想在对某个订阅者做修改呢？
         */
        Observable.just("Hello, RxAndroid!")
                .subscribe(s -> showToast(s + " -openXu"));
        /*
         * ②. 操作符（Operators）
         * 根据响应式函数编程的概念，Subscribers更应该做的事情是“响应”，响应Observable发出的事件，而不是去修改
         * 所以我们应该在某些中间步骤中对"Hello, RxAndroid!"进行变换
         *
         * 操作符就是为了解决对Observable对象的变换的问题，
         * 操作符用于在Observable和最终的Subscriber之间修改Observable发出的事件。
         * RxJava提供了很多很有用的操作符，比如map操作符，就是用来把把一个事件转换为另一个事件的。
         */
        Observable.just("Hello, RxAndroid!")
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String s) {
                        return s + " -openXu";
                    }
                })
                .subscribe(s -> showToast(s));
        //使用lambda可以简化为
        Observable.just("Hello, RxAndroid!")
                .map(s -> s + " -openXu")
                .subscribe(s -> showToast(s));

        /*
         * ③. map操作符进阶
         * map操作符更有趣的一点是它不必返回Observable对象返回的类型;
         * 你可以使用map操作符返回一个发射新的数据类型的observable对象。
         * 比如上面的例子中，subscriber并不关心返回的字符串，而是想要字符串的hash值
         */
        Observable.just("Hello, RxAndroid!")
                .map(new Func1<String, Integer>() {
                    @Override
                    public Integer call(String s) {
                        return s.hashCode();
                    }
                })
                .subscribe(i -> showToast(Integer.toString(i)));
        //我们初始的Observable返回的是字符串，最终的Subscriber收到的却是Integer，使用lambda可以进一步简化代码
        Observable.just("Hello, RxAndroid!")
                .map(s -> s.hashCode())
                .subscribe(i -> showToast(Integer.toString(i)));
        //Subscriber做的事情越少越好，我们再增加一个map操作符
        Observable.just("Hello, RxAndroid!")
                .map(s -> s.hashCode())
                .map(i -> Integer.toString(i))
                .subscribe(s -> showToast(s));

    }








}
