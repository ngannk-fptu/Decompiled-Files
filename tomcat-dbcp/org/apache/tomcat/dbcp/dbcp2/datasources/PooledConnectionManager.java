/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.dbcp.dbcp2.datasources;

import java.sql.SQLException;
import javax.sql.PooledConnection;

interface PooledConnectionManager {
    public void closePool(String var1) throws SQLException;

    public void invalidate(PooledConnection var1) throws SQLException;

    public void setPassword(String var1);
}

