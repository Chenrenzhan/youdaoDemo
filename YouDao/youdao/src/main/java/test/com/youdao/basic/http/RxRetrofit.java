package test.com.youdao.basic.http;

import android.support.annotation.NonNull;
import android.util.Log;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import org.reactivestreams.Publisher;

import java.lang.ref.SoftReference;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.FlowableTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiPredicate;
import io.reactivex.functions.Cancellable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.internal.schedulers.IoScheduler;
import io.reactivex.internal.util.AppendOnlyLinkedArrayList;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import test.com.youdao.basic.log.MLog;
import test.com.youdao.basic.utils.RxLifecycleUtils;

/**
 * Created by DW on 2017/4/28.
 */
public class RxRetrofit {
    private static final String TAG = "RxRetrofit";
    private final static ConcurrentHashMap<String, SoftReference<ApiService>> mApiServices = new ConcurrentHashMap<String, SoftReference<ApiService>>();
    
    private static ApiService getApiService(String baseUrl) throws InvalidUrlException {
        if(!HttpUtils.isValidUrl(baseUrl)){
            throw new InvalidUrlException("invalid base url: " + baseUrl);
        }
        SoftReference<ApiService> service;
        service = mApiServices.get(baseUrl);
        if(service == null || service.get() == null){
            service = new SoftReference<ApiService>(createService(ApiService.class, baseUrl));
            mApiServices.put(baseUrl, service);
        }
        return service.get();
    }
    
    public static ApiService getDefaultService(String baseUrl) throws InvalidUrlException {
        return getApiService(baseUrl);
    }
    
    public static <T> T getService(Class<T> service, String baseUrl) throws InvalidUrlException {
        if(!HttpUtils.isValidUrl(baseUrl)){
            throw new InvalidUrlException("invalid base url: " + baseUrl);
        }
        return createService(service, baseUrl);
    }

    public static <T> T createService(Class<T> service, String baseUrl){
        Retrofit retrofit = new Retrofit.Builder()
                .client(OkHttp.getDefault())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(baseUrl)
                .build();
        return  retrofit.create(service);
    }
    
    public static <T> Call<T> getDefaultCall(@NonNull RequestParam requestParam){
        ApiService service = null;
        try {
            service = getDefaultService(requestParam.getBaseUrl());
        } catch (InvalidUrlException e) {
            MLog.error(TAG, e.toString());
            return null;
        }
        if(requestParam.getMethod() == RequestParam.GET){
            return service.executeGet(requestParam.getPathUrl(), requestParam.getParams());
        } else if(requestParam.getMethod() == RequestParam.POST){
            return service.executePost(requestParam.getPathUrl(), requestParam.getParams());
        }
        return null;
    }

    public static Observable<ResponseBody> request(@NonNull final RequestParam requestParam, final OnHttpErrorListener listener) {
        return request(requestParam, null, listener);
    }

    /**
     * @param requestParam 封装的请求数据
     * @return Flowable
     * TODO 不能调用 onError ，调用 onError 会导致抛出异常，必须使用 onErrorResumeNext(Observable.<T>empty()) 处理掉异常，不然会崩溃，
     * TODO 在 doOnError 之前调用就不会再调用 donOnError ，在 doOnError 之后会先调用 doOnError, 还没想到好方法封装
     */
    public static Observable<ResponseBody> request(@NonNull final RequestParam requestParam, final Object lifecycleObject, final OnHttpErrorListener listener) {
        final Call<ResponseBody> call = getDefaultCall(requestParam);
        Observable<ResponseBody> observable = Flowable.create(new FlowableOnSubscribe<ResponseBody>() {
            @Override
            public void subscribe(final FlowableEmitter<ResponseBody> e) throws Exception {
                //设置取消监听
                e.setCancellable(new Cancellable() {
                    @Override
                    public void cancel() throws Exception {
                        MLog.info(TAG, "request cancel");
                        if (!call.isCanceled()) {
                            call.cancel();
                        }
                    }
                });
                try {
                    Response<ResponseBody> response = call.execute();
                    MLog.debug("chenrenzhan", "response :  " + response);
                    if(response.isSuccessful()){
                        if (!e.isCancelled()) {
                            e.onNext(response.body());
                            e.onComplete();
                        }
                    } else if(listener != null){
                        e.onError(new ExceptionHandle.ResponseThrowable(response.code(), response.message()));
                        listener.onError(new ExceptionHandle.ResponseThrowable(response.code(), response.message()));
                    }
                } catch (Exception exception) {
                    MLog.error(TAG, "request error: " + exception);
                    if (!e.isCancelled()) {
                        MLog.error(TAG, "onResponse: no cancel");
                        if(listener != null){
                            listener.onError(exception);
                        }
                        e.onComplete();
                    }
                }
            }
        }, BackpressureStrategy.BUFFER)
                .toObservable()
                .compose(RxRetrofit.<ResponseBody>background());
        return RxLifecycleUtils.bindLifecycle(observable, lifecycleObject);
    }

    public static <T> Observable<T> create(@NonNull final Call<T> call, final OnHttpErrorListener listener) {
        return create(call, null, listener);
    }

    /**
     * @param call retrofit的call
     * @param <T>  泛型
     * @return Flowable
     */
    public static <T> Observable<T> create(@NonNull final Call<T> call, final Object lifecycleObject, final OnHttpErrorListener listener) {
        Observable<T> observable = Flowable.create(new FlowableOnSubscribe<T>() {
            @Override
            public void subscribe(final FlowableEmitter<T> e) throws Exception {
                e.setCancellable(new Cancellable() {
                    @Override
                    public void cancel() throws Exception {
                        MLog.error(TAG, "request cancel");
                        if (!call.isCanceled()) {
                            call.cancel();
                        }
                    }
                });
                try {
                    Response<T> response = call.execute();
                    if(response.isSuccessful()){
                        if (!e.isCancelled()) {
                            e.onNext(response.body());
                            e.onComplete();
                        }
                    } else if(listener != null){
                        listener.onError(new ExceptionHandle.ResponseThrowable(response.code(), response.message()));
                    }
                } catch (Exception exception) {
                    MLog.error(TAG, "request error: " + exception);
                    if (!e.isCancelled()) {
                        MLog.error(TAG, "onResponse: no cancel");
                        listener.onError(exception);
                        e.onComplete();
                    }
                }
            }
        }, BackpressureStrategy.BUFFER)
                .toObservable()
                .compose(RxRetrofit.<T>background());
        return RxLifecycleUtils.bindLifecycle(observable, lifecycleObject);
    }

    /**
     * 后台线程执行同步，主线程执行异步操作
     * 并且拦截所有错误，不让app崩溃
     *
     * @param <T> 数据类型
     * @return Transformer
     */
    public static <T> ObservableTransformer<T, T> background() {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(@io.reactivex.annotations.NonNull Observable<T> upstream) {
                return upstream
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .onErrorResumeNext(Observable.<T>empty());
            }
        };
    }
}
