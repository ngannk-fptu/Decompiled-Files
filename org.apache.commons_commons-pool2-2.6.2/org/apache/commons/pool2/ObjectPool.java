/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.pool2;

import java.io.Closeable;
import java.util.NoSuchElementException;

public interface ObjectPool<T>
extends Closeable {
    public T borrowObject() throws Exception, NoSuchElementException, IllegalStateException;

    public void returnObject(T var1) throws Exception;

    public void invalidateObject(T var1) throws Exception;

    public void addObject() throws Exception, IllegalStateException, UnsupportedOperationException;

    public int getNumIdle();

    public int getNumActive();

    public void clear() throws Exception, UnsupportedOperationException;

    @Override
    public void close();
}

