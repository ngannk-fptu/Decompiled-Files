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
import com.google.common.collect.ForwardingObject;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.ParametricNullness;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public abstract class ForwardingMultimap<K, V>
extends ForwardingObject
implements Multimap<K, V> {
    protected ForwardingMultimap() {
    }

    @Override
    protected abstract Multimap<K, V> delegate();

    @Override
    public Map<K, Collection<V>> asMap() {
        return this.delegate().asMap();
    }

    @Override
    public void clear() {
        this.delegate().clear();
    }

    @Override
    public boolean containsEntry(@CheckForNull Object key, @CheckForNull Object value) {
        return this.delegate().containsEntry(key, value);
    }

    @Override
    public boolean containsKey(@CheckForNull Object key) {
        return this.delegate().containsKey(key);
    }

    @Override
    public boolean containsValue(@CheckForNull Object value) {
        return this.delegate().containsValue(value);
    }

    @Override
    public Collection<Map.Entry<K, V>> entries() {
        return this.delegate().entries();
    }

    @Override
    public Collection<V> get(@ParametricNullness K key) {
        return this.delegate().get(key);
    }

    @Override
    public boolean isEmpty() {
        return this.delegate().isEmpty();
    }

    @Override
    public Multiset<K> keys() {
        return this.delegate().keys();
    }

    @Override
    public Set<K> keySet() {
        return this.delegate().keySet();
    }

    @Override
    @CanIgnoreReturnValue
    public boolean put(@ParametricNullness K key, @ParametricNullness V value) {
        return this.delegate().put(key, value);
    }

    @Override
    @CanIgnoreReturnValue
    public boolean putAll(@ParametricNullness K key, Iterable<? extends V> values) {
        return this.delegate().putAll(key, values);
    }

    @Override
    @CanIgnoreReturnValue
    public boolean putAll(Multimap<? extends K, ? extends V> multimap) {
        return this.delegate().putAll(multimap);
    }

    @Override
    @CanIgnoreReturnValue
    public boolean remove(@CheckForNull Object key, @CheckForNull Object value) {
        return this.delegate().remove(key, value);
    }

    @Override
    @CanIgnoreReturnValue
    public Collection<V> removeAll(@CheckForNull Object key) {
        return this.delegate().removeAll(key);
    }

    @Override
    @CanIgnoreReturnValue
    public Collection<V> replaceValues(@ParametricNullness K key, Iterable<? extends V> values) {
        return this.delegate().replaceValues(key, values);
    }

    @Override
    public int size() {
        return this.delegate().size();
    }

    @Override
    public Collection<V> values() {
        return this.delegate().values();
    }

    @Override
    public boolean equals(@CheckForNull Object object) {
        return object == this || this.delegate().equals(object);
    }

    @Override
    public int hashCode() {
        return this.delegate().hashCode();
    }
}

