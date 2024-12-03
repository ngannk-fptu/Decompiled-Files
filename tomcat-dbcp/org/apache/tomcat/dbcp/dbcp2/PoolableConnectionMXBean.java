/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.dbcp.dbcp2;

import java.sql.SQLException;

public interface PoolableConnectionMXBean {
    public void clearCachedState();

    public void clearWarnings() throws SQLException;

    public void close() throws SQLException;

    public boolean getAutoCommit() throws SQLException;

    public boolean getCacheState();

    public String getCatalog() throws SQLException;

    public int getHoldability() throws SQLException;

    public String getSchema() throws SQLException;

    public String getToString();

    public int getTransactionIsolation() throws SQLException;

    public boolean isClosed() throws SQLException;

    public boolean isReadOnly() throws SQLException;

    public void reallyClose() throws SQLException;

    public void setAutoCommit(boolean var1) throws SQLException;

    public void setCacheState(boolean var1);

    public void setCatalog(String var1) throws SQLException;

    public void setHoldability(int var1) throws SQLException;

    public void setReadOnly(boolean var1) throws SQLException;

    public void setSchema(String var1) throws SQLException;

    public void setTransactionIsolation(int var1) throws SQLException;
}

