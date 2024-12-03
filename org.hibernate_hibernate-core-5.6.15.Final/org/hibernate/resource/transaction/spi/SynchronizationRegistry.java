/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.transaction.Synchronization
 */
package org.hibernate.resource.transaction.spi;

import java.io.Serializable;
import javax.transaction.Synchronization;

public interface SynchronizationRegistry
extends Serializable {
    public void registerSynchronization(Synchronization var1);
}

