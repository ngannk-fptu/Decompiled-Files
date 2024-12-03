/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  io.atlassian.fugue.Option
 *  javax.annotation.Nonnull
 */
package com.atlassian.sal.api.rdbms;

import com.atlassian.annotations.PublicApi;
import com.atlassian.sal.api.rdbms.ConnectionCallback;
import io.atlassian.fugue.Option;
import javax.annotation.Nonnull;

@PublicApi
public interface TransactionalExecutor {
    public <A> A execute(@Nonnull ConnectionCallback<A> var1);

    @Nonnull
    public Option<String> getSchemaName();

    @Nonnull
    public TransactionalExecutor readOnly();

    @Nonnull
    public TransactionalExecutor readWrite();

    @Nonnull
    public TransactionalExecutor newTransaction();

    @Nonnull
    public TransactionalExecutor existingTransaction();
}

