/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.tomcat.jdbc.pool;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.management.ObjectName;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.DataSource;
import javax.sql.XAConnection;
import javax.sql.XADataSource;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.jdbc.pool.ClassLoaderUtil;
import org.apache.tomcat.jdbc.pool.ConnectionPool;
import org.apache.tomcat.jdbc.pool.JdbcInterceptor;
import org.apache.tomcat.jdbc.pool.PoolConfiguration;
import org.apache.tomcat.jdbc.pool.PoolUtilities;
import org.apache.tomcat.jdbc.pool.PooledConnectionMBean;
import org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;
import org.apache.tomcat.jdbc.pool.jmx.JmxUtil;

public class PooledConnection
implements PooledConnectionMBean {
    private static final Log log = LogFactory.getLog(PooledConnection.class);
    public static final String PROP_USER = "user";
    public static final String PROP_PASSWORD = "password";
    public static final int VALIDATE_BORROW = 1;
    public static final int VALIDATE_RETURN = 2;
    public static final int VALIDATE_IDLE = 3;
    public static final int VALIDATE_INIT = 4;
    protected PoolConfiguration poolProperties;
    private volatile Connection connection;
    protected volatile XAConnection xaConnection;
    private String abandonTrace = null;
    private volatile long timestamp;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(false);
    private volatile boolean discarded = false;
    private volatile long lastConnected = -1L;
    private volatile long lastValidated = System.currentTimeMillis();
    protected ConnectionPool parent;
    private HashMap<Object, Object> attributes = new HashMap();
    private volatile long connectionVersion = 0L;
    private static final AtomicLong connectionIndex = new AtomicLong(0L);
    private ObjectName oname = null;
    private volatile JdbcInterceptor handler = null;
    private AtomicBoolean released = new AtomicBoolean(false);
    private volatile boolean suspect = false;
    private Driver driver = null;

    public PooledConnection(PoolConfiguration prop, ConnectionPool parent) {
        this.poolProperties = prop;
        this.parent = parent;
        this.connectionVersion = parent.getPoolVersion();
    }

    @Override
    public long getConnectionVersion() {
        return this.connectionVersion;
    }

    @Deprecated
    public boolean checkUser(String username, String password) {
        return !this.shouldForceReconnect(username, password);
    }

    public boolean shouldForceReconnect(String username, String password) {
        if (!this.getPoolProperties().isAlternateUsernameAllowed()) {
            return false;
        }
        if (username == null) {
            username = this.poolProperties.getUsername();
        }
        if (password == null) {
            password = this.poolProperties.getPassword();
        }
        String storedUsr = (String)this.getAttributes().get(PROP_USER);
        String storedPwd = (String)this.getAttributes().get(PROP_PASSWORD);
        boolean noChangeInCredentials = username == null && storedUsr == null;
        noChangeInCredentials = noChangeInCredentials || username != null && username.equals(storedUsr);
        boolean bl = noChangeInCredentials = noChangeInCredentials && (password == null && storedPwd == null || password != null && password.equals(storedPwd));
        if (username == null) {
            this.getAttributes().remove(PROP_USER);
        } else {
            this.getAttributes().put(PROP_USER, username);
        }
        if (password == null) {
            this.getAttributes().remove(PROP_PASSWORD);
        } else {
            this.getAttributes().put(PROP_PASSWORD, password);
        }
        return !noChangeInCredentials;
    }

    public void connect() throws SQLException {
        if (this.released.get()) {
            throw new SQLException("A connection once released, can't be reestablished.");
        }
        if (this.connection != null) {
            try {
                this.disconnect(false);
            }
            catch (Exception x) {
                log.debug((Object)"Unable to disconnect previous connection.", (Throwable)x);
            }
        }
        if (this.poolProperties.getDataSource() != null) {
            this.connectUsingDataSource();
        } else {
            this.connectUsingDriver();
        }
        if (this.poolProperties.getJdbcInterceptors() == null || this.poolProperties.getJdbcInterceptors().indexOf(ConnectionState.class.getName()) < 0 || this.poolProperties.getJdbcInterceptors().indexOf(ConnectionState.class.getSimpleName()) < 0) {
            if (this.poolProperties.getDefaultTransactionIsolation() != -1) {
                this.connection.setTransactionIsolation(this.poolProperties.getDefaultTransactionIsolation());
            }
            if (this.poolProperties.getDefaultReadOnly() != null) {
                this.connection.setReadOnly(this.poolProperties.getDefaultReadOnly());
            }
            if (this.poolProperties.getDefaultAutoCommit() != null) {
                this.connection.setAutoCommit(this.poolProperties.getDefaultAutoCommit());
            }
            if (this.poolProperties.getDefaultCatalog() != null) {
                this.connection.setCatalog(this.poolProperties.getDefaultCatalog());
            }
        }
        this.discarded = false;
        this.lastConnected = System.currentTimeMillis();
    }

    protected void connectUsingDataSource() throws SQLException {
        String usr = null;
        String pwd = null;
        if (this.getAttributes().containsKey(PROP_USER)) {
            usr = (String)this.getAttributes().get(PROP_USER);
        } else {
            usr = this.poolProperties.getUsername();
            this.getAttributes().put(PROP_USER, usr);
        }
        if (this.getAttributes().containsKey(PROP_PASSWORD)) {
            pwd = (String)this.getAttributes().get(PROP_PASSWORD);
        } else {
            pwd = this.poolProperties.getPassword();
            this.getAttributes().put(PROP_PASSWORD, pwd);
        }
        if (this.poolProperties.getDataSource() instanceof XADataSource) {
            XADataSource xds = (XADataSource)this.poolProperties.getDataSource();
            if (usr != null && pwd != null) {
                this.xaConnection = xds.getXAConnection(usr, pwd);
                this.connection = this.xaConnection.getConnection();
            } else {
                this.xaConnection = xds.getXAConnection();
                this.connection = this.xaConnection.getConnection();
            }
        } else if (this.poolProperties.getDataSource() instanceof DataSource) {
            DataSource ds = (DataSource)this.poolProperties.getDataSource();
            this.connection = usr != null && pwd != null ? ds.getConnection(usr, pwd) : ds.getConnection();
        } else if (this.poolProperties.getDataSource() instanceof ConnectionPoolDataSource) {
            ConnectionPoolDataSource ds = (ConnectionPoolDataSource)this.poolProperties.getDataSource();
            this.connection = usr != null && pwd != null ? ds.getPooledConnection(usr, pwd).getConnection() : ds.getPooledConnection().getConnection();
        } else {
            throw new SQLException("DataSource is of unknown class:" + (this.poolProperties.getDataSource() != null ? this.poolProperties.getDataSource().getClass() : "null"));
        }
    }

    protected void connectUsingDriver() throws SQLException {
        try {
            if (this.driver == null) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)("Instantiating driver using class: " + this.poolProperties.getDriverClassName() + " [url=" + this.poolProperties.getUrl() + "]"));
                }
                if (this.poolProperties.getDriverClassName() == null) {
                    log.warn((Object)"Not loading a JDBC driver as driverClassName property is null.");
                } else {
                    this.driver = (Driver)ClassLoaderUtil.loadClass(this.poolProperties.getDriverClassName(), PooledConnection.class.getClassLoader(), Thread.currentThread().getContextClassLoader()).getConstructor(new Class[0]).newInstance(new Object[0]);
                }
            }
        }
        catch (Exception cn) {
            if (log.isDebugEnabled()) {
                log.debug((Object)"Unable to instantiate JDBC driver.", (Throwable)cn);
            }
            SQLException ex = new SQLException(cn.getMessage());
            ex.initCause(cn);
            throw ex;
        }
        String driverURL = this.poolProperties.getUrl();
        String usr = null;
        String pwd = null;
        if (this.getAttributes().containsKey(PROP_USER)) {
            usr = (String)this.getAttributes().get(PROP_USER);
        } else {
            usr = this.poolProperties.getUsername();
            this.getAttributes().put(PROP_USER, usr);
        }
        if (this.getAttributes().containsKey(PROP_PASSWORD)) {
            pwd = (String)this.getAttributes().get(PROP_PASSWORD);
        } else {
            pwd = this.poolProperties.getPassword();
            this.getAttributes().put(PROP_PASSWORD, pwd);
        }
        Properties properties = PoolUtilities.clone(this.poolProperties.getDbProperties());
        if (usr != null) {
            properties.setProperty(PROP_USER, usr);
        }
        if (pwd != null) {
            properties.setProperty(PROP_PASSWORD, pwd);
        }
        try {
            this.connection = this.driver == null ? DriverManager.getConnection(driverURL, properties) : this.driver.connect(driverURL, properties);
        }
        catch (Exception x) {
            if (log.isDebugEnabled()) {
                log.debug((Object)"Unable to connect to database.", (Throwable)x);
            }
            if (this.parent.jmxPool != null) {
                this.parent.jmxPool.notify("CONNECTION FAILED", ConnectionPool.getStackTrace(x));
            }
            if (x instanceof SQLException) {
                throw (SQLException)x;
            }
            SQLException ex = new SQLException(x.getMessage());
            ex.initCause(x);
            throw ex;
        }
        if (this.connection == null) {
            throw new SQLException("Driver:" + this.driver + " returned null for URL:" + driverURL);
        }
    }

    @Override
    public boolean isInitialized() {
        return this.connection != null;
    }

    @Override
    public boolean isMaxAgeExpired() {
        if (this.getPoolProperties().getMaxAge() > 0L) {
            return System.currentTimeMillis() - this.getLastConnected() > this.getPoolProperties().getMaxAge();
        }
        return false;
    }

    public void reconnect() throws SQLException {
        this.disconnect(false);
        this.connect();
    }

    private void disconnect(boolean finalize) {
        block7: {
            if (this.isDiscarded() && this.connection == null) {
                return;
            }
            this.setDiscarded(true);
            if (this.connection != null) {
                try {
                    this.parent.disconnectEvent(this, finalize);
                    if (this.xaConnection == null) {
                        this.connection.close();
                    } else {
                        this.xaConnection.close();
                    }
                }
                catch (Exception ignore) {
                    if (!log.isDebugEnabled()) break block7;
                    log.debug((Object)"Unable to close underlying SQL connection", (Throwable)ignore);
                }
            }
        }
        this.connection = null;
        this.xaConnection = null;
        this.lastConnected = -1L;
        if (finalize) {
            this.parent.finalize(this);
        }
    }

    public long getAbandonTimeout() {
        if (this.poolProperties.getRemoveAbandonedTimeout() <= 0) {
            return Long.MAX_VALUE;
        }
        return (long)this.poolProperties.getRemoveAbandonedTimeout() * 1000L;
    }

    private boolean doValidate(int action) {
        if (action == 1 && this.poolProperties.isTestOnBorrow()) {
            return true;
        }
        if (action == 2 && this.poolProperties.isTestOnReturn()) {
            return true;
        }
        if (action == 3 && this.poolProperties.isTestWhileIdle()) {
            return true;
        }
        if (action == 4 && this.poolProperties.isTestOnConnect()) {
            return true;
        }
        return action == 4 && this.poolProperties.getInitSQL() != null;
    }

    public boolean validate(int validateAction) {
        return this.validate(validateAction, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean validate(int validateAction, String sql) {
        if (this.isDiscarded()) {
            return false;
        }
        if (!this.doValidate(validateAction)) {
            return true;
        }
        long now = System.currentTimeMillis();
        if (validateAction != 4 && this.poolProperties.getValidationInterval() > 0L && now - this.lastValidated < this.poolProperties.getValidationInterval()) {
            return true;
        }
        if (this.poolProperties.getValidator() != null) {
            if (this.poolProperties.getValidator().validate(this.connection, validateAction)) {
                this.lastValidated = now;
                return true;
            }
            if (this.getPoolProperties().getLogValidationErrors()) {
                log.error((Object)("Custom validation through " + this.poolProperties.getValidator() + " failed."));
            }
            return false;
        }
        String query = sql;
        if (validateAction == 4 && this.poolProperties.getInitSQL() != null) {
            query = this.poolProperties.getInitSQL();
        }
        if (query == null) {
            query = this.poolProperties.getValidationQuery();
        }
        if (query == null) {
            boolean transactionCommitted = false;
            int validationQueryTimeout = this.poolProperties.getValidationQueryTimeout();
            if (validationQueryTimeout < 0) {
                validationQueryTimeout = 0;
            }
            try {
                if (this.connection.isValid(validationQueryTimeout)) {
                    this.lastValidated = now;
                    transactionCommitted = this.silentlyCommitTransactionIfNeeded();
                    boolean bl = true;
                    return bl;
                }
                if (this.getPoolProperties().getLogValidationErrors()) {
                    log.error((Object)"isValid() returned false.");
                }
                boolean bl = false;
                return bl;
            }
            catch (SQLException e) {
                if (this.getPoolProperties().getLogValidationErrors()) {
                    log.error((Object)"isValid() failed.", (Throwable)e);
                } else if (log.isDebugEnabled()) {
                    log.debug((Object)"isValid() failed.", (Throwable)e);
                }
                boolean bl = false;
                return bl;
            }
            finally {
                if (!transactionCommitted) {
                    this.silentlyRollbackTransactionIfNeeded();
                }
            }
        }
        boolean transactionCommitted = false;
        Statement stmt = null;
        try {
            stmt = this.connection.createStatement();
            int validationQueryTimeout = this.poolProperties.getValidationQueryTimeout();
            if (validationQueryTimeout > 0) {
                stmt.setQueryTimeout(validationQueryTimeout);
            }
            stmt.execute(query);
            stmt.close();
            this.lastValidated = now;
            transactionCommitted = this.silentlyCommitTransactionIfNeeded();
            boolean bl = true;
            return bl;
        }
        catch (Exception ex) {
            if (this.getPoolProperties().getLogValidationErrors()) {
                log.error((Object)"SQL Validation error", (Throwable)ex);
            } else if (log.isDebugEnabled()) {
                log.debug((Object)"Unable to validate object:", (Throwable)ex);
            }
            if (stmt != null) {
                try {
                    stmt.close();
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
        }
        finally {
            if (!transactionCommitted) {
                this.silentlyRollbackTransactionIfNeeded();
            }
        }
        return false;
    }

    private boolean silentlyCommitTransactionIfNeeded() {
        try {
            if (!this.connection.getAutoCommit()) {
                this.connection.commit();
            }
            return true;
        }
        catch (SQLException e) {
            log.debug((Object)"Failed to commit transaction", (Throwable)e);
            return false;
        }
    }

    private boolean silentlyRollbackTransactionIfNeeded() {
        try {
            if (!this.connection.getAutoCommit()) {
                this.connection.rollback();
            }
            return true;
        }
        catch (SQLException e) {
            log.debug((Object)"Failed to rollback transaction", (Throwable)e);
            return false;
        }
    }

    public long getReleaseTime() {
        return this.poolProperties.getMinEvictableIdleTimeMillis();
    }

    public boolean release() {
        block3: {
            try {
                this.disconnect(true);
            }
            catch (Exception x) {
                if (!log.isDebugEnabled()) break block3;
                log.debug((Object)"Unable to close SQL connection", (Throwable)x);
            }
        }
        if (this.oname != null) {
            JmxUtil.unregisterJmx(this.oname);
            this.oname = null;
        }
        return this.released.compareAndSet(false, true);
    }

    public void setStackTrace(String trace) {
        this.abandonTrace = trace;
    }

    public String getStackTrace() {
        return this.abandonTrace;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
        this.setSuspect(false);
    }

    @Override
    public boolean isSuspect() {
        return this.suspect;
    }

    public void setSuspect(boolean suspect) {
        this.suspect = suspect;
    }

    public void setDiscarded(boolean discarded) {
        if (this.discarded && !discarded) {
            throw new IllegalStateException("Unable to change the state once the connection has been discarded");
        }
        this.discarded = discarded;
    }

    public void setLastValidated(long lastValidated) {
        this.lastValidated = lastValidated;
    }

    public void setPoolProperties(PoolConfiguration poolProperties) {
        this.poolProperties = poolProperties;
    }

    @Override
    public long getTimestamp() {
        return this.timestamp;
    }

    @Override
    public boolean isDiscarded() {
        return this.discarded;
    }

    @Override
    public long getLastValidated() {
        return this.lastValidated;
    }

    public PoolConfiguration getPoolProperties() {
        return this.poolProperties;
    }

    public void lock() {
        if (this.poolProperties.getUseLock() || this.poolProperties.isPoolSweeperEnabled()) {
            this.lock.writeLock().lock();
        }
    }

    public void unlock() {
        if (this.poolProperties.getUseLock() || this.poolProperties.isPoolSweeperEnabled()) {
            this.lock.writeLock().unlock();
        }
    }

    public Connection getConnection() {
        return this.connection;
    }

    public XAConnection getXAConnection() {
        return this.xaConnection;
    }

    @Override
    public long getLastConnected() {
        return this.lastConnected;
    }

    public JdbcInterceptor getHandler() {
        return this.handler;
    }

    public void setHandler(JdbcInterceptor handler) {
        if (this.handler != null && this.handler != handler) {
            for (JdbcInterceptor interceptor = this.handler; interceptor != null; interceptor = interceptor.getNext()) {
                interceptor.reset(null, null);
            }
        }
        this.handler = handler;
    }

    public String toString() {
        return "PooledConnection[" + (this.connection != null ? this.connection.toString() : "null") + "]";
    }

    @Override
    public boolean isReleased() {
        return this.released.get();
    }

    public HashMap<Object, Object> getAttributes() {
        return this.attributes;
    }

    public void createMBean() {
        if (this.oname != null) {
            return;
        }
        String keyprop = ",connections=PooledConnection[" + connectionIndex.getAndIncrement() + "]";
        this.oname = JmxUtil.registerJmx(this.parent.getJmxPool().getObjectName(), keyprop, this);
    }

    public ObjectName getObjectName() {
        return this.oname;
    }

    @Override
    public void clearWarnings() {
        try {
            this.connection.clearWarnings();
        }
        catch (SQLException e) {
            log.warn((Object)"Unable to clear Warnings, connection will be closed.", (Throwable)e);
        }
    }

    @Override
    public boolean isClosed() throws SQLException {
        return this.connection.isClosed();
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        return this.connection.getAutoCommit();
    }

    @Override
    public String getCatalog() throws SQLException {
        return this.connection.getCatalog();
    }

    @Override
    public int getHoldability() throws SQLException {
        return this.connection.getHoldability();
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return this.connection.isReadOnly();
    }

    @Override
    public String getSchema() throws SQLException {
        return this.connection.getSchema();
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        return this.connection.getTransactionIsolation();
    }
}

