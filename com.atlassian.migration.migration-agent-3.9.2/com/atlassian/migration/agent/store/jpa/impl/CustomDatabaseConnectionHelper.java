/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.store.jpa.impl;

import com.atlassian.migration.agent.store.jpa.interfaces.ConnectionHelper;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;

public class CustomDatabaseConnectionHelper
implements ConnectionHelper {
    private final Object wrappedProvider;

    public CustomDatabaseConnectionHelper(Object databaseConnectionProvider) {
        this.wrappedProvider = databaseConnectionProvider;
    }

    @Override
    public Connection getConnection() {
        try {
            Method getConnectionMethod = Class.forName("com.atlassian.confluence.persistence.DatabaseConnectionProvider").getMethod("getConnection", new Class[0]);
            return (Connection)getConnectionMethod.invoke(this.wrappedProvider, new Object[0]);
        }
        catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void closeConnection(Connection conn) throws SQLException {
        conn.close();
    }
}

