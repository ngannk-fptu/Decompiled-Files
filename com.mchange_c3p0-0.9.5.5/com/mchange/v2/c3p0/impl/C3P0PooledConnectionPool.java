/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mchange.v1.db.sql.ConnectionUtils
 *  com.mchange.v2.async.AsynchronousRunner
 *  com.mchange.v2.async.ThreadPoolAsynchronousRunner
 *  com.mchange.v2.log.MLevel
 *  com.mchange.v2.log.MLog
 *  com.mchange.v2.log.MLogger
 *  com.mchange.v2.sql.SqlUtils
 */
package com.mchange.v2.c3p0.impl;

import com.mchange.v1.db.sql.ConnectionUtils;
import com.mchange.v2.async.AsynchronousRunner;
import com.mchange.v2.async.ThreadPoolAsynchronousRunner;
import com.mchange.v2.c3p0.ConnectionCustomizer;
import com.mchange.v2.c3p0.ConnectionTester;
import com.mchange.v2.c3p0.QueryConnectionTester;
import com.mchange.v2.c3p0.SQLWarnings;
import com.mchange.v2.c3p0.UnifiedConnectionTester;
import com.mchange.v2.c3p0.WrapperConnectionPoolDataSource;
import com.mchange.v2.c3p0.impl.AbstractC3P0PooledConnection;
import com.mchange.v2.c3p0.impl.C3P0ImplUtils;
import com.mchange.v2.c3p0.impl.C3P0PooledConnection;
import com.mchange.v2.c3p0.impl.DbAuth;
import com.mchange.v2.c3p0.impl.DefaultConnectionTester;
import com.mchange.v2.c3p0.impl.NewPooledConnection;
import com.mchange.v2.c3p0.impl.WrapperConnectionPoolDataSourceBase;
import com.mchange.v2.c3p0.stmt.DoubleMaxStatementCache;
import com.mchange.v2.c3p0.stmt.GlobalMaxOnlyStatementCache;
import com.mchange.v2.c3p0.stmt.GooGooStatementCache;
import com.mchange.v2.c3p0.stmt.PerConnectionMaxOnlyStatementCache;
import com.mchange.v2.log.MLevel;
import com.mchange.v2.log.MLog;
import com.mchange.v2.log.MLogger;
import com.mchange.v2.resourcepool.CannotAcquireResourceException;
import com.mchange.v2.resourcepool.ResourcePool;
import com.mchange.v2.resourcepool.ResourcePoolException;
import com.mchange.v2.resourcepool.ResourcePoolFactory;
import com.mchange.v2.resourcepool.TimeoutException;
import com.mchange.v2.sql.SqlUtils;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.PooledConnection;

public final class C3P0PooledConnectionPool {
    private static final boolean ASYNCHRONOUS_CONNECTION_EVENT_LISTENER = false;
    private static final Throwable[] EMPTY_THROWABLE_HOLDER = new Throwable[1];
    static final MLogger logger = MLog.getLogger(C3P0PooledConnectionPool.class);
    final ResourcePool rp;
    final ConnectionEventListener cl = new ConnectionEventListenerImpl();
    final ConnectionTester connectionTester;
    final GooGooStatementCache scache;
    final boolean c3p0PooledConnections;
    final boolean effectiveStatementCache;
    final int checkoutTimeout;
    final AsynchronousRunner sharedTaskRunner;
    final AsynchronousRunner deferredStatementDestroyer;
    final ThrowableHolderPool thp = new ThrowableHolderPool();
    final InUseLockFetcher inUseLockFetcher;
    private static InUseLockFetcher RESOURCE_ITSELF_IN_USE_LOCK_FETCHER = new ResourceItselfInUseLockFetcher();
    private static InUseLockFetcher C3P0_POOLED_CONNECION_NESTED_LOCK_LOCK_FETCHER = new C3P0PooledConnectionNestedLockLockFetcher();

    public int getStatementDestroyerNumConnectionsInUse() {
        return this.scache == null ? -1 : this.scache.getStatementDestroyerNumConnectionsInUse();
    }

    public int getStatementDestroyerNumConnectionsWithDeferredDestroyStatements() {
        return this.scache == null ? -1 : this.scache.getStatementDestroyerNumConnectionsWithDeferredDestroyStatements();
    }

