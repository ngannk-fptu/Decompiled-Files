/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mchange.v1.db.sql.ConnectionUtils
 *  com.mchange.v1.db.sql.ResultSetUtils
 *  com.mchange.v1.db.sql.StatementUtils
 *  com.mchange.v1.lang.BooleanUtils
 *  com.mchange.v2.async.AsynchronousRunner
 *  com.mchange.v2.async.ThreadPoolAsynchronousRunner
 *  com.mchange.v2.coalesce.CoalesceChecker
 *  com.mchange.v2.coalesce.Coalescer
 *  com.mchange.v2.coalesce.CoalescerFactory
 *  com.mchange.v2.log.MLevel
 *  com.mchange.v2.log.MLog
 *  com.mchange.v2.log.MLogger
 *  com.mchange.v2.sql.SqlUtils
 */
package com.mchange.v2.c3p0.impl;

import com.mchange.v1.db.sql.ConnectionUtils;
import com.mchange.v1.db.sql.ResultSetUtils;
import com.mchange.v1.db.sql.StatementUtils;
import com.mchange.v1.lang.BooleanUtils;
import com.mchange.v2.async.AsynchronousRunner;
import com.mchange.v2.async.ThreadPoolAsynchronousRunner;
import com.mchange.v2.c3p0.C3P0Registry;
import com.mchange.v2.c3p0.ConnectionCustomizer;
import com.mchange.v2.c3p0.ConnectionTester;
import com.mchange.v2.c3p0.cfg.C3P0Config;
import com.mchange.v2.c3p0.cfg.C3P0ConfigUtils;
import com.mchange.v2.c3p0.impl.C3P0Defaults;
import com.mchange.v2.c3p0.impl.C3P0ImplUtils;
import com.mchange.v2.c3p0.impl.C3P0PooledConnectionPool;
import com.mchange.v2.c3p0.impl.DbAuth;
import com.mchange.v2.c3p0.impl.IdentityTokenizedCoalesceChecker;
import com.mchange.v2.coalesce.CoalesceChecker;
import com.mchange.v2.coalesce.Coalescer;
import com.mchange.v2.coalesce.CoalescerFactory;
import com.mchange.v2.log.MLevel;
import com.mchange.v2.log.MLog;
import com.mchange.v2.log.MLogger;
import com.mchange.v2.resourcepool.BasicResourcePoolFactory;
import com.mchange.v2.resourcepool.ResourcePoolFactory;
import com.mchange.v2.sql.SqlUtils;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.PooledConnection;

public final class C3P0PooledConnectionPoolManager {
    private static final MLogger logger = MLog.getLogger(C3P0PooledConnectionPoolManager.class);
    private static final boolean POOL_EVENT_SUPPORT = false;
    private static final CoalesceChecker COALESCE_CHECKER = IdentityTokenizedCoalesceChecker.INSTANCE;
    static final Coalescer COALESCER = CoalescerFactory.createCoalescer((CoalesceChecker)COALESCE_CHECKER, (boolean)true, (boolean)false);
    static final int DFLT_NUM_TASK_THREADS_PER_DATA_SOURCE = 3;
    ThreadPoolAsynchronousRunner taskRunner;
    ThreadPoolAsynchronousRunner deferredStatementDestroyer;
    Timer timer;
    ResourcePoolFactory rpfact;
    Map authsToPools;
    final ConnectionPoolDataSource cpds;
    final Map propNamesToReadMethods;
    final Map flatPropertyOverrides;
    final Map userOverrides;
    final DbAuth defaultAuth;
    final String parentDataSourceIdentityToken;
    final String parentDataSourceName;
    int num_task_threads = 3;

    public int getThreadPoolSize() {
        return this.taskRunner.getThreadCount();
    }

    public int getThreadPoolNumActiveThreads() {
        return this.taskRunner.getActiveCount();
    }

    public int getThreadPoolNumIdleThreads() {
        return this.taskRunner.getIdleCount();
    }

    public int getThreadPoolNumTasksPending() {
        return this.taskRunner.getPendingTaskCount();
    }

    public String getThreadPoolStackTraces() {
        return this.taskRunner.getStackTraces();
    }

    public String getThreadPoolStatus() {
        return this.taskRunner.getStatus();
    }

    public int getStatementDestroyerNumThreads() {
        return this.deferredStatementDestroyer != null ? this.deferredStatementDestroyer.getThreadCount() : -1;
    }

    public int getStatementDestroyerNumActiveThreads() {
        return this.deferredStatementDestroyer != null ? this.deferredStatementDestroyer.getActiveCount() : -1;
    }

