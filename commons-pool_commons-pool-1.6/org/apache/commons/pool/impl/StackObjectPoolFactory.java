/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.pool.impl;

import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.ObjectPoolFactory;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.StackObjectPool;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class StackObjectPoolFactory<T>
implements ObjectPoolFactory<T> {
    @Deprecated
    protected PoolableObjectFactory<T> _factory = null;
    @Deprecated
    protected int _maxSleeping = 8;
    @Deprecated
    protected int _initCapacity = 4;

    @Deprecated
    public StackObjectPoolFactory() {
        this(null, 8, 4);
    }

    @Deprecated
    public StackObjectPoolFactory(int maxIdle) {
        this(null, maxIdle, 4);
    }

    @Deprecated
    public StackObjectPoolFactory(int maxIdle, int initIdleCapacity) {
        this(null, maxIdle, initIdleCapacity);
    }

    public StackObjectPoolFactory(PoolableObjectFactory<T> factory) {
        this(factory, 8, 4);
    }

    public StackObjectPoolFactory(PoolableObjectFactory<T> factory, int maxIdle) {
        this(factory, maxIdle, 4);
    }

    public StackObjectPoolFactory(PoolableObjectFactory<T> factory, int maxIdle, int initIdleCapacity) {
        this._factory = factory;
        this._maxSleeping = maxIdle;
        this._initCapacity = initIdleCapacity;
    }

    @Override
    public ObjectPool<T> createPool() {
        return new StackObjectPool<T>(this._factory, this._maxSleeping, this._initCapacity);
    }

    public PoolableObjectFactory<T> getFactory() {
        return this._factory;
    }

    public int getMaxSleeping() {
        return this._maxSleeping;
    }

    public int getInitCapacity() {
        return this._initCapacity;
    }
}

