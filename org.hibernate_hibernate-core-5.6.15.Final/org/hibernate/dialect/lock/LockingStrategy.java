/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect.lock;

import java.io.Serializable;
import org.hibernate.StaleObjectStateException;
import org.hibernate.dialect.lock.LockingStrategyException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

public interface LockingStrategy {
    public void lock(Serializable var1, Object var2, Object var3, int var4, SharedSessionContractImplementor var5) throws StaleObjectStateException, LockingStrategyException;
}

