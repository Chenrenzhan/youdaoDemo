package test.com.youdao.basic.collection;

import android.support.annotation.NonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by ruoshili on 3/28/2017.
 */

public final class ImmutableMap<K, V> implements Map<K, V> {
    private final Map<K, V> mInnerMap;

    /**
     * 构造一个不可变的map，自动对原始map进行拷贝，以保证不可变
     *
     * @param map 原始map
     */
    public ImmutableMap(@NonNull final Map<K, V> map) {
        this(map, false);
    }

    /**
     * 构造一个不可变的map
     *
     * @param map       原始map
     * @param doNotCopy 不要拷贝原始map，如果调用者可以保证原始map不再被修改，则传true以节省内存，否则应该传false
     */
    public ImmutableMap(@NonNull final Map<K, V> map, final boolean doNotCopy) {
        final Map<K, V> temp = doNotCopy ? map : new HashMap<>(map);
        mInnerMap = Collections.unmodifiableMap(temp);
    }

    @Deprecated
    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsKey(Object key) {
        return mInnerMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return mInnerMap.containsValue(value);
    }

    @NonNull
    @Override
    public Set<Entry<K, V>> entrySet() {
        return mInnerMap.entrySet();
    }

    @Override
    public V get(Object key) {
        return mInnerMap.get(key);
    }

    @Override
    public boolean isEmpty() {
        return mInnerMap.isEmpty();
    }

    @NonNull
    @Override
    public Set<K> keySet() {
        return mInnerMap.keySet();
    }

    @Deprecated
    @Override
    public V put(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    public void putAll(@NonNull Map<? extends K, ? extends V> map) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    public V remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        return mInnerMap.size();
    }

    @NonNull
    @Override
    public Collection<V> values() {
        return mInnerMap.values();
    }
}
