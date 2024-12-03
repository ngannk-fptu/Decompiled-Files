/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc.connections.spi;

import java.sql.Connection;
import java.sql.SQLException;
import org.hibernate.service.Service;
import org.hibernate.service.spi.Wrapped;

public interface MultiTenantConnectionProvider
extends Service,
Wrapped {
    public Connection getAnyConnection() throws SQLException;

    public void releaseAnyConnection(Connection var1) throws SQLException;

    public Connection getConnection(String var1) throws SQLException;

    public void releaseConnection(String var1, Connection var2) throws SQLException;

    public boolean supportsAggressiveRelease();
}

