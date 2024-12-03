/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.hbm2ddl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.registry.internal.StandardServiceRegistryImpl;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.tool.hbm2ddl.ConnectionHelper;

@Deprecated
class ManagedProviderConnectionHelper
implements ConnectionHelper {
    private Properties cfgProperties;
    private StandardServiceRegistryImpl serviceRegistry;
    private Connection connection;

    public ManagedProviderConnectionHelper(Properties cfgProperties) {
        this.cfgProperties = cfgProperties;
    }

    @Override
    public void prepare(boolean needsAutoCommit) throws SQLException {
        this.serviceRegistry = ManagedProviderConnectionHelper.createServiceRegistry(this.cfgProperties);
        this.connection = this.serviceRegistry.getService(ConnectionProvider.class).getConnection();
        if (needsAutoCommit && !this.connection.getAutoCommit()) {
            this.connection.commit();
            this.connection.setAutoCommit(true);
        }
    }

    private static StandardServiceRegistryImpl createServiceRegistry(Properties properties) {
        ConfigurationHelper.resolvePlaceHolders(properties);
        return (StandardServiceRegistryImpl)new StandardServiceRegistryBuilder().applySettings(properties).build();
    }

    @Override
    public Connection getConnection() throws SQLException {
        return this.connection;
    }

    @Override
    public void release() throws SQLException {
        try {
            this.releaseConnection();
        }
        finally {
            this.releaseServiceRegistry();
        }
    }

    private void releaseConnection() throws SQLException {
        if (this.connection != null) {
            try {
                this.serviceRegistry.getService(JdbcEnvironment.class).getSqlExceptionHelper().logAndClearWarnings(this.connection);
            }
            finally {
                try {
                    this.serviceRegistry.getService(ConnectionProvider.class).closeConnection(this.connection);
                }
                finally {
                    this.connection = null;
                }
            }
        }
    }

    private void releaseServiceRegistry() {
        if (this.serviceRegistry != null) {
            try {
                this.serviceRegistry.destroy();
            }
            finally {
                this.serviceRegistry = null;
            }
        }
    }
}

