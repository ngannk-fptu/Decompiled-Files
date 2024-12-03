/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.tomcat.dbcp.dbcp2;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.StandardMBean;
import javax.sql.DataSource;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.dbcp.dbcp2.BasicDataSourceMXBean;
import org.apache.tomcat.dbcp.dbcp2.ConnectionFactory;
import org.apache.tomcat.dbcp.dbcp2.ConnectionFactoryFactory;
import org.apache.tomcat.dbcp.dbcp2.DataSourceMXBean;
import org.apache.tomcat.dbcp.dbcp2.DriverFactory;
import org.apache.tomcat.dbcp.dbcp2.ObjectNameWrapper;
import org.apache.tomcat.dbcp.dbcp2.PoolableConnection;
import org.apache.tomcat.dbcp.dbcp2.PoolableConnectionFactory;
import org.apache.tomcat.dbcp.dbcp2.PoolingDataSource;
import org.apache.tomcat.dbcp.dbcp2.SwallowedExceptionLogger;
import org.apache.tomcat.dbcp.dbcp2.Utils;
import org.apache.tomcat.dbcp.pool2.PooledObject;
import org.apache.tomcat.dbcp.pool2.impl.AbandonedConfig;
import org.apache.tomcat.dbcp.pool2.impl.BaseGenericObjectPool;
import org.apache.tomcat.dbcp.pool2.impl.BaseObjectPoolConfig;
import org.apache.tomcat.dbcp.pool2.impl.GenericObjectPool;
import org.apache.tomcat.dbcp.pool2.impl.GenericObjectPoolConfig;