    public int getStatementDestroyerNumIdleThreads() {
        return this.deferredStatementDestroyer != null ? this.deferredStatementDestroyer.getIdleCount() : -1;
    }

    public int getStatementDestroyerNumTasksPending() {
        return this.deferredStatementDestroyer != null ? this.deferredStatementDestroyer.getPendingTaskCount() : -1;
    }

    public String getStatementDestroyerStackTraces() {
        return this.deferredStatementDestroyer != null ? this.deferredStatementDestroyer.getStackTraces() : null;
    }

    public String getStatementDestroyerStatus() {
        return this.deferredStatementDestroyer != null ? this.deferredStatementDestroyer.getStatus() : null;
    }

    private ThreadPoolAsynchronousRunner createTaskRunner(int num_threads, int matt, Timer timer, String threadLabel) {
        ThreadPoolAsynchronousRunner out = null;
        if (matt > 0) {
            int matt_ms = matt * 1000;
            out = new ThreadPoolAsynchronousRunner(num_threads, true, matt_ms, matt_ms * 3, matt_ms * 6, timer, threadLabel);
        } else {
            out = new ThreadPoolAsynchronousRunner(num_threads, true, timer, threadLabel);
        }
        return out;
    }

    private String idString() {
        StringBuffer sb = new StringBuffer(512);
        sb.append("C3P0PooledConnectionPoolManager");
        sb.append('[');
        sb.append("identityToken->");
        sb.append(this.parentDataSourceIdentityToken);
        if (this.parentDataSourceIdentityToken == null || !this.parentDataSourceIdentityToken.equals(this.parentDataSourceName)) {
            sb.append(", dataSourceName->");
            sb.append(this.parentDataSourceName);
        }
        sb.append(']');
        return sb.toString();
    }

    private void maybePrivilegedPoolsInit(boolean privilege_spawned_threads) {
        if (privilege_spawned_threads) {
            PrivilegedAction<Void> privilegedPoolsInit = new PrivilegedAction<Void>(){

                @Override
                public Void run() {
                    C3P0PooledConnectionPoolManager.this._poolsInit();
                    return null;
                }
            };
            AccessController.doPrivileged(privilegedPoolsInit);
        } else {
            this._poolsInit();
        }
    }

    private void poolsInit() {
        block7: {
            final boolean privilege_spawned_threads = this.getPrivilegeSpawnedThreads();
            String contextClassLoaderSource = this.getContextClassLoaderSource();
            try {
                class ContextClassLoaderPoolsInitThread
                extends Thread {
                    ContextClassLoaderPoolsInitThread(ClassLoader ccl) {
                        this.setContextClassLoader(ccl);
                    }

                    @Override
                    public void run() {
                        C3P0PooledConnectionPoolManager.this.maybePrivilegedPoolsInit(privilege_spawned_threads);
                    }
                }
                if ("library".equalsIgnoreCase(contextClassLoaderSource)) {
                    ContextClassLoaderPoolsInitThread t = new ContextClassLoaderPoolsInitThread(this.getClass().getClassLoader());
                    t.start();
                    t.join();
                } else if ("none".equalsIgnoreCase(contextClassLoaderSource)) {
                    ContextClassLoaderPoolsInitThread t = new ContextClassLoaderPoolsInitThread(null);
                    t.start();
                    t.join();
                } else {
                    if (logger.isLoggable(MLevel.WARNING) && !"caller".equalsIgnoreCase(contextClassLoaderSource)) {
                        logger.log(MLevel.WARNING, "Unknown contextClassLoaderSource: " + contextClassLoaderSource + " -- should be 'caller', 'library', or 'none'. Using default value 'caller'.");
                    }
                    this.maybePrivilegedPoolsInit(privilege_spawned_threads);
                }
            }
            catch (InterruptedException e) {
                if (!logger.isLoggable(MLevel.SEVERE)) break block7;
                logger.log(MLevel.SEVERE, "Unexpected interruption while trying to initialize DataSource Thread resources [ poolsInit() ].", (Throwable)e);
            }
        }
    }

    private synchronized void _poolsInit() {
        String idStr = this.idString();
        this.timer = new Timer(idStr + "-AdminTaskTimer", true);
        int matt = this.getMaxAdministrativeTaskTime();
        this.taskRunner = this.createTaskRunner(this.num_task_threads, matt, this.timer, idStr + "-HelperThread");
        int num_deferred_close_threads = this.getStatementCacheNumDeferredCloseThreads();
        this.deferredStatementDestroyer = num_deferred_close_threads > 0 ? this.createTaskRunner(num_deferred_close_threads, matt, this.timer, idStr + "-DeferredStatementDestroyerThread") : null;
        this.rpfact = BasicResourcePoolFactory.createNoEventSupportInstance((AsynchronousRunner)this.taskRunner, this.timer);
        this.authsToPools = new HashMap();
    }

