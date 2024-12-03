/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.dao;

import org.springframework.dao.PessimisticLockingFailureException;

public class DeadlockLoserDataAccessException
extends PessimisticLockingFailureException {
    public DeadlockLoserDataAccessException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

