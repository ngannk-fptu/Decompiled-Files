/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.store.tx;

public class TransactionException
extends RuntimeException {
    public TransactionException(String message) {
        super(message);
    }

    public TransactionException(String message, Throwable cause) {
        super(message, cause);
    }
}