    private void poolsDestroy() {
        this.poolsDestroy(true);
    }

    private synchronized void poolsDestroy(boolean close_outstanding_connections) {
        Iterator ii = this.authsToPools.values().iterator();
        while (ii.hasNext()) {
            try {
                ((C3P0PooledConnectionPool)ii.next()).close(close_outstanding_connections);
            }
            catch (Exception e) {
                logger.log(MLevel.WARNING, "An Exception occurred while trying to clean up a pool!", (Throwable)e);
            }
        }
        this.taskRunner.close(true);
        if (this.deferredStatementDestroyer != null) {
            this.deferredStatementDestroyer.close(false);
        }
        this.timer.cancel();
        this.taskRunner = null;
        this.timer = null;
        this.rpfact = null;
        this.authsToPools = null;
    }

    public C3P0PooledConnectionPoolManager(ConnectionPoolDataSource cpds, Map flatPropertyOverrides, Map forceUserOverrides, int num_task_threads, String parentDataSourceIdentityToken, String parentDataSourceName) throws SQLException {
        try {
            this.cpds = cpds;
            this.flatPropertyOverrides = flatPropertyOverrides;
            this.num_task_threads = num_task_threads;
            this.parentDataSourceIdentityToken = parentDataSourceIdentityToken;
            this.parentDataSourceName = parentDataSourceName;
            DbAuth auth = null;
            if (flatPropertyOverrides != null) {
                String overrideUser = (String)flatPropertyOverrides.get("overrideDefaultUser");
                String overridePassword = (String)flatPropertyOverrides.get("overrideDefaultPassword");
                if (overrideUser == null) {
                    overrideUser = (String)flatPropertyOverrides.get("user");
                    overridePassword = (String)flatPropertyOverrides.get("password");
                }
                if (overrideUser != null) {
                    auth = new DbAuth(overrideUser, overridePassword);
                }
            }
            if (auth == null) {
                auth = C3P0ImplUtils.findAuth(cpds);
            }
            this.defaultAuth = auth;
            HashMap<String, Method> tmp = new HashMap<String, Method>();
            BeanInfo bi = Introspector.getBeanInfo(cpds.getClass());
            PropertyDescriptor[] pds = bi.getPropertyDescriptors();
            PropertyDescriptor pd2 = null;
            for (PropertyDescriptor pd2 : pds) {
                String name = pd2.getName();
                Method m = pd2.getReadMethod();
                if (m == null) continue;
                tmp.put(name, m);
            }
            this.propNamesToReadMethods = tmp;
            if (forceUserOverrides == null) {
                Method uom = (Method)this.propNamesToReadMethods.get("userOverridesAsString");
                if (uom != null) {
                    Map uo;
                    String uoas = (String)uom.invoke((Object)cpds, (Object[])null);
                    this.userOverrides = uo = C3P0ImplUtils.parseUserOverridesAsString(uoas);
                } else {
                    this.userOverrides = Collections.EMPTY_MAP;
                }
            } else {
                this.userOverrides = forceUserOverrides;
            }
            this.poolsInit();
        }
        catch (Exception e) {
            logger.log(MLevel.FINE, null, (Throwable)e);
            throw SqlUtils.toSQLException((Throwable)e);
        }
    }

    public synchronized C3P0PooledConnectionPool getPool(String username, String password, boolean create) throws SQLException {
        if (create) {
            return this.getPool(username, password);
        }
        DbAuth checkAuth = new DbAuth(username, password);
        C3P0PooledConnectionPool out = (C3P0PooledConnectionPool)this.authsToPools.get(checkAuth);
        if (out == null) {
            throw new SQLException("No pool has been initialized for databse user '" + username + "' with the specified password.");
        }
        return out;
    }

    public C3P0PooledConnectionPool getPool(String username, String password) throws SQLException {
        return this.getPool(new DbAuth(username, password));
    }

    public synchronized C3P0PooledConnectionPool getPool(DbAuth auth) throws SQLException {
        C3P0PooledConnectionPool out = (C3P0PooledConnectionPool)this.authsToPools.get(auth);
        if (out == null) {
            out = this.createPooledConnectionPool(auth);
            this.authsToPools.put(auth, out);
            if (logger.isLoggable(MLevel.FINE)) {
                logger.log(MLevel.FINE, "Created new pool for auth, username (masked): '" + auth.getMaskedUserString() + "'.");
            }
        }
        return out;
    }

