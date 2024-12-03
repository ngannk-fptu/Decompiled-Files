/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.BasicLogger
 *  org.jboss.logging.DelegatingBasicLogger
 *  org.jboss.logging.Logger
 *  org.jboss.logging.Logger$Level
 */
package org.hibernate.internal.log;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Properties;
import org.hibernate.internal.log.ConnectionPoolingLogger;
import org.jboss.logging.BasicLogger;
import org.jboss.logging.DelegatingBasicLogger;
import org.jboss.logging.Logger;

public class ConnectionPoolingLogger_$logger
extends DelegatingBasicLogger
implements ConnectionPoolingLogger,
BasicLogger,
Serializable {
    private static final long serialVersionUID = 1L;
    private static final String FQCN = ConnectionPoolingLogger_$logger.class.getName();
    private static final Locale LOCALE = Locale.ROOT;

    public ConnectionPoolingLogger_$logger(Logger log) {
        super(log);
    }

    protected Locale getLoggingLocale() {
        return LOCALE;
    }

    @Override
    public final void connectionProperties(Properties connectionProps) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.connectionProperties$str(), (Object)connectionProps);
    }

    protected String connectionProperties$str() {
        return "HHH10001001: Connection properties: %s";
    }

    @Override
    public final void usingHibernateBuiltInConnectionPool() {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.usingHibernateBuiltInConnectionPool$str(), new Object[0]);
    }

    protected String usingHibernateBuiltInConnectionPool$str() {
        return "HHH10001002: Using Hibernate built-in connection pool (not for production use!)";
    }

    @Override
    public final void autoCommitMode(boolean autocommit) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.autoCommitMode$str(), (Object)autocommit);
    }

    protected String autoCommitMode$str() {
        return "HHH10001003: Autocommit mode: %s";
    }

    protected String jdbcUrlNotSpecified$str() {
        return "HHH10001004: JDBC URL was not specified by property %s";
    }

    @Override
    public final String jdbcUrlNotSpecified(String url) {
        return String.format(this.getLoggingLocale(), this.jdbcUrlNotSpecified$str(), url);
    }

    @Override
    public final void usingDriver(String driverClassName, String url) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.usingDriver$str(), (Object)driverClassName, (Object)url);
    }

    protected String usingDriver$str() {
        return "HHH10001005: using driver [%s] at URL [%s]";
    }

    @Override
    public final void jdbcDriverNotSpecified(String driver) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.jdbcDriverNotSpecified$str(), (Object)driver);
    }

    protected String jdbcDriverNotSpecified$str() {
        return "HHH10001006: No JDBC Driver class was specified by property %s";
    }

    @Override
    public final void jdbcIsolationLevel(String isolationLevelToString) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.jdbcIsolationLevel$str(), (Object)isolationLevelToString);
    }

    protected String jdbcIsolationLevel$str() {
        return "HHH10001007: JDBC isolation level: %s";
    }

    @Override
    public final void cleaningUpConnectionPool(String url) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.cleaningUpConnectionPool$str(), (Object)url);
    }

    protected String cleaningUpConnectionPool$str() {
        return "HHH10001008: Cleaning up connection pool [%s]";
    }

    @Override
    public final void unableToClosePooledConnection(SQLException e) {
        this.log.logf(FQCN, Logger.Level.WARN, (Throwable)e, this.unableToClosePooledConnection$str(), new Object[0]);
    }

    protected String unableToClosePooledConnection$str() {
        return "HHH10001009: Problem closing pooled connection";
    }
}

