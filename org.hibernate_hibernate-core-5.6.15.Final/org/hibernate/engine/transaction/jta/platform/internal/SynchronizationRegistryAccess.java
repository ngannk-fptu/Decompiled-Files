/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.transaction.TransactionSynchronizationRegistry
 */
package org.hibernate.engine.transaction.jta.platform.internal;

import java.io.Serializable;
import javax.transaction.TransactionSynchronizationRegistry;

public interface SynchronizationRegistryAccess
extends Serializable {
    public TransactionSynchronizationRegistry getSynchronizationRegistry();
}

