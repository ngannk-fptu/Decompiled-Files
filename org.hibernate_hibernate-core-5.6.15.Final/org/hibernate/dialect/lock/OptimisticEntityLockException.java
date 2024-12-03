/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect.lock;

import org.hibernate.dialect.lock.LockingStrategyException;

public class OptimisticEntityLockException
extends LockingStrategyException {
    public OptimisticEntityLockException(Object entity, String message) {
        super(entity, message);
    }

    public OptimisticEntityLockException(Object entity, String message, Throwable cause) {
        super(entity, message, cause);
    }
}

