/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.transaction;

import org.springframework.transaction.TransactionUsageException;

public class InvalidTimeoutException
extends TransactionUsageException {
    private final int timeout;

    public InvalidTimeoutException(String msg, int timeout) {
        super(msg);
        this.timeout = timeout;
    }

    public int getTimeout() {
        return this.timeout;
    }
}

