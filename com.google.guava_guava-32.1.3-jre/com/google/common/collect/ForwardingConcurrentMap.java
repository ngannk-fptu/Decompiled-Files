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
import com.google.common.collect.ForwardingMap;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.concurrent.ConcurrentMap;
import javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public abstract class ForwardingConcurrentMap<K, V>
extends ForwardingMap<K, V>
implements ConcurrentMap<K, V> {
    protected ForwardingConcurrentMap() {
    }

    @Override
    protected abstract ConcurrentMap<K, V> delegate();

    @Override
    @CheckForNull
    @CanIgnoreReturnValue
    public V putIfAbsent(K key, V value) {
        return this.delegate().putIfAbsent(key, value);
    }

    @Override
    @CanIgnoreReturnValue
    public boolean remove(@CheckForNull Object key, @CheckForNull Object value) {
        return this.delegate().remove(key, value);
    }

    @Override
    @CheckForNull
    @CanIgnoreReturnValue
    public V replace(K key, V value) {
        return this.delegate().replace(key, value);
    }

    @Override
    @CanIgnoreReturnValue
    public boolean replace(K key, V oldValue, V newValue) {
        return this.delegate().replace(key, oldValue, newValue);
    }
}

