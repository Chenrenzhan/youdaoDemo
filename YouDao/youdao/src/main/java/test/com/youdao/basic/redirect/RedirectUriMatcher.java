package test.com.youdao.basic.redirect;

import android.content.UriMatcher;
import android.net.Uri;

public class RedirectUriMatcher {
    public static final int ERROR_MATCH = -1; // 匹配不到时返回 -1
    private UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    public void addMatch(ARedirectApi api) {
        if (api == null){
            return;
        }
        uriMatcher.addURI(api.getAuthority(), api.getPath(), api.getMatchCode());
    }

    public int matchCode(Uri uri) {
        if (uri == null){
            return ERROR_MATCH;
        }
        return uriMatcher.match(uri);
    }
}