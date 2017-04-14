package test.com.youdao.basic.pref;

import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import test.com.youdao.basic.log.MLog;

/**
 *
 * Rule : input key cannot be null.
 *
 */
public abstract class BaseSharedPref {

    private static final String DELIMITER = ",";

    protected final SharedPreferences mPref;

    public BaseSharedPref(SharedPreferences pref) {
        mPref = pref;
    }

    public void putString(String key, String value) {
        put(key, value);
    }

    public String getString(String key) {
        return get(key);
    }

    public String getString(String key, String defaultValue) {
        return mPref.getString(key, defaultValue);
    }

    public void putInt(String key, int value) {
        put(key, String.valueOf(value));
    }

    public void putBoolean(String key, boolean value) {
        put(key, String.valueOf(value));
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        String rawValue = get(key);
        if (TextUtils.isEmpty(rawValue)) {
            return defaultValue;
        }
        try {
            return Boolean.parseBoolean(rawValue);
        }
        catch (Exception e) {
            MLog.error(this, "failed to parse boolean value for key %s, %s", key, e);
            return defaultValue;
        }
    }

    public int getInt(String key, int defaultValue) {
        String rawValue = get(key);
        if (TextUtils.isEmpty(rawValue)) {
            return defaultValue;
        }
        return parseInt(rawValue, defaultValue);
    }

    private int parseInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            MLog.error(this, "lcy failed to parse value for key %s, %s", value,
                    e);
            return defaultValue;
        }
    }

    public int getInt(String key) {
        return getInt(key, -1);
    }

    public void putLong(String key, long value) {
        put(key, String.valueOf(value));
    }

    public long getLong(String key, long defaultValue) {
        String rawValue = get(key);
        if (TextUtils.isEmpty(rawValue)) {
            return defaultValue;
        }
        try {
            return Long.parseLong(rawValue);
        } catch (NumberFormatException e) {
            MLog.error(this,
                    "lcy failed to parse %s as long, for key %s, ex : %s",
                    rawValue, key, e);
            return defaultValue;
        }
    }

    public long getLong(String key) {
        return getLong(key, -1L);
    }

    public void putIntArray(String key, Integer[] values) {
        putIntList(key, Arrays.asList(values));
    }

    public int[] getIntArray(String key) {
        return getIntArray(key, null);
    }

    /**
     *
     * @param key
     * @param outValues
     *            For memory reuse, if the result is no greater than this space,
     *            will fill into this, the redundant elements won't be touched.
     *            If it is null, a new int array will be created if result is
     *            not empty.
     * @return The result list, null if no correlated.
     */
    public int[] getIntArray(String key, int[] outValues) {
        List<Integer> list = getIntList(key);
        if (list == null || list.size() == 0) {
            return null;
        }

        final int[] ret = (list.size() <= outValues.length) ? outValues
            : new int[list.size()];

        int i = 0;
        for (Integer e : list) {
            ret[i++] = e;
        }
        return ret;

    }

    public void putIntList(String key, List<Integer> values) {
        if (values == null || values.size() == 0) {
            return;
        }

        String value = TextUtils.join(DELIMITER, values);
        put(key, value);
    }

    public List<Integer> getIntList(String key) {
        String val = get(key);
        if (TextUtils.isEmpty(val)) {
            return null;
        }

        String[] values = TextUtils.split(val, DELIMITER);
        if (values == null || values.length == 0) {
            return null;
        }

        ArrayList<Integer> list = new ArrayList<Integer>();
        for (String e : values) {
            try {
                list.add(Integer.parseInt(e));
            } catch (NumberFormatException ex) {
                MLog.error(
                        this,
                        "lcy failed to parse value for key: %s, value: %s, exception: %s",
                        key, e, ex);
                continue;
            }

        }
        return list;
    }

    final public void put(String key, String value) {
        mPref.edit().putString(key, value).apply();
    }

    final public String get(String key) {
        return mPref.getString(key, null);
    }

    public void remove(String key){
        mPref.edit().remove(key).apply();
    }

    public void clear() {
        mPref.edit().clear().apply();
    }

    public Map<String, ?> getAll(){
        return mPref.getAll();
    }

    public boolean contain(String key) {
        if (key == null || key.length() == 0) {
            return false;
        }
        return mPref.contains(key);
    }

    public  void putObject(String key, Object obj){
        Gson gson = new Gson();
        String json = gson.toJson(obj);
        put(key, json);
    }


    public Object getObj(String key, Class className){
        Gson gson = new Gson();
        String json = getString(key,"");
        Object obj = gson.fromJson(json, className);
        return obj;
    }

}
