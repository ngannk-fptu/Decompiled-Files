/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.concurrent.LazyInit
 *  com.google.j2objc.annotations.RetainedWith
 *  javax.annotation.CheckForNull
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.CollectPreconditions;
import com.google.common.collect.ElementTypesAreNonnullByDefault;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.errorprone.annotations.concurrent.LazyInit;
import com.google.j2objc.annotations.RetainedWith;
import java.util.Map;
import java.util.function.BiConsumer;
import javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible(serializable=true, emulated=true)
final class SingletonImmutableBiMap<K, V>
extends ImmutableBiMap<K, V> {
    final transient K singleKey;
    final transient V singleValue;
    @CheckForNull
    private final transient ImmutableBiMap<V, K> inverse;
    @LazyInit
    @CheckForNull
    @RetainedWith
    private transient ImmutableBiMap<V, K> lazyInverse;

    SingletonImmutableBiMap(K singleKey, V singleValue) {
        CollectPreconditions.checkEntryNotNull(singleKey, singleValue);
        this.singleKey = singleKey;
        this.singleValue = singleValue;
        this.inverse = null;
    }

    private SingletonImmutableBiMap(K singleKey, V singleValue, ImmutableBiMap<V, K> inverse) {
        this.singleKey = singleKey;
        this.singleValue = singleValue;
        this.inverse = inverse;
    }

    @Override
    @CheckForNull
    public V get(@CheckForNull Object key) {
        return this.singleKey.equals(key) ? (V)this.singleValue : null;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {
        Preconditions.checkNotNull(action).accept(this.singleKey, this.singleValue);
    }

    @Override
    public boolean containsKey(@CheckForNull Object key) {
        return this.singleKey.equals(key);
    }

    @Override
    public boolean containsValue(@CheckForNull Object value) {
        return this.singleValue.equals(value);
    }

    @Override
    boolean isPartialView() {
        return false;
    }

    @Override
    ImmutableSet<Map.Entry<K, V>> createEntrySet() {
        return ImmutableSet.of(Maps.immutableEntry(this.singleKey, this.singleValue));
    }

    @Override
    ImmutableSet<K> createKeySet() {
        return ImmutableSet.of(this.singleKey);
    }

    @Override
    public ImmutableBiMap<V, K> inverse() {
        if (this.inverse != null) {
            return this.inverse;
        }
        ImmutableBiMap<V, K> result = this.lazyInverse;
        if (result == null) {
            this.lazyInverse = new SingletonImmutableBiMap<K, V>(this.singleValue, this.singleKey, this);
            return this.lazyInverse;
        }
        return result;
    }
}

