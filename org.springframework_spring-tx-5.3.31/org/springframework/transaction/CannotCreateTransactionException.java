/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.transaction;

import org.springframework.transaction.TransactionException;

public class CannotCreateTransactionException
extends TransactionException {
    public CannotCreateTransactionException(String msg) {
        super(msg);
    }

    public CannotCreateTransactionException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

