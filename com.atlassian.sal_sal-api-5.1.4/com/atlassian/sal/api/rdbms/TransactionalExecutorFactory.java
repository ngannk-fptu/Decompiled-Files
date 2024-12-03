/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 */
package com.atlassian.sal.api.rdbms;

import com.atlassian.annotations.PublicApi;
import com.atlassian.sal.api.rdbms.TransactionalExecutor;

@PublicApi
public interface TransactionalExecutorFactory {
    default public TransactionalExecutor create() {
        return this.createExecutor(false, false);
    }

    default public TransactionalExecutor createReadOnly() {
        return this.createExecutor(true, false);
    }

    default public TransactionalExecutor createExecutor(boolean readOnly, boolean requiresNew) {
        throw new UnsupportedOperationException("not implemented by default");
    }

    @Deprecated
    default public TransactionalExecutor createExecutor() {
        return this.createExecutor(true, false);
    }
}

