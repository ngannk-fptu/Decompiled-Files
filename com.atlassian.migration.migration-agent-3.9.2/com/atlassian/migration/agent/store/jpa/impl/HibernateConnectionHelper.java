/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.store.jpa.impl;

import com.atlassian.migration.agent.store.jpa.interfaces.ConnectionHelper;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;

public class HibernateConnectionHelper
implements ConnectionHelper {
    private final Object sessionFactoryImplementor;
    private final Object wrappedProvider;

    public HibernateConnectionHelper(Object sessionFactoryImplementor) {
        this.sessionFactoryImplementor = sessionFactoryImplementor;
        this.wrappedProvider = this.getConnectionProvider();
    }

    public Object getConnectionProvider() {
        try {
            Method getConnectionProvider = Class.forName("net.sf.hibernate.engine.SessionFactoryImplementor").getMethod("getConnectionProvider", new Class[0]);
            return getConnectionProvider.invoke(this.sessionFactoryImplementor, new Object[0]);
        }
        catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Connection getConnection() {
        try {
            Method getConnectionMethod = Class.forName("net.sf.hibernate.connection.ConnectionProvider").getMethod("getConnection", new Class[0]);
            return (Connection)getConnectionMethod.invoke(this.wrappedProvider, new Object[0]);
        }
        catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void closeConnection(Connection conn) throws SQLException {
        try {
            Method closeConnectionMethod = Class.forName("net.sf.hibernate.connection.ConnectionProvider").getMethod("closeConnection", Connection.class);
            closeConnectionMethod.invoke(this.wrappedProvider, conn);
        }
        catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}

