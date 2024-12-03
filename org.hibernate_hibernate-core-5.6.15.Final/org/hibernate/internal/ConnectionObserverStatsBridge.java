/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.internal;

import java.io.Serializable;
import java.sql.Connection;
import org.hibernate.engine.jdbc.spi.ConnectionObserver;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.stat.spi.StatisticsImplementor;

public class ConnectionObserverStatsBridge
implements ConnectionObserver,
Serializable {
    private final SessionFactoryImplementor sessionFactory;

    public ConnectionObserverStatsBridge(SessionFactoryImplementor sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void physicalConnectionObtained(Connection connection) {
        StatisticsImplementor statistics = this.sessionFactory.getStatistics();
        if (statistics.isStatisticsEnabled()) {
            statistics.connect();
        }
    }

    @Override
    public void physicalConnectionReleased() {
    }

    @Override
    public void logicalConnectionClosed() {
    }

    @Override
    public void statementPrepared() {
        StatisticsImplementor statistics = this.sessionFactory.getStatistics();
        if (statistics.isStatisticsEnabled()) {
            statistics.prepareStatement();
        }
    }
}

