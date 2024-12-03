/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.HibernateException
 *  org.hibernate.engine.jdbc.connections.spi.ConnectionProvider
 *  org.hibernate.hikaricp.internal.HikariCPConnectionProvider
 *  org.hibernate.service.spi.Configurable
 *  org.hibernate.service.spi.Stoppable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.hibernate;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.hibernate.HibernateException;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.hikaricp.internal.HikariCPConnectionProvider;
import org.hibernate.service.spi.Configurable;
import org.hibernate.service.spi.Stoppable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated(forRemoval=false)
public final class DelegatingHikariConnectionProvider
implements ConnectionProvider,
Configurable,
Stoppable {
    private static final Logger logger = LoggerFactory.getLogger(DelegatingHikariConnectionProvider.class);
    private static AtomicInteger totalCPTracker = new AtomicInteger();
    private final HikariCPConnectionProvider delegate = new HikariCPConnectionProvider();

    public DelegatingHikariConnectionProvider() {
        int totalCP = totalCPTracker.incrementAndGet();
        logger.info("Total HirakiCP: {}", (Object)totalCP);
        int totalAllowCP = Integer.getInteger("confluence.total.allow.hikaricp", -1);
        if (totalAllowCP != -1 && totalCP > totalAllowCP) {
            boolean shouldReset = Boolean.getBoolean("confluence.reset.total.allow.hikaricp");
            if (shouldReset) {
                totalCPTracker.set(0);
            }
            throw new UnsupportedOperationException("Total connection pool is excess the allowed number");
        }
    }

    public void configure(Map configurationValues) {
        this.delegate.configure(configurationValues);
    }

    public Connection getConnection() throws SQLException {
        Connection connection = this.delegate.getConnection();
        if (connection == null) {
            throw new HibernateException("Hikari ConnectionProvider hasn't been initialized");
        }
        return connection;
    }

    public void closeConnection(Connection conn) throws SQLException {
        this.delegate.closeConnection(conn);
    }

    public boolean supportsAggressiveRelease() {
        return this.delegate.supportsAggressiveRelease();
    }

    public void stop() {
        this.delegate.stop();
    }

    public boolean isUnwrappableAs(Class unwrapType) {
        return this.delegate.isUnwrappableAs(unwrapType);
    }

    public <T> T unwrap(Class<T> unwrapType) {
        return (T)this.delegate.unwrap(unwrapType);
    }
}

