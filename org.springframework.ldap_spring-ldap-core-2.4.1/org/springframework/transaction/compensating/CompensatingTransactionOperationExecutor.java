/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.transaction.compensating;

public interface CompensatingTransactionOperationExecutor {
    public void rollback();

    public void commit();

    public void performOperation();
}

