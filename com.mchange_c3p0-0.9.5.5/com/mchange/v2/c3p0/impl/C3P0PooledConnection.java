/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mchange.v2.lang.ObjectUtils
 *  com.mchange.v2.log.MLevel
 *  com.mchange.v2.log.MLog
 *  com.mchange.v2.log.MLogger
 *  com.mchange.v2.sql.SqlUtils
 *  com.mchange.v2.sql.filter.FilterCallableStatement
 *  com.mchange.v2.sql.filter.FilterPreparedStatement
 *  com.mchange.v2.sql.filter.FilterStatement
 */
package com.mchange.v2.c3p0.impl;

import com.mchange.v2.c3p0.C3P0ProxyConnection;
import com.mchange.v2.c3p0.C3P0ProxyStatement;
import com.mchange.v2.c3p0.ConnectionCustomizer;
import com.mchange.v2.c3p0.ConnectionTester;
import com.mchange.v2.c3p0.impl.AbstractC3P0PooledConnection;
import com.mchange.v2.c3p0.impl.C3P0ImplUtils;
import com.mchange.v2.c3p0.impl.NullStatementSetManagedResultSet;
import com.mchange.v2.c3p0.impl.SetManagedDatabaseMetaData;
import com.mchange.v2.c3p0.impl.SetManagedResultSet;
import com.mchange.v2.c3p0.stmt.GooGooStatementCache;
import com.mchange.v2.c3p0.util.ConnectionEventSupport;
import com.mchange.v2.c3p0.util.StatementEventSupport;
import com.mchange.v2.lang.ObjectUtils;
import com.mchange.v2.log.MLevel;
import com.mchange.v2.log.MLog;
import com.mchange.v2.log.MLogger;
import com.mchange.v2.sql.SqlUtils;
import com.mchange.v2.sql.filter.FilterCallableStatement;
import com.mchange.v2.sql.filter.FilterPreparedStatement;
import com.mchange.v2.sql.filter.FilterStatement;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.sql.ConnectionEventListener;
import javax.sql.StatementEventListener;

