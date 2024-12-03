/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.dao;

import org.springframework.dao.PessimisticLockingFailureException;

public class CannotAcquireLockException
extends PessimisticLockingFailureException {
    public CannotAcquireLockException(String msg) {
        super(msg);
    }

    public CannotAcquireLockException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

