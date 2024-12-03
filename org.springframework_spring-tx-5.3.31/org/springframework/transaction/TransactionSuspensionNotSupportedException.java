/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.transaction;

import org.springframework.transaction.CannotCreateTransactionException;

public class TransactionSuspensionNotSupportedException
extends CannotCreateTransactionException {
    public TransactionSuspensionNotSupportedException(String msg) {
        super(msg);
    }

    public TransactionSuspensionNotSupportedException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

