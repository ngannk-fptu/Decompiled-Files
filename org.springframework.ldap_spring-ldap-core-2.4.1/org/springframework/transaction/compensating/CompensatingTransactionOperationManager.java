/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.transaction.compensating;

public interface CompensatingTransactionOperationManager {
    public void performOperation(Object var1, String var2, Object[] var3);

    public void rollback();

    public void commit();
}

