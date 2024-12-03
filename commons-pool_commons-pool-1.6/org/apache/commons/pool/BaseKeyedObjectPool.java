/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.pool;

import org.apache.commons.pool.KeyedObjectPool;
import org.apache.commons.pool.KeyedPoolableObjectFactory;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class BaseKeyedObjectPool<K, V>
implements KeyedObjectPool<K, V> {
    private volatile boolean closed = false;

    @Override
    public abstract V borrowObject(K var1) throws Exception;

    @Override
    public abstract void returnObject(K var1, V var2) throws Exception;

    @Override
    public abstract void invalidateObject(K var1, V var2) throws Exception;

    @Override
    public void addObject(K key) throws Exception, UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getNumIdle(K key) throws UnsupportedOperationException {
        return -1;
    }

    @Override
    public int getNumActive(K key) throws UnsupportedOperationException {
        return -1;
    }

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
    public void clear(K key) throws Exception, UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() throws Exception {
        this.closed = true;
    }

    @Override
    @Deprecated
    public void setFactory(KeyedPoolableObjectFactory<K, V> factory) throws IllegalStateException, UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    protected final boolean isClosed() {
        return this.closed;
    }

    protected final void assertOpen() throws IllegalStateException {
        if (this.isClosed()) {
            throw new IllegalStateException("Pool not open");
        }
    }
}