public final class C3P0PooledConnection
extends AbstractC3P0PooledConnection {
    static final MLogger logger = MLog.getLogger(C3P0PooledConnection.class);
    static final Class[] PROXY_CTOR_ARGS = new Class[]{InvocationHandler.class};
    static final Constructor CON_PROXY_CTOR;
    static final Method RS_CLOSE_METHOD;
    static final Method STMT_CLOSE_METHOD;
    static final Object[] CLOSE_ARGS;
    static final Set OBJECT_METHODS;
    final ConnectionTester connectionTester;
    final boolean autoCommitOnClose;
    final boolean forceIgnoreUnresolvedTransactions;
    final boolean supports_setTypeMap;
    final boolean supports_setHoldability;
    final int dflt_txn_isolation;
    final String dflt_catalog;
    final int dflt_holdability;
    final ConnectionEventSupport ces = new ConnectionEventSupport(this);
    final StatementEventSupport ses = new StatementEventSupport(this);
    volatile Connection physicalConnection;
    volatile Exception invalidatingException = null;
    ProxyConnection exposedProxy;
    int connection_status = 0;
    final Set uncachedActiveStatements = Collections.synchronizedSet(new HashSet());
    volatile GooGooStatementCache scache;
    volatile boolean isolation_lvl_nondefault = false;
    volatile boolean catalog_nondefault = false;
    volatile boolean holdability_nondefault = false;

    private static Constructor createProxyConstructor(Class intfc) throws NoSuchMethodException {
        Class[] proxyInterfaces = new Class[]{intfc};
        Class<?> proxyCl = Proxy.getProxyClass(C3P0PooledConnection.class.getClassLoader(), proxyInterfaces);
        return proxyCl.getConstructor(PROXY_CTOR_ARGS);
    }

    public C3P0PooledConnection(Connection con, ConnectionTester connectionTester, boolean autoCommitOnClose, boolean forceIgnoreUnresolvedTransactions, ConnectionCustomizer cc, String pdsIdt) throws SQLException {
        try {
            if (cc != null) {
                cc.onAcquire(con, pdsIdt);
            }
        }
        catch (Exception e) {
            throw SqlUtils.toSQLException((Throwable)e);
        }
        this.physicalConnection = con;
        this.connectionTester = connectionTester;
        this.autoCommitOnClose = autoCommitOnClose;
        this.forceIgnoreUnresolvedTransactions = forceIgnoreUnresolvedTransactions;
        this.supports_setTypeMap = C3P0ImplUtils.supportsMethod(con, "setTypeMap", new Class[]{Map.class});
        this.supports_setHoldability = C3P0ImplUtils.supportsMethod(con, "setHoldability", new Class[]{Integer.TYPE});
        this.dflt_txn_isolation = con.getTransactionIsolation();
        this.dflt_catalog = con.getCatalog();
        this.dflt_holdability = this.supports_setHoldability ? con.getHoldability() : 2;
    }

    @Override
    Connection getPhysicalConnection() {
        return this.physicalConnection;
    }

    boolean isClosed() throws SQLException {
        return this.physicalConnection == null;
    }

    @Override
    void initStatementCache(GooGooStatementCache scache) {
        this.scache = scache;
    }

    @Override
    public synchronized Connection getConnection() throws SQLException {
        if (this.exposedProxy != null) {
            logger.warning("c3p0 -- Uh oh... getConnection() was called on a PooledConnection when it had already provided a client with a Connection that has not yet been closed. This probably indicates a bug in the connection pool!!!");
            return this.exposedProxy;
        }
        return this.getCreateNewConnection();
    }

    private Connection getCreateNewConnection() throws SQLException {
        try {
            this.ensureOkay();
            this.exposedProxy = this.createProxyConnection();
            return this.exposedProxy;
        }
        catch (SQLException e) {
            throw e;
        }
        catch (Exception e) {
            logger.log(MLevel.WARNING, "Failed to acquire connection!", (Throwable)e);
            throw new SQLException("Failed to acquire connection!");
        }
    }

    public void closeAll() throws SQLException {
        if (this.scache != null) {
            this.scache.closeAll(this.physicalConnection);
        }
    }

    @Override
    public void close() throws SQLException {
        this.close(false);
    }

    @Override
    synchronized void closeMaybeCheckedOut(boolean checked_out) throws SQLException {
        block3: {
            if (checked_out) {
                try {
                    C3P0ImplUtils.resetTxnState(this.physicalConnection, this.forceIgnoreUnresolvedTransactions, this.autoCommitOnClose, false);
                }
                catch (Exception e) {
                    if (!logger.isLoggable(MLevel.FINER)) break block3;
                    logger.log(MLevel.FINER, "Failed to reset the transaction state of  " + this.physicalConnection + "just prior to close(). Only relevant at all if this was a Connection being forced close()ed midtransaction.", (Throwable)e);
                }
            }
        }
        this.close(false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private synchronized void close(boolean known_invalid) throws SQLException {
        if (this.physicalConnection != null) {
            try {
                Exception exc;
                StringBuffer debugOnlyLog = null;
                if (known_invalid) {
                    debugOnlyLog = new StringBuffer();
                    debugOnlyLog.append("[ exceptions: ");
                }
                if ((exc = this.cleanupUncachedActiveStatements()) != null) {
                    if (known_invalid) {
                        debugOnlyLog.append(exc.toString() + ' ');
                    } else {
                        logger.log(MLevel.WARNING, "An exception occurred while cleaning up uncached active Statements.", (Throwable)exc);
                    }
                }
                try {
                    if (this.exposedProxy != null) {
                        this.exposedProxy.silentClose(known_invalid);
                    }
                }
                catch (Exception e) {
                    if (known_invalid) {
                        debugOnlyLog.append(e.toString() + ' ');
                    } else {
                        logger.log(MLevel.WARNING, "An exception occurred.", (Throwable)exc);
                    }
                    exc = e;
                }
                try {
                    this.closeAll();
                }
                catch (Exception e) {
                    if (known_invalid) {
                        debugOnlyLog.append(e.toString() + ' ');
                    } else {
                        logger.log(MLevel.WARNING, "An exception occurred.", (Throwable)exc);
                    }
                    exc = e;
                }
                try {
                    this.physicalConnection.close();
                }
                catch (Exception e) {
                    if (known_invalid) {
                        debugOnlyLog.append(e.toString() + ' ');
                    } else {
                        logger.log(MLevel.WARNING, "An exception occurred.", (Throwable)exc);
                    }
                    e.printStackTrace();
                    exc = e;
                }
                if (exc != null) {
                    if (known_invalid) {
                        debugOnlyLog.append(" ]");
                        logger.fine(this + ": while closing a PooledConnection known to be invalid,   some exceptions occurred. This is probably not a problem: " + debugOnlyLog.toString());
                    } else {
                        throw new SQLException("At least one error occurred while attempting to close() the PooledConnection: " + exc);
                    }
                }
                logger.fine("C3P0PooledConnection closed. [" + this + ']');
            }
            finally {
                this.physicalConnection = null;
            }
        }
    }

    @Override
    public void addConnectionEventListener(ConnectionEventListener listener) {
        this.ces.addConnectionEventListener(listener);
    }

    @Override
    public void removeConnectionEventListener(ConnectionEventListener listener) {
        this.ces.removeConnectionEventListener(listener);
    }

    @Override
    public void addStatementEventListener(StatementEventListener sel) {
        if (logger.isLoggable(MLevel.INFO)) {
            logger.info("Per the JDBC4 spec, " + this.getClass().getName() + " accepts StatementListeners, but for now there is no circumstance under which they are notified!");
        }
        this.ses.addStatementEventListener(sel);
    }

    @Override
    public void removeStatementEventListener(StatementEventListener sel) {
        this.ses.removeStatementEventListener(sel);
    }

    private void reset() throws SQLException {
        this.reset(false);
    }

    private void reset(boolean known_resolved_txn) throws SQLException {
        block9: {
            block8: {
                this.ensureOkay();
                C3P0ImplUtils.resetTxnState(this.physicalConnection, this.forceIgnoreUnresolvedTransactions, this.autoCommitOnClose, known_resolved_txn);
                if (this.isolation_lvl_nondefault) {
                    this.physicalConnection.setTransactionIsolation(this.dflt_txn_isolation);
                    this.isolation_lvl_nondefault = false;
                }
                if (this.catalog_nondefault) {
                    this.physicalConnection.setCatalog(this.dflt_catalog);
                    this.catalog_nondefault = false;
                }
                if (this.holdability_nondefault) {
                    this.physicalConnection.setHoldability(this.dflt_holdability);
                    this.holdability_nondefault = false;
                }
                try {
                    this.physicalConnection.setReadOnly(false);
                }
                catch (Throwable t) {
                    if (!logger.isLoggable(MLevel.FINE)) break block8;
                    logger.log(MLevel.FINE, "A Throwable occurred while trying to reset the readOnly property of our Connection to false!", t);
                }
            }
            try {
                if (this.supports_setTypeMap) {
                    this.physicalConnection.setTypeMap(Collections.EMPTY_MAP);
                }
            }
            catch (Throwable t) {
                if (!logger.isLoggable(MLevel.FINE)) break block9;
                logger.log(MLevel.FINE, "A Throwable occurred while trying to reset the typeMap property of our Connection to Collections.EMPTY_MAP!", t);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    boolean closeAndRemoveResultSets(Set rsSet) {
        boolean okay = true;
        Set set = rsSet;
        synchronized (set) {
            Iterator ii = rsSet.iterator();
            while (ii.hasNext()) {
                ResultSet rs = (ResultSet)ii.next();
                try {
                    rs.close();
                }
                catch (SQLException e) {
                    logger.log(MLevel.WARNING, "An exception occurred while cleaning up a ResultSet.", (Throwable)e);
                    okay = false;
                }
                finally {
                    ii.remove();
                }
            }
        }
        return okay;
    }

    void ensureOkay() throws SQLException {
        if (this.physicalConnection == null) {
            throw new SQLException(this.invalidatingException == null ? "Connection is closed or broken." : "Connection is broken. Invalidating Exception: " + this.invalidatingException.toString());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    boolean closeAndRemoveResourcesInSet(Set s, Method closeMethod) {
        HashSet temp;
        boolean okay = true;
        Set set = s;
        synchronized (set) {
            temp = new HashSet(s);
        }
        for (Object rsrc : temp) {
            try {
                closeMethod.invoke(rsrc, CLOSE_ARGS);
            }
            catch (Exception e) {
                Throwable t = e;
                if (t instanceof InvocationTargetException) {
                    t = ((InvocationTargetException)e).getTargetException();
                }
                logger.log(MLevel.WARNING, "An exception occurred while cleaning up a resource.", t);
                okay = false;
            }
            finally {
                s.remove(rsrc);
            }
        }
        return okay;
    }

    private SQLException cleanupUncachedActiveStatements() {
        boolean okay = this.closeAndRemoveResourcesInSet(this.uncachedActiveStatements, STMT_CLOSE_METHOD);
        if (okay) {
            return null;
        }
        return new SQLException("An exception occurred while trying to clean up orphaned resources.");
    }

    ProxyConnection createProxyConnection() throws Exception {
        ProxyConnectionInvocationHandler handler = new ProxyConnectionInvocationHandler();
        return (ProxyConnection)CON_PROXY_CTOR.newInstance(handler);
    }

    Statement createProxyStatement(Statement innerStmt) throws Exception {
        return this.createProxyStatement(false, innerStmt);
    }

    Statement createProxyStatement(final boolean inner_is_cached, final Statement innerStmt) throws Exception {
        final Set activeResultSets = Collections.synchronizedSet(new HashSet());
        final ProxyConnection parentConnection = this.exposedProxy;
        if (parentConnection == null) {
            logger.warning("PROBABLE C3P0 BUG -- " + this + ": created a proxy Statement when there is no active, exposed proxy Connection???");
        }
        final StatementProxyingSetManagedResultSet mainResultSet = new StatementProxyingSetManagedResultSet(activeResultSets);
        if (innerStmt instanceof CallableStatement) {
            class ProxyCallableStatement
            extends FilterCallableStatement
            implements C3P0ProxyStatement {
                1WrapperStatementHelper wsh;

                ProxyCallableStatement(CallableStatement is) {
                    super(is);
                    class WrapperStatementHelper {
                        Statement wrapperStmt;
                        Statement nakedInner;
                        final /* synthetic */ boolean val$inner_is_cached;
                        final /* synthetic */ Set val$activeResultSets;
                        final /* synthetic */ StatementProxyingSetManagedResultSet val$mainResultSet;
                        final /* synthetic */ Statement val$innerStmt;

                        public WrapperStatementHelper(Statement wrapperStmt, Statement nakedInner) {
                            this.val$inner_is_cached = bl;
                            this.val$activeResultSets = set;
                            this.val$mainResultSet = statementProxyingSetManagedResultSet;
                            this.val$innerStmt = statement;
                            this.wrapperStmt = wrapperStmt;
                            this.nakedInner = nakedInner;
                            if (!this.val$inner_is_cached) {
                                C3P0PooledConnection.this.uncachedActiveStatements.add(wrapperStmt);
                            }
                        }

                        private boolean closeAndRemoveActiveResultSets() {
                            return C3P0PooledConnection.this.closeAndRemoveResultSets(this.val$activeResultSets);
                        }

                        public ResultSet wrap(ResultSet rs) {
                            if (this.val$mainResultSet.getInner() == null) {
                                this.val$mainResultSet.setInner(rs);
                                this.val$mainResultSet.setProxyStatement(this.wrapperStmt);
                                return this.val$mainResultSet;
                            }
                            StatementProxyingSetManagedResultSet out = new StatementProxyingSetManagedResultSet(this.val$activeResultSets);
                            out.setInner(rs);
                            out.setProxyStatement(this.wrapperStmt);
                            return out;
                        }

                        public void doClose() throws SQLException {
                            boolean okay = this.closeAndRemoveActiveResultSets();
                            if (this.val$inner_is_cached) {
                                C3P0PooledConnection.this.scache.checkinStatement(this.val$innerStmt);
                            } else {
                                this.val$innerStmt.close();
                                C3P0PooledConnection.this.uncachedActiveStatements.remove(this.wrapperStmt);
                            }
                            if (!okay) {
                                throw new SQLException("Failed to close an orphaned ResultSet properly.");
                            }
                        }

                        public Object doRawStatementOperation(Method m, Object target, Object[] args) throws IllegalAccessException, InvocationTargetException, SQLException {
                            if (target == C3P0ProxyStatement.RAW_STATEMENT) {
                                target = this.nakedInner;
                            }
                            int len = args.length;
                            for (int i = 0; i < len; ++i) {
                                if (args[i] != C3P0ProxyStatement.RAW_STATEMENT) continue;
                                args[i] = this.nakedInner;
                            }
                            Object out = m.invoke(target, args);
                            if (out instanceof ResultSet) {
                                out = this.wrap((ResultSet)out);
                            }
                            return out;
                        }
                    }
                    this.wsh = new WrapperStatementHelper(C3P0PooledConnection.this, (Statement)this, (Statement)is, inner_is_cached, activeResultSets, mainResultSet, innerStmt);
                }

                @Override
                public Connection getConnection() {
                    return parentConnection;
                }

                @Override
                public ResultSet getResultSet() throws SQLException {
                    return this.wsh.wrap(super.getResultSet());
                }

                @Override
                public ResultSet getGeneratedKeys() throws SQLException {
                    return this.wsh.wrap(super.getGeneratedKeys());
                }

                @Override
                public ResultSet executeQuery(String sql) throws SQLException {
                    return this.wsh.wrap(super.executeQuery(sql));
                }

                public ResultSet executeQuery() throws SQLException {
                    return this.wsh.wrap(super.executeQuery());
                }

                @Override
                public Object rawStatementOperation(Method m, Object target, Object[] args) throws IllegalAccessException, InvocationTargetException, SQLException {
                    return this.wsh.doRawStatementOperation(m, target, args);
                }

                @Override
                public void close() throws SQLException {
                    this.wsh.doClose();
                }
            }
            return new ProxyCallableStatement((CallableStatement)innerStmt);
        }
        if (innerStmt instanceof PreparedStatement) {
            class ProxyPreparedStatement
            extends FilterPreparedStatement
            implements C3P0ProxyStatement {
                1WrapperStatementHelper wsh;

                ProxyPreparedStatement(PreparedStatement ps) {
                    super(ps);
                    this.wsh = new WrapperStatementHelper(C3P0PooledConnection.this, (Statement)this, (Statement)ps, inner_is_cached, activeResultSets, mainResultSet, innerStmt);
                }

                @Override
                public Connection getConnection() {
                    return parentConnection;
                }

                @Override
                public ResultSet getResultSet() throws SQLException {
                    return this.wsh.wrap(super.getResultSet());
                }

                @Override
                public ResultSet getGeneratedKeys() throws SQLException {
                    return this.wsh.wrap(super.getGeneratedKeys());
                }

                @Override
                public ResultSet executeQuery(String sql) throws SQLException {
                    return this.wsh.wrap(super.executeQuery(sql));
                }

                public ResultSet executeQuery() throws SQLException {
                    return this.wsh.wrap(super.executeQuery());
                }

                @Override
                public Object rawStatementOperation(Method m, Object target, Object[] args) throws IllegalAccessException, InvocationTargetException, SQLException {
                    return this.wsh.doRawStatementOperation(m, target, args);
                }

                @Override
                public void close() throws SQLException {
                    this.wsh.doClose();
                }
            }
            return new ProxyPreparedStatement((PreparedStatement)innerStmt);
        }
        class ProxyStatement
        extends FilterStatement
        implements C3P0ProxyStatement {
            1WrapperStatementHelper wsh;

            ProxyStatement(Statement s) {
                super(s);
                this.wsh = new WrapperStatementHelper(C3P0PooledConnection.this, (Statement)this, s, inner_is_cached, activeResultSets, mainResultSet, innerStmt);
            }

            @Override
            public Connection getConnection() {
                return parentConnection;
            }

            @Override
            public ResultSet getResultSet() throws SQLException {
                return this.wsh.wrap(super.getResultSet());
            }

            @Override
            public ResultSet getGeneratedKeys() throws SQLException {
                return this.wsh.wrap(super.getGeneratedKeys());
            }

            @Override
            public ResultSet executeQuery(String sql) throws SQLException {
                return this.wsh.wrap(super.executeQuery(sql));
            }

            @Override
            public Object rawStatementOperation(Method m, Object target, Object[] args) throws IllegalAccessException, InvocationTargetException, SQLException {
                return this.wsh.doRawStatementOperation(m, target, args);
            }

            @Override
            public void close() throws SQLException {
                this.wsh.doClose();
            }
        }
        return new ProxyStatement(innerStmt);
    }

    public synchronized int getConnectionStatus() {
        return this.connection_status;
    }

    private synchronized void updateConnectionStatus(int status) {
        switch (this.connection_status) {
            case -8: {
                break;
            }
            case -1: {
                if (status != -8) break;
                this.doBadUpdate(status);
                break;
            }
            case 0: {
                if (status == 0) break;
                this.doBadUpdate(status);
                break;
            }
            default: {
                throw new InternalError(this + " -- Illegal Connection Status: " + this.connection_status);
            }
        }
    }

    private void doBadUpdate(int new_status) {
        this.connection_status = new_status;
        try {
            this.close(true);
        }
        catch (SQLException e) {
            logger.log(MLevel.WARNING, "Broken Connection Close Error. ", (Throwable)e);
        }
    }

    static {
        try {
            CON_PROXY_CTOR = C3P0PooledConnection.createProxyConstructor(ProxyConnection.class);
            Class[] argClasses = new Class[]{};
            RS_CLOSE_METHOD = ResultSet.class.getMethod("close", argClasses);
            STMT_CLOSE_METHOD = Statement.class.getMethod("close", argClasses);
            CLOSE_ARGS = new Object[0];
            OBJECT_METHODS = Collections.unmodifiableSet(new HashSet<Method>(Arrays.asList(Object.class.getMethods())));
        }
        catch (Exception e) {
            logger.log(MLevel.SEVERE, "An Exception occurred in static initializer of" + C3P0PooledConnection.class.getName(), (Throwable)e);
            throw new InternalError("Something is very wrong, or this is a pre 1.3 JVM.We cannot set up dynamic proxies and/or methods!");
        }
    }

    static interface ProxyConnection
    extends C3P0ProxyConnection {
        public void silentClose(boolean var1) throws SQLException;
    }

    final class ProxyConnectionInvocationHandler
    implements InvocationHandler {
        Connection activeConnection;
        DatabaseMetaData metaData;
        boolean connection_error_signaled;
        final Set activeMetaDataResultSets;
        Set doRawResultSets;
        boolean txn_known_resolved;

        ProxyConnectionInvocationHandler() {
            this.activeConnection = C3P0PooledConnection.this.physicalConnection;
            this.metaData = null;
            this.connection_error_signaled = false;
            this.activeMetaDataResultSets = new HashSet();
            this.doRawResultSets = null;
            this.txn_known_resolved = true;
        }

        public String toString() {
            return "C3P0ProxyConnection [Invocation Handler: " + super.toString() + ']';
        }

        private Object doRawConnectionOperation(Method m, Object target, Object[] args) throws IllegalAccessException, InvocationTargetException, SQLException, Exception {
            if (this.activeConnection == null) {
                throw new SQLException("Connection previously closed. You cannot operate on a closed Connection.");
            }
            if (target == C3P0ProxyConnection.RAW_CONNECTION) {
                target = this.activeConnection;
            }
            int len = args.length;
            for (int i = 0; i < len; ++i) {
                if (args[i] != C3P0ProxyConnection.RAW_CONNECTION) continue;
                args[i] = this.activeConnection;
            }
            Object out = m.invoke(target, args);
            if (out instanceof Statement) {
                out = C3P0PooledConnection.this.createProxyStatement(false, (Statement)out);
            } else if (out instanceof ResultSet) {
                if (this.doRawResultSets == null) {
                    this.doRawResultSets = new HashSet();
                }
                out = new NullStatementSetManagedResultSet((ResultSet)out, this.doRawResultSets);
            }
            return out;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public synchronized Object invoke(Object proxy, Method m, Object[] args) throws Throwable {
            if (OBJECT_METHODS.contains(m)) {
                return m.invoke((Object)this, args);
            }
            try {
                String mname = m.getName();
                if (this.activeConnection != null) {
                    if (mname.equals("rawConnectionOperation")) {
                        C3P0PooledConnection.this.ensureOkay();
                        this.txn_known_resolved = false;
                        return this.doRawConnectionOperation((Method)args[0], args[1], (Object[])args[2]);
                    }
                    if (mname.equals("setTransactionIsolation")) {
                        C3P0PooledConnection.this.ensureOkay();
                        m.invoke((Object)this.activeConnection, args);
                        int lvl = (Integer)args[0];
                        C3P0PooledConnection.this.isolation_lvl_nondefault = lvl != C3P0PooledConnection.this.dflt_txn_isolation;
                        return null;
                    }
                    if (mname.equals("setCatalog")) {
                        C3P0PooledConnection.this.ensureOkay();
                        m.invoke((Object)this.activeConnection, args);
                        String catalog = (String)args[0];
                        C3P0PooledConnection.this.catalog_nondefault = ObjectUtils.eqOrBothNull((Object)catalog, (Object)C3P0PooledConnection.this.dflt_catalog);
                        return null;
                    }
                    if (mname.equals("setHoldability")) {
                        C3P0PooledConnection.this.ensureOkay();
                        m.invoke((Object)this.activeConnection, args);
                        int holdability = (Integer)args[0];
                        C3P0PooledConnection.this.holdability_nondefault = holdability != C3P0PooledConnection.this.dflt_holdability;
                        return null;
                    }
                    if (mname.equals("createStatement")) {
                        C3P0PooledConnection.this.ensureOkay();
                        this.txn_known_resolved = false;
                        Object stmt = m.invoke((Object)this.activeConnection, args);
                        return C3P0PooledConnection.this.createProxyStatement((Statement)stmt);
                    }
                    if (mname.equals("prepareStatement")) {
                        C3P0PooledConnection.this.ensureOkay();
                        this.txn_known_resolved = false;
                        if (C3P0PooledConnection.this.scache == null) {
                            Object pstmt = m.invoke((Object)this.activeConnection, args);
                            return C3P0PooledConnection.this.createProxyStatement((Statement)pstmt);
                        }
                        Object pstmt = C3P0PooledConnection.this.scache.checkoutStatement(C3P0PooledConnection.this.physicalConnection, m, args);
                        return C3P0PooledConnection.this.createProxyStatement(true, (Statement)pstmt);
                    }
                    if (mname.equals("prepareCall")) {
                        C3P0PooledConnection.this.ensureOkay();
                        this.txn_known_resolved = false;
                        if (C3P0PooledConnection.this.scache == null) {
                            Object cstmt = m.invoke((Object)this.activeConnection, args);
                            return C3P0PooledConnection.this.createProxyStatement((Statement)cstmt);
                        }
                        Object cstmt = C3P0PooledConnection.this.scache.checkoutStatement(C3P0PooledConnection.this.physicalConnection, m, args);
                        return C3P0PooledConnection.this.createProxyStatement(true, (Statement)cstmt);
                    }
                    if (mname.equals("getMetaData")) {
                        C3P0PooledConnection.this.ensureOkay();
                        this.txn_known_resolved = false;
                        DatabaseMetaData innerMd = this.activeConnection.getMetaData();
                        if (this.metaData == null) {
                            C3P0PooledConnection c3P0PooledConnection = C3P0PooledConnection.this;
                            synchronized (c3P0PooledConnection) {
                                this.metaData = new SetManagedDatabaseMetaData(innerMd, this.activeMetaDataResultSets, C3P0PooledConnection.this.exposedProxy);
                            }
                        }
                        return this.metaData;
                    }
                    if (mname.equals("silentClose")) {
                        this.doSilentClose(proxy, (Boolean)args[0], this.txn_known_resolved);
                        return null;
                    }
                    if (mname.equals("close")) {
                        Exception e = this.doSilentClose(proxy, false, this.txn_known_resolved);
                        if (!this.connection_error_signaled) {
                            C3P0PooledConnection.this.ces.fireConnectionClosed();
                        }
                        if (e != null) {
                            throw e;
                        }
                        return null;
                    }
                    C3P0PooledConnection.this.ensureOkay();
                    this.txn_known_resolved = false;
                    return m.invoke((Object)this.activeConnection, args);
                }
                if (mname.equals("close") || mname.equals("silentClose")) {
                    return null;
                }
                if (mname.equals("isClosed")) {
                    return Boolean.TRUE;
                }
                throw new SQLException("You can't operate on a closed connection!!!");
            }
            catch (InvocationTargetException e) {
                Throwable convertMe = e.getTargetException();
                SQLException sqle = this.handleMaybeFatalToPooledConnection(convertMe, proxy, false);
                sqle.fillInStackTrace();
                throw sqle;
            }
        }

        private Exception doSilentClose(Object proxyConnection, boolean pooled_connection_is_dead) {
            return this.doSilentClose(proxyConnection, pooled_connection_is_dead, false);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private Exception doSilentClose(Object proxyConnection, boolean pooled_connection_is_dead, boolean known_resolved_txn) {
            if (this.activeConnection != null) {
                String errSource;
                C3P0PooledConnection c3P0PooledConnection = C3P0PooledConnection.this;
                synchronized (c3P0PooledConnection) {
                    if (C3P0PooledConnection.this.exposedProxy == proxyConnection) {
                        C3P0PooledConnection.this.exposedProxy = null;
                    } else {
                        logger.warning("(c3p0 issue) doSilentClose( ... ) called on a proxyConnection other than the current exposed proxy for its PooledConnection. [exposedProxy: " + C3P0PooledConnection.this.exposedProxy + ", proxyConnection: " + proxyConnection);
                    }
                }
                Exception out = null;
                Exception exc1 = null;
                SQLException exc2 = null;
                SQLException exc3 = null;
                Exception exc4 = null;
                try {
                    if (!pooled_connection_is_dead) {
                        C3P0PooledConnection.this.reset(known_resolved_txn);
                    }
                }
                catch (Exception e) {
                    exc1 = e;
                }
                exc2 = C3P0PooledConnection.this.cleanupUncachedActiveStatements();
                if (this.doRawResultSets != null) {
                    this.activeMetaDataResultSets.addAll(this.doRawResultSets);
                    errSource = "DataBaseMetaData or raw Connection operation";
                } else {
                    errSource = "DataBaseMetaData";
                }
                if (!C3P0PooledConnection.this.closeAndRemoveResultSets(this.activeMetaDataResultSets)) {
                    exc3 = new SQLException("Failed to close some " + errSource + " Result Sets.");
                }
                if (C3P0PooledConnection.this.scache != null) {
                    try {
                        C3P0PooledConnection.this.scache.checkinAll(C3P0PooledConnection.this.physicalConnection);
                    }
                    catch (Exception e) {
                        exc4 = e;
                    }
                }
                if (exc1 != null) {
                    this.handleMaybeFatalToPooledConnection(exc1, proxyConnection, true);
                    out = exc1;
                } else if (exc2 != null) {
                    this.handleMaybeFatalToPooledConnection(exc2, proxyConnection, true);
                    out = exc2;
                } else if (exc3 != null) {
                    this.handleMaybeFatalToPooledConnection(exc3, proxyConnection, true);
                    out = exc3;
                } else if (exc4 != null) {
                    this.handleMaybeFatalToPooledConnection(exc4, proxyConnection, true);
                    out = exc4;
                }
                this.activeConnection = null;
                return out;
            }
            return null;
        }

        private SQLException handleMaybeFatalToPooledConnection(Throwable t, Object proxyConnection, boolean already_closed) {
            SQLException sqle = SqlUtils.toSQLException((Throwable)t);
            int status = C3P0PooledConnection.this.connectionTester.statusOnException(C3P0PooledConnection.this.physicalConnection, sqle);
            C3P0PooledConnection.this.updateConnectionStatus(status);
            if (status != 0) {
                logger.log(MLevel.INFO, C3P0PooledConnection.this + " will no longer be pooled because it has been marked invalid by an Exception.", t);
                C3P0PooledConnection.this.invalidatingException = sqle;
                if (!this.connection_error_signaled) {
                    C3P0PooledConnection.this.ces.fireConnectionErrorOccurred(sqle);
                    this.connection_error_signaled = true;
                }
            }
            return sqle;
        }
    }

    private static class StatementProxyingSetManagedResultSet
    extends SetManagedResultSet {
        private Statement proxyStatement;

        StatementProxyingSetManagedResultSet(Set activeResultSets) {
            super(activeResultSets);
        }

        public void setProxyStatement(Statement proxyStatement) {
            this.proxyStatement = proxyStatement;
        }

        public Statement getStatement() throws SQLException {
            return this.proxyStatement == null ? super.getStatement() : this.proxyStatement;
        }
    }
}

