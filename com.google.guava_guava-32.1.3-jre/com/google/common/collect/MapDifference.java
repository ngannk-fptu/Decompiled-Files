/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.DoNotMock
 *  javax.annotation.CheckForNull
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.ElementTypesAreNonnullByDefault;
import com.google.common.collect.ParametricNullness;
import com.google.errorprone.annotations.DoNotMock;
import java.util.Map;
import javax.annotation.CheckForNull;

@DoNotMock(value="Use Maps.difference")
@ElementTypesAreNonnullByDefault
@GwtCompatible
public interface MapDifference<K, V> {
    public boolean areEqual();

    public Map<K, V> entriesOnlyOnLeft();

    public Map<K, V> entriesOnlyOnRight();

    public Map<K, V> entriesInCommon();

    public Map<K, ValueDifference<V>> entriesDiffering();

    public boolean equals(@CheckForNull Object var1);

    public int hashCode();

    @DoNotMock(value="Use Maps.difference")
    public static interface ValueDifference<V> {
        @ParametricNullness
        public V leftValue();

        @ParametricNullness
        public V rightValue();

        public boolean equals(@CheckForNull Object var1);

        public int hashCode();
    }
}

