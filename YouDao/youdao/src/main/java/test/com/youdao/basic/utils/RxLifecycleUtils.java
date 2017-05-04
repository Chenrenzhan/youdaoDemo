package test.com.youdao.basic.utils;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.RxLifecycle;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;
import com.trello.rxlifecycle2.android.RxLifecycleAndroid;
import com.trello.rxlifecycle2.components.RxActivity;
import com.trello.rxlifecycle2.components.support.RxFragmentActivity;

import java.security.InvalidParameterException;

import io.reactivex.Flowable;
import io.reactivex.Observable;

/**
 * Created by DW on 2017/5/3.
 */
public class RxLifecycleUtils {

    private static final String TAG = "RxLifecycleUtils";

    public static <T> Observable<T> bindLifecycle(@NonNull final Observable<T> observable, final Object lifecycleObject){
        if(lifecycleObject == null){
            return observable;
        }
        if (lifecycleObject instanceof RxActivity) {
            RxActivity rxActivity = (RxActivity) lifecycleObject;
            return bindOnActivity(observable, rxActivity);
        }

        if (lifecycleObject instanceof RxFragmentActivity) {
            RxFragmentActivity rxActivity = (RxFragmentActivity) lifecycleObject;
            return bindOnActivity(observable, rxActivity);
        }

        if (lifecycleObject instanceof com.trello.rxlifecycle2.components.support.RxFragment) {
            com.trello.rxlifecycle2.components.support.RxFragment rxFragment
                    = (com.trello.rxlifecycle2.components.support.RxFragment) lifecycleObject;
            return bindOnFragment(observable, rxFragment);
        }

        if (lifecycleObject instanceof com.trello.rxlifecycle2.components.RxFragment) {
            com.trello.rxlifecycle2.components.RxFragment rxFragment
                    = (com.trello.rxlifecycle2.components.RxFragment) lifecycleObject;
            return bindOnFragment(observable, rxFragment);
        }

        if (lifecycleObject instanceof com.trello.rxlifecycle2.components.RxDialogFragment) {
            com.trello.rxlifecycle2.components.RxDialogFragment rxFragment
                    = (com.trello.rxlifecycle2.components.RxDialogFragment) lifecycleObject;
            return bindOnDialogFragment(observable, rxFragment);
        }

        if (lifecycleObject instanceof com.trello.rxlifecycle2.components.support.RxDialogFragment) {
            com.trello.rxlifecycle2.components.support.RxDialogFragment rxFragment
                    = (com.trello.rxlifecycle2.components.support.RxDialogFragment) lifecycleObject;
            return bindOnDialogFragment(observable, rxFragment);
        }

        if (lifecycleObject instanceof View) {
            View view = (View) lifecycleObject;
            return bindOnView(observable, view);
        }

        Log.w(TAG, "Type of lifecycleObject is: ["
                + lifecycleObject.getClass().getName()
                + "], which is not supported. You should un-subscribe from the returned Observable object yourself.");

        throw new IllegalArgumentException("lifecycleObject is not supported.");
    }
    
    /**
     * 绑定一个事件源 Observable 到 activity 的生命周期，该函数可以不用调用者管理 Subscription 的退订，activity onDestroy 时将自动销毁。
     *
     * @param observable      事件源Observable
     * @param activity 绑定订阅的生命周期到一个 activity 上
     * @return 事件源Observable
     */
    private static <T> Observable<T> bindOnActivity(@NonNull final Observable<T> observable, final RxFragmentActivity activity) {
        if (activity == null) {
            throw new InvalidParameterException("activity can not be null");
        }

        return observable.compose(
                RxLifecycle.<T, ActivityEvent>bindUntilEvent(
                        activity.lifecycle(),
                        ActivityEvent.DESTROY));
    }

    private <T> Flowable<T> bindOnActivity(@NonNull final Flowable<T> observable, final RxFragmentActivity activity) {
        if (activity == null) {
            throw new InvalidParameterException("activity can not be null");
        }

        return observable.compose(
                RxLifecycle.<T, ActivityEvent>bindUntilEvent(
                        activity.lifecycle(),
                        ActivityEvent.DESTROY));
    }