    public int getStatementDestroyerNumDeferredDestroyStatements() {
        return this.scache == null ? -1 : this.scache.getStatementDestroyerNumDeferredDestroyStatements();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    C3P0PooledConnectionPool(final ConnectionPoolDataSource cpds, final DbAuth auth, int min, int max, int start, int inc, int acq_retry_attempts, int acq_retry_delay, boolean break_after_acq_failure, int checkoutTimeout, int idleConnectionTestPeriod, int maxIdleTime, int maxIdleTimeExcessConnections, int maxConnectionAge, int propertyCycle, int unreturnedConnectionTimeout, boolean debugUnreturnedConnectionStackTraces, boolean forceSynchronousCheckins, final boolean testConnectionOnCheckout, final boolean testConnectionOnCheckin, int maxStatements, int maxStatementsPerConnection, final ConnectionTester connectionTester, final ConnectionCustomizer connectionCustomizer, final String testQuery, ResourcePoolFactory fact, ThreadPoolAsynchronousRunner taskRunner, ThreadPoolAsynchronousRunner deferredStatementDestroyer, final String parentDataSourceIdentityToken) throws SQLException {
        try {
            this.scache = maxStatements > 0 && maxStatementsPerConnection > 0 ? new DoubleMaxStatementCache((AsynchronousRunner)taskRunner, (AsynchronousRunner)deferredStatementDestroyer, maxStatements, maxStatementsPerConnection) : (maxStatementsPerConnection > 0 ? new PerConnectionMaxOnlyStatementCache((AsynchronousRunner)taskRunner, (AsynchronousRunner)deferredStatementDestroyer, maxStatementsPerConnection) : (maxStatements > 0 ? new GlobalMaxOnlyStatementCache((AsynchronousRunner)taskRunner, (AsynchronousRunner)deferredStatementDestroyer, maxStatements) : null));
            this.connectionTester = connectionTester;
            this.checkoutTimeout = checkoutTimeout;
            this.sharedTaskRunner = taskRunner;
            this.deferredStatementDestroyer = deferredStatementDestroyer;
            this.c3p0PooledConnections = cpds instanceof WrapperConnectionPoolDataSource;
            this.effectiveStatementCache = this.c3p0PooledConnections && this.scache != null;
            this.inUseLockFetcher = this.c3p0PooledConnections ? C3P0_POOLED_CONNECION_NESTED_LOCK_LOCK_FETCHER : RESOURCE_ITSELF_IN_USE_LOCK_FETCHER;
            class PooledConnectionResourcePoolManager
            implements ResourcePool.Manager {
                final boolean connectionTesterIsDefault;

                PooledConnectionResourcePoolManager() {
                    this.connectionTesterIsDefault = connectionTester instanceof DefaultConnectionTester;
                }

                @Override
                public Object acquireResource() throws Exception {
                    PooledConnection out;
                    if (connectionCustomizer == null) {
                        out = auth.equals(C3P0ImplUtils.NULL_AUTH) ? cpds.getPooledConnection() : cpds.getPooledConnection(auth.getUser(), auth.getPassword());
                    } else {
                        try {
                            WrapperConnectionPoolDataSourceBase wcpds = (WrapperConnectionPoolDataSourceBase)((Object)cpds);
                            out = auth.equals(C3P0ImplUtils.NULL_AUTH) ? wcpds.getPooledConnection(connectionCustomizer, parentDataSourceIdentityToken) : wcpds.getPooledConnection(auth.getUser(), auth.getPassword(), connectionCustomizer, parentDataSourceIdentityToken);
                        }
                        catch (ClassCastException e) {
                            throw SqlUtils.toSQLException((String)("Cannot use a ConnectionCustomizer with a non-c3p0 ConnectionPoolDataSource. ConnectionPoolDataSource: " + cpds.getClass().getName()), (Throwable)e);
                        }
                    }
                    try {
                        if (C3P0PooledConnectionPool.this.scache != null) {
                            if (C3P0PooledConnectionPool.this.c3p0PooledConnections) {
                                ((AbstractC3P0PooledConnection)out).initStatementCache(C3P0PooledConnectionPool.this.scache);
                            } else {
                                logger.warning("StatementPooling not implemented for external (non-c3p0) ConnectionPoolDataSources.");
                            }
                        }
                        Connection con = null;
                        try {
                            C3P0PooledConnectionPool.this.waitMarkPooledConnectionInUse(out);
                            con = out.getConnection();
                            SQLWarnings.logAndClearWarnings(con);
                        }
                        catch (Throwable throwable) {
                            ConnectionUtils.attemptClose(con);
                            C3P0PooledConnectionPool.this.unmarkPooledConnectionInUse(out);
                            throw throwable;
                        }
                        ConnectionUtils.attemptClose((Connection)con);
                        C3P0PooledConnectionPool.this.unmarkPooledConnectionInUse(out);
                        PooledConnection pooledConnection = out;
                        return pooledConnection;
                    }
                    catch (Exception e) {
                        block18: {
                            if (logger.isLoggable(MLevel.WARNING)) {
                                logger.log(MLevel.WARNING, "A PooledConnection was acquired, but an Exception occurred while preparing it for use. Attempting to destroy.", (Throwable)e);
                            }
                            try {
                                this.destroyResource(out, false);
                            }
                            catch (Exception e2) {
                                if (!logger.isLoggable(MLevel.WARNING)) break block18;
                                logger.log(MLevel.WARNING, "An Exception occurred while trying to close partially acquired PooledConnection.", (Throwable)e2);
                            }
                        }
                        throw e;
                    }
                    finally {
                        if (logger.isLoggable(MLevel.FINEST)) {
                            logger.finest(this + ".acquireResource() returning. ");
                        }
                    }
                }

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 */
                @Override
                public void refurbishResourceOnCheckout(Object resc) throws Exception {
                    Object object = C3P0PooledConnectionPool.this.inUseLockFetcher.getInUseLock(resc);
                    synchronized (object) {
                        if (connectionCustomizer != null) {
                            Connection physicalConnection = null;
                            try {
                                physicalConnection = ((AbstractC3P0PooledConnection)resc).getPhysicalConnection();
                                C3P0PooledConnectionPool.this.waitMarkPhysicalConnectionInUse(physicalConnection);
                                if (testConnectionOnCheckout) {
                                    if (logger.isLoggable(MLevel.FINER)) {
                                        this.finerLoggingTestPooledConnection(resc, "CHECKOUT");
                                    } else {
                                        this.testPooledConnection(resc);
                                    }
                                }
                                connectionCustomizer.onCheckOut(physicalConnection, parentDataSourceIdentityToken);
                            }
                            catch (ClassCastException e) {
                                throw SqlUtils.toSQLException((String)("Cannot use a ConnectionCustomizer with a non-c3p0 PooledConnection. PooledConnection: " + resc + "; ConnectionPoolDataSource: " + cpds.getClass().getName()), (Throwable)e);
                            }
                            finally {
                                C3P0PooledConnectionPool.this.unmarkPhysicalConnectionInUse(physicalConnection);
                            }
                        }
                        if (testConnectionOnCheckout) {
                            PooledConnection pc = (PooledConnection)resc;
                            try {
                                C3P0PooledConnectionPool.this.waitMarkPooledConnectionInUse(pc);
                                assert (!Boolean.FALSE.equals(C3P0PooledConnectionPool.this.pooledConnectionInUse(pc)));
                                if (logger.isLoggable(MLevel.FINER)) {
                                    this.finerLoggingTestPooledConnection(pc, "CHECKOUT");
                                } else {
                                    this.testPooledConnection(pc);
                                }
                            }
                            finally {
                                C3P0PooledConnectionPool.this.unmarkPooledConnectionInUse(pc);
                            }
                        }
                    }
                }

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 * Unable to fully structure code
                 */
                @Override
                public void refurbishResourceOnCheckin(Object resc) throws Exception {
                    proxyToClose = null;
                    try {
                        var3_3 = C3P0PooledConnectionPool.this.inUseLockFetcher.getInUseLock(resc);
                        synchronized (var3_3) {
                            if (connectionCustomizer != null) {
                                physicalConnection = null;
                                try {
                                    physicalConnection = ((AbstractC3P0PooledConnection)resc).getPhysicalConnection();
                                    C3P0PooledConnectionPool.access$400(C3P0PooledConnectionPool.this, physicalConnection);
                                    connectionCustomizer.onCheckIn(physicalConnection, parentDataSourceIdentityToken);
                                    SQLWarnings.logAndClearWarnings(physicalConnection);
                                    if (!testConnectionOnCheckin) ** GOTO lbl40
                                    if (C3P0PooledConnectionPool.logger.isLoggable(MLevel.FINER)) {
                                        this.finerLoggingTestPooledConnection(resc, "CHECKIN");
                                    }
                                    this.testPooledConnection(resc);
                                }
                                catch (ClassCastException e) {
                                    throw SqlUtils.toSQLException((String)("Cannot use a ConnectionCustomizer with a non-c3p0 PooledConnection. PooledConnection: " + resc + "; ConnectionPoolDataSource: " + cpds.getClass().getName()), (Throwable)e);
                                }
                                finally {
                                    C3P0PooledConnectionPool.access$500(C3P0PooledConnectionPool.this, physicalConnection);
                                }
                            } else {
                                pc = (PooledConnection)resc;
                                con = null;
                                try {
                                    C3P0PooledConnectionPool.access$200(C3P0PooledConnectionPool.this, pc);
                                    con = pc.getConnection();
                                    SQLWarnings.logAndClearWarnings(con);
                                    if (testConnectionOnCheckin) {
                                        if (C3P0PooledConnectionPool.logger.isLoggable(MLevel.FINER)) {
                                            this.finerLoggingTestPooledConnection(resc, con, "CHECKIN");
                                        } else {
                                            this.testPooledConnection(resc, con);
                                        }
                                    }
                                }
                                finally {
                                    proxyToClose = con;
                                    C3P0PooledConnectionPool.access$300(C3P0PooledConnectionPool.this, pc);
                                }
                            }
                        }
                    }
                    catch (Throwable var9_11) {
                        ConnectionUtils.attemptClose(proxyToClose);
                        throw var9_11;
                    }
                    ConnectionUtils.attemptClose((Connection)proxyToClose);
                }

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 */
                @Override
                public void refurbishIdleResource(Object resc) throws Exception {
                    Object object = C3P0PooledConnectionPool.this.inUseLockFetcher.getInUseLock(resc);
                    synchronized (object) {
                        PooledConnection pc = (PooledConnection)resc;
                        try {
                            C3P0PooledConnectionPool.this.waitMarkPooledConnectionInUse(pc);
                            if (logger.isLoggable(MLevel.FINER)) {
                                this.finerLoggingTestPooledConnection(resc, "IDLE CHECK");
                            } else {
                                this.testPooledConnection(resc);
                            }
                        }
                        finally {
                            C3P0PooledConnectionPool.this.unmarkPooledConnectionInUse(pc);
                        }
                    }
                }

                private void finerLoggingTestPooledConnection(Object resc, String testImpetus) throws Exception {
                    this.finerLoggingTestPooledConnection(resc, null, testImpetus);
                }

                private void finerLoggingTestPooledConnection(Object resc, Connection proxyConn, String testImpetus) throws Exception {
                    logger.finer("Testing PooledConnection [" + resc + "] on " + testImpetus + ".");
                    try {
                        this.testPooledConnection(resc, proxyConn);
                        logger.finer("Test of PooledConnection [" + resc + "] on " + testImpetus + " has SUCCEEDED.");
                    }
                    catch (Exception e) {
                        logger.log(MLevel.FINER, "Test of PooledConnection [" + resc + "] on " + testImpetus + " has FAILED.", (Throwable)e);
                        e.fillInStackTrace();
                        throw e;
                    }
                }

                private void testPooledConnection(Object resc) throws Exception {
                    this.testPooledConnection(resc, null);
                }

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 */
                private void testPooledConnection(Object resc, Connection proxyConn) throws Exception {
                    int status;
                    PooledConnection pc = (PooledConnection)resc;
                    assert (!Boolean.FALSE.equals(C3P0PooledConnectionPool.this.pooledConnectionInUse(pc)));
                    Throwable[] throwableHolder = EMPTY_THROWABLE_HOLDER;
                    Connection openedConn = null;
                    Throwable rootCause = null;
                    try {
                        Connection testConn;
                        if (C3P0PooledConnectionPool.this.scache != null) {
                            testConn = testQuery == null && this.connectionTesterIsDefault && C3P0PooledConnectionPool.this.c3p0PooledConnections ? ((AbstractC3P0PooledConnection)pc).getPhysicalConnection() : (proxyConn == null ? (openedConn = pc.getConnection()) : proxyConn);
                        } else if (C3P0PooledConnectionPool.this.c3p0PooledConnections) {
                            testConn = ((AbstractC3P0PooledConnection)pc).getPhysicalConnection();
                        } else {
                            Connection connection = testConn = proxyConn == null ? (openedConn = pc.getConnection()) : proxyConn;
                        }
                        if (testQuery == null) {
                            status = connectionTester.activeCheckConnection(testConn);
                        } else if (connectionTester instanceof UnifiedConnectionTester) {
                            throwableHolder = C3P0PooledConnectionPool.this.thp.getThrowableHolder();
                            status = ((UnifiedConnectionTester)connectionTester).activeCheckConnection(testConn, testQuery, throwableHolder);
                        } else if (connectionTester instanceof QueryConnectionTester) {
                            status = ((QueryConnectionTester)connectionTester).activeCheckConnection(testConn, testQuery);
                        } else {
                            logger.warning("[c3p0] testQuery '" + testQuery + "' ignored. Please set a ConnectionTester that implements com.mchange.v2.c3p0.QueryConnectionTester, or use the DefaultConnectionTester, to test with the testQuery.");
                            status = connectionTester.activeCheckConnection(testConn);
                        }
                    }
                    catch (Exception e) {
                        logger.log(MLevel.FINE, "A Connection test failed with an Exception.", (Throwable)e);
                        status = -1;
                        rootCause = e;
                    }
                    finally {
                        if (rootCause == null) {
                            rootCause = throwableHolder[0];
                        } else if (throwableHolder[0] != null && logger.isLoggable(MLevel.FINE)) {
                            logger.log(MLevel.FINE, "Internal Connection Test Exception", throwableHolder[0]);
                        }
                        if (throwableHolder != EMPTY_THROWABLE_HOLDER) {
                            C3P0PooledConnectionPool.this.thp.returnThrowableHolder(throwableHolder);
                        }
                        ConnectionUtils.attemptClose((Connection)openedConn);
                    }
                    switch (status) {
                        case 0: {
                            break;
                        }
                        case -8: {
                            C3P0PooledConnectionPool.this.rp.resetPool();
                        }
                        case -1: {
                            SQLException throwMe = rootCause == null ? new SQLException("Connection is invalid") : SqlUtils.toSQLException((String)"Connection is invalid", (Throwable)rootCause);
                            throw throwMe;
                        }
                        default: {
                            throw new Error("Bad Connection Tester (" + connectionTester + ") returned invalid status (" + status + ").");
                        }
                    }
                }

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 */
                @Override
                public void destroyResource(Object resc, boolean checked_out) throws Exception {
                    Object object = C3P0PooledConnectionPool.this.inUseLockFetcher.getInUseLock(resc);
                    synchronized (object) {
                        try {
                            block17: {
                                C3P0PooledConnectionPool.this.waitMarkPooledConnectionInUse((PooledConnection)resc);
                                if (connectionCustomizer != null) {
                                    Connection physicalConnection = null;
                                    try {
                                        physicalConnection = ((AbstractC3P0PooledConnection)resc).getPhysicalConnection();
                                        connectionCustomizer.onDestroy(physicalConnection, parentDataSourceIdentityToken);
                                    }
                                    catch (ClassCastException e) {
                                        throw SqlUtils.toSQLException((String)("Cannot use a ConnectionCustomizer with a non-c3p0 PooledConnection. PooledConnection: " + resc + "; ConnectionPoolDataSource: " + cpds.getClass().getName()), (Throwable)e);
                                    }
                                    catch (Exception e) {
                                        if (!logger.isLoggable(MLevel.WARNING)) break block17;
                                        logger.log(MLevel.WARNING, "An exception occurred while executing the onDestroy() method of " + connectionCustomizer + ". c3p0 will attempt to destroy the target Connection regardless, but this issue  should be investigated and fixed.", (Throwable)e);
                                    }
                                }
                            }
                            if (logger.isLoggable(MLevel.FINER)) {
                                logger.log(MLevel.FINER, "Preparing to destroy PooledConnection: " + resc);
                            }
                            if (C3P0PooledConnectionPool.this.c3p0PooledConnections) {
                                ((AbstractC3P0PooledConnection)resc).closeMaybeCheckedOut(checked_out);
                            } else {
                                ((PooledConnection)resc).close();
                            }
                            if (logger.isLoggable(MLevel.FINER)) {
                                logger.log(MLevel.FINER, "Successfully destroyed PooledConnection: " + resc);
                            }
                        }
                        catch (Exception e) {
                            if (logger.isLoggable(MLevel.FINER)) {
                                logger.log(MLevel.FINER, "Failed to destroy PooledConnection: " + resc);
                            }
                            throw e;
                        }
                        finally {
                            C3P0PooledConnectionPool.this.unmarkPooledConnectionInUse((PooledConnection)resc);
                        }
                    }
                }
            }
            PooledConnectionResourcePoolManager manager = new PooledConnectionResourcePoolManager();
            ResourcePoolFactory resourcePoolFactory = fact;
            synchronized (resourcePoolFactory) {
                fact.setMin(min);
                fact.setMax(max);
                fact.setStart(start);
                fact.setIncrement(inc);
                fact.setIdleResourceTestPeriod(idleConnectionTestPeriod * 1000);
                fact.setResourceMaxIdleTime(maxIdleTime * 1000);
                fact.setExcessResourceMaxIdleTime(maxIdleTimeExcessConnections * 1000);
                fact.setResourceMaxAge(maxConnectionAge * 1000);
                fact.setExpirationEnforcementDelay(propertyCycle * 1000);
                fact.setDestroyOverdueResourceTime(unreturnedConnectionTimeout * 1000);
                fact.setDebugStoreCheckoutStackTrace(debugUnreturnedConnectionStackTraces);
                fact.setForceSynchronousCheckins(forceSynchronousCheckins);
                fact.setAcquisitionRetryAttempts(acq_retry_attempts);
                fact.setAcquisitionRetryDelay(acq_retry_delay);
                fact.setBreakOnAcquisitionFailure(break_after_acq_failure);
                this.rp = fact.createPool(manager);
            }
        }
        catch (ResourcePoolException e) {
            throw SqlUtils.toSQLException((Throwable)((Object)e));
        }
    }

    public PooledConnection checkoutPooledConnection() throws SQLException {
        try {
            PooledConnection pc = (PooledConnection)this.checkoutAndMarkConnectionInUse();
            pc.addConnectionEventListener(this.cl);
            return pc;
        }
        catch (TimeoutException e) {
            throw SqlUtils.toSQLException((String)"An attempt by a client to checkout a Connection has timed out.", (Throwable)((Object)e));
        }
        catch (CannotAcquireResourceException e) {
            throw SqlUtils.toSQLException((String)"Connections could not be acquired from the underlying database!", (String)"08001", (Throwable)((Object)e));
        }
        catch (Exception e) {
            throw SqlUtils.toSQLException((Throwable)e);
        }
    }

    private void waitMarkPhysicalConnectionInUse(Connection physicalConnection) throws InterruptedException {
        if (this.effectiveStatementCache) {
            this.scache.waitMarkConnectionInUse(physicalConnection);
        }
    }

    private boolean tryMarkPhysicalConnectionInUse(Connection physicalConnection) {
        return this.effectiveStatementCache ? this.scache.tryMarkConnectionInUse(physicalConnection) : true;
    }

    private void unmarkPhysicalConnectionInUse(Connection physicalConnection) {
        if (this.effectiveStatementCache) {
            this.scache.unmarkConnectionInUse(physicalConnection);
        }
    }

    private void waitMarkPooledConnectionInUse(PooledConnection pooledCon) throws InterruptedException {
        if (this.c3p0PooledConnections) {
            this.waitMarkPhysicalConnectionInUse(((AbstractC3P0PooledConnection)pooledCon).getPhysicalConnection());
        }
    }

    private boolean tryMarkPooledConnectionInUse(PooledConnection pooledCon) {
        if (this.c3p0PooledConnections) {
            return this.tryMarkPhysicalConnectionInUse(((AbstractC3P0PooledConnection)pooledCon).getPhysicalConnection());
        }
        return true;
    }

    private void unmarkPooledConnectionInUse(PooledConnection pooledCon) {
        if (this.c3p0PooledConnections) {
            this.unmarkPhysicalConnectionInUse(((AbstractC3P0PooledConnection)pooledCon).getPhysicalConnection());
        }
    }

    private Boolean physicalConnectionInUse(Connection physicalConnection) throws InterruptedException {
        if (physicalConnection != null && this.effectiveStatementCache) {
            return this.scache.inUse(physicalConnection);
        }
        return null;
    }

    private Boolean pooledConnectionInUse(PooledConnection pc) throws InterruptedException {
        if (pc != null && this.effectiveStatementCache) {
            return this.scache.inUse(((AbstractC3P0PooledConnection)pc).getPhysicalConnection());
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Object checkoutAndMarkConnectionInUse() throws TimeoutException, CannotAcquireResourceException, ResourcePoolException, InterruptedException {
        Object out = null;
        boolean success = false;
        while (!success) {
            try {
                out = this.rp.checkoutResource(this.checkoutTimeout);
                if (out instanceof AbstractC3P0PooledConnection) {
                    AbstractC3P0PooledConnection acpc = (AbstractC3P0PooledConnection)out;
                    Connection physicalConnection = acpc.getPhysicalConnection();
                    success = this.tryMarkPhysicalConnectionInUse(physicalConnection);
                    continue;
                }
                success = true;
            }
            finally {
                try {
                    if (success || out == null) continue;
                    this.rp.checkinResource(out);
                }
                catch (Exception e) {
                    logger.log(MLevel.WARNING, "Failed to check in a Connection that was unusable due to pending Statement closes.", (Throwable)e);
                }
            }
        }
        return out;
    }

    private void unmarkConnectionInUseAndCheckin(PooledConnection pcon) throws ResourcePoolException {
        block3: {
            if (this.effectiveStatementCache) {
                try {
                    AbstractC3P0PooledConnection acpc = (AbstractC3P0PooledConnection)pcon;
                    Connection physicalConnection = acpc.getPhysicalConnection();
                    this.unmarkPhysicalConnectionInUse(physicalConnection);
                }
                catch (ClassCastException e) {
                    if (!logger.isLoggable(MLevel.SEVERE)) break block3;
                    logger.log(MLevel.SEVERE, "You are checking a non-c3p0 PooledConnection implementation intoa c3p0 PooledConnectionPool instance that expects only c3p0-generated PooledConnections.This isn't good, and may indicate a c3p0 bug, or an unusual (and unspported) use of the c3p0 library.", (Throwable)e);
                }
            }
        }
        this.rp.checkinResource(pcon);
    }

    public void checkinPooledConnection(PooledConnection pcon) throws SQLException {
        try {
            pcon.removeConnectionEventListener(this.cl);
            this.unmarkConnectionInUseAndCheckin(pcon);
        }
        catch (ResourcePoolException e) {
            throw SqlUtils.toSQLException((Throwable)((Object)e));
        }
    }

    public float getEffectivePropertyCycle() throws SQLException {
        try {
            return (float)this.rp.getEffectiveExpirationEnforcementDelay() / 1000.0f;
        }
        catch (ResourcePoolException e) {
            throw SqlUtils.toSQLException((Throwable)((Object)e));
        }
    }

    public int getNumThreadsAwaitingCheckout() throws SQLException {
        try {
            return this.rp.getNumCheckoutWaiters();
        }
        catch (ResourcePoolException e) {
            throw SqlUtils.toSQLException((Throwable)((Object)e));
        }
    }

    public int getStatementCacheNumStatements() {
        return this.scache == null ? 0 : this.scache.getNumStatements();
    }

    public int getStatementCacheNumCheckedOut() {
        return this.scache == null ? 0 : this.scache.getNumStatementsCheckedOut();
    }

    public int getStatementCacheNumConnectionsWithCachedStatements() {
        return this.scache == null ? 0 : this.scache.getNumConnectionsWithCachedStatements();
    }

    public String dumpStatementCacheStatus() {
        return this.scache == null ? "Statement caching disabled." : this.scache.dumpStatementCacheStatus();
    }

    public void close() throws SQLException {
        this.close(true);
    }

    public void close(boolean close_outstanding_connections) throws SQLException {
        Object throwMe = null;
        try {
            if (this.scache != null) {
                this.scache.close();
            }
        }
        catch (SQLException e) {
            throwMe = e;
        }
        try {
            this.rp.close(close_outstanding_connections);
        }
        catch (ResourcePoolException e) {
            if (throwMe != null && logger.isLoggable(MLevel.WARNING)) {
                logger.log(MLevel.WARNING, "An Exception occurred while closing the StatementCache.", (Throwable)throwMe);
            }
            throwMe = e;
        }
        if (throwMe != null) {
            throw SqlUtils.toSQLException((Throwable)throwMe);
        }
    }

    public int getNumConnections() throws SQLException {
        try {
            return this.rp.getPoolSize();
        }
        catch (Exception e) {
            logger.log(MLevel.WARNING, null, (Throwable)e);
            throw SqlUtils.toSQLException((Throwable)e);
        }
    }

    public int getNumIdleConnections() throws SQLException {
        try {
            return this.rp.getAvailableCount();
        }
        catch (Exception e) {
            logger.log(MLevel.WARNING, null, (Throwable)e);
            throw SqlUtils.toSQLException((Throwable)e);
        }
    }

    public int getNumBusyConnections() throws SQLException {
        try {
            return this.rp.getAwaitingCheckinNotExcludedCount();
        }
        catch (Exception e) {
            logger.log(MLevel.WARNING, null, (Throwable)e);
            throw SqlUtils.toSQLException((Throwable)e);
        }
    }

    public int getNumUnclosedOrphanedConnections() throws SQLException {
        try {
            return this.rp.getExcludedCount();
        }
        catch (Exception e) {
            logger.log(MLevel.WARNING, null, (Throwable)e);
            throw SqlUtils.toSQLException((Throwable)e);
        }
    }

    public long getStartTime() throws SQLException {
        try {
            return this.rp.getStartTime();
        }
        catch (Exception e) {
            logger.log(MLevel.WARNING, null, (Throwable)e);
            throw SqlUtils.toSQLException((Throwable)e);
        }
    }

    public long getUpTime() throws SQLException {
        try {
            return this.rp.getUpTime();
        }
        catch (Exception e) {
            logger.log(MLevel.WARNING, null, (Throwable)e);
            throw SqlUtils.toSQLException((Throwable)e);
        }
    }

    public long getNumFailedCheckins() throws SQLException {
        try {
            return this.rp.getNumFailedCheckins();
        }
        catch (Exception e) {
            logger.log(MLevel.WARNING, null, (Throwable)e);
            throw SqlUtils.toSQLException((Throwable)e);
        }
    }

    public long getNumFailedCheckouts() throws SQLException {
        try {
            return this.rp.getNumFailedCheckouts();
        }
        catch (Exception e) {
            logger.log(MLevel.WARNING, null, (Throwable)e);
            throw SqlUtils.toSQLException((Throwable)e);
        }
    }

    public long getNumFailedIdleTests() throws SQLException {
        try {
            return this.rp.getNumFailedIdleTests();
        }
        catch (Exception e) {
            logger.log(MLevel.WARNING, null, (Throwable)e);
            throw SqlUtils.toSQLException((Throwable)e);
        }
    }

    public Throwable getLastCheckinFailure() throws SQLException {
        try {
            return this.rp.getLastCheckinFailure();
        }
        catch (Exception e) {
            logger.log(MLevel.WARNING, null, (Throwable)e);
            throw SqlUtils.toSQLException((Throwable)e);
        }
    }

    public Throwable getLastCheckoutFailure() throws SQLException {
        try {
            return this.rp.getLastCheckoutFailure();
        }
        catch (Exception e) {
            logger.log(MLevel.WARNING, null, (Throwable)e);
            throw SqlUtils.toSQLException((Throwable)e);
        }
    }

    public Throwable getLastIdleTestFailure() throws SQLException {
        try {
            return this.rp.getLastIdleCheckFailure();
        }
        catch (Exception e) {
            logger.log(MLevel.WARNING, null, (Throwable)e);
            throw SqlUtils.toSQLException((Throwable)e);
        }
    }

    public Throwable getLastConnectionTestFailure() throws SQLException {
        try {
            return this.rp.getLastResourceTestFailure();
        }
        catch (Exception e) {
            logger.log(MLevel.WARNING, null, (Throwable)e);
            throw SqlUtils.toSQLException((Throwable)e);
        }
    }

    public Throwable getLastAcquisitionFailure() throws SQLException {
        try {
            return this.rp.getLastAcquisitionFailure();
        }
        catch (Exception e) {
            logger.log(MLevel.WARNING, null, (Throwable)e);
            throw SqlUtils.toSQLException((Throwable)e);
        }
    }

    public void reset() throws SQLException {
        try {
            this.rp.resetPool();
        }
        catch (Exception e) {
            logger.log(MLevel.WARNING, null, (Throwable)e);
            throw SqlUtils.toSQLException((Throwable)e);
        }
    }

    static final class ThrowableHolderPool {
        LinkedList l = new LinkedList();

        ThrowableHolderPool() {
        }

        synchronized Throwable[] getThrowableHolder() {
            if (this.l.size() == 0) {
                return new Throwable[1];
            }
            return (Throwable[])this.l.remove(0);
        }

        synchronized void returnThrowableHolder(Throwable[] th) {
            th[0] = null;
            this.l.add(th);
        }
    }

    class ConnectionEventListenerImpl
    implements ConnectionEventListener {
        ConnectionEventListenerImpl() {
        }

        @Override
        public void connectionClosed(ConnectionEvent evt) {
            this.doCheckinResource(evt);
        }

        private void doCheckinResource(ConnectionEvent evt) {
            try {
                C3P0PooledConnectionPool.this.checkinPooledConnection((PooledConnection)evt.getSource());
            }
            catch (Exception e) {
                logger.log(MLevel.WARNING, "An Exception occurred while trying to check a PooledConection into a ResourcePool.", (Throwable)e);
            }
        }

        @Override
        public void connectionErrorOccurred(ConnectionEvent evt) {
            PooledConnection pc;
            if (logger.isLoggable(MLevel.FINE)) {
                logger.fine("CONNECTION ERROR OCCURRED!");
            }
            int status = (pc = (PooledConnection)evt.getSource()) instanceof C3P0PooledConnection ? ((C3P0PooledConnection)pc).getConnectionStatus() : (pc instanceof NewPooledConnection ? ((NewPooledConnection)pc).getConnectionStatus() : -1);
            int final_status = status;
            this.doMarkPoolStatus(pc, final_status);
        }

        private void doMarkPoolStatus(PooledConnection pc, int status) {
            try {
                switch (status) {
                    case 0: {
                        throw new RuntimeException("connectionErrorOcccurred() should only be called for errors fatal to the Connection.");
                    }
                    case -1: {
                        C3P0PooledConnectionPool.this.rp.markBroken(pc);
                        break;
                    }
                    case -8: {
                        if (logger.isLoggable(MLevel.WARNING)) {
                            logger.warning("A ConnectionTest has failed, reporting that all previously acquired Connections are likely invalid. The pool will be reset.");
                        }
                        C3P0PooledConnectionPool.this.rp.resetPool();
                        break;
                    }
                    default: {
                        throw new RuntimeException("Bad Connection Tester (" + C3P0PooledConnectionPool.this.connectionTester + ") returned invalid status (" + status + ").");
                    }
                }
            }
            catch (ResourcePoolException e) {
                logger.log(MLevel.WARNING, "Uh oh... our resource pool is probably broken!", (Throwable)((Object)e));
            }
        }
    }

    private static class C3P0PooledConnectionNestedLockLockFetcher
    implements InUseLockFetcher {
        private C3P0PooledConnectionNestedLockLockFetcher() {
        }

        @Override
        public Object getInUseLock(Object resc) {
            return ((AbstractC3P0PooledConnection)resc).inInternalUseLock;
        }
    }

    private static class ResourceItselfInUseLockFetcher
    implements InUseLockFetcher {
        private ResourceItselfInUseLockFetcher() {
        }

        @Override
        public Object getInUseLock(Object resc) {
            return resc;
        }
    }

    private static interface InUseLockFetcher {
        public Object getInUseLock(Object var1);
    }
}

