/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.transaction;

import org.springframework.transaction.CannotCreateTransactionException;

public class NestedTransactionNotSupportedException
extends CannotCreateTransactionException {
    public NestedTransactionNotSupportedException(String msg) {
        super(msg);
    }

    public NestedTransactionNotSupportedException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

