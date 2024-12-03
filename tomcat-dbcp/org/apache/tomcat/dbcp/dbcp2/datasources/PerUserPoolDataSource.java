/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.tomcat.dbcp.dbcp2.datasources;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.NoSuchElementException;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.StringRefAddr;
import javax.sql.ConnectionPoolDataSource;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.dbcp.dbcp2.SwallowedExceptionLogger;
import org.apache.tomcat.dbcp.dbcp2.Utils;
import org.apache.tomcat.dbcp.dbcp2.datasources.CPDSConnectionFactory;
import org.apache.tomcat.dbcp.dbcp2.datasources.InstanceKeyDataSource;
import org.apache.tomcat.dbcp.dbcp2.datasources.InstanceKeyDataSourceFactory;
import org.apache.tomcat.dbcp.dbcp2.datasources.PerUserPoolDataSourceFactory;
import org.apache.tomcat.dbcp.dbcp2.datasources.PoolKey;
import org.apache.tomcat.dbcp.dbcp2.datasources.PooledConnectionAndInfo;
import org.apache.tomcat.dbcp.dbcp2.datasources.PooledConnectionManager;
import org.apache.tomcat.dbcp.dbcp2.datasources.UserPassKey;
import org.apache.tomcat.dbcp.pool2.ObjectPool;
import org.apache.tomcat.dbcp.pool2.impl.GenericObjectPool;

