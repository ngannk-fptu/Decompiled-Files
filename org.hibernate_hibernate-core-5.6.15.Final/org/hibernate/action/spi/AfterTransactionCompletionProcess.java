/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.action.spi;

import org.hibernate.engine.spi.SharedSessionContractImplementor;

public interface AfterTransactionCompletionProcess {
    public void doAfterTransactionCompletion(boolean var1, SharedSessionContractImplementor var2);
}

