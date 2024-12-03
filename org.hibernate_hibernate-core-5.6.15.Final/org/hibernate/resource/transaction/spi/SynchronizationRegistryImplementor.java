/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.resource.transaction.spi;

import org.hibernate.resource.transaction.spi.SynchronizationRegistry;

public interface SynchronizationRegistryImplementor
extends SynchronizationRegistry {
    public void notifySynchronizationsBeforeTransactionCompletion();

    public void notifySynchronizationsAfterTransactionCompletion(int var1);

    public void clearSynchronizations();
}

