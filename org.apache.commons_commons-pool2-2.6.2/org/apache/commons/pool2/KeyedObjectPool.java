/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.pool2;

import java.io.Closeable;
import java.util.NoSuchElementException;

public interface KeyedObjectPool<K, V>
extends Closeable {
    public V borrowObject(K var1) throws Exception, NoSuchElementException, IllegalStateException;

    public void returnObject(K var1, V var2) throws Exception;

    public void invalidateObject(K var1, V var2) throws Exception;

    public void addObject(K var1) throws Exception, IllegalStateException, UnsupportedOperationException;

    public int getNumIdle(K var1);

    public int getNumActive(K var1);

    public int getNumIdle();

    public int getNumActive();

    public void clear() throws Exception, UnsupportedOperationException;

    public void clear(K var1) throws Exception, UnsupportedOperationException;

    @Override
    public void close();
}

