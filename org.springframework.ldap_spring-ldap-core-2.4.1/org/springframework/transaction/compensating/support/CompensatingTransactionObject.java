/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.transaction.compensating.support;

import org.springframework.transaction.compensating.support.CompensatingTransactionHolderSupport;

public class CompensatingTransactionObject {
    private CompensatingTransactionHolderSupport holder;

    public CompensatingTransactionObject(CompensatingTransactionHolderSupport holder) {
        this.holder = holder;
    }

    public CompensatingTransactionHolderSupport getHolder() {
        return this.holder;
    }

    public void setHolder(CompensatingTransactionHolderSupport holder) {
        this.holder = holder;
    }
}

