/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.store.jpa.interfaces;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionHelper {
    public Connection getConnection();

    public void closeConnection(Connection var1) throws SQLException;
}

