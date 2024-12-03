/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.transaction.compensating;

import org.springframework.transaction.compensating.CompensatingTransactionOperationRecorder;

public interface CompensatingTransactionOperationFactory {
    public CompensatingTransactionOperationRecorder createRecordingOperation(Object var1, String var2);
}