    public synchronized Set getManagedAuths() {
        return Collections.unmodifiableSet(this.authsToPools.keySet());
    }

    public synchronized int getNumManagedAuths() {
        return this.authsToPools.size();
    }

    public C3P0PooledConnectionPool getPool() throws SQLException {
        return this.getPool(this.defaultAuth);
    }

    public synchronized int getNumIdleConnectionsAllAuths() throws SQLException {
        int out = 0;
        Iterator ii = this.authsToPools.values().iterator();
        while (ii.hasNext()) {
            out += ((C3P0PooledConnectionPool)ii.next()).getNumIdleConnections();
        }
        return out;
    }

    public synchronized int getNumBusyConnectionsAllAuths() throws SQLException {
        int out = 0;
        Iterator ii = this.authsToPools.values().iterator();
        while (ii.hasNext()) {
            out += ((C3P0PooledConnectionPool)ii.next()).getNumBusyConnections();
        }
        return out;
    }

    public synchronized int getNumConnectionsAllAuths() throws SQLException {
        int out = 0;
        Iterator ii = this.authsToPools.values().iterator();
        while (ii.hasNext()) {
            out += ((C3P0PooledConnectionPool)ii.next()).getNumConnections();
        }
        return out;
    }

    public synchronized int getNumUnclosedOrphanedConnectionsAllAuths() throws SQLException {
        int out = 0;
        Iterator ii = this.authsToPools.values().iterator();
        while (ii.hasNext()) {
            out += ((C3P0PooledConnectionPool)ii.next()).getNumUnclosedOrphanedConnections();
        }
        return out;
    }

    public synchronized int getStatementCacheNumStatementsAllUsers() throws SQLException {
        int out = 0;
        Iterator ii = this.authsToPools.values().iterator();
        while (ii.hasNext()) {
            out += ((C3P0PooledConnectionPool)ii.next()).getStatementCacheNumStatements();
        }
        return out;
    }

    public synchronized int getStatementCacheNumCheckedOutStatementsAllUsers() throws SQLException {
        int out = 0;
        Iterator ii = this.authsToPools.values().iterator();
        while (ii.hasNext()) {
            out += ((C3P0PooledConnectionPool)ii.next()).getStatementCacheNumCheckedOut();
        }
        return out;
    }

    public synchronized int getStatementCacheNumConnectionsWithCachedStatementsAllUsers() throws SQLException {
        int out = 0;
        Iterator ii = this.authsToPools.values().iterator();
        while (ii.hasNext()) {
            out += ((C3P0PooledConnectionPool)ii.next()).getStatementCacheNumConnectionsWithCachedStatements();
        }
        return out;
    }

    public synchronized int getStatementDestroyerNumConnectionsInUseAllUsers() throws SQLException {
        if (this.deferredStatementDestroyer != null) {
            int out = 0;
            Iterator ii = this.authsToPools.values().iterator();
            while (ii.hasNext()) {
                out += ((C3P0PooledConnectionPool)ii.next()).getStatementDestroyerNumConnectionsInUse();
            }
            return out;
        }
        return -1;
    }

    public synchronized int getStatementDestroyerNumConnectionsWithDeferredDestroyStatementsAllUsers() throws SQLException {
        if (this.deferredStatementDestroyer != null) {
            int out = 0;
            Iterator ii = this.authsToPools.values().iterator();
            while (ii.hasNext()) {
                out += ((C3P0PooledConnectionPool)ii.next()).getStatementDestroyerNumConnectionsWithDeferredDestroyStatements();
            }
            return out;
        }
        return -1;
    }

    public synchronized int getStatementDestroyerNumDeferredDestroyStatementsAllUsers() throws SQLException {
        if (this.deferredStatementDestroyer != null) {
            int out = 0;
            Iterator ii = this.authsToPools.values().iterator();
            while (ii.hasNext()) {
                out += ((C3P0PooledConnectionPool)ii.next()).getStatementDestroyerNumDeferredDestroyStatements();
            }
            return out;
        }
        return -1;
    }

    public synchronized void softResetAllAuths() throws SQLException {
        Iterator ii = this.authsToPools.values().iterator();
        while (ii.hasNext()) {
            ((C3P0PooledConnectionPool)ii.next()).reset();
        }
    }

