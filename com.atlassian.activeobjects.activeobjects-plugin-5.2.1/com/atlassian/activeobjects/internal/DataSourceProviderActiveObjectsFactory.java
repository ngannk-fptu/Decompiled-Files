/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.spi.DataSourceProvider
 *  com.atlassian.activeobjects.spi.DatabaseType
 *  com.atlassian.activeobjects.spi.TransactionSynchronisationManager
 *  com.atlassian.beehive.ClusterLockService
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Supplier
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.activeobjects.internal;

import com.atlassian.activeobjects.ActiveObjectsPluginException;
import com.atlassian.activeobjects.config.ActiveObjectsConfiguration;
import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.external.ActiveObjectsUpgradeTask;
import com.atlassian.activeobjects.external.ModelVersion;
import com.atlassian.activeobjects.internal.AbstractActiveObjectsFactory;
import com.atlassian.activeobjects.internal.ActiveObjectUpgradeManager;
import com.atlassian.activeobjects.internal.ConnectionUnwrapper;
import com.atlassian.activeobjects.internal.DataSourceType;
import com.atlassian.activeobjects.internal.EntityManagedActiveObjects;
import com.atlassian.activeobjects.internal.EntityManagerFactory;
import com.atlassian.activeobjects.internal.SalTransactionManager;
import com.atlassian.activeobjects.spi.DataSourceProvider;
import com.atlassian.activeobjects.spi.DatabaseType;
import com.atlassian.activeobjects.spi.TransactionSynchronisationManager;
import com.atlassian.beehive.ClusterLockService;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.List;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import net.java.ao.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DataSourceProviderActiveObjectsFactory
extends AbstractActiveObjectsFactory {
    private final EntityManagerFactory entityManagerFactory;
    private final DataSourceProvider dataSourceProvider;
    private TransactionSynchronisationManager transactionSynchronizationManager;

    public DataSourceProviderActiveObjectsFactory(ActiveObjectUpgradeManager aoUpgradeManager, EntityManagerFactory entityManagerFactory, DataSourceProvider dataSourceProvider, TransactionTemplate transactionTemplate, ClusterLockService clusterLockService) {
        super(DataSourceType.APPLICATION, aoUpgradeManager, transactionTemplate, clusterLockService);
        this.entityManagerFactory = (EntityManagerFactory)Preconditions.checkNotNull((Object)entityManagerFactory);
        this.dataSourceProvider = (DataSourceProvider)Preconditions.checkNotNull((Object)dataSourceProvider);
    }

    public void setTransactionSynchronizationManager(TransactionSynchronisationManager transactionSynchronizationManager) {
        this.transactionSynchronizationManager = transactionSynchronizationManager;
    }

    @Override
    protected ActiveObjects doCreate(ActiveObjectsConfiguration configuration) {
        return (ActiveObjects)this.transactionTemplate.execute(() -> {
            DataSource dataSource = this.getDataSource();
            DatabaseType dbType = this.getDatabaseType();
            EntityManager entityManager = this.entityManagerFactory.getEntityManager(dataSource, dbType, this.dataSourceProvider.getSchema(), configuration);
            return new EntityManagedActiveObjects(entityManager, new SalTransactionManager(this.transactionTemplate, entityManager, this.transactionSynchronizationManager), dbType);
        });
    }

    @Override
    protected void upgrade(ActiveObjectsConfiguration configuration) {
        DatabaseType dbType = this.getDatabaseType();
        List<ActiveObjectsUpgradeTask> upgradeTasks = configuration.getUpgradeTasks();
        if (dbType == DatabaseType.POSTGRESQL || dbType == DatabaseType.UNKNOWN) {
            DataSource dataSource = this.getDataSource();
            upgradeTasks = upgradeTasks.stream().map(task -> new PostgresActiveObjectsUpgradeTask(dataSource, (ActiveObjectsUpgradeTask)task)).collect(Collectors.toList());
        }
        this.aoUpgradeManager.upgrade(configuration.getTableNamePrefix(), upgradeTasks, (Supplier<ActiveObjects>)((Supplier)() -> this.doCreate(configuration)));
    }

    private DataSource getDataSource() {
        DataSource dataSource = this.dataSourceProvider.getDataSource();
        if (dataSource == null) {
            throw new ActiveObjectsPluginException("No data source defined in the application");
        }
        return new ActiveObjectsDataSource(dataSource);
    }

    private DatabaseType getDatabaseType() {
        DatabaseType databaseType = this.dataSourceProvider.getDatabaseType();
        if (databaseType == null) {
            throw new ActiveObjectsPluginException("No database type defined in the application");
        }
        return databaseType;
    }

    private static class PostgresActiveObjectsUpgradeTask
    implements ActiveObjectsUpgradeTask {
        private static final Logger logger = LoggerFactory.getLogger(PostgresActiveObjectsUpgradeTask.class);
        private final DataSource dataSource;
        private final ActiveObjectsUpgradeTask delegate;

        private PostgresActiveObjectsUpgradeTask(DataSource dataSource, ActiveObjectsUpgradeTask delegate) {
            this.dataSource = dataSource;
            this.delegate = delegate;
        }

        @Override
        public ModelVersion getModelVersion() {
            return this.delegate.getModelVersion();
        }

        @Override
        public void upgrade(ModelVersion currentVersion, ActiveObjects ao) {
            boolean upgraded = this.configureConnectionAndRun(currentVersion, ao);
            if (!upgraded) {
                this.delegate.upgrade(currentVersion, ao);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        private boolean configureConnectionAndRun(ModelVersion currentVersion, ActiveObjects ao) {
            try (Connection c = this.dataSource.getConnection();){
                Method setPrepareThreshold;
                int previousPrepareThreshold;
                Connection connection;
                try {
                    Class<?> pgConnection = Class.forName("org.postgresql.PGConnection");
                    connection = ConnectionUnwrapper.tryUnwrapConnection(c, pgConnection).orElse(c);
                }
                catch (ClassNotFoundException e) {
                    logger.debug("Failed to load PGConnection. Falling back to unwrapping java.sql.Connection");
                    connection = ConnectionUnwrapper.tryUnwrapConnection(c, Connection.class).orElse(c);
                }
                Class<?> actualClass = connection.getClass();
                try {
                    Method getPrepareThreshold = actualClass.getMethod("getPrepareThreshold", new Class[0]);
                    previousPrepareThreshold = (Integer)getPrepareThreshold.invoke((Object)connection, new Object[0]);
                }
                catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                    logger.warn("Failed to retrieve prepare threshold", (Throwable)e);
                    boolean bl = false;
                    if (c == null) return bl;
                    if (var4_5 == null) {
                        c.close();
                        return bl;
                    }
                    try {
                        c.close();
                        return bl;
                    }
                    catch (Throwable throwable) {
                        var4_5.addSuppressed(throwable);
                        return bl;
                    }
                }
                if (previousPrepareThreshold == 0) {
                    boolean e = false;
                    return e;
                }
                try {
                    setPrepareThreshold = actualClass.getMethod("setPrepareThreshold", Integer.TYPE);
                    setPrepareThreshold.invoke((Object)connection, 0);
                }
                catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                    logger.warn("Failed to disable server side prepared statements", (Throwable)e);
                    boolean bl = false;
                    if (c == null) return bl;
                    if (var4_5 == null) {
                        c.close();
                        return bl;
                    }
                    try {
                        c.close();
                        return bl;
                    }
                    catch (Throwable throwable) {
                        var4_5.addSuppressed(throwable);
                        return bl;
                    }
                }
                try {
                    this.delegate.upgrade(currentVersion, ao);
                }
                catch (Throwable throwable) {
                    try {
                        setPrepareThreshold.invoke((Object)connection, previousPrepareThreshold);
                        throw throwable;
                    }
                    catch (IllegalAccessException | InvocationTargetException e) {
                        logger.warn("Failed to reset server side prepared statements", (Throwable)e);
                    }
                    throw throwable;
                }
                try {
                    setPrepareThreshold.invoke((Object)connection, previousPrepareThreshold);
                }
                catch (IllegalAccessException | InvocationTargetException e) {
                    logger.warn("Failed to reset server side prepared statements", (Throwable)e);
                }
                boolean bl = true;
                return bl;
            }
            catch (SQLException e) {
                logger.warn("Failed to retrieve connection", (Throwable)e);
                return false;
            }
        }
    }

    public static class ActiveObjectsDataSource
    implements DataSource {
        private final DataSource dataSource;

        ActiveObjectsDataSource(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        @Override
        public Connection getConnection() throws SQLException {
            return this.dataSource.getConnection();
        }

        @Override
        public Connection getConnection(String username, String password) {
            throw new IllegalStateException("Not allowed to get a connection for non default username/password");
        }

        @Override
        public int getLoginTimeout() {
            return 0;
        }

        @Override
        public void setLoginTimeout(int timeout) {
            throw new UnsupportedOperationException("setLoginTimeout");
        }

        @Override
        public PrintWriter getLogWriter() {
            throw new UnsupportedOperationException("getLogWriter");
        }

        @Override
        public void setLogWriter(PrintWriter pw) {
            throw new UnsupportedOperationException("setLogWriter");
        }

        @Override
        public <T> T unwrap(Class<T> tClass) {
            throw new UnsupportedOperationException("unwrap");
        }

        @Override
        public boolean isWrapperFor(Class<?> aClass) {
            throw new UnsupportedOperationException("isWrapperFor");
        }

        @Override
        public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
            throw new SQLFeatureNotSupportedException();
        }
    }
}

