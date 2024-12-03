/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.transaction.compensating;

import org.springframework.transaction.compensating.CompensatingTransactionOperationExecutor;

public interface CompensatingTransactionOperationRecorder {
    public CompensatingTransactionOperationExecutor recordOperation(Object[] var1);
}

