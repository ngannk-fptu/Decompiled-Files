/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc.internal;

import java.util.Map;
import org.hibernate.MultiTenancyStrategy;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.LobCreationContext;
import org.hibernate.engine.jdbc.LobCreator;
import org.hibernate.engine.jdbc.connections.spi.JdbcConnectionAccess;
import org.hibernate.engine.jdbc.env.internal.JdbcEnvironmentInitiator;
import org.hibernate.engine.jdbc.env.spi.ExtractedDatabaseMetaData;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.hibernate.engine.jdbc.internal.ResultSetWrapperImpl;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.jdbc.spi.ResultSetWrapper;
import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
import org.hibernate.engine.jdbc.spi.SqlStatementLogger;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.service.spi.Configurable;
import org.hibernate.service.spi.ServiceRegistryAwareService;
import org.hibernate.service.spi.ServiceRegistryImplementor;

public class JdbcServicesImpl
implements JdbcServices,
ServiceRegistryAwareService,
Configurable {
    private ServiceRegistryImplementor serviceRegistry;
    private JdbcEnvironment jdbcEnvironment;
    private MultiTenancyStrategy multiTenancyStrategy;
    private SqlStatementLogger sqlStatementLogger;
    private ResultSetWrapperImpl resultSetWrapper;

    @Override
    public void injectServices(ServiceRegistryImplementor serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public void configure(Map configValues) {
        this.jdbcEnvironment = this.serviceRegistry.getService(JdbcEnvironment.class);
        assert (this.jdbcEnvironment != null) : "JdbcEnvironment was not found!";
        this.multiTenancyStrategy = MultiTenancyStrategy.determineMultiTenancyStrategy(configValues);
        boolean showSQL = ConfigurationHelper.getBoolean("hibernate.show_sql", configValues, false);
        boolean formatSQL = ConfigurationHelper.getBoolean("hibernate.format_sql", configValues, false);
        boolean highlightSQL = ConfigurationHelper.getBoolean("hibernate.highlight_sql", configValues, false);
        long logSlowQuery = ConfigurationHelper.getLong("hibernate.session.events.log.LOG_QUERIES_SLOWER_THAN_MS", configValues, 0);
        this.sqlStatementLogger = new SqlStatementLogger(showSQL, formatSQL, highlightSQL, logSlowQuery);
        this.resultSetWrapper = new ResultSetWrapperImpl(this.serviceRegistry);
    }

    @Override
    public JdbcEnvironment getJdbcEnvironment() {
        return this.jdbcEnvironment;
    }

    @Override
    public JdbcConnectionAccess getBootstrapJdbcConnectionAccess() {
        return JdbcEnvironmentInitiator.buildBootstrapJdbcConnectionAccess(this.multiTenancyStrategy, this.serviceRegistry);
    }

    @Override
    public Dialect getDialect() {
        if (this.jdbcEnvironment != null) {
            return this.jdbcEnvironment.getDialect();
        }
        return null;
    }

    @Override
    public SqlStatementLogger getSqlStatementLogger() {
        return this.sqlStatementLogger;
    }

    @Override
    public SqlExceptionHelper getSqlExceptionHelper() {
        if (this.jdbcEnvironment != null) {
            return this.jdbcEnvironment.getSqlExceptionHelper();
        }
        return null;
    }

    @Override
    public ExtractedDatabaseMetaData getExtractedMetaDataSupport() {
        if (this.jdbcEnvironment != null) {
            return this.jdbcEnvironment.getExtractedDatabaseMetaData();
        }
        return null;
    }

    @Override
    public LobCreator getLobCreator(LobCreationContext lobCreationContext) {
        if (this.jdbcEnvironment != null) {
            return this.jdbcEnvironment.getLobCreatorBuilder().buildLobCreator(lobCreationContext);
        }
        return null;
    }

    @Override
    public ResultSetWrapper getResultSetWrapper() {
        return this.resultSetWrapper;
    }
}

