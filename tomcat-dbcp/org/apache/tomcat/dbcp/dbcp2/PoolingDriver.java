/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.dbcp.dbcp2;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.logging.Logger;
import org.apache.tomcat.dbcp.dbcp2.DelegatingConnection;
import org.apache.tomcat.dbcp.dbcp2.Utils;
import org.apache.tomcat.dbcp.pool2.ObjectPool;

public class PoolingDriver
implements Driver {
    private static final DriverPropertyInfo[] EMPTY_DRIVER_PROPERTY_INFO_ARRAY = new DriverPropertyInfo[0];
    protected static final HashMap<String, ObjectPool<? extends Connection>> pools;
    public static final String URL_PREFIX = "jdbc:apache:commons:dbcp:";
    protected static final int URL_PREFIX_LEN;
    protected static final int MAJOR_VERSION = 1;
    protected static final int MINOR_VERSION = 0;
    private final boolean accessToUnderlyingConnectionAllowed;

    public PoolingDriver() {
        this(true);
    }

    protected PoolingDriver(boolean accessToUnderlyingConnectionAllowed) {
        this.accessToUnderlyingConnectionAllowed = accessToUnderlyingConnectionAllowed;
    }

    @Override
    public boolean acceptsURL(String url) throws SQLException {
        return url != null && url.startsWith(URL_PREFIX);
    }

    public synchronized void closePool(String name) throws SQLException {
        ObjectPool<? extends Connection> pool = pools.get(name);
        if (pool != null) {
            pools.remove(name);
            try {
                pool.close();
            }
            catch (Exception e) {
                throw new SQLException("Error closing pool " + name, e);
            }
        }
    }

    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        if (this.acceptsURL(url)) {
            ObjectPool<? extends Connection> pool = this.getConnectionPool(url.substring(URL_PREFIX_LEN));
            try {
                Connection conn = pool.borrowObject();
                if (conn == null) {
                    return null;
                }
                return new PoolGuardConnectionWrapper(pool, conn);
            }
            catch (NoSuchElementException e) {
                throw new SQLException("Cannot get a connection, pool error: " + e.getMessage(), e);
            }
            catch (RuntimeException | SQLException e) {
                throw e;
            }
            catch (Exception e) {
                throw new SQLException("Cannot get a connection, general error: " + e.getMessage(), e);
            }
        }
        return null;
    }

    public synchronized ObjectPool<? extends Connection> getConnectionPool(String name) throws SQLException {
        ObjectPool<? extends Connection> pool = pools.get(name);
        if (null == pool) {
            throw new SQLException("Pool not registered: " + name);
        }
        return pool;
    }

    @Override
    public int getMajorVersion() {
        return 1;
    }

    @Override
    public int getMinorVersion() {
        return 0;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException();
    }

    public synchronized String[] getPoolNames() {
        return pools.keySet().toArray(Utils.EMPTY_STRING_ARRAY);
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) {
        return EMPTY_DRIVER_PROPERTY_INFO_ARRAY;
    }

    public void invalidateConnection(Connection conn) throws SQLException {
        if (!(conn instanceof PoolGuardConnectionWrapper)) {
            throw new SQLException("Invalid connection class");
        }
        PoolGuardConnectionWrapper pgconn = (PoolGuardConnectionWrapper)conn;
        ObjectPool pool = pgconn.pool;
        try {
            pool.invalidateObject(pgconn.getDelegateInternal());
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    protected boolean isAccessToUnderlyingConnectionAllowed() {
        return this.accessToUnderlyingConnectionAllowed;
    }

    @Override
    public boolean jdbcCompliant() {
        return true;
    }

    public synchronized void registerPool(String name, ObjectPool<? extends Connection> pool) {
        pools.put(name, pool);
    }

    static {
        try {
            DriverManager.registerDriver(new PoolingDriver());
        }
        catch (Exception exception) {
            // empty catch block
        }
        pools = new HashMap();
        URL_PREFIX_LEN = URL_PREFIX.length();
    }

    private class PoolGuardConnectionWrapper
    extends DelegatingConnection<Connection> {
        private final ObjectPool<? extends Connection> pool;

        PoolGuardConnectionWrapper(ObjectPool<? extends Connection> pool, Connection delegate) {
            super(delegate);
            this.pool = pool;
        }

        @Override
        public Connection getDelegate() {
            if (PoolingDriver.this.isAccessToUnderlyingConnectionAllowed()) {
                return super.getDelegate();
            }
            return null;
        }

        @Override
        public Connection getInnermostDelegate() {
            if (PoolingDriver.this.isAccessToUnderlyingConnectionAllowed()) {
                return super.getInnermostDelegate();
            }
            return null;
        }
    }
}

