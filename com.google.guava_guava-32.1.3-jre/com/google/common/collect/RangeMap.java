/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.DoNotMock
 *  javax.annotation.CheckForNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.google.common.collect;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.collect.ElementTypesAreNonnullByDefault;
import com.google.common.collect.Range;
import com.google.errorprone.annotations.DoNotMock;
import java.util.Map;
import java.util.function.BiFunction;
import javax.annotation.CheckForNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@DoNotMock(value="Use ImmutableRangeMap or TreeRangeMap")
@ElementTypesAreNonnullByDefault
@GwtIncompatible
public interface RangeMap<K extends Comparable, V> {
    @CheckForNull
    public V get(K var1);

    @CheckForNull
    public Map.Entry<Range<K>, V> getEntry(K var1);

    public Range<K> span();

    public void put(Range<K> var1, V var2);

    public void putCoalescing(Range<K> var1, V var2);

    public void putAll(RangeMap<K, ? extends V> var1);

    public void clear();

    public void remove(Range<K> var1);

    public void merge(Range<K> var1, @CheckForNull V var2, BiFunction<? super V, ? super @Nullable V, ? extends @Nullable V> var3);

    public Map<Range<K>, V> asMapOfRanges();

    public Map<Range<K>, V> asDescendingMapOfRanges();

    public RangeMap<K, V> subRangeMap(Range<K> var1);

    public boolean equals(@CheckForNull Object var1);

    public int hashCode();

    public String toString();
}

