/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.pool.impl;

import org.apache.commons.pool.KeyedObjectPool;
import org.apache.commons.pool.KeyedObjectPoolFactory;
import org.apache.commons.pool.KeyedPoolableObjectFactory;
import org.apache.commons.pool.impl.StackKeyedObjectPool;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class StackKeyedObjectPoolFactory<K, V>
implements KeyedObjectPoolFactory<K, V> {
    @Deprecated
    protected KeyedPoolableObjectFactory<K, V> _factory = null;
    @Deprecated
    protected int _maxSleeping = 8;
    @Deprecated
    protected int _initCapacity = 4;

    public StackKeyedObjectPoolFactory() {
        this(null, 8, 4);
    }

    public StackKeyedObjectPoolFactory(int maxSleeping) {
        this(null, maxSleeping, 4);
    }

    public StackKeyedObjectPoolFactory(int maxSleeping, int initialCapacity) {
        this(null, maxSleeping, initialCapacity);
    }

    public StackKeyedObjectPoolFactory(KeyedPoolableObjectFactory<K, V> factory) {
        this(factory, 8, 4);
    }

    public StackKeyedObjectPoolFactory(KeyedPoolableObjectFactory<K, V> factory, int maxSleeping) {
        this(factory, maxSleeping, 4);
    }

    public StackKeyedObjectPoolFactory(KeyedPoolableObjectFactory<K, V> factory, int maxSleeping, int initialCapacity) {
        this._factory = factory;
        this._maxSleeping = maxSleeping;
        this._initCapacity = initialCapacity;
    }

    @Override
    public KeyedObjectPool<K, V> createPool() {
        return new StackKeyedObjectPool<K, V>(this._factory, this._maxSleeping, this._initCapacity);
    }

    public KeyedPoolableObjectFactory<K, V> getFactory() {
        return this._factory;
    }

    public int getMaxSleeping() {
        return this._maxSleeping;
    }

    public int getInitialCapacity() {
        return this._initCapacity;
    }
}

