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
import org.apache.tomcat.dbcp.pool2.ObjectPool;
import org.apache.tomcat.dbcp.pool2.PooledObject;
import org.apache.tomcat.dbcp.pool2.PooledObjectFactory;
import org.apache.tomcat.dbcp.pool2.impl.DefaultPooledObject;

final class CPDSConnectionFactory
implements PooledObjectFactory<PooledConnectionAndInfo>,
ConnectionEventListener,
PooledConnectionManager {
    private static final String NO_KEY_MESSAGE = "close() was called on a Connection, but I have no record of the underlying PooledConnection.";
    private final ConnectionPoolDataSource cpds;
    private final String validationQuery;
    private final Duration validationQueryTimeoutDuration;
    private final boolean rollbackAfterValidation;
    private ObjectPool<PooledConnectionAndInfo> pool;
    private UserPassKey userPassKey;
    private Duration maxConnDuration = Duration.ofMillis(-1L);
    private final Set<PooledConnection> validatingSet = Collections.newSetFromMap(new ConcurrentHashMap());
    private final Map<PooledConnection, PooledConnectionAndInfo> pcMap = new ConcurrentHashMap<PooledConnection, PooledConnectionAndInfo>();

    CPDSConnectionFactory(ConnectionPoolDataSource cpds, String validationQuery, Duration validationQueryTimeoutDuration, boolean rollbackAfterValidation, String userName, char[] userPassword) {
        this.cpds = cpds;
        this.validationQuery = validationQuery;
        this.validationQueryTimeoutDuration = validationQueryTimeoutDuration;
        this.userPassKey = new UserPassKey(userName, userPassword);
        this.rollbackAfterValidation = rollbackAfterValidation;
    }

    CPDSConnectionFactory(ConnectionPoolDataSource cpds, String validationQuery, Duration validationQueryTimeoutDuration, boolean rollbackAfterValidation, String userName, String userPassword) {
        this(cpds, validationQuery, validationQueryTimeoutDuration, rollbackAfterValidation, userName, Utils.toCharArray(userPassword));
    }

    @Deprecated
    CPDSConnectionFactory(ConnectionPoolDataSource cpds, String validationQuery, int validationQueryTimeoutSeconds, boolean rollbackAfterValidation, String userName, char[] userPassword) {
        this.cpds = cpds;
        this.validationQuery = validationQuery;
        this.validationQueryTimeoutDuration = Duration.ofSeconds(validationQueryTimeoutSeconds);
        this.userPassKey = new UserPassKey(userName, userPassword);
        this.rollbackAfterValidation = rollbackAfterValidation;
    }

    @Deprecated
    CPDSConnectionFactory(ConnectionPoolDataSource cpds, String validationQuery, int validationQueryTimeoutSeconds, boolean rollbackAfterValidation, String userName, String userPassword) {
        this(cpds, validationQuery, validationQueryTimeoutSeconds, rollbackAfterValidation, userName, Utils.toCharArray(userPassword));
    }

    @Override
    public void activateObject(PooledObject<PooledConnectionAndInfo> p) throws SQLException {
        this.validateLifetime(p);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void closePool(String userName) throws SQLException {
        CPDSConnectionFactory cPDSConnectionFactory = this;
        synchronized (cPDSConnectionFactory) {
            if (userName == null || !userName.equals(this.userPassKey.getUserName())) {
                return;
            }
        }
        try {
            this.pool.close();
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
                this.pool.returnObject(pci);
            }
            catch (Exception e) {
                System.err.println("CLOSING DOWN CONNECTION AS IT COULD NOT BE RETURNED TO THE POOL");
                pc.removeConnectionEventListener(this);
                try {
                    this.doDestroyObject(pci);
                }
                catch (Exception e2) {
                    System.err.println("EXCEPTION WHILE DESTROYING OBJECT " + pci);
                    e2.printStackTrace();
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
        PooledConnectionAndInfo pci = this.pcMap.get(pc);
        if (pci == null) {
            throw new IllegalStateException(NO_KEY_MESSAGE);
        }
        try {
            this.pool.invalidateObject(pci);
        }
        catch (Exception e) {
            System.err.println("EXCEPTION WHILE DESTROYING OBJECT " + pci);
            e.printStackTrace();
        }
    }

    @Override
    public void destroyObject(PooledObject<PooledConnectionAndInfo> p) throws SQLException {
        this.doDestroyObject(p.getObject());
    }

    private void doDestroyObject(PooledConnectionAndInfo pci) throws SQLException {
        PooledConnection pc = pci.getPooledConnection();
        pc.removeConnectionEventListener(this);
        this.pcMap.remove(pc);
        pc.close();
    }

    char[] getPasswordCharArray() {
        return this.userPassKey.getPasswordCharArray();
    }

    public ObjectPool<PooledConnectionAndInfo> getPool() {
        return this.pool;
    }

    @Override
    public void invalidate(PooledConnection pc) throws SQLException {
        PooledConnectionAndInfo pci = this.pcMap.get(pc);
        if (pci == null) {
            throw new IllegalStateException(NO_KEY_MESSAGE);
        }
        try {
            this.pool.invalidateObject(pci);
            this.pool.close();
        }
        catch (Exception ex) {
            throw new SQLException("Error invalidating connection", ex);
        }
    }

    @Override
    public synchronized PooledObject<PooledConnectionAndInfo> makeObject() {
        try {
            PooledConnection pc = null;
            pc = this.userPassKey.getUserName() == null ? this.cpds.getPooledConnection() : this.cpds.getPooledConnection(this.userPassKey.getUserName(), this.userPassKey.getPassword());
            if (pc == null) {
                throw new IllegalStateException("Connection pool data source returned null from getPooledConnection");
            }
            pc.addConnectionEventListener(this);
            PooledConnectionAndInfo pci = new PooledConnectionAndInfo(pc, this.userPassKey);
            this.pcMap.put(pc, pci);
            return new DefaultPooledObject<PooledConnectionAndInfo>(pci);
        }
        catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void passivateObject(PooledObject<PooledConnectionAndInfo> p) throws SQLException {
        this.validateLifetime(p);
    }

    public void setMaxConn(Duration maxConnDuration) {
        this.maxConnDuration = maxConnDuration;
    }

    @Deprecated
    public void setMaxConnLifetime(Duration maxConnDuration) {
        this.maxConnDuration = maxConnDuration;
    }

    @Deprecated
    public void setMaxConnLifetimeMillis(long maxConnLifetimeMillis) {
        this.setMaxConnLifetime(Duration.ofMillis(maxConnLifetimeMillis));
    }

    public synchronized void setPassword(char[] userPassword) {
        this.userPassKey = new UserPassKey(this.userPassKey.getUserName(), userPassword);
    }

    @Override
    public synchronized void setPassword(String userPassword) {
        this.userPassKey = new UserPassKey(this.userPassKey.getUserName(), userPassword);
    }

    public void setPool(ObjectPool<PooledConnectionAndInfo> pool) {
        this.pool = pool;
    }

    public synchronized String toString() {
        StringBuilder builder = new StringBuilder(super.toString());
        builder.append("[cpds=");
        builder.append(this.cpds);
        builder.append(", validationQuery=");
        builder.append(this.validationQuery);
        builder.append(", validationQueryTimeoutDuration=");
        builder.append(this.validationQueryTimeoutDuration);
        builder.append(", rollbackAfterValidation=");
        builder.append(this.rollbackAfterValidation);
        builder.append(", pool=");
        builder.append(this.pool);
        builder.append(", maxConnDuration=");
        builder.append(this.maxConnDuration);
        builder.append(", validatingSet=");
        builder.append(this.validatingSet);
        builder.append(", pcMap=");
        builder.append(this.pcMap);
        builder.append("]");
        return builder.toString();
    }

    private void validateLifetime(PooledObject<PooledConnectionAndInfo> p) throws SQLException {
        Utils.validateLifetime(p, this.maxConnDuration);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public boolean validateObject(PooledObject<PooledConnectionAndInfo> p) {
        try {
            this.validateLifetime(p);
        }
        catch (Exception e) {
            return false;
        }
        boolean valid = false;
        PooledConnection pconn = p.getObject().getPooledConnection();
        Connection conn = null;
        this.validatingSet.add(pconn);
        if (null == this.validationQuery) {
            Duration timeoutDuration = this.validationQueryTimeoutDuration;
            if (timeoutDuration.isNegative()) {
                timeoutDuration = Duration.ZERO;
            }
            try {
                conn = pconn.getConnection();
                valid = conn.isValid((int)timeoutDuration.getSeconds());
                return valid;
            }
            catch (SQLException e) {
                valid = false;
                return valid;
            }
            finally {
                Utils.closeQuietly((AutoCloseable)conn);
                this.validatingSet.remove(pconn);
            }
        }
        Statement stmt = null;
        ResultSet rset = null;
        this.validatingSet.add(pconn);
        try {
            conn = pconn.getConnection();
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
        this.validatingSet.remove(pconn);
        return valid;
        finally {
            Utils.closeQuietly(rset);
            Utils.closeQuietly((AutoCloseable)stmt);
            Utils.closeQuietly((AutoCloseable)conn);
            this.validatingSet.remove(pconn);
        }
    }
}

