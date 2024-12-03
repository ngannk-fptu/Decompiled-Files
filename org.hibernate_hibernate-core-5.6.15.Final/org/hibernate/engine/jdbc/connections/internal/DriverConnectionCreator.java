/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc.connections.internal;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;
import org.hibernate.engine.jdbc.connections.internal.BasicConnectionCreator;
import org.hibernate.service.spi.ServiceRegistryImplementor;

public class DriverConnectionCreator
extends BasicConnectionCreator {
    private final Driver driver;

    public DriverConnectionCreator(Driver driver, ServiceRegistryImplementor serviceRegistry, String url, Properties connectionProps, Boolean autocommit, Integer isolation, String initSql) {
        super(serviceRegistry, url, connectionProps, autocommit, isolation, initSql);
        this.driver = driver;
    }

    @Override
    protected Connection makeConnection(String url, Properties connectionProps) {
        try {
            return this.driver.connect(url, connectionProps);
        }
        catch (SQLException e) {
            throw this.convertSqlException("Error calling Driver#connect", e);
        }
    }
}

