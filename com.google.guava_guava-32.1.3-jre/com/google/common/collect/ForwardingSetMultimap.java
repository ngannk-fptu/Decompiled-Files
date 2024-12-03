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
import com.google.common.collect.ForwardingMultimap;
import com.google.common.collect.ParametricNullness;
import com.google.common.collect.SetMultimap;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Map;
import java.util.Set;
import javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public abstract class ForwardingSetMultimap<K, V>
extends ForwardingMultimap<K, V>
implements SetMultimap<K, V> {
    @Override
    protected abstract SetMultimap<K, V> delegate();

    @Override
    public Set<Map.Entry<K, V>> entries() {
        return this.delegate().entries();
    }

    @Override
    public Set<V> get(@ParametricNullness K key) {
        return this.delegate().get((Object)key);
    }

    @Override
    @CanIgnoreReturnValue
    public Set<V> removeAll(@CheckForNull Object key) {
        return this.delegate().removeAll(key);
    }

    @Override
    @CanIgnoreReturnValue
    public Set<V> replaceValues(@ParametricNullness K key, Iterable<? extends V> values) {
        return this.delegate().replaceValues((Object)key, (Iterable)values);
    }
}

