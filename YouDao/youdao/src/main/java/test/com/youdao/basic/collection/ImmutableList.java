package test.com.youdao.basic.collection;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by ruoshili on 3/28/2017.
 */

public final class ImmutableList<E> extends ImmutableCollection<E> implements List<E> {
    private final List<E> mInnerList;

    /**
     * 构造一个不可变的list，自动对原始list进行拷贝，以保证不可变
     *
     * @param list 原始list
     */
    public ImmutableList(@NonNull final List<E> list) {
        this(list, false);
    }

    /**
     * 构造一个不可变的list
     *
     * @param list      原始list
     * @param doNotCopy 不要拷贝原始list，如果调用者可以保证原始list不再被修改，则传true以节省内存，否则应该传false
     */
    public ImmutableList(@NonNull final List<E> list, final boolean doNotCopy) {
        final List<E> temp = doNotCopy ? list : new ArrayList<>(list);
        mInnerList = Collections.unmodifiableList(temp);
    }

    @NonNull
    @Override
    protected Collection<E> getInnerCollection() {
        return mInnerList;
    }

    @Deprecated
    @Override
    public void add(int location, E object) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    public boolean addAll(int location, @NonNull Collection<? extends E> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public E get(int location) {
        return mInnerList.get(location);
    }

    @Override
    public int indexOf(Object object) {
        return mInnerList.indexOf(object);
    }

    @Override
    public int lastIndexOf(Object object) {
        return mInnerList.lastIndexOf(object);
    }

    @Override
    public ListIterator<E> listIterator() {
        return mInnerList.listIterator();
    }

    @NonNull
    @Override
    public ListIterator<E> listIterator(int location) {
        return mInnerList.listIterator(location);
    }

    @Deprecated
    @Override
    public E remove(int location) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    public E set(int location, E object) {
        throw new UnsupportedOperationException();
    }

    @NonNull
    @Override
    public List<E> subList(int start, int end) {
        return mInnerList.subList(start, end);
    }
}
