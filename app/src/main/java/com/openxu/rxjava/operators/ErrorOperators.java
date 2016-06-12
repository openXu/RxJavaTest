package com.openxu.rxjava.operators;

import android.util.Log;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * author : openXu
 * created time : 16/6/6 下午7:34
 * blog : http://blog.csdn.net/xmxkf
 * github : http://blog.csdn.net/xmxkf
 * class name : ErrorOperators
 *
 * discription :错误处理操作符
 *
 * 很多操作符可用于对Observable发射的onError通知做出响应或者从错误中恢复，例如，你可以：
 *
 * 吞掉这个错误，切换到一个备用的Observable继续发射数据
 * 吞掉这个错误然后发射默认值
 * 吞掉这个错误并立即尝试重启这个Observable
 * 吞掉这个错误，在一些回退间隔后重启这个Observable
 * 这是操作符列表：

 * onErrorReturn( ) — 指示Observable在遇到错误时发射一个特定的数据
 * onErrorResumeNext( ) — 指示Observable在遇到错误时发射一个数据序列
 * onExceptionResumeNext( ) — instructs an Observable to continue emitting items after it encounters an exception (but not another variety of throwable)指示Observable遇到错误时继续发射数据
 * retry( ) — 指示Observable遇到错误时重试
 * retryWhen( ) — 指示Observable遇到错误时，将错误传递给另一个Observable来决定是否要重新给订阅这个Observable
 */
public class ErrorOperators extends OperatorsBase {


