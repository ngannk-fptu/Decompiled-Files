/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.tomcat.jdbc.pool.interceptor;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import javax.management.ObjectName;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.jdbc.pool.ConnectionPool;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.apache.tomcat.jdbc.pool.PooledConnection;
import org.apache.tomcat.jdbc.pool.interceptor.StatementCacheMBean;
import org.apache.tomcat.jdbc.pool.interceptor.StatementDecoratorInterceptor;
import org.apache.tomcat.jdbc.pool.jmx.JmxUtil;

public class StatementCache
extends StatementDecoratorInterceptor
implements StatementCacheMBean {
    private static final Log log = LogFactory.getLog(StatementCache.class);
    protected static final String[] ALL_TYPES = new String[]{"prepareStatement", "prepareCall"};
    protected static final String[] CALLABLE_TYPE = new String[]{"prepareCall"};
    protected static final String[] PREPARED_TYPE = new String[]{"prepareStatement"};
    protected static final String[] NO_TYPE = new String[0];
    protected static final String STATEMENT_CACHE_ATTR = StatementCache.class.getName() + ".cache";
    private boolean cachePrepared = true;
    private boolean cacheCallable = false;
    private int maxCacheSize = 50;
    private PooledConnection pcon;
    private String[] types;
    private ObjectName oname = null;
    private static ConcurrentHashMap<ConnectionPool, AtomicInteger> cacheSizeMap = new ConcurrentHashMap();
    private AtomicInteger cacheSize;

    @Override
    public boolean isCachePrepared() {
        return this.cachePrepared;
    }

    @Override
    public boolean isCacheCallable() {
        return this.cacheCallable;
    }

    @Override
    public int getMaxCacheSize() {
        return this.maxCacheSize;
    }

    public String[] getTypes() {
        return this.types;
    }

    @Override
    public AtomicInteger getCacheSize() {
        return this.cacheSize;
    }

    @Override
    public void setProperties(Map<String, PoolProperties.InterceptorProperty> properties) {
        super.setProperties(properties);
        PoolProperties.InterceptorProperty p = properties.get("prepared");
        if (p != null) {
            this.cachePrepared = p.getValueAsBoolean(this.cachePrepared);
        }
        if ((p = properties.get("callable")) != null) {
            this.cacheCallable = p.getValueAsBoolean(this.cacheCallable);
        }
        if ((p = properties.get("max")) != null) {
            this.maxCacheSize = p.getValueAsInt(this.maxCacheSize);
        }
        this.types = this.cachePrepared && this.cacheCallable ? ALL_TYPES : (this.cachePrepared ? PREPARED_TYPE : (this.cacheCallable ? CALLABLE_TYPE : NO_TYPE));
    }

    @Override
    public void poolStarted(ConnectionPool pool) {
        cacheSizeMap.putIfAbsent(pool, new AtomicInteger(0));
        super.poolStarted(pool);
    }

    @Override
    public void poolClosed(ConnectionPool pool) {
        cacheSizeMap.remove(pool);
        super.poolClosed(pool);
    }

    @Override
    public void reset(ConnectionPool parent, PooledConnection con) {
        super.reset(parent, con);
        if (parent == null) {
            this.cacheSize = null;
            this.pcon = null;
            if (this.oname != null) {
                JmxUtil.unregisterJmx(this.oname);
                this.oname = null;
            }
        } else {
            this.cacheSize = cacheSizeMap.get(parent);
            this.pcon = con;
            if (!this.pcon.getAttributes().containsKey(STATEMENT_CACHE_ATTR)) {
                ConcurrentHashMap cache = new ConcurrentHashMap();
                this.pcon.getAttributes().put(STATEMENT_CACHE_ATTR, cache);
            }
            if (this.oname == null) {
                String keyprop = ",JdbcInterceptor=" + this.getClass().getSimpleName();
                this.oname = JmxUtil.registerJmx(this.pcon.getObjectName(), keyprop, this);
            }
        }
    }

    @Override
    public void disconnected(ConnectionPool parent, PooledConnection con, boolean finalizing) {
        ConcurrentHashMap statements = (ConcurrentHashMap)con.getAttributes().get(STATEMENT_CACHE_ATTR);
        if (statements != null) {
            for (Map.Entry p : statements.entrySet()) {
                this.closeStatement((CachedStatement)p.getValue());
            }
            statements.clear();
        }
        super.disconnected(parent, con, finalizing);
    }

    public void closeStatement(CachedStatement st) {
        if (st == null) {
            return;
        }
        st.forceClose();
    }

    @Override
    protected Object createDecorator(Object proxy, Method method, Object[] args, Object statement, Constructor<?> constructor, String sql) throws InstantiationException, IllegalAccessException, InvocationTargetException {
        boolean process = this.process(this.types, method, false);
        if (process) {
            Object result = null;
            CachedStatement statementProxy = new CachedStatement((PreparedStatement)statement, sql);
            result = constructor.newInstance(statementProxy);
            statementProxy.setActualProxy(result);
            statementProxy.setConnection(proxy);
            statementProxy.setConstructor(constructor);
            statementProxy.setCacheKey(this.createCacheKey(method, args));
            return result;
        }
        return super.createDecorator(proxy, method, args, statement, constructor, sql);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        boolean process = this.process(this.types, method, false);
        if (process && args.length > 0 && args[0] instanceof String) {
            CachedStatement statement = this.isCached(method, args);
            if (statement != null) {
                this.removeStatement(statement);
                return statement.getActualProxy();
            }
            return super.invoke(proxy, method, args);
        }
        return super.invoke(proxy, method, args);
    }

    public CachedStatement isCached(Method method, Object[] args) {
        ConcurrentHashMap<CacheKey, CachedStatement> cache = this.getCache();
        if (cache == null) {
            return null;
        }
        CacheKey key = this.createCacheKey(method, args);
        if (key == null) {
            throw new IllegalArgumentException("Null key");
        }
        return cache.get(key);
    }

    public boolean cacheStatement(CachedStatement proxy) {
        ConcurrentHashMap<CacheKey, CachedStatement> cache = this.getCache();
        if (cache == null) {
            return false;
        }
        if (proxy.getCacheKey() == null) {
            return false;
        }
        if (cache.containsKey(proxy.getCacheKey())) {
            return false;
        }
        if (this.cacheSize.get() >= this.maxCacheSize) {
            return false;
        }
        if (this.cacheSize.incrementAndGet() > this.maxCacheSize) {
            this.cacheSize.decrementAndGet();
            return false;
        }
        cache.put(proxy.getCacheKey(), proxy);
        return true;
    }

    public boolean removeStatement(CachedStatement proxy) {
        ConcurrentHashMap<CacheKey, CachedStatement> cache = this.getCache();
        if (cache == null) {
            return false;
        }
        if (cache.remove(proxy.getCacheKey()) != null) {
            this.cacheSize.decrementAndGet();
            return true;
        }
        return false;
    }

    protected ConcurrentHashMap<CacheKey, CachedStatement> getCache() {
        PooledConnection pCon = this.pcon;
        if (pCon == null) {
            if (log.isWarnEnabled()) {
                log.warn((Object)"Connection has already been closed or abandoned");
            }
            return null;
        }
        ConcurrentHashMap cache = (ConcurrentHashMap)pCon.getAttributes().get(STATEMENT_CACHE_ATTR);
        return cache;
    }

    @Override
    public int getCacheSizePerConnection() {
        ConcurrentHashMap<CacheKey, CachedStatement> cache = this.getCache();
        if (cache == null) {
            return 0;
        }
        return cache.size();
    }

    protected CacheKey createCacheKey(Method method, Object[] args) {
        return this.createCacheKey(method.getName(), args);
    }

    protected CacheKey createCacheKey(String methodName, Object[] args) {
        CacheKey key = null;
        if (this.compare("prepareStatement", methodName)) {
            key = new CacheKey("prepareStatement", args);
        } else if (this.compare("prepareCall", methodName)) {
            key = new CacheKey("prepareCall", args);
        }
        return key;
    }

    protected class CachedStatement
    extends StatementDecoratorInterceptor.StatementProxy<PreparedStatement> {
        CacheKey key;

        public CachedStatement(PreparedStatement parent, String sql) {
            super((StatementDecoratorInterceptor)StatementCache.this, (Statement)parent, sql);
        }

        @Override
        public void closeInvoked() {
            boolean shouldClose = true;
            if (StatementCache.this.cacheSize.get() < StatementCache.this.maxCacheSize) {
                CachedStatement proxy = new CachedStatement((PreparedStatement)this.getDelegate(), this.getSql());
                proxy.setCacheKey(this.getCacheKey());
                try {
                    ResultSet result = ((PreparedStatement)this.getDelegate()).getResultSet();
                    if (result != null && !result.isClosed()) {
                        result.close();
                    }
                    ((PreparedStatement)this.getDelegate()).clearParameters();
                    Object actualProxy = this.getConstructor().newInstance(proxy);
                    proxy.setActualProxy(actualProxy);
                    proxy.setConnection(this.getConnection());
                    proxy.setConstructor(this.getConstructor());
                    if (StatementCache.this.cacheStatement(proxy)) {
                        shouldClose = false;
                    }
                }
                catch (ReflectiveOperationException | RuntimeException | SQLException x) {
                    StatementCache.this.removeStatement(proxy);
                }
            }
            if (shouldClose) {
                super.closeInvoked();
            }
            this.closed = true;
            this.delegate = null;
        }

        public void forceClose() {
            StatementCache.this.removeStatement(this);
            super.closeInvoked();
        }

        public CacheKey getCacheKey() {
            return this.key;
        }

        public void setCacheKey(CacheKey cacheKey) {
            this.key = cacheKey;
        }
    }

    private static final class CacheKey {
        private final String stmtType;
        private final Object[] args;

        private CacheKey(String type, Object[] methodArgs) {
            this.stmtType = type;
            this.args = methodArgs;
        }

        public int hashCode() {
            int prime = 31;
            int result = 1;
            result = 31 * result + Arrays.deepHashCode(this.args);
            result = 31 * result + (this.stmtType == null ? 0 : this.stmtType.hashCode());
            return result;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            CacheKey other = (CacheKey)obj;
            if (!Arrays.deepEquals(this.args, other.args)) {
                return false;
            }
            return !(this.stmtType == null ? other.stmtType != null : !this.stmtType.equals(other.stmtType));
        }
    }
}

