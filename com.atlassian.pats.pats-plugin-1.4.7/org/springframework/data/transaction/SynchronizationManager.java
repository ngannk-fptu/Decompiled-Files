/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.data.transaction;

interface SynchronizationManager {
    public void initSynchronization();

    public boolean isSynchronizationActive();

    public void clearSynchronization();
}

