/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.tomcat.jdbc.pool;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.concurrent.Future;
import java.util.logging.Logger;
import javax.sql.PooledConnection;
import javax.sql.XAConnection;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.jdbc.pool.ConnectionPool;
import org.apache.tomcat.jdbc.pool.DataSourceFactory;
import org.apache.tomcat.jdbc.pool.PoolConfiguration;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.apache.tomcat.jdbc.pool.Validator;

public class DataSourceProxy
implements PoolConfiguration {
    private static final Log log = LogFactory.getLog(DataSourceProxy.class);
    protected volatile ConnectionPool pool = null;
    protected volatile PoolConfiguration poolProperties = null;

    public DataSourceProxy() {
        this(new PoolProperties());
    }

    public DataSourceProxy(PoolConfiguration poolProperties) {
        if (poolProperties == null) {
            throw new NullPointerException("PoolConfiguration cannot be null.");
        }
        this.poolProperties = poolProperties;
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    public Connection getConnection(String username, String password) throws SQLException {
        if (this.getPoolProperties().isAlternateUsernameAllowed()) {
            if (this.pool == null) {
                return this.createPool().getConnection(username, password);
            }
            return this.pool.getConnection(username, password);
        }
        return this.getConnection();
    }

    public PoolConfiguration getPoolProperties() {
        return this.poolProperties;
    }

    public ConnectionPool createPool() throws SQLException {
        if (this.pool != null) {
            return this.pool;
        }
        return this.pCreatePool();
    }

    private synchronized ConnectionPool pCreatePool() throws SQLException {
        if (this.pool != null) {
            return this.pool;
        }
        this.pool = new ConnectionPool(this.poolProperties);
        return this.pool;
    }

    public Connection getConnection() throws SQLException {
        if (this.pool == null) {
            return this.createPool().getConnection();
        }
        return this.pool.getConnection();
    }

    public Future<Connection> getConnectionAsync() throws SQLException {
        if (this.pool == null) {
            return this.createPool().getConnectionAsync();
        }
        return this.pool.getConnectionAsync();
    }

    public XAConnection getXAConnection() throws SQLException {
        Connection con = this.getConnection();
        if (con instanceof XAConnection) {
            return (XAConnection)((Object)con);
        }
        try {
            con.close();
        }
        catch (Exception exception) {
            // empty catch block
        }
        throw new SQLException("Connection from pool does not implement javax.sql.XAConnection");
    }

    public XAConnection getXAConnection(String username, String password) throws SQLException {
        Connection con = this.getConnection(username, password);
        if (con instanceof XAConnection) {
            return (XAConnection)((Object)con);
        }
        try {
            con.close();
        }
        catch (Exception exception) {
            // empty catch block
        }
        throw new SQLException("Connection from pool does not implement javax.sql.XAConnection");
    }

    public PooledConnection getPooledConnection() throws SQLException {
        return (PooledConnection)((Object)this.getConnection());
    }

    public PooledConnection getPooledConnection(String username, String password) throws SQLException {
        return (PooledConnection)((Object)this.getConnection());
    }

    public ConnectionPool getPool() {
        try {
            return this.createPool();
        }
        catch (SQLException x) {
            log.error((Object)"Error during connection pool creation.", (Throwable)x);
            return null;
        }
    }

    public void close() {
        this.close(false);
    }

    public void close(boolean all) {
        try {
            if (this.pool != null) {
                ConnectionPool p = this.pool;
                this.pool = null;
                if (p != null) {
                    p.close(all);
                }
            }
        }
        catch (Exception x) {
            log.warn((Object)"Error during connection pool closure.", (Throwable)x);
        }
    }

    public int getPoolSize() {
        ConnectionPool p = this.pool;
        if (p == null) {
            return 0;
        }
        return p.getSize();
    }

    public String toString() {
        return super.toString() + "{" + this.getPoolProperties() + "}";
    }

    @Override
    public String getPoolName() {
        return this.pool.getName();
    }

    public void setPoolProperties(PoolConfiguration poolProperties) {
        this.poolProperties = poolProperties;
    }

    @Override
    public void setDriverClassName(String driverClassName) {
        this.poolProperties.setDriverClassName(driverClassName);
    }

    @Override
    public void setInitialSize(int initialSize) {
        this.poolProperties.setInitialSize(initialSize);
    }

    @Override
    public void setInitSQL(String initSQL) {
        this.poolProperties.setInitSQL(initSQL);
    }

    @Override
    public void setLogAbandoned(boolean logAbandoned) {
        this.poolProperties.setLogAbandoned(logAbandoned);
    }

    @Override
    public void setMaxActive(int maxActive) {
        this.poolProperties.setMaxActive(maxActive);
    }

    @Override
    public void setMaxIdle(int maxIdle) {
        this.poolProperties.setMaxIdle(maxIdle);
    }

    @Override
    public void setMaxWait(int maxWait) {
        this.poolProperties.setMaxWait(maxWait);
    }

    @Override
    public void setMinEvictableIdleTimeMillis(int minEvictableIdleTimeMillis) {
        this.poolProperties.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
    }

    @Override
    public void setMinIdle(int minIdle) {
        this.poolProperties.setMinIdle(minIdle);
    }

    @Override
    public void setNumTestsPerEvictionRun(int numTestsPerEvictionRun) {
        this.poolProperties.setNumTestsPerEvictionRun(numTestsPerEvictionRun);
    }

    @Override
    public void setPassword(String password) {
        this.poolProperties.setPassword(password);
        this.poolProperties.getDbProperties().setProperty("password", this.poolProperties.getPassword());
    }

    @Override
    public void setRemoveAbandoned(boolean removeAbandoned) {
        this.poolProperties.setRemoveAbandoned(removeAbandoned);
    }

    @Override
    public void setRemoveAbandonedTimeout(int removeAbandonedTimeout) {
        this.poolProperties.setRemoveAbandonedTimeout(removeAbandonedTimeout);
    }

    @Override
    public void setTestOnBorrow(boolean testOnBorrow) {
        this.poolProperties.setTestOnBorrow(testOnBorrow);
    }

    @Override
    public void setTestOnConnect(boolean testOnConnect) {
        this.poolProperties.setTestOnConnect(testOnConnect);
    }

    @Override
    public void setTestOnReturn(boolean testOnReturn) {
        this.poolProperties.setTestOnReturn(testOnReturn);
    }

    @Override
    public void setTestWhileIdle(boolean testWhileIdle) {
        this.poolProperties.setTestWhileIdle(testWhileIdle);
    }

    @Override
    public void setTimeBetweenEvictionRunsMillis(int timeBetweenEvictionRunsMillis) {
        this.poolProperties.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
    }

    @Override
    public void setUrl(String url) {
        this.poolProperties.setUrl(url);
    }

    @Override
    public void setUsername(String username) {
        this.poolProperties.setUsername(username);
        this.poolProperties.getDbProperties().setProperty("user", this.getPoolProperties().getUsername());
    }

    @Override
    public void setValidationInterval(long validationInterval) {
        this.poolProperties.setValidationInterval(validationInterval);
    }

    @Override
    public void setValidationQuery(String validationQuery) {
        this.poolProperties.setValidationQuery(validationQuery);
    }

    @Override
    public void setValidatorClassName(String className) {
        this.poolProperties.setValidatorClassName(className);
    }

    @Override
    public void setValidationQueryTimeout(int validationQueryTimeout) {
        this.poolProperties.setValidationQueryTimeout(validationQueryTimeout);
    }

    @Override
    public void setJdbcInterceptors(String interceptors) {
        this.getPoolProperties().setJdbcInterceptors(interceptors);
    }

    @Override
    public void setJmxEnabled(boolean enabled) {
        this.getPoolProperties().setJmxEnabled(enabled);
    }

    @Override
    public void setFairQueue(boolean fairQueue) {
        this.getPoolProperties().setFairQueue(fairQueue);
    }

    @Override
    public void setUseLock(boolean useLock) {
        this.getPoolProperties().setUseLock(useLock);
    }

    @Override
    public void setDefaultCatalog(String catalog) {
        this.getPoolProperties().setDefaultCatalog(catalog);
    }

    @Override
    public void setDefaultAutoCommit(Boolean autocommit) {
        this.getPoolProperties().setDefaultAutoCommit(autocommit);
    }

    @Override
    public void setDefaultTransactionIsolation(int defaultTransactionIsolation) {
        this.getPoolProperties().setDefaultTransactionIsolation(defaultTransactionIsolation);
    }

    @Override
    public void setConnectionProperties(String properties) {
        try {
            Properties prop = DataSourceFactory.getProperties(properties);
            for (String string : prop.keySet()) {
                String value = prop.getProperty(string);
                this.getPoolProperties().getDbProperties().setProperty(string, value);
            }
        }
        catch (Exception x) {
            log.error((Object)"Unable to parse connection properties.", (Throwable)x);
            throw new RuntimeException(x);
        }
    }

    @Override
    public void setUseEquals(boolean useEquals) {
        this.getPoolProperties().setUseEquals(useEquals);
    }

    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException();
    }

    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    public void setLogWriter(PrintWriter out) throws SQLException {
    }

    public int getLoginTimeout() {
        if (this.poolProperties == null) {
            return 0;
        }
        return this.poolProperties.getMaxWait() / 1000;
    }

    public void setLoginTimeout(int i) {
        if (this.poolProperties == null) {
            return;
        }
        this.poolProperties.setMaxWait(1000 * i);
    }

    @Override
    public int getSuspectTimeout() {
        return this.getPoolProperties().getSuspectTimeout();
    }

    @Override
    public void setSuspectTimeout(int seconds) {
        this.getPoolProperties().setSuspectTimeout(seconds);
    }

    public int getIdle() {
        try {
            return this.createPool().getIdle();
        }
        catch (SQLException x) {
            throw new RuntimeException(x);
        }
    }

    public int getNumIdle() {
        return this.getIdle();
    }

    public void checkAbandoned() {
        try {
            this.createPool().checkAbandoned();
        }
        catch (SQLException x) {
            throw new RuntimeException(x);
        }
    }

    public void checkIdle() {
        try {
            this.createPool().checkIdle();
        }
        catch (SQLException x) {
            throw new RuntimeException(x);
        }
    }

    public int getActive() {
        try {
            return this.createPool().getActive();
        }
        catch (SQLException x) {
            throw new RuntimeException(x);
        }
    }

    public int getNumActive() {
        return this.getActive();
    }

    public int getWaitCount() {
        try {
            return this.createPool().getWaitCount();
        }
        catch (SQLException x) {
            throw new RuntimeException(x);
        }
    }

    public int getSize() {
        try {
            return this.createPool().getSize();
        }
        catch (SQLException x) {
            throw new RuntimeException(x);
        }
    }

    public void testIdle() {
        try {
            this.createPool().testAllIdle();
        }
        catch (SQLException x) {
            throw new RuntimeException(x);
        }
    }

    public long getBorrowedCount() {
        try {
            return this.createPool().getBorrowedCount();
        }
        catch (SQLException x) {
            throw new RuntimeException(x);
        }
    }

    public long getReturnedCount() {
        try {
            return this.createPool().getReturnedCount();
        }
        catch (SQLException x) {
            throw new RuntimeException(x);
        }
    }

    public long getCreatedCount() {
        try {
            return this.createPool().getCreatedCount();
        }
        catch (SQLException x) {
            throw new RuntimeException(x);
        }
    }

    public long getReleasedCount() {
        try {
            return this.createPool().getReleasedCount();
        }
        catch (SQLException x) {
            throw new RuntimeException(x);
        }
    }

    public long getReconnectedCount() {
        try {
            return this.createPool().getReconnectedCount();
        }
        catch (SQLException x) {
            throw new RuntimeException(x);
        }
    }

    public long getRemoveAbandonedCount() {
        try {
            return this.createPool().getRemoveAbandonedCount();
        }
        catch (SQLException x) {
            throw new RuntimeException(x);
        }
    }

    public long getReleasedIdleCount() {
        try {
            return this.createPool().getReleasedIdleCount();
        }
        catch (SQLException x) {
            throw new RuntimeException(x);
        }
    }

    public void resetStats() {
        try {
            this.createPool().resetStats();
        }
        catch (SQLException x) {
            throw new RuntimeException(x);
        }
    }

    @Override
    public String getConnectionProperties() {
        return this.getPoolProperties().getConnectionProperties();
    }

    @Override
    public Properties getDbProperties() {
        return this.getPoolProperties().getDbProperties();
    }

    @Override
    public String getDefaultCatalog() {
        return this.getPoolProperties().getDefaultCatalog();
    }

    @Override
    public int getDefaultTransactionIsolation() {
        return this.getPoolProperties().getDefaultTransactionIsolation();
    }

    @Override
    public String getDriverClassName() {
        return this.getPoolProperties().getDriverClassName();
    }

    @Override
    public int getInitialSize() {
        return this.getPoolProperties().getInitialSize();
    }

    @Override
    public String getInitSQL() {
        return this.getPoolProperties().getInitSQL();
    }

    @Override
    public String getJdbcInterceptors() {
        return this.getPoolProperties().getJdbcInterceptors();
    }

    @Override
    public int getMaxActive() {
        return this.getPoolProperties().getMaxActive();
    }

    @Override
    public int getMaxIdle() {
        return this.getPoolProperties().getMaxIdle();
    }

    @Override
    public int getMaxWait() {
        return this.getPoolProperties().getMaxWait();
    }

    @Override
    public int getMinEvictableIdleTimeMillis() {
        return this.getPoolProperties().getMinEvictableIdleTimeMillis();
    }

    @Override
    public int getMinIdle() {
        return this.getPoolProperties().getMinIdle();
    }

    @Override
    public long getMaxAge() {
        return this.getPoolProperties().getMaxAge();
    }

    @Override
    public String getName() {
        return this.getPoolProperties().getName();
    }

    @Override
    public int getNumTestsPerEvictionRun() {
        return this.getPoolProperties().getNumTestsPerEvictionRun();
    }

    @Override
    public String getPassword() {
        return "Password not available as DataSource/JMX operation.";
    }

    @Override
    public int getRemoveAbandonedTimeout() {
        return this.getPoolProperties().getRemoveAbandonedTimeout();
    }

    @Override
    public int getTimeBetweenEvictionRunsMillis() {
        return this.getPoolProperties().getTimeBetweenEvictionRunsMillis();
    }

    @Override
    public String getUrl() {
        return this.getPoolProperties().getUrl();
    }

    @Override
    public String getUsername() {
        return this.getPoolProperties().getUsername();
    }

    @Override
    public long getValidationInterval() {
        return this.getPoolProperties().getValidationInterval();
    }

    @Override
    public String getValidationQuery() {
        return this.getPoolProperties().getValidationQuery();
    }

    @Override
    public int getValidationQueryTimeout() {
        return this.getPoolProperties().getValidationQueryTimeout();
    }

    @Override
    public String getValidatorClassName() {
        return this.getPoolProperties().getValidatorClassName();
    }

    @Override
    public Validator getValidator() {
        return this.getPoolProperties().getValidator();
    }

    @Override
    public void setValidator(Validator validator) {
        this.getPoolProperties().setValidator(validator);
    }

    @Override
    public boolean isAccessToUnderlyingConnectionAllowed() {
        return this.getPoolProperties().isAccessToUnderlyingConnectionAllowed();
    }

    @Override
    public Boolean isDefaultAutoCommit() {
        return this.getPoolProperties().isDefaultAutoCommit();
    }

    @Override
    public Boolean isDefaultReadOnly() {
        return this.getPoolProperties().isDefaultReadOnly();
    }

    @Override
    public boolean isLogAbandoned() {
        return this.getPoolProperties().isLogAbandoned();
    }

    @Override
    public boolean isPoolSweeperEnabled() {
        return this.getPoolProperties().isPoolSweeperEnabled();
    }

    @Override
    public boolean isRemoveAbandoned() {
        return this.getPoolProperties().isRemoveAbandoned();
    }

    @Override
    public int getAbandonWhenPercentageFull() {
        return this.getPoolProperties().getAbandonWhenPercentageFull();
    }

    @Override
    public boolean isTestOnBorrow() {
        return this.getPoolProperties().isTestOnBorrow();
    }

    @Override
    public boolean isTestOnConnect() {
        return this.getPoolProperties().isTestOnConnect();
    }

    @Override
    public boolean isTestOnReturn() {
        return this.getPoolProperties().isTestOnReturn();
    }

    @Override
    public boolean isTestWhileIdle() {
        return this.getPoolProperties().isTestWhileIdle();
    }

    @Override
    public Boolean getDefaultAutoCommit() {
        return this.getPoolProperties().getDefaultAutoCommit();
    }

    @Override
    public Boolean getDefaultReadOnly() {
        return this.getPoolProperties().getDefaultReadOnly();
    }

    @Override
    public PoolProperties.InterceptorDefinition[] getJdbcInterceptorsAsArray() {
        return this.getPoolProperties().getJdbcInterceptorsAsArray();
    }

    @Override
    public boolean getUseLock() {
        return this.getPoolProperties().getUseLock();
    }

    @Override
    public boolean isFairQueue() {
        return this.getPoolProperties().isFairQueue();
    }

    @Override
    public boolean isJmxEnabled() {
        return this.getPoolProperties().isJmxEnabled();
    }

    @Override
    public boolean isUseEquals() {
        return this.getPoolProperties().isUseEquals();
    }

    @Override
    public void setAbandonWhenPercentageFull(int percentage) {
        this.getPoolProperties().setAbandonWhenPercentageFull(percentage);
    }

    @Override
    public void setAccessToUnderlyingConnectionAllowed(boolean accessToUnderlyingConnectionAllowed) {
        this.getPoolProperties().setAccessToUnderlyingConnectionAllowed(accessToUnderlyingConnectionAllowed);
    }

    @Override
    public void setDbProperties(Properties dbProperties) {
        this.getPoolProperties().setDbProperties(dbProperties);
    }

    @Override
    public void setDefaultReadOnly(Boolean defaultReadOnly) {
        this.getPoolProperties().setDefaultReadOnly(defaultReadOnly);
    }

    @Override
    public void setMaxAge(long maxAge) {
        this.getPoolProperties().setMaxAge(maxAge);
    }

    @Override
    public void setName(String name) {
        this.getPoolProperties().setName(name);
    }

    @Override
    public void setDataSource(Object ds) {
        this.getPoolProperties().setDataSource(ds);
    }

    @Override
    public Object getDataSource() {
        return this.getPoolProperties().getDataSource();
    }

    @Override
    public void setDataSourceJNDI(String jndiDS) {
        this.getPoolProperties().setDataSourceJNDI(jndiDS);
    }

    @Override
    public String getDataSourceJNDI() {
        return this.getPoolProperties().getDataSourceJNDI();
    }

    @Override
    public boolean isAlternateUsernameAllowed() {
        return this.getPoolProperties().isAlternateUsernameAllowed();
    }

    @Override
    public void setAlternateUsernameAllowed(boolean alternateUsernameAllowed) {
        this.getPoolProperties().setAlternateUsernameAllowed(alternateUsernameAllowed);
    }

    @Override
    public void setCommitOnReturn(boolean commitOnReturn) {
        this.getPoolProperties().setCommitOnReturn(commitOnReturn);
    }

    @Override
    public boolean getCommitOnReturn() {
        return this.getPoolProperties().getCommitOnReturn();
    }

    @Override
    public void setRollbackOnReturn(boolean rollbackOnReturn) {
        this.getPoolProperties().setRollbackOnReturn(rollbackOnReturn);
    }

    @Override
    public boolean getRollbackOnReturn() {
        return this.getPoolProperties().getRollbackOnReturn();
    }

    @Override
    public void setUseDisposableConnectionFacade(boolean useDisposableConnectionFacade) {
        this.getPoolProperties().setUseDisposableConnectionFacade(useDisposableConnectionFacade);
    }

    @Override
    public boolean getUseDisposableConnectionFacade() {
        return this.getPoolProperties().getUseDisposableConnectionFacade();
    }

    @Override
    public void setLogValidationErrors(boolean logValidationErrors) {
        this.getPoolProperties().setLogValidationErrors(logValidationErrors);
    }

    @Override
    public boolean getLogValidationErrors() {
        return this.getPoolProperties().getLogValidationErrors();
    }

    @Override
    public boolean getPropagateInterruptState() {
        return this.getPoolProperties().getPropagateInterruptState();
    }

    @Override
    public void setPropagateInterruptState(boolean propagateInterruptState) {
        this.getPoolProperties().setPropagateInterruptState(propagateInterruptState);
    }

    @Override
    public boolean isIgnoreExceptionOnPreLoad() {
        return this.getPoolProperties().isIgnoreExceptionOnPreLoad();
    }

    @Override
    public void setIgnoreExceptionOnPreLoad(boolean ignoreExceptionOnPreLoad) {
        this.getPoolProperties().setIgnoreExceptionOnPreLoad(ignoreExceptionOnPreLoad);
    }

    @Override
    public boolean getUseStatementFacade() {
        return this.getPoolProperties().getUseStatementFacade();
    }

    @Override
    public void setUseStatementFacade(boolean useStatementFacade) {
        this.getPoolProperties().setUseStatementFacade(useStatementFacade);
    }

    public void purge() {
        try {
            this.createPool().purge();
        }
        catch (SQLException x) {
            log.error((Object)"Unable to purge pool.", (Throwable)x);
        }
    }

    public void purgeOnReturn() {
        try {
            this.createPool().purgeOnReturn();
        }
        catch (SQLException x) {
            log.error((Object)"Unable to purge pool.", (Throwable)x);
        }
    }
}