    public void close() {
        this.close(true);
    }

    public synchronized void close(boolean close_outstanding_connections) {
        if (this.authsToPools != null) {
            this.poolsDestroy(close_outstanding_connections);
        }
    }

    protected synchronized void finalize() {
        this.close();
    }

    private Object getObject(String propName, String userName) {
        Object out;
        block7: {
            out = null;
            if (userName != null) {
                out = C3P0ConfigUtils.extractUserOverride(propName, userName, this.userOverrides);
            }
            if (out == null && this.flatPropertyOverrides != null) {
                out = this.flatPropertyOverrides.get(propName);
            }
            if (out == null) {
                try {
                    Object readProp;
                    Method m = (Method)this.propNamesToReadMethods.get(propName);
                    if (m != null && (readProp = m.invoke((Object)this.cpds, (Object[])null)) != null) {
                        out = readProp.toString();
                    }
                }
                catch (Exception e) {
                    if (!logger.isLoggable(MLevel.WARNING)) break block7;
                    logger.log(MLevel.WARNING, "An exception occurred while trying to read property '" + propName + "' from ConnectionPoolDataSource: " + this.cpds + ". Default config value will be used.", (Throwable)e);
                }
            }
        }
        if (out == null) {
            out = C3P0Config.getUnspecifiedUserProperty(propName, null);
        }
        return out;
    }

    private String getString(String propName, String userName) {
        Object o = this.getObject(propName, userName);
        return o == null ? null : o.toString();
    }

    private int getInt(String propName, String userName) throws Exception {
        Object o = this.getObject(propName, userName);
        if (o instanceof Integer) {
            return (Integer)o;
        }
        if (o instanceof String) {
            return Integer.parseInt((String)o);
        }
        throw new Exception("Unexpected object found for putative int property '" + propName + "': " + o);
    }

    private boolean getBoolean(String propName, String userName) throws Exception {
        Object o = this.getObject(propName, userName);
        if (o instanceof Boolean) {
            return (Boolean)o;
        }
        if (o instanceof String) {
            return BooleanUtils.parseBoolean((String)((String)o));
        }
        throw new Exception("Unexpected object found for putative boolean property '" + propName + "': " + o);
    }

    public String getAutomaticTestTable(String userName) {
        return this.getString("automaticTestTable", userName);
    }

    public String getPreferredTestQuery(String userName) {
        return this.getString("preferredTestQuery", userName);
    }

    private int getInitialPoolSize(String userName) {
        try {
            return this.getInt("initialPoolSize", userName);
        }
        catch (Exception e) {
            if (logger.isLoggable(MLevel.FINE)) {
                logger.log(MLevel.FINE, "Could not fetch int property", (Throwable)e);
            }
            return C3P0Defaults.initialPoolSize();
        }
    }

    public int getMinPoolSize(String userName) {
        try {
            return this.getInt("minPoolSize", userName);
        }
        catch (Exception e) {
            if (logger.isLoggable(MLevel.FINE)) {
                logger.log(MLevel.FINE, "Could not fetch int property", (Throwable)e);
            }
            return C3P0Defaults.minPoolSize();
        }
    }

    private int getMaxPoolSize(String userName) {
        try {
            return this.getInt("maxPoolSize", userName);
        }
        catch (Exception e) {
            if (logger.isLoggable(MLevel.FINE)) {
                logger.log(MLevel.FINE, "Could not fetch int property", (Throwable)e);
            }
            return C3P0Defaults.maxPoolSize();
        }
    }

    private int getMaxStatements(String userName) {
        try {
            return this.getInt("maxStatements", userName);
        }
        catch (Exception e) {
            if (logger.isLoggable(MLevel.FINE)) {
                logger.log(MLevel.FINE, "Could not fetch int property", (Throwable)e);
            }
            return C3P0Defaults.maxStatements();
        }
    }

    private int getMaxStatementsPerConnection(String userName) {
        try {
            return this.getInt("maxStatementsPerConnection", userName);
        }
        catch (Exception e) {
            if (logger.isLoggable(MLevel.FINE)) {
                logger.log(MLevel.FINE, "Could not fetch int property", (Throwable)e);
            }
            return C3P0Defaults.maxStatementsPerConnection();
        }
    }

    private int getAcquireIncrement(String userName) {
        try {
            return this.getInt("acquireIncrement", userName);
        }
        catch (Exception e) {
            if (logger.isLoggable(MLevel.FINE)) {
                logger.log(MLevel.FINE, "Could not fetch int property", (Throwable)e);
            }
            return C3P0Defaults.acquireIncrement();
        }
    }

