package test.com.youdao.basic.http;

import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import test.com.youdao.MyApplication;

/**
 * Created by DW on 2017/4/28.
 */
public class OkHttp {
    private static final int DEFAULT_TIMEOUT = 5;
    private static final int DEFAULT_CACHE_SIZE = 10 * 1024 * 1024;
    
    private static OkHttpClient mDefault;
    private static Object syncLock = new Object();
            
    public static OkHttpClient getDefault(){
        if(mDefault == null){
            synchronized (syncLock){
                mDefault = crate().build();
            }
        }
        return mDefault;
    }
    
    public static OkHttpClient.Builder crate(){
        //创建Cache
        Cache cache = new Cache(MyApplication.getApplication().getCacheDir(), DEFAULT_CACHE_SIZE);
        return  new OkHttpClient.Builder()
                .cache(cache)
                .addNetworkInterceptor(InterceptorUtils.getHttpLoggingInterceptor()) // 日志拦截器
                .addNetworkInterceptor(InterceptorUtils.getCacheInterceptor()) // 缓存拦截器，无网络使用缓存，有网络直接请求
                .addInterceptor(InterceptorUtils.getCacheInterceptor())
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
    }
    
}
