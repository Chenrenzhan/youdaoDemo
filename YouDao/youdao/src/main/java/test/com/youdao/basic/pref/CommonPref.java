package test.com.youdao.basic.pref;

import android.content.Context;
import android.content.SharedPreferences;

import test.com.youdao.MyApplication;

/**
 * 
 * Rule : input key cannot be null.
 * 
 */
public class CommonPref extends BaseSharedPref {
    public static final String COMMONREF_NAME = "CommonPref";
    private static final int OVER_LENGTH_STRING_VALUE = 300;

    private volatile static CommonPref sInst;

    private IPrefMonitor mMonitor;
    private CommonPref(SharedPreferences preferences){
        super(preferences);
    }

    public static CommonPref instance() {
        if (sInst == null) {
            synchronized (CommonPref.class) {
                if (sInst == null) {
                    sInst = new CommonPref(MyApplication.getApplication().getSharedPreferences(COMMONREF_NAME, Context.MODE_PRIVATE));
                }
            }
        }

        return sInst;
    }

    public void setCommonPrefMonitor(IPrefMonitor monitor) {
        mMonitor = monitor;
    }

    public void putString(String key, String value) {
        super.putString(key, value);

        if (mMonitor != null) {
            if (value != null && value.length() > OVER_LENGTH_STRING_VALUE) {
                mMonitor.onPutOverLengthString(key, value, COMMONREF_NAME);
            }
        }
    }
}