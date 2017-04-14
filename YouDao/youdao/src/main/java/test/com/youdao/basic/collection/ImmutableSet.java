package test.com.youdao.basic.collection;

import android.support.annotation.NonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by ruoshili on 3/28/2017.
 */

public final class ImmutableSet<E> extends ImmutableCollection<E> implements Set<E> {

    private final Set<E> mInnerSet;

    /**
     * 构造一个不可变的set，自动对原始set进行拷贝，以保证不可变
     *
     * @param set 原始set
     */
    public ImmutableSet(@NonNull final Set<E> set) {
        this(set, false);
    }

    /**
     * 构造一个不可变的set
     *
     * @param set       原始set
     * @param doNotCopy 不要拷贝原始set，如果调用者可以保证原始set不再被修改，则传true以节省内存，否则应该传false
     */
    public ImmutableSet(@NonNull final Set<E> set, final boolean doNotCopy) {
        final Set<E> temp = doNotCopy ? set : new HashSet<>(set);
        mInnerSet = Collections.unmodifiableSet(temp);
    }

    @NonNull
    @Override
    protected Collection<E> getInnerCollection() {
        return mInnerSet;
    }
}
