/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jvnet.hk2.annotations.Service
 */
package org.glassfish.ha.store.impl;

import java.io.Serializable;
import org.glassfish.ha.store.api.BackingStore;
import org.glassfish.ha.store.api.BackingStoreConfiguration;
import org.glassfish.ha.store.api.BackingStoreException;
import org.glassfish.ha.store.api.BackingStoreFactory;
import org.glassfish.ha.store.api.BackingStoreTransaction;
import org.glassfish.ha.store.impl.NoOpBackingStore;
import org.jvnet.hk2.annotations.Service;

@Service(name="noop")
public class NoOpBackingStoreFactory
implements BackingStoreFactory {
    private static BackingStoreTransaction _noOpTransaction = new BackingStoreTransaction(){

        @Override
        public void commit() {
        }
    };

    @Override
    public <K extends Serializable, V extends Serializable> BackingStore<K, V> createBackingStore(BackingStoreConfiguration<K, V> conf) throws BackingStoreException {
        NoOpBackingStore<K, V> store = new NoOpBackingStore<K, V>();
        store.initialize(conf);
        return store;
    }

    @Override
    public BackingStoreTransaction createBackingStoreTransaction() {
        return _noOpTransaction;
    }
}

