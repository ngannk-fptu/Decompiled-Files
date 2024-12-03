/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc.connections.spi;

import java.sql.Connection;
import java.sql.SQLException;
import org.hibernate.service.Service;
import org.hibernate.service.spi.Wrapped;

public interface ConnectionProvider
extends Service,
Wrapped {
    public Connection getConnection() throws SQLException;

    public void closeConnection(Connection var1) throws SQLException;

    public boolean supportsAggressiveRelease();
}

