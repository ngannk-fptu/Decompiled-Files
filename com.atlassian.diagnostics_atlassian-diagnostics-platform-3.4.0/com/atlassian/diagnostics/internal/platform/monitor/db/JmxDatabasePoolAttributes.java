/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.diagnostics.internal.platform.monitor.db;

import javax.annotation.Nonnull;

public enum JmxDatabasePoolAttributes {
    APACHE_COMMONS("org.apache.commons.pool2.impl.GenericObjectPool", "NumIdle", "NumActive", "MaxTotal", "RemoveAbandonedTimeout"),
    TOMCAT("org.apache.tomcat.dbcp.pool2.impl.GenericObjectPool", "NumIdle", "NumActive", "MaxTotal", "RemoveAbandonedTimeout"),
    VIBUR("org.vibur.dbcp.ViburDBCPMonitoring", "PoolRemainingCreated", "PoolTaken", "PoolMaxSize", ""),
    HIKARI("com.zaxxer.hikari.pool.HikariPool", "IdleConnections", "ActiveConnections", "TotalConnections", ""),
    C3P0("com.mchange.v2.c3p0.management.DynamicPooledDataSourceManagerMBean", "numIdleConnections", "numBusyConnections", "numConnections", ""),
    UNKNOWN("", "", "", "", "");

    public final String instanceOfQuery;
    public final String idleConnectionsAttributeName;
    public final String activeConnectionsAttribute;
    public final String maxConnectionsAttribute;
    public final String abandonedTimeoutAttributeName;

    private JmxDatabasePoolAttributes(@Nonnull String instanceOfQuery, @Nonnull String idleConnectionsAttributeName, @Nonnull String activeConnectionsAttributeName, String maxConnectionsAttributeName, String abandonedTimeoutAttributeName) {
        this.instanceOfQuery = instanceOfQuery;
        this.idleConnectionsAttributeName = idleConnectionsAttributeName;
        this.activeConnectionsAttribute = activeConnectionsAttributeName;
        this.maxConnectionsAttribute = maxConnectionsAttributeName;
        this.abandonedTimeoutAttributeName = abandonedTimeoutAttributeName;
    }
}