    /**
     * 绑定一个事件源 Observable 到 activity 的生命周期，该函数可以不用调用者管理 Subscription 的退订，activity onDestroy 时将自动销毁。
     *
     * @param observable      事件源Observable
     * @param activity 绑定订阅的生命周期到一个 activity 上
     * @return 事件源Observable
     */
    private static <T> Observable<T> bindOnActivity(@NonNull final Observable<T> observable, final RxActivity activity) {
        if (activity == null) {
            throw new InvalidParameterException("activity can not be null");
        }

        return observable.compose(RxLifecycle.<T, ActivityEvent>bindUntilEvent(
                activity.lifecycle(),
                ActivityEvent.DESTROY));
    }

    /**
     * 绑定一个事件源 Observable 到 fragment 的生命周期，该函数可以不用调用者管理 Subscription 的退订，fragment onDestroy 时将自动销毁。
     *
     * @param observable      事件源Observable
     * @param fragment 绑定订阅的生命周期到一个 fragment 上
     * @return 事件源Observable
     */
    private static <T> Observable<T> bindOnFragment(@NonNull final Observable<T> observable,
                                                 final com.trello.rxlifecycle2.components.support.RxFragment fragment) {
        if (fragment == null) {
            throw new InvalidParameterException("fragment can not be null");
        }

        return observable.compose(
                RxLifecycle.<T, FragmentEvent>bindUntilEvent(fragment.lifecycle(),
                        FragmentEvent.DESTROY));
    }


    /**
     * 绑定一个事件源 Observable 到 fragment 的生命周期，该函数可以不用调用者管理 Subscription 的退订，fragment onDestroy 时将自动销毁。
     *
     * @param observable      事件源Observable
     * @param fragment 绑定订阅的生命周期到一个 fragment 上
     * @return 事件源Observable
     */
    private static <T> Observable<T> bindOnFragment(@NonNull final Observable<T> observable,
                                                 final com.trello.rxlifecycle2.components.RxFragment fragment) {
        if (fragment == null) {
            throw new InvalidParameterException("fragment can not be null");
        }

        return observable.compose(RxLifecycle.<T, FragmentEvent>bindUntilEvent(fragment.lifecycle(), FragmentEvent.DESTROY));
    }

    /**
     * 绑定一个事件源 Observable 到 fragment 的生命周期，该函数可以不用调用者管理 Subscription 的退订，fragment onDestroy 时将自动销毁。
     *
     * @param observable         事件源Observable
     * @param dlgFragment 绑定订阅的生命周期到一个 DialogFragment 上
     * @return 事件源Observable
     */
    private static <T> Observable<T> bindOnDialogFragment(@NonNull final Observable<T> observable,
                                                       final com.trello.rxlifecycle2.components.RxDialogFragment dlgFragment) {
        if (dlgFragment == null) {
            throw new InvalidParameterException("dlgFragment can not be null");
        }

        return observable.compose(RxLifecycle.<T, FragmentEvent>bindUntilEvent(dlgFragment.lifecycle(), FragmentEvent.DESTROY));
    }

    private static <T> Observable<T> bindOnDialogFragment(@NonNull final Observable<T> observable,
                                                       final com.trello.rxlifecycle2.components.support.RxDialogFragment dlgFragment) {
        if (dlgFragment == null) {
            throw new InvalidParameterException("dlgFragment can not be null");
        }

        return observable.compose(
                RxLifecycle.<T, FragmentEvent>bindUntilEvent(
                        dlgFragment.lifecycle(),
                        FragmentEvent.DESTROY));
    }

    /**
     * 绑定一个事件源 Observable 到 View 的生命周期，该函数可以不用调用者管理 Subscription 的退订，在view detached时将自动销毁。
     * 注意：该函数必须在UI现场调用
     *
     * @param observable  事件源Observable
     * @param view 绑定订阅的生命周期到一个 view 上
     * @return 事件源Observable
     */
    private static <T> Observable<T> bindOnView(@NonNull final Observable<T> observable, final View view) {
        if (view == null) {
            throw new InvalidParameterException("view can not be null");
        }

        return observable.compose(RxLifecycleAndroid.<T>bindView(view));
    }
}
