/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.transaction;

import org.springframework.transaction.TransactionException;

public interface SavepointManager {
    public Object createSavepoint() throws TransactionException;

    public void rollbackToSavepoint(Object var1) throws TransactionException;

    public void releaseSavepoint(Object var1) throws TransactionException;
}

