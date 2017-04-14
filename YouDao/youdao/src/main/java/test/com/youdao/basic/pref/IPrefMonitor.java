package test.com.youdao.basic.pref;

/**
 * Created by lulong on 2017/1/18.
 * Email:lulong@yy.com
 */
public interface IPrefMonitor {
    void onPutOverLengthString(String key, String value, String pref);
}
