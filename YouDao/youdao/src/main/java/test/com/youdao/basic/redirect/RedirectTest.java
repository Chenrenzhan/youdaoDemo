package test.com.youdao.basic.redirect;

import android.app.Activity;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import test.com.youdao.basic.log.MLog;

/**
 * Created by DW on 2017/5/4.
 */
public class RedirectTest implements IRedirectApiList {
    private static final String AUTHORITY = "RedirectTest";
    @Override
    public List<ARedirectApi> getList() {
        List<ARedirectApi> apiList = new ArrayList<ARedirectApi>();
        apiList.add(gotoTest());
        return apiList;
    }
    
    private ARedirectApi gotoTest(){
        return new ARedirectApi() {
            @Override
            public String getAuthority() {
                return AUTHORITY;
            }

            @Override
            public String getPath() {
                return "redirect/test/*/*"; // * 好表示参数，?后表示get方式的url参数
            }

            @Override
            public void run() {
                final RedirectParam param = getParam();
                final Activity act = param.srActivity.get(); // 注意判空
                final Uri uri = param.uri;
                MLog.debug("chenrenzhan-1", " uri = " + uri);
                List<String> segments = uri.getPathSegments(); // 0,1,2,3....分别表示 path中的第几个
                String param1 = segments.get(2); // 第一个 * 号位置的值
                String param2 = segments.get(3); // 第二个 * 位置的值
                String exParam = uri.getQueryParameter("param"); // ?param=1 ,获取 ? 后边的参数
            }
        };
    }
}
