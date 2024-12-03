/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.transaction;

import org.springframework.transaction.TransactionUsageException;

public class NoTransactionException
extends TransactionUsageException {
    public NoTransactionException(String msg) {
        super(msg);
    }

    public NoTransactionException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

