/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.transaction.Synchronization
 */
package org.hibernate.engine.transaction.jta.platform.internal;

import java.io.Serializable;
import javax.transaction.Synchronization;

public interface JtaSynchronizationStrategy
extends Serializable {
    public void registerSynchronization(Synchronization var1);

    public boolean canRegisterSynchronization();
}

