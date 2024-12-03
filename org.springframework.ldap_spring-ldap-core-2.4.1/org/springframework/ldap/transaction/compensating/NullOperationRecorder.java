/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.transaction.compensating;

import org.springframework.ldap.transaction.compensating.NullOperationExecutor;
import org.springframework.transaction.compensating.CompensatingTransactionOperationExecutor;
import org.springframework.transaction.compensating.CompensatingTransactionOperationRecorder;

public class NullOperationRecorder
implements CompensatingTransactionOperationRecorder {
    @Override
    public CompensatingTransactionOperationExecutor recordOperation(Object[] args) {
        return new NullOperationExecutor();
    }
}

