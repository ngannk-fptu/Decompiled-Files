/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.transaction;

import org.springframework.transaction.TransactionException;

public class TransactionTimedOutException
extends TransactionException {
    public TransactionTimedOutException(String msg) {
        super(msg);
    }

    public TransactionTimedOutException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

