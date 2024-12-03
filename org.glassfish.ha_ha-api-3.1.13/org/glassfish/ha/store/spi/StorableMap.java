/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.ha.store.spi;

import java.util.Collection;

public interface StorableMap<K, V> {
    public Collection<K> getNewKeys();

    public Collection<K> getModifiedKeys();

    public Collection<K> getDeletedKeys();

    public V get(K var1);
}

