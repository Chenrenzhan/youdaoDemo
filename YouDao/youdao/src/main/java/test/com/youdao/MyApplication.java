package test.com.youdao;

import android.app.Application;

import test.com.youdao.basic.crash.CrashHandler;
import test.com.youdao.basic.redirect.RedirectHandler;
import test.com.youdao.basic.redirect.RedirectTest;

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

        RedirectHandler.getInstance().addMatchList(new RedirectTest()); // 添加 自定义 重定向 URI
    }
    
    public static Application getApplication(){
        return mApplication;
    }
}
