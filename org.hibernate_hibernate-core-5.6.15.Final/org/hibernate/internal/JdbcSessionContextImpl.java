/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.internal;

import org.hibernate.ConnectionAcquisitionMode;
import org.hibernate.ConnectionReleaseMode;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.internal.FastSessionServices;
import org.hibernate.internal.JdbcObserverImpl;
import org.hibernate.resource.jdbc.spi.JdbcObserver;
import org.hibernate.resource.jdbc.spi.JdbcSessionContext;
import org.hibernate.resource.jdbc.spi.PhysicalConnectionHandlingMode;
import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.hibernate.service.ServiceRegistry;

public class JdbcSessionContextImpl
implements JdbcSessionContext {
    private final SessionFactoryImplementor sessionFactory;
    private final StatementInspector statementInspector;
    private final PhysicalConnectionHandlingMode connectionHandlingMode;
    private final transient ServiceRegistry serviceRegistry;
    private final transient JdbcObserver jdbcObserver;

    public JdbcSessionContextImpl(SharedSessionContractImplementor session, StatementInspector statementInspector, PhysicalConnectionHandlingMode connectionHandlingMode, FastSessionServices fastSessionServices) {
        this.sessionFactory = session.getFactory();
        this.statementInspector = statementInspector;
        this.connectionHandlingMode = connectionHandlingMode;
        this.serviceRegistry = this.sessionFactory.getServiceRegistry();
        this.jdbcObserver = new JdbcObserverImpl(session, fastSessionServices);
        if (this.statementInspector == null) {
            throw new IllegalArgumentException("StatementInspector cannot be null");
        }
    }

    @Override
    public boolean isScrollableResultSetsEnabled() {
        return this.settings().isScrollableResultSetsEnabled();
    }

    @Override
    public boolean isGetGeneratedKeysEnabled() {
        return this.settings().isGetGeneratedKeysEnabled();
    }

    @Override
    public int getFetchSize() {
        return this.settings().getJdbcFetchSize();
    }

    @Override
    public PhysicalConnectionHandlingMode getPhysicalConnectionHandlingMode() {
        return this.connectionHandlingMode;
    }

    @Override
    public boolean doesConnectionProviderDisableAutoCommit() {
        return this.settings().doesConnectionProviderDisableAutoCommit();
    }

    @Override
    public ConnectionReleaseMode getConnectionReleaseMode() {
        return this.connectionHandlingMode.getReleaseMode();
    }

    @Override
    public ConnectionAcquisitionMode getConnectionAcquisitionMode() {
        return this.connectionHandlingMode.getAcquisitionMode();
    }

    @Override
    public StatementInspector getStatementInspector() {
        return this.statementInspector;
    }

    @Override
    public JdbcObserver getObserver() {
        return this.jdbcObserver;
    }

    @Override
    public SessionFactoryImplementor getSessionFactory() {
        return this.sessionFactory;
    }

    @Override
    public ServiceRegistry getServiceRegistry() {
        return this.serviceRegistry;
    }

    private SessionFactoryOptions settings() {
        return this.sessionFactory.getSessionFactoryOptions();
    }
}

