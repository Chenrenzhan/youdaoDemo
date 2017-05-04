package test.com.youdao;

import android.app.Application;

import java.util.Arrays;
import java.util.Collections;

import test.com.youdao.basic.crash.CrashHandler;
import test.com.youdao.basic.redirect.RedirectHandler;
import test.com.youdao.basic.redirect.RedirectTest;
import test.com.youdao.basic.redux.Action;
import test.com.youdao.basic.redux.Middleware;
import test.com.youdao.basic.redux.Reducer;
import test.com.youdao.redux.test.ReduxTestRedecer;
import test.com.youdao.redux.test.TestMiddleware;

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
        
        //  初始化 Redux
        HostStore.INSTANCE.init(new HostState.Builder(null).build(),
                Collections.<Middleware>singletonList(new TestMiddleware()),
                Arrays.<Reducer<HostState, ? extends Action>>asList(
                        new ReduxTestRedecer()
                ));
    }
    
    public static Application getApplication(){
        return mApplication;
    }
}
