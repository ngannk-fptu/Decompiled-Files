/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc.connections.internal;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.hibernate.HibernateException;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.engine.jdbc.connections.internal.BasicConnectionCreator;
import org.hibernate.engine.jdbc.connections.internal.ConnectionCreator;
import org.hibernate.engine.jdbc.connections.internal.ConnectionCreatorFactory;
import org.hibernate.engine.jdbc.connections.internal.ConnectionCreatorFactoryImpl;
import org.hibernate.engine.jdbc.connections.internal.ConnectionProviderInitiator;
import org.hibernate.engine.jdbc.connections.internal.ConnectionValidator;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.log.ConnectionPoolingLogger;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.service.UnknownUnwrapTypeException;
import org.hibernate.service.spi.Configurable;
import org.hibernate.service.spi.ServiceException;
import org.hibernate.service.spi.ServiceRegistryAwareService;
import org.hibernate.service.spi.ServiceRegistryImplementor;
import org.hibernate.service.spi.Stoppable;

public class DriverManagerConnectionProviderImpl
implements ConnectionProvider,
Configurable,
Stoppable,
ServiceRegistryAwareService,
ConnectionValidator {
    private static final ConnectionPoolingLogger log = ConnectionPoolingLogger.CONNECTIONS_LOGGER;
    public static final String MIN_SIZE = "hibernate.connection.min_pool_size";
    public static final String INITIAL_SIZE = "hibernate.connection.initial_pool_size";
    public static final String VALIDATION_INTERVAL = "hibernate.connection.pool_validation_interval";
    public static final String INIT_SQL = "hibernate.connection.init_sql";
    public static final String CONNECTION_CREATOR_FACTORY = "hibernate.connection.creator_factory_class";
    private volatile PoolState state;
    private volatile ServiceRegistryImplementor serviceRegistry;

    @Override
    public void injectServices(ServiceRegistryImplementor serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public void configure(Map configurationValues) {
        PoolState newstate;
        log.usingHibernateBuiltInConnectionPool();
        PooledConnections pool = this.buildPool(configurationValues, this.serviceRegistry);
        long validationInterval = ConfigurationHelper.getLong(VALIDATION_INTERVAL, configurationValues, 30);
        this.state = newstate = new PoolState(pool, validationInterval);
    }

    private PooledConnections buildPool(Map configurationValues, ServiceRegistryImplementor serviceRegistry) {
        boolean autoCommit = ConfigurationHelper.getBoolean("hibernate.connection.autocommit", configurationValues, false);
        int minSize = ConfigurationHelper.getInt(MIN_SIZE, configurationValues, 1);
        int maxSize = ConfigurationHelper.getInt("hibernate.connection.pool_size", configurationValues, 20);
        int initialSize = ConfigurationHelper.getInt(INITIAL_SIZE, configurationValues, minSize);
        ConnectionCreator connectionCreator = DriverManagerConnectionProviderImpl.buildCreator(configurationValues, serviceRegistry);
        PooledConnections.Builder pooledConnectionBuilder = new PooledConnections.Builder(connectionCreator, autoCommit);
        pooledConnectionBuilder.initialSize(initialSize);
        pooledConnectionBuilder.minSize(minSize);
        pooledConnectionBuilder.maxSize(maxSize);
        pooledConnectionBuilder.validator(this);
        return pooledConnectionBuilder.build();
    }

    private static ConnectionCreator buildCreator(Map configurationValues, ServiceRegistryImplementor serviceRegistry) {
        String url = (String)configurationValues.get("hibernate.connection.url");
        String driverClassName = (String)configurationValues.get("hibernate.connection.driver_class");
        Driver driver = null;
        if (driverClassName != null) {
            driver = DriverManagerConnectionProviderImpl.loadDriverIfPossible(driverClassName, serviceRegistry);
        }
        if (url == null) {
            String msg = log.jdbcUrlNotSpecified("hibernate.connection.url");
            log.error(msg);
            throw new HibernateException(msg);
        }
        log.usingDriver(driverClassName, url);
        Properties connectionProps = ConnectionProviderInitiator.getConnectionProperties(configurationValues);
        if (log.isDebugEnabled()) {
            log.connectionProperties(connectionProps);
        } else {
            log.connectionProperties(ConfigurationHelper.maskOut(connectionProps, "password"));
        }
        boolean autoCommit = ConfigurationHelper.getBoolean("hibernate.connection.autocommit", configurationValues, false);
        log.autoCommitMode(autoCommit);
        Integer isolation = ConnectionProviderInitiator.extractIsolation(configurationValues);
        if (isolation != null) {
            log.jdbcIsolationLevel(ConnectionProviderInitiator.toIsolationNiceName(isolation));
        }
        String initSql = (String)configurationValues.get(INIT_SQL);
        Object connectionCreatorFactory = configurationValues.get(CONNECTION_CREATOR_FACTORY);
        ConnectionCreatorFactory factory = null;
        if (connectionCreatorFactory instanceof ConnectionCreatorFactory) {
            factory = (ConnectionCreatorFactory)connectionCreatorFactory;
        } else if (connectionCreatorFactory != null) {
            factory = DriverManagerConnectionProviderImpl.loadConnectionCreatorFactory(connectionCreatorFactory.toString(), serviceRegistry);
        }
        if (factory == null) {
            factory = ConnectionCreatorFactoryImpl.INSTANCE;
        }
        return factory.create(driver, serviceRegistry, url, connectionProps, autoCommit, isolation, initSql, configurationValues);
    }

    private static Driver loadDriverIfPossible(String driverClassName, ServiceRegistryImplementor serviceRegistry) {
        if (driverClassName == null) {
            log.debug("No driver class specified");
            return null;
        }
        if (serviceRegistry != null) {
            ClassLoaderService classLoaderService = serviceRegistry.getService(ClassLoaderService.class);
            Class driverClass = classLoaderService.classForName(driverClassName);
            try {
                return (Driver)driverClass.newInstance();
            }
            catch (Exception e) {
                throw new ServiceException("Specified JDBC Driver " + driverClassName + " could not be loaded", e);
            }
        }
        try {
            return (Driver)Class.forName(driverClassName).newInstance();
        }
        catch (Exception e1) {
            throw new ServiceException("Specified JDBC Driver " + driverClassName + " could not be loaded", e1);
        }
    }

    private static ConnectionCreatorFactory loadConnectionCreatorFactory(String connectionCreatorFactoryClassName, ServiceRegistryImplementor serviceRegistry) {
        if (connectionCreatorFactoryClassName == null) {
            log.debug("No connection creator factory class specified");
            return null;
        }
        if (serviceRegistry != null) {
            ClassLoaderService classLoaderService = serviceRegistry.getService(ClassLoaderService.class);
            Class factoryClass = classLoaderService.classForName(connectionCreatorFactoryClassName);
            try {
                return (ConnectionCreatorFactory)factoryClass.newInstance();
            }
            catch (Exception e) {
                throw new ServiceException("Specified ConnectionCreatorFactory " + connectionCreatorFactoryClassName + " could not be loaded", e);
            }
        }
        try {
            return (ConnectionCreatorFactory)Class.forName(connectionCreatorFactoryClassName).newInstance();
        }
        catch (Exception e1) {
            throw new ServiceException("Specified ConnectionCreatorFactory " + connectionCreatorFactoryClassName + " could not be loaded", e1);
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (this.state == null) {
            throw new IllegalStateException("Cannot get a connection as the driver manager is not properly initialized");
        }
        return this.state.getConnection();
    }

    @Override
    public void closeConnection(Connection conn) throws SQLException {
        if (this.state == null) {
            throw new IllegalStateException("Cannot close a connection as the driver manager is not properly initialized");
        }
        this.state.closeConnection(conn);
    }

    @Override
    public boolean supportsAggressiveRelease() {
        return false;
    }

    @Override
    public boolean isUnwrappableAs(Class unwrapType) {
        return ConnectionProvider.class.equals((Object)unwrapType) || DriverManagerConnectionProviderImpl.class.isAssignableFrom(unwrapType);
    }

    @Override
    public <T> T unwrap(Class<T> unwrapType) {
        if (ConnectionProvider.class.equals(unwrapType) || DriverManagerConnectionProviderImpl.class.isAssignableFrom(unwrapType)) {
            return (T)this;
        }
        throw new UnknownUnwrapTypeException(unwrapType);
    }

    protected void validateConnectionsReturned() {
        int allocationCount = this.state.pool.allConnections.size() - this.state.pool.availableConnections.size();
        if (allocationCount != 0) {
            log.error("Connection leak detected: there are " + allocationCount + " unclosed connections!");
        }
    }

    @Override
    public void stop() {
        if (this.state != null) {
            this.state.stop();
            this.validateConnectionsReturned();
        }
    }

    protected void finalize() throws Throwable {
        if (this.state != null) {
            this.state.stop();
        }
        super.finalize();
    }

    public Properties getConnectionProperties() {
        BasicConnectionCreator connectionCreator = (BasicConnectionCreator)this.state.pool.connectionCreator;
        return connectionCreator.getConnectionProperties();
    }

    @Override
    public boolean isValid(Connection connection) throws SQLException {
        return true;
    }

    private static class ValidationThreadFactory
    implements ThreadFactory {
        private ValidationThreadFactory() {
        }

        @Override
        public Thread newThread(Runnable runnable) {
            Thread thread = new Thread(runnable);
            thread.setDaemon(true);
            thread.setName("Hibernate Connection Pool Validation Thread");
            return thread;
        }
    }

    private static class PoolState {
        private final ReadWriteLock statelock = new ReentrantReadWriteLock();
        private volatile boolean active = false;
        private ScheduledExecutorService executorService;
        private final PooledConnections pool;
        private final long validationInterval;

        public PoolState(PooledConnections pool, long validationInterval) {
            this.pool = pool;
            this.validationInterval = validationInterval;
        }

        private void startIfNeeded() {
            if (this.active) {
                return;
            }
            this.statelock.writeLock().lock();
            try {
                if (this.active) {
                    return;
                }
                this.executorService = Executors.newSingleThreadScheduledExecutor(new ValidationThreadFactory());
                this.executorService.scheduleWithFixedDelay(this.pool::validate, this.validationInterval, this.validationInterval, TimeUnit.SECONDS);
                this.active = true;
            }
            finally {
                this.statelock.writeLock().unlock();
            }
        }

        public void stop() {
            this.statelock.writeLock().lock();
            try {
                if (!this.active) {
                    return;
                }
                log.cleaningUpConnectionPool(this.pool.getUrl());
                this.active = false;
                if (this.executorService != null) {
                    this.executorService.shutdown();
                }
                this.executorService = null;
                try {
                    this.pool.close();
                }
                catch (SQLException e) {
                    log.unableToClosePooledConnection(e);
                }
            }
            finally {
                this.statelock.writeLock().unlock();
            }
        }

        public Connection getConnection() throws SQLException {
            this.startIfNeeded();
            this.statelock.readLock().lock();
            try {
                Connection connection = this.pool.poll();
                return connection;
            }
            finally {
                this.statelock.readLock().unlock();
            }
        }

        public void closeConnection(Connection conn) throws SQLException {
            if (conn == null) {
                return;
            }
            this.startIfNeeded();
            this.statelock.readLock().lock();
            try {
                this.pool.add(conn);
            }
            finally {
                this.statelock.readLock().unlock();
            }
        }
    }

    public static class PooledConnections {
        private final ConcurrentLinkedQueue<Connection> allConnections = new ConcurrentLinkedQueue();
        private final ConcurrentLinkedQueue<Connection> availableConnections = new ConcurrentLinkedQueue();
        private static final CoreMessageLogger log = CoreLogging.messageLogger(DriverManagerConnectionProviderImpl.class);
        private final ConnectionCreator connectionCreator;
        private final ConnectionValidator connectionValidator;
        private final boolean autoCommit;
        private final int minSize;
        private final int maxSize;
        private volatile boolean primed;

        private PooledConnections(Builder builder) {
            log.debugf("Initializing Connection pool with %s Connections", builder.initialSize);
            this.connectionCreator = builder.connectionCreator;
            this.connectionValidator = builder.connectionValidator == null ? ConnectionValidator.ALWAYS_VALID : builder.connectionValidator;
            this.autoCommit = builder.autoCommit;
            this.maxSize = builder.maxSize;
            this.minSize = builder.minSize;
            log.hibernateConnectionPoolSize(this.maxSize, this.minSize);
            this.addConnections(builder.initialSize);
        }

        public void validate() {
            int size = this.size();
            if (!this.primed && size >= this.minSize) {
                log.debug("Connection pool now considered primed; min-size will be maintained");
                this.primed = true;
            }
            if (size < this.minSize && this.primed) {
                int numberToBeAdded = this.minSize - size;
                log.debugf("Adding %s Connections to the pool", numberToBeAdded);
                this.addConnections(numberToBeAdded);
            } else if (size > this.maxSize) {
                int numberToBeRemoved = size - this.maxSize;
                log.debugf("Removing %s Connections from the pool", numberToBeRemoved);
                this.removeConnections(numberToBeRemoved);
            }
        }

        public void add(Connection conn) throws SQLException {
            Connection connection = this.releaseConnection(conn);
            if (connection != null) {
                this.availableConnections.offer(connection);
            }
        }

        protected Connection releaseConnection(Connection conn) {
            SQLException t = null;
            try {
                conn.setAutoCommit(true);
                conn.clearWarnings();
                if (this.connectionValidator.isValid(conn)) {
                    return conn;
                }
            }
            catch (SQLException ex) {
                t = ex;
            }
            this.closeConnection(conn, t);
            log.debug("Connection release failed. Closing pooled connection", t);
            return null;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public Connection poll() throws SQLException {
            Connection conn;
            do {
                if ((conn = this.availableConnections.poll()) != null) continue;
                ConcurrentLinkedQueue<Connection> concurrentLinkedQueue = this.allConnections;
                synchronized (concurrentLinkedQueue) {
                    if (this.allConnections.size() < this.maxSize) {
                        this.addConnections(1);
                        return this.poll();
                    }
                }
                throw new HibernateException("The internal connection pool has reached its maximum size and no connection is currently available!");
            } while ((conn = this.prepareConnection(conn)) == null);
            return conn;
        }

        protected Connection prepareConnection(Connection conn) {
            SQLException t = null;
            try {
                conn.setAutoCommit(this.autoCommit);
                if (this.connectionValidator.isValid(conn)) {
                    return conn;
                }
            }
            catch (SQLException ex) {
                t = ex;
            }
            this.closeConnection(conn, t);
            log.debug("Connection preparation failed. Closing pooled connection", t);
            return null;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        protected void closeConnection(Connection conn, Throwable t) {
            try {
                conn.close();
            }
            catch (SQLException ex) {
                log.unableToCloseConnection(ex);
                if (t != null) {
                    t.addSuppressed(ex);
                }
            }
            finally {
                this.allConnections.remove(conn);
            }
        }

        public void close() throws SQLException {
            try {
                int allocationCount = this.allConnections.size() - this.availableConnections.size();
                if (allocationCount > 0) {
                    log.error("Connection leak detected: there are " + allocationCount + " unclosed connections upon shutting down pool " + this.getUrl());
                }
            }
            finally {
                for (Connection connection : this.allConnections) {
                    connection.close();
                }
            }
        }

        public int size() {
            return this.availableConnections.size();
        }

        protected void removeConnections(int numberToBeRemoved) {
            for (int i = 0; i < numberToBeRemoved; ++i) {
                Connection connection = this.availableConnections.poll();
                try {
                    if (connection != null) {
                        connection.close();
                    }
                    this.allConnections.remove(connection);
                    continue;
                }
                catch (SQLException e) {
                    log.unableToCloseConnection(e);
                }
            }
        }

        protected void addConnections(int numberOfConnections) {
            for (int i = 0; i < numberOfConnections; ++i) {
                Connection connection = this.connectionCreator.createConnection();
                this.allConnections.add(connection);
                this.availableConnections.add(connection);
            }
        }

        public String getUrl() {
            return this.connectionCreator.getUrl();
        }

        public static class Builder {
            private final ConnectionCreator connectionCreator;
            private ConnectionValidator connectionValidator;
            private boolean autoCommit;
            private int initialSize = 1;
            private int minSize = 1;
            private int maxSize = 20;

            public Builder(ConnectionCreator connectionCreator, boolean autoCommit) {
                this.connectionCreator = connectionCreator;
                this.autoCommit = autoCommit;
            }

            public Builder initialSize(int initialSize) {
                this.initialSize = initialSize;
                return this;
            }

            public Builder minSize(int minSize) {
                this.minSize = minSize;
                return this;
            }

            public Builder maxSize(int maxSize) {
                this.maxSize = maxSize;
                return this;
            }

            public Builder validator(ConnectionValidator connectionValidator) {
                this.connectionValidator = connectionValidator;
                return this;
            }

            public PooledConnections build() {
                return new PooledConnections(this);
            }
        }
    }
}

