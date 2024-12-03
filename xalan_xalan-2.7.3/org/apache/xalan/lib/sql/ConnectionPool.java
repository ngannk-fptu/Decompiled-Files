/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.lib.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public interface ConnectionPool {
    public boolean isEnabled();

    public void setDriver(String var1);

    public void setURL(String var1);

    public void freeUnused();

    public boolean hasActiveConnections();

    public void setPassword(String var1);

    public void setUser(String var1);

    public void setMinConnections(int var1);

    public boolean testConnection();

    public Connection getConnection() throws SQLException;

    public void releaseConnection(Connection var1) throws SQLException;

    public void releaseConnectionOnError(Connection var1) throws SQLException;

    public void setPoolEnabled(boolean var1);

    public void setProtocol(Properties var1);
}

