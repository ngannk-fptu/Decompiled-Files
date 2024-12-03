/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.pool.impl;

import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.ObjectPoolFactory;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class GenericObjectPoolFactory<T>
implements ObjectPoolFactory<T> {
    @Deprecated
    protected int _maxIdle = 8;
    @Deprecated
    protected int _minIdle = 0;
    @Deprecated
    protected int _maxActive = 8;
    @Deprecated
    protected long _maxWait = -1L;
    @Deprecated
    protected byte _whenExhaustedAction = 1;
    @Deprecated
    protected boolean _testOnBorrow = false;
    @Deprecated
    protected boolean _testOnReturn = false;
    @Deprecated
    protected boolean _testWhileIdle = false;
    @Deprecated
    protected long _timeBetweenEvictionRunsMillis = -1L;
    @Deprecated
    protected int _numTestsPerEvictionRun = 3;
    @Deprecated
    protected long _minEvictableIdleTimeMillis = 1800000L;
    @Deprecated
    protected long _softMinEvictableIdleTimeMillis = 1800000L;
    @Deprecated
    protected boolean _lifo = true;
    @Deprecated
    protected PoolableObjectFactory<T> _factory = null;

    public GenericObjectPoolFactory(PoolableObjectFactory<T> factory) {
        this(factory, 8, 1, -1L, 8, 0, false, false, -1L, 3, 1800000L, false);
    }

    public GenericObjectPoolFactory(PoolableObjectFactory<T> factory, GenericObjectPool.Config config) throws NullPointerException {
        this(factory, config.maxActive, config.whenExhaustedAction, config.maxWait, config.maxIdle, config.minIdle, config.testOnBorrow, config.testOnReturn, config.timeBetweenEvictionRunsMillis, config.numTestsPerEvictionRun, config.minEvictableIdleTimeMillis, config.testWhileIdle, config.softMinEvictableIdleTimeMillis, config.lifo);
    }

    public GenericObjectPoolFactory(PoolableObjectFactory<T> factory, int maxActive) {
        this(factory, maxActive, 1, -1L, 8, 0, false, false, -1L, 3, 1800000L, false);
    }

    public GenericObjectPoolFactory(PoolableObjectFactory<T> factory, int maxActive, byte whenExhaustedAction, long maxWait) {
        this(factory, maxActive, whenExhaustedAction, maxWait, 8, 0, false, false, -1L, 3, 1800000L, false);
    }

    public GenericObjectPoolFactory(PoolableObjectFactory<T> factory, int maxActive, byte whenExhaustedAction, long maxWait, boolean testOnBorrow, boolean testOnReturn) {
        this(factory, maxActive, whenExhaustedAction, maxWait, 8, 0, testOnBorrow, testOnReturn, -1L, 3, 1800000L, false);
    }

    public GenericObjectPoolFactory(PoolableObjectFactory<T> factory, int maxActive, byte whenExhaustedAction, long maxWait, int maxIdle) {
        this(factory, maxActive, whenExhaustedAction, maxWait, maxIdle, 0, false, false, -1L, 3, 1800000L, false);
    }

    public GenericObjectPoolFactory(PoolableObjectFactory<T> factory, int maxActive, byte whenExhaustedAction, long maxWait, int maxIdle, boolean testOnBorrow, boolean testOnReturn) {
        this(factory, maxActive, whenExhaustedAction, maxWait, maxIdle, 0, testOnBorrow, testOnReturn, -1L, 3, 1800000L, false);
    }

    public GenericObjectPoolFactory(PoolableObjectFactory<T> factory, int maxActive, byte whenExhaustedAction, long maxWait, int maxIdle, boolean testOnBorrow, boolean testOnReturn, long timeBetweenEvictionRunsMillis, int numTestsPerEvictionRun, long minEvictableIdleTimeMillis, boolean testWhileIdle) {
        this(factory, maxActive, whenExhaustedAction, maxWait, maxIdle, 0, testOnBorrow, testOnReturn, timeBetweenEvictionRunsMillis, numTestsPerEvictionRun, minEvictableIdleTimeMillis, testWhileIdle, -1L);
    }

    public GenericObjectPoolFactory(PoolableObjectFactory<T> factory, int maxActive, byte whenExhaustedAction, long maxWait, int maxIdle, int minIdle, boolean testOnBorrow, boolean testOnReturn, long timeBetweenEvictionRunsMillis, int numTestsPerEvictionRun, long minEvictableIdleTimeMillis, boolean testWhileIdle) {
        this(factory, maxActive, whenExhaustedAction, maxWait, maxIdle, minIdle, testOnBorrow, testOnReturn, timeBetweenEvictionRunsMillis, numTestsPerEvictionRun, minEvictableIdleTimeMillis, testWhileIdle, -1L);
    }

    public GenericObjectPoolFactory(PoolableObjectFactory<T> factory, int maxActive, byte whenExhaustedAction, long maxWait, int maxIdle, int minIdle, boolean testOnBorrow, boolean testOnReturn, long timeBetweenEvictionRunsMillis, int numTestsPerEvictionRun, long minEvictableIdleTimeMillis, boolean testWhileIdle, long softMinEvictableIdleTimeMillis) {
        this(factory, maxActive, whenExhaustedAction, maxWait, maxIdle, minIdle, testOnBorrow, testOnReturn, timeBetweenEvictionRunsMillis, numTestsPerEvictionRun, minEvictableIdleTimeMillis, testWhileIdle, softMinEvictableIdleTimeMillis, true);
    }

    public GenericObjectPoolFactory(PoolableObjectFactory<T> factory, int maxActive, byte whenExhaustedAction, long maxWait, int maxIdle, int minIdle, boolean testOnBorrow, boolean testOnReturn, long timeBetweenEvictionRunsMillis, int numTestsPerEvictionRun, long minEvictableIdleTimeMillis, boolean testWhileIdle, long softMinEvictableIdleTimeMillis, boolean lifo) {
        this._maxIdle = maxIdle;
        this._minIdle = minIdle;
        this._maxActive = maxActive;
        this._maxWait = maxWait;
        this._whenExhaustedAction = whenExhaustedAction;
        this._testOnBorrow = testOnBorrow;
        this._testOnReturn = testOnReturn;
        this._testWhileIdle = testWhileIdle;
        this._timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
        this._numTestsPerEvictionRun = numTestsPerEvictionRun;
        this._minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
        this._softMinEvictableIdleTimeMillis = softMinEvictableIdleTimeMillis;
        this._lifo = lifo;
        this._factory = factory;
    }

    @Override
    public ObjectPool<T> createPool() {
        return new GenericObjectPool<T>(this._factory, this._maxActive, this._whenExhaustedAction, this._maxWait, this._maxIdle, this._minIdle, this._testOnBorrow, this._testOnReturn, this._timeBetweenEvictionRunsMillis, this._numTestsPerEvictionRun, this._minEvictableIdleTimeMillis, this._testWhileIdle, this._softMinEvictableIdleTimeMillis, this._lifo);
    }

    public int getMaxIdle() {
        return this._maxIdle;
    }

    public int getMinIdle() {
        return this._minIdle;
    }

    public int getMaxActive() {
        return this._maxActive;
    }

    public long getMaxWait() {
        return this._maxWait;
    }

    public byte getWhenExhaustedAction() {
        return this._whenExhaustedAction;
    }

    public boolean getTestOnBorrow() {
        return this._testOnBorrow;
    }

    public boolean getTestOnReturn() {
        return this._testOnReturn;
    }

    public boolean getTestWhileIdle() {
        return this._testWhileIdle;
    }

    public long getTimeBetweenEvictionRunsMillis() {
        return this._timeBetweenEvictionRunsMillis;
    }

    public int getNumTestsPerEvictionRun() {
        return this._numTestsPerEvictionRun;
    }

    public long getMinEvictableIdleTimeMillis() {
        return this._minEvictableIdleTimeMillis;
    }

    public long getSoftMinEvictableIdleTimeMillis() {
        return this._softMinEvictableIdleTimeMillis;
    }

    public boolean getLifo() {
        return this._lifo;
    }

    public PoolableObjectFactory<T> getFactory() {
        return this._factory;
    }
}

