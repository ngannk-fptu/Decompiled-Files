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
import com.google.common.collect.FilteredKeyMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.ParametricNullness;
import java.util.List;
import javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
final class FilteredKeyListMultimap<K, V>
extends FilteredKeyMultimap<K, V>
implements ListMultimap<K, V> {
    FilteredKeyListMultimap(ListMultimap<K, V> unfiltered, Predicate<? super K> keyPredicate) {
        super(unfiltered, keyPredicate);
    }

    @Override
    public ListMultimap<K, V> unfiltered() {
        return (ListMultimap)super.unfiltered();
    }

    @Override
    public List<V> get(@ParametricNullness K key) {
        return (List)super.get(key);
    }

    @Override
    public List<V> removeAll(@CheckForNull Object key) {
        return (List)super.removeAll(key);
    }

    @Override
    public List<V> replaceValues(@ParametricNullness K key, Iterable<? extends V> values) {
        return (List)super.replaceValues(key, values);
    }
}

