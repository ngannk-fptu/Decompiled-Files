/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mchange.v2.c3p0.DataSources
 *  org.hibernate.HibernateException
 *  org.hibernate.boot.registry.classloading.spi.ClassLoaderService
 *  org.hibernate.boot.registry.classloading.spi.ClassLoadingException
 *  org.hibernate.engine.jdbc.connections.internal.ConnectionProviderInitiator
 *  org.hibernate.engine.jdbc.connections.spi.ConnectionProvider
 *  org.hibernate.internal.util.config.ConfigurationHelper
 *  org.hibernate.service.UnknownUnwrapTypeException
 *  org.hibernate.service.spi.Configurable
 *  org.hibernate.service.spi.ServiceRegistryAwareService
 *  org.hibernate.service.spi.ServiceRegistryImplementor
 *  org.hibernate.service.spi.Stoppable
 *  org.jboss.logging.Logger
 */
package org.hibernate.c3p0.internal;

import com.mchange.v2.c3p0.DataSources;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.sql.DataSource;
import org.hibernate.HibernateException;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.boot.registry.classloading.spi.ClassLoadingException;
import org.hibernate.c3p0.internal.C3P0MessageLogger;
import org.hibernate.engine.jdbc.connections.internal.ConnectionProviderInitiator;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.service.UnknownUnwrapTypeException;
import org.hibernate.service.spi.Configurable;
import org.hibernate.service.spi.ServiceRegistryAwareService;
import org.hibernate.service.spi.ServiceRegistryImplementor;
import org.hibernate.service.spi.Stoppable;
import org.jboss.logging.Logger;