    private int getAcquireRetryAttempts(String userName) {
        try {
            return this.getInt("acquireRetryAttempts", userName);
        }
        catch (Exception e) {
            if (logger.isLoggable(MLevel.FINE)) {
                logger.log(MLevel.FINE, "Could not fetch int property", (Throwable)e);
            }
            return C3P0Defaults.acquireRetryAttempts();
        }
    }

    private int getAcquireRetryDelay(String userName) {
        try {
            return this.getInt("acquireRetryDelay", userName);
        }
        catch (Exception e) {
            if (logger.isLoggable(MLevel.FINE)) {
                logger.log(MLevel.FINE, "Could not fetch int property", (Throwable)e);
            }
            return C3P0Defaults.acquireRetryDelay();
        }
    }

    private boolean getBreakAfterAcquireFailure(String userName) {
        try {
            return this.getBoolean("breakAfterAcquireFailure", userName);
        }
        catch (Exception e) {
            if (logger.isLoggable(MLevel.FINE)) {
                logger.log(MLevel.FINE, "Could not fetch boolean property", (Throwable)e);
            }
            return C3P0Defaults.breakAfterAcquireFailure();
        }
    }

    private int getCheckoutTimeout(String userName) {
        try {
            return this.getInt("checkoutTimeout", userName);
        }
        catch (Exception e) {
            if (logger.isLoggable(MLevel.FINE)) {
                logger.log(MLevel.FINE, "Could not fetch int property", (Throwable)e);
            }
            return C3P0Defaults.checkoutTimeout();
        }
    }

    private int getIdleConnectionTestPeriod(String userName) {
        try {
            return this.getInt("idleConnectionTestPeriod", userName);
        }
        catch (Exception e) {
            if (logger.isLoggable(MLevel.FINE)) {
                logger.log(MLevel.FINE, "Could not fetch int property", (Throwable)e);
            }
            return C3P0Defaults.idleConnectionTestPeriod();
        }
    }

    private int getMaxIdleTime(String userName) {
        try {
            return this.getInt("maxIdleTime", userName);
        }
        catch (Exception e) {
            if (logger.isLoggable(MLevel.FINE)) {
                logger.log(MLevel.FINE, "Could not fetch int property", (Throwable)e);
            }
            return C3P0Defaults.maxIdleTime();
        }
    }

    private int getUnreturnedConnectionTimeout(String userName) {
        try {
            return this.getInt("unreturnedConnectionTimeout", userName);
        }
        catch (Exception e) {
            if (logger.isLoggable(MLevel.FINE)) {
                logger.log(MLevel.FINE, "Could not fetch int property", (Throwable)e);
            }
            return C3P0Defaults.unreturnedConnectionTimeout();
        }
    }

    private boolean getTestConnectionOnCheckout(String userName) {
        try {
            return this.getBoolean("testConnectionOnCheckout", userName);
        }
        catch (Exception e) {
            if (logger.isLoggable(MLevel.FINE)) {
                logger.log(MLevel.FINE, "Could not fetch boolean property", (Throwable)e);
            }
            return C3P0Defaults.testConnectionOnCheckout();
        }
    }

    private boolean getTestConnectionOnCheckin(String userName) {
        try {
            return this.getBoolean("testConnectionOnCheckin", userName);
        }
        catch (Exception e) {
            if (logger.isLoggable(MLevel.FINE)) {
                logger.log(MLevel.FINE, "Could not fetch boolean property", (Throwable)e);
            }
            return C3P0Defaults.testConnectionOnCheckin();
        }
    }

    private boolean getDebugUnreturnedConnectionStackTraces(String userName) {
        try {
            return this.getBoolean("debugUnreturnedConnectionStackTraces", userName);
        }
        catch (Exception e) {
            if (logger.isLoggable(MLevel.FINE)) {
                logger.log(MLevel.FINE, "Could not fetch boolean property", (Throwable)e);
            }
            return C3P0Defaults.debugUnreturnedConnectionStackTraces();
        }
    }

    private boolean getForceSynchronousCheckins(String userName) {
        try {
            return this.getBoolean("forceSynchronousCheckins", userName);
        }
        catch (Exception e) {
            if (logger.isLoggable(MLevel.FINE)) {
                logger.log(MLevel.FINE, "Could not fetch boolean property", (Throwable)e);
            }
            return C3P0Defaults.forceSynchronousCheckins();
        }
    }

    private String getConnectionTesterClassName(String userName) {
        return this.getString("connectionTesterClassName", userName);
    }

