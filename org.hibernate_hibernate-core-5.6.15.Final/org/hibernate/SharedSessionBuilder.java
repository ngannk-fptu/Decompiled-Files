/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate;

import java.sql.Connection;
import org.hibernate.ConnectionReleaseMode;
import org.hibernate.FlushMode;
import org.hibernate.Interceptor;
import org.hibernate.SessionBuilder;

public interface SharedSessionBuilder<T extends SharedSessionBuilder>
extends SessionBuilder<T> {
    @Deprecated
    default public T transactionContext() {
        return this.connection();
    }

    public T connection();

    public T interceptor();

    @Deprecated
    public T connectionReleaseMode();

    public T connectionHandlingMode();

    public T autoJoinTransactions();

    public T flushMode();

    public T autoClose();

    @Deprecated
    default public T flushBeforeCompletion() {
        this.flushMode();
        return (T)this;
    }

    @Override
    public T interceptor(Interceptor var1);

    @Override
    public T noInterceptor();

    @Override
    public T connection(Connection var1);

    @Override
    public T connectionReleaseMode(ConnectionReleaseMode var1);

    @Override
    public T autoJoinTransactions(boolean var1);

    @Override
    public T autoClose(boolean var1);

    @Override
    default public T flushBeforeCompletion(boolean flushBeforeCompletion) {
        if (flushBeforeCompletion) {
            this.flushMode(FlushMode.ALWAYS);
        } else {
            this.flushMode(FlushMode.MANUAL);
        }
        return (T)this;
    }
}

