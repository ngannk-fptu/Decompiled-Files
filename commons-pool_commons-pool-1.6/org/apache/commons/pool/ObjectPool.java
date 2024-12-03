/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.pool;

import java.util.NoSuchElementException;
import org.apache.commons.pool.PoolableObjectFactory;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface ObjectPool<T> {
    public T borrowObject() throws Exception, NoSuchElementException, IllegalStateException;

    public void returnObject(T var1) throws Exception;

    public void invalidateObject(T var1) throws Exception;

    public void addObject() throws Exception, IllegalStateException, UnsupportedOperationException;

    public int getNumIdle() throws UnsupportedOperationException;

    public int getNumActive() throws UnsupportedOperationException;

    public void clear() throws Exception, UnsupportedOperationException;

    public void close() throws Exception;

    @Deprecated
    public void setFactory(PoolableObjectFactory<T> var1) throws IllegalStateException, UnsupportedOperationException;
}

