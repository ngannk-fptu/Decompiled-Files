/*
 * Decompiled with CFR 0.152.
 */
package groovy.lang;

import groovy.lang.Closure;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public final class ListWithDefault<T>
implements List<T> {
    private final List<T> delegate;
    private final boolean lazyDefaultValues;
    private final Closure initClosure;

    private ListWithDefault(List<T> items, boolean lazyDefaultValues, Closure initClosure) {
        this.delegate = items;
        this.lazyDefaultValues = lazyDefaultValues;
        this.initClosure = initClosure;
    }

    public List<T> getDelegate() {
        return this.delegate != null ? new ArrayList<T>(this.delegate) : null;
    }

    public boolean isLazyDefaultValues() {
        return this.lazyDefaultValues;
    }

    public Closure getInitClosure() {
        return this.initClosure != null ? (Closure)this.initClosure.clone() : null;
    }

    public static <T> List<T> newInstance(List<T> items, boolean lazyDefaultValues, Closure initClosure) {
        if (items == null) {
            throw new IllegalArgumentException("Parameter \"items\" must not be null");
        }
        if (initClosure == null) {
            throw new IllegalArgumentException("Parameter \"initClosure\" must not be null");
        }
        return new ListWithDefault<T>(new ArrayList<T>(items), lazyDefaultValues, (Closure)initClosure.clone());
    }

    @Override
    public int size() {
        return this.delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return this.delegate.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return this.delegate.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return this.delegate.iterator();
    }

    @Override
    public Object[] toArray() {
        return this.delegate.toArray();
    }

    @Override
    public <T> T[] toArray(T[] ts) {
        return this.delegate.toArray(ts);
    }

    @Override
    public boolean add(T t) {
        return this.delegate.add(t);
    }

    @Override
    public boolean remove(Object o) {
        return this.delegate.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> objects) {
        return this.delegate.containsAll(objects);
    }

    @Override
    public boolean addAll(Collection<? extends T> ts) {
        return this.delegate.addAll(ts);
    }

    @Override
    public boolean addAll(int i, Collection<? extends T> ts) {
        return this.delegate.addAll(i, ts);
    }

    @Override
    public boolean removeAll(Collection<?> objects) {
        return this.delegate.removeAll(objects);
    }

    @Override
    public boolean retainAll(Collection<?> objects) {
        return this.delegate.retainAll(objects);
    }

    @Override
    public void clear() {
        this.delegate.clear();
    }

    public T getAt(int index) {
        return this.get(index);
    }

    @Override
    public T get(int index) {
        T item;
        int size = this.size();
        int normalisedIndex = ListWithDefault.normaliseIndex(index, size);
        if (normalisedIndex < 0) {
            throw new IndexOutOfBoundsException("Negative index [" + normalisedIndex + "] too large for list size " + size);
        }
        if (normalisedIndex >= size) {
            int gapCount = normalisedIndex - size;
            for (int i = 0; i < gapCount; ++i) {
                int idx = this.size();
                if (this.lazyDefaultValues) {
                    this.delegate.add(idx, null);
                    continue;
                }
                this.delegate.add(idx, this.getDefaultValue(idx));
            }
            int idx = normalisedIndex;
            this.delegate.add(idx, this.getDefaultValue(idx));
            normalisedIndex = ListWithDefault.normaliseIndex(index, this.size());
        }
        if ((item = this.delegate.get(normalisedIndex)) == null && this.lazyDefaultValues) {
            item = this.getDefaultValue(normalisedIndex);
            this.delegate.set(normalisedIndex, item);
        }
        return item;
    }

    private T getDefaultValue(int idx) {
        return (T)this.initClosure.call(new Object[]{idx});
    }

    private static int normaliseIndex(int index, int size) {
        if (index < 0) {
            index += size;
        }
        return index;
    }

    @Override
    public T set(int i, T t) {
        return this.delegate.set(i, t);
    }

    @Override
    public void add(int i, T t) {
        this.delegate.add(i, t);
    }

    @Override
    public T remove(int i) {
        return this.delegate.remove(i);
    }

    @Override
    public int indexOf(Object o) {
        return this.delegate.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return this.delegate.lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
        return this.delegate.listIterator();
    }

    @Override
    public ListIterator<T> listIterator(int i) {
        return this.delegate.listIterator(i);
    }

    @Override
    public boolean equals(Object obj) {
        return this.delegate.equals(obj);
    }

    @Override
    public int hashCode() {
        return this.delegate.hashCode();
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return new ListWithDefault<T>(this.delegate.subList(fromIndex, toIndex), this.lazyDefaultValues, (Closure)this.initClosure.clone());
    }
}

