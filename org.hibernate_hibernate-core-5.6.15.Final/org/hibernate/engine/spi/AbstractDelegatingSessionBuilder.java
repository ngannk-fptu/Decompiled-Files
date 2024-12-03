/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.spi;

import java.sql.Connection;
import java.util.TimeZone;
import org.hibernate.ConnectionReleaseMode;
import org.hibernate.FlushMode;
import org.hibernate.Interceptor;
import org.hibernate.Session;
import org.hibernate.SessionBuilder;
import org.hibernate.SessionEventListener;
import org.hibernate.resource.jdbc.spi.PhysicalConnectionHandlingMode;
import org.hibernate.resource.jdbc.spi.StatementInspector;

public abstract class AbstractDelegatingSessionBuilder<T extends SessionBuilder>
implements SessionBuilder<T> {
    private final SessionBuilder delegate;

    public AbstractDelegatingSessionBuilder(SessionBuilder delegate) {
        this.delegate = delegate;
    }

    protected T getThis() {
        return (T)this;
    }

    protected SessionBuilder delegate() {
        return this.delegate;
    }

    @Override
    public Session openSession() {
        return this.delegate.openSession();
    }

    @Override
    public T interceptor(Interceptor interceptor) {
        this.delegate.interceptor(interceptor);
        return this.getThis();
    }

    @Override
    public T noInterceptor() {
        this.delegate.noInterceptor();
        return this.getThis();
    }

    @Override
    public T statementInspector(StatementInspector statementInspector) {
        this.delegate.statementInspector(statementInspector);
        return this.getThis();
    }

    @Override
    public T connection(Connection connection) {
        this.delegate.connection(connection);
        return this.getThis();
    }

    @Override
    public T connectionReleaseMode(ConnectionReleaseMode connectionReleaseMode) {
        this.delegate.connectionReleaseMode(connectionReleaseMode);
        return this.getThis();
    }

    @Override
    public T autoJoinTransactions(boolean autoJoinTransactions) {
        this.delegate.autoJoinTransactions(autoJoinTransactions);
        return this.getThis();
    }

    @Override
    public T autoClose(boolean autoClose) {
        this.delegate.autoClose(autoClose);
        return this.getThis();
    }

    @Override
    public T flushBeforeCompletion(boolean flushBeforeCompletion) {
        this.delegate.flushBeforeCompletion(flushBeforeCompletion);
        return this.getThis();
    }

    @Override
    public T tenantIdentifier(String tenantIdentifier) {
        this.delegate.tenantIdentifier(tenantIdentifier);
        return this.getThis();
    }

    @Override
    public T eventListeners(SessionEventListener ... listeners) {
        this.delegate.eventListeners(listeners);
        return this.getThis();
    }

    @Override
    public T clearEventListeners() {
        this.delegate.clearEventListeners();
        return this.getThis();
    }

    @Override
    public T jdbcTimeZone(TimeZone timeZone) {
        this.delegate.jdbcTimeZone(timeZone);
        return this.getThis();
    }

    @Override
    public T setQueryParameterValidation(boolean enabled) {
        this.delegate.setQueryParameterValidation(enabled);
        return this.getThis();
    }

    @Override
    public T connectionHandlingMode(PhysicalConnectionHandlingMode mode) {
        this.delegate.connectionHandlingMode(mode);
        return this.getThis();
    }

    @Override
    public T autoClear(boolean autoClear) {
        this.delegate.autoClear(autoClear);
        return this.getThis();
    }

    @Override
    public T flushMode(FlushMode flushMode) {
        this.delegate.flushMode(flushMode);
        return this.getThis();
    }
}