public class PerUserPoolDataSource
extends InstanceKeyDataSource {
    private static final long serialVersionUID = 7872747993848065028L;
    private static final Log log = LogFactory.getLog(PerUserPoolDataSource.class);
    private Map<String, Boolean> perUserBlockWhenExhausted;
    private Map<String, String> perUserEvictionPolicyClassName;
    private Map<String, Boolean> perUserLifo;
    private Map<String, Integer> perUserMaxIdle;
    private Map<String, Integer> perUserMaxTotal;
    private Map<String, Duration> perUserMaxWaitDuration;
    private Map<String, Duration> perUserMinEvictableIdleDuration;
    private Map<String, Integer> perUserMinIdle;
    private Map<String, Integer> perUserNumTestsPerEvictionRun;
    private Map<String, Duration> perUserSoftMinEvictableIdleDuration;
    private Map<String, Boolean> perUserTestOnCreate;
    private Map<String, Boolean> perUserTestOnBorrow;
    private Map<String, Boolean> perUserTestOnReturn;
    private Map<String, Boolean> perUserTestWhileIdle;
    private Map<String, Duration> perUserDurationBetweenEvictionRuns;
    private Map<String, Boolean> perUserDefaultAutoCommit;
    private Map<String, Integer> perUserDefaultTransactionIsolation;
    private Map<String, Boolean> perUserDefaultReadOnly;
    private transient Map<PoolKey, PooledConnectionManager> managers = PerUserPoolDataSource.createMap();

    private static <K, V> HashMap<K, V> createMap() {
        return new HashMap();
    }

    public void clear() {
        this.managers.values().forEach(manager -> {
            try {
                this.getCPDSConnectionFactoryPool((PooledConnectionManager)manager).clear();
            }
            catch (Exception exception) {
                // empty catch block
            }
        });
        InstanceKeyDataSourceFactory.removeInstance(this.getInstanceKey());
    }

    @Override
    public void close() {
        this.managers.values().forEach(manager -> Utils.closeQuietly(this.getCPDSConnectionFactoryPool((PooledConnectionManager)manager)));
        InstanceKeyDataSourceFactory.removeInstance(this.getInstanceKey());
    }

    private Map<String, Duration> convertMap(Map<String, Duration> currentMap, Map<String, Long> longMap) {
        HashMap<String, Duration> durationMap = PerUserPoolDataSource.createMap();
        longMap.forEach((k, v) -> durationMap.put((String)k, this.toDurationOrNull((Long)v)));
        if (currentMap == null) {
            return durationMap;
        }
        currentMap.clear();
        currentMap.putAll(durationMap);
        return currentMap;
    }

    @Override
    protected PooledConnectionManager getConnectionManager(UserPassKey upKey) {
        return this.managers.get(this.getPoolKey(upKey.getUserName()));
    }

    private ObjectPool<PooledConnectionAndInfo> getCPDSConnectionFactoryPool(PooledConnectionManager manager) {
        return ((CPDSConnectionFactory)manager).getPool();
    }

    public int getNumActive() {
        return this.getNumActive(null);
    }

    public int getNumActive(String userName) {
        ObjectPool<PooledConnectionAndInfo> pool = this.getPool(this.getPoolKey(userName));
        return pool == null ? 0 : pool.getNumActive();
    }

    public int getNumIdle() {
        return this.getNumIdle(null);
    }

    public int getNumIdle(String userName) {
        ObjectPool<PooledConnectionAndInfo> pool = this.getPool(this.getPoolKey(userName));
        return pool == null ? 0 : pool.getNumIdle();
    }

    public boolean getPerUserBlockWhenExhausted(String userName) {
        Boolean value = null;
        if (this.perUserBlockWhenExhausted != null) {
            value = this.perUserBlockWhenExhausted.get(userName);
        }
        if (value == null) {
            return this.getDefaultBlockWhenExhausted();
        }
        return value;
    }

    public Boolean getPerUserDefaultAutoCommit(String userName) {
        Boolean value = null;
        if (this.perUserDefaultAutoCommit != null) {
            value = this.perUserDefaultAutoCommit.get(userName);
        }
        return value;
    }

    public Boolean getPerUserDefaultReadOnly(String userName) {
        Boolean value = null;
        if (this.perUserDefaultReadOnly != null) {
            value = this.perUserDefaultReadOnly.get(userName);
        }
        return value;
    }

    public Integer getPerUserDefaultTransactionIsolation(String userName) {
        Integer value = null;
        if (this.perUserDefaultTransactionIsolation != null) {
            value = this.perUserDefaultTransactionIsolation.get(userName);
        }
        return value;
    }

    public Duration getPerUserDurationBetweenEvictionRuns(String userName) {
        Duration value = null;
        if (this.perUserDurationBetweenEvictionRuns != null) {
            value = this.perUserDurationBetweenEvictionRuns.get(userName);
        }
        if (value == null) {
            return this.getDefaultDurationBetweenEvictionRuns();
        }
        return value;
    }

    public String getPerUserEvictionPolicyClassName(String userName) {
        String value = null;
        if (this.perUserEvictionPolicyClassName != null) {
            value = this.perUserEvictionPolicyClassName.get(userName);
        }
        if (value == null) {
            return this.getDefaultEvictionPolicyClassName();
        }
        return value;
    }

    public boolean getPerUserLifo(String userName) {
        Boolean value = null;
        if (this.perUserLifo != null) {
            value = this.perUserLifo.get(userName);
        }
        if (value == null) {
            return this.getDefaultLifo();
        }
        return value;
    }

    public int getPerUserMaxIdle(String userName) {
        Integer value = null;
        if (this.perUserMaxIdle != null) {
            value = this.perUserMaxIdle.get(userName);
        }
        if (value == null) {
            return this.getDefaultMaxIdle();
        }
        return value;
    }

    public int getPerUserMaxTotal(String userName) {
        Integer value = null;
        if (this.perUserMaxTotal != null) {
            value = this.perUserMaxTotal.get(userName);
        }
        if (value == null) {
            return this.getDefaultMaxTotal();
        }
        return value;
    }

    public Duration getPerUserMaxWaitDuration(String userName) {
        Duration value = null;
        if (this.perUserMaxWaitDuration != null) {
            value = this.perUserMaxWaitDuration.get(userName);
        }
        if (value == null) {
            return this.getDefaultMaxWait();
        }
        return value;
    }

    @Deprecated
    public long getPerUserMaxWaitMillis(String userName) {
        return this.getPerUserMaxWaitDuration(userName).toMillis();
    }

    public Duration getPerUserMinEvictableIdleDuration(String userName) {
        Duration value = null;
        if (this.perUserMinEvictableIdleDuration != null) {
            value = this.perUserMinEvictableIdleDuration.get(userName);
        }
        if (value == null) {
            return this.getDefaultMinEvictableIdleDuration();
        }
        return value;
    }

    @Deprecated
    public long getPerUserMinEvictableIdleTimeMillis(String userName) {
        return this.getPerUserMinEvictableIdleDuration(userName).toMillis();
    }

    public int getPerUserMinIdle(String userName) {
        Integer value = null;
        if (this.perUserMinIdle != null) {
            value = this.perUserMinIdle.get(userName);
        }
        if (value == null) {
            return this.getDefaultMinIdle();
        }
        return value;
    }

    public int getPerUserNumTestsPerEvictionRun(String userName) {
        Integer value = null;
        if (this.perUserNumTestsPerEvictionRun != null) {
            value = this.perUserNumTestsPerEvictionRun.get(userName);
        }
        if (value == null) {
            return this.getDefaultNumTestsPerEvictionRun();
        }
        return value;
    }

    public Duration getPerUserSoftMinEvictableIdleDuration(String userName) {
        Duration value = null;
        if (this.perUserSoftMinEvictableIdleDuration != null) {
            value = this.perUserSoftMinEvictableIdleDuration.get(userName);
        }
        if (value == null) {
            return this.getDefaultSoftMinEvictableIdleDuration();
        }
        return value;
    }

    @Deprecated
    public long getPerUserSoftMinEvictableIdleTimeMillis(String userName) {
        return this.getPerUserSoftMinEvictableIdleDuration(userName).toMillis();
    }

    public boolean getPerUserTestOnBorrow(String userName) {
        Boolean value = null;
        if (this.perUserTestOnBorrow != null) {
            value = this.perUserTestOnBorrow.get(userName);
        }
        if (value == null) {
            return this.getDefaultTestOnBorrow();
        }
        return value;
    }

    public boolean getPerUserTestOnCreate(String userName) {
        Boolean value = null;
        if (this.perUserTestOnCreate != null) {
            value = this.perUserTestOnCreate.get(userName);
        }
        if (value == null) {
            return this.getDefaultTestOnCreate();
        }
        return value;
    }

    public boolean getPerUserTestOnReturn(String userName) {
        Boolean value = null;
        if (this.perUserTestOnReturn != null) {
            value = this.perUserTestOnReturn.get(userName);
        }
        if (value == null) {
            return this.getDefaultTestOnReturn();
        }
        return value;
    }

    public boolean getPerUserTestWhileIdle(String userName) {
        Boolean value = null;
        if (this.perUserTestWhileIdle != null) {
            value = this.perUserTestWhileIdle.get(userName);
        }
        if (value == null) {
            return this.getDefaultTestWhileIdle();
        }
        return value;
    }

    @Deprecated
    public long getPerUserTimeBetweenEvictionRunsMillis(String userName) {
        return this.getPerUserDurationBetweenEvictionRuns(userName).toMillis();
    }

    private ObjectPool<PooledConnectionAndInfo> getPool(PoolKey poolKey) {
        CPDSConnectionFactory mgr = (CPDSConnectionFactory)this.managers.get(poolKey);
        return mgr == null ? null : mgr.getPool();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected PooledConnectionAndInfo getPooledConnectionAndInfo(String userName, String password) throws SQLException {
        ObjectPool<PooledConnectionAndInfo> pool;
        PooledConnectionManager manager;
        PoolKey key = this.getPoolKey(userName);
        PerUserPoolDataSource perUserPoolDataSource = this;
        synchronized (perUserPoolDataSource) {
            manager = this.managers.get(key);
            if (manager == null) {
                try {
                    this.registerPool(userName, password);
                    manager = this.managers.get(key);
                }
                catch (NamingException e) {
                    throw new SQLException("RegisterPool failed", e);
                }
            }
            pool = this.getCPDSConnectionFactoryPool(manager);
        }
        PooledConnectionAndInfo info = null;
        try {
            info = pool.borrowObject();
        }
        catch (NoSuchElementException ex) {
            throw new SQLException("Could not retrieve connection info from pool", ex);
        }
        catch (Exception e) {
            try {
                this.testCPDS(userName, password);
            }
            catch (Exception ex) {
                throw new SQLException("Could not retrieve connection info from pool", ex);
            }
            manager.closePool(userName);
            PerUserPoolDataSource ex = this;
            synchronized (ex) {
                this.managers.remove(key);
            }
            try {
                this.registerPool(userName, password);
                pool = this.getPool(key);
            }
            catch (NamingException ne) {
                throw new SQLException("RegisterPool failed", ne);
            }
            try {
                info = pool.borrowObject();
            }
            catch (Exception ex2) {
                throw new SQLException("Could not retrieve connection info from pool", ex2);
            }
        }
        return info;
    }

    private PoolKey getPoolKey(String userName) {
        return new PoolKey(this.getDataSourceName(), userName);
    }

    @Override
    public Reference getReference() throws NamingException {
        Reference ref = new Reference(this.getClass().getName(), PerUserPoolDataSourceFactory.class.getName(), null);
        ref.add(new StringRefAddr("instanceKey", this.getInstanceKey()));
        return ref;
    }

    <K, V> Map<K, V> put(Map<K, V> map, K key, V value) {
        if (map == null) {
            map = PerUserPoolDataSource.createMap();
        }
        map.put(key, value);
        return map;
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        try {
            in.defaultReadObject();
            PerUserPoolDataSource oldDS = (PerUserPoolDataSource)new PerUserPoolDataSourceFactory().getObjectInstance((Object)this.getReference(), (Name)null, (Context)null, (Hashtable)null);
            this.managers = oldDS.managers;
        }
        catch (NamingException e) {
            throw new IOException("NamingException: " + e);
        }
    }

    private synchronized void registerPool(String userName, String password) throws NamingException, SQLException {
        ConnectionPoolDataSource cpds = this.testCPDS(userName, password);
        CPDSConnectionFactory factory = new CPDSConnectionFactory(cpds, this.getValidationQuery(), this.getValidationQueryTimeoutDuration(), this.isRollbackAfterValidation(), userName, password);
        factory.setMaxConn(this.getMaxConnDuration());
        GenericObjectPool<PooledConnectionAndInfo> pool = new GenericObjectPool<PooledConnectionAndInfo>(factory);
        factory.setPool(pool);
        pool.setBlockWhenExhausted(this.getPerUserBlockWhenExhausted(userName));
        pool.setEvictionPolicyClassName(this.getPerUserEvictionPolicyClassName(userName));
        pool.setLifo(this.getPerUserLifo(userName));
        pool.setMaxIdle(this.getPerUserMaxIdle(userName));
        pool.setMaxTotal(this.getPerUserMaxTotal(userName));
        pool.setMaxWait(Duration.ofMillis(this.getPerUserMaxWaitMillis(userName)));
        pool.setMinEvictableIdleDuration(this.getPerUserMinEvictableIdleDuration(userName));
        pool.setMinIdle(this.getPerUserMinIdle(userName));
        pool.setNumTestsPerEvictionRun(this.getPerUserNumTestsPerEvictionRun(userName));
        pool.setSoftMinEvictableIdleDuration(this.getPerUserSoftMinEvictableIdleDuration(userName));
        pool.setTestOnCreate(this.getPerUserTestOnCreate(userName));
        pool.setTestOnBorrow(this.getPerUserTestOnBorrow(userName));
        pool.setTestOnReturn(this.getPerUserTestOnReturn(userName));
        pool.setTestWhileIdle(this.getPerUserTestWhileIdle(userName));
        pool.setDurationBetweenEvictionRuns(this.getPerUserDurationBetweenEvictionRuns(userName));
        pool.setSwallowedExceptionListener(new SwallowedExceptionLogger(log));
        PooledConnectionManager old = this.managers.put(this.getPoolKey(userName), factory);
        if (old != null) {
            throw new IllegalStateException("Pool already contains an entry for this user/password: " + userName);
        }
    }

    private <K, V> Map<K, V> replaceAll(Map<K, V> currentMap, Map<K, V> newMap) {
        if (currentMap == null) {
            return new HashMap<K, V>(newMap);
        }
        currentMap.clear();
        currentMap.putAll(newMap);
        return currentMap;
    }

    void setPerUserBlockWhenExhausted(Map<String, Boolean> newMap) {
        this.assertInitializationAllowed();
        this.perUserBlockWhenExhausted = this.replaceAll(this.perUserBlockWhenExhausted, newMap);
    }

    public void setPerUserBlockWhenExhausted(String userName, Boolean value) {
        this.assertInitializationAllowed();
        this.perUserBlockWhenExhausted = this.put(this.perUserBlockWhenExhausted, userName, value);
    }

    void setPerUserDefaultAutoCommit(Map<String, Boolean> newMap) {
        this.assertInitializationAllowed();
        this.perUserDefaultAutoCommit = this.replaceAll(this.perUserDefaultAutoCommit, newMap);
    }

    public void setPerUserDefaultAutoCommit(String userName, Boolean value) {
        this.assertInitializationAllowed();
        this.perUserDefaultAutoCommit = this.put(this.perUserDefaultAutoCommit, userName, value);
    }

    void setPerUserDefaultReadOnly(Map<String, Boolean> newMap) {
        this.assertInitializationAllowed();
        this.perUserDefaultReadOnly = this.replaceAll(this.perUserDefaultReadOnly, newMap);
    }

    public void setPerUserDefaultReadOnly(String userName, Boolean value) {
        this.assertInitializationAllowed();
        this.perUserDefaultReadOnly = this.put(this.perUserDefaultReadOnly, userName, value);
    }

    void setPerUserDefaultTransactionIsolation(Map<String, Integer> newMap) {
        this.assertInitializationAllowed();
        this.perUserDefaultTransactionIsolation = this.replaceAll(this.perUserDefaultTransactionIsolation, newMap);
    }

    public void setPerUserDefaultTransactionIsolation(String userName, Integer value) {
        this.assertInitializationAllowed();
        this.perUserDefaultTransactionIsolation = this.put(this.perUserDefaultTransactionIsolation, userName, value);
    }

    void setPerUserDurationBetweenEvictionRuns(Map<String, Duration> newMap) {
        this.assertInitializationAllowed();
        this.perUserDurationBetweenEvictionRuns = this.replaceAll(this.perUserDurationBetweenEvictionRuns, newMap);
    }

    public void setPerUserDurationBetweenEvictionRuns(String userName, Duration value) {
        this.assertInitializationAllowed();
        this.perUserDurationBetweenEvictionRuns = this.put(this.perUserDurationBetweenEvictionRuns, userName, value);
    }

    void setPerUserEvictionPolicyClassName(Map<String, String> newMap) {
        this.assertInitializationAllowed();
        this.perUserEvictionPolicyClassName = this.replaceAll(this.perUserEvictionPolicyClassName, newMap);
    }

    public void setPerUserEvictionPolicyClassName(String userName, String value) {
        this.assertInitializationAllowed();
        this.perUserEvictionPolicyClassName = this.put(this.perUserEvictionPolicyClassName, userName, value);
    }

    void setPerUserLifo(Map<String, Boolean> newMap) {
        this.assertInitializationAllowed();
        this.perUserLifo = this.replaceAll(this.perUserLifo, newMap);
    }

    public void setPerUserLifo(String userName, Boolean value) {
        this.assertInitializationAllowed();
        this.perUserLifo = this.put(this.perUserLifo, userName, value);
    }

    void setPerUserMaxIdle(Map<String, Integer> newMap) {
        this.assertInitializationAllowed();
        this.perUserMaxIdle = this.replaceAll(this.perUserMaxIdle, newMap);
    }

    public void setPerUserMaxIdle(String userName, Integer value) {
        this.assertInitializationAllowed();
        this.perUserMaxIdle = this.put(this.perUserMaxIdle, userName, value);
    }

    void setPerUserMaxTotal(Map<String, Integer> newMap) {
        this.assertInitializationAllowed();
        this.perUserMaxTotal = this.replaceAll(this.perUserMaxTotal, newMap);
    }

    public void setPerUserMaxTotal(String userName, Integer value) {
        this.assertInitializationAllowed();
        this.perUserMaxTotal = this.put(this.perUserMaxTotal, userName, value);
    }

    public void setPerUserMaxWait(String userName, Duration value) {
        this.assertInitializationAllowed();
        this.perUserMaxWaitDuration = this.put(this.perUserMaxWaitDuration, userName, value);
    }

    void setPerUserMaxWaitDuration(Map<String, Duration> newMap) {
        this.assertInitializationAllowed();
        this.perUserMaxWaitDuration = this.replaceAll(this.perUserMaxWaitDuration, newMap);
    }

    void setPerUserMaxWaitMillis(Map<String, Long> newMap) {
        this.assertInitializationAllowed();
        this.perUserMaxWaitDuration = this.convertMap(this.perUserMaxWaitDuration, newMap);
    }

    @Deprecated
    public void setPerUserMaxWaitMillis(String userName, Long value) {
        this.setPerUserMaxWait(userName, this.toDurationOrNull(value));
    }

    void setPerUserMinEvictableIdle(Map<String, Duration> newMap) {
        this.assertInitializationAllowed();
        this.perUserMinEvictableIdleDuration = this.replaceAll(this.perUserMinEvictableIdleDuration, newMap);
    }

    public void setPerUserMinEvictableIdle(String userName, Duration value) {
        this.assertInitializationAllowed();
        this.perUserMinEvictableIdleDuration = this.put(this.perUserMinEvictableIdleDuration, userName, value);
    }

    @Deprecated
    public void setPerUserMinEvictableIdleTimeMillis(String userName, Long value) {
        this.setPerUserMinEvictableIdle(userName, this.toDurationOrNull(value));
    }

    void setPerUserMinIdle(Map<String, Integer> newMap) {
        this.assertInitializationAllowed();
        this.perUserMinIdle = this.replaceAll(this.perUserMinIdle, newMap);
    }

    public void setPerUserMinIdle(String userName, Integer value) {
        this.assertInitializationAllowed();
        this.perUserMinIdle = this.put(this.perUserMinIdle, userName, value);
    }

    void setPerUserNumTestsPerEvictionRun(Map<String, Integer> newMap) {
        this.assertInitializationAllowed();
        this.perUserNumTestsPerEvictionRun = this.replaceAll(this.perUserNumTestsPerEvictionRun, newMap);
    }

    public void setPerUserNumTestsPerEvictionRun(String userName, Integer value) {
        this.assertInitializationAllowed();
        this.perUserNumTestsPerEvictionRun = this.put(this.perUserNumTestsPerEvictionRun, userName, value);
    }

    void setPerUserSoftMinEvictableIdle(Map<String, Duration> newMap) {
        this.assertInitializationAllowed();
        this.perUserSoftMinEvictableIdleDuration = this.replaceAll(this.perUserSoftMinEvictableIdleDuration, newMap);
    }

    public void setPerUserSoftMinEvictableIdle(String userName, Duration value) {
        this.assertInitializationAllowed();
        this.perUserSoftMinEvictableIdleDuration = this.put(this.perUserSoftMinEvictableIdleDuration, userName, value);
    }

    @Deprecated
    public void setPerUserSoftMinEvictableIdleTimeMillis(String userName, Long value) {
        this.setPerUserSoftMinEvictableIdle(userName, this.toDurationOrNull(value));
    }

    void setPerUserTestOnBorrow(Map<String, Boolean> newMap) {
        this.assertInitializationAllowed();
        this.perUserTestOnBorrow = this.replaceAll(this.perUserTestOnBorrow, newMap);
    }

    public void setPerUserTestOnBorrow(String userName, Boolean value) {
        this.assertInitializationAllowed();
        this.perUserTestOnBorrow = this.put(this.perUserTestOnBorrow, userName, value);
    }

    void setPerUserTestOnCreate(Map<String, Boolean> newMap) {
        this.assertInitializationAllowed();
        this.perUserTestOnCreate = this.replaceAll(this.perUserTestOnCreate, newMap);
    }

    public void setPerUserTestOnCreate(String userName, Boolean value) {
        this.assertInitializationAllowed();
        this.perUserTestOnCreate = this.put(this.perUserTestOnCreate, userName, value);
    }

    void setPerUserTestOnReturn(Map<String, Boolean> newMap) {
        this.assertInitializationAllowed();
        this.perUserTestOnReturn = this.replaceAll(this.perUserTestOnReturn, newMap);
    }

    public void setPerUserTestOnReturn(String userName, Boolean value) {
        this.assertInitializationAllowed();
        this.perUserTestOnReturn = this.put(this.perUserTestOnReturn, userName, value);
    }

    void setPerUserTestWhileIdle(Map<String, Boolean> newMap) {
        this.assertInitializationAllowed();
        this.perUserTestWhileIdle = this.replaceAll(this.perUserTestWhileIdle, newMap);
    }

    public void setPerUserTestWhileIdle(String userName, Boolean value) {
        this.assertInitializationAllowed();
        this.perUserTestWhileIdle = this.put(this.perUserTestWhileIdle, userName, value);
    }

    @Deprecated
    public void setPerUserTimeBetweenEvictionRunsMillis(String userName, Long value) {
        this.setPerUserDurationBetweenEvictionRuns(userName, this.toDurationOrNull(value));
    }

    @Override
    protected void setupDefaults(Connection con, String userName) throws SQLException {
        Integer userMax;
        Boolean userMax2;
        Boolean userMax3;
        Boolean defaultAutoCommit = this.isDefaultAutoCommit();
        if (userName != null && (userMax3 = this.getPerUserDefaultAutoCommit(userName)) != null) {
            defaultAutoCommit = userMax3;
        }
        Boolean defaultReadOnly = this.isDefaultReadOnly();
        if (userName != null && (userMax2 = this.getPerUserDefaultReadOnly(userName)) != null) {
            defaultReadOnly = userMax2;
        }
        int defaultTransactionIsolation = this.getDefaultTransactionIsolation();
        if (userName != null && (userMax = this.getPerUserDefaultTransactionIsolation(userName)) != null) {
            defaultTransactionIsolation = userMax;
        }
        if (defaultAutoCommit != null && con.getAutoCommit() != defaultAutoCommit.booleanValue()) {
            con.setAutoCommit(defaultAutoCommit);
        }
        if (defaultTransactionIsolation != -1) {
            con.setTransactionIsolation(defaultTransactionIsolation);
        }
        if (defaultReadOnly != null && con.isReadOnly() != defaultReadOnly.booleanValue()) {
            con.setReadOnly(defaultReadOnly);
        }
    }

    private Duration toDurationOrNull(Long millis) {
        return millis == null ? null : Duration.ofMillis(millis);
    }
}