public class BasicDataSource
implements DataSource,
BasicDataSourceMXBean,
MBeanRegistration,
AutoCloseable {
    private static final Log log = LogFactory.getLog(BasicDataSource.class);
    private volatile Boolean defaultAutoCommit;
    private transient Boolean defaultReadOnly;
    private volatile int defaultTransactionIsolation = -1;
    private Duration defaultQueryTimeoutDuration;
    private volatile String defaultCatalog;
    private volatile String defaultSchema;
    private boolean cacheState = true;
    private Driver driver;
    private String driverClassName;
    private ClassLoader driverClassLoader;
    private boolean lifo = true;
    private int maxTotal = 8;
    private int maxIdle = 8;
    private int minIdle = 0;
    private int initialSize;
    private Duration maxWaitDuration = BaseObjectPoolConfig.DEFAULT_MAX_WAIT;
    private boolean poolPreparedStatements;
    private boolean clearStatementPoolOnReturn;
    private int maxOpenPreparedStatements = -1;
    private boolean testOnCreate;
    private boolean testOnBorrow = true;
    private boolean testOnReturn;
    private Duration durationBetweenEvictionRuns = BaseObjectPoolConfig.DEFAULT_DURATION_BETWEEN_EVICTION_RUNS;
    private int numTestsPerEvictionRun = 3;
    private Duration minEvictableIdleDuration = BaseObjectPoolConfig.DEFAULT_MIN_EVICTABLE_IDLE_DURATION;
    private Duration softMinEvictableIdleDuration = BaseObjectPoolConfig.DEFAULT_SOFT_MIN_EVICTABLE_IDLE_DURATION;
    private String evictionPolicyClassName = BaseObjectPoolConfig.DEFAULT_EVICTION_POLICY_CLASS_NAME;
    private boolean testWhileIdle;
    private volatile String password;
    private String connectionString;
    private String userName;
    private volatile String validationQuery;
    private volatile Duration validationQueryTimeoutDuration = Duration.ofSeconds(-1L);
    private String connectionFactoryClassName;
    private volatile List<String> connectionInitSqls;
    private boolean accessToUnderlyingConnectionAllowed;
    private Duration maxConnDuration = Duration.ofMillis(-1L);
    private boolean logExpiredConnections = true;
    private String jmxName;
    private boolean registerConnectionMBean = true;
    private boolean autoCommitOnReturn = true;
    private boolean rollbackOnReturn = true;
    private volatile Set<String> disconnectionSqlCodes;
    private boolean fastFailValidation;
    private volatile GenericObjectPool<PoolableConnection> connectionPool;
    private Properties connectionProperties = new Properties();
    private volatile DataSource dataSource;
    private volatile PrintWriter logWriter = new PrintWriter(new OutputStreamWriter((OutputStream)System.out, StandardCharsets.UTF_8));
    private AbandonedConfig abandonedConfig;
    private boolean closed;
    private ObjectNameWrapper registeredJmxObjectName;

    protected static void validateConnectionFactory(PoolableConnectionFactory connectionFactory) throws SQLException {
        PoolableConnection conn = null;
        PooledObject<PoolableConnection> p = null;
        try {
            p = connectionFactory.makeObject();
            conn = p.getObject();
            connectionFactory.activateObject(p);
            connectionFactory.validateConnection(conn);
            connectionFactory.passivateObject(p);
        }
        finally {
            if (p != null) {
                connectionFactory.destroyObject(p);
            }
        }
    }

    public void addConnectionProperty(String name, String value) {
        this.connectionProperties.put(name, value);
    }

    @Override
    public synchronized void close() throws SQLException {
        if (this.registeredJmxObjectName != null) {
            this.registeredJmxObjectName.unregisterMBean();
            this.registeredJmxObjectName = null;
        }
        this.closed = true;
        GenericObjectPool<PoolableConnection> oldPool = this.connectionPool;
        this.connectionPool = null;
        this.dataSource = null;
        try {
            if (oldPool != null) {
                oldPool.close();
            }
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new SQLException(Utils.getMessage("pool.close.fail"), e);
        }
    }

    private void closeConnectionPool() {
        GenericObjectPool<PoolableConnection> oldPool = this.connectionPool;
        this.connectionPool = null;
        Utils.closeQuietly(oldPool);
    }

    protected ConnectionFactory createConnectionFactory() throws SQLException {
        return ConnectionFactoryFactory.createConnectionFactory(this, DriverFactory.createDriver(this));
    }

    protected void createConnectionPool(PoolableConnectionFactory factory) {
        GenericObjectPoolConfig<PoolableConnection> config = new GenericObjectPoolConfig<PoolableConnection>();
        this.updateJmxName(config);
        config.setJmxEnabled(this.registeredJmxObjectName != null);
        GenericObjectPool<PoolableConnection> gop = this.createObjectPool(factory, config, this.abandonedConfig);
        gop.setMaxTotal(this.maxTotal);
        gop.setMaxIdle(this.maxIdle);
        gop.setMinIdle(this.minIdle);
        gop.setMaxWait(this.maxWaitDuration);
        gop.setTestOnCreate(this.testOnCreate);
        gop.setTestOnBorrow(this.testOnBorrow);
        gop.setTestOnReturn(this.testOnReturn);
        gop.setNumTestsPerEvictionRun(this.numTestsPerEvictionRun);
        gop.setMinEvictableIdleDuration(this.minEvictableIdleDuration);
        gop.setSoftMinEvictableIdleDuration(this.softMinEvictableIdleDuration);
        gop.setTestWhileIdle(this.testWhileIdle);
        gop.setLifo(this.lifo);
        gop.setSwallowedExceptionListener(new SwallowedExceptionLogger(log, this.logExpiredConnections));
        gop.setEvictionPolicyClassName(this.evictionPolicyClassName);
        factory.setPool(gop);
        this.connectionPool = gop;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected synchronized DataSource createDataSource() throws SQLException {
        if (this.closed) {
            throw new SQLException("Data source is closed");
        }
        if (this.dataSource != null) {
            return this.dataSource;
        }
        BasicDataSource basicDataSource = this;
        synchronized (basicDataSource) {
            if (this.dataSource != null) {
                return this.dataSource;
            }
            this.jmxRegister();
            ConnectionFactory driverConnectionFactory = this.createConnectionFactory();
            try {
                PoolableConnectionFactory poolableConnectionFactory = this.createPoolableConnectionFactory(driverConnectionFactory);
                poolableConnectionFactory.setPoolStatements(this.poolPreparedStatements);
                poolableConnectionFactory.setMaxOpenPreparedStatements(this.maxOpenPreparedStatements);
                this.createConnectionPool(poolableConnectionFactory);
                DataSource newDataSource = this.createDataSourceInstance();
                newDataSource.setLogWriter(this.logWriter);
                this.connectionPool.addObjects(this.initialSize);
                this.startPoolMaintenance();
                this.dataSource = newDataSource;
            }
            catch (RuntimeException | SQLException se) {
                this.closeConnectionPool();
                throw se;
            }
            catch (Exception ex) {
                this.closeConnectionPool();
                throw new SQLException("Error creating connection factory", ex);
            }
            return this.dataSource;
        }
    }

    protected DataSource createDataSourceInstance() throws SQLException {
        PoolingDataSource<PoolableConnection> pds = new PoolingDataSource<PoolableConnection>(this.connectionPool);
        pds.setAccessToUnderlyingConnectionAllowed(this.isAccessToUnderlyingConnectionAllowed());
        return pds;
    }

    protected GenericObjectPool<PoolableConnection> createObjectPool(PoolableConnectionFactory factory, GenericObjectPoolConfig<PoolableConnection> poolConfig, AbandonedConfig abandonedConfig) {
        GenericObjectPool<PoolableConnection> gop = abandonedConfig != null && (abandonedConfig.getRemoveAbandonedOnBorrow() || abandonedConfig.getRemoveAbandonedOnMaintenance()) ? new GenericObjectPool<PoolableConnection>(factory, poolConfig, abandonedConfig) : new GenericObjectPool<PoolableConnection>(factory, poolConfig);
        return gop;
    }

    protected PoolableConnectionFactory createPoolableConnectionFactory(ConnectionFactory driverConnectionFactory) throws SQLException {
        PoolableConnectionFactory connectionFactory = null;
        try {
            connectionFactory = this.registerConnectionMBean ? new PoolableConnectionFactory(driverConnectionFactory, ObjectNameWrapper.unwrap(this.registeredJmxObjectName)) : new PoolableConnectionFactory(driverConnectionFactory, null);
            connectionFactory.setValidationQuery(this.validationQuery);
            connectionFactory.setValidationQueryTimeout(this.validationQueryTimeoutDuration);
            connectionFactory.setConnectionInitSql(this.connectionInitSqls);
            connectionFactory.setDefaultReadOnly(this.defaultReadOnly);
            connectionFactory.setDefaultAutoCommit(this.defaultAutoCommit);
            connectionFactory.setDefaultTransactionIsolation(this.defaultTransactionIsolation);
            connectionFactory.setDefaultCatalog(this.defaultCatalog);
            connectionFactory.setDefaultSchema(this.defaultSchema);
            connectionFactory.setCacheState(this.cacheState);
            connectionFactory.setPoolStatements(this.poolPreparedStatements);
            connectionFactory.setClearStatementPoolOnReturn(this.clearStatementPoolOnReturn);
            connectionFactory.setMaxOpenPreparedStatements(this.maxOpenPreparedStatements);
            connectionFactory.setMaxConn(this.maxConnDuration);
            connectionFactory.setRollbackOnReturn(this.getRollbackOnReturn());
            connectionFactory.setAutoCommitOnReturn(this.getAutoCommitOnReturn());
            connectionFactory.setDefaultQueryTimeout(this.getDefaultQueryTimeoutDuration());
            connectionFactory.setFastFailValidation(this.fastFailValidation);
            connectionFactory.setDisconnectionSqlCodes(this.disconnectionSqlCodes);
            BasicDataSource.validateConnectionFactory(connectionFactory);
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new SQLException("Cannot create PoolableConnectionFactory (" + e.getMessage() + ")", e);
        }
        return connectionFactory;
    }

    public void evict() throws Exception {
        if (this.connectionPool != null) {
            this.connectionPool.evict();
        }
    }

    public PrintWriter getAbandonedLogWriter() {
        return this.abandonedConfig == null ? null : this.abandonedConfig.getLogWriter();
    }

    @Override
    public boolean getAbandonedUsageTracking() {
        return this.abandonedConfig != null && this.abandonedConfig.getUseUsageTracking();
    }

    public boolean getAutoCommitOnReturn() {
        return this.autoCommitOnReturn;
    }

    @Override
    public boolean getCacheState() {
        return this.cacheState;
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (Utils.isSecurityEnabled()) {
            PrivilegedExceptionAction<Connection> action = () -> this.createDataSource().getConnection();
            try {
                return AccessController.doPrivileged(action);
            }
            catch (PrivilegedActionException e) {
                Throwable cause = e.getCause();
                if (cause instanceof SQLException) {
                    throw (SQLException)cause;
                }
                throw new SQLException(e);
            }
        }
        return this.createDataSource().getConnection();
    }

    @Override
    public Connection getConnection(String user, String pass) throws SQLException {
        throw new UnsupportedOperationException("Not supported by BasicDataSource");
    }

    public String getConnectionFactoryClassName() {
        return this.connectionFactoryClassName;
    }

    public List<String> getConnectionInitSqls() {
        List<String> result = this.connectionInitSqls;
        return result == null ? Collections.emptyList() : result;
    }

    @Override
    public String[] getConnectionInitSqlsAsArray() {
        return this.getConnectionInitSqls().toArray(Utils.EMPTY_STRING_ARRAY);
    }

    protected GenericObjectPool<PoolableConnection> getConnectionPool() {
        return this.connectionPool;
    }

    Properties getConnectionProperties() {
        return this.connectionProperties;
    }

    @Override
    public Boolean getDefaultAutoCommit() {
        return this.defaultAutoCommit;
    }

    @Override
    public String getDefaultCatalog() {
        return this.defaultCatalog;
    }

    @Deprecated
    public Integer getDefaultQueryTimeout() {
        return this.defaultQueryTimeoutDuration == null ? null : Integer.valueOf((int)this.defaultQueryTimeoutDuration.getSeconds());
    }

    public Duration getDefaultQueryTimeoutDuration() {
        return this.defaultQueryTimeoutDuration;
    }

    @Override
    public Boolean getDefaultReadOnly() {
        return this.defaultReadOnly;
    }

    @Override
    public String getDefaultSchema() {
        return this.defaultSchema;
    }

    @Override
    public int getDefaultTransactionIsolation() {
        return this.defaultTransactionIsolation;
    }

    public Set<String> getDisconnectionSqlCodes() {
        Set<String> result = this.disconnectionSqlCodes;
        return result == null ? Collections.emptySet() : result;
    }

    @Override
    public String[] getDisconnectionSqlCodesAsArray() {
        return this.getDisconnectionSqlCodes().toArray(Utils.EMPTY_STRING_ARRAY);
    }

    public synchronized Driver getDriver() {
        return this.driver;
    }

    public synchronized ClassLoader getDriverClassLoader() {
        return this.driverClassLoader;
    }

    @Override
    public synchronized String getDriverClassName() {
        return this.driverClassName;
    }

    @Deprecated
    public boolean getEnableAutoCommitOnReturn() {
        return this.autoCommitOnReturn;
    }

    public synchronized String getEvictionPolicyClassName() {
        return this.evictionPolicyClassName;
    }

    @Override
    public boolean getFastFailValidation() {
        return this.fastFailValidation;
    }

    @Override
    public synchronized int getInitialSize() {
        return this.initialSize;
    }

    public String getJmxName() {
        return this.jmxName;
    }

    @Override
    public synchronized boolean getLifo() {
        return this.lifo;
    }

    @Override
    public boolean getLogAbandoned() {
        return this.abandonedConfig != null && this.abandonedConfig.getLogAbandoned();
    }

    @Override
    public boolean getLogExpiredConnections() {
        return this.logExpiredConnections;
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        throw new UnsupportedOperationException("Not supported by BasicDataSource");
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return this.createDataSource().getLogWriter();
    }

    public Duration getMaxConnDuration() {
        return this.maxConnDuration;
    }

    @Override
    @Deprecated
    public long getMaxConnLifetimeMillis() {
        return this.maxConnDuration.toMillis();
    }

    @Override
    public synchronized int getMaxIdle() {
        return this.maxIdle;
    }

    @Override
    public synchronized int getMaxOpenPreparedStatements() {
        return this.maxOpenPreparedStatements;
    }

    @Override
    public synchronized int getMaxTotal() {
        return this.maxTotal;
    }

    public synchronized Duration getMaxWaitDuration() {
        return this.maxWaitDuration;
    }

    @Override
    @Deprecated
    public synchronized long getMaxWaitMillis() {
        return this.maxWaitDuration.toMillis();
    }

    public synchronized Duration getMinEvictableIdleDuration() {
        return this.minEvictableIdleDuration;
    }

    @Override
    @Deprecated
    public synchronized long getMinEvictableIdleTimeMillis() {
        return this.minEvictableIdleDuration.toMillis();
    }

    @Override
    public synchronized int getMinIdle() {
        return this.minIdle;
    }

    @Override
    public int getNumActive() {
        GenericObjectPool<PoolableConnection> pool = this.connectionPool;
        return pool == null ? 0 : pool.getNumActive();
    }

    @Override
    public int getNumIdle() {
        GenericObjectPool<PoolableConnection> pool = this.connectionPool;
        return pool == null ? 0 : pool.getNumIdle();
    }

    @Override
    public synchronized int getNumTestsPerEvictionRun() {
        return this.numTestsPerEvictionRun;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    protected ObjectName getRegisteredJmxName() {
        return ObjectNameWrapper.unwrap(this.registeredJmxObjectName);
    }

    @Override
    public boolean getRemoveAbandonedOnBorrow() {
        return this.abandonedConfig != null && this.abandonedConfig.getRemoveAbandonedOnBorrow();
    }

    @Override
    public boolean getRemoveAbandonedOnMaintenance() {
        return this.abandonedConfig != null && this.abandonedConfig.getRemoveAbandonedOnMaintenance();
    }

    @Override
    @Deprecated
    public int getRemoveAbandonedTimeout() {
        return (int)this.getRemoveAbandonedTimeoutDuration().getSeconds();
    }

    public Duration getRemoveAbandonedTimeoutDuration() {
        return this.abandonedConfig == null ? Duration.ofSeconds(300L) : this.abandonedConfig.getRemoveAbandonedTimeoutDuration();
    }

    public boolean getRollbackOnReturn() {
        return this.rollbackOnReturn;
    }

    public synchronized Duration getSoftMinEvictableIdleDuration() {
        return this.softMinEvictableIdleDuration;
    }

    @Override
    @Deprecated
    public synchronized long getSoftMinEvictableIdleTimeMillis() {
        return this.softMinEvictableIdleDuration.toMillis();
    }

    @Override
    public synchronized boolean getTestOnBorrow() {
        return this.testOnBorrow;
    }

    @Override
    public synchronized boolean getTestOnCreate() {
        return this.testOnCreate;
    }

    public synchronized boolean getTestOnReturn() {
        return this.testOnReturn;
    }

    @Override
    public synchronized boolean getTestWhileIdle() {
        return this.testWhileIdle;
    }

    public synchronized Duration getDurationBetweenEvictionRuns() {
        return this.durationBetweenEvictionRuns;
    }

    @Override
    @Deprecated
    public synchronized long getTimeBetweenEvictionRunsMillis() {
        return this.durationBetweenEvictionRuns.toMillis();
    }

    @Override
    public synchronized String getUrl() {
        return this.connectionString;
    }

    @Override
    public String getUsername() {
        return this.userName;
    }

    @Override
    public String getValidationQuery() {
        return this.validationQuery;
    }

    public Duration getValidationQueryTimeoutDuration() {
        return this.validationQueryTimeoutDuration;
    }

    @Override
    @Deprecated
    public int getValidationQueryTimeout() {
        return (int)this.validationQueryTimeoutDuration.getSeconds();
    }

    public void invalidateConnection(Connection connection) throws IllegalStateException {
        PoolableConnection poolableConnection;
        if (connection == null) {
            return;
        }
        if (this.connectionPool == null) {
            throw new IllegalStateException("Cannot invalidate connection: ConnectionPool is null.");
        }
        try {
            poolableConnection = connection.unwrap(PoolableConnection.class);
            if (poolableConnection == null) {
                throw new IllegalStateException("Cannot invalidate connection: Connection is not a poolable connection.");
            }
        }
        catch (SQLException e) {
            throw new IllegalStateException("Cannot invalidate connection: Unwrapping poolable connection failed.", e);
        }
        try {
            this.connectionPool.invalidateObject(poolableConnection);
        }
        catch (Exception e) {
            throw new IllegalStateException("Invalidating connection threw unexpected exception", e);
        }
    }

    @Override
    public synchronized boolean isAccessToUnderlyingConnectionAllowed() {
        return this.accessToUnderlyingConnectionAllowed;
    }

    @Override
    public boolean isClearStatementPoolOnReturn() {
        return this.clearStatementPoolOnReturn;
    }

    @Override
    public synchronized boolean isClosed() {
        return this.closed;
    }

    private boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    @Override
    public synchronized boolean isPoolPreparedStatements() {
        return this.poolPreparedStatements;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface != null && iface.isInstance(this);
    }

    private void jmxRegister() {
        if (this.registeredJmxObjectName != null) {
            return;
        }
        String requestedName = this.getJmxName();
        if (requestedName == null) {
            return;
        }
        this.registeredJmxObjectName = this.registerJmxObjectName(requestedName, null);
        try {
            StandardMBean standardMBean = new StandardMBean(this, DataSourceMXBean.class);
            this.registeredJmxObjectName.registerMBean(standardMBean);
        }
        catch (NotCompliantMBeanException e) {
            log.warn((Object)("The requested JMX name [" + requestedName + "] was not valid and will be ignored."));
        }
    }

    protected void log(String message) {
        if (this.logWriter != null) {
            this.logWriter.println(message);
        }
    }

    protected void log(String message, Throwable throwable) {
        if (this.logWriter != null) {
            this.logWriter.println(message);
            throwable.printStackTrace(this.logWriter);
        }
    }

    @Override
    public void postDeregister() {
    }

    @Override
    public void postRegister(Boolean registrationDone) {
    }

    @Override
    public void preDeregister() throws Exception {
    }

    @Override
    public ObjectName preRegister(MBeanServer server, ObjectName objectName) {
        this.registeredJmxObjectName = this.registerJmxObjectName(this.getJmxName(), objectName);
        return ObjectNameWrapper.unwrap(this.registeredJmxObjectName);
    }

    private ObjectNameWrapper registerJmxObjectName(String requestedName, ObjectName objectName) {
        ObjectNameWrapper objectNameWrapper = null;
        if (requestedName != null) {
            try {
                objectNameWrapper = ObjectNameWrapper.wrap(requestedName);
            }
            catch (MalformedObjectNameException e) {
                log.warn((Object)("The requested JMX name '" + requestedName + "' was not valid and will be ignored."));
            }
        }
        if (objectNameWrapper == null) {
            objectNameWrapper = ObjectNameWrapper.wrap(objectName);
        }
        return objectNameWrapper;
    }

    public void removeConnectionProperty(String name) {
        this.connectionProperties.remove(name);
    }

    @Override
    public synchronized void restart() throws SQLException {
        this.close();
        this.start();
    }

    private <T> void setAbandoned(BiConsumer<AbandonedConfig, T> consumer, T object) {
        if (this.abandonedConfig == null) {
            this.abandonedConfig = new AbandonedConfig();
        }
        consumer.accept(this.abandonedConfig, (AbandonedConfig)object);
        GenericObjectPool<PoolableConnection> gop = this.connectionPool;
        if (gop != null) {
            gop.setAbandonedConfig(this.abandonedConfig);
        }
    }

    private <T> void setConnectionPool(BiConsumer<GenericObjectPool<PoolableConnection>, T> consumer, T object) {
        if (this.connectionPool != null) {
            consumer.accept(this.connectionPool, (GenericObjectPool<PoolableConnection>)object);
        }
    }

    public void setAbandonedLogWriter(PrintWriter logWriter) {
        this.setAbandoned(AbandonedConfig::setLogWriter, logWriter);
    }

    public void setAbandonedUsageTracking(boolean usageTracking) {
        this.setAbandoned(AbandonedConfig::setUseUsageTracking, usageTracking);
    }

    public synchronized void setAccessToUnderlyingConnectionAllowed(boolean allow) {
        this.accessToUnderlyingConnectionAllowed = allow;
    }

    public void setAutoCommitOnReturn(boolean autoCommitOnReturn) {
        this.autoCommitOnReturn = autoCommitOnReturn;
    }

    public void setCacheState(boolean cacheState) {
        this.cacheState = cacheState;
    }

    public void setClearStatementPoolOnReturn(boolean clearStatementPoolOnReturn) {
        this.clearStatementPoolOnReturn = clearStatementPoolOnReturn;
    }

    public void setConnectionFactoryClassName(String connectionFactoryClassName) {
        this.connectionFactoryClassName = this.isEmpty(connectionFactoryClassName) ? null : connectionFactoryClassName;
    }

    public void setConnectionInitSqls(Collection<String> connectionInitSqls) {
        List collect = Utils.isEmpty(connectionInitSqls) ? null : connectionInitSqls.stream().filter(s -> !this.isEmpty((String)s)).collect(Collectors.toList());
        this.connectionInitSqls = Utils.isEmpty(collect) ? null : collect;
    }

    public void setConnectionProperties(String connectionProperties) {
        Objects.requireNonNull(connectionProperties, "connectionProperties");
        String[] entries = connectionProperties.split(";");
        Properties properties = new Properties();
        Stream.of(entries).filter(e -> !e.isEmpty()).forEach(entry -> {
            int index = entry.indexOf(61);
            if (index > 0) {
                String name = entry.substring(0, index);
                String value = entry.substring(index + 1);
                properties.setProperty(name, value);
            } else {
                properties.setProperty((String)entry, "");
            }
        });
        this.connectionProperties = properties;
    }

    public void setDefaultAutoCommit(Boolean defaultAutoCommit) {
        this.defaultAutoCommit = defaultAutoCommit;
    }

    public void setDefaultCatalog(String defaultCatalog) {
        this.defaultCatalog = this.isEmpty(defaultCatalog) ? null : defaultCatalog;
    }

    public void setDefaultQueryTimeout(Duration defaultQueryTimeoutDuration) {
        this.defaultQueryTimeoutDuration = defaultQueryTimeoutDuration;
    }

    @Deprecated
    public void setDefaultQueryTimeout(Integer defaultQueryTimeoutSeconds) {
        this.defaultQueryTimeoutDuration = defaultQueryTimeoutSeconds == null ? null : Duration.ofSeconds(defaultQueryTimeoutSeconds.longValue());
    }

    public void setDefaultReadOnly(Boolean defaultReadOnly) {
        this.defaultReadOnly = defaultReadOnly;
    }

    public void setDefaultSchema(String defaultSchema) {
        this.defaultSchema = this.isEmpty(defaultSchema) ? null : defaultSchema;
    }

    public void setDefaultTransactionIsolation(int defaultTransactionIsolation) {
        this.defaultTransactionIsolation = defaultTransactionIsolation;
    }

    public void setDisconnectionSqlCodes(Collection<String> disconnectionSqlCodes) {
        Set collect = Utils.isEmpty(disconnectionSqlCodes) ? null : disconnectionSqlCodes.stream().filter(s -> !this.isEmpty((String)s)).collect(Collectors.toSet());
        this.disconnectionSqlCodes = Utils.isEmpty(collect) ? null : collect;
    }

    public synchronized void setDriver(Driver driver) {
        this.driver = driver;
    }

    public synchronized void setDriverClassLoader(ClassLoader driverClassLoader) {
        this.driverClassLoader = driverClassLoader;
    }

    public synchronized void setDriverClassName(String driverClassName) {
        this.driverClassName = this.isEmpty(driverClassName) ? null : driverClassName;
    }

    @Deprecated
    public void setEnableAutoCommitOnReturn(boolean autoCommitOnReturn) {
        this.autoCommitOnReturn = autoCommitOnReturn;
    }

    public synchronized void setEvictionPolicyClassName(String evictionPolicyClassName) {
        this.setConnectionPool(BaseGenericObjectPool::setEvictionPolicyClassName, evictionPolicyClassName);
        this.evictionPolicyClassName = evictionPolicyClassName;
    }

    public void setFastFailValidation(boolean fastFailValidation) {
        this.fastFailValidation = fastFailValidation;
    }

    public synchronized void setInitialSize(int initialSize) {
        this.initialSize = initialSize;
    }

    public void setJmxName(String jmxName) {
        this.jmxName = jmxName;
    }

    public void setRegisterConnectionMBean(boolean registerConnectionMBean) {
        this.registerConnectionMBean = registerConnectionMBean;
    }

    public synchronized void setLifo(boolean lifo) {
        this.lifo = lifo;
        this.setConnectionPool(BaseGenericObjectPool::setLifo, lifo);
    }

    public void setLogAbandoned(boolean logAbandoned) {
        this.setAbandoned(AbandonedConfig::setLogAbandoned, logAbandoned);
    }

    public void setLogExpiredConnections(boolean logExpiredConnections) {
        this.logExpiredConnections = logExpiredConnections;
    }

    @Override
    public void setLoginTimeout(int loginTimeout) throws SQLException {
        throw new UnsupportedOperationException("Not supported by BasicDataSource");
    }

    @Override
    public void setLogWriter(PrintWriter logWriter) throws SQLException {
        this.createDataSource().setLogWriter(logWriter);
        this.logWriter = logWriter;
    }

    public void setMaxConn(Duration maxConnDuration) {
        this.maxConnDuration = maxConnDuration;
    }

    @Deprecated
    public void setMaxConnLifetimeMillis(long maxConnLifetimeMillis) {
        this.maxConnDuration = Duration.ofMillis(maxConnLifetimeMillis);
    }

    public synchronized void setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
        this.setConnectionPool(GenericObjectPool::setMaxIdle, maxIdle);
    }

    public synchronized void setMaxOpenPreparedStatements(int maxOpenStatements) {
        this.maxOpenPreparedStatements = maxOpenStatements;
    }

    public synchronized void setMaxTotal(int maxTotal) {
        this.maxTotal = maxTotal;
        this.setConnectionPool(BaseGenericObjectPool::setMaxTotal, maxTotal);
    }

    public synchronized void setMaxWait(Duration maxWaitDuration) {
        this.maxWaitDuration = maxWaitDuration;
        this.setConnectionPool(BaseGenericObjectPool::setMaxWait, maxWaitDuration);
    }

    @Deprecated
    public synchronized void setMaxWaitMillis(long maxWaitMillis) {
        this.setMaxWait(Duration.ofMillis(maxWaitMillis));
    }

    public synchronized void setMinEvictableIdle(Duration minEvictableIdleDuration) {
        this.minEvictableIdleDuration = minEvictableIdleDuration;
        this.setConnectionPool(BaseGenericObjectPool::setMinEvictableIdleDuration, minEvictableIdleDuration);
    }

    @Deprecated
    public synchronized void setMinEvictableIdleTimeMillis(long minEvictableIdleTimeMillis) {
        this.setMinEvictableIdle(Duration.ofMillis(minEvictableIdleTimeMillis));
    }

    public synchronized void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
        this.setConnectionPool(GenericObjectPool::setMinIdle, minIdle);
    }

    public synchronized void setNumTestsPerEvictionRun(int numTestsPerEvictionRun) {
        this.numTestsPerEvictionRun = numTestsPerEvictionRun;
        this.setConnectionPool(BaseGenericObjectPool::setNumTestsPerEvictionRun, numTestsPerEvictionRun);
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public synchronized void setPoolPreparedStatements(boolean poolingStatements) {
        this.poolPreparedStatements = poolingStatements;
    }

    public void setRemoveAbandonedOnBorrow(boolean removeAbandonedOnBorrow) {
        this.setAbandoned(AbandonedConfig::setRemoveAbandonedOnBorrow, removeAbandonedOnBorrow);
    }

    public void setRemoveAbandonedOnMaintenance(boolean removeAbandonedOnMaintenance) {
        this.setAbandoned(AbandonedConfig::setRemoveAbandonedOnMaintenance, removeAbandonedOnMaintenance);
    }

    public void setRemoveAbandonedTimeout(Duration removeAbandonedTimeout) {
        this.setAbandoned(AbandonedConfig::setRemoveAbandonedTimeout, removeAbandonedTimeout);
    }

    @Deprecated
    public void setRemoveAbandonedTimeout(int removeAbandonedTimeout) {
        this.setAbandoned(AbandonedConfig::setRemoveAbandonedTimeout, Duration.ofSeconds(removeAbandonedTimeout));
    }

    public void setRollbackOnReturn(boolean rollbackOnReturn) {
        this.rollbackOnReturn = rollbackOnReturn;
    }

    public synchronized void setSoftMinEvictableIdle(Duration softMinEvictableIdleTimeMillis) {
        this.softMinEvictableIdleDuration = softMinEvictableIdleTimeMillis;
        this.setConnectionPool(BaseGenericObjectPool::setSoftMinEvictableIdleDuration, softMinEvictableIdleTimeMillis);
    }

    @Deprecated
    public synchronized void setSoftMinEvictableIdleTimeMillis(long softMinEvictableIdleTimeMillis) {
        this.setSoftMinEvictableIdle(Duration.ofMillis(softMinEvictableIdleTimeMillis));
    }

    public synchronized void setTestOnBorrow(boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
        this.setConnectionPool(BaseGenericObjectPool::setTestOnBorrow, testOnBorrow);
    }

    public synchronized void setTestOnCreate(boolean testOnCreate) {
        this.testOnCreate = testOnCreate;
        this.setConnectionPool(BaseGenericObjectPool::setTestOnCreate, testOnCreate);
    }

    public synchronized void setTestOnReturn(boolean testOnReturn) {
        this.testOnReturn = testOnReturn;
        this.setConnectionPool(BaseGenericObjectPool::setTestOnReturn, testOnReturn);
    }

    public synchronized void setTestWhileIdle(boolean testWhileIdle) {
        this.testWhileIdle = testWhileIdle;
        this.setConnectionPool(BaseGenericObjectPool::setTestWhileIdle, testWhileIdle);
    }

    public synchronized void setDurationBetweenEvictionRuns(Duration timeBetweenEvictionRunsMillis) {
        this.durationBetweenEvictionRuns = timeBetweenEvictionRunsMillis;
        this.setConnectionPool(BaseGenericObjectPool::setDurationBetweenEvictionRuns, timeBetweenEvictionRunsMillis);
    }

    @Deprecated
    public synchronized void setTimeBetweenEvictionRunsMillis(long timeBetweenEvictionRunsMillis) {
        this.setDurationBetweenEvictionRuns(Duration.ofMillis(timeBetweenEvictionRunsMillis));
    }

    public synchronized void setUrl(String connectionString) {
        this.connectionString = connectionString;
    }

    public void setUsername(String userName) {
        this.userName = userName;
    }

    public void setValidationQuery(String validationQuery) {
        this.validationQuery = this.isEmpty(validationQuery) ? null : validationQuery;
    }

    public void setValidationQueryTimeout(Duration validationQueryTimeoutDuration) {
        this.validationQueryTimeoutDuration = validationQueryTimeoutDuration;
    }

    @Deprecated
    public void setValidationQueryTimeout(int validationQueryTimeoutSeconds) {
        this.validationQueryTimeoutDuration = Duration.ofSeconds(validationQueryTimeoutSeconds);
    }

    @Override
    public synchronized void start() throws SQLException {
        this.closed = false;
        this.createDataSource();
    }

    protected void startPoolMaintenance() {
        if (this.connectionPool != null && this.durationBetweenEvictionRuns.compareTo(Duration.ZERO) > 0) {
            this.connectionPool.setDurationBetweenEvictionRuns(this.durationBetweenEvictionRuns);
        }
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (this.isWrapperFor(iface)) {
            return iface.cast(this);
        }
        throw new SQLException(this + " is not a wrapper for " + iface);
    }

    private void updateJmxName(GenericObjectPoolConfig<?> config) {
        if (this.registeredJmxObjectName == null) {
            return;
        }
        StringBuilder base = new StringBuilder(this.registeredJmxObjectName.toString());
        base.append(",connectionpool=");
        config.setJmxNameBase(base.toString());
        config.setJmxNamePrefix("connections");
    }

    static {
        DriverManager.getDrivers();
        try {
            if (Utils.isSecurityEnabled()) {
                ClassLoader loader = BasicDataSource.class.getClassLoader();
                String dbcpPackageName = BasicDataSource.class.getPackage().getName();
                loader.loadClass(dbcpPackageName + ".DelegatingCallableStatement");
                loader.loadClass(dbcpPackageName + ".DelegatingDatabaseMetaData");
                loader.loadClass(dbcpPackageName + ".DelegatingPreparedStatement");
                loader.loadClass(dbcpPackageName + ".DelegatingResultSet");
                loader.loadClass(dbcpPackageName + ".PoolableCallableStatement");
                loader.loadClass(dbcpPackageName + ".PoolablePreparedStatement");
                loader.loadClass(dbcpPackageName + ".PoolingConnection$StatementType");
                loader.loadClass(dbcpPackageName + ".PStmtKey");
                String poolPackageName = PooledObject.class.getPackage().getName();
                loader.loadClass(poolPackageName + ".impl.LinkedBlockingDeque$Node");
                loader.loadClass(poolPackageName + ".impl.GenericKeyedObjectPool$ObjectDeque");
            }
        }
        catch (ClassNotFoundException cnfe) {
            throw new IllegalStateException("Unable to pre-load classes", cnfe);
        }
    }
}

