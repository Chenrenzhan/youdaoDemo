package test.com.youdao.basic.redirect;

import android.net.Uri;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import test.com.youdao.basic.log.MLog;
import test.com.youdao.basic.utils.SingleTonUtils;

/**
 * Created by DW on 2017/5/4.
 * 自定义 URI ，匹配 URI 重定向到 Runnable
 * 自定义 URI 组成： host://authority/path, 例如： redirect://RedirectTest/redirect/test/param1/param2?param=2
 */
public class RedirectHandler {
    public final static String URI_HOST = "redirect://";
    private RedirectUriMatcher mUriMatcher = new RedirectUriMatcher();
    protected Map<Integer, ARedirectRunnable> mRunnables = new HashMap<Integer, ARedirectRunnable>();
    
    private static RedirectHandler mInstance = new SingleTonUtils<RedirectHandler>(){
        @Override
        public RedirectHandler create() {
            return new RedirectHandler();
        }
    }.get();
    
    public static RedirectHandler getInstance(){
        return mInstance;
    }
    
    private RedirectHandler(){}

    public void addMatchList(IRedirectApiList apis) {
        if (apis == null || apis.getList() == null){
            return;
        }
        for (ARedirectApi api : apis.getList()) {
            addMatched(api);
        }
    }

    public void addMatchList(List<ARedirectApi> apis) {
        if (apis == null){
            return;
        }
        for (ARedirectApi api : apis) {
            addMatched(api);
        }
    }

    public void addMatched(ARedirectApi restApi) {
        mUriMatcher.addMatch(restApi);
        addRunnable(restApi.getMatchCode(), restApi);
    }

    protected void addRunnable(int code, ARedirectRunnable runnable) {
        if (runnable == null){
            return;
        }
        mRunnables.put(code, runnable);
    }

    public void handleUri(Uri uri) throws RedirectAPINotSupportException {
        if (uri == null) {
            MLog.error(this, "handleUri uri is NULL");
            return;
        }
        this.handleUri(uri, null);
    }

    public void handleUri(Uri uri, IRedirectParam params) throws RedirectAPINotSupportException {
        if (uri == null) {
            MLog.error(this, "handleUri uri is NULL");
            return;
        }
        int code = mUriMatcher.matchCode(uri);
        ARedirectRunnable<IRedirectParam> runnable = mRunnables.get(code);
        if (runnable != null) {
            runnable.setParam(params);
            try{
                runnable.run();
                /**
                 * NOTE:Must do this after running, prevent memory leak
                 */
                runnable.setParam(null);
            }catch (Exception e){
                MLog.error(this, "exception occurs when handleUri, uri = " + uri + ", params = " + params + ", e = " + e);
            }
        }else{
            throw new RedirectAPINotSupportException(uri);
        }
    }

    public void handleUriString(String uriString) throws RedirectAPINotSupportException {
        if (TextUtils.isEmpty(uriString)) {
            MLog.error(this, "handleUriString uriString is NULL");
            return;
        }
        handleUri(Uri.parse(uriString));
    }

    public void handleUriString(String uriString, IRedirectParam params) throws RedirectAPINotSupportException {
        if (TextUtils.isEmpty(uriString)) {
            MLog.error(this, "handleUriString uriString is NULL");
            return;
        }
        handleUri(Uri.parse(uriString), params);
    }

    public int matchCode(String uriString) {
        if(TextUtils.isEmpty(uriString)){
            return RedirectUriMatcher.ERROR_MATCH;
        }
        Uri uri = Uri.parse(uriString);
        if(uri == null){
            return RedirectUriMatcher.ERROR_MATCH;
        }
        return mUriMatcher.matchCode(uri);
    }

    public ARedirectRunnable getRunnable(int code) {
        return mRunnables.get(code);
    }
}
