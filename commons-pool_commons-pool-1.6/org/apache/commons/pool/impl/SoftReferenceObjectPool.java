/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.pool.impl;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import org.apache.commons.pool.BaseObjectPool;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.PoolUtils;
import org.apache.commons.pool.PoolableObjectFactory;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class SoftReferenceObjectPool<T>
extends BaseObjectPool<T>
implements ObjectPool<T> {
    private final List<SoftReference<T>> _pool;
    private PoolableObjectFactory<T> _factory = null;
    private final ReferenceQueue<T> refQueue = new ReferenceQueue();
    private int _numActive = 0;

    @Deprecated
    public SoftReferenceObjectPool() {
        this._pool = new ArrayList<SoftReference<T>>();
        this._factory = null;
    }

    public SoftReferenceObjectPool(PoolableObjectFactory<T> factory) {
        this._pool = new ArrayList<SoftReference<T>>();
        this._factory = factory;
    }

    @Deprecated
    public SoftReferenceObjectPool(PoolableObjectFactory<T> factory, int initSize) throws Exception, IllegalArgumentException {
        if (factory == null) {
            throw new IllegalArgumentException("factory required to prefill the pool.");
        }
        this._pool = new ArrayList<SoftReference<T>>(initSize);
        this._factory = factory;
        PoolUtils.prefill(this, initSize);
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
            if (this._pool.isEmpty()) {
                if (null == this._factory) {
                    throw new NoSuchElementException();
                }
                newlyCreated = true;
                obj = this._factory.makeObject();
            } else {
                SoftReference<T> ref = this._pool.remove(this._pool.size() - 1);
                obj = ref.get();
                ref.clear();
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
        if (this._factory != null) {
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
            this._pool.add(new SoftReference<T>(obj, this.refQueue));
        }
        this.notifyAll();
        if (shouldDestroy && this._factory != null) {
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
        if (this._factory != null) {
            this._factory.destroyObject(obj);
        }
        this.notifyAll();
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
            this._pool.add(new SoftReference<T>(obj, this.refQueue));
            this.notifyAll();
        }
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
    public synchronized int getNumIdle() {
        this.pruneClearedReferences();
        return this._pool.size();
    }

    @Override
    public synchronized int getNumActive() {
        return this._numActive;
    }

    @Override
    public synchronized void clear() {
        if (null != this._factory) {
            Iterator<SoftReference<T>> iter = this._pool.iterator();
            while (iter.hasNext()) {
                try {
                    T obj = iter.next().get();
                    if (null == obj) continue;
                    this._factory.destroyObject(obj);
                }
                catch (Exception exception) {}
            }
        }
        this._pool.clear();
        this.pruneClearedReferences();
    }

    @Override
    public void close() throws Exception {
        super.close();
        this.clear();
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

    private void pruneClearedReferences() {
        Reference<T> ref;
        while ((ref = this.refQueue.poll()) != null) {
            try {
                this._pool.remove(ref);
            }
            catch (UnsupportedOperationException unsupportedOperationException) {}
        }
    }

    public synchronized PoolableObjectFactory<T> getFactory() {
        return this._factory;
    }
}

