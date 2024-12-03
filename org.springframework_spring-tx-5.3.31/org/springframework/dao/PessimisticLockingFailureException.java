/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.dao;

import org.springframework.dao.ConcurrencyFailureException;

public class PessimisticLockingFailureException
extends ConcurrencyFailureException {
    public PessimisticLockingFailureException(String msg) {
        super(msg);
    }

    public PessimisticLockingFailureException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