    private ConnectionTester getConnectionTester(String userName) {
        return C3P0Registry.getConnectionTester(this.getConnectionTesterClassName(userName));
    }

    private String getConnectionCustomizerClassName(String userName) {
        return this.getString("connectionCustomizerClassName", userName);
    }

    private ConnectionCustomizer getConnectionCustomizer(String userName) throws SQLException {
        return C3P0Registry.getConnectionCustomizer(this.getConnectionCustomizerClassName(userName));
    }

    private int getMaxIdleTimeExcessConnections(String userName) {
        try {
            return this.getInt("maxIdleTimeExcessConnections", userName);
        }
        catch (Exception e) {
            if (logger.isLoggable(MLevel.FINE)) {
                logger.log(MLevel.FINE, "Could not fetch int property", (Throwable)e);
            }
            return C3P0Defaults.maxIdleTimeExcessConnections();
        }
    }

    private int getMaxConnectionAge(String userName) {
        try {
            return this.getInt("maxConnectionAge", userName);
        }
        catch (Exception e) {
            if (logger.isLoggable(MLevel.FINE)) {
                logger.log(MLevel.FINE, "Could not fetch int property", (Throwable)e);
            }
            return C3P0Defaults.maxConnectionAge();
        }
    }

    private int getPropertyCycle(String userName) {
        try {
            return this.getInt("propertyCycle", userName);
        }
        catch (Exception e) {
            if (logger.isLoggable(MLevel.FINE)) {
                logger.log(MLevel.FINE, "Could not fetch int property", (Throwable)e);
            }
            return C3P0Defaults.propertyCycle();
        }
    }

    private String getContextClassLoaderSource() {
        try {
            return this.getString("contextClassLoaderSource", null);
        }
        catch (Exception e) {
            if (logger.isLoggable(MLevel.FINE)) {
                logger.log(MLevel.FINE, "Could not fetch String property", (Throwable)e);
            }
            return C3P0Defaults.contextClassLoaderSource();
        }
    }

    private boolean getPrivilegeSpawnedThreads() {
        try {
            return this.getBoolean("privilegeSpawnedThreads", null);
        }
        catch (Exception e) {
            if (logger.isLoggable(MLevel.FINE)) {
                logger.log(MLevel.FINE, "Could not fetch boolean property", (Throwable)e);
            }
            return C3P0Defaults.privilegeSpawnedThreads();
        }
    }

    private int getMaxAdministrativeTaskTime() {
        try {
            return this.getInt("maxAdministrativeTaskTime", null);
        }
        catch (Exception e) {
            if (logger.isLoggable(MLevel.FINE)) {
                logger.log(MLevel.FINE, "Could not fetch int property", (Throwable)e);
            }
            return C3P0Defaults.maxAdministrativeTaskTime();
        }
    }

    private int getStatementCacheNumDeferredCloseThreads() {
        try {
            return this.getInt("statementCacheNumDeferredCloseThreads", null);
        }
        catch (Exception e) {
            if (logger.isLoggable(MLevel.FINE)) {
                logger.log(MLevel.FINE, "Could not fetch int property", (Throwable)e);
            }
            return C3P0Defaults.statementCacheNumDeferredCloseThreads();
        }
    }

