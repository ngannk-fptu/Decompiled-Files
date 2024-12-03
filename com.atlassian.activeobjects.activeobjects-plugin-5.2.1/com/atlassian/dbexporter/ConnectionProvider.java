/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.dbexporter;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionProvider {
    public Connection getConnection() throws SQLException;
}

