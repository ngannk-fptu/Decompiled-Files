/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.CheckForNull
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.ElementTypesAreNonnullByDefault;
import com.google.common.collect.ForwardingSetMultimap;
import com.google.common.collect.ParametricNullness;
import com.google.common.collect.SortedSetMultimap;
import java.util.Comparator;
import java.util.SortedSet;
import javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public abstract class ForwardingSortedSetMultimap<K, V>
extends ForwardingSetMultimap<K, V>
implements SortedSetMultimap<K, V> {
    protected ForwardingSortedSetMultimap() {
    }

    @Override
    protected abstract SortedSetMultimap<K, V> delegate();

    @Override
    public SortedSet<V> get(@ParametricNullness K key) {
        return this.delegate().get((Object)key);
    }

    @Override
    public SortedSet<V> removeAll(@CheckForNull Object key) {
        return this.delegate().removeAll(key);
    }

    @Override
    public SortedSet<V> replaceValues(@ParametricNullness K key, Iterable<? extends V> values) {
        return this.delegate().replaceValues((Object)key, (Iterable)values);
    }

    @Override
    @CheckForNull
    public Comparator<? super V> valueComparator() {
        return this.delegate().valueComparator();
    }
}

