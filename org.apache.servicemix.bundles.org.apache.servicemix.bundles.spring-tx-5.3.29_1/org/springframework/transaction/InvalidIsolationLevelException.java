/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.transaction;

import org.springframework.transaction.TransactionUsageException;

public class InvalidIsolationLevelException
extends TransactionUsageException {
    public InvalidIsolationLevelException(String msg) {
        super(msg);
    }
}

