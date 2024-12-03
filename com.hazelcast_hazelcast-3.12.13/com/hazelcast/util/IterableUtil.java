/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.util;

import com.hazelcast.core.IFunction;
import java.util.Collections;
import java.util.Iterator;

public final class IterableUtil {
    private IterableUtil() {
    }

    public static <T> T getFirst(Iterable<T> iterable, T defaultValue) {
        Iterator<T> iterator = iterable.iterator();
        return iterator.hasNext() ? iterator.next() : defaultValue;
    }

    public static <T, R> Iterable<R> map(final Iterable<T> iterable, final IFunction<T, R> mapper) {
        return new Iterable<R>(){

            @Override
            public Iterator<R> iterator() {
                return IterableUtil.map(iterable.iterator(), mapper);
            }
        };
    }

    public static <T, R> Iterator<R> map(final Iterator<T> iterator, final IFunction<T, R> mapper) {
        return new Iterator<R>(){

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public R next() {
                return mapper.apply(iterator.next());
            }

            @Override
            public void remove() {
                iterator.remove();
            }
        };
    }

    public static <T, R> Iterator<R> limit(final Iterator<R> iterator, final int limit) {
        return new Iterator<R>(){
            private int iterated;

            @Override
            public boolean hasNext() {
                return this.iterated < limit && iterator.hasNext();
            }

            @Override
            public R next() {
                ++this.iterated;
                return iterator.next();
            }

            @Override
            public void remove() {
                iterator.remove();
            }
        };
    }

    public static <T> Iterable<T> nullToEmpty(Iterable<T> iterable) {
        return iterable == null ? Collections.emptyList() : iterable;
    }
}

