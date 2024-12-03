/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.transaction;

import org.springframework.transaction.TransactionException;

public class UnexpectedRollbackException
extends TransactionException {
    public UnexpectedRollbackException(String msg) {
        super(msg);
    }

    public UnexpectedRollbackException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

