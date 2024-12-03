/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.transaction;

import java.io.Flushable;
import org.springframework.transaction.SavepointManager;
import org.springframework.transaction.TransactionExecution;

public interface TransactionStatus
extends TransactionExecution,
SavepointManager,
Flushable {
    public boolean hasSavepoint();

    @Override
    public void flush();
}

