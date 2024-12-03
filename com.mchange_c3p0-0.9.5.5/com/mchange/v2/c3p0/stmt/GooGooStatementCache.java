/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mchange.v1.db.sql.StatementUtils
 *  com.mchange.v2.async.AsynchronousRunner
 *  com.mchange.v2.io.IndentedWriter
 *  com.mchange.v2.log.MLevel
 *  com.mchange.v2.log.MLog
 *  com.mchange.v2.log.MLogger
 *  com.mchange.v2.sql.SqlUtils
 *  com.mchange.v2.util.ResourceClosedException
 */
package com.mchange.v2.c3p0.stmt;

import com.mchange.v1.db.sql.StatementUtils;
import com.mchange.v2.async.AsynchronousRunner;
import com.mchange.v2.c3p0.stmt.StatementCacheKey;
import com.mchange.v2.io.IndentedWriter;
import com.mchange.v2.log.MLevel;
import com.mchange.v2.log.MLog;
import com.mchange.v2.log.MLogger;
import com.mchange.v2.sql.SqlUtils;
import com.mchange.v2.util.ResourceClosedException;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public abstract class GooGooStatementCache {
    private static final MLogger logger = MLog.getLogger(GooGooStatementCache.class);
    private static final int DESTROY_NEVER = 0;
    private static final int DESTROY_IF_CHECKED_IN = 1;
    private static final int DESTROY_IF_CHECKED_OUT = 2;
    private static final int DESTROY_ALWAYS = 3;
    private static final boolean CULL_ONLY_FROM_UNUSED_CONNECTIONS = false;
    ConnectionStatementManager cxnStmtMgr;
    HashMap stmtToKey = new HashMap();
    HashMap keyToKeyRec = new HashMap();
    HashSet checkedOut = new HashSet();
    AsynchronousRunner blockingTaskAsyncRunner;
    HashSet removalPending = new HashSet();
    StatementDestructionManager destructo;

    public GooGooStatementCache(AsynchronousRunner blockingTaskAsyncRunner, AsynchronousRunner deferredStatementDestroyer) {
        this.blockingTaskAsyncRunner = blockingTaskAsyncRunner;
        this.cxnStmtMgr = this.createConnectionStatementManager();
        this.destructo = deferredStatementDestroyer != null ? new CautiousStatementDestructionManager(deferredStatementDestroyer) : new IncautiousStatementDestructionManager(blockingTaskAsyncRunner);
    }

    public synchronized int getNumStatements() {
        return this.isClosed() ? -1 : this.countCachedStatements();
    }

    public synchronized int getNumStatementsCheckedOut() {
        return this.isClosed() ? -1 : this.checkedOut.size();
    }

    public synchronized int getNumConnectionsWithCachedStatements() {
        return this.isClosed() ? -1 : this.cxnStmtMgr.getNumConnectionsWithCachedStatements();
    }

    public synchronized String dumpStatementCacheStatus() {
        if (this.isClosed()) {
            return this + "status: Closed.";
        }
        StringWriter sw = new StringWriter(2048);
        IndentedWriter iw = new IndentedWriter((Writer)sw);
        try {
            iw.print((Object)this);
            iw.println(" status:");
            iw.upIndent();
            iw.println("core stats:");
            iw.upIndent();
            iw.print("num cached statements: ");
            iw.println(this.countCachedStatements());
            iw.print("num cached statements in use: ");
            iw.println(this.checkedOut.size());
            iw.print("num connections with cached statements: ");
            iw.println(this.cxnStmtMgr.getNumConnectionsWithCachedStatements());
            iw.downIndent();
            iw.println("cached statement dump:");
            iw.upIndent();
            for (Connection pcon : this.cxnStmtMgr.connectionSet()) {
                iw.print((Object)pcon);
                iw.println(':');
                iw.upIndent();
                Iterator jj = this.cxnStmtMgr.statementSet(pcon).iterator();
                while (jj.hasNext()) {
                    iw.println(jj.next());
                }
                iw.downIndent();
            }
            iw.downIndent();
            iw.downIndent();
            return sw.toString();
        }
        catch (IOException e) {
            if (logger.isLoggable(MLevel.SEVERE)) {
                logger.log(MLevel.SEVERE, "Huh? We've seen an IOException writing to s StringWriter?!", (Throwable)e);
            }
            return e.toString();
        }
    }

    public void waitMarkConnectionInUse(Connection physicalConnection) throws InterruptedException {
        this.destructo.waitMarkConnectionInUse(physicalConnection);
    }

    public boolean tryMarkConnectionInUse(Connection physicalConnection) {
        return this.destructo.tryMarkConnectionInUse(physicalConnection);
    }

    public void unmarkConnectionInUse(Connection physicalConnection) {
        this.destructo.unmarkConnectionInUse(physicalConnection);
    }

    public Boolean inUse(Connection physicalConnection) {
        return this.destructo.tvlInUse(physicalConnection);
    }

    public int getStatementDestroyerNumConnectionsInUse() {
        return this.destructo.getNumConnectionsInUse();
    }

    public int getStatementDestroyerNumConnectionsWithDeferredDestroyStatements() {
        return this.destructo.getNumConnectionsWithDeferredDestroyStatements();
    }

    public int getStatementDestroyerNumDeferredDestroyStatements() {
        return this.destructo.getNumDeferredDestroyStatements();
    }

    abstract ConnectionStatementManager createConnectionStatementManager();

    public synchronized Object checkoutStatement(Connection physicalConnection, Method stmtProducingMethod, Object[] args) throws SQLException, ResourceClosedException {
        try {
            Object out = null;
            StatementCacheKey key = StatementCacheKey.find(physicalConnection, stmtProducingMethod, args);
            LinkedList l = this.checkoutQueue(key);
            if (l == null || l.isEmpty()) {
                out = this.acquireStatement(physicalConnection, stmtProducingMethod, args);
                if (this.prepareAssimilateNewStatement(physicalConnection)) {
                    this.assimilateNewCheckedOutStatement(key, physicalConnection, out);
                }
            } else {
                logger.finest(this.getClass().getName() + " ----> CACHE HIT");
                out = l.get(0);
                l.remove(0);
                if (!this.checkedOut.add(out)) {
                    throw new RuntimeException("Internal inconsistency: Checking out a statement marked as already checked out!");
                }
                this.removeStatementFromDeathmarches(out, physicalConnection);
            }
            if (logger.isLoggable(MLevel.FINEST)) {
                logger.finest("checkoutStatement: " + this.statsString());
            }
            return out;
        }
        catch (NullPointerException npe) {
            if (this.checkedOut == null) {
                if (logger.isLoggable(MLevel.FINE)) {
                    logger.log(MLevel.FINE, "A client attempted to work with a closed Statement cache, provoking a NullPointerException. c3p0 recovers, but this should be rare.", (Throwable)npe);
                }
                throw new ResourceClosedException((Throwable)npe);
            }
            throw npe;
        }
    }

    public synchronized void checkinStatement(Object pstmt) throws SQLException {
        if (this.checkedOut == null) {
            this.destructo.synchronousDestroyStatement(pstmt);
            return;
        }
        if (!this.checkedOut.remove(pstmt)) {
            if (!this.ourResource(pstmt)) {
                this.destructo.uncheckedDestroyStatement(pstmt);
            }
            return;
        }
        try {
            this.refreshStatement((PreparedStatement)pstmt);
        }
        catch (Exception e) {
            if (logger.isLoggable(MLevel.INFO)) {
                logger.log(MLevel.INFO, "Problem with checked-in Statement, discarding.", (Throwable)e);
            }
            this.checkedOut.add(pstmt);
            this.removeStatement(pstmt, 3);
            return;
        }
        StatementCacheKey key = (StatementCacheKey)this.stmtToKey.get(pstmt);
        if (key == null) {
            throw new RuntimeException("Internal inconsistency: A checked-out statement has no key associated with it!");
        }
        LinkedList l = this.checkoutQueue(key);
        l.add(pstmt);
        this.addStatementToDeathmarches(pstmt, key.physicalConnection);
        if (logger.isLoggable(MLevel.FINEST)) {
            logger.finest("checkinStatement(): " + this.statsString());
        }
    }

    public synchronized void checkinAll(Connection pcon) throws SQLException {
        Set stmtSet = this.cxnStmtMgr.statementSet(pcon);
        if (stmtSet != null) {
            for (Object stmt : stmtSet) {
                if (!this.checkedOut.contains(stmt)) continue;
                this.checkinStatement(stmt);
            }
        }
        if (logger.isLoggable(MLevel.FINEST)) {
            logger.log(MLevel.FINEST, "checkinAll(): " + this.statsString());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void closeAll(Connection pcon) throws SQLException {
        if (!this.isClosed()) {
            if (logger.isLoggable(MLevel.FINEST)) {
                logger.log(MLevel.FINEST, "ENTER METHOD: closeAll( " + pcon + " )! -- num_connections: " + this.cxnStmtMgr.getNumConnectionsWithCachedStatements());
            }
            HashSet stmtSet = null;
            GooGooStatementCache gooGooStatementCache = this;
            synchronized (gooGooStatementCache) {
                Set cSet = this.cxnStmtMgr.statementSet(pcon);
                if (cSet != null) {
                    stmtSet = new HashSet(cSet);
                    for (Object stmt : stmtSet) {
                        this.removeStatement(stmt, 0);
                    }
                }
            }
            if (stmtSet != null) {
                for (Object stmt : stmtSet) {
                    this.destructo.synchronousDestroyStatement(stmt);
                }
            }
            if (logger.isLoggable(MLevel.FINEST)) {
                logger.finest("closeAll(): " + this.statsString());
            }
        }
    }

    public synchronized void close() throws SQLException {
        if (!this.isClosed()) {
            Iterator ii = this.stmtToKey.keySet().iterator();
            while (ii.hasNext()) {
                this.destructo.synchronousDestroyStatement(ii.next());
            }
            this.destructo.close();
            this.cxnStmtMgr = null;
            this.stmtToKey = null;
            this.keyToKeyRec = null;
            this.checkedOut = null;
        } else if (logger.isLoggable(MLevel.FINE)) {
            logger.log(MLevel.FINE, this + ": duplicate call to close() [not harmful! -- debug only!]", (Throwable)new Exception("DUPLICATE CLOSE DEBUG STACK TRACE."));
        }
    }

    public synchronized boolean isClosed() {
        return this.cxnStmtMgr == null;
    }

    abstract boolean prepareAssimilateNewStatement(Connection var1);

    abstract void addStatementToDeathmarches(Object var1, Connection var2);

    abstract void removeStatementFromDeathmarches(Object var1, Connection var2);

    final int countCachedStatements() {
        return this.stmtToKey.size();
    }

    private void assimilateNewCheckedOutStatement(StatementCacheKey key, Connection pConn, Object ps) {
        this.stmtToKey.put(ps, key);
        HashSet ks = this.keySet(key);
        if (ks == null) {
            this.keyToKeyRec.put(key, new KeyRec());
        } else {
            if (logger.isLoggable(MLevel.INFO)) {
                logger.info("Multiply-cached PreparedStatement: " + key.stmtText);
            }
            if (logger.isLoggable(MLevel.FINE)) {
                logger.fine("(The same statement has already been prepared by this Connection, and that other instance has not yet been closed, so the statement pool has to prepare a second PreparedStatement object rather than reusing the previously-cached Statement. The new Statement will be cached, in case you frequently need multiple copies of this Statement.)");
            }
        }
        this.keySet(key).add(ps);
        this.cxnStmtMgr.addStatementForConnection(ps, pConn);
        if (logger.isLoggable(MLevel.FINEST)) {
            logger.finest("cxnStmtMgr.statementSet( " + pConn + " ).size(): " + this.cxnStmtMgr.statementSet(pConn).size());
        }
        this.checkedOut.add(ps);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void removeStatement(Object ps, int destruction_policy) {
        boolean check;
        boolean checked_in;
        HashSet hashSet = this.removalPending;
        synchronized (hashSet) {
            if (this.removalPending.contains(ps)) {
                return;
            }
            this.removalPending.add(ps);
        }
        StatementCacheKey sck = (StatementCacheKey)this.stmtToKey.remove(ps);
        this.removeFromKeySet(sck, ps);
        Connection pConn = sck.physicalConnection;
        boolean bl = checked_in = !this.checkedOut.contains(ps);
        if (checked_in) {
            this.removeStatementFromDeathmarches(ps, pConn);
            this.removeFromCheckoutQueue(sck, ps);
            if ((destruction_policy & 1) != 0) {
                this.destructo.deferredDestroyStatement(pConn, ps);
            }
        } else {
            this.checkedOut.remove(ps);
            if ((destruction_policy & 2) != 0) {
                this.destructo.deferredDestroyStatement(pConn, ps);
            }
        }
        if (!(check = this.cxnStmtMgr.removeStatementForConnection(ps, pConn)) && logger.isLoggable(MLevel.WARNING)) {
            logger.log(MLevel.WARNING, this + " removed a statement that apparently wasn't in a statement set!!!", (Throwable)new Exception("LOG STACK TRACE"));
        }
        HashSet hashSet2 = this.removalPending;
        synchronized (hashSet2) {
            this.removalPending.remove(ps);
        }
    }

    private Object acquireStatement(final Connection pConn, final Method stmtProducingMethod, final Object[] args) throws SQLException {
        try {
            final Object[] outHolder = new Object[1];
            final Throwable[] exceptionHolder = new Throwable[1];
            class StmtAcquireTask
            implements Runnable {
                StmtAcquireTask() {
                }

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 */
                @Override
                public void run() {
                    try {
                        outHolder[0] = stmtProducingMethod.invoke((Object)pConn, args);
                    }
                    catch (InvocationTargetException e) {
                        Throwable targetException;
                        exceptionHolder[0] = targetException = e.getTargetException();
                    }
                    catch (Exception e) {
                        exceptionHolder[0] = e;
                    }
                    finally {
                        GooGooStatementCache e = GooGooStatementCache.this;
                        synchronized (e) {
                            GooGooStatementCache.this.notifyAll();
                        }
                    }
                }
            }
            StmtAcquireTask r = new StmtAcquireTask();
            this.blockingTaskAsyncRunner.postRunnable((Runnable)r);
            while (outHolder[0] == null && exceptionHolder[0] == null) {
                this.wait();
            }
            if (exceptionHolder[0] != null) {
                throw new SQLException("A problem occurred while trying to acquire a cached PreparedStatement in a background thread.", exceptionHolder[0]);
            }
            Object out = outHolder[0];
            return out;
        }
        catch (InterruptedException e) {
            throw SqlUtils.toSQLException((Throwable)e);
        }
    }

    private KeyRec keyRec(StatementCacheKey key) {
        return (KeyRec)this.keyToKeyRec.get(key);
    }

    private HashSet keySet(StatementCacheKey key) {
        KeyRec rec = this.keyRec(key);
        return rec == null ? null : rec.allStmts;
    }

    private boolean removeFromKeySet(StatementCacheKey key, Object pstmt) {
        HashSet stmtSet = this.keySet(key);
        boolean out = stmtSet.remove(pstmt);
        if (stmtSet.isEmpty() && this.checkoutQueue(key).isEmpty()) {
            this.keyToKeyRec.remove(key);
        }
        return out;
    }

    private LinkedList checkoutQueue(StatementCacheKey key) {
        KeyRec rec = this.keyRec(key);
        return rec == null ? null : rec.checkoutQueue;
    }

    private boolean removeFromCheckoutQueue(StatementCacheKey key, Object pstmt) {
        LinkedList q = this.checkoutQueue(key);
        boolean out = q.remove(pstmt);
        if (q.isEmpty() && this.keySet(key).isEmpty()) {
            this.keyToKeyRec.remove(key);
        }
        return out;
    }

    private boolean ourResource(Object ps) {
        return this.stmtToKey.keySet().contains(ps);
    }

    private void refreshStatement(PreparedStatement ps) throws Exception {
        ps.clearParameters();
        ps.clearBatch();
    }

    private void printStats() {
        int total_size = this.countCachedStatements();
        int checked_out_size = this.checkedOut.size();
        int num_connections = this.cxnStmtMgr.getNumConnectionsWithCachedStatements();
        int num_keys = this.keyToKeyRec.size();
        System.err.print(this.getClass().getName() + " stats -- ");
        System.err.print("total size: " + total_size);
        System.err.print("; checked out: " + checked_out_size);
        System.err.print("; num connections: " + num_connections);
        System.err.println("; num keys: " + num_keys);
    }

    private String statsString() {
        int total_size = this.countCachedStatements();
        int checked_out_size = this.checkedOut.size();
        int num_connections = this.cxnStmtMgr.getNumConnectionsWithCachedStatements();
        int num_keys = this.keyToKeyRec.size();
        StringBuffer sb = new StringBuffer(255);
        sb.append(this.getClass().getName());
        sb.append(" stats -- ");
        sb.append("total size: ");
        sb.append(total_size);
        sb.append("; checked out: ");
        sb.append(checked_out_size);
        sb.append("; num connections: ");
        sb.append(num_connections);
        int in_use = this.destructo.countConnectionsInUse();
        if (in_use >= 0) {
            sb.append("; num connections in use: ");
            sb.append(in_use);
        }
        sb.append("; num keys: ");
        sb.append(num_keys);
        return sb.toString();
    }

    private final class CautiousStatementDestructionManager
    extends StatementDestructionManager {
        HashSet inUseConnections;
        HashMap connectionsToZombieStatementSets;
        AsynchronousRunner deferredStatementDestroyer;
        boolean closed;

        @Override
        synchronized void close() {
            this.closed = true;
        }

        CautiousStatementDestructionManager(AsynchronousRunner deferredStatementDestroyer) {
            super(deferredStatementDestroyer);
            this.inUseConnections = new HashSet();
            this.connectionsToZombieStatementSets = new HashMap();
            this.closed = false;
            this.deferredStatementDestroyer = deferredStatementDestroyer;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private String trace() {
            Set keys = this.connectionsToZombieStatementSets.keySet();
            int sum = 0;
            for (Object con : keys) {
                Set stmts;
                Set set = stmts = (Set)this.connectionsToZombieStatementSets.get(con);
                synchronized (set) {
                    sum += stmts == null ? 0 : stmts.size();
                }
            }
            return this.getClass().getName() + " [connections in use: " + this.inUseConnections.size() + "; connections with deferred statements: " + keys.size() + "; statements to destroy: " + sum + "]";
        }

        private void printAllStats() {
            GooGooStatementCache.this.printStats();
            System.err.println(this.trace());
        }

        @Override
        synchronized void waitMarkConnectionInUse(Connection physicalConnection) throws InterruptedException {
            if (!this.closed) {
                Set stmts = this.statementsUnderDestruction(physicalConnection);
                if (stmts != null) {
                    if (logger.isLoggable(MLevel.FINE)) {
                        logger.log(MLevel.FINE, "A connection is waiting to be accepted by the Statement cache because " + stmts.size() + " cached Statements are still being destroyed.");
                    }
                    while (!stmts.isEmpty()) {
                        this.wait();
                    }
                }
                this.inUseConnections.add(physicalConnection);
            }
        }

        @Override
        synchronized boolean tryMarkConnectionInUse(Connection physicalConnection) {
            if (!this.closed) {
                Set stmts = this.statementsUnderDestruction(physicalConnection);
                if (stmts != null) {
                    int sz = stmts.size();
                    if (logger.isLoggable(MLevel.FINE)) {
                        logger.log(MLevel.FINE, "A connection could not be accepted by the Statement cache because " + sz + " cached Statements are still being destroyed.");
                    }
                    return false;
                }
                this.inUseConnections.add(physicalConnection);
                return true;
            }
            return true;
        }

        @Override
        synchronized void unmarkConnectionInUse(Connection physicalConnection) {
            boolean unmarked = this.inUseConnections.remove(physicalConnection);
            Set zombieStatements = (Set)this.connectionsToZombieStatementSets.get(physicalConnection);
            if (zombieStatements != null) {
                this.destroyAllTrackedStatements(physicalConnection);
            }
        }

        @Override
        synchronized void deferredDestroyStatement(Object parentConnection, Object pstmt) {
            if (!this.closed) {
                if (this.inUseConnections.contains(parentConnection)) {
                    Set<Object> s = (Set<Object>)this.connectionsToZombieStatementSets.get(parentConnection);
                    if (s == null) {
                        s = Collections.synchronizedSet(new HashSet());
                        this.connectionsToZombieStatementSets.put(parentConnection, s);
                    }
                    s.add(pstmt);
                } else {
                    this.uncheckedDestroyStatement(pstmt);
                }
            } else {
                this.uncheckedDestroyStatement(pstmt);
            }
        }

        @Override
        synchronized int countConnectionsInUse() {
            return this.inUseConnections.size();
        }

        @Override
        synchronized boolean knownInUse(Connection pCon) {
            return this.inUseConnections.contains(pCon);
        }

        @Override
        Boolean tvlInUse(Connection pCon) {
            return this.knownInUse(pCon);
        }

        @Override
        synchronized int getNumConnectionsInUse() {
            return this.inUseConnections.size();
        }

        @Override
        synchronized int getNumConnectionsWithDeferredDestroyStatements() {
            return this.connectionsToZombieStatementSets.keySet().size();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        synchronized int getNumDeferredDestroyStatements() {
            Set keys = this.connectionsToZombieStatementSets.keySet();
            int sum = 0;
            for (Object con : keys) {
                Set stmts;
                Set set = stmts = (Set)this.connectionsToZombieStatementSets.get(con);
                synchronized (set) {
                    sum += stmts == null ? 0 : stmts.size();
                }
            }
            return sum;
        }

        private void trackedDestroyStatement(final Object parentConnection, final Object pstmt) {
            final class TrackedStatementCloseTask
            implements Runnable {
                TrackedStatementCloseTask() {
                }

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 */
                @Override
                public void run() {
                    CautiousStatementDestructionManager cautiousStatementDestructionManager = CautiousStatementDestructionManager.this;
                    synchronized (cautiousStatementDestructionManager) {
                        Set stmts = (Set)CautiousStatementDestructionManager.this.connectionsToZombieStatementSets.get(parentConnection);
                        if (stmts != null) {
                            StatementUtils.attemptClose((Statement)((PreparedStatement)pstmt));
                            boolean removed1 = stmts.remove(pstmt);
                            assert (removed1);
                            if (stmts.isEmpty()) {
                                Object removed2 = CautiousStatementDestructionManager.this.connectionsToZombieStatementSets.remove(parentConnection);
                                assert (removed2 == stmts);
                                CautiousStatementDestructionManager.this.notifyAll();
                            }
                        }
                    }
                }
            }
            TrackedStatementCloseTask r = new TrackedStatementCloseTask();
            if (!this.closed) {
                this.deferredStatementDestroyer.postRunnable((Runnable)r);
            } else {
                r.run();
            }
        }

        private void destroyAllTrackedStatements(final Object parentConnection) {
            final class TrackedDestroyAllStatementsTask
            implements Runnable {
                TrackedDestroyAllStatementsTask() {
                }

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 */
                @Override
                public void run() {
                    CautiousStatementDestructionManager cautiousStatementDestructionManager = CautiousStatementDestructionManager.this;
                    synchronized (cautiousStatementDestructionManager) {
                        Set stmts = (Set)CautiousStatementDestructionManager.this.connectionsToZombieStatementSets.remove(parentConnection);
                        if (stmts != null) {
                            Iterator ii = stmts.iterator();
                            while (ii.hasNext()) {
                                PreparedStatement pstmt = (PreparedStatement)ii.next();
                                StatementUtils.attemptClose((Statement)pstmt);
                                ii.remove();
                            }
                            CautiousStatementDestructionManager.this.notifyAll();
                        }
                    }
                }
            }
            TrackedDestroyAllStatementsTask r = new TrackedDestroyAllStatementsTask();
            if (!this.closed) {
                this.deferredStatementDestroyer.postRunnable((Runnable)r);
            } else {
                r.run();
            }
        }

        private Set statementsUnderDestruction(Object parentConnection) {
            assert (Thread.holdsLock(this));
            return (Set)this.connectionsToZombieStatementSets.get(parentConnection);
        }
    }

    private final class IncautiousStatementDestructionManager
    extends StatementDestructionManager {
        IncautiousStatementDestructionManager(AsynchronousRunner runner) {
            super(runner);
        }

        @Override
        void waitMarkConnectionInUse(Connection physicalConnection) throws InterruptedException {
        }

        @Override
        boolean tryMarkConnectionInUse(Connection physicalConnection) {
            return true;
        }

        @Override
        void unmarkConnectionInUse(Connection physicalConnection) {
        }

        @Override
        void deferredDestroyStatement(Object parentConnection, Object pstmt) {
            this.uncheckedDestroyStatement(pstmt);
        }

        @Override
        void close() {
        }

        @Override
        int countConnectionsInUse() {
            return -1;
        }

        @Override
        boolean knownInUse(Connection pCon) {
            return false;
        }

        @Override
        Boolean tvlInUse(Connection pCon) {
            return null;
        }

        @Override
        int getNumConnectionsInUse() {
            return -1;
        }

        @Override
        int getNumConnectionsWithDeferredDestroyStatements() {
            return -1;
        }

        @Override
        int getNumDeferredDestroyStatements() {
            return -1;
        }
    }

    private abstract class StatementDestructionManager {
        AsynchronousRunner runner;

        StatementDestructionManager(AsynchronousRunner runner) {
            this.runner = runner;
        }

        abstract void waitMarkConnectionInUse(Connection var1) throws InterruptedException;

        abstract boolean tryMarkConnectionInUse(Connection var1);

        abstract void unmarkConnectionInUse(Connection var1);

        abstract void deferredDestroyStatement(Object var1, Object var2);

        abstract int countConnectionsInUse();

        abstract boolean knownInUse(Connection var1);

        abstract Boolean tvlInUse(Connection var1);

        abstract int getNumConnectionsInUse();

        abstract int getNumConnectionsWithDeferredDestroyStatements();

        abstract int getNumDeferredDestroyStatements();

        abstract void close();

        final void uncheckedDestroyStatement(final Object pstmt) {
            class UncheckedStatementCloseTask
            implements Runnable {
                UncheckedStatementCloseTask() {
                }

                @Override
                public void run() {
                    StatementUtils.attemptClose((Statement)((PreparedStatement)pstmt));
                }
            }
            UncheckedStatementCloseTask r = new UncheckedStatementCloseTask();
            this.runner.postRunnable((Runnable)r);
        }

        final void synchronousDestroyStatement(Object pstmt) {
            StatementUtils.attemptClose((Statement)((PreparedStatement)pstmt));
        }
    }

    protected final class DeathmarchConnectionStatementManager
    extends ConnectionStatementManager {
        Map cxnsToDms = new HashMap();

        protected DeathmarchConnectionStatementManager() {
        }

        @Override
        public void addStatementForConnection(Object ps, Connection pcon) {
            super.addStatementForConnection(ps, pcon);
            Deathmarch dm = (Deathmarch)this.cxnsToDms.get(pcon);
            if (dm == null) {
                dm = new Deathmarch();
                this.cxnsToDms.put(pcon, dm);
            }
        }

        @Override
        public boolean removeStatementForConnection(Object ps, Connection pcon) {
            boolean out = super.removeStatementForConnection(ps, pcon);
            if (out && this.statementSet(pcon) == null) {
                this.cxnsToDms.remove(pcon);
            }
            return out;
        }

        public Deathmarch getDeathmarch(Connection pcon) {
            return (Deathmarch)this.cxnsToDms.get(pcon);
        }
    }

    protected static final class SimpleConnectionStatementManager
    extends ConnectionStatementManager {
        protected SimpleConnectionStatementManager() {
        }
    }

    protected static abstract class ConnectionStatementManager {
        Map cxnToStmtSets = new HashMap();

        protected ConnectionStatementManager() {
        }

        public int getNumConnectionsWithCachedStatements() {
            return this.cxnToStmtSets.size();
        }

        public Set connectionSet() {
            return this.cxnToStmtSets.keySet();
        }

        public Set statementSet(Connection pcon) {
            return (Set)this.cxnToStmtSets.get(pcon);
        }

        public int getNumStatementsForConnection(Connection pcon) {
            Set stmtSet = this.statementSet(pcon);
            return stmtSet == null ? 0 : stmtSet.size();
        }

        public void addStatementForConnection(Object ps, Connection pcon) {
            HashSet<Object> stmtSet = this.statementSet(pcon);
            if (stmtSet == null) {
                stmtSet = new HashSet<Object>();
                this.cxnToStmtSets.put(pcon, stmtSet);
            }
            stmtSet.add(ps);
        }

        public boolean removeStatementForConnection(Object ps, Connection pcon) {
            boolean out;
            Set stmtSet = this.statementSet(pcon);
            if (stmtSet != null) {
                out = stmtSet.remove(ps);
                if (stmtSet.isEmpty()) {
                    this.cxnToStmtSets.remove(pcon);
                }
            } else {
                out = false;
            }
            return out;
        }
    }

    protected class Deathmarch {
        TreeMap longsToStmts = new TreeMap();
        HashMap stmtsToLongs = new HashMap();
        long last_long = -1L;

        protected Deathmarch() {
        }

        public void deathmarchStatement(Object ps) {
            assert (Thread.holdsLock(GooGooStatementCache.this));
            Long old = (Long)this.stmtsToLongs.get(ps);
            if (old != null) {
                throw new RuntimeException("Internal inconsistency: A statement is being double-deathmatched. no checked-out statements should be in a deathmarch already; no already checked-in statement should be deathmarched!");
            }
            Long youth = this.getNextLong();
            this.stmtsToLongs.put(ps, youth);
            this.longsToStmts.put(youth, ps);
        }

        public void undeathmarchStatement(Object ps) {
            assert (Thread.holdsLock(GooGooStatementCache.this));
            Long old = (Long)this.stmtsToLongs.remove(ps);
            if (old == null) {
                throw new RuntimeException("Internal inconsistency: A (not new) checking-out statement is not in deathmarch.");
            }
            Object check = this.longsToStmts.remove(old);
            if (old == null) {
                throw new RuntimeException("Internal inconsistency: A (not new) checking-out statement is not in deathmarch.");
            }
        }

        public boolean cullNext() {
            assert (Thread.holdsLock(GooGooStatementCache.this));
            Object cullMeStmt = null;
            StatementCacheKey sck = null;
            if (!this.longsToStmts.isEmpty()) {
                Long l = (Long)this.longsToStmts.firstKey();
                cullMeStmt = this.longsToStmts.get(l);
            }
            if (cullMeStmt == null) {
                return false;
            }
            if (sck == null) {
                sck = (StatementCacheKey)GooGooStatementCache.this.stmtToKey.get(cullMeStmt);
            }
            if (logger.isLoggable(MLevel.FINEST)) {
                logger.finest("CULLING: " + sck.stmtText);
            }
            GooGooStatementCache.this.removeStatement(cullMeStmt, 3);
            if (this.contains(cullMeStmt)) {
                throw new RuntimeException("Inconsistency!!! Statement culled from deathmarch failed to be removed by removeStatement( ... )!");
            }
            return true;
        }

        public boolean contains(Object ps) {
            return this.stmtsToLongs.keySet().contains(ps);
        }

        public int size() {
            return this.longsToStmts.size();
        }

        private Long getNextLong() {
            return new Long(++this.last_long);
        }
    }

    private static class KeyRec {
        HashSet allStmts = new HashSet();
        LinkedList checkoutQueue = new LinkedList();

        private KeyRec() {
        }
    }
}

