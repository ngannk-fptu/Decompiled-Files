/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.dbcp.dbcp2.datasources;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.PooledConnection;
import org.apache.tomcat.dbcp.dbcp2.Utils;
import org.apache.tomcat.dbcp.dbcp2.datasources.PooledConnectionAndInfo;
import org.apache.tomcat.dbcp.dbcp2.datasources.PooledConnectionManager;
import org.apache.tomcat.dbcp.dbcp2.datasources.UserPassKey;
import org.apache.tomcat.dbcp.pool2.KeyedObjectPool;
import org.apache.tomcat.dbcp.pool2.KeyedPooledObjectFactory;
import org.apache.tomcat.dbcp.pool2.PooledObject;
import org.apache.tomcat.dbcp.pool2.impl.DefaultPooledObject;

final class KeyedCPDSConnectionFactory
implements KeyedPooledObjectFactory<UserPassKey, PooledConnectionAndInfo>,
ConnectionEventListener,
PooledConnectionManager {
    private static final String NO_KEY_MESSAGE = "close() was called on a Connection, but I have no record of the underlying PooledConnection.";
    private final ConnectionPoolDataSource cpds;
    private final String validationQuery;
    private final Duration validationQueryTimeoutDuration;
    private final boolean rollbackAfterValidation;
    private KeyedObjectPool<UserPassKey, PooledConnectionAndInfo> pool;
    private Duration maxConnLifetime = Duration.ofMillis(-1L);
    private final Set<PooledConnection> validatingSet = Collections.newSetFromMap(new ConcurrentHashMap());
    private final Map<PooledConnection, PooledConnectionAndInfo> pcMap = new ConcurrentHashMap<PooledConnection, PooledConnectionAndInfo>();

    KeyedCPDSConnectionFactory(ConnectionPoolDataSource cpds, String validationQuery, Duration validationQueryTimeoutSeconds, boolean rollbackAfterValidation) {
        this.cpds = cpds;
        this.validationQuery = validationQuery;
        this.validationQueryTimeoutDuration = validationQueryTimeoutSeconds;
        this.rollbackAfterValidation = rollbackAfterValidation;
    }

    @Deprecated
    KeyedCPDSConnectionFactory(ConnectionPoolDataSource cpds, String validationQuery, int validationQueryTimeoutSeconds, boolean rollbackAfterValidation) {
        this(cpds, validationQuery, Duration.ofSeconds(validationQueryTimeoutSeconds), rollbackAfterValidation);
    }

    @Override
    public void activateObject(UserPassKey key, PooledObject<PooledConnectionAndInfo> p) throws SQLException {
        this.validateLifetime(p);
    }

    @Override
    public void closePool(String userName) throws SQLException {
        try {
            this.pool.clear(new UserPassKey(userName));
        }
        catch (Exception ex) {
            throw new SQLException("Error closing connection pool", ex);
        }
    }

    @Override
    public void connectionClosed(ConnectionEvent event) {
        PooledConnection pc = (PooledConnection)event.getSource();
        if (!this.validatingSet.contains(pc)) {
            PooledConnectionAndInfo pci = this.pcMap.get(pc);
            if (pci == null) {
                throw new IllegalStateException(NO_KEY_MESSAGE);
            }
            try {
                this.pool.returnObject(pci.getUserPassKey(), pci);
            }
            catch (Exception e) {
                System.err.println("CLOSING DOWN CONNECTION AS IT COULD NOT BE RETURNED TO THE POOL");
                pc.removeConnectionEventListener(this);
                try {
                    this.pool.invalidateObject(pci.getUserPassKey(), pci);
                }
                catch (Exception e3) {
                    System.err.println("EXCEPTION WHILE DESTROYING OBJECT " + pci);
                    e3.printStackTrace();
                }
            }
        }
    }

    @Override
    public void connectionErrorOccurred(ConnectionEvent event) {
        PooledConnection pc = (PooledConnection)event.getSource();
        if (null != event.getSQLException()) {
            System.err.println("CLOSING DOWN CONNECTION DUE TO INTERNAL ERROR (" + event.getSQLException() + ")");
        }
        pc.removeConnectionEventListener(this);
        PooledConnectionAndInfo info = this.pcMap.get(pc);
        if (info == null) {
            throw new IllegalStateException(NO_KEY_MESSAGE);
        }
        try {
            this.pool.invalidateObject(info.getUserPassKey(), info);
        }
        catch (Exception e) {
            System.err.println("EXCEPTION WHILE DESTROYING OBJECT " + info);
            e.printStackTrace();
        }
    }

    @Override
    public void destroyObject(UserPassKey key, PooledObject<PooledConnectionAndInfo> p) throws SQLException {
        PooledConnection pooledConnection = p.getObject().getPooledConnection();
        pooledConnection.removeConnectionEventListener(this);
        this.pcMap.remove(pooledConnection);
        pooledConnection.close();
    }

    public KeyedObjectPool<UserPassKey, PooledConnectionAndInfo> getPool() {
        return this.pool;
    }

    @Override
    public void invalidate(PooledConnection pc) throws SQLException {
        PooledConnectionAndInfo info = this.pcMap.get(pc);
        if (info == null) {
            throw new IllegalStateException(NO_KEY_MESSAGE);
        }
        UserPassKey key = info.getUserPassKey();
        try {
            this.pool.invalidateObject(key, info);
            this.pool.clear(key);
        }
        catch (Exception ex) {
            throw new SQLException("Error invalidating connection", ex);
        }
    }

    @Override
    public synchronized PooledObject<PooledConnectionAndInfo> makeObject(UserPassKey userPassKey) throws SQLException {
        PooledConnection pooledConnection = null;
        String userName = userPassKey.getUserName();
        String password = userPassKey.getPassword();
        pooledConnection = userName == null ? this.cpds.getPooledConnection() : this.cpds.getPooledConnection(userName, password);
        if (pooledConnection == null) {
            throw new IllegalStateException("Connection pool data source returned null from getPooledConnection");
        }
        pooledConnection.addConnectionEventListener(this);
        PooledConnectionAndInfo pci = new PooledConnectionAndInfo(pooledConnection, userPassKey);
        this.pcMap.put(pooledConnection, pci);
        return new DefaultPooledObject<PooledConnectionAndInfo>(pci);
    }

    @Override
    public void passivateObject(UserPassKey key, PooledObject<PooledConnectionAndInfo> p) throws SQLException {
        this.validateLifetime(p);
    }

    public void setMaxConn(Duration maxConnLifetimeMillis) {
        this.maxConnLifetime = maxConnLifetimeMillis;
    }

    @Deprecated
    public void setMaxConnLifetime(Duration maxConnLifetimeMillis) {
        this.maxConnLifetime = maxConnLifetimeMillis;
    }

    @Deprecated
    public void setMaxConnLifetimeMillis(long maxConnLifetimeMillis) {
        this.setMaxConn(Duration.ofMillis(maxConnLifetimeMillis));
    }

    @Override
    public void setPassword(String password) {
    }

    public void setPool(KeyedObjectPool<UserPassKey, PooledConnectionAndInfo> pool) {
        this.pool = pool;
    }

    private void validateLifetime(PooledObject<PooledConnectionAndInfo> pooledObject) throws SQLException {
        Utils.validateLifetime(pooledObject, this.maxConnLifetime);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public boolean validateObject(UserPassKey key, PooledObject<PooledConnectionAndInfo> pooledObject) {
        try {
            this.validateLifetime(pooledObject);
        }
        catch (Exception e) {
            return false;
        }
        boolean valid = false;
        PooledConnection pooledConn = pooledObject.getObject().getPooledConnection();
        Connection conn = null;
        this.validatingSet.add(pooledConn);
        if (null == this.validationQuery) {
            Duration timeoutDuration = this.validationQueryTimeoutDuration;
            if (timeoutDuration.isNegative()) {
                timeoutDuration = Duration.ZERO;
            }
            try {
                conn = pooledConn.getConnection();
                valid = conn.isValid((int)timeoutDuration.getSeconds());
                return valid;
            }
            catch (SQLException e) {
                valid = false;
                return valid;
            }
            finally {
                Utils.closeQuietly((AutoCloseable)conn);
                this.validatingSet.remove(pooledConn);
            }
        }
        Statement stmt = null;
        ResultSet rset = null;
        this.validatingSet.add(pooledConn);
        try {
            conn = pooledConn.getConnection();
            stmt = conn.createStatement();
            rset = stmt.executeQuery(this.validationQuery);
            valid = rset.next();
            if (this.rollbackAfterValidation) {
                conn.rollback();
            }
            Utils.closeQuietly((AutoCloseable)rset);
        }
        catch (Exception e) {
            valid = false;
            return valid;
        }
        Utils.closeQuietly((AutoCloseable)stmt);
        Utils.closeQuietly((AutoCloseable)conn);
        this.validatingSet.remove(pooledConn);
        return valid;
        finally {
            Utils.closeQuietly(rset);
            Utils.closeQuietly((AutoCloseable)stmt);
            Utils.closeQuietly((AutoCloseable)conn);
            this.validatingSet.remove(pooledConn);
        }
    }
}

