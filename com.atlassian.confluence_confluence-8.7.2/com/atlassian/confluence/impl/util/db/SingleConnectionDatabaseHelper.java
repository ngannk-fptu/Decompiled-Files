/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.db.DelegatingConnection
 *  org.hibernate.cfg.Environment
 *  org.hibernate.engine.jdbc.connections.internal.ConnectionProviderInitiator
 *  org.hibernate.engine.jdbc.connections.internal.DatasourceConnectionProviderImpl
 *  org.hibernate.engine.jndi.internal.JndiServiceImpl
 *  org.hibernate.engine.jndi.spi.JndiService
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.util.db;

import com.atlassian.config.db.DelegatingConnection;
import com.atlassian.confluence.impl.util.db.SingleConnectionProvider;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import org.hibernate.cfg.Environment;
import org.hibernate.engine.jdbc.connections.internal.ConnectionProviderInitiator;
import org.hibernate.engine.jdbc.connections.internal.DatasourceConnectionProviderImpl;
import org.hibernate.engine.jndi.internal.JndiServiceImpl;
import org.hibernate.engine.jndi.spi.JndiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class SingleConnectionDatabaseHelper
implements SingleConnectionProvider {
    private static final Logger log = LoggerFactory.getLogger(SingleConnectionDatabaseHelper.class);

    SingleConnectionDatabaseHelper() {
    }

    @Override
    public Connection getConnection(Properties databaseProperties) throws SQLException {
        if (databaseProperties.getProperty("hibernate.connection.datasource") != null) {
            return this.createConnectionFromDataSource(databaseProperties);
        }
        Properties connectionProperties = ConnectionProviderInitiator.getConnectionProperties((Map)databaseProperties);
        String jdbcUrl = databaseProperties.getProperty("hibernate.connection.url");
        try {
            Class.forName(databaseProperties.getProperty("hibernate.connection.driver_class"));
        }
        catch (ClassNotFoundException e) {
            log.error("Failed to load JDBC driver class {}", (Object)databaseProperties.getProperty("hibernate.connection.driver_class"), (Object)e);
        }
        return DriverManager.getConnection(jdbcUrl, connectionProperties);
    }

    private Connection createConnectionFromDataSource(Properties databaseProperties) throws SQLException {
        Properties properties = new Properties();
        properties.putAll((Map<?, ?>)databaseProperties);
        properties.putAll((Map<?, ?>)Environment.getProperties());
        JndiServiceImpl jndiService = new JndiServiceImpl((Map)properties);
        final DatasourceConnectionProviderImpl connectionProvider = new DatasourceConnectionProviderImpl();
        connectionProvider.setJndiService((JndiService)jndiService);
        connectionProvider.configure((Map)properties);
        final Connection underlying = connectionProvider.getConnection();
        return new DelegatingConnection(underlying){

            public void close() throws SQLException {
                try {
                    super.close();
                }
                finally {
                    try {
                        connectionProvider.closeConnection(underlying);
                    }
                    catch (SQLException e) {
                        log.warn("Problem while closing hibernate connection provider", (Throwable)e);
                    }
                }
            }
        };
    }
}

