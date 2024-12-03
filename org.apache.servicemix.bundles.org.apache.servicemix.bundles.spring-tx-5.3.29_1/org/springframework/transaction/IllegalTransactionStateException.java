/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.transaction;

import org.springframework.transaction.TransactionUsageException;

public class IllegalTransactionStateException
extends TransactionUsageException {
    public IllegalTransactionStateException(String msg) {
        super(msg);
    }

    public IllegalTransactionStateException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

