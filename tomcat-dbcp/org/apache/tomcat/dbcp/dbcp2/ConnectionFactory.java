/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.dbcp.dbcp2;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionFactory {
    public Connection createConnection() throws SQLException;
}

