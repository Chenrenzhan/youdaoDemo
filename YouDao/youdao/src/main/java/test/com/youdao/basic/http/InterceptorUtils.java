package test.com.youdao.basic.http;

import java.io.IOException;

import okhttp3.CacheControl;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import test.com.youdao.BuildConfig;
import test.com.youdao.basic.utils.NetworkUtils;

/**
 * Created by DW on 2017/4/27.
 * addNetworkInterfacetor()添加的是网络拦截器（Network Interfacetor），它会在request和response时分别被调用一次；
 * addInterceptor（）添加的是应用拦截器（Application Interceptor），他只会在response被调用一次
 */
public class InterceptorUtils {

    /**
     * 日志拦截器
     * @return
     */
    public static HttpLoggingInterceptor getHttpLoggingInterceptor() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        if(BuildConfig.DEBUG){
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        }
        return loggingInterceptor;
    }

    /**
     * 在无网络的情况下读取缓存，有网络的情况下直接重新请求
     * @return
     */
    public static Interceptor getCacheInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Interceptor.Chain chain) throws IOException {
                Request request = chain.request();
                boolean isNetWork = NetworkUtils.isNetworkAvailable();
                if (!isNetWork) {
                    //无网络下强制使用缓存，无论缓存是否过期,此时该请求实际上不会被发送出去。
                    request=request.newBuilder().cacheControl(CacheControl.FORCE_CACHE)
                            .build();
                }

                Response response = chain.proceed(request);
                if (isNetWork) {//有网络情况下，根据请求接口的设置，配置缓存。
                    //这样在下次请求时，根据缓存决定是否真正发出请求。
                    String cacheControl = request.cacheControl().toString();
                    //当然如果你想在有网络的情况下都直接走网络，那么只需要
                    //将其超时时间这是为0即可:
                    cacheControl="Cache-Control:public,max-age=0";
                    return response.newBuilder().header("Cache-Control", cacheControl)
                            .removeHeader("Pragma")
                            .build();
                }else{//无网络
                    return response.newBuilder().header("Cache-Control", "public,only-if-cached,max-stale=360000")
                            .removeHeader("Pragma")
                            .build();
                }
            }
        };
    }

    public static Interceptor getInterceptor() {
        Interceptor headerInterceptor = new Interceptor() {

            @Override
            public Response intercept(Chain chain) throws IOException {
                Request originalRequest = chain.request();
                Request.Builder builder = originalRequest.newBuilder();
                builder.header("appid", "1");
                builder.header("timestamp", System.currentTimeMillis() + "");
                builder.header("appkey", "zRc9bBpQvZYmpqkwOo");
                builder.header("signature", "dsljdljflajsnxdsd");

                Request.Builder requestBuilder =builder.method(originalRequest.method(), originalRequest.body());
                Request request = requestBuilder.build();

                // 给所有请求添加参数，最好将其放在请求头当中
                HttpUrl httpUrl = originalRequest.url().newBuilder().addQueryParameter("paltform", "android").addQueryParameter("version", "1.0.0").build();
                request = originalRequest.newBuilder().url(httpUrl).build();

                Response response = chain.proceed(chain.request());
                String timestamp = response.header("time");
                if (timestamp != null) {
                    //获取到响应header中的time
                }
                
                
                return chain.proceed(request);
            }

        };

        return headerInterceptor;
    }
}
