/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal.util;

import com.google.inject.internal.util.$Function;
import com.google.inject.internal.util.$Iterators;
import com.google.inject.internal.util.$Preconditions;
import java.util.Arrays;
import java.util.Iterator;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class $Iterables {
    private $Iterables() {
    }

    public static String toString(Iterable<?> iterable) {
        return $Iterators.toString(iterable.iterator());
    }

    public static <T> T getOnlyElement(Iterable<T> iterable) {
        return $Iterators.getOnlyElement(iterable.iterator());
    }

    public static <T> Iterable<T> concat(Iterable<? extends T> a, Iterable<? extends T> b) {
        $Preconditions.checkNotNull(a);
        $Preconditions.checkNotNull(b);
        return $Iterables.concat(Arrays.asList(a, b));
    }

    public static <T> Iterable<T> concat(Iterable<? extends Iterable<? extends T>> inputs) {
        $Function function = new $Function<Iterable<? extends T>, Iterator<? extends T>>(){

            @Override
            public Iterator<? extends T> apply(Iterable<? extends T> from) {
                return from.iterator();
            }
        };
        final Iterable<T> iterators = $Iterables.transform(inputs, function);
        return new IterableWithToString<T>(){

            @Override
            public Iterator<T> iterator() {
                return $Iterators.concat(iterators.iterator());
            }
        };
    }

    public static <F, T> Iterable<T> transform(final Iterable<F> fromIterable, final $Function<? super F, ? extends T> function) {
        $Preconditions.checkNotNull(fromIterable);
        $Preconditions.checkNotNull(function);
        return new IterableWithToString<T>(){

            @Override
            public Iterator<T> iterator() {
                return $Iterators.transform(fromIterable.iterator(), function);
            }
        };
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static abstract class IterableWithToString<E>
    implements Iterable<E> {
        IterableWithToString() {
        }

        public String toString() {
            return $Iterables.toString(this);
        }
    }
}

