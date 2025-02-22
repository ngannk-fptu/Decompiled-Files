/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.CheckForNull
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Predicate;
import com.google.common.collect.ElementTypesAreNonnullByDefault;
import com.google.common.collect.FilteredEntryMultimap;
import com.google.common.collect.FilteredSetMultimap;
import com.google.common.collect.ParametricNullness;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Set;
import javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
final class FilteredEntrySetMultimap<K, V>
extends FilteredEntryMultimap<K, V>
implements FilteredSetMultimap<K, V> {
    FilteredEntrySetMultimap(SetMultimap<K, V> unfiltered, Predicate<? super Map.Entry<K, V>> predicate) {
        super(unfiltered, predicate);
    }

    @Override
    public SetMultimap<K, V> unfiltered() {
        return (SetMultimap)this.unfiltered;
    }

    @Override
    public Set<V> get(@ParametricNullness K key) {
        return (Set)super.get(key);
    }

    @Override
    public Set<V> removeAll(@CheckForNull Object key) {
        return (Set)super.removeAll(key);
    }

    @Override
    public Set<V> replaceValues(@ParametricNullness K key, Iterable<? extends V> values) {
        return (Set)super.replaceValues(key, values);
    }

    @Override
    Set<Map.Entry<K, V>> createEntries() {
        return Sets.filter(this.unfiltered().entries(), this.entryPredicate());
    }

    @Override
    public Set<Map.Entry<K, V>> entries() {
        return (Set)super.entries();
    }
}