public class C3P0ConnectionProvider
implements ConnectionProvider,
Configurable,
Stoppable,
ServiceRegistryAwareService {
    private static final C3P0MessageLogger LOG = (C3P0MessageLogger)Logger.getMessageLogger(C3P0MessageLogger.class, (String)C3P0ConnectionProvider.class.getName());
    private static final String C3P0_STYLE_MIN_POOL_SIZE = "c3p0.minPoolSize";
    private static final String C3P0_STYLE_MAX_POOL_SIZE = "c3p0.maxPoolSize";
    private static final String C3P0_STYLE_MAX_IDLE_TIME = "c3p0.maxIdleTime";
    private static final String C3P0_STYLE_MAX_STATEMENTS = "c3p0.maxStatements";
    private static final String C3P0_STYLE_ACQUIRE_INCREMENT = "c3p0.acquireIncrement";
    private static final String C3P0_STYLE_IDLE_CONNECTION_TEST_PERIOD = "c3p0.idleConnectionTestPeriod";
    private static final String C3P0_STYLE_INITIAL_POOL_SIZE = "c3p0.initialPoolSize";
    private DataSource ds;
    private Integer isolation;
    private boolean autocommit;
    private ServiceRegistryImplementor serviceRegistry;

    public Connection getConnection() throws SQLException {
        Connection c = this.ds.getConnection();
        if (this.isolation != null && !this.isolation.equals(c.getTransactionIsolation())) {
            c.setTransactionIsolation(this.isolation);
        }
        if (c.getAutoCommit() != this.autocommit) {
            c.setAutoCommit(this.autocommit);
        }
        return c;
    }

    public void closeConnection(Connection conn) throws SQLException {
        conn.close();
    }

    public boolean isUnwrappableAs(Class unwrapType) {
        return ConnectionProvider.class.equals((Object)unwrapType) || C3P0ConnectionProvider.class.isAssignableFrom(unwrapType) || DataSource.class.isAssignableFrom(unwrapType);
    }

    public <T> T unwrap(Class<T> unwrapType) {
        if (ConnectionProvider.class.equals(unwrapType) || C3P0ConnectionProvider.class.isAssignableFrom(unwrapType)) {
            return (T)this;
        }
        if (DataSource.class.isAssignableFrom(unwrapType)) {
            return (T)this.ds;
        }
        throw new UnknownUnwrapTypeException(unwrapType);
    }

    public void configure(Map props) {
        String jdbcDriverClass = (String)props.get("hibernate.connection.driver_class");
        String jdbcUrl = (String)props.get("hibernate.connection.url");
        Properties connectionProps = ConnectionProviderInitiator.getConnectionProperties((Map)props);
        LOG.c3p0UsingDriver(jdbcDriverClass, jdbcUrl);
        LOG.connectionProperties(ConfigurationHelper.maskOut((Properties)connectionProps, (String)"password"));
        this.autocommit = ConfigurationHelper.getBoolean((String)"hibernate.connection.autocommit", (Map)props);
        LOG.autoCommitMode(this.autocommit);
        if (jdbcDriverClass == null) {
            LOG.jdbcDriverNotSpecified("hibernate.connection.driver_class");
        } else {
            try {
                ((ClassLoaderService)this.serviceRegistry.getService(ClassLoaderService.class)).classForName(jdbcDriverClass);
            }
            catch (ClassLoadingException e) {
                throw new ClassLoadingException(LOG.jdbcDriverNotFound(jdbcDriverClass), (Throwable)e);
            }
        }
        try {
            Integer minPoolSize = ConfigurationHelper.getInteger((String)"hibernate.c3p0.min_size", (Map)props);
            Integer maxPoolSize = ConfigurationHelper.getInteger((String)"hibernate.c3p0.max_size", (Map)props);
            Integer maxIdleTime = ConfigurationHelper.getInteger((String)"hibernate.c3p0.timeout", (Map)props);
            Integer maxStatements = ConfigurationHelper.getInteger((String)"hibernate.c3p0.max_statements", (Map)props);
            Integer acquireIncrement = ConfigurationHelper.getInteger((String)"hibernate.c3p0.acquire_increment", (Map)props);
            Integer idleTestPeriod = ConfigurationHelper.getInteger((String)"hibernate.c3p0.idle_test_period", (Map)props);
            Properties c3props = new Properties();
            for (Object o : props.keySet()) {
                String key;
                if (!String.class.isInstance(o) || !(key = (String)o).startsWith("hibernate.c3p0.")) continue;
                String newKey = key.substring(15);
                if (props.containsKey(newKey)) {
                    this.warnPropertyConflict(key, newKey);
                }
                c3props.put(newKey, props.get(key));
            }
            this.setOverwriteProperty("hibernate.c3p0.min_size", C3P0_STYLE_MIN_POOL_SIZE, props, c3props, minPoolSize);
            this.setOverwriteProperty("hibernate.c3p0.max_size", C3P0_STYLE_MAX_POOL_SIZE, props, c3props, maxPoolSize);
            this.setOverwriteProperty("hibernate.c3p0.timeout", C3P0_STYLE_MAX_IDLE_TIME, props, c3props, maxIdleTime);
            this.setOverwriteProperty("hibernate.c3p0.max_statements", C3P0_STYLE_MAX_STATEMENTS, props, c3props, maxStatements);
            this.setOverwriteProperty("hibernate.c3p0.acquire_increment", C3P0_STYLE_ACQUIRE_INCREMENT, props, c3props, acquireIncrement);
            this.setOverwriteProperty("hibernate.c3p0.idle_test_period", C3P0_STYLE_IDLE_CONNECTION_TEST_PERIOD, props, c3props, idleTestPeriod);
            Integer initialPoolSize = ConfigurationHelper.getInteger((String)C3P0_STYLE_INITIAL_POOL_SIZE, (Map)props);
            if (initialPoolSize == null) {
                this.setOverwriteProperty("", C3P0_STYLE_INITIAL_POOL_SIZE, props, c3props, minPoolSize);
            }
            DataSource unpooled = DataSources.unpooledDataSource((String)jdbcUrl, (Properties)connectionProps);
            HashMap<Object, Object> allProps = new HashMap<Object, Object>();
            allProps.putAll(props);
            allProps.putAll(c3props);
            this.ds = DataSources.pooledDataSource((DataSource)unpooled, allProps);
        }
        catch (Exception e) {
            LOG.error(LOG.unableToInstantiateC3p0ConnectionPool(), e);
            throw new HibernateException(LOG.unableToInstantiateC3p0ConnectionPool(), (Throwable)e);
        }
        this.isolation = ConnectionProviderInitiator.extractIsolation((Map)props);
        LOG.jdbcIsolationLevel(ConnectionProviderInitiator.toIsolationNiceName((Integer)this.isolation));
    }

    public boolean supportsAggressiveRelease() {
        return false;
    }

    private void setOverwriteProperty(String hibernateStyleKey, String c3p0StyleKey, Map hibp, Properties c3p, Integer value) {
        if (value != null) {
            String longC3p0StyleKey;
            String peeledC3p0Key = c3p0StyleKey.substring(5);
            c3p.put(peeledC3p0Key, String.valueOf(value).trim());
            if (hibp.containsKey(c3p0StyleKey)) {
                this.warnPropertyConflict(hibernateStyleKey, c3p0StyleKey);
            }
            if (hibp.containsKey(longC3p0StyleKey = "hibernate." + c3p0StyleKey)) {
                this.warnPropertyConflict(hibernateStyleKey, longC3p0StyleKey);
            }
        }
    }

    private void warnPropertyConflict(String hibernateStyle, String c3p0Style) {
        LOG.bothHibernateAndC3p0StylesSet(hibernateStyle, c3p0Style);
    }

    public void stop() {
        try {
            DataSources.destroy((DataSource)this.ds);
        }
        catch (SQLException sqle) {
            LOG.unableToDestroyC3p0ConnectionPool(sqle);
        }
    }

    @Deprecated
    public void close() {
        this.stop();
    }

    public void injectServices(ServiceRegistryImplementor serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }
}

