/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.pool2;

import java.io.PrintWriter;
import java.util.Deque;
import org.apache.commons.pool2.PooledObjectState;

public interface PooledObject<T>
extends Comparable<PooledObject<T>> {
    public T getObject();

    public long getCreateTime();

    public long getActiveTimeMillis();

    public long getIdleTimeMillis();

    public long getLastBorrowTime();

    public long getLastReturnTime();

    public long getLastUsedTime();

    @Override
    public int compareTo(PooledObject<T> var1);

    public boolean equals(Object var1);

    public int hashCode();

    public String toString();

    public boolean startEvictionTest();

    public boolean endEvictionTest(Deque<PooledObject<T>> var1);

    public boolean allocate();

    public boolean deallocate();

    public void invalidate();

    public void setLogAbandoned(boolean var1);

    public void use();

    public void printStackTrace(PrintWriter var1);

    public PooledObjectState getState();

    public void markAbandoned();

    public void markReturning();
}

