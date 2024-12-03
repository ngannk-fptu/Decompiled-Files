/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc.connections.spi;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;

public interface JdbcConnectionAccess
extends Serializable {
    public Connection obtainConnection() throws SQLException;

    public void releaseConnection(Connection var1) throws SQLException;

    public boolean supportsAggressiveRelease();
}

