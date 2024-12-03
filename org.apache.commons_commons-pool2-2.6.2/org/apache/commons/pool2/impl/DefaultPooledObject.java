/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.pool2.impl;

import java.io.PrintWriter;
import java.util.Deque;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectState;
import org.apache.commons.pool2.TrackedUse;
import org.apache.commons.pool2.impl.CallStack;
import org.apache.commons.pool2.impl.CallStackUtils;
import org.apache.commons.pool2.impl.NoOpCallStack;

public class DefaultPooledObject<T>
implements PooledObject<T> {
    private final T object;
    private PooledObjectState state = PooledObjectState.IDLE;
    private final long createTime;
    private volatile long lastBorrowTime = this.createTime = System.currentTimeMillis();
    private volatile long lastUseTime = this.createTime;
    private volatile long lastReturnTime = this.createTime;
    private volatile boolean logAbandoned = false;
    private volatile CallStack borrowedBy = NoOpCallStack.INSTANCE;
    private volatile CallStack usedBy = NoOpCallStack.INSTANCE;
    private volatile long borrowedCount = 0L;

    public DefaultPooledObject(T object) {
        this.object = object;
    }

    @Override
    public T getObject() {
        return this.object;
    }

    @Override
    public long getCreateTime() {
        return this.createTime;
    }

    @Override
    public long getActiveTimeMillis() {
        long rTime = this.lastReturnTime;
        long bTime = this.lastBorrowTime;
        if (rTime > bTime) {
            return rTime - bTime;
        }
        return System.currentTimeMillis() - bTime;
    }

    @Override
    public long getIdleTimeMillis() {
        long elapsed = System.currentTimeMillis() - this.lastReturnTime;
        return elapsed >= 0L ? elapsed : 0L;
    }

    @Override
    public long getLastBorrowTime() {
        return this.lastBorrowTime;
    }

    @Override
    public long getLastReturnTime() {
        return this.lastReturnTime;
    }

    public long getBorrowedCount() {
        return this.borrowedCount;
    }

    @Override
    public long getLastUsedTime() {
        if (this.object instanceof TrackedUse) {
            return Math.max(((TrackedUse)this.object).getLastUsed(), this.lastUseTime);
        }
        return this.lastUseTime;
    }

    @Override
    public int compareTo(PooledObject<T> other) {
        long lastActiveDiff = this.getLastReturnTime() - other.getLastReturnTime();
        if (lastActiveDiff == 0L) {
            return System.identityHashCode(this) - System.identityHashCode(other);
        }
        return (int)Math.min(Math.max(lastActiveDiff, Integer.MIN_VALUE), Integer.MAX_VALUE);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("Object: ");
        result.append(this.object.toString());
        result.append(", State: ");
        DefaultPooledObject defaultPooledObject = this;
        synchronized (defaultPooledObject) {
            result.append(this.state.toString());
        }
        return result.toString();
    }

    @Override
    public synchronized boolean startEvictionTest() {
        if (this.state == PooledObjectState.IDLE) {
            this.state = PooledObjectState.EVICTION;
            return true;
        }
        return false;
    }

    @Override
    public synchronized boolean endEvictionTest(Deque<PooledObject<T>> idleQueue) {
        if (this.state == PooledObjectState.EVICTION) {
            this.state = PooledObjectState.IDLE;
            return true;
        }
        if (this.state == PooledObjectState.EVICTION_RETURN_TO_HEAD) {
            this.state = PooledObjectState.IDLE;
            if (!idleQueue.offerFirst(this)) {
                // empty if block
            }
        }
        return false;
    }

    @Override
    public synchronized boolean allocate() {
        if (this.state == PooledObjectState.IDLE) {
            this.state = PooledObjectState.ALLOCATED;
            this.lastUseTime = this.lastBorrowTime = System.currentTimeMillis();
            ++this.borrowedCount;
            if (this.logAbandoned) {
                this.borrowedBy.fillInStackTrace();
            }
            return true;
        }
        if (this.state == PooledObjectState.EVICTION) {
            this.state = PooledObjectState.EVICTION_RETURN_TO_HEAD;
            return false;
        }
        return false;
    }

    @Override
    public synchronized boolean deallocate() {
        if (this.state == PooledObjectState.ALLOCATED || this.state == PooledObjectState.RETURNING) {
            this.state = PooledObjectState.IDLE;
            this.lastReturnTime = System.currentTimeMillis();
            this.borrowedBy.clear();
            return true;
        }
        return false;
    }

    @Override
    public synchronized void invalidate() {
        this.state = PooledObjectState.INVALID;
    }

    @Override
    public void use() {
        this.lastUseTime = System.currentTimeMillis();
        this.usedBy.fillInStackTrace();
    }

    @Override
    public void printStackTrace(PrintWriter writer) {
        boolean written = this.borrowedBy.printStackTrace(writer);
        if (written |= this.usedBy.printStackTrace(writer)) {
            writer.flush();
        }
    }

    @Override
    public synchronized PooledObjectState getState() {
        return this.state;
    }

    @Override
    public synchronized void markAbandoned() {
        this.state = PooledObjectState.ABANDONED;
    }

    @Override
    public synchronized void markReturning() {
        this.state = PooledObjectState.RETURNING;
    }

    @Override
    public void setLogAbandoned(boolean logAbandoned) {
        this.logAbandoned = logAbandoned;
    }

    public void setRequireFullStackTrace(boolean requireFullStackTrace) {
        this.borrowedBy = CallStackUtils.newCallStack("'Pooled object created' yyyy-MM-dd HH:mm:ss Z 'by the following code has not been returned to the pool:'", true, requireFullStackTrace);
        this.usedBy = CallStackUtils.newCallStack("The last code to use this object was:", false, requireFullStackTrace);
    }
}

