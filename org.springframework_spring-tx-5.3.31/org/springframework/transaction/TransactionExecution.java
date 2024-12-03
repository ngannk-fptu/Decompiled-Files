/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.transaction;

public interface TransactionExecution {
    public boolean isNewTransaction();

    public void setRollbackOnly();

    public boolean isRollbackOnly();

    public boolean isCompleted();
}

