/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jvnet.hk2.annotations.Contract
 */
package org.glassfish.ha.store.api;

import java.io.Serializable;
import org.glassfish.ha.store.api.BackingStore;
import org.glassfish.ha.store.api.BackingStoreConfiguration;
import org.glassfish.ha.store.api.BackingStoreException;
import org.glassfish.ha.store.api.BackingStoreTransaction;
import org.jvnet.hk2.annotations.Contract;

@Contract
public interface BackingStoreFactory {
    public <K extends Serializable, V extends Serializable> BackingStore<K, V> createBackingStore(BackingStoreConfiguration<K, V> var1) throws BackingStoreException;

    public BackingStoreTransaction createBackingStoreTransaction();
}

