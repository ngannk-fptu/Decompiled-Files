/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.resource.jdbc.spi;

import org.hibernate.ConnectionAcquisitionMode;
import org.hibernate.ConnectionReleaseMode;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.resource.jdbc.spi.JdbcObserver;
import org.hibernate.resource.jdbc.spi.PhysicalConnectionHandlingMode;
import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.hibernate.service.ServiceRegistry;

public interface JdbcSessionContext {
    public boolean isScrollableResultSetsEnabled();

    public boolean isGetGeneratedKeysEnabled();

    public int getFetchSize();

    public PhysicalConnectionHandlingMode getPhysicalConnectionHandlingMode();

    public boolean doesConnectionProviderDisableAutoCommit();

    @Deprecated
    public ConnectionReleaseMode getConnectionReleaseMode();

    @Deprecated
    public ConnectionAcquisitionMode getConnectionAcquisitionMode();

    public StatementInspector getStatementInspector();

    public JdbcObserver getObserver();

    public SessionFactoryImplementor getSessionFactory();

    public ServiceRegistry getServiceRegistry();
}

