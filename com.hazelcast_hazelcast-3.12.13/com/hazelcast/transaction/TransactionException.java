/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.transaction;

import com.hazelcast.core.HazelcastException;

public class TransactionException
extends HazelcastException {
    public TransactionException() {
    }

    public TransactionException(String message) {
        super(message);
    }

    public TransactionException(String message, Throwable cause) {
        super(message, cause);
    }

    public TransactionException(Throwable cause) {
        super(cause);
    }
}

