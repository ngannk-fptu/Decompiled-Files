/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.DoNotCall
 */
package com.google.common.collect;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.collect.ElementTypesAreNonnullByDefault;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.errorprone.annotations.DoNotCall;
import java.util.stream.Collector;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
abstract class ImmutableSortedSetFauxverideShim<E>
extends ImmutableSet.CachingAsList<E> {
    ImmutableSortedSetFauxverideShim() {
    }

    @Deprecated
    @DoNotCall(value="Use toImmutableSortedSet")
    public static <E> Collector<E, ?, ImmutableSet<E>> toImmutableSet() {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @DoNotCall(value="Use naturalOrder")
    public static <E> ImmutableSortedSet.Builder<E> builder() {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @DoNotCall(value="Use naturalOrder (which does not accept an expected size)")
    public static <E> ImmutableSortedSet.Builder<E> builderWithExpectedSize(int expectedSize) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @DoNotCall(value="Pass a parameter of type Comparable")
    public static <E> ImmutableSortedSet<E> of(E element) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @DoNotCall(value="Pass parameters of type Comparable")
    public static <E> ImmutableSortedSet<E> of(E e1, E e2) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @DoNotCall(value="Pass parameters of type Comparable")
    public static <E> ImmutableSortedSet<E> of(E e1, E e2, E e3) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @DoNotCall(value="Pass parameters of type Comparable")
    public static <E> ImmutableSortedSet<E> of(E e1, E e2, E e3, E e4) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @DoNotCall(value="Pass parameters of type Comparable")
    public static <E> ImmutableSortedSet<E> of(E e1, E e2, E e3, E e4, E e5) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @DoNotCall(value="Pass parameters of type Comparable")
    public static <E> ImmutableSortedSet<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E ... remaining) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @DoNotCall(value="Pass parameters of type Comparable")
    public static <E> ImmutableSortedSet<E> copyOf(E[] elements) {
        throw new UnsupportedOperationException();
    }
}

