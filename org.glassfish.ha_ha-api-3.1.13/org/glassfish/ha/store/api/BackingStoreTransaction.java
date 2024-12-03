/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.ha.store.api;

import org.glassfish.ha.store.api.BackingStoreException;

public interface BackingStoreTransaction {
    public void commit() throws BackingStoreException;
}

