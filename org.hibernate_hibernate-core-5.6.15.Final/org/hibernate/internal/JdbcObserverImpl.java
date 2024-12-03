/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.internal;

import java.sql.Connection;
import org.hibernate.engine.spi.SessionEventListenerManager;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.internal.ConnectionObserverStatsBridge;
import org.hibernate.internal.FastSessionServices;
import org.hibernate.resource.jdbc.spi.JdbcObserver;

public final class JdbcObserverImpl
implements JdbcObserver {
    private final ConnectionObserverStatsBridge observer;
    private final SessionEventListenerManager eventListenerManager;
    private final SharedSessionContractImplementor session;

    public JdbcObserverImpl(SharedSessionContractImplementor session, FastSessionServices fastSessionServices) {
        this.session = session;
        this.observer = fastSessionServices.getDefaultJdbcObserver();
        this.eventListenerManager = session.getEventListenerManager();
    }

    @Override
    public void jdbcConnectionAcquisitionStart() {
    }

    @Override
    public void jdbcConnectionAcquisitionEnd(Connection connection) {
        this.observer.physicalConnectionObtained(connection);
    }

    @Override
    public void jdbcConnectionReleaseStart() {
    }

    @Override
    public void jdbcConnectionReleaseEnd() {
        this.observer.physicalConnectionReleased();
    }

    @Override
    public void jdbcPrepareStatementStart() {
        this.eventListenerManager.jdbcPrepareStatementStart();
    }

    @Override
    public void jdbcPrepareStatementEnd() {
        this.observer.statementPrepared();
        this.eventListenerManager.jdbcPrepareStatementEnd();
    }

    @Override
    public void jdbcExecuteStatementStart() {
        this.eventListenerManager.jdbcExecuteStatementStart();
    }

    @Override
    public void jdbcExecuteStatementEnd() {
        this.eventListenerManager.jdbcExecuteStatementEnd();
    }

    @Override
    public void jdbcExecuteBatchStart() {
        this.eventListenerManager.jdbcExecuteBatchStart();
    }

    @Override
    public void jdbcExecuteBatchEnd() {
        this.eventListenerManager.jdbcExecuteBatchEnd();
    }

    @Override
    public void jdbcReleaseRegistryResourcesStart() {
        this.session.getJdbcCoordinator().abortBatch();
    }

    @Override
    public void jdbcReleaseRegistryResourcesEnd() {
    }
}

