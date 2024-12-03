/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.transaction.support;

import org.springframework.transaction.support.AbstractTransactionStatus;

public class SimpleTransactionStatus
extends AbstractTransactionStatus {
    private final boolean newTransaction;

    public SimpleTransactionStatus() {
        this(true);
    }

    public SimpleTransactionStatus(boolean newTransaction) {
        this.newTransaction = newTransaction;
    }

    @Override
    public boolean isNewTransaction() {
        return this.newTransaction;
    }
}

