/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.flat;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

public class Rank<T> {
    private final T[] values;
    private final Comparator<? super T> order;
    private int first;

    public Rank(T[] values, Comparator<? super T> order) {
        this.values = values;
        this.order = order;
    }

    public Rank(Collection<T> values, Class<T> componentType, Comparator<? super T> order) {
        this.values = Rank.toArray(values, componentType);
        this.order = order;
    }

    public Rank(Iterator<T> values, Class<T> componentType, int count, Comparator<? super T> order) {
        this.order = order;
        if (count >= 0) {
            this.values = Rank.createArray(count, componentType);
            for (int k = 0; k < count; ++k) {
                this.values[k] = values.next();
            }
        } else {
            LinkedList<T> l = new LinkedList<T>();
            while (values.hasNext()) {
                l.add(values.next());
            }
            this.values = Rank.toArray(l, componentType);
        }
    }

    public static <S extends Comparable<S>> Rank<S> rank(S[] values) {
        return new Rank<S>(values, Rank.comparableComparator());
    }

    public static <S extends Comparable<S>> Rank<S> rank(Collection<S> values, Class<S> componentType) {
        return new Rank<S>(values, componentType, Rank.comparableComparator());
    }

    public static <S extends Comparable<S>> Rank<S> rank(Iterator<S> values, Class<S> componentType, int count) {
        return new Rank<S>(values, componentType, count, Rank.comparableComparator());
    }

    public static <T extends Comparable<T>> Comparator<T> comparableComparator() {
        return new Comparator<T>(){

            @Override
            public int compare(T c1, T c2) {
                return c1.compareTo(c2);
            }
        };
    }

    public Comparator<? super T> getOrder() {
        return this.order;
    }

    public Iterator<T> take(int n) {
        if (n < 0 || n + this.first > this.values.length) {
            throw new NoSuchElementException();
        }
        if (n > 0) {
            this.take(n, this.first, this.values.length - 1);
            this.first += n;
            return Arrays.asList(this.values).subList(this.first - n, this.first).iterator();
        }
        return Collections.emptySet().iterator();
    }

    public int size() {
        return this.values.length - this.first;
    }

    private void take(int n, int from, int to) {
        if (n >= to - from + 1) {
            return;
        }
        int pivot = from + n - 1;
        int lo = from;
        int hi = to;
        while (lo < hi) {
            while (this.order.compare(this.values[lo], this.values[pivot]) < 0) {
                ++lo;
            }
            while (this.order.compare(this.values[hi], this.values[pivot]) > 0) {
                --hi;
            }
            if (lo >= hi) continue;
            if (lo == pivot) {
                pivot = hi;
            } else if (hi == pivot) {
                pivot = lo;
            }
            this.swap(lo, hi);
            ++lo;
            --hi;
        }
        int nn = pivot + 1 - from;
        if (nn > n) {
            this.take(n, from, pivot);
        } else if (nn < n) {
            this.take(n - nn, pivot + 1, to);
        }
    }

    private void swap(int lo, int hi) {
        T t1 = this.values[lo];
        T t2 = this.values[hi];
        if (this.order.compare(t1, t2) == 0) {
            throw new IllegalStateException("Detected duplicates " + t1);
        }
        this.values[lo] = t2;
        this.values[hi] = t1;
    }

    private static <S> S[] toArray(Collection<S> collection, Class<S> componentType) {
        return collection.toArray(Rank.createArray(collection.size(), componentType));
    }

    private static <S> S[] createArray(int size, Class<S> componentType) {
        return (Object[])Array.newInstance(componentType, size);
    }
}

