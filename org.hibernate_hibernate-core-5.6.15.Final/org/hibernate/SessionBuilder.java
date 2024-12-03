/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate;

import java.sql.Connection;
import java.util.TimeZone;
import org.hibernate.ConnectionReleaseMode;
import org.hibernate.FlushMode;
import org.hibernate.Interceptor;
import org.hibernate.Session;
import org.hibernate.SessionEventListener;
import org.hibernate.resource.jdbc.spi.PhysicalConnectionHandlingMode;
import org.hibernate.resource.jdbc.spi.StatementInspector;

public interface SessionBuilder<T extends SessionBuilder> {
    public Session openSession();

    public T interceptor(Interceptor var1);

    public T noInterceptor();

    public T statementInspector(StatementInspector var1);

    public T connection(Connection var1);

    public T connectionHandlingMode(PhysicalConnectionHandlingMode var1);

    public T autoJoinTransactions(boolean var1);

    public T autoClear(boolean var1);

    public T flushMode(FlushMode var1);

    public T tenantIdentifier(String var1);

    public T eventListeners(SessionEventListener ... var1);

    public T clearEventListeners();

    public T jdbcTimeZone(TimeZone var1);

    default public T setQueryParameterValidation(boolean enabled) {
        return (T)this;
    }

    @Deprecated
    public T autoClose(boolean var1);

    @Deprecated
    public T connectionReleaseMode(ConnectionReleaseMode var1);

    @Deprecated
    default public T flushBeforeCompletion(boolean flushBeforeCompletion) {
        if (flushBeforeCompletion) {
            this.flushMode(FlushMode.ALWAYS);
        } else {
            this.flushMode(FlushMode.MANUAL);
        }
        return (T)this;
    }
}

