package test.com.youdao;

import android.app.Application;

import test.com.youdao.basic.crash.CrashHandler;

/**
 * Created by DW on 2017/4/13.
 */
public class MyApplication extends Application {
    private static Application mApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;

        CrashHandler.getInstance().init(this);
    }
    
    public static Application getApplication(){
        return mApplication;
    }
}
