/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.DoNotCall
 */
package com.google.common.collect;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.collect.ElementTypesAreNonnullByDefault;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.ImmutableSortedMultiset;
import com.google.errorprone.annotations.DoNotCall;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Collector;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
abstract class ImmutableSortedMultisetFauxverideShim<E>
extends ImmutableMultiset<E> {
    ImmutableSortedMultisetFauxverideShim() {
    }

    @Deprecated
    @DoNotCall(value="Use toImmutableSortedMultiset.")
    public static <E> Collector<E, ?, ImmutableMultiset<E>> toImmutableMultiset() {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @DoNotCall(value="Use toImmutableSortedMultiset.")
    public static <T, E> Collector<T, ?, ImmutableMultiset<E>> toImmutableMultiset(Function<? super T, ? extends E> elementFunction, ToIntFunction<? super T> countFunction) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @DoNotCall(value="Use naturalOrder.")
    public static <E> ImmutableSortedMultiset.Builder<E> builder() {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @DoNotCall(value="Elements must be Comparable. (Or, pass a Comparator to orderedBy or copyOf.)")
    public static <E> ImmutableSortedMultiset<E> of(E element) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @DoNotCall(value="Elements must be Comparable. (Or, pass a Comparator to orderedBy or copyOf.)")
    public static <E> ImmutableSortedMultiset<E> of(E e1, E e2) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @DoNotCall(value="Elements must be Comparable. (Or, pass a Comparator to orderedBy or copyOf.)")
    public static <E> ImmutableSortedMultiset<E> of(E e1, E e2, E e3) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @DoNotCall(value="Elements must be Comparable. (Or, pass a Comparator to orderedBy or copyOf.)")
    public static <E> ImmutableSortedMultiset<E> of(E e1, E e2, E e3, E e4) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @DoNotCall(value="Elements must be Comparable. (Or, pass a Comparator to orderedBy or copyOf.)")
    public static <E> ImmutableSortedMultiset<E> of(E e1, E e2, E e3, E e4, E e5) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @DoNotCall(value="Elements must be Comparable. (Or, pass a Comparator to orderedBy or copyOf.)")
    public static <E> ImmutableSortedMultiset<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E ... remaining) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @DoNotCall(value="Elements must be Comparable. (Or, pass a Comparator to orderedBy or copyOf.)")
    public static <E> ImmutableSortedMultiset<E> copyOf(E[] elements) {
        throw new UnsupportedOperationException();
    }
}

