/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.dbcp.dbcp2;

import java.sql.SQLException;

public interface DataSourceMXBean {
    public boolean getAbandonedUsageTracking();

    public boolean getCacheState();

    public String[] getConnectionInitSqlsAsArray();

    public Boolean getDefaultAutoCommit();

    public String getDefaultCatalog();

    public Boolean getDefaultReadOnly();

    default public String getDefaultSchema() {
        return null;
    }

    public int getDefaultTransactionIsolation();

    public String[] getDisconnectionSqlCodesAsArray();

    public String getDriverClassName();

    public boolean getFastFailValidation();

    public int getInitialSize();

    public boolean getLifo();

    public boolean getLogAbandoned();

    public boolean getLogExpiredConnections();

    public long getMaxConnLifetimeMillis();

    public int getMaxIdle();

    public int getMaxOpenPreparedStatements();

    public int getMaxTotal();

    public long getMaxWaitMillis();

    public long getMinEvictableIdleTimeMillis();

    public int getMinIdle();

    public int getNumActive();

    public int getNumIdle();

    public int getNumTestsPerEvictionRun();

    public boolean getRemoveAbandonedOnBorrow();

    public boolean getRemoveAbandonedOnMaintenance();

    public int getRemoveAbandonedTimeout();

    public long getSoftMinEvictableIdleTimeMillis();

    public boolean getTestOnBorrow();

    public boolean getTestOnCreate();

    public boolean getTestWhileIdle();

    public long getTimeBetweenEvictionRunsMillis();

    public String getUrl();

    public String getUsername();

    public String getValidationQuery();

    public int getValidationQueryTimeout();

    public boolean isAccessToUnderlyingConnectionAllowed();

    default public boolean isClearStatementPoolOnReturn() {
        return false;
    }

    public boolean isClosed();

    public boolean isPoolPreparedStatements();

    default public void restart() throws SQLException {
    }

    default public void start() throws SQLException {
    }
}

