/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mchange.lang.ThrowableUtils
 *  com.mchange.v2.log.MLevel
 *  com.mchange.v2.log.MLog
 *  com.mchange.v2.log.MLogger
 */
package com.mchange.v2.c3p0.impl;

import com.mchange.lang.ThrowableUtils;
import com.mchange.v2.c3p0.C3P0Registry;
import com.mchange.v2.c3p0.PooledDataSource;
import com.mchange.v2.c3p0.cfg.C3P0Config;
import com.mchange.v2.c3p0.impl.C3P0PooledConnectionPool;
import com.mchange.v2.c3p0.impl.C3P0PooledConnectionPoolManager;
import com.mchange.v2.c3p0.impl.DbAuth;
import com.mchange.v2.c3p0.impl.PoolBackedDataSourceBase;
import com.mchange.v2.log.MLevel;
import com.mchange.v2.log.MLog;
import com.mchange.v2.log.MLogger;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.PooledConnection;

public abstract class AbstractPoolBackedDataSource
extends PoolBackedDataSourceBase
implements PooledDataSource {
    static final MLogger logger = MLog.getLogger(AbstractPoolBackedDataSource.class);
    static final String NO_CPDS_ERR_MSG = "Attempted to use an uninitialized PoolBackedDataSource. Please call setConnectionPoolDataSource( ... ) to initialize.";
    transient C3P0PooledConnectionPoolManager poolManager;
    transient boolean is_closed = false;
    private static final long serialVersionUID = 1L;
    private static final short VERSION = 1;

    protected AbstractPoolBackedDataSource(boolean autoregister) {
        super(autoregister);
        this.setUpPropertyEvents();
    }

    private void setUpPropertyEvents() {
        PropertyChangeListener l = new PropertyChangeListener(){

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                AbstractPoolBackedDataSource.this.resetPoolManager(false);
            }
        };
        this.addPropertyChangeListener(l);
    }

    protected void initializeNamedConfig(String configName, boolean shouldBindUserOverridesAsString) {
        block4: {
            try {
                if (configName != null) {
                    C3P0Config.bindNamedConfigToBean(this, configName, shouldBindUserOverridesAsString);
                    if (this.getDataSourceName().equals(this.getIdentityToken())) {
                        this.setDataSourceName(configName);
                    }
                }
            }
            catch (Exception e) {
                if (!logger.isLoggable(MLevel.WARNING)) break block4;
                logger.log(MLevel.WARNING, "Error binding PoolBackedDataSource to named-config '" + configName + "'. Some default-config values may be used.", (Throwable)e);
            }
        }
    }

    @Override
    public String getDataSourceName() {
        String out = super.getDataSourceName();
        if (out == null) {
            out = this.getIdentityToken();
        }
        return out;
    }

    @Override
    public Connection getConnection() throws SQLException {
        PooledConnection pc = this.getPoolManager().getPool().checkoutPooledConnection();
        return pc.getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        PooledConnection pc = this.getPoolManager().getPool(username, password).checkoutPooledConnection();
        return pc.getConnection();
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return this.assertCpds().getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        this.assertCpds().setLogWriter(out);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return this.assertCpds().getLoginTimeout();
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        this.assertCpds().setLoginTimeout(seconds);
    }

    @Override
    public int getNumConnections() throws SQLException {
        return this.getPoolManager().getPool().getNumConnections();
    }

    @Override
    public int getNumIdleConnections() throws SQLException {
        return this.getPoolManager().getPool().getNumIdleConnections();
    }

    @Override
    public int getNumBusyConnections() throws SQLException {
        return this.getPoolManager().getPool().getNumBusyConnections();
    }

    @Override
    public int getNumUnclosedOrphanedConnections() throws SQLException {
        return this.getPoolManager().getPool().getNumUnclosedOrphanedConnections();
    }

    @Override
    public int getNumConnectionsDefaultUser() throws SQLException {
        return this.getNumConnections();
    }

    @Override
    public int getNumIdleConnectionsDefaultUser() throws SQLException {
        return this.getNumIdleConnections();
    }

    @Override
    public int getNumBusyConnectionsDefaultUser() throws SQLException {
        return this.getNumBusyConnections();
    }

    @Override
    public int getNumUnclosedOrphanedConnectionsDefaultUser() throws SQLException {
        return this.getNumUnclosedOrphanedConnections();
    }

    @Override
    public int getStatementCacheNumStatementsDefaultUser() throws SQLException {
        return this.getPoolManager().getPool().getStatementCacheNumStatements();
    }

    @Override
    public int getStatementCacheNumCheckedOutDefaultUser() throws SQLException {
        return this.getPoolManager().getPool().getStatementCacheNumCheckedOut();
    }

    @Override
    public int getStatementCacheNumConnectionsWithCachedStatementsDefaultUser() throws SQLException {
        return this.getPoolManager().getPool().getStatementCacheNumConnectionsWithCachedStatements();
    }

    @Override
    public float getEffectivePropertyCycleDefaultUser() throws SQLException {
        return this.getPoolManager().getPool().getEffectivePropertyCycle();
    }

    @Override
    public long getStartTimeMillisDefaultUser() throws SQLException {
        return this.getPoolManager().getPool().getStartTime();
    }

    @Override
    public long getUpTimeMillisDefaultUser() throws SQLException {
        return this.getPoolManager().getPool().getUpTime();
    }

    @Override
    public long getNumFailedCheckinsDefaultUser() throws SQLException {
        return this.getPoolManager().getPool().getNumFailedCheckins();
    }

    @Override
    public long getNumFailedCheckoutsDefaultUser() throws SQLException {
        return this.getPoolManager().getPool().getNumFailedCheckouts();
    }

    @Override
    public long getNumFailedIdleTestsDefaultUser() throws SQLException {
        return this.getPoolManager().getPool().getNumFailedIdleTests();
    }

    @Override
    public int getNumThreadsAwaitingCheckoutDefaultUser() throws SQLException {
        return this.getPoolManager().getPool().getNumThreadsAwaitingCheckout();
    }

    @Override
    public int getThreadPoolSize() throws SQLException {
        return this.getPoolManager().getThreadPoolSize();
    }

    @Override
    public int getThreadPoolNumActiveThreads() throws SQLException {
        return this.getPoolManager().getThreadPoolNumActiveThreads();
    }

    @Override
    public int getThreadPoolNumIdleThreads() throws SQLException {
        return this.getPoolManager().getThreadPoolNumIdleThreads();
    }

    @Override
    public int getThreadPoolNumTasksPending() throws SQLException {
        return this.getPoolManager().getThreadPoolNumTasksPending();
    }

    @Override
    public String sampleThreadPoolStackTraces() throws SQLException {
        return this.getPoolManager().getThreadPoolStackTraces();
    }

    @Override
    public String sampleThreadPoolStatus() throws SQLException {
        return this.getPoolManager().getThreadPoolStatus();
    }

    @Override
    public String sampleStatementCacheStatusDefaultUser() throws SQLException {
        return this.getPoolManager().getPool().dumpStatementCacheStatus();
    }

    @Override
    public String sampleStatementCacheStatus(String username, String password) throws SQLException {
        return this.assertAuthPool(username, password).dumpStatementCacheStatus();
    }

    @Override
    public Throwable getLastAcquisitionFailureDefaultUser() throws SQLException {
        return this.getPoolManager().getPool().getLastAcquisitionFailure();
    }

    @Override
    public Throwable getLastCheckinFailureDefaultUser() throws SQLException {
        return this.getPoolManager().getPool().getLastCheckinFailure();
    }

    @Override
    public Throwable getLastCheckoutFailureDefaultUser() throws SQLException {
        return this.getPoolManager().getPool().getLastCheckoutFailure();
    }

    @Override
    public Throwable getLastIdleTestFailureDefaultUser() throws SQLException {
        return this.getPoolManager().getPool().getLastIdleTestFailure();
    }

    @Override
    public Throwable getLastConnectionTestFailureDefaultUser() throws SQLException {
        return this.getPoolManager().getPool().getLastConnectionTestFailure();
    }

    @Override
    public Throwable getLastAcquisitionFailure(String username, String password) throws SQLException {
        return this.assertAuthPool(username, password).getLastAcquisitionFailure();
    }

    @Override
    public Throwable getLastCheckinFailure(String username, String password) throws SQLException {
        return this.assertAuthPool(username, password).getLastCheckinFailure();
    }

    @Override
    public Throwable getLastCheckoutFailure(String username, String password) throws SQLException {
        return this.assertAuthPool(username, password).getLastCheckoutFailure();
    }

    @Override
    public Throwable getLastIdleTestFailure(String username, String password) throws SQLException {
        return this.assertAuthPool(username, password).getLastIdleTestFailure();
    }

    @Override
    public Throwable getLastConnectionTestFailure(String username, String password) throws SQLException {
        return this.assertAuthPool(username, password).getLastConnectionTestFailure();
    }

    @Override
    public int getNumThreadsAwaitingCheckout(String username, String password) throws SQLException {
        return this.assertAuthPool(username, password).getNumThreadsAwaitingCheckout();
    }

    @Override
    public String sampleLastAcquisitionFailureStackTraceDefaultUser() throws SQLException {
        Throwable t = this.getLastAcquisitionFailureDefaultUser();
        return t == null ? null : ThrowableUtils.extractStackTrace((Throwable)t);
    }

    @Override
    public String sampleLastCheckinFailureStackTraceDefaultUser() throws SQLException {
        Throwable t = this.getLastCheckinFailureDefaultUser();
        return t == null ? null : ThrowableUtils.extractStackTrace((Throwable)t);
    }

    @Override
    public String sampleLastCheckoutFailureStackTraceDefaultUser() throws SQLException {
        Throwable t = this.getLastCheckoutFailureDefaultUser();
        return t == null ? null : ThrowableUtils.extractStackTrace((Throwable)t);
    }

    @Override
    public String sampleLastIdleTestFailureStackTraceDefaultUser() throws SQLException {
        Throwable t = this.getLastIdleTestFailureDefaultUser();
        return t == null ? null : ThrowableUtils.extractStackTrace((Throwable)t);
    }

    @Override
    public String sampleLastConnectionTestFailureStackTraceDefaultUser() throws SQLException {
        Throwable t = this.getLastConnectionTestFailureDefaultUser();
        return t == null ? null : ThrowableUtils.extractStackTrace((Throwable)t);
    }

    @Override
    public String sampleLastAcquisitionFailureStackTrace(String username, String password) throws SQLException {
        Throwable t = this.getLastAcquisitionFailure(username, password);
        return t == null ? null : ThrowableUtils.extractStackTrace((Throwable)t);
    }

    @Override
    public String sampleLastCheckinFailureStackTrace(String username, String password) throws SQLException {
        Throwable t = this.getLastCheckinFailure(username, password);
        return t == null ? null : ThrowableUtils.extractStackTrace((Throwable)t);
    }

    @Override
    public String sampleLastCheckoutFailureStackTrace(String username, String password) throws SQLException {
        Throwable t = this.getLastCheckoutFailure(username, password);
        return t == null ? null : ThrowableUtils.extractStackTrace((Throwable)t);
    }

    @Override
    public String sampleLastIdleTestFailureStackTrace(String username, String password) throws SQLException {
        Throwable t = this.getLastIdleTestFailure(username, password);
        return t == null ? null : ThrowableUtils.extractStackTrace((Throwable)t);
    }

    @Override
    public String sampleLastConnectionTestFailureStackTrace(String username, String password) throws SQLException {
        Throwable t = this.getLastConnectionTestFailure(username, password);
        return t == null ? null : ThrowableUtils.extractStackTrace((Throwable)t);
    }

    @Override
    public void softResetDefaultUser() throws SQLException {
        this.getPoolManager().getPool().reset();
    }

    @Override
    public int getNumConnections(String username, String password) throws SQLException {
        return this.assertAuthPool(username, password).getNumConnections();
    }

    @Override
    public int getNumIdleConnections(String username, String password) throws SQLException {
        return this.assertAuthPool(username, password).getNumIdleConnections();
    }

    @Override
    public int getNumBusyConnections(String username, String password) throws SQLException {
        return this.assertAuthPool(username, password).getNumBusyConnections();
    }

    @Override
    public int getNumUnclosedOrphanedConnections(String username, String password) throws SQLException {
        return this.assertAuthPool(username, password).getNumUnclosedOrphanedConnections();
    }

    @Override
    public int getStatementCacheNumStatements(String username, String password) throws SQLException {
        return this.assertAuthPool(username, password).getStatementCacheNumStatements();
    }

    @Override
    public int getStatementCacheNumCheckedOut(String username, String password) throws SQLException {
        return this.assertAuthPool(username, password).getStatementCacheNumCheckedOut();
    }

    @Override
    public int getStatementCacheNumConnectionsWithCachedStatements(String username, String password) throws SQLException {
        return this.assertAuthPool(username, password).getStatementCacheNumConnectionsWithCachedStatements();
    }

    @Override
    public float getEffectivePropertyCycle(String username, String password) throws SQLException {
        return this.assertAuthPool(username, password).getEffectivePropertyCycle();
    }

    public long getStartTimeMillis(String username, String password) throws SQLException {
        return this.assertAuthPool(username, password).getStartTime();
    }

    public long getUpTimeMillis(String username, String password) throws SQLException {
        return this.assertAuthPool(username, password).getUpTime();
    }

    public long getNumFailedCheckins(String username, String password) throws SQLException {
        return this.assertAuthPool(username, password).getNumFailedCheckins();
    }

    public long getNumFailedCheckouts(String username, String password) throws SQLException {
        return this.assertAuthPool(username, password).getNumFailedCheckouts();
    }

    public long getNumFailedIdleTests(String username, String password) throws SQLException {
        return this.assertAuthPool(username, password).getNumFailedIdleTests();
    }

    @Override
    public void softReset(String username, String password) throws SQLException {
        this.assertAuthPool(username, password).reset();
    }

    @Override
    public int getNumBusyConnectionsAllUsers() throws SQLException {
        return this.getPoolManager().getNumBusyConnectionsAllAuths();
    }

    @Override
    public int getNumIdleConnectionsAllUsers() throws SQLException {
        return this.getPoolManager().getNumIdleConnectionsAllAuths();
    }

    @Override
    public int getNumConnectionsAllUsers() throws SQLException {
        return this.getPoolManager().getNumConnectionsAllAuths();
    }

    @Override
    public int getNumUnclosedOrphanedConnectionsAllUsers() throws SQLException {
        return this.getPoolManager().getNumUnclosedOrphanedConnectionsAllAuths();
    }

    @Override
    public int getStatementCacheNumStatementsAllUsers() throws SQLException {
        return this.getPoolManager().getStatementCacheNumStatementsAllUsers();
    }

    @Override
    public int getStatementCacheNumCheckedOutStatementsAllUsers() throws SQLException {
        return this.getPoolManager().getStatementCacheNumCheckedOutStatementsAllUsers();
    }

    @Override
    public int getStatementCacheNumConnectionsWithCachedStatementsAllUsers() throws SQLException {
        return this.getPoolManager().getStatementCacheNumConnectionsWithCachedStatementsAllUsers();
    }

    @Override
    public int getStatementDestroyerNumConnectionsInUseAllUsers() throws SQLException {
        return this.getPoolManager().getStatementDestroyerNumConnectionsInUseAllUsers();
    }

    @Override
    public int getStatementDestroyerNumConnectionsWithDeferredDestroyStatementsAllUsers() throws SQLException {
        return this.getPoolManager().getStatementDestroyerNumConnectionsWithDeferredDestroyStatementsAllUsers();
    }

    @Override
    public int getStatementDestroyerNumDeferredDestroyStatementsAllUsers() throws SQLException {
        return this.getPoolManager().getStatementDestroyerNumDeferredDestroyStatementsAllUsers();
    }

    @Override
    public int getStatementDestroyerNumConnectionsInUseDefaultUser() throws SQLException {
        return this.getPoolManager().getPool().getStatementDestroyerNumConnectionsInUse();
    }

    @Override
    public int getStatementDestroyerNumConnectionsWithDeferredDestroyStatementsDefaultUser() throws SQLException {
        return this.getPoolManager().getPool().getStatementDestroyerNumConnectionsWithDeferredDestroyStatements();
    }

    @Override
    public int getStatementDestroyerNumDeferredDestroyStatementsDefaultUser() throws SQLException {
        return this.getPoolManager().getPool().getStatementDestroyerNumDeferredDestroyStatements();
    }

    @Override
    public int getStatementDestroyerNumThreads() throws SQLException {
        return this.getPoolManager().getStatementDestroyerNumThreads();
    }

    @Override
    public int getStatementDestroyerNumActiveThreads() throws SQLException {
        return this.getPoolManager().getStatementDestroyerNumActiveThreads();
    }

    @Override
    public int getStatementDestroyerNumIdleThreads() throws SQLException {
        return this.getPoolManager().getStatementDestroyerNumIdleThreads();
    }

    @Override
    public int getStatementDestroyerNumTasksPending() throws SQLException {
        return this.getPoolManager().getStatementDestroyerNumTasksPending();
    }

    @Override
    public int getStatementDestroyerNumConnectionsInUse(String username, String password) throws SQLException {
        return this.assertAuthPool(username, password).getStatementDestroyerNumConnectionsInUse();
    }

    @Override
    public int getStatementDestroyerNumConnectionsWithDeferredDestroyStatements(String username, String password) throws SQLException {
        return this.assertAuthPool(username, password).getStatementDestroyerNumConnectionsWithDeferredDestroyStatements();
    }

    @Override
    public int getStatementDestroyerNumDeferredDestroyStatements(String username, String password) throws SQLException {
        return this.assertAuthPool(username, password).getStatementDestroyerNumDeferredDestroyStatements();
    }

    @Override
    public String sampleStatementDestroyerStackTraces() throws SQLException {
        return this.getPoolManager().getStatementDestroyerStackTraces();
    }

    @Override
    public String sampleStatementDestroyerStatus() throws SQLException {
        return this.getPoolManager().getStatementDestroyerStatus();
    }

    @Override
    public void softResetAllUsers() throws SQLException {
        this.getPoolManager().softResetAllAuths();
    }

    @Override
    public int getNumUserPools() throws SQLException {
        return this.getPoolManager().getNumManagedAuths();
    }

    @Override
    public Collection getAllUsers() throws SQLException {
        LinkedList<String> out = new LinkedList<String>();
        Set auths = this.getPoolManager().getManagedAuths();
        Iterator ii = auths.iterator();
        while (ii.hasNext()) {
            out.add(((DbAuth)ii.next()).getUser());
        }
        return Collections.unmodifiableList(out);
    }

    @Override
    public synchronized void hardReset() {
        this.resetPoolManager();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void close() {
        AbstractPoolBackedDataSource abstractPoolBackedDataSource = this;
        synchronized (abstractPoolBackedDataSource) {
            this.resetPoolManager();
            this.is_closed = true;
        }
        C3P0Registry.markClosed(this);
        if (logger.isLoggable(MLevel.FINEST)) {
            logger.log(MLevel.FINEST, this.getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + " has been closed. ", (Throwable)new Exception("DEBUG STACK TRACE for PoolBackedDataSource.close()."));
        }
    }

    @Override
    public void close(boolean force_destroy) {
        this.close();
    }

    public synchronized void resetPoolManager() {
        this.resetPoolManager(true);
    }

    public synchronized void resetPoolManager(boolean close_checked_out_connections) {
        if (this.poolManager != null) {
            this.poolManager.close(close_checked_out_connections);
            this.poolManager = null;
        }
    }

    private synchronized ConnectionPoolDataSource assertCpds() throws SQLException {
        if (this.is_closed) {
            throw new SQLException(this + " has been closed() -- you can no longer use it.");
        }
        ConnectionPoolDataSource out = this.getConnectionPoolDataSource();
        if (out == null) {
            throw new SQLException(NO_CPDS_ERR_MSG);
        }
        return out;
    }

    private synchronized C3P0PooledConnectionPoolManager getPoolManager() throws SQLException {
        if (this.poolManager == null) {
            ConnectionPoolDataSource cpds = this.assertCpds();
            this.poolManager = new C3P0PooledConnectionPoolManager(cpds, null, null, this.getNumHelperThreads(), this.getIdentityToken(), this.getDataSourceName());
            if (logger.isLoggable(MLevel.INFO)) {
                logger.info("Initializing c3p0 pool... " + this.toString(true));
            }
        }
        return this.poolManager;
    }

    private C3P0PooledConnectionPool assertAuthPool(String username, String password) throws SQLException {
        C3P0PooledConnectionPool authPool = this.getPoolManager().getPool(username, password, false);
        if (authPool == null) {
            throw new SQLException("No pool has been yet been established for Connections authenticated by user '" + username + "' with the password provided. [Use getConnection( username, password ) to initialize such a pool.]");
        }
        return authPool;
    }

    public abstract String toString(boolean var1);

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.writeShort(1);
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        short version = ois.readShort();
        switch (version) {
            case 1: {
                this.setUpPropertyEvents();
                break;
            }
            default: {
                throw new IOException("Unsupported Serialized Version: " + version);
            }
        }
    }

    protected final boolean isWrapperForThis(Class<?> iface) {
        return iface.isAssignableFrom(this.getClass());
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return this.isWrapperForThis(iface);
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (this.isWrapperForThis(iface)) {
            return (T)this;
        }
        throw new SQLException(this + " is not a wrapper for or implementation of " + iface.getName());
    }
}

