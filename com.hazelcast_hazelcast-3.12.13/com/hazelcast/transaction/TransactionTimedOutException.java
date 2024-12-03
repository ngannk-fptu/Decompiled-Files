/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.transaction;

import com.hazelcast.transaction.TransactionException;

public class TransactionTimedOutException
extends TransactionException {
    public TransactionTimedOutException() {
    }

    public TransactionTimedOutException(String message) {
        super(message);
    }

    public TransactionTimedOutException(String message, Throwable cause) {
        super(message, cause);
    }

    public TransactionTimedOutException(Throwable cause) {
        super(cause);
    }
}

