/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.ha.store.impl;

import java.io.Serializable;
import org.glassfish.ha.store.api.BackingStore;
import org.glassfish.ha.store.api.BackingStoreConfiguration;
import org.glassfish.ha.store.api.BackingStoreException;
import org.glassfish.ha.store.api.BackingStoreFactory;
import org.glassfish.ha.store.impl.NoOpBackingStoreFactory;

public class NoOpBackingStore<K extends Serializable, V extends Serializable>
extends BackingStore<K, V> {
    private String myName;

    NoOpBackingStore() {
    }

    @Override
    protected void initialize(BackingStoreConfiguration<K, V> conf) throws BackingStoreException {
        super.initialize(conf);
        this.myName = conf == null ? null : conf.getInstanceName();
    }

    @Override
    public BackingStoreFactory getBackingStoreFactory() {
        return new NoOpBackingStoreFactory();
    }

    @Override
    public V load(K key, String version) throws BackingStoreException {
        return null;
    }

    @Override
    public String save(K key, V value, boolean isNew) throws BackingStoreException {
        return null;
    }

    @Override
    public void remove(K key) throws BackingStoreException {
    }

    @Override
    public String updateTimestamp(K key, String version, Long accessTime) throws BackingStoreException {
        return this.myName;
    }

    @Override
    public int removeExpired() throws BackingStoreException {
        return 0;
    }

    @Override
    public int size() throws BackingStoreException {
        return 0;
    }

    @Override
    public void destroy() throws BackingStoreException {
    }
}

