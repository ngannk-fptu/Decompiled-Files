/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.pool;

import java.util.NoSuchElementException;
import org.apache.commons.pool.KeyedPoolableObjectFactory;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface KeyedObjectPool<K, V> {
    public V borrowObject(K var1) throws Exception, NoSuchElementException, IllegalStateException;

    public void returnObject(K var1, V var2) throws Exception;

    public void invalidateObject(K var1, V var2) throws Exception;

    public void addObject(K var1) throws Exception, IllegalStateException, UnsupportedOperationException;

    public int getNumIdle(K var1) throws UnsupportedOperationException;

    public int getNumActive(K var1) throws UnsupportedOperationException;

    public int getNumIdle() throws UnsupportedOperationException;

    public int getNumActive() throws UnsupportedOperationException;

    public void clear() throws Exception, UnsupportedOperationException;

    public void clear(K var1) throws Exception, UnsupportedOperationException;

    public void close() throws Exception;

    @Deprecated
    public void setFactory(KeyedPoolableObjectFactory<K, V> var1) throws IllegalStateException, UnsupportedOperationException;
}

