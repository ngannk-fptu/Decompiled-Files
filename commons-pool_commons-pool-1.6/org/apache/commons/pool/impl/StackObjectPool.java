/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.pool.impl;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Stack;
import org.apache.commons.pool.BaseObjectPool;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.PoolUtils;
import org.apache.commons.pool.PoolableObjectFactory;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class StackObjectPool<T>
extends BaseObjectPool<T>
implements ObjectPool<T> {
    protected static final int DEFAULT_MAX_SLEEPING = 8;
    protected static final int DEFAULT_INIT_SLEEPING_CAPACITY = 4;
    @Deprecated
    protected Stack<T> _pool = null;
    @Deprecated
    protected PoolableObjectFactory<T> _factory = null;
    @Deprecated
    protected int _maxSleeping = 8;
    @Deprecated
    protected int _numActive = 0;

    @Deprecated
    public StackObjectPool() {
        this(null, 8, 4);
    }

    @Deprecated
    public StackObjectPool(int maxIdle) {
        this(null, maxIdle, 4);
    }

    @Deprecated
    public StackObjectPool(int maxIdle, int initIdleCapacity) {
        this(null, maxIdle, initIdleCapacity);
    }

    public StackObjectPool(PoolableObjectFactory<T> factory) {
        this(factory, 8, 4);
    }

    public StackObjectPool(PoolableObjectFactory<T> factory, int maxIdle) {
        this(factory, maxIdle, 4);
    }

    public StackObjectPool(PoolableObjectFactory<T> factory, int maxIdle, int initIdleCapacity) {
        this._factory = factory;
        this._maxSleeping = maxIdle < 0 ? 8 : maxIdle;
        int initcapacity = initIdleCapacity < 1 ? 4 : initIdleCapacity;
        this._pool = new Stack();
        this._pool.ensureCapacity(initcapacity > this._maxSleeping ? this._maxSleeping : initcapacity);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public synchronized T borrowObject() throws Exception {
        this.assertOpen();
        Object obj = null;
        boolean newlyCreated = false;
        while (null == obj) {
            if (!this._pool.empty()) {
                obj = this._pool.pop();
            } else {
                if (null == this._factory) {
                    throw new NoSuchElementException();
                }
                obj = this._factory.makeObject();
                newlyCreated = true;
                if (obj == null) {
                    throw new NoSuchElementException("PoolableObjectFactory.makeObject() returned null.");
                }
            }
            if (null == this._factory || null == obj) continue;
            try {
                this._factory.activateObject(obj);
                if (this._factory.validateObject(obj)) continue;
                throw new Exception("ValidateObject failed");
            }
            catch (Throwable t) {
                PoolUtils.checkRethrow(t);
                try {
                    this._factory.destroyObject(obj);
                }
                catch (Throwable t2) {
                    PoolUtils.checkRethrow(t2);
                }
                finally {
                    obj = null;
                }
                if (!newlyCreated) continue;
                throw new NoSuchElementException("Could not create a validated object, cause: " + t.getMessage());
            }
        }
        ++this._numActive;
        return obj;
    }

    @Override
    public synchronized void returnObject(T obj) throws Exception {
        boolean success;
        boolean bl = success = !this.isClosed();
        if (null != this._factory) {
            if (!this._factory.validateObject(obj)) {
                success = false;
            } else {
                try {
                    this._factory.passivateObject(obj);
                }
                catch (Exception e) {
                    success = false;
                }
            }
        }
        boolean shouldDestroy = !success;
        --this._numActive;
        if (success) {
            Object toBeDestroyed = null;
            if (this._pool.size() >= this._maxSleeping) {
                shouldDestroy = true;
                toBeDestroyed = this._pool.remove(0);
            }
            this._pool.push(obj);
            obj = toBeDestroyed;
        }
        this.notifyAll();
        if (shouldDestroy) {
            try {
                this._factory.destroyObject(obj);
            }
            catch (Exception e) {
                // empty catch block
            }
        }
    }

    @Override
    public synchronized void invalidateObject(T obj) throws Exception {
        --this._numActive;
        if (null != this._factory) {
            this._factory.destroyObject(obj);
        }
        this.notifyAll();
    }

    @Override
    public synchronized int getNumIdle() {
        return this._pool.size();
    }

    @Override
    public synchronized int getNumActive() {
        return this._numActive;
    }

    @Override
    public synchronized void clear() {
        if (null != this._factory) {
            Iterator it = this._pool.iterator();
            while (it.hasNext()) {
                try {
                    this._factory.destroyObject(it.next());
                }
                catch (Exception exception) {}
            }
        }
        this._pool.clear();
    }

    @Override
    public void close() throws Exception {
        super.close();
        this.clear();
    }

    @Override
    public synchronized void addObject() throws Exception {
        boolean shouldDestroy;
        this.assertOpen();
        if (this._factory == null) {
            throw new IllegalStateException("Cannot add objects without a factory.");
        }
        T obj = this._factory.makeObject();
        boolean success = true;
        if (!this._factory.validateObject(obj)) {
            success = false;
        } else {
            this._factory.passivateObject(obj);
        }
        boolean bl = shouldDestroy = !success;
        if (success) {
            Object toBeDestroyed = null;
            if (this._pool.size() >= this._maxSleeping) {
                shouldDestroy = true;
                toBeDestroyed = this._pool.remove(0);
            }
            this._pool.push(obj);
            obj = toBeDestroyed;
        }
        this.notifyAll();
        if (shouldDestroy) {
            try {
                this._factory.destroyObject(obj);
            }
            catch (Exception e) {
                // empty catch block
            }
        }
    }

    @Override
    @Deprecated
    public synchronized void setFactory(PoolableObjectFactory<T> factory) throws IllegalStateException {
        this.assertOpen();
        if (0 < this.getNumActive()) {
            throw new IllegalStateException("Objects are already active");
        }
        this.clear();
        this._factory = factory;
    }

    public synchronized PoolableObjectFactory<T> getFactory() {
        return this._factory;
    }

    public int getMaxSleeping() {
        return this._maxSleeping;
    }
}

