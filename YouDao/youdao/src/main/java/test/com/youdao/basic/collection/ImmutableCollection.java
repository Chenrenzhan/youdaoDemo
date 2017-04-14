package test.com.youdao.basic.collection;

import android.support.annotation.NonNull;

import java.util.Collection;
import java.util.Iterator;

/**
 * Created by ruoshili on 3/28/2017.
 */

abstract class ImmutableCollection<E> implements Collection<E> {

    @NonNull
    protected abstract Collection<E> getInnerCollection();

    @Deprecated
    @Override
    public final boolean add(E object) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    public final boolean addAll(@NonNull Collection<? extends E> collection) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    public final void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public final boolean contains(Object object) {
        return getInnerCollection().contains(object);
    }

    @Override
    public final boolean containsAll(@NonNull Collection<?> collection) {
        return getInnerCollection().containsAll(collection);
    }

    @Override
    public final boolean isEmpty() {
        return getInnerCollection().isEmpty();
    }

    @NonNull
    @Override
    public final Iterator<E> iterator() {
        return getInnerCollection().iterator();
    }

    @Deprecated
    @Override
    public final boolean remove(Object object) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    public final boolean removeAll(@NonNull Collection<?> collection) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    public final boolean retainAll(@NonNull Collection<?> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final int size() {
        return getInnerCollection().size();
    }

    @NonNull
    @Override
    public final Object[] toArray() {
        return getInnerCollection().toArray();
    }

    @NonNull
    @Override
    public final <T> T[] toArray(@NonNull T[] array) {
        return getInnerCollection().toArray(array);
    }
}
