/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.dao;

import org.springframework.dao.PessimisticLockingFailureException;

public class CannotSerializeTransactionException
extends PessimisticLockingFailureException {
    public CannotSerializeTransactionException(String msg) {
        super(msg);
    }

    public CannotSerializeTransactionException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

