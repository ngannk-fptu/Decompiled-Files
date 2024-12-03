/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.tomcat.jdbc.pool;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import javax.sql.XAConnection;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.jdbc.pool.DisposableConnectionFacade;
import org.apache.tomcat.jdbc.pool.FairBlockingQueue;
import org.apache.tomcat.jdbc.pool.JdbcInterceptor;
import org.apache.tomcat.jdbc.pool.MultiLockFairBlockingQueue;
import org.apache.tomcat.jdbc.pool.PoolConfiguration;
import org.apache.tomcat.jdbc.pool.PoolExhaustedException;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.apache.tomcat.jdbc.pool.PooledConnection;
import org.apache.tomcat.jdbc.pool.ProxyConnection;
import org.apache.tomcat.jdbc.pool.StatementFacade;

public class ConnectionPool {
    public static final String POOL_JMX_DOMAIN = "tomcat.jdbc";
    public static final String POOL_JMX_TYPE_PREFIX = "tomcat.jdbc:type=";
    private static final Log log = LogFactory.getLog(ConnectionPool.class);
    private AtomicInteger size = new AtomicInteger(0);
    private PoolConfiguration poolProperties;
    private BlockingQueue<PooledConnection> busy;
    private BlockingQueue<PooledConnection> idle;
    private volatile PoolCleaner poolCleaner;
    private volatile boolean closed = false;
    private Constructor<?> proxyClassConstructor;
    private ThreadPoolExecutor cancellator = new ThreadPoolExecutor(0, 1, 1000L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
    protected org.apache.tomcat.jdbc.pool.jmx.ConnectionPool jmxPool = null;
    private AtomicInteger waitcount = new AtomicInteger(0);
    private AtomicLong poolVersion = new AtomicLong(Long.MIN_VALUE);
    private final AtomicLong borrowedCount = new AtomicLong(0L);
    private final AtomicLong returnedCount = new AtomicLong(0L);
    private final AtomicLong createdCount = new AtomicLong(0L);
    private final AtomicLong releasedCount = new AtomicLong(0L);
    private final AtomicLong reconnectedCount = new AtomicLong(0L);
    private final AtomicLong removeAbandonedCount = new AtomicLong(0L);
    private final AtomicLong releasedIdleCount = new AtomicLong(0L);
    private static volatile Timer poolCleanTimer = null;
    private static Set<PoolCleaner> cleaners = new HashSet<PoolCleaner>();

    public ConnectionPool(PoolConfiguration prop) throws SQLException {
        this.init(prop);
    }

    public Future<Connection> getConnectionAsync() throws SQLException {
        Future<PooledConnection> pcf;
        block5: {
            try {
                PooledConnection pc = this.borrowConnection(0, null, null);
                if (pc != null) {
                    return new ConnectionFuture(pc);
                }
            }
            catch (SQLException x) {
                if (x.getMessage().indexOf("NoWait") >= 0) break block5;
                throw x;
            }
        }
        if (this.idle instanceof FairBlockingQueue) {
            pcf = ((FairBlockingQueue)this.idle).pollAsync();
            return new ConnectionFuture(pcf);
        }
        if (this.idle instanceof MultiLockFairBlockingQueue) {
            pcf = ((MultiLockFairBlockingQueue)this.idle).pollAsync();
            return new ConnectionFuture(pcf);
        }
        throw new SQLException("Connection pool is misconfigured, doesn't support async retrieval. Set the 'fair' property to 'true'");
    }

    public Connection getConnection() throws SQLException {
        PooledConnection con = this.borrowConnection(-1, null, null);
        return this.setupConnection(con);
    }

    public Connection getConnection(String username, String password) throws SQLException {
        PooledConnection con = this.borrowConnection(-1, username, password);
        return this.setupConnection(con);
    }

    public String getName() {
        return this.getPoolProperties().getPoolName();
    }

    public int getWaitCount() {
        return this.waitcount.get();
    }

    public PoolConfiguration getPoolProperties() {
        return this.poolProperties;
    }

    public int getSize() {
        return this.size.get();
    }

    public int getActive() {
        return this.busy.size();
    }

    public int getIdle() {
        return this.idle.size();
    }

    public boolean isClosed() {
        return this.closed;
    }

    protected Connection setupConnection(PooledConnection con) throws SQLException {
        JdbcInterceptor handler = con.getHandler();
        if (handler == null) {
            if (this.jmxPool != null) {
                con.createMBean();
            }
            handler = new ProxyConnection(this, con, this.getPoolProperties().isUseEquals());
            PoolProperties.InterceptorDefinition[] proxies = this.getPoolProperties().getJdbcInterceptorsAsArray();
            for (int i = proxies.length - 1; i >= 0; --i) {
                try {
                    JdbcInterceptor interceptor = proxies[i].getInterceptorClass().getConstructor(new Class[0]).newInstance(new Object[0]);
                    interceptor.setProperties(proxies[i].getProperties());
                    interceptor.setNext(handler);
                    interceptor.reset(this, con);
                    handler = interceptor;
                    continue;
                }
                catch (Exception x) {
                    SQLException sx = new SQLException("Unable to instantiate interceptor chain.");
                    sx.initCause(x);
                    throw sx;
                }
            }
            con.setHandler(handler);
        } else {
            for (JdbcInterceptor next = handler; next != null; next = next.getNext()) {
                next.reset(this, con);
            }
        }
        if (this.getPoolProperties().getUseStatementFacade()) {
            handler = new StatementFacade(handler);
        }
        try {
            this.getProxyConstructor(con.getXAConnection() != null);
            Connection connection = null;
            connection = this.getPoolProperties().getUseDisposableConnectionFacade() ? (Connection)this.proxyClassConstructor.newInstance(new DisposableConnectionFacade(handler)) : (Connection)this.proxyClassConstructor.newInstance(handler);
            return connection;
        }
        catch (Exception x) {
            SQLException s = new SQLException();
            s.initCause(x);
            throw s;
        }
    }

    public Constructor<?> getProxyConstructor(boolean xa) throws NoSuchMethodException {
        if (this.proxyClassConstructor == null) {
            Class<?> proxyClass = xa ? Proxy.getProxyClass(ConnectionPool.class.getClassLoader(), Connection.class, javax.sql.PooledConnection.class, XAConnection.class) : Proxy.getProxyClass(ConnectionPool.class.getClassLoader(), Connection.class, javax.sql.PooledConnection.class);
            this.proxyClassConstructor = proxyClass.getConstructor(InvocationHandler.class);
        }
        return this.proxyClassConstructor;
    }

    protected void close(boolean force) {
        BlockingQueue<PooledConnection> pool;
        if (this.closed) {
            return;
        }
        this.closed = true;
        if (this.poolCleaner != null) {
            this.poolCleaner.stopRunning();
        }
        BlockingQueue<PooledConnection> blockingQueue = !this.idle.isEmpty() ? this.idle : (pool = force ? this.busy : this.idle);
        while (!pool.isEmpty()) {
            block13: {
                try {
                    PooledConnection con = pool.poll(1000L, TimeUnit.MILLISECONDS);
                    while (con != null) {
                        if (pool == this.idle) {
                            this.release(con);
                        } else {
                            this.abandon(con);
                        }
                        if (!pool.isEmpty()) {
                            con = pool.poll(1000L, TimeUnit.MILLISECONDS);
                            continue;
                        }
                        break;
                    }
                }
                catch (InterruptedException ex) {
                    if (!this.getPoolProperties().getPropagateInterruptState()) break block13;
                    Thread.currentThread().interrupt();
                }
            }
            if (!pool.isEmpty() || !force || pool == this.busy) continue;
            pool = this.busy;
        }
        if (this.getPoolProperties().isJmxEnabled()) {
            this.jmxPool = null;
        }
        PoolProperties.InterceptorDefinition[] proxies = this.getPoolProperties().getJdbcInterceptorsAsArray();
        for (int i = 0; i < proxies.length; ++i) {
            try {
                JdbcInterceptor interceptor = proxies[i].getInterceptorClass().getConstructor(new Class[0]).newInstance(new Object[0]);
                interceptor.setProperties(proxies[i].getProperties());
                interceptor.poolClosed(this);
                continue;
            }
            catch (Exception x) {
                log.debug((Object)"Unable to inform interceptor of pool closure.", (Throwable)x);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void init(PoolConfiguration properties) throws SQLException {
        this.poolProperties = properties;
        this.checkPoolConfiguration(properties);
        this.busy = new LinkedBlockingQueue<PooledConnection>();
        this.idle = properties.isFairQueue() ? new FairBlockingQueue<PooledConnection>() : new LinkedBlockingQueue<PooledConnection>();
        this.initializePoolCleaner(properties);
        if (this.getPoolProperties().isJmxEnabled()) {
            this.createMBean();
        }
        PoolProperties.InterceptorDefinition[] proxies = this.getPoolProperties().getJdbcInterceptorsAsArray();
        for (int i = 0; i < proxies.length; ++i) {
            try {
                if (log.isDebugEnabled()) {
                    log.debug((Object)("Creating interceptor instance of class:" + proxies[i].getInterceptorClass()));
                }
                JdbcInterceptor interceptor = proxies[i].getInterceptorClass().getConstructor(new Class[0]).newInstance(new Object[0]);
                interceptor.setProperties(proxies[i].getProperties());
                interceptor.poolStarted(this);
                continue;
            }
            catch (Exception x) {
                log.error((Object)"Unable to inform interceptor of pool start.", (Throwable)x);
                if (this.jmxPool != null) {
                    this.jmxPool.notify("INIT FAILED", ConnectionPool.getStackTrace(x));
                }
                this.close(true);
                SQLException ex = new SQLException();
                ex.initCause(x);
                throw ex;
            }
        }
        PooledConnection[] initialPool = new PooledConnection[this.poolProperties.getInitialSize()];
        try {
            for (int i = 0; i < initialPool.length; ++i) {
                initialPool[i] = this.borrowConnection(0, null, null);
            }
        }
        catch (SQLException x) {
            log.error((Object)"Unable to create initial connections of pool.", (Throwable)x);
            if (!this.poolProperties.isIgnoreExceptionOnPreLoad()) {
                if (this.jmxPool != null) {
                    this.jmxPool.notify("INIT FAILED", ConnectionPool.getStackTrace(x));
                }
                this.close(true);
                throw x;
            }
        }
        finally {
            for (int i = 0; i < initialPool.length; ++i) {
                if (initialPool[i] == null) continue;
                try {
                    this.returnConnection(initialPool[i]);
                    continue;
                }
                catch (Exception exception) {}
            }
        }
        this.closed = false;
    }

    public void checkPoolConfiguration(PoolConfiguration properties) {
        if (properties.getMaxActive() < 1) {
            log.warn((Object)"maxActive is smaller than 1, setting maxActive to: 100");
            properties.setMaxActive(100);
        }
        if (properties.getMaxActive() < properties.getInitialSize()) {
            log.warn((Object)("initialSize is larger than maxActive, setting initialSize to: " + properties.getMaxActive()));
            properties.setInitialSize(properties.getMaxActive());
        }
        if (properties.getMinIdle() > properties.getMaxActive()) {
            log.warn((Object)("minIdle is larger than maxActive, setting minIdle to: " + properties.getMaxActive()));
            properties.setMinIdle(properties.getMaxActive());
        }
        if (properties.getMaxIdle() > properties.getMaxActive()) {
            log.warn((Object)("maxIdle is larger than maxActive, setting maxIdle to: " + properties.getMaxActive()));
            properties.setMaxIdle(properties.getMaxActive());
        }
        if (properties.getMaxIdle() < properties.getMinIdle()) {
            log.warn((Object)("maxIdle is smaller than minIdle, setting maxIdle to: " + properties.getMinIdle()));
            properties.setMaxIdle(properties.getMinIdle());
        }
        if (properties.getMaxAge() > 0L && properties.isPoolSweeperEnabled() && (long)properties.getTimeBetweenEvictionRunsMillis() > properties.getMaxAge()) {
            log.warn((Object)("timeBetweenEvictionRunsMillis is larger than maxAge, setting timeBetweenEvictionRunsMillis to: " + properties.getMaxAge()));
            properties.setTimeBetweenEvictionRunsMillis((int)properties.getMaxAge());
        }
    }

    public void initializePoolCleaner(PoolConfiguration properties) {
        if (properties.isPoolSweeperEnabled()) {
            this.poolCleaner = new PoolCleaner(this, properties.getTimeBetweenEvictionRunsMillis());
            this.poolCleaner.start();
        }
    }

    public void terminatePoolCleaner() {
        if (this.poolCleaner != null) {
            this.poolCleaner.stopRunning();
            this.poolCleaner = null;
        }
    }

    protected void abandon(PooledConnection con) {
        if (con == null) {
            return;
        }
        try {
            con.lock();
            String trace = con.getStackTrace();
            if (this.getPoolProperties().isLogAbandoned()) {
                log.warn((Object)("Connection has been abandoned " + con + ":" + trace));
            }
            if (this.jmxPool != null) {
                this.jmxPool.notify("CONNECTION ABANDONED", trace);
            }
            this.removeAbandonedCount.incrementAndGet();
            this.release(con);
        }
        finally {
            con.unlock();
        }
    }

    protected void suspect(PooledConnection con) {
        if (con == null) {
            return;
        }
        if (con.isSuspect()) {
            return;
        }
        try {
            con.lock();
            String trace = con.getStackTrace();
            if (this.getPoolProperties().isLogAbandoned()) {
                log.warn((Object)("Connection has been marked suspect, possibly abandoned " + con + "[" + (System.currentTimeMillis() - con.getTimestamp()) + " ms.]:" + trace));
            }
            if (this.jmxPool != null) {
                this.jmxPool.notify("SUSPECT CONNECTION ABANDONED", trace);
            }
            con.setSuspect(true);
        }
        finally {
            con.unlock();
        }
    }

    protected void release(PooledConnection con) {
        if (con == null) {
            return;
        }
        try {
            con.lock();
            if (con.release()) {
                this.size.addAndGet(-1);
                con.setHandler(null);
            }
            this.releasedCount.incrementAndGet();
        }
        finally {
            con.unlock();
        }
        if (this.waitcount.get() > 0 && !this.idle.offer(this.create(true))) {
            log.warn((Object)"Failed to add a new connection to the pool after releasing a connection when at least one thread was waiting for a connection.");
        }
    }

    private PooledConnection borrowConnection(int wait, String username, String password) throws SQLException {
        long timetowait;
        long maxWait;
        if (this.isClosed()) {
            throw new SQLException("Connection pool closed.");
        }
        long now = System.currentTimeMillis();
        PooledConnection con = (PooledConnection)this.idle.poll();
        do {
            if (con != null) {
                PooledConnection result = this.borrowConnection(now, con, username, password);
                this.borrowedCount.incrementAndGet();
                if (result != null) {
                    return result;
                }
            }
            if (this.size.get() < this.getPoolProperties().getMaxActive()) {
                if (this.size.addAndGet(1) > this.getPoolProperties().getMaxActive()) {
                    this.size.decrementAndGet();
                } else {
                    return this.createConnection(now, con, username, password);
                }
            }
            maxWait = wait;
            if (wait == -1) {
                maxWait = this.getPoolProperties().getMaxWait() <= 0 ? Long.MAX_VALUE : (long)this.getPoolProperties().getMaxWait();
            }
            timetowait = Math.max(0L, maxWait - (System.currentTimeMillis() - now));
            this.waitcount.incrementAndGet();
            try {
                con = this.idle.poll(timetowait, TimeUnit.MILLISECONDS);
            }
            catch (InterruptedException ex) {
                if (this.getPoolProperties().getPropagateInterruptState()) {
                    Thread.currentThread().interrupt();
                }
                SQLException sx = new SQLException("Pool wait interrupted.");
                sx.initCause(ex);
                throw sx;
            }
            finally {
                this.waitcount.decrementAndGet();
            }
            if (maxWait != 0L || con != null) continue;
            if (this.jmxPool != null) {
                this.jmxPool.notify("POOL EMPTY", "Pool empty - no wait.");
            }
            throw new PoolExhaustedException("[" + Thread.currentThread().getName() + "] NoWait: Pool empty. Unable to fetch a connection, none available[" + this.busy.size() + " in use].");
        } while (con != null || System.currentTimeMillis() - now < maxWait);
        if (this.jmxPool != null) {
            this.jmxPool.notify("POOL EMPTY", "Pool empty - timeout.");
        }
        throw new PoolExhaustedException("[" + Thread.currentThread().getName() + "] Timeout: Pool empty. Unable to fetch a connection in " + maxWait / 1000L + " seconds, none available[size:" + this.size.get() + "; busy:" + this.busy.size() + "; idle:" + this.idle.size() + "; lastwait:" + timetowait + "].");
    }

    protected PooledConnection createConnection(long now, PooledConnection notUsed, String username, String password) throws SQLException {
        PooledConnection con = this.create(false);
        if (username != null) {
            con.getAttributes().put("user", username);
        }
        if (password != null) {
            con.getAttributes().put("password", password);
        }
        boolean error = false;
        try {
            con.lock();
            con.connect();
            if (con.validate(4)) {
                con.setTimestamp(now);
                if (this.getPoolProperties().isLogAbandoned()) {
                    con.setStackTrace(ConnectionPool.getThreadDump());
                }
                if (!this.busy.offer(con)) {
                    log.debug((Object)"Connection doesn't fit into busy array, connection will not be traceable.");
                }
                this.createdCount.incrementAndGet();
                PooledConnection pooledConnection = con;
                return pooledConnection;
            }
            try {
                throw new SQLException("Validation Query Failed, enable logValidationErrors for more details.");
            }
            catch (Exception e) {
                error = true;
                if (log.isDebugEnabled()) {
                    log.debug((Object)"Unable to create a new JDBC connection.", (Throwable)e);
                }
                if (e instanceof SQLException) {
                    throw (SQLException)e;
                }
                SQLException ex = new SQLException(e.getMessage());
                ex.initCause(e);
                throw ex;
            }
        }
        finally {
            if (error) {
                this.release(con);
            }
            con.unlock();
        }
    }

    protected PooledConnection borrowConnection(long now, PooledConnection con, String username, String password) throws SQLException {
        boolean setToNull = false;
        try {
            int validationMode;
            boolean forceReconnect;
            con.lock();
            if (con.isReleased()) {
                PooledConnection pooledConnection = null;
                return pooledConnection;
            }
            boolean bl = forceReconnect = con.shouldForceReconnect(username, password) || con.isMaxAgeExpired();
            if (!con.isDiscarded() && !con.isInitialized()) {
                forceReconnect = true;
            }
            if (!forceReconnect && !con.isDiscarded() && con.validate(1)) {
                con.setTimestamp(now);
                if (this.getPoolProperties().isLogAbandoned()) {
                    con.setStackTrace(ConnectionPool.getThreadDump());
                }
                if (!this.busy.offer(con)) {
                    log.debug((Object)"Connection doesn't fit into busy array, connection will not be traceable.");
                }
                PooledConnection pooledConnection = con;
                return pooledConnection;
            }
            con.reconnect();
            this.reconnectedCount.incrementAndGet();
            int n = validationMode = this.isInitNewConnections() ? 4 : 1;
            if (con.validate(validationMode)) {
                con.setTimestamp(now);
                if (this.getPoolProperties().isLogAbandoned()) {
                    con.setStackTrace(ConnectionPool.getThreadDump());
                }
                if (!this.busy.offer(con)) {
                    log.debug((Object)"Connection doesn't fit into busy array, connection will not be traceable.");
                }
                PooledConnection pooledConnection = con;
                return pooledConnection;
            }
            try {
                throw new SQLException("Failed to validate a newly established connection.");
            }
            catch (Exception x) {
                this.release(con);
                setToNull = true;
                if (x instanceof SQLException) {
                    throw (SQLException)x;
                }
                SQLException ex = new SQLException(x.getMessage());
                ex.initCause(x);
                throw ex;
            }
        }
        finally {
            con.unlock();
            if (setToNull) {
                con = null;
            }
        }
    }

    private boolean isInitNewConnections() {
        return this.getPoolProperties().isTestOnConnect() || this.getPoolProperties().getInitSQL() != null;
    }

    protected boolean terminateTransaction(PooledConnection con) {
        try {
            if (Boolean.FALSE.equals(con.getPoolProperties().getDefaultAutoCommit())) {
                boolean autocommit;
                if (this.getPoolProperties().getRollbackOnReturn()) {
                    boolean autocommit2 = con.getConnection().getAutoCommit();
                    if (!autocommit2) {
                        con.getConnection().rollback();
                    }
                } else if (this.getPoolProperties().getCommitOnReturn() && !(autocommit = con.getConnection().getAutoCommit())) {
                    con.getConnection().commit();
                }
            }
            return true;
        }
        catch (SQLException x) {
            log.warn((Object)"Unable to terminate transaction, connection will be closed.", (Throwable)x);
            return false;
        }
    }

    protected boolean shouldClose(PooledConnection con, int action) {
        if (con.getConnectionVersion() < this.getPoolVersion()) {
            return true;
        }
        if (con.isDiscarded()) {
            return true;
        }
        if (this.isClosed()) {
            return true;
        }
        if (!con.validate(action)) {
            return true;
        }
        return !this.terminateTransaction(con);
    }

    protected boolean reconnectIfExpired(PooledConnection con) {
        if (con.isMaxAgeExpired()) {
            try {
                if (log.isDebugEnabled()) {
                    log.debug((Object)("Connection [" + this + "] expired because of maxAge, trying to reconnect"));
                }
                con.reconnect();
                this.reconnectedCount.incrementAndGet();
                if (this.isInitNewConnections() && !con.validate(4)) {
                    return false;
                }
            }
            catch (Exception e) {
                log.error((Object)("Failed to re-connect connection [" + this + "] that expired because of maxAge"), (Throwable)e);
                return false;
            }
        }
        return true;
    }

    protected void returnConnection(PooledConnection con) {
        if (this.isClosed()) {
            this.release(con);
            return;
        }
        if (con != null) {
            try {
                this.returnedCount.incrementAndGet();
                con.lock();
                if (con.isSuspect()) {
                    if (this.poolProperties.isLogAbandoned() && log.isInfoEnabled()) {
                        log.info((Object)("Connection(" + con + ") that has been marked suspect was returned. The processing time is " + (System.currentTimeMillis() - con.getTimestamp()) + " ms."));
                    }
                    if (this.jmxPool != null) {
                        this.jmxPool.notify("SUSPECT CONNECTION RETURNED", "Connection(" + con + ") that has been marked suspect was returned.");
                    }
                }
                if (this.busy.remove(con)) {
                    if (!this.shouldClose(con, 2) && this.reconnectIfExpired(con)) {
                        con.clearWarnings();
                        con.setStackTrace(null);
                        con.setTimestamp(System.currentTimeMillis());
                        if (this.idle.size() >= this.poolProperties.getMaxIdle() && !this.poolProperties.isPoolSweeperEnabled() || !this.idle.offer(con)) {
                            if (log.isDebugEnabled()) {
                                log.debug((Object)("Connection [" + con + "] will be closed and not returned to the pool, idle[" + this.idle.size() + "]>=maxIdle[" + this.poolProperties.getMaxIdle() + "] idle.offer failed."));
                            }
                            this.release(con);
                        }
                    } else {
                        if (log.isDebugEnabled()) {
                            log.debug((Object)("Connection [" + con + "] will be closed and not returned to the pool."));
                        }
                        this.release(con);
                    }
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug((Object)("Connection [" + con + "] will be closed and not returned to the pool, busy.remove failed."));
                    }
                    this.release(con);
                }
            }
            finally {
                con.unlock();
            }
        }
    }

    protected boolean shouldAbandon() {
        float perc;
        float max;
        if (!this.poolProperties.isRemoveAbandoned()) {
            return false;
        }
        if (this.poolProperties.getAbandonWhenPercentageFull() == 0) {
            return true;
        }
        float used = this.busy.size();
        return used / (max = (float)this.poolProperties.getMaxActive()) * 100.0f >= (perc = (float)this.poolProperties.getAbandonWhenPercentageFull());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void checkAbandoned() {
        try {
            if (this.busy.isEmpty()) {
                return;
            }
            Iterator locked = this.busy.iterator();
            int sto = this.getPoolProperties().getSuspectTimeout();
            while (locked.hasNext()) {
                PooledConnection con = (PooledConnection)locked.next();
                boolean setToNull = false;
                try {
                    con.lock();
                    if (this.idle.contains(con) || con.isReleased()) continue;
                    long time = con.getTimestamp();
                    long now = System.currentTimeMillis();
                    if (this.shouldAbandon() && now - time > con.getAbandonTimeout()) {
                        locked.remove();
                        this.abandon(con);
                        setToNull = true;
                        continue;
                    }
                    if (sto <= 0 || now - time <= (long)sto * 1000L) continue;
                    this.suspect(con);
                }
                finally {
                    con.unlock();
                    if (!setToNull) continue;
                    con = null;
                }
            }
        }
        catch (ConcurrentModificationException e) {
            log.debug((Object)"checkAbandoned failed.", (Throwable)e);
        }
        catch (Exception e) {
            log.warn((Object)"checkAbandoned failed, it will be retried.", (Throwable)e);
        }
    }

    public void checkIdle() {
        this.checkIdle(false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void checkIdle(boolean ignoreMinSize) {
        try {
            if (this.idle.isEmpty()) {
                return;
            }
            long now = System.currentTimeMillis();
            Iterator unlocked = this.idle.iterator();
            while ((ignoreMinSize || this.idle.size() >= this.getPoolProperties().getMinIdle()) && unlocked.hasNext()) {
                PooledConnection con = (PooledConnection)unlocked.next();
                boolean setToNull = false;
                try {
                    long time;
                    con.lock();
                    if (this.busy.contains(con) || !this.shouldReleaseIdle(now, con, time = con.getTimestamp())) continue;
                    this.releasedIdleCount.incrementAndGet();
                    this.release(con);
                    unlocked.remove();
                    setToNull = true;
                }
                finally {
                    con.unlock();
                    if (!setToNull) continue;
                    con = null;
                }
            }
        }
        catch (ConcurrentModificationException e) {
            log.debug((Object)"checkIdle failed.", (Throwable)e);
        }
        catch (Exception e) {
            log.warn((Object)"checkIdle failed, it will be retried.", (Throwable)e);
        }
    }

    protected boolean shouldReleaseIdle(long now, PooledConnection con, long time) {
        if (con.getConnectionVersion() < this.getPoolVersion()) {
            return true;
        }
        return con.getReleaseTime() > 0L && now - time > con.getReleaseTime() && this.getSize() > this.getPoolProperties().getMinIdle();
    }

    public void testAllIdle() {
        this.testAllIdle(false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void testAllIdle(boolean checkMaxAgeOnly) {
        try {
            if (this.idle.isEmpty()) {
                return;
            }
            Iterator unlocked = this.idle.iterator();
            while (unlocked.hasNext()) {
                PooledConnection con = (PooledConnection)unlocked.next();
                try {
                    boolean release;
                    con.lock();
                    if (this.busy.contains(con)) continue;
                    if (checkMaxAgeOnly) {
                        release = !this.reconnectIfExpired(con);
                    } else {
                        boolean bl = release = !this.reconnectIfExpired(con) || !con.validate(3);
                    }
                    if (!release) continue;
                    this.releasedIdleCount.incrementAndGet();
                    unlocked.remove();
                    this.release(con);
                }
                finally {
                    con.unlock();
                }
            }
        }
        catch (ConcurrentModificationException e) {
            log.debug((Object)"testAllIdle failed.", (Throwable)e);
        }
        catch (Exception e) {
            log.warn((Object)"testAllIdle failed, it will be retried.", (Throwable)e);
        }
    }

    protected static String getThreadDump() {
        Exception x = new Exception();
        x.fillInStackTrace();
        return ConnectionPool.getStackTrace(x);
    }

    public static String getStackTrace(Throwable x) {
        if (x == null) {
            return null;
        }
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        PrintStream writer = new PrintStream(bout);
        x.printStackTrace(writer);
        String result = bout.toString();
        return x.getMessage() != null && x.getMessage().length() > 0 ? x.getMessage() + ";" + result : result;
    }

    protected PooledConnection create(boolean incrementCounter) {
        if (incrementCounter) {
            this.size.incrementAndGet();
        }
        PooledConnection con = new PooledConnection(this.getPoolProperties(), this);
        return con;
    }

    public void purge() {
        this.purgeOnReturn();
        this.checkIdle(true);
    }

    public void purgeOnReturn() {
        this.poolVersion.incrementAndGet();
    }

    protected void finalize(PooledConnection con) {
        for (JdbcInterceptor handler = con.getHandler(); handler != null; handler = handler.getNext()) {
            handler.reset(null, null);
        }
    }

    protected void disconnectEvent(PooledConnection con, boolean finalizing) {
        for (JdbcInterceptor handler = con.getHandler(); handler != null; handler = handler.getNext()) {
            handler.disconnected(this, con, finalizing);
        }
    }

    public org.apache.tomcat.jdbc.pool.jmx.ConnectionPool getJmxPool() {
        return this.jmxPool;
    }

    protected void createMBean() {
        try {
            this.jmxPool = new org.apache.tomcat.jdbc.pool.jmx.ConnectionPool(this);
        }
        catch (Exception x) {
            log.warn((Object)("Unable to start JMX integration for connection pool. Instance[" + this.getName() + "] can't be monitored."), (Throwable)x);
        }
    }

    public long getBorrowedCount() {
        return this.borrowedCount.get();
    }

    public long getReturnedCount() {
        return this.returnedCount.get();
    }

    public long getCreatedCount() {
        return this.createdCount.get();
    }

    public long getReleasedCount() {
        return this.releasedCount.get();
    }

    public long getReconnectedCount() {
        return this.reconnectedCount.get();
    }

    public long getRemoveAbandonedCount() {
        return this.removeAbandonedCount.get();
    }

    public long getReleasedIdleCount() {
        return this.releasedIdleCount.get();
    }

    public void resetStats() {
        this.borrowedCount.set(0L);
        this.returnedCount.set(0L);
        this.createdCount.set(0L);
        this.releasedCount.set(0L);
        this.reconnectedCount.set(0L);
        this.removeAbandonedCount.set(0L);
        this.releasedIdleCount.set(0L);
    }

    private static synchronized void registerCleaner(PoolCleaner cleaner) {
        ConnectionPool.unregisterCleaner(cleaner);
        cleaners.add(cleaner);
        if (poolCleanTimer == null) {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            try {
                Thread.currentThread().setContextClassLoader(ConnectionPool.class.getClassLoader());
                PrivilegedNewTimer pa = new PrivilegedNewTimer();
                poolCleanTimer = AccessController.doPrivileged(pa);
            }
            finally {
                Thread.currentThread().setContextClassLoader(loader);
            }
        }
        poolCleanTimer.schedule((TimerTask)cleaner, cleaner.sleepTime, cleaner.sleepTime);
    }

    private static synchronized void unregisterCleaner(PoolCleaner cleaner) {
        boolean removed = cleaners.remove(cleaner);
        if (removed) {
            cleaner.cancel();
            if (poolCleanTimer != null) {
                poolCleanTimer.purge();
                if (cleaners.isEmpty()) {
                    poolCleanTimer.cancel();
                    poolCleanTimer = null;
                }
            }
        }
    }

    public static synchronized Set<TimerTask> getPoolCleaners() {
        return Collections.unmodifiableSet(cleaners);
    }

    public long getPoolVersion() {
        return this.poolVersion.get();
    }

    public static synchronized Timer getPoolTimer() {
        return poolCleanTimer;
    }

    protected static class PoolCleaner
    extends TimerTask {
        protected WeakReference<ConnectionPool> pool;
        protected long sleepTime;

        PoolCleaner(ConnectionPool pool, long sleepTime) {
            this.pool = new WeakReference<ConnectionPool>(pool);
            this.sleepTime = sleepTime;
            if (sleepTime <= 0L) {
                log.warn((Object)"Database connection pool evicter thread interval is set to 0, defaulting to 30 seconds");
                this.sleepTime = 30000L;
            } else if (sleepTime < 1000L) {
                log.warn((Object)"Database connection pool evicter thread interval is set to lower than 1 second.");
            }
        }

        @Override
        public void run() {
            ConnectionPool pool = (ConnectionPool)this.pool.get();
            if (pool == null) {
                this.stopRunning();
            } else if (!pool.isClosed()) {
                try {
                    if (pool.getPoolProperties().isRemoveAbandoned() || pool.getPoolProperties().getSuspectTimeout() > 0) {
                        pool.checkAbandoned();
                    }
                    if (pool.getPoolProperties().getMinIdle() < pool.idle.size()) {
                        pool.checkIdle();
                    }
                    if (pool.getPoolProperties().isTestWhileIdle()) {
                        pool.testAllIdle(false);
                    } else if (pool.getPoolProperties().getMaxAge() > 0L) {
                        pool.testAllIdle(true);
                    }
                }
                catch (Exception x) {
                    log.error((Object)"", (Throwable)x);
                }
            }
        }

        public void start() {
            ConnectionPool.registerCleaner(this);
        }

        public void stopRunning() {
            ConnectionPool.unregisterCleaner(this);
        }
    }

    protected class ConnectionFuture
    implements Future<Connection>,
    Runnable {
        Future<PooledConnection> pcFuture = null;
        AtomicBoolean configured = new AtomicBoolean(false);
        CountDownLatch latch = new CountDownLatch(1);
        volatile Connection result = null;
        SQLException cause = null;
        AtomicBoolean cancelled = new AtomicBoolean(false);
        volatile PooledConnection pc = null;

        public ConnectionFuture(Future<PooledConnection> pcf) {
            this.pcFuture = pcf;
        }

        public ConnectionFuture(PooledConnection pc) throws SQLException {
            this.pc = pc;
            this.result = ConnectionPool.this.setupConnection(pc);
            this.configured.set(true);
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            if (this.pc != null) {
                return false;
            }
            if (!this.cancelled.get() && this.cancelled.compareAndSet(false, true)) {
                ConnectionPool.this.cancellator.execute(this);
            }
            return true;
        }

        @Override
        public Connection get() throws InterruptedException, ExecutionException {
            try {
                return this.get(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
            }
            catch (TimeoutException x) {
                throw new ExecutionException(x);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         * Unable to fully structure code
         */
        @Override
        public Connection get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            v0 = pc = this.pc != null ? this.pc : this.pcFuture.get(timeout, unit);
            if (pc != null) {
                if (this.result != null) {
                    return this.result;
                }
                if (this.configured.compareAndSet(false, true)) {
                    try {
                        pc = ConnectionPool.this.borrowConnection(System.currentTimeMillis(), pc, null, null);
                        if (pc == null) ** GOTO lbl19
                        this.result = ConnectionPool.this.setupConnection(pc);
                    }
                    catch (SQLException x) {
                        this.cause = x;
                    }
                    finally {
                        this.latch.countDown();
                    }
                } else {
                    this.latch.await(timeout, unit);
                }
lbl19:
                // 4 sources

                if (this.result == null) {
                    throw new ExecutionException(this.cause);
                }
                return this.result;
            }
            return null;
        }

        @Override
        public boolean isCancelled() {
            return this.pc == null && (this.pcFuture.isCancelled() || this.cancelled.get());
        }

        @Override
        public boolean isDone() {
            return this.pc != null || this.pcFuture.isDone();
        }

        @Override
        public void run() {
            try {
                Connection con = this.get();
                if (con != null) {
                    con.close();
                }
            }
            catch (ExecutionException con) {
            }
            catch (Exception x) {
                log.error((Object)"Unable to cancel ConnectionFuture.", (Throwable)x);
            }
        }
    }

    private static class PrivilegedNewTimer
    implements PrivilegedAction<Timer> {
        private PrivilegedNewTimer() {
        }

        @Override
        public Timer run() {
            return new Timer("Tomcat JDBC Pool Cleaner[" + System.identityHashCode(ConnectionPool.class.getClassLoader()) + ":" + System.currentTimeMillis() + "]", true);
        }
    }
}

