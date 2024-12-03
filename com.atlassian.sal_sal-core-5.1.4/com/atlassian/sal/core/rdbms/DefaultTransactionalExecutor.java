/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.util.PluginKeyStack
 *  com.atlassian.sal.api.rdbms.ConnectionCallback
 *  com.atlassian.sal.api.rdbms.RdbmsException
 *  com.atlassian.sal.api.rdbms.TransactionalExecutor
 *  com.atlassian.sal.spi.HostConnectionAccessor
 *  com.atlassian.util.profiling.Metrics
 *  com.atlassian.util.profiling.Ticker
 *  com.google.common.annotations.VisibleForTesting
 *  io.atlassian.fugue.Option
 *  javax.annotation.Nonnull
 */
package com.atlassian.sal.core.rdbms;

import com.atlassian.plugin.util.PluginKeyStack;
import com.atlassian.sal.api.rdbms.ConnectionCallback;
import com.atlassian.sal.api.rdbms.RdbmsException;
import com.atlassian.sal.api.rdbms.TransactionalExecutor;
import com.atlassian.sal.core.rdbms.WrappedConnection;
import com.atlassian.sal.spi.HostConnectionAccessor;
import com.atlassian.util.profiling.Metrics;
import com.atlassian.util.profiling.Ticker;
import com.google.common.annotations.VisibleForTesting;
import io.atlassian.fugue.Option;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import javax.annotation.Nonnull;

public class DefaultTransactionalExecutor
implements TransactionalExecutor {
    private final HostConnectionAccessor hostConnectionAccessor;
    private static final String TASK_NAME = "taskName";
    @VisibleForTesting
    boolean readOnly;
    @VisibleForTesting
    boolean newTransaction;
    @VisibleForTesting
    String pluginKeyAtCreation;

    public DefaultTransactionalExecutor(@Nonnull HostConnectionAccessor hostConnectionAccessor, boolean readOnly, boolean newTransaction) {
        this.hostConnectionAccessor = hostConnectionAccessor;
        this.readOnly = readOnly;
        this.newTransaction = newTransaction;
        this.pluginKeyAtCreation = PluginKeyStack.getFirstPluginKey();
    }

    public <A> A execute(@Nonnull ConnectionCallback<A> callback) {
        String invokerPluginKey = Optional.ofNullable(PluginKeyStack.getFirstPluginKey()).orElse(this.pluginKeyAtCreation);
        try (Ticker ignored = Metrics.metric((String)"db.sal.transactionalExecutor").withAnalytics().invokerPluginKey(invokerPluginKey).tag(TASK_NAME, callback.getClass().getName()).startLongRunningTimer();){
            Object object = this.hostConnectionAccessor.execute(this.readOnly, this.newTransaction, connection -> this.executeInternal(connection, callback));
            return (A)object;
        }
    }

    @Nonnull
    public Option<String> getSchemaName() {
        return this.hostConnectionAccessor.getSchemaName();
    }

    @Nonnull
    public TransactionalExecutor readOnly() {
        this.readOnly = true;
        return this;
    }

    @Nonnull
    public TransactionalExecutor readWrite() {
        this.readOnly = false;
        return this;
    }

    @Nonnull
    public TransactionalExecutor newTransaction() {
        this.newTransaction = true;
        return this;
    }

    @Nonnull
    public TransactionalExecutor existingTransaction() {
        this.newTransaction = false;
        return this;
    }

    @VisibleForTesting
    <A> A executeInternal(@Nonnull Connection connection, @Nonnull ConnectionCallback<A> callback) {
        this.assertAutoCommitFalse(connection);
        try (WrappedConnection wrappedConnection = new WrappedConnection(connection);){
            Object object = callback.execute((Connection)wrappedConnection);
            return (A)object;
        }
    }

    private void assertAutoCommitFalse(Connection connection) {
        try {
            if (connection.getAutoCommit()) {
                throw new IllegalStateException("com.atlassian.sal.spi.HostConnectionAccessor returned connection with autocommit set");
            }
        }
        catch (SQLException e) {
            throw new RdbmsException("unable to invoke java.sql.Connection#getAutoCommit", (Throwable)e);
        }
    }
}

