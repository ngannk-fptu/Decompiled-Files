/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.pool2;

import org.apache.commons.pool2.BaseObject;
import org.apache.commons.pool2.ObjectPool;

public abstract class BaseObjectPool<T>
extends BaseObject
implements ObjectPool<T> {
    private volatile boolean closed = false;

    @Override
    public abstract T borrowObject() throws Exception;

    @Override
    public abstract void returnObject(T var1) throws Exception;

    @Override
    public abstract void invalidateObject(T var1) throws Exception;

    @Override
    public int getNumIdle() {
        return -1;
    }

    @Override
    public int getNumActive() {
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
    public void close() {
        this.closed = true;
    }

    public final boolean isClosed() {
        return this.closed;
    }

    protected final void assertOpen() throws IllegalStateException {
        if (this.isClosed()) {
            throw new IllegalStateException("Pool not open");
        }
    }

    @Override
    protected void toStringAppendFields(StringBuilder builder) {
        builder.append("closed=");
        builder.append(this.closed);
    }
}

