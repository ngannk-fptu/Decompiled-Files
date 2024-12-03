/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.internal;

import java.sql.Connection;
import java.util.List;
import java.util.TimeZone;
import org.hibernate.FlushMode;
import org.hibernate.Interceptor;
import org.hibernate.SessionEventListener;
import org.hibernate.engine.spi.SessionOwner;
import org.hibernate.resource.jdbc.spi.PhysicalConnectionHandlingMode;
import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.hibernate.resource.transaction.backend.jta.internal.synchronization.AfterCompletionAction;
import org.hibernate.resource.transaction.backend.jta.internal.synchronization.ExceptionMapper;
import org.hibernate.resource.transaction.backend.jta.internal.synchronization.ManagedFlushChecker;

public interface SessionCreationOptions {
    public boolean shouldAutoJoinTransactions();

    public FlushMode getInitialSessionFlushMode();

    public boolean shouldAutoClose();

    public boolean shouldAutoClear();

    public Connection getConnection();

    public Interceptor getInterceptor();

    public StatementInspector getStatementInspector();

    public PhysicalConnectionHandlingMode getPhysicalConnectionHandlingMode();

    public String getTenantIdentifier();

    public TimeZone getJdbcTimeZone();

    public List<SessionEventListener> getCustomSessionEventListener();

    @Deprecated
    public SessionOwner getSessionOwner();

    public ExceptionMapper getExceptionMapper();

    public AfterCompletionAction getAfterCompletionAction();

    public ManagedFlushChecker getManagedFlushChecker();

    public boolean isQueryParametersValidationEnabled();
}

