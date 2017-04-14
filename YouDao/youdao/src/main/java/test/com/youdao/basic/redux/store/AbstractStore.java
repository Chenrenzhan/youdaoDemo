package test.com.youdao.basic.redux.store;

import android.support.annotation.NonNull;
import android.util.Log;

import com.jakewharton.rxrelay2.PublishRelay;
import com.jakewharton.rxrelay2.Relay;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import test.com.youdao.basic.log.MLog;
import test.com.youdao.basic.redux.Action;
import test.com.youdao.basic.redux.Middleware;
import test.com.youdao.basic.redux.Reducer;
import test.com.youdao.basic.redux.StateChangedEventArgs;
import test.com.youdao.basic.redux.StateChangedListener;
import test.com.youdao.basic.redux.StateChangedListener2;
import test.com.youdao.basic.redux.Store;

/**
 * Created by ruoshili on 2/16/2017.
 * <p>
 * Store的泛型实现，已经实现了所有的Store的功能，它的子类只需要继承它，并特化TState即可
 */
public abstract class AbstractStore<TState extends State> implements Store<TState> {
    private static final String TAG = "AbstractStore";

    private TState mState;
    private List<Reducer<TState, ? extends Action>> mReducers = Collections.emptyList();
    private List<Middleware> mMiddlewareList = Collections.emptyList();


    protected final Object mReduceSyncRoot = new Object();
    protected final Object mMiddlewareSyncRoot = new Object();

    @Override
    public TState getState() {
        if (mState == null) {
            Log.e(TAG, "mState is null");
        }
        return mState;
    }

    private final Relay<StateChangedEventArgs<TState>> mActionRelay = PublishRelay.create();

    private final Consumer<Throwable> mOnError = new Consumer<Throwable>() {
        @Override
        public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception {
            Log.e(TAG, "AbstractStore onError", throwable);
        }
    };


    /**
     * 分发动作。在这里寻找合适的reducer处理action
     *
     * @param action
     * @param <TAction>
     * @return 参数中的action是否被处理了，如果没有找到相应的reducer，则说明未被处理
     */
    @Override
    public <TAction extends Action> boolean dispatch(@NonNull final TAction action) {
        executeMiddleware(action);

        // 目前由于reducer做的事情比较简单，只是创建对象和修改对象状态，就在调用者线程执行了
        final boolean shouldFireStateChangedEvent;
        final TState newState;
        synchronized (mReduceSyncRoot) {
            final TState originalState = mState;

            for (Reducer<TState, ? extends Action> r : mReducers) {
                // 对比类型，如果匹配才调用
                if (action.getClass().equals(r.getActionClass())) {
                    //noinspection unchecked
                    mState = ((Reducer<TState, TAction>) r).reduce(action, mState);
                    //noinspection ConstantConditions
                    if (mState == null) {
                        MLog.error(TAG, "dispatch action: %s, reducer return null, restore to prev state.");
                        mState = originalState;
                    }
                }
            }
            // 只有状态发生了改变才发出通知
            shouldFireStateChangedEvent = originalState != mState;
            // 在同步区域内，保证mState不会被其他线程意外的改变
            newState = mState;
        }
        if (shouldFireStateChangedEvent) {
            mActionRelay.accept(new StateChangedEventArgs<>(action, newState));
        }
        return shouldFireStateChangedEvent;
    }

    private <TAction extends Action> void executeMiddleware(@NonNull final TAction action) {
        if (mMiddlewareList.size() > 0) {
            // 使用 middleware 处理副作用，Middleware需要自己处理异步。
            // 支持多个middleware处理同一个action，比如可以一个写数据库，一个发送http请求
            synchronized (mMiddlewareSyncRoot) {
                Observable.fromIterable(mMiddlewareList)
                        .filter(new Predicate<Middleware>() {
                            @Override
                            public boolean test(@io.reactivex.annotations.NonNull
                                                        Middleware middleware) throws Exception {
                                return middleware.canHandlerAction(action);
                            }
                        })
                        .flatMap(new Function<Middleware, ObservableSource<? extends Action>>() {
                            @Override
                            public ObservableSource<? extends Action> apply(@io.reactivex.annotations.NonNull
                                                                                    Middleware middleware) throws Exception {
                                //noinspection unchecked
                                return middleware.process(action);
                            }
                        })
                        .subscribe(new Observer<Action>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(Action action) {
                                dispatch(action);
                            }

                            @Override
                            public void onError(Throwable e) {
                                MLog.error(TAG, "executeMiddleware failed.", e);
                            }

                            @Override
                            public void onComplete() {

                            }
                        });
            }
        }
    }

    @Override
    public Observable<StateChangedEventArgs<TState>> getObservable() {
        return mActionRelay.toSerialized();
    }

    @Override
    public Disposable subscribe(
            @NonNull final StateChangedListener<TState> stateChangedListener) {
        Observable<StateChangedEventArgs<TState>> observable = mActionRelay;

        if (stateChangedListener instanceof StateChangedListener2) {
            StateChangedListener2<TState> listener2 = (StateChangedListener2<TState>) stateChangedListener;

            final List<Class<? extends Action>> interestedActionTypes = listener2.getInterestedActionTypes();

            if (interestedActionTypes != null && interestedActionTypes.size() > 0) {
                observable = observable.filter(new Predicate<StateChangedEventArgs<TState>>() {
                    @Override
                    public boolean test(@io.reactivex.annotations.NonNull
                                                StateChangedEventArgs<TState> eventArgs) throws Exception {
                        return interestedActionTypes.contains(eventArgs.action.getClass());
                    }
                });
            }
        }
        return observable.subscribe(new Consumer<StateChangedEventArgs<TState>>() {
            @Override
            public void accept(@io.reactivex.annotations.NonNull
                                       StateChangedEventArgs<TState> eventArgs) throws Exception {
                try {
                    stateChangedListener.onStateChanged(eventArgs);
                } catch (Throwable ignore) {
                    Log.e(TAG, "onStateChanged failed.", ignore);
                }
            }
        }, mOnError);
    }

    @SafeVarargs
    public final void init(@NonNull final TState initState,
                           final Reducer<TState, ? extends Action>... reducers) {
        init(initState,
                Collections.<Middleware>emptyList(),
                Arrays.asList(reducers));
    }

    public final void init(@NonNull final TState initState,
                           final List<Middleware> middlewareList,
                           final List<Reducer<TState, ? extends Action>> reducers) {
        //noinspection ConstantConditions
        if (initState == null) {
            throw new NullPointerException("initState is null");
        }
        mState = initState;
        mMiddlewareList = Collections.unmodifiableList(middlewareList);
        mReducers = Collections.unmodifiableList(reducers);
    }

}
