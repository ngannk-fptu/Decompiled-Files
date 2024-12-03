/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc.connections.internal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import org.hibernate.engine.jdbc.connections.internal.BasicConnectionCreator;
import org.hibernate.service.spi.ServiceRegistryImplementor;

public class DriverManagerConnectionCreator
extends BasicConnectionCreator {
    public DriverManagerConnectionCreator(ServiceRegistryImplementor serviceRegistry, String url, Properties connectionProps, Boolean autocommit, Integer isolation, String initSql) {
        super(serviceRegistry, url, connectionProps, autocommit, isolation, initSql);
    }

    @Override
    protected Connection makeConnection(String url, Properties connectionProps) {
        try {
            return DriverManager.getConnection(url, connectionProps);
        }
        catch (SQLException e) {
            throw this.convertSqlException("Error calling DriverManager#getConnection", e);
        }
    }
}

