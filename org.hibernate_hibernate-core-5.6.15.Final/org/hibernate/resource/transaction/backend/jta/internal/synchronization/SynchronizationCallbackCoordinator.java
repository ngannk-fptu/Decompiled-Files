/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.transaction.Synchronization
 */
package org.hibernate.resource.transaction.backend.jta.internal.synchronization;

import javax.transaction.Synchronization;

public interface SynchronizationCallbackCoordinator
extends Synchronization {
    public void synchronizationRegistered();

    public void processAnyDelayedAfterCompletion();
}

