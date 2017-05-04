package test.com.youdao.basic.redirect;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;

import java.lang.ref.SoftReference;
import java.util.Map;

/**
 * Created by DW on 2017/5/4.
 */
public class RedirectParam implements IRedirectParam{
    public SoftReference<Activity> srActivity;
    public Uri uri;
    public Object custom;
    public Map<String, Object> extend;
}
