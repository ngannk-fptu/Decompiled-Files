/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.jdbc.pool;

import java.sql.SQLException;

public interface PooledConnectionMBean {
    public long getConnectionVersion();

    public boolean isInitialized();

    public boolean isMaxAgeExpired();

    public boolean isSuspect();

    public long getTimestamp();

    public boolean isDiscarded();

    public long getLastValidated();

    public long getLastConnected();

    public boolean isReleased();

    public void clearWarnings();

    public boolean isClosed() throws SQLException;

    public boolean getAutoCommit() throws SQLException;

    public String getCatalog() throws SQLException;

    public int getHoldability() throws SQLException;

    public boolean isReadOnly() throws SQLException;

    public String getSchema() throws SQLException;

    public int getTransactionIsolation() throws SQLException;
}

