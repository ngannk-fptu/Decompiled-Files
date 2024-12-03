/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.internal.log.ConnectionPoolingLogger
 *  org.jboss.logging.BasicLogger
 *  org.jboss.logging.DelegatingBasicLogger
 *  org.jboss.logging.Logger
 *  org.jboss.logging.Logger$Level
 */
package org.hibernate.c3p0.internal;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Properties;
import org.hibernate.c3p0.internal.C3P0MessageLogger;
import org.hibernate.internal.log.ConnectionPoolingLogger;
import org.jboss.logging.BasicLogger;
import org.jboss.logging.DelegatingBasicLogger;
import org.jboss.logging.Logger;

public class C3P0MessageLogger_$logger
extends DelegatingBasicLogger
implements C3P0MessageLogger,
ConnectionPoolingLogger,
BasicLogger,
Serializable {
    private static final long serialVersionUID = 1L;
    private static final String FQCN = C3P0MessageLogger_$logger.class.getName();
    private static final Locale LOCALE = Locale.ROOT;

    public C3P0MessageLogger_$logger(Logger log) {
        super(log);
    }

    protected Locale getLoggingLocale() {
        return LOCALE;
    }

    @Override
    public final void bothHibernateAndC3p0StylesSet(String hibernateStyle, String c3p0Style) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.bothHibernateAndC3p0StylesSet$str(), (Object)hibernateStyle, (Object)c3p0Style);
    }

    protected String bothHibernateAndC3p0StylesSet$str() {
        return "HHH010001: Both hibernate-style property '%1$s' and c3p0-style property '%2$s' have been set in Hibernate properties.  Hibernate-style property '%1$s' will be used and c3p0-style property '%2$s' will be ignored!";
    }

    @Override
    public final void c3p0UsingDriver(String jdbcDriverClass, String jdbcUrl) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.c3p0UsingDriver$str(), (Object)jdbcDriverClass, (Object)jdbcUrl);
    }

    protected String c3p0UsingDriver$str() {
        return "HHH010002: C3P0 using driver: %s at URL: %s";
    }

    protected String jdbcDriverNotFound$str() {
        return "HHH010003: JDBC Driver class not found: %s";
    }

    @Override
    public final String jdbcDriverNotFound(String jdbcDriverClass) {
        return String.format(this.getLoggingLocale(), this.jdbcDriverNotFound$str(), jdbcDriverClass);
    }

    @Override
    public final void unableToDestroyC3p0ConnectionPool(SQLException e) {
        this.log.logf(FQCN, Logger.Level.WARN, (Throwable)e, this.unableToDestroyC3p0ConnectionPool$str(), new Object[0]);
    }

    protected String unableToDestroyC3p0ConnectionPool$str() {
        return "HHH010004: Could not destroy C3P0 connection pool";
    }

    protected String unableToInstantiateC3p0ConnectionPool$str() {
        return "HHH010005: Could not instantiate C3P0 connection pool";
    }

    @Override
    public final String unableToInstantiateC3p0ConnectionPool() {
        return String.format(this.getLoggingLocale(), this.unableToInstantiateC3p0ConnectionPool$str(), new Object[0]);
    }

    public final void connectionProperties(Properties arg0) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.connectionProperties$str(), (Object)arg0);
    }

    protected String connectionProperties$str() {
        return "HHH10001001: Connection properties: %s";
    }

    public final void usingHibernateBuiltInConnectionPool() {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.usingHibernateBuiltInConnectionPool$str(), new Object[0]);
    }

    protected String usingHibernateBuiltInConnectionPool$str() {
        return "HHH10001002: Using Hibernate built-in connection pool (not for production use!)";
    }

    public final void autoCommitMode(boolean arg0) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.autoCommitMode$str(), (Object)arg0);
    }

    protected String autoCommitMode$str() {
        return "HHH10001003: Autocommit mode: %s";
    }

    protected String jdbcUrlNotSpecified$str() {
        return "HHH10001004: JDBC URL was not specified by property %s";
    }

    public final String jdbcUrlNotSpecified(String arg0) {
        return String.format(this.getLoggingLocale(), this.jdbcUrlNotSpecified$str(), arg0);
    }

    public final void usingDriver(String arg0, String arg1) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.usingDriver$str(), (Object)arg0, (Object)arg1);
    }

    protected String usingDriver$str() {
        return "HHH10001005: using driver [%s] at URL [%s]";
    }

    public final void jdbcDriverNotSpecified(String arg0) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.jdbcDriverNotSpecified$str(), (Object)arg0);
    }

    protected String jdbcDriverNotSpecified$str() {
        return "HHH10001006: No JDBC Driver class was specified by property %s";
    }

    public final void jdbcIsolationLevel(String arg0) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.jdbcIsolationLevel$str(), (Object)arg0);
    }

    protected String jdbcIsolationLevel$str() {
        return "HHH10001007: JDBC isolation level: %s";
    }

    public final void cleaningUpConnectionPool(String arg0) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.cleaningUpConnectionPool$str(), (Object)arg0);
    }

    protected String cleaningUpConnectionPool$str() {
        return "HHH10001008: Cleaning up connection pool [%s]";
    }

    public final void unableToClosePooledConnection(SQLException arg0) {
        this.log.logf(FQCN, Logger.Level.WARN, (Throwable)arg0, this.unableToClosePooledConnection$str(), new Object[0]);
    }

    protected String unableToClosePooledConnection$str() {
        return "HHH10001009: Problem closing pooled connection";
    }
}

