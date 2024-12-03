/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.dbcp.dbcp2.datasources;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.time.Duration;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.Referenceable;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.DataSource;
import javax.sql.PooledConnection;
import org.apache.tomcat.dbcp.dbcp2.Utils;
import org.apache.tomcat.dbcp.dbcp2.datasources.InstanceKeyDataSourceFactory;
import org.apache.tomcat.dbcp.dbcp2.datasources.PooledConnectionAndInfo;
import org.apache.tomcat.dbcp.dbcp2.datasources.PooledConnectionManager;
import org.apache.tomcat.dbcp.dbcp2.datasources.UserPassKey;
import org.apache.tomcat.dbcp.pool2.impl.BaseObjectPoolConfig;

public abstract class InstanceKeyDataSource
implements DataSource,
Referenceable,
Serializable,
AutoCloseable {
    private static final long serialVersionUID = -6819270431752240878L;
    private static final String GET_CONNECTION_CALLED = "A Connection was already requested from this source, further initialization is not allowed.";
    private static final String BAD_TRANSACTION_ISOLATION = "The requested TransactionIsolation level is invalid.";
    protected static final int UNKNOWN_TRANSACTIONISOLATION = -1;
    private volatile boolean getConnectionCalled;
    private ConnectionPoolDataSource dataSource;
    private String dataSourceName;
    private String description;
    private Properties jndiEnvironment;
    private Duration loginTimeoutDuration = Duration.ZERO;
    private PrintWriter logWriter;
    private String instanceKey;
    private boolean defaultBlockWhenExhausted = true;
    private String defaultEvictionPolicyClassName = BaseObjectPoolConfig.DEFAULT_EVICTION_POLICY_CLASS_NAME;
    private boolean defaultLifo = true;
    private int defaultMaxIdle = 8;
    private int defaultMaxTotal = -1;
    private Duration defaultMaxWaitDuration = BaseObjectPoolConfig.DEFAULT_MAX_WAIT;
    private Duration defaultMinEvictableIdleDuration = BaseObjectPoolConfig.DEFAULT_MIN_EVICTABLE_IDLE_DURATION;
    private int defaultMinIdle = 0;
    private int defaultNumTestsPerEvictionRun = 3;
    private Duration defaultSoftMinEvictableIdleDuration = BaseObjectPoolConfig.DEFAULT_SOFT_MIN_EVICTABLE_IDLE_DURATION;
    private boolean defaultTestOnCreate = false;
    private boolean defaultTestOnBorrow = false;
    private boolean defaultTestOnReturn = false;
    private boolean defaultTestWhileIdle = false;
    private Duration defaultDurationBetweenEvictionRuns = BaseObjectPoolConfig.DEFAULT_DURATION_BETWEEN_EVICTION_RUNS;
    private String validationQuery;
    private Duration validationQueryTimeoutDuration = Duration.ofSeconds(-1L);
    private boolean rollbackAfterValidation;
    private Duration maxConnDuration = Duration.ofMillis(-1L);
    private Boolean defaultAutoCommit;
    private int defaultTransactionIsolation = -1;
    private Boolean defaultReadOnly;

    protected void assertInitializationAllowed() throws IllegalStateException {
        if (this.getConnectionCalled) {
            throw new IllegalStateException(GET_CONNECTION_CALLED);
        }
    }

    @Override
    public abstract void close() throws SQLException;

    private void closeDueToException(PooledConnectionAndInfo info) {
        if (info != null) {
            try {
                info.getPooledConnection().getConnection().close();
            }
            catch (Exception e) {
                this.getLogWriter().println("[ERROR] Could not return connection to pool during exception handling. " + e.getMessage());
            }
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        return this.getConnection(null, null);
    }

    @Override
    public Connection getConnection(String userName, String userPassword) throws SQLException {
        if (this.instanceKey == null) {
            throw new SQLException("Must set the ConnectionPoolDataSource through setDataSourceName or setConnectionPoolDataSource before calling getConnection.");
        }
        this.getConnectionCalled = true;
        PooledConnectionAndInfo info = null;
        try {
            info = this.getPooledConnectionAndInfo(userName, userPassword);
        }
        catch (RuntimeException | SQLException e2) {
            this.closeDueToException(info);
            throw e2;
        }
        catch (Exception e3) {
            this.closeDueToException(info);
            throw new SQLException("Cannot borrow connection from pool", e3);
        }
        if (!(null != userPassword ? userPassword.equals(info.getPassword()) : null == info.getPassword())) {
            try {
                this.testCPDS(userName, userPassword);
            }
            catch (SQLException ex) {
                this.closeDueToException(info);
                throw new SQLException("Given password did not match password used to create the PooledConnection.", ex);
            }
            catch (NamingException ne) {
                throw new SQLException("NamingException encountered connecting to database", ne);
            }
            UserPassKey upkey = info.getUserPassKey();
            PooledConnectionManager manager = this.getConnectionManager(upkey);
            manager.invalidate(info.getPooledConnection());
            manager.setPassword(upkey.getPassword());
            info = null;
            for (int i = 0; i < 10; ++i) {
                try {
                    info = this.getPooledConnectionAndInfo(userName, userPassword);
                }
                catch (RuntimeException | SQLException e4) {
                    this.closeDueToException(info);
                    throw e4;
                }
                catch (Exception e5) {
                    this.closeDueToException(info);
                    throw new SQLException("Cannot borrow connection from pool", e5);
                }
                if (info != null && userPassword != null && userPassword.equals(info.getPassword())) break;
                if (info != null) {
                    manager.invalidate(info.getPooledConnection());
                }
                info = null;
            }
            if (info == null) {
                throw new SQLException("Cannot borrow connection from pool - password change failure.");
            }
        }
        Connection connection = info.getPooledConnection().getConnection();
        try {
            this.setupDefaults(connection, userName);
            connection.clearWarnings();
            return connection;
        }
        catch (SQLException ex) {
            Utils.close(connection, e -> this.getLogWriter().println("ignoring exception during close: " + e));
            throw ex;
        }
    }

    protected abstract PooledConnectionManager getConnectionManager(UserPassKey var1);

    public ConnectionPoolDataSource getConnectionPoolDataSource() {
        return this.dataSource;
    }

    public String getDataSourceName() {
        return this.dataSourceName;
    }

    public boolean getDefaultBlockWhenExhausted() {
        return this.defaultBlockWhenExhausted;
    }

    public Duration getDefaultDurationBetweenEvictionRuns() {
        return this.defaultDurationBetweenEvictionRuns;
    }

    public String getDefaultEvictionPolicyClassName() {
        return this.defaultEvictionPolicyClassName;
    }

    public boolean getDefaultLifo() {
        return this.defaultLifo;
    }

    public int getDefaultMaxIdle() {
        return this.defaultMaxIdle;
    }

    public int getDefaultMaxTotal() {
        return this.defaultMaxTotal;
    }

    public Duration getDefaultMaxWait() {
        return this.defaultMaxWaitDuration;
    }

    @Deprecated
    public long getDefaultMaxWaitMillis() {
        return this.getDefaultMaxWait().toMillis();
    }

    public Duration getDefaultMinEvictableIdleDuration() {
        return this.defaultMinEvictableIdleDuration;
    }

    @Deprecated
    public long getDefaultMinEvictableIdleTimeMillis() {
        return this.defaultMinEvictableIdleDuration.toMillis();
    }

    public int getDefaultMinIdle() {
        return this.defaultMinIdle;
    }

    public int getDefaultNumTestsPerEvictionRun() {
        return this.defaultNumTestsPerEvictionRun;
    }

    public Duration getDefaultSoftMinEvictableIdleDuration() {
        return this.defaultSoftMinEvictableIdleDuration;
    }

    @Deprecated
    public long getDefaultSoftMinEvictableIdleTimeMillis() {
        return this.defaultSoftMinEvictableIdleDuration.toMillis();
    }

    public boolean getDefaultTestOnBorrow() {
        return this.defaultTestOnBorrow;
    }

    public boolean getDefaultTestOnCreate() {
        return this.defaultTestOnCreate;
    }

    public boolean getDefaultTestOnReturn() {
        return this.defaultTestOnReturn;
    }

    public boolean getDefaultTestWhileIdle() {
        return this.defaultTestWhileIdle;
    }

    @Deprecated
    public long getDefaultTimeBetweenEvictionRunsMillis() {
        return this.defaultDurationBetweenEvictionRuns.toMillis();
    }

    public int getDefaultTransactionIsolation() {
        return this.defaultTransactionIsolation;
    }

    public String getDescription() {
        return this.description;
    }

    protected String getInstanceKey() {
        return this.instanceKey;
    }

    public String getJndiEnvironment(String key) {
        String value = null;
        if (this.jndiEnvironment != null) {
            value = this.jndiEnvironment.getProperty(key);
        }
        return value;
    }

    @Override
    @Deprecated
    public int getLoginTimeout() {
        return (int)this.loginTimeoutDuration.getSeconds();
    }

    public Duration getLoginTimeoutDuration() {
        return this.loginTimeoutDuration;
    }

    @Override
    public PrintWriter getLogWriter() {
        if (this.logWriter == null) {
            this.logWriter = new PrintWriter(new OutputStreamWriter((OutputStream)System.out, StandardCharsets.UTF_8));
        }
        return this.logWriter;
    }

    public Duration getMaxConnDuration() {
        return this.maxConnDuration;
    }

    @Deprecated
    public Duration getMaxConnLifetime() {
        return this.maxConnDuration;
    }

    @Deprecated
    public long getMaxConnLifetimeMillis() {
        return this.maxConnDuration.toMillis();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException();
    }

    protected abstract PooledConnectionAndInfo getPooledConnectionAndInfo(String var1, String var2) throws SQLException;

    public String getValidationQuery() {
        return this.validationQuery;
    }

    @Deprecated
    public int getValidationQueryTimeout() {
        return (int)this.validationQueryTimeoutDuration.getSeconds();
    }

    public Duration getValidationQueryTimeoutDuration() {
        return this.validationQueryTimeoutDuration;
    }

    public Boolean isDefaultAutoCommit() {
        return this.defaultAutoCommit;
    }

    public Boolean isDefaultReadOnly() {
        return this.defaultReadOnly;
    }

    public boolean isRollbackAfterValidation() {
        return this.rollbackAfterValidation;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface.isInstance(this);
    }

    public void setConnectionPoolDataSource(ConnectionPoolDataSource dataSource) {
        this.assertInitializationAllowed();
        if (this.dataSourceName != null) {
            throw new IllegalStateException("Cannot set the DataSource, if JNDI is used.");
        }
        if (this.dataSource != null) {
            throw new IllegalStateException("The CPDS has already been set. It cannot be altered.");
        }
        this.dataSource = dataSource;
        this.instanceKey = InstanceKeyDataSourceFactory.registerNewInstance(this);
    }

    public void setDataSourceName(String dataSourceName) {
        this.assertInitializationAllowed();
        if (this.dataSource != null) {
            throw new IllegalStateException("Cannot set the JNDI name for the DataSource, if already set using setConnectionPoolDataSource.");
        }
        if (this.dataSourceName != null) {
            throw new IllegalStateException("The DataSourceName has already been set. It cannot be altered.");
        }
        this.dataSourceName = dataSourceName;
        this.instanceKey = InstanceKeyDataSourceFactory.registerNewInstance(this);
    }

    public void setDefaultAutoCommit(Boolean defaultAutoCommit) {
        this.assertInitializationAllowed();
        this.defaultAutoCommit = defaultAutoCommit;
    }

    public void setDefaultBlockWhenExhausted(boolean blockWhenExhausted) {
        this.assertInitializationAllowed();
        this.defaultBlockWhenExhausted = blockWhenExhausted;
    }

    public void setDefaultDurationBetweenEvictionRuns(Duration defaultDurationBetweenEvictionRuns) {
        this.assertInitializationAllowed();
        this.defaultDurationBetweenEvictionRuns = defaultDurationBetweenEvictionRuns;
    }

    public void setDefaultEvictionPolicyClassName(String evictionPolicyClassName) {
        this.assertInitializationAllowed();
        this.defaultEvictionPolicyClassName = evictionPolicyClassName;
    }

    public void setDefaultLifo(boolean lifo) {
        this.assertInitializationAllowed();
        this.defaultLifo = lifo;
    }

    public void setDefaultMaxIdle(int maxIdle) {
        this.assertInitializationAllowed();
        this.defaultMaxIdle = maxIdle;
    }

    public void setDefaultMaxTotal(int maxTotal) {
        this.assertInitializationAllowed();
        this.defaultMaxTotal = maxTotal;
    }

    public void setDefaultMaxWait(Duration maxWaitMillis) {
        this.assertInitializationAllowed();
        this.defaultMaxWaitDuration = maxWaitMillis;
    }

    @Deprecated
    public void setDefaultMaxWaitMillis(long maxWaitMillis) {
        this.setDefaultMaxWait(Duration.ofMillis(maxWaitMillis));
    }

    public void setDefaultMinEvictableIdle(Duration defaultMinEvictableIdleDuration) {
        this.assertInitializationAllowed();
        this.defaultMinEvictableIdleDuration = defaultMinEvictableIdleDuration;
    }

    @Deprecated
    public void setDefaultMinEvictableIdleTimeMillis(long minEvictableIdleTimeMillis) {
        this.assertInitializationAllowed();
        this.defaultMinEvictableIdleDuration = Duration.ofMillis(minEvictableIdleTimeMillis);
    }

    public void setDefaultMinIdle(int minIdle) {
        this.assertInitializationAllowed();
        this.defaultMinIdle = minIdle;
    }

    public void setDefaultNumTestsPerEvictionRun(int numTestsPerEvictionRun) {
        this.assertInitializationAllowed();
        this.defaultNumTestsPerEvictionRun = numTestsPerEvictionRun;
    }

    public void setDefaultReadOnly(Boolean defaultReadOnly) {
        this.assertInitializationAllowed();
        this.defaultReadOnly = defaultReadOnly;
    }

    public void setDefaultSoftMinEvictableIdle(Duration defaultSoftMinEvictableIdleDuration) {
        this.assertInitializationAllowed();
        this.defaultSoftMinEvictableIdleDuration = defaultSoftMinEvictableIdleDuration;
    }

    @Deprecated
    public void setDefaultSoftMinEvictableIdleTimeMillis(long softMinEvictableIdleTimeMillis) {
        this.assertInitializationAllowed();
        this.defaultSoftMinEvictableIdleDuration = Duration.ofMillis(softMinEvictableIdleTimeMillis);
    }

    public void setDefaultTestOnBorrow(boolean testOnBorrow) {
        this.assertInitializationAllowed();
        this.defaultTestOnBorrow = testOnBorrow;
    }

    public void setDefaultTestOnCreate(boolean testOnCreate) {
        this.assertInitializationAllowed();
        this.defaultTestOnCreate = testOnCreate;
    }

    public void setDefaultTestOnReturn(boolean testOnReturn) {
        this.assertInitializationAllowed();
        this.defaultTestOnReturn = testOnReturn;
    }

    public void setDefaultTestWhileIdle(boolean testWhileIdle) {
        this.assertInitializationAllowed();
        this.defaultTestWhileIdle = testWhileIdle;
    }

    @Deprecated
    public void setDefaultTimeBetweenEvictionRunsMillis(long timeBetweenEvictionRunsMillis) {
        this.assertInitializationAllowed();
        this.defaultDurationBetweenEvictionRuns = Duration.ofMillis(timeBetweenEvictionRunsMillis);
    }

    public void setDefaultTransactionIsolation(int defaultTransactionIsolation) {
        this.assertInitializationAllowed();
        switch (defaultTransactionIsolation) {
            case 0: 
            case 1: 
            case 2: 
            case 4: 
            case 8: {
                break;
            }
            default: {
                throw new IllegalArgumentException(BAD_TRANSACTION_ISOLATION);
            }
        }
        this.defaultTransactionIsolation = defaultTransactionIsolation;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    void setJndiEnvironment(Properties properties) {
        if (this.jndiEnvironment == null) {
            this.jndiEnvironment = new Properties();
        } else {
            this.jndiEnvironment.clear();
        }
        this.jndiEnvironment.putAll((Map<?, ?>)properties);
    }

    public void setJndiEnvironment(String key, String value) {
        if (this.jndiEnvironment == null) {
            this.jndiEnvironment = new Properties();
        }
        this.jndiEnvironment.setProperty(key, value);
    }

    public void setLoginTimeout(Duration loginTimeout) {
        this.loginTimeoutDuration = loginTimeout;
    }

    @Override
    @Deprecated
    public void setLoginTimeout(int loginTimeout) {
        this.loginTimeoutDuration = Duration.ofSeconds(loginTimeout);
    }

    @Override
    public void setLogWriter(PrintWriter logWriter) {
        this.logWriter = logWriter;
    }

    public void setMaxConnLifetime(Duration maxConnLifetimeMillis) {
        this.maxConnDuration = maxConnLifetimeMillis;
    }

    @Deprecated
    public void setMaxConnLifetimeMillis(long maxConnLifetimeMillis) {
        this.setMaxConnLifetime(Duration.ofMillis(maxConnLifetimeMillis));
    }

    public void setRollbackAfterValidation(boolean rollbackAfterValidation) {
        this.assertInitializationAllowed();
        this.rollbackAfterValidation = rollbackAfterValidation;
    }

    protected abstract void setupDefaults(Connection var1, String var2) throws SQLException;

    public void setValidationQuery(String validationQuery) {
        this.assertInitializationAllowed();
        this.validationQuery = validationQuery;
    }

    public void setValidationQueryTimeout(Duration validationQueryTimeoutDuration) {
        this.validationQueryTimeoutDuration = validationQueryTimeoutDuration;
    }

    @Deprecated
    public void setValidationQueryTimeout(int validationQueryTimeoutSeconds) {
        this.validationQueryTimeoutDuration = Duration.ofSeconds(validationQueryTimeoutSeconds);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected ConnectionPoolDataSource testCPDS(String userName, String userPassword) throws NamingException, SQLException {
        ConnectionPoolDataSource cpds = this.dataSource;
        if (cpds == null) {
            InitialContext ctx = null;
            ctx = this.jndiEnvironment == null ? new InitialContext() : new InitialContext(this.jndiEnvironment);
            Object ds = ctx.lookup(this.dataSourceName);
            if (!(ds instanceof ConnectionPoolDataSource)) {
                throw new SQLException("Illegal configuration: DataSource " + this.dataSourceName + " (" + ds.getClass().getName() + ") doesn't implement javax.sql.ConnectionPoolDataSource");
            }
            cpds = (ConnectionPoolDataSource)ds;
        }
        PooledConnection conn = null;
        try {
            conn = userName != null ? cpds.getPooledConnection(userName, userPassword) : cpds.getPooledConnection();
            if (conn == null) {
                throw new SQLException("Cannot connect using the supplied userName/password");
            }
        }
        finally {
            if (conn != null) {
                try {
                    conn.close();
                }
                catch (SQLException sQLException) {}
            }
        }
        return cpds;
    }

    public synchronized String toString() {
        StringBuilder builder = new StringBuilder(super.toString());
        builder.append('[');
        this.toStringFields(builder);
        builder.append(']');
        return builder.toString();
    }

    protected void toStringFields(StringBuilder builder) {
        builder.append("getConnectionCalled=");
        builder.append(this.getConnectionCalled);
        builder.append(", dataSource=");
        builder.append(this.dataSource);
        builder.append(", dataSourceName=");
        builder.append(this.dataSourceName);
        builder.append(", description=");
        builder.append(this.description);
        builder.append(", jndiEnvironment=");
        builder.append(this.jndiEnvironment);
        builder.append(", loginTimeoutDuration=");
        builder.append(this.loginTimeoutDuration);
        builder.append(", logWriter=");
        builder.append(this.logWriter);
        builder.append(", instanceKey=");
        builder.append(this.instanceKey);
        builder.append(", defaultBlockWhenExhausted=");
        builder.append(this.defaultBlockWhenExhausted);
        builder.append(", defaultEvictionPolicyClassName=");
        builder.append(this.defaultEvictionPolicyClassName);
        builder.append(", defaultLifo=");
        builder.append(this.defaultLifo);
        builder.append(", defaultMaxIdle=");
        builder.append(this.defaultMaxIdle);
        builder.append(", defaultMaxTotal=");
        builder.append(this.defaultMaxTotal);
        builder.append(", defaultMaxWaitDuration=");
        builder.append(this.defaultMaxWaitDuration);
        builder.append(", defaultMinEvictableIdleDuration=");
        builder.append(this.defaultMinEvictableIdleDuration);
        builder.append(", defaultMinIdle=");
        builder.append(this.defaultMinIdle);
        builder.append(", defaultNumTestsPerEvictionRun=");
        builder.append(this.defaultNumTestsPerEvictionRun);
        builder.append(", defaultSoftMinEvictableIdleDuration=");
        builder.append(this.defaultSoftMinEvictableIdleDuration);
        builder.append(", defaultTestOnCreate=");
        builder.append(this.defaultTestOnCreate);
        builder.append(", defaultTestOnBorrow=");
        builder.append(this.defaultTestOnBorrow);
        builder.append(", defaultTestOnReturn=");
        builder.append(this.defaultTestOnReturn);
        builder.append(", defaultTestWhileIdle=");
        builder.append(this.defaultTestWhileIdle);
        builder.append(", defaultDurationBetweenEvictionRuns=");
        builder.append(this.defaultDurationBetweenEvictionRuns);
        builder.append(", validationQuery=");
        builder.append(this.validationQuery);
        builder.append(", validationQueryTimeoutDuration=");
        builder.append(this.validationQueryTimeoutDuration);
        builder.append(", rollbackAfterValidation=");
        builder.append(this.rollbackAfterValidation);
        builder.append(", maxConnDuration=");
        builder.append(this.maxConnDuration);
        builder.append(", defaultAutoCommit=");
        builder.append(this.defaultAutoCommit);
        builder.append(", defaultTransactionIsolation=");
        builder.append(this.defaultTransactionIsolation);
        builder.append(", defaultReadOnly=");
        builder.append(this.defaultReadOnly);
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (this.isWrapperFor(iface)) {
            return (T)this;
        }
        throw new SQLException(this + " is not a wrapper for " + iface);
    }
}

