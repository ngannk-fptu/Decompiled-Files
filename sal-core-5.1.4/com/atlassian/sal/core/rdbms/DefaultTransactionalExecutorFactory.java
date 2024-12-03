/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.rdbms.TransactionalExecutor
 *  com.atlassian.sal.api.rdbms.TransactionalExecutorFactory
 *  com.atlassian.sal.spi.HostConnectionAccessor
 */
package com.atlassian.sal.core.rdbms;

import com.atlassian.sal.api.rdbms.TransactionalExecutor;
import com.atlassian.sal.api.rdbms.TransactionalExecutorFactory;
import com.atlassian.sal.core.rdbms.DefaultTransactionalExecutor;
import com.atlassian.sal.spi.HostConnectionAccessor;

public class DefaultTransactionalExecutorFactory
implements TransactionalExecutorFactory {
    private final HostConnectionAccessor hostConnectionAccessor;

    public DefaultTransactionalExecutorFactory(HostConnectionAccessor hostConnectionAccessor) {
        this.hostConnectionAccessor = hostConnectionAccessor;
    }

    public TransactionalExecutor createExecutor(boolean readOnly, boolean newTransaction) {
        return new DefaultTransactionalExecutor(this.hostConnectionAccessor, readOnly, newTransaction);
    }
}