    private C3P0PooledConnectionPool createPooledConnectionPool(DbAuth auth) throws SQLException {
        String realTestQuery;
        String userName = auth.getUser();
        String automaticTestTable = this.getAutomaticTestTable(userName);
        if (automaticTestTable != null) {
            realTestQuery = this.initializeAutomaticTestTable(automaticTestTable, auth);
            if (this.getPreferredTestQuery(userName) != null && logger.isLoggable(MLevel.WARNING)) {
                logger.logp(MLevel.WARNING, C3P0PooledConnectionPoolManager.class.getName(), "createPooledConnectionPool", "[c3p0] Both automaticTestTable and preferredTestQuery have been set! Using automaticTestTable, and ignoring preferredTestQuery. Real test query is ''{0}''.", (Object)realTestQuery);
            }
        } else {
            if (!this.defaultAuth.equals(auth)) {
                this.ensureFirstConnectionAcquisition(auth);
            }
            realTestQuery = this.getPreferredTestQuery(userName);
        }
        C3P0PooledConnectionPool out = new C3P0PooledConnectionPool(this.cpds, auth, this.getMinPoolSize(userName), this.getMaxPoolSize(userName), this.getInitialPoolSize(userName), this.getAcquireIncrement(userName), this.getAcquireRetryAttempts(userName), this.getAcquireRetryDelay(userName), this.getBreakAfterAcquireFailure(userName), this.getCheckoutTimeout(userName), this.getIdleConnectionTestPeriod(userName), this.getMaxIdleTime(userName), this.getMaxIdleTimeExcessConnections(userName), this.getMaxConnectionAge(userName), this.getPropertyCycle(userName), this.getUnreturnedConnectionTimeout(userName), this.getDebugUnreturnedConnectionStackTraces(userName), this.getForceSynchronousCheckins(userName), this.getTestConnectionOnCheckout(userName), this.getTestConnectionOnCheckin(userName), this.getMaxStatements(userName), this.getMaxStatementsPerConnection(userName), this.getConnectionTester(userName), this.getConnectionCustomizer(userName), realTestQuery, this.rpfact, this.taskRunner, this.deferredStatementDestroyer, this.parentDataSourceIdentityToken);
        return out;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private String initializeAutomaticTestTable(String automaticTestTable, DbAuth auth) throws SQLException {
        String string;
        PooledConnection throwawayPooledConnection = auth.equals(this.defaultAuth) ? this.cpds.getPooledConnection() : this.cpds.getPooledConnection(auth.getUser(), auth.getPassword());
        Connection c = null;
        PreparedStatement testStmt = null;
        PreparedStatement createStmt = null;
        ResultSet mdrs = null;
        ResultSet rs = null;
        try {
            c = throwawayPooledConnection.getConnection();
            DatabaseMetaData dmd = c.getMetaData();
            String q = dmd.getIdentifierQuoteString();
            String quotedTableName = q + automaticTestTable + q;
            String out = "SELECT * FROM " + quotedTableName;
            mdrs = dmd.getTables(null, null, automaticTestTable, new String[]{"TABLE"});
            boolean exists = mdrs.next();
            if (exists) {
                testStmt = c.prepareStatement(out);
                rs = testStmt.executeQuery();
                boolean has_rows = rs.next();
                if (has_rows) {
                    throw new SQLException("automatic test table '" + automaticTestTable + "' contains rows, and it should not! Please set this parameter to the name of a table c3p0 can create on its own, that is not used elsewhere in the database!");
                }
            } else {
                String createSql = "CREATE TABLE " + quotedTableName + " ( a CHAR(1) )";
                try {
                    createStmt = c.prepareStatement(createSql);
                    createStmt.executeUpdate();
                }
                catch (SQLException e) {
                    if (logger.isLoggable(MLevel.WARNING)) {
                        logger.log(MLevel.WARNING, "An attempt to create an automatic test table failed. Create SQL: " + createSql, (Throwable)e);
                    }
                    throw e;
                }
            }
            string = out;
        }
        catch (Throwable throwable) {
            ResultSetUtils.attemptClose(mdrs);
            ResultSetUtils.attemptClose(rs);
            StatementUtils.attemptClose(testStmt);
            StatementUtils.attemptClose(createStmt);
            ConnectionUtils.attemptClose((Connection)c);
            try {
                if (throwawayPooledConnection != null) {
                    throwawayPooledConnection.close();
                }
            }
            catch (Exception e) {
                logger.log(MLevel.WARNING, "A PooledConnection failed to close.", (Throwable)e);
            }
            throw throwable;
        }
        ResultSetUtils.attemptClose((ResultSet)mdrs);
        ResultSetUtils.attemptClose((ResultSet)rs);
        StatementUtils.attemptClose((Statement)testStmt);
        StatementUtils.attemptClose((Statement)createStmt);
        ConnectionUtils.attemptClose((Connection)c);
        try {
            if (throwawayPooledConnection != null) {
                throwawayPooledConnection.close();
            }
        }
        catch (Exception e) {
            logger.log(MLevel.WARNING, "A PooledConnection failed to close.", (Throwable)e);
        }
        return string;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void ensureFirstConnectionAcquisition(DbAuth auth) throws SQLException {
        PooledConnection throwawayPooledConnection = auth.equals(this.defaultAuth) ? this.cpds.getPooledConnection() : this.cpds.getPooledConnection(auth.getUser(), auth.getPassword());
        Connection c = null;
        try {
            c = throwawayPooledConnection.getConnection();
        }
        finally {
            ConnectionUtils.attemptClose((Connection)c);
            try {
                if (throwawayPooledConnection != null) {
                    throwawayPooledConnection.close();
                }
            }
            catch (Exception e) {
                logger.log(MLevel.WARNING, "A PooledConnection failed to close.", (Throwable)e);
            }
        }
    }
}

