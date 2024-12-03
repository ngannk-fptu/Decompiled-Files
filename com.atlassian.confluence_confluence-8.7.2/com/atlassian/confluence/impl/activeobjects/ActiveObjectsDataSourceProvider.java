/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.spi.ConnectionHandler
 *  com.atlassian.activeobjects.spi.DatabaseType
 *  com.atlassian.activeobjects.spi.TenantAwareDataSourceProvider
 *  com.atlassian.config.db.HibernateConfig
 *  com.atlassian.tenancy.api.Tenant
 *  io.atlassian.util.concurrent.Lazy
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.hibernate.engine.spi.SessionFactoryImplementor
 *  org.hibernate.engine.spi.SessionImplementor
 *  org.springframework.jdbc.datasource.AbstractDataSource
 */
package com.atlassian.confluence.impl.activeobjects;

import com.atlassian.activeobjects.spi.ConnectionHandler;
import com.atlassian.activeobjects.spi.DatabaseType;
import com.atlassian.activeobjects.spi.TenantAwareDataSourceProvider;
import com.atlassian.config.db.HibernateConfig;
import com.atlassian.confluence.status.service.SystemInformationService;
import com.atlassian.tenancy.api.Tenant;
import io.atlassian.util.concurrent.Lazy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.sql.DataSource;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.springframework.jdbc.datasource.AbstractDataSource;

public final class ActiveObjectsDataSourceProvider
implements TenantAwareDataSourceProvider {
    private final DataSourceWrapper dataSource = new DataSourceWrapper(() -> ActiveObjectsDataSourceProvider.currentSessionConnection(sessionFactory));
    private final Supplier<String> dialectRef = Lazy.supplier(() -> ActiveObjectsDataSourceProvider.getDialect(systemInformationService));

    public ActiveObjectsDataSourceProvider(SystemInformationService systemInformationService, SessionFactoryImplementor sessionFactory) {
    }

    @Nonnull
    public DataSource getDataSource(@Nonnull Tenant tenant) {
        return this.dataSource;
    }

    @Nonnull
    public DatabaseType getDatabaseType(@Nonnull Tenant tenant) {
        String dialect = this.dialectRef.get();
        if (dialect == null) {
            return DatabaseType.UNKNOWN;
        }
        if (HibernateConfig.isHsqlDialect((String)dialect)) {
            return DatabaseType.HSQL;
        }
        if (HibernateConfig.isH2Dialect((String)dialect)) {
            return DatabaseType.H2;
        }
        if (HibernateConfig.isMySqlDialect((String)dialect)) {
            return DatabaseType.MYSQL;
        }
        if (HibernateConfig.isPostgreSqlDialect((String)dialect)) {
            return DatabaseType.POSTGRESQL;
        }
        if (HibernateConfig.isOracleDialect((String)dialect)) {
            return DatabaseType.ORACLE;
        }
        if (HibernateConfig.isSqlServerDialect((String)dialect)) {
            return DatabaseType.MS_SQL;
        }
        return DatabaseType.UNKNOWN;
    }

    @Nullable
    public String getSchema(Tenant tenant) {
        return null;
    }

    private static Connection currentSessionConnection(SessionFactoryImplementor sessionFactory) {
        return ((SessionImplementor)sessionFactory.getCurrentSession()).connection();
    }

    private static String getDialect(SystemInformationService systemInformationService) {
        return systemInformationService.getSafeDatabaseInfo().getDialect();
    }

    private static Connection wrap(Connection connection) {
        return ConnectionHandler.newInstance((Connection)connection);
    }

    private static class DataSourceWrapper
    extends AbstractDataSource {
        private final Supplier<Connection> connectionSupplier;

        DataSourceWrapper(Supplier<Connection> connectionSupplier) {
            this.connectionSupplier = Objects.requireNonNull(connectionSupplier);
        }

        public Connection getConnection() throws SQLException {
            return ActiveObjectsDataSourceProvider.wrap(this.connectionSupplier.get());
        }

        public Connection getConnection(String username, String password) {
            throw new UnsupportedOperationException();
        }
    }
}

