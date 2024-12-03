/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.dbcp.dbcp2.cpdsadapter;

import java.io.PrintWriter;
import java.io.Serializable;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.time.Duration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.naming.StringRefAddr;
import javax.naming.spi.ObjectFactory;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.PooledConnection;
import org.apache.tomcat.dbcp.dbcp2.DelegatingPreparedStatement;
import org.apache.tomcat.dbcp.dbcp2.PStmtKey;
import org.apache.tomcat.dbcp.dbcp2.Utils;
import org.apache.tomcat.dbcp.dbcp2.cpdsadapter.PooledConnectionImpl;
import org.apache.tomcat.dbcp.pool2.impl.BaseObjectPoolConfig;
import org.apache.tomcat.dbcp.pool2.impl.GenericKeyedObjectPool;
import org.apache.tomcat.dbcp.pool2.impl.GenericKeyedObjectPoolConfig;

public class DriverAdapterCPDS
implements ConnectionPoolDataSource,
Referenceable,
Serializable,
ObjectFactory {
    private static final long serialVersionUID = -4820523787212147844L;
    private static final String GET_CONNECTION_CALLED = "A PooledConnection was already requested from this source, further initialization is not allowed.";
    private String description;
    private String connectionString;
    private String userName;
    private char[] userPassword;
    private String driver;
    private int loginTimeout;
    private transient PrintWriter logWriter;
    private boolean poolPreparedStatements;
    private int maxIdle = 10;
    private Duration durationBetweenEvictionRuns = BaseObjectPoolConfig.DEFAULT_DURATION_BETWEEN_EVICTION_RUNS;
    private int numTestsPerEvictionRun = -1;
    private Duration minEvictableIdleDuration = BaseObjectPoolConfig.DEFAULT_MIN_EVICTABLE_IDLE_DURATION;
    private int maxPreparedStatements = -1;
    private volatile boolean getConnectionCalled;
    private Properties connectionProperties;
    private boolean accessToUnderlyingConnectionAllowed;

    private void assertInitializationAllowed() throws IllegalStateException {
        if (this.getConnectionCalled) {
            throw new IllegalStateException(GET_CONNECTION_CALLED);
        }
    }

    private boolean getBooleanContentString(RefAddr ra) {
        return Boolean.parseBoolean(this.getStringContent(ra));
    }

    public Properties getConnectionProperties() {
        return this.connectionProperties;
    }

    public String getDescription() {
        return this.description;
    }

    public String getDriver() {
        return this.driver;
    }

    public Duration getDurationBetweenEvictionRuns() {
        return this.durationBetweenEvictionRuns;
    }

    private int getIntegerStringContent(RefAddr ra) {
        return Integer.parseInt(this.getStringContent(ra));
    }

    @Override
    public int getLoginTimeout() {
        return this.loginTimeout;
    }

    @Override
    public PrintWriter getLogWriter() {
        return this.logWriter;
    }

    public int getMaxIdle() {
        return this.maxIdle;
    }

    public int getMaxPreparedStatements() {
        return this.maxPreparedStatements;
    }

    public Duration getMinEvictableIdleDuration() {
        return this.minEvictableIdleDuration;
    }

    @Deprecated
    public int getMinEvictableIdleTimeMillis() {
        return (int)this.minEvictableIdleDuration.toMillis();
    }

    public int getNumTestsPerEvictionRun() {
        return this.numTestsPerEvictionRun;
    }

    @Override
    public Object getObjectInstance(Object refObj, Name name, Context context, Hashtable<?, ?> env) throws ClassNotFoundException {
        Reference ref;
        DriverAdapterCPDS cpds = null;
        if (refObj instanceof Reference && (ref = (Reference)refObj).getClassName().equals(this.getClass().getName())) {
            RefAddr ra = ref.get("description");
            if (this.isNotEmpty(ra)) {
                this.setDescription(this.getStringContent(ra));
            }
            if (this.isNotEmpty(ra = ref.get("driver"))) {
                this.setDriver(this.getStringContent(ra));
            }
            if (this.isNotEmpty(ra = ref.get("url"))) {
                this.setUrl(this.getStringContent(ra));
            }
            if (this.isNotEmpty(ra = ref.get("user"))) {
                this.setUser(this.getStringContent(ra));
            }
            if (this.isNotEmpty(ra = ref.get("password"))) {
                this.setPassword(this.getStringContent(ra));
            }
            if (this.isNotEmpty(ra = ref.get("poolPreparedStatements"))) {
                this.setPoolPreparedStatements(this.getBooleanContentString(ra));
            }
            if (this.isNotEmpty(ra = ref.get("maxIdle"))) {
                this.setMaxIdle(this.getIntegerStringContent(ra));
            }
            if (this.isNotEmpty(ra = ref.get("timeBetweenEvictionRunsMillis"))) {
                this.setTimeBetweenEvictionRunsMillis(this.getIntegerStringContent(ra));
            }
            if (this.isNotEmpty(ra = ref.get("numTestsPerEvictionRun"))) {
                this.setNumTestsPerEvictionRun(this.getIntegerStringContent(ra));
            }
            if (this.isNotEmpty(ra = ref.get("minEvictableIdleTimeMillis"))) {
                this.setMinEvictableIdleTimeMillis(this.getIntegerStringContent(ra));
            }
            if (this.isNotEmpty(ra = ref.get("maxPreparedStatements"))) {
                this.setMaxPreparedStatements(this.getIntegerStringContent(ra));
            }
            if (this.isNotEmpty(ra = ref.get("accessToUnderlyingConnectionAllowed"))) {
                this.setAccessToUnderlyingConnectionAllowed(this.getBooleanContentString(ra));
            }
            cpds = this;
        }
        return cpds;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException();
    }

    public String getPassword() {
        return Utils.toString(this.userPassword);
    }

    public char[] getPasswordCharArray() {
        return Utils.clone(this.userPassword);
    }

    @Override
    public PooledConnection getPooledConnection() throws SQLException {
        return this.getPooledConnection(this.getUser(), this.getPassword());
    }

    @Override
    public PooledConnection getPooledConnection(String pooledUserName, String pooledUserPassword) throws SQLException {
        this.getConnectionCalled = true;
        PooledConnectionImpl pooledConnection = null;
        try {
            if (this.connectionProperties != null) {
                this.update(this.connectionProperties, "user", pooledUserName);
                this.update(this.connectionProperties, "password", pooledUserPassword);
                pooledConnection = new PooledConnectionImpl(DriverManager.getConnection(this.getUrl(), this.connectionProperties));
            } else {
                pooledConnection = new PooledConnectionImpl(DriverManager.getConnection(this.getUrl(), pooledUserName, pooledUserPassword));
            }
            pooledConnection.setAccessToUnderlyingConnectionAllowed(this.isAccessToUnderlyingConnectionAllowed());
        }
        catch (ClassCircularityError e) {
            pooledConnection = this.connectionProperties != null ? new PooledConnectionImpl(DriverManager.getConnection(this.getUrl(), this.connectionProperties)) : new PooledConnectionImpl(DriverManager.getConnection(this.getUrl(), pooledUserName, pooledUserPassword));
            pooledConnection.setAccessToUnderlyingConnectionAllowed(this.isAccessToUnderlyingConnectionAllowed());
        }
        GenericKeyedObjectPool<PStmtKey, DelegatingPreparedStatement> stmtPool = null;
        if (this.isPoolPreparedStatements()) {
            GenericKeyedObjectPoolConfig config = new GenericKeyedObjectPoolConfig();
            config.setMaxTotalPerKey(Integer.MAX_VALUE);
            config.setBlockWhenExhausted(false);
            config.setMaxWait(Duration.ZERO);
            config.setMaxIdlePerKey(this.getMaxIdle());
            if (this.getMaxPreparedStatements() <= 0) {
                config.setTimeBetweenEvictionRuns(this.getDurationBetweenEvictionRuns());
                config.setNumTestsPerEvictionRun(this.getNumTestsPerEvictionRun());
                config.setMinEvictableIdleDuration(this.getMinEvictableIdleDuration());
            } else {
                config.setMaxTotal(this.getMaxPreparedStatements());
                config.setTimeBetweenEvictionRuns(Duration.ofMillis(-1L));
                config.setNumTestsPerEvictionRun(0);
                config.setMinEvictableIdleDuration(Duration.ZERO);
            }
            stmtPool = new GenericKeyedObjectPool<PStmtKey, DelegatingPreparedStatement>(pooledConnection, config);
            pooledConnection.setStatementPool(stmtPool);
        }
        return pooledConnection;
    }

    @Override
    public Reference getReference() throws NamingException {
        String factory = this.getClass().getName();
        Reference ref = new Reference(this.getClass().getName(), factory, null);
        ref.add(new StringRefAddr("description", this.getDescription()));
        ref.add(new StringRefAddr("driver", this.getDriver()));
        ref.add(new StringRefAddr("loginTimeout", String.valueOf(this.getLoginTimeout())));
        ref.add(new StringRefAddr("password", this.getPassword()));
        ref.add(new StringRefAddr("user", this.getUser()));
        ref.add(new StringRefAddr("url", this.getUrl()));
        ref.add(new StringRefAddr("poolPreparedStatements", String.valueOf(this.isPoolPreparedStatements())));
        ref.add(new StringRefAddr("maxIdle", String.valueOf(this.getMaxIdle())));
        ref.add(new StringRefAddr("numTestsPerEvictionRun", String.valueOf(this.getNumTestsPerEvictionRun())));
        ref.add(new StringRefAddr("maxPreparedStatements", String.valueOf(this.getMaxPreparedStatements())));
        ref.add(new StringRefAddr("durationBetweenEvictionRuns", String.valueOf(this.getDurationBetweenEvictionRuns())));
        ref.add(new StringRefAddr("timeBetweenEvictionRunsMillis", String.valueOf(this.getTimeBetweenEvictionRunsMillis())));
        ref.add(new StringRefAddr("minEvictableIdleDuration", String.valueOf(this.getMinEvictableIdleDuration())));
        ref.add(new StringRefAddr("minEvictableIdleTimeMillis", String.valueOf(this.getMinEvictableIdleTimeMillis())));
        return ref;
    }

    private String getStringContent(RefAddr ra) {
        return ra.getContent().toString();
    }

    @Deprecated
    public long getTimeBetweenEvictionRunsMillis() {
        return this.durationBetweenEvictionRuns.toMillis();
    }

    public String getUrl() {
        return this.connectionString;
    }

    public String getUser() {
        return this.userName;
    }

    public synchronized boolean isAccessToUnderlyingConnectionAllowed() {
        return this.accessToUnderlyingConnectionAllowed;
    }

    private boolean isNotEmpty(RefAddr ra) {
        return ra != null && ra.getContent() != null;
    }

    public boolean isPoolPreparedStatements() {
        return this.poolPreparedStatements;
    }

    public synchronized void setAccessToUnderlyingConnectionAllowed(boolean allow) {
        this.accessToUnderlyingConnectionAllowed = allow;
    }

    public void setConnectionProperties(Properties props) {
        this.assertInitializationAllowed();
        this.connectionProperties = props;
        if (this.connectionProperties != null) {
            if (this.connectionProperties.containsKey("user")) {
                this.setUser(this.connectionProperties.getProperty("user"));
            }
            if (this.connectionProperties.containsKey("password")) {
                this.setPassword(this.connectionProperties.getProperty("password"));
            }
        }
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDriver(String driver) throws ClassNotFoundException {
        this.assertInitializationAllowed();
        this.driver = driver;
        Class.forName(driver);
    }

    public void setDurationBetweenEvictionRuns(Duration durationBetweenEvictionRuns) {
        this.assertInitializationAllowed();
        this.durationBetweenEvictionRuns = durationBetweenEvictionRuns;
    }

    @Override
    public void setLoginTimeout(int seconds) {
        this.loginTimeout = seconds;
    }

    @Override
    public void setLogWriter(PrintWriter logWriter) {
        this.logWriter = logWriter;
    }

    public void setMaxIdle(int maxIdle) {
        this.assertInitializationAllowed();
        this.maxIdle = maxIdle;
    }

    public void setMaxPreparedStatements(int maxPreparedStatements) {
        this.maxPreparedStatements = maxPreparedStatements;
    }

    public void setMinEvictableIdleDuration(Duration minEvictableIdleDuration) {
        this.assertInitializationAllowed();
        this.minEvictableIdleDuration = minEvictableIdleDuration;
    }

    @Deprecated
    public void setMinEvictableIdleTimeMillis(int minEvictableIdleTimeMillis) {
        this.assertInitializationAllowed();
        this.minEvictableIdleDuration = Duration.ofMillis(minEvictableIdleTimeMillis);
    }

    public void setNumTestsPerEvictionRun(int numTestsPerEvictionRun) {
        this.assertInitializationAllowed();
        this.numTestsPerEvictionRun = numTestsPerEvictionRun;
    }

    public void setPassword(char[] userPassword) {
        this.assertInitializationAllowed();
        this.userPassword = Utils.clone(userPassword);
        this.update(this.connectionProperties, "password", Utils.toString(this.userPassword));
    }

    public void setPassword(String userPassword) {
        this.assertInitializationAllowed();
        this.userPassword = Utils.toCharArray(userPassword);
        this.update(this.connectionProperties, "password", userPassword);
    }

    public void setPoolPreparedStatements(boolean poolPreparedStatements) {
        this.assertInitializationAllowed();
        this.poolPreparedStatements = poolPreparedStatements;
    }

    @Deprecated
    public void setTimeBetweenEvictionRunsMillis(long timeBetweenEvictionRunsMillis) {
        this.assertInitializationAllowed();
        this.durationBetweenEvictionRuns = Duration.ofMillis(timeBetweenEvictionRunsMillis);
    }

    public void setUrl(String connectionString) {
        this.assertInitializationAllowed();
        this.connectionString = connectionString;
    }

    public void setUser(String userName) {
        this.assertInitializationAllowed();
        this.userName = userName;
        this.update(this.connectionProperties, "user", userName);
    }

    public synchronized String toString() {
        StringBuilder builder = new StringBuilder(super.toString());
        builder.append("[description=");
        builder.append(this.description);
        builder.append(", connectionString=");
        builder.append(this.connectionString);
        builder.append(", driver=");
        builder.append(this.driver);
        builder.append(", loginTimeout=");
        builder.append(this.loginTimeout);
        builder.append(", poolPreparedStatements=");
        builder.append(this.poolPreparedStatements);
        builder.append(", maxIdle=");
        builder.append(this.maxIdle);
        builder.append(", timeBetweenEvictionRunsMillis=");
        builder.append(this.durationBetweenEvictionRuns);
        builder.append(", numTestsPerEvictionRun=");
        builder.append(this.numTestsPerEvictionRun);
        builder.append(", minEvictableIdleTimeMillis=");
        builder.append(this.minEvictableIdleDuration);
        builder.append(", maxPreparedStatements=");
        builder.append(this.maxPreparedStatements);
        builder.append(", getConnectionCalled=");
        builder.append(this.getConnectionCalled);
        builder.append(", connectionProperties=");
        builder.append(Utils.cloneWithoutCredentials(this.connectionProperties));
        builder.append(", accessToUnderlyingConnectionAllowed=");
        builder.append(this.accessToUnderlyingConnectionAllowed);
        builder.append("]");
        return builder.toString();
    }

    private void update(Properties properties, String key, String value) {
        if (properties != null && key != null) {
            if (value == null) {
                properties.remove(key);
            } else {
                properties.setProperty(key, value);
            }
        }
    }

    static {
        DriverManager.getDrivers();
    }
}

