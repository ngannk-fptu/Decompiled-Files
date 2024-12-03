/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.transaction;

import org.springframework.transaction.TransactionException;

public class TransactionUsageException
extends TransactionException {
    public TransactionUsageException(String msg) {
        super(msg);
    }

    public TransactionUsageException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