    /**
     * Catch操作符拦截原始Observable的onError通知，将它替换为其它的数据项或数据序列，
     * 让产生的Observable能够正常终止或者根本不终止。
     *
     * 相当于try/catch，捕获到错误然后再catch中做一些事情。
     */
    private void op_Catch(TextView textView) {

        /*
         * ①.onErrorReturn：
         * 返回一个原有Observable行为的新Observable镜像，
         * 后者会忽略前者的onError调用，不会将错误传递给观察者，
         * 作为替代，它会发发射一个特殊的项并调用观察者的onCompleted方法
         */
        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                for(int i = 0;i < 10; i++){
                    if(i>3){
                        //会忽略onError调用，不会将错误传递给观察者
                        subscriber.onError(new Throwable("i太大了"));
                    }
                    subscriber.onNext(i);
                }
                subscriber.onCompleted();
            }
        }).onErrorReturn(new Func1<Throwable, Integer>() {
            //作为替代，它会发发射一个特殊的项并调用观察者的onCompleted方法。
            @Override
            public Integer call(Throwable throwable) {
                return 10;
            }
        }).subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {
                Log.v(TAG, "①onErrorReturn(Func1)->onCompleted");
            }
            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "①onErrorReturn(Func1)->onError:"+e.getMessage());
            }
            @Override
            public void onNext(Integer integer) {
                Log.v(TAG, "①onErrorReturn(Func1)->onNext:"+integer);
            }
        });

        /*
         * ②.onErrorResumeNext(Observable):
         * 当原Observable发射onError消息时，会忽略onError消息，不会传递给观察者；
         * 然后它会开始另一个备用的Observable，继续发射数据
         */
        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                for(int i = 0;i < 10; i++){
                    if(i>3){
                        //会忽略onError调用，不会将错误传递给观察者
                        subscriber.onError(new Throwable("i太大了"));
                    }
                    subscriber.onNext(i);
                }
                subscriber.onCompleted();
            }
        }).onErrorResumeNext(Observable.create(new Observable.OnSubscribe<Integer>() {

            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                for(int i = 10;i < 13; i++){
                    subscriber.onNext(i);
                }
                subscriber.onCompleted();
            }
        })).subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {
                Log.v(TAG, "②onErrorResumeNext(Observable)->onCompleted");
            }
            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "②onErrorResumeNext(Observable)->onError:"+e.getMessage());
            }
            @Override
            public void onNext(Integer integer) {
                Log.v(TAG, "②onErrorResumeNext(Observable)->onNext:"+integer);
            }
        });

        /*
         * ③.onErrorResumeNext(Func1):
         * 和onErrorResumeNext(Observable)相似，但他能截取到原Observable的onError消息
         */
        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                for(int i = 0;i < 10; i++){
                    if(i>3){
                        //会忽略onError调用，不会将错误传递给观察者
                        subscriber.onError(new Throwable("i太大了"));
                    }
                    subscriber.onNext(i);
                }
                subscriber.onCompleted();
            }
        }).onErrorResumeNext(new Func1<Throwable, Observable<? extends Integer>>() {
            @Override
            public Observable<? extends Integer> call(Throwable throwable) {
                //throwable就是原Observable发射的onError消息中的Throwable对象
                Log.e(TAG, "③onErrorResumeNext(Func1)->throwable:"+throwable.getMessage());
                //如果原Observable发射了onError消息，将会开启下面的Observable
                return Observable.create(new Observable.OnSubscribe<Integer>() {
                    @Override
                    public void call(Subscriber<? super Integer> subscriber) {
                        for(int i = 100;i < 103; i++){
                            subscriber.onNext(i);
                        }
                        subscriber.onCompleted();
                    }
                });
            }
        }).subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {
                Log.v(TAG, "③onErrorResumeNext(Func1)->onCompleted");
            }
            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "③onErrorResumeNext(Func1)->onError:"+e.getMessage());
            }
            @Override
            public void onNext(Integer integer) {
                Log.v(TAG, "③onErrorResumeNext(Func1)->onNext:"+integer);
            }
        });

        /*
         * ④.onExceptionResumeNext：
         *    和onErrorResumeNext类似，可以说是onErrorResumeNext的特例，
         *    区别是如果onError收到的Throwable不是一个Exception，它会将错误传递给观察者的onError方法，不会使用备用的Observable。
         */
        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                for(int i = 0;i < 10; i++){
                    if(i>3){
                        //如果不是Exception，错误会传递给观察者，不会开启备用Observable
                        //subscriber.onError(new Throwable("i太大了"));
                        //如果Exception，不会将错误传递给观察者，并会开启备用Observable
                        subscriber.onError(new Exception("i太大了哦哦哦"));
                    }
                    subscriber.onNext(i);
                }
                subscriber.onCompleted();
            }
        }).onExceptionResumeNext(Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                for(int i = 10;i < 13; i++){
                    subscriber.onNext(i);
                }
                subscriber.onCompleted();
            }
        })).subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {
                Log.v(TAG, "④onExceptionResumeNext(Observable)->onCompleted");
            }
            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "④onExceptionResumeNext(Observable)->onError:"+e.getClass().getSimpleName()+":"+e.getMessage());
            }
            @Override
            public void onNext(Integer integer) {
                Log.v(TAG, "④onExceptionResumeNext(Observable)->onNext:"+integer);
            }
        });

    }

    /**
     * 尝试重新订阅Observable
     */
    private void op_Retry(TextView textView) {
        /**
         * ①. retry()
         *     无限次尝试重新订阅
         */
 /*       Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                for(int i = 0; i<3; i++){
                    if(i==1){
                        Log.v(TAG, "①retry()->onError");
                        subscriber.onError(new RuntimeException("always fails"));
                    }else{
                        subscriber.onNext(i);
                    }
                }
            }
        }).retry()    //无限次尝试重新订阅
        .subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {
                Log.v(TAG, "①retry()->onCompleted");
            }
            @Override
            public void onError(Throwable e) {
                Log.v(TAG, "①retry()->onError"+e.getMessage());
            }
            @Override
            public void onNext(Integer i) {
                Log.v(TAG, "①retry()->onNext"+i);
            }
        });*/

        /**
         * ②. retry(count)
         *     最多2次尝试重新订阅
         */
        /*
       Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                for(int i = 0; i<3; i++){
                    if(i==1){
                        Log.v(TAG, "②retry(count)->onError");
                        subscriber.onError(new RuntimeException("always fails"));
                    }else{
                        subscriber.onNext(i);
                    }
                }
            }
        }).retry(2)    //最多尝试2次重新订阅
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        Log.v(TAG, "②retry(count)->onCompleted");
                    }
                    @Override
                    public void onError(Throwable e) {
                        Log.v(TAG, "②retry(count)->onError"+e.getMessage());
                    }
                    @Override
                    public void onNext(Integer i) {
                        Log.v(TAG, "②retry(count)->onNext"+i);
                    }
                });

        /**
         * ③. retry(Func2)
         */
        /*
        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                for(int i = 0; i<3; i++){
                    if(i==1){
                        Log.v(TAG, "③retry(Func2)->onError");
                        subscriber.onError(new RuntimeException("always fails"));
                    }else{
                        subscriber.onNext(i);
                    }
                }
            }
        }).retry(new Func2<Integer, Throwable, Boolean>() {
            @Override
            public Boolean call(Integer integer, Throwable throwable) {
                Log.v(TAG, "③发生错误了："+throwable.getMessage()+",第"+integer+"次重新订阅");
                if(integer>2){
                    return false;//不再重新订阅
                }
                //此处也可以通过判断throwable来控制不同的错误不同处理
                return true;
            }
        }).subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {
                Log.v(TAG, "③retry(Func2)->onCompleted");
            }
            @Override
            public void onError(Throwable e) {
                Log.v(TAG, "③retry(Func2)->onError"+e.getMessage());
            }
            @Override
            public void onNext(Integer i) {
                Log.v(TAG, "③retry(Func2)->onNext"+i);
            }
        });

        */


        /********retryWhen*********/
        /**
         * retryWhen和retry类似，区别是，retryWhen将onError中的Throwable传递给一个函数，
         * 这个函数产生另一个Observable，retryWhen观察它的结果再决定是不是要重新订阅原始的Observable。
         * 如果这个Observable发射了一项数据，它就重新订阅，
         * 如果这个Observable发射的是onError通知，它就将这个通知传递给观察者然后终止。
         */
        Observable.create((Subscriber<? super String> s) -> {
            System.out.println("subscribing");
            s.onError(new RuntimeException("always fails"));
        }).retryWhen(attempts -> {
            return attempts.zipWith(Observable.range(1, 3), (n, i) -> i).flatMap(i -> {
                System.out.println("delay retry by " + i + " second(s)");
                return Observable.timer(i, TimeUnit.SECONDS);
            });
        }).toBlocking().forEach(System.out::println);
    }


}
