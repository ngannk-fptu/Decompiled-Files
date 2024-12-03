/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.pool;

import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.PoolableObjectFactory;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class BaseObjectPool<T>
implements ObjectPool<T> {
    private volatile boolean closed = false;

    @Override
    public abstract T borrowObject() throws Exception;

    @Override
    public abstract void returnObject(T var1) throws Exception;

    @Override
    public abstract void invalidateObject(T var1) throws Exception;

    @Override
    public int getNumIdle() throws UnsupportedOperationException {
        return -1;
    }

    @Override
    public int getNumActive() throws UnsupportedOperationException {
        return -1;
    }

    @Override
    public void clear() throws Exception, UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addObject() throws Exception, UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() throws Exception {
        this.closed = true;
    }

    @Override
    @Deprecated
    public void setFactory(PoolableObjectFactory<T> factory) throws IllegalStateException, UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    public final boolean isClosed() {
        return this.closed;
    }

    protected final void assertOpen() throws IllegalStateException {
        if (this.isClosed()) {
            throw new IllegalStateException("Pool not open");
        }
    }
}

