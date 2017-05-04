package test.com.youdao.basic.redirect;

/**
 * Created by DW on 2017/5/4.
 * 每一个 ARedirectApi 必须对应一个 IRedirectParam 具体实现类
 */
public abstract class ARedirectApi extends ARedirectRunnable<RedirectParam> {
    
    public abstract String getAuthority();

    public abstract String getPath();

    public int getMatchCode() {
        return System.identityHashCode(this);
    }
}
