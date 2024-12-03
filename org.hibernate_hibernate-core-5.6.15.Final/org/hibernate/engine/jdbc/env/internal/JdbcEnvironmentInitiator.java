/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.engine.jdbc.env.internal;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Map;
import org.hibernate.HibernateException;
import org.hibernate.MultiTenancyStrategy;
import org.hibernate.boot.registry.StandardServiceInitiator;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.engine.jdbc.connections.spi.JdbcConnectionAccess;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.hibernate.engine.jdbc.dialect.spi.DatabaseMetaDataDialectResolutionInfoAdapter;
import org.hibernate.engine.jdbc.dialect.spi.DialectFactory;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolutionInfo;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolutionInfoSource;
import org.hibernate.engine.jdbc.env.internal.JdbcEnvironmentImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.service.spi.ServiceRegistryImplementor;
import org.jboss.logging.Logger;

public class JdbcEnvironmentInitiator
implements StandardServiceInitiator<JdbcEnvironment> {
    private static final CoreMessageLogger log = (CoreMessageLogger)Logger.getMessageLogger(CoreMessageLogger.class, (String)JdbcEnvironmentInitiator.class.getName());
    public static final JdbcEnvironmentInitiator INSTANCE = new JdbcEnvironmentInitiator();

    @Override
    public Class<JdbcEnvironment> getServiceInitiated() {
        return JdbcEnvironment.class;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public JdbcEnvironment initiateService(Map configurationValues, ServiceRegistryImplementor registry) {
        DialectFactory dialectFactory = registry.getService(DialectFactory.class);
        boolean useJdbcMetadata = ConfigurationHelper.getBoolean("hibernate.temp.use_jdbc_metadata_defaults", configurationValues, true);
        if (!useJdbcMetadata) return new JdbcEnvironmentImpl(registry, dialectFactory.buildDialect(configurationValues, null));
        JdbcConnectionAccess jdbcConnectionAccess = this.buildJdbcConnectionAccess(configurationValues, registry);
        try {
            final Connection connection = jdbcConnectionAccess.obtainConnection();
            try {
                DatabaseMetaData dbmd = connection.getMetaData();
                if (log.isDebugEnabled()) {
                    log.debugf("Database ->\n       name : %s\n    version : %s\n      major : %s\n      minor : %s", new Object[]{dbmd.getDatabaseProductName(), dbmd.getDatabaseProductVersion(), dbmd.getDatabaseMajorVersion(), dbmd.getDatabaseMinorVersion()});
                    log.debugf("Driver ->\n       name : %s\n    version : %s\n      major : %s\n      minor : %s", new Object[]{dbmd.getDriverName(), dbmd.getDriverVersion(), dbmd.getDriverMajorVersion(), dbmd.getDriverMinorVersion()});
                    log.debugf("JDBC version : %s.%s", dbmd.getJDBCMajorVersion(), dbmd.getJDBCMinorVersion());
                }
                Dialect dialect = dialectFactory.buildDialect(configurationValues, new DialectResolutionInfoSource(){

                    @Override
                    public DialectResolutionInfo getDialectResolutionInfo() {
                        try {
                            return new DatabaseMetaDataDialectResolutionInfoAdapter(connection.getMetaData());
                        }
                        catch (SQLException sqlException) {
                            throw new HibernateException("Unable to access java.sql.DatabaseMetaData to determine appropriate Dialect to use", sqlException);
                        }
                    }
                });
                JdbcEnvironmentImpl jdbcEnvironmentImpl = new JdbcEnvironmentImpl(registry, dialect, dbmd, jdbcConnectionAccess);
                return jdbcEnvironmentImpl;
            }
            catch (SQLException e) {
                log.unableToObtainConnectionMetadata(e);
                return new JdbcEnvironmentImpl(registry, dialectFactory.buildDialect(configurationValues, null));
            }
            finally {
                try {
                    jdbcConnectionAccess.releaseConnection(connection);
                }
                catch (SQLException sQLException) {}
            }
        }
        catch (Exception e2) {
            log.unableToObtainConnectionToQueryMetadata(e2);
        }
        return new JdbcEnvironmentImpl(registry, dialectFactory.buildDialect(configurationValues, null));
    }

    private JdbcConnectionAccess buildJdbcConnectionAccess(Map configValues, ServiceRegistryImplementor registry) {
        MultiTenancyStrategy multiTenancyStrategy = MultiTenancyStrategy.determineMultiTenancyStrategy(configValues);
        if (!multiTenancyStrategy.requiresMultiTenantConnectionProvider()) {
            ConnectionProvider connectionProvider = registry.getService(ConnectionProvider.class);
            return new ConnectionProviderJdbcConnectionAccess(connectionProvider);
        }
        MultiTenantConnectionProvider multiTenantConnectionProvider = registry.getService(MultiTenantConnectionProvider.class);
        return new MultiTenantConnectionProviderJdbcConnectionAccess(multiTenantConnectionProvider);
    }

    public static JdbcConnectionAccess buildBootstrapJdbcConnectionAccess(MultiTenancyStrategy multiTenancyStrategy, ServiceRegistryImplementor registry) {
        if (!multiTenancyStrategy.requiresMultiTenantConnectionProvider()) {
            ConnectionProvider connectionProvider = registry.getService(ConnectionProvider.class);
            return new ConnectionProviderJdbcConnectionAccess(connectionProvider);
        }
        MultiTenantConnectionProvider multiTenantConnectionProvider = registry.getService(MultiTenantConnectionProvider.class);
        return new MultiTenantConnectionProviderJdbcConnectionAccess(multiTenantConnectionProvider);
    }

    public static class MultiTenantConnectionProviderJdbcConnectionAccess
    implements JdbcConnectionAccess {
        private final MultiTenantConnectionProvider connectionProvider;

        public MultiTenantConnectionProviderJdbcConnectionAccess(MultiTenantConnectionProvider connectionProvider) {
            this.connectionProvider = connectionProvider;
        }

        public MultiTenantConnectionProvider getConnectionProvider() {
            return this.connectionProvider;
        }

        @Override
        public Connection obtainConnection() throws SQLException {
            return this.connectionProvider.getAnyConnection();
        }

        @Override
        public void releaseConnection(Connection connection) throws SQLException {
            this.connectionProvider.releaseAnyConnection(connection);
        }

        @Override
        public boolean supportsAggressiveRelease() {
            return this.connectionProvider.supportsAggressiveRelease();
        }
    }

    public static class ConnectionProviderJdbcConnectionAccess
    implements JdbcConnectionAccess {
        private final ConnectionProvider connectionProvider;

        public ConnectionProviderJdbcConnectionAccess(ConnectionProvider connectionProvider) {
            this.connectionProvider = connectionProvider;
        }

        public ConnectionProvider getConnectionProvider() {
            return this.connectionProvider;
        }

        @Override
        public Connection obtainConnection() throws SQLException {
            return this.connectionProvider.getConnection();
        }

        @Override
        public void releaseConnection(Connection connection) throws SQLException {
            this.connectionProvider.closeConnection(connection);
        }

        @Override
        public boolean supportsAggressiveRelease() {
            return this.connectionProvider.supportsAggressiveRelease();
        }
    }
}

