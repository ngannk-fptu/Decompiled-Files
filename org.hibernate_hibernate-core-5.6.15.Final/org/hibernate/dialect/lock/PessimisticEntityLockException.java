/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect.lock;

import org.hibernate.JDBCException;
import org.hibernate.dialect.lock.LockingStrategyException;

public class PessimisticEntityLockException
extends LockingStrategyException {
    public PessimisticEntityLockException(Object entity, String message, JDBCException cause) {
        super(entity, message, (Throwable)((Object)cause));
    }
}

