package test.com.youdao.basic.rxbus;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.jakewharton.rxrelay2.PublishRelay;
import com.jakewharton.rxrelay2.Relay;
import com.trello.rxlifecycle2.RxLifecycle;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;
import com.trello.rxlifecycle2.android.RxLifecycleAndroid;
import com.trello.rxlifecycle2.components.RxActivity;
import com.trello.rxlifecycle2.components.support.RxFragmentActivity;

import java.lang.ref.SoftReference;
import java.security.InvalidParameterException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import test.com.youdao.basic.utils.RxLifecycleUtils;


/**
 * 基于Rx的事件总线
 * 内置了一个默认实例便于全局使用，也可通过create创建新实例在自定义范围内使用
 * Action 总线使用 {@link RxBusAction#action} 字符串来区分，可避免 RxBus 事件每一次都要写一个类
 * Sticky(粘性) 事件只指事件消费者在事件发布之后才注册的也能接收到该事件的特殊类型
 * <p/>
 */
public class RxBus {
    private static final String TAG = "RxBus";
    private final static RxBus mDefault = new RxBus(0, "Default");
    private final Relay<Object> mRelay;
    private final Relay<Object> mActionRelay;

    private final int mMaxBufferSize;
    private final String mName;
    
    // 粘性事件，只保留同一个事件的最后一个
    private final ConcurrentHashMap<Class<?>, SoftReference<Object>> mStickyEventMap = new ConcurrentHashMap<Class<?>, SoftReference<Object>>();

    public String getName() {
        return mName;
    }

    @Override
    public String toString() {
        return "RxBus{" +
                "MaxBufferSize=" + mMaxBufferSize +
                ", Name='" + mName + '\'' +
                '}';
    }

    private RxBus(final int maxBufferSize, @NonNull final String name) {
        mMaxBufferSize = maxBufferSize;
        mName = name;
        mRelay = PublishRelay.create().toSerialized();
        mActionRelay = PublishRelay.create().toSerialized();
    }

    /**
     * 获得默认总线实例
     *
     * @return
     */
    public static RxBus getDefault() {
        return mDefault;
    }

    public static RxBus create(final int maxBufferSize,
                               @NonNull final String name) {
        return new RxBus(maxBufferSize, name);
    }

    /**
     * 向总线填入一个事件对象
     *
     * @param event
     */
    public void post(@NonNull Object event) {
        mRelay.accept(event);
    }

