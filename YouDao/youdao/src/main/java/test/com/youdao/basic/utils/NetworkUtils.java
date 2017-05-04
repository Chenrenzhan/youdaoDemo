package test.com.youdao.basic.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import test.com.youdao.MyApplication;
import test.com.youdao.basic.log.MLog;

public class NetworkUtils {
    private static final String TAG = "NetworkUtils";

    public static boolean isNetworkAvailable() {
        try{
            Context context = MyApplication.getApplication();
            ConnectivityManager connectivityManager = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = connectivityManager.getActiveNetworkInfo();
            return ni != null && (ni.isConnected() || (ni.isAvailable() && ni.isConnectedOrConnecting()));
        }catch (Exception ex){
            MLog.error(TAG, "isNetworkAvailable error.", ex);
            return false;
        }
    }

    public static boolean isWifiActive() {

        try {
            Context context = MyApplication.getApplication();
            ConnectivityManager mgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = mgr.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
        }catch (Throwable throwable){
            MLog.error(TAG, "isWifiActive error.", throwable);
            return false;
        }
    }
}
