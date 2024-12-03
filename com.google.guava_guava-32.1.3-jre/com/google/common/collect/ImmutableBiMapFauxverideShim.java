/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.DoNotCall
 */
package com.google.common.collect;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.collect.ElementTypesAreNonnullByDefault;
import com.google.common.collect.ImmutableMap;
import com.google.errorprone.annotations.DoNotCall;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collector;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
abstract class ImmutableBiMapFauxverideShim<K, V>
extends ImmutableMap<K, V> {
    ImmutableBiMapFauxverideShim() {
    }

    @Deprecated
    @DoNotCall(value="Use toImmutableBiMap")
    public static <T, K, V> Collector<T, ?, ImmutableMap<K, V>> toImmutableMap(Function<? super T, ? extends K> keyFunction, Function<? super T, ? extends V> valueFunction) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @DoNotCall(value="Use toImmutableBiMap")
    public static <T, K, V> Collector<T, ?, ImmutableMap<K, V>> toImmutableMap(Function<? super T, ? extends K> keyFunction, Function<? super T, ? extends V> valueFunction, BinaryOperator<V> mergeFunction) {
        throw new UnsupportedOperationException();
    }
}