    /**
     * 向总线填入一个事件对象，延迟发送
     *
     * @param event
     * @param milliSecs 延迟毫秒时间
     */
    public void postDelay(@NonNull final Object event, long milliSecs) {
        Observable.timer(milliSecs, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Long aLong) throws Exception {
                        mRelay.accept(event);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception {
                        Log.e(TAG, "Post Delay failed.", throwable);
                    }
                });
    }

    /**
     * 向 Action 总线填入一个 RxBusAction 事件对象
     * @param action
     */
    public void postAction(@NonNull RxBusAction action){
        mActionRelay.accept(action);
    }

    /**
     * 向 Action 总线填入一个 RxBusAction 事件对象
     * @param action
     * @param milliSecs
     */
    public void postActionDelay(@NonNull final RxBusAction action, long milliSecs){
        Observable.timer(milliSecs, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Long aLong) throws Exception {
                        mActionRelay.accept(action);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception {
                        Log.e(TAG, "Post Delay failed.", throwable);
                    }
                });
    }

    /**
     * 向总线填入一个粘性事件对象
     * @param event
     */
    public void postSticky(@NonNull Object event){
        mStickyEventMap.put(event.getClass(), new SoftReference<Object>(event));
        post(event);
    }

    /**
     * 向总线填入一个粘性事件对象，延迟发送
     *
     * @param event
     * @param milliSecs 延迟毫秒时间
     */
    public void postStickyDelay(@NonNull final Object event, long milliSecs) {
        Observable.timer(milliSecs, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Long aLong) throws Exception {
                        mStickyEventMap.put(event.getClass(), new SoftReference<Object>(event));
                        mRelay.accept(event);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception {
                        Log.e(TAG, "Post Delay failed.", throwable);
                    }
                });
    }
    
    /**
     * 过滤事件类型，获得一个可订阅的事件源
     * 注意：订阅该事件源后必须自行调用 unsubscribe 进行释放，避免内存泄露
     *
     * @param cls 要过滤的事件对象类型
     * @return 事件源 Observable
     */
    public <T> Observable<T> register(@NonNull final Class<T> cls) {
        if (mMaxBufferSize > 0) {
            return mRelay.toFlowable(BackpressureStrategy.BUFFER)
                    .filter(new Predicate<Object>() {
                        @Override
                        public boolean test(@io.reactivex.annotations.NonNull Object o) throws Exception {
                            return cls.isInstance(o);
                        }
                    })
                    .onBackpressureBuffer(mMaxBufferSize)
                    .cast(cls).toObservable();
        }
        return mRelay
                .filter(new Predicate<Object>() {
                    @Override
                    public boolean test(@io.reactivex.annotations.NonNull Object o) throws Exception {
                        return cls.isInstance(o);
                    }
                }).cast(cls);
    }

    /**
     * 过滤事件类型，获得一个可订阅的事件源
     * 使用该函数可以不用调用者管理 Subscription 的退订，在 activity onDestroy 时将自动销毁。
     *
     * @param cls             要过滤的事件载体类型
     * @param lifecycleObject 绑定订阅的生命周期到一个支持生命周期的对象上,这个对象的类型可以是:
     *                        RxActivity/RxFragmentActivity/RxFragment/RxDialogFragment/View
     * @return 事件源Observable
     */
    public <T> Observable<T> register(@NonNull final Class<T> cls, final Object lifecycleObject) {
        if (lifecycleObject == null) {
            throw new InvalidParameterException("lifecycleObject can not be null");
        }

        Log.v(TAG, "Register for class: " + cls.getName() + ", lifecycleObject type: " + lifecycleObject.getClass().getName());

        return register(register(cls), lifecycleObject);
    }

    /**
     * 过滤事件类型，获得一个可订阅的事件源
     * 注意：订阅该事件源后必须自行调用 unsubscribe 进行释放，避免内存泄露
     *
     * @param action 要过滤的 action 事件类型
     * @return 事件源 Observable
     */
    public Observable<RxBusAction> registerAction(@NonNull final String action) {
        if (mMaxBufferSize > 0) {
            return mActionRelay.toFlowable(BackpressureStrategy.BUFFER)
                    .filter(new Predicate<Object>() {
                        @Override
                        public boolean test(@io.reactivex.annotations.NonNull Object o) throws Exception {
                            return o instanceof RxBusAction && ((RxBusAction) o).action.equals(action);
                        }
                    })
                    .onBackpressureBuffer(mMaxBufferSize)
                    .cast(RxBusAction.class).toObservable();
        }
        return mActionRelay
                .filter(new Predicate<Object>() {
                    @Override
                    public boolean test(@io.reactivex.annotations.NonNull Object o) throws Exception {
                        return o instanceof RxBusAction && ((RxBusAction) o).action.equals(action);
                    }
                }).cast(RxBusAction.class);
    }

    /**
     * 过滤事件类型，获得一个可订阅的事件源
     * 使用该函数可以不用调用者管理 Subscription 的退订，在 activity onDestroy 时将自动销毁。
     *
     * @param action             要过滤的 action 事件载体类型
     * @param lifecycleObject 绑定订阅的生命周期到一个支持生命周期的对象上,这个对象的类型可以是:
     *                        RxActivity/RxFragmentActivity/RxFragment/RxDialogFragment/View
     * @return 事件源Observable
     */
    public Observable<RxBusAction> registerAction(@NonNull final String action, final Object lifecycleObject) {
        if (lifecycleObject == null) {
            throw new InvalidParameterException("lifecycleObject can not be null");
        }

        Log.v(TAG, "Register for action: " + action + ", lifecycleObject type: " + lifecycleObject.getClass().getName());

        return register(registerAction(action), lifecycleObject);
    }

    /**
     * 注册粘性事件，如果该事件已经发送之后再订阅，则该事件仍然能够收到通知
     * 注意：订阅该事件源后必须自行调用 unsubscribe 进行释放，避免内存泄露
     *
     * @param cls 要过滤的事件对象类型
     * @return 事件源 Observable
     */
    public <T> Observable<T> registerSticky(@NonNull final Class<T> cls) {
        Observable<T> observable = register(cls);
        final SoftReference<Object> event = mStickyEventMap.get(cls);
        if(event != null && event.get() != null){
            return observable.mergeWith(new ObservableSource<T>(){
                @Override
                public void subscribe(@io.reactivex.annotations.NonNull Observer<? super T> observer) {
                    observer.onNext(cls.cast(event.get()));
                }
            });
        }
        return observable;
    }

    /**
     * 注册粘性事件，如果该事件已经发送之后再订阅，则该事件仍然能够收到通知
     * 使用该函数可以不用调用者管理 Subscription 的退订，在 activity onDestroy 时将自动销毁。
     *
     * @param cls             要过滤的事件载体类型
     * @param lifecycleObject 绑定订阅的生命周期到一个支持生命周期的对象上,这个对象的类型可以是:
     *                        RxActivity/RxFragmentActivity/RxFragment/RxDialogFragment/View
     * @return 事件源Observable
     */
    public <T> Observable<T> registerSticky(@NonNull final Class<T> cls, final Object lifecycleObject) {
        Observable<T> observable = register(cls, lifecycleObject);
        final SoftReference<Object> event = mStickyEventMap.get(cls);
        if(event != null && event.get() != null){
            return observable.mergeWith(new ObservableSource<T>(){
                @Override
                public void subscribe(@io.reactivex.annotations.NonNull Observer<? super T> observer) {
                    observer.onNext(cls.cast(event.get()));
                }
            });
        }
        return observable;
    }

    /**
     * 绑定一个事件源 Observable 到 xxx 的生命周期，该函数可以不用调用者管理 Subscription 的退订，xxx onDestroy 时将自动销毁。
     *
     * @param observable             要过滤的事件载体类型
     * @param lifecycleObject 绑定订阅的生命周期到一个支持生命周期的对象上,这个对象的类型可以是:
     *                        RxActivity/RxFragmentActivity/RxFragment/RxDialogFragment/View
     * @return 事件源Observable
     */
    private <T> Observable<T> register(@NonNull Observable<T> observable, final Object lifecycleObject) {
        return RxLifecycleUtils.bindLifecycle(observable, lifecycleObject);
//        if (lifecycleObject instanceof RxActivity) {
//            RxActivity rxActivity = (RxActivity) lifecycleObject;
//            return registerOnActivity(observable, rxActivity);
//        }
//
//        if (lifecycleObject instanceof RxFragmentActivity) {
//            RxFragmentActivity rxActivity = (RxFragmentActivity) lifecycleObject;
//            return registerOnActivity(observable, rxActivity);
//        }
//
//        if (lifecycleObject instanceof com.trello.rxlifecycle2.components.support.RxFragment) {
//            com.trello.rxlifecycle2.components.support.RxFragment rxFragment
//                    = (com.trello.rxlifecycle2.components.support.RxFragment) lifecycleObject;
//            return registerOnFragment(observable, rxFragment);
//        }
//
//        if (lifecycleObject instanceof com.trello.rxlifecycle2.components.RxFragment) {
//            com.trello.rxlifecycle2.components.RxFragment rxFragment
//                    = (com.trello.rxlifecycle2.components.RxFragment) lifecycleObject;
//            return registerOnFragment(observable, rxFragment);
//        }
//
//        if (lifecycleObject instanceof com.trello.rxlifecycle2.components.RxDialogFragment) {
//            com.trello.rxlifecycle2.components.RxDialogFragment rxFragment
//                    = (com.trello.rxlifecycle2.components.RxDialogFragment) lifecycleObject;
//            return registerOnDialogFragment(observable, rxFragment);
//        }
//
//        if (lifecycleObject instanceof com.trello.rxlifecycle2.components.support.RxDialogFragment) {
//            com.trello.rxlifecycle2.components.support.RxDialogFragment rxFragment
//                    = (com.trello.rxlifecycle2.components.support.RxDialogFragment) lifecycleObject;
//            return registerOnDialogFragment(observable, rxFragment);
//        }
//
//        if (lifecycleObject instanceof View) {
//            View view = (View) lifecycleObject;
//            return registerOnView(observable, view);
//        }
//
//        Log.w(TAG, "Type of lifecycleObject is: ["
//                + lifecycleObject.getClass().getName()
//                + "], which is not supported. You should un-subscribe from the returned Observable object yourself.");
//
//        throw new IllegalArgumentException("lifecycleObject is not supported.");
    }
