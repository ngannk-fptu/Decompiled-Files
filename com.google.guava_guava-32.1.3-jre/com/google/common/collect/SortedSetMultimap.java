/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  javax.annotation.CheckForNull
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.ElementTypesAreNonnullByDefault;
import com.google.common.collect.ParametricNullness;
import com.google.common.collect.SetMultimap;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.SortedSet;
import javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public interface SortedSetMultimap<K, V>
extends SetMultimap<K, V> {
    @Override
    public SortedSet<V> get(@ParametricNullness K var1);

    @Override
    @CanIgnoreReturnValue
    public SortedSet<V> removeAll(@CheckForNull Object var1);

    @Override
    @CanIgnoreReturnValue
    public SortedSet<V> replaceValues(@ParametricNullness K var1, Iterable<? extends V> var2);

    @Override
    public Map<K, Collection<V>> asMap();

    @CheckForNull
    public Comparator<? super V> valueComparator();
}