//
//
//    /**
//     * 绑定一个事件源 Observable 到 activity 的生命周期，该函数可以不用调用者管理 Subscription 的退订，activity onDestroy 时将自动销毁。
//     *
//     * @param observable      事件源Observable
//     * @param activity 绑定订阅的生命周期到一个 activity 上
//     * @return 事件源Observable
//     */
//    private <T> Observable<T> registerOnActivity(@NonNull final Observable<T> observable, final RxFragmentActivity activity) {
//        if (activity == null) {
//            throw new InvalidParameterException("activity can not be null");
//        }
//
//        return observable.compose(
//                RxLifecycle.<T, ActivityEvent>bindUntilEvent(
//                        activity.lifecycle(),
//                        ActivityEvent.DESTROY));
//    }
//
//    /**
//     * 绑定一个事件源 Observable 到 activity 的生命周期，该函数可以不用调用者管理 Subscription 的退订，activity onDestroy 时将自动销毁。
//     *
//     * @param observable      事件源Observable
//     * @param activity 绑定订阅的生命周期到一个 activity 上
//     * @return 事件源Observable
//     */
//    private <T> Observable<T> registerOnActivity(@NonNull final Observable<T> observable, final RxActivity activity) {
//        if (activity == null) {
//            throw new InvalidParameterException("activity can not be null");
//        }
//
//        return observable.compose(RxLifecycle.<T, ActivityEvent>bindUntilEvent(
//                activity.lifecycle(),
//                ActivityEvent.DESTROY));
//    }
//
//    /**
//     * 绑定一个事件源 Observable 到 fragment 的生命周期，该函数可以不用调用者管理 Subscription 的退订，fragment onDestroy 时将自动销毁。
//     *
//     * @param observable      事件源Observable
//     * @param fragment 绑定订阅的生命周期到一个 fragment 上
//     * @return 事件源Observable
//     */
//    private <T> Observable<T> registerOnFragment(@NonNull final Observable<T> observable,
//                                                final com.trello.rxlifecycle2.components.support.RxFragment fragment) {
//        if (fragment == null) {
//            throw new InvalidParameterException("fragment can not be null");
//        }
//
//        return observable.compose(
//                RxLifecycle.<T, FragmentEvent>bindUntilEvent(fragment.lifecycle(),
//                        FragmentEvent.DESTROY));
//    }
//
//
//    /**
//     * 绑定一个事件源 Observable 到 fragment 的生命周期，该函数可以不用调用者管理 Subscription 的退订，fragment onDestroy 时将自动销毁。
//     *
//     * @param observable      事件源Observable
//     * @param fragment 绑定订阅的生命周期到一个 fragment 上
//     * @return 事件源Observable
//     */
//    private <T> Observable<T> registerOnFragment(@NonNull final Observable<T> observable,
//                                                final com.trello.rxlifecycle2.components.RxFragment fragment) {
//        if (fragment == null) {
//            throw new InvalidParameterException("fragment can not be null");
//        }
//
//        return observable.compose(RxLifecycle.<T, FragmentEvent>bindUntilEvent(fragment.lifecycle(), FragmentEvent.DESTROY));
//    }
//
//    /**
//     * 绑定一个事件源 Observable 到 fragment 的生命周期，该函数可以不用调用者管理 Subscription 的退订，fragment onDestroy 时将自动销毁。
//     *
//     * @param observable         事件源Observable
//     * @param dlgFragment 绑定订阅的生命周期到一个 DialogFragment 上
//     * @return 事件源Observable
//     */
//    private <T> Observable<T> registerOnDialogFragment(@NonNull final Observable<T> observable,
//                                                      final com.trello.rxlifecycle2.components.RxDialogFragment dlgFragment) {
//        if (dlgFragment == null) {
//            throw new InvalidParameterException("dlgFragment can not be null");
//        }
//
//        return observable.compose(RxLifecycle.<T, FragmentEvent>bindUntilEvent(dlgFragment.lifecycle(), FragmentEvent.DESTROY));
//    }
//
//    private <T> Observable<T> registerOnDialogFragment(@NonNull final Observable<T> observable,
//                                                      final com.trello.rxlifecycle2.components.support.RxDialogFragment dlgFragment) {
//        if (dlgFragment == null) {
//            throw new InvalidParameterException("dlgFragment can not be null");
//        }
//
//        return observable.compose(
//                RxLifecycle.<T, FragmentEvent>bindUntilEvent(
//                        dlgFragment.lifecycle(),
//                        FragmentEvent.DESTROY));
//    }
//
//    /**
//     * 绑定一个事件源 Observable 到 View 的生命周期，该函数可以不用调用者管理 Subscription 的退订，在view detached时将自动销毁。
//     * 注意：该函数必须在UI现场调用
//     *
//     * @param observable  事件源Observable
//     * @param view 绑定订阅的生命周期到一个 view 上
//     * @return 事件源Observable
//     */
//    private <T> Observable<T> registerOnView(@NonNull final Observable<T> observable, final View view) {
//        if (view == null) {
//            throw new InvalidParameterException("view can not be null");
//        }
//
//        return observable.compose(RxLifecycleAndroid.<T>bindView(view));
//    }

    /**
     * 获取一个已经发生的粘性事件对象
     * @param cls
     * @param <T>
     * @return
     */
    public <T> T getStickyEvent(Class<T> cls){
        SoftReference<Object> event = mStickyEventMap.get(cls);
        if(event != null && event.get() != null){
            return cls.cast(mStickyEventMap.get(cls));
        }
        return null;
    }

    /**
     * 移除一个已经发生的粘性事件对象
     * @param cls
     * @param <T>
     * @return
     */
    public <T> T removeStickyEvent(Class<T> cls){
        SoftReference<Object> event = mStickyEventMap.remove(cls);
        if(event != null && event.get() != null){
            return cls.cast(mStickyEventMap.get(cls));
        }
        return null;
    }

    /**
     * 移除所有的粘性事件对象
     */
    public void clearStickyEvents(){
        mStickyEventMap.clear();
    }
}
