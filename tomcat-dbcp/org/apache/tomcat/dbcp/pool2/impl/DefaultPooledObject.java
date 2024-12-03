/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.dbcp.pool2.impl;

import java.io.PrintWriter;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Deque;
import org.apache.tomcat.dbcp.pool2.PooledObject;
import org.apache.tomcat.dbcp.pool2.PooledObjectState;
import org.apache.tomcat.dbcp.pool2.TrackedUse;
import org.apache.tomcat.dbcp.pool2.impl.CallStack;
import org.apache.tomcat.dbcp.pool2.impl.CallStackUtils;
import org.apache.tomcat.dbcp.pool2.impl.NoOpCallStack;
import org.apache.tomcat.dbcp.pool2.impl.PoolImplUtils;

public class DefaultPooledObject<T>
implements PooledObject<T> {
    private final T object;
    private PooledObjectState state = PooledObjectState.IDLE;
    private final Clock systemClock = Clock.systemUTC();
    private final Instant createInstant;
    private volatile Instant lastBorrowInstant = this.createInstant = this.now();
    private volatile Instant lastUseInstant = this.createInstant;
    private volatile Instant lastReturnInstant = this.createInstant;
    private volatile boolean logAbandoned;
    private volatile CallStack borrowedBy = NoOpCallStack.INSTANCE;
    private volatile CallStack usedBy = NoOpCallStack.INSTANCE;
    private volatile long borrowedCount;

    public DefaultPooledObject(T object) {
        this.object = object;
    }

    @Override
    public synchronized boolean allocate() {
        if (this.state == PooledObjectState.IDLE) {
            this.state = PooledObjectState.ALLOCATED;
            this.lastUseInstant = this.lastBorrowInstant = this.now();
            ++this.borrowedCount;
            if (this.logAbandoned) {
                this.borrowedBy.fillInStackTrace();
            }
            return true;
        }
        if (this.state == PooledObjectState.EVICTION) {
            this.state = PooledObjectState.EVICTION_RETURN_TO_HEAD;
        }
        return false;
    }

    @Override
    public int compareTo(PooledObject<T> other) {
        int compareTo = this.getLastReturnInstant().compareTo(other.getLastReturnInstant());
        if (compareTo == 0) {
            return System.identityHashCode(this) - System.identityHashCode(other);
        }
        return compareTo;
    }

    @Override
    public synchronized boolean deallocate() {
        if (this.state == PooledObjectState.ALLOCATED || this.state == PooledObjectState.RETURNING) {
            this.state = PooledObjectState.IDLE;
            this.lastReturnInstant = this.now();
            this.borrowedBy.clear();
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
            idleQueue.offerFirst(this);
        }
        return false;
    }

    @Override
    public long getActiveTimeMillis() {
        return this.getActiveDuration().toMillis();
    }

    @Override
    public long getBorrowedCount() {
        return this.borrowedCount;
    }

    @Override
    public Instant getCreateInstant() {
        return this.createInstant;
    }

    @Override
    public long getCreateTime() {
        return this.createInstant.toEpochMilli();
    }

    @Override
    public Duration getIdleDuration() {
        Duration elapsed = Duration.between(this.lastReturnInstant, this.now());
        return elapsed.isNegative() ? Duration.ZERO : elapsed;
    }

    @Override
    public Duration getIdleTime() {
        return this.getIdleDuration();
    }

    @Override
    public long getIdleTimeMillis() {
        return this.getIdleDuration().toMillis();
    }

    @Override
    public Instant getLastBorrowInstant() {
        return this.lastBorrowInstant;
    }

    @Override
    public long getLastBorrowTime() {
        return this.lastBorrowInstant.toEpochMilli();
    }

    @Override
    public Instant getLastReturnInstant() {
        return this.lastReturnInstant;
    }

    @Override
    public long getLastReturnTime() {
        return this.lastReturnInstant.toEpochMilli();
    }

    @Override
    public Instant getLastUsedInstant() {
        if (this.object instanceof TrackedUse) {
            return PoolImplUtils.max(((TrackedUse)this.object).getLastUsedInstant(), this.lastUseInstant);
        }
        return this.lastUseInstant;
    }

    @Override
    public long getLastUsedTime() {
        return this.getLastUsedInstant().toEpochMilli();
    }

    @Override
    public T getObject() {
        return this.object;
    }

    @Override
    public synchronized PooledObjectState getState() {
        return this.state;
    }

    @Override
    public synchronized void invalidate() {
        this.state = PooledObjectState.INVALID;
    }

    @Override
    public synchronized void markAbandoned() {
        this.state = PooledObjectState.ABANDONED;
    }

    @Override
    public synchronized void markReturning() {
        this.state = PooledObjectState.RETURNING;
    }

    private Instant now() {
        return this.systemClock.instant();
    }

    @Override
    public void printStackTrace(PrintWriter writer) {
        boolean written = this.borrowedBy.printStackTrace(writer);
        if (written |= this.usedBy.printStackTrace(writer)) {
            writer.flush();
        }
    }

    @Override
    public void setLogAbandoned(boolean logAbandoned) {
        this.logAbandoned = logAbandoned;
    }

    @Override
    public void setRequireFullStackTrace(boolean requireFullStackTrace) {
        this.borrowedBy = CallStackUtils.newCallStack("'Pooled object created' yyyy-MM-dd HH:mm:ss Z 'by the following code has not been returned to the pool:'", true, requireFullStackTrace);
        this.usedBy = CallStackUtils.newCallStack("The last code to use this object was:", false, requireFullStackTrace);
    }

    @Override
    public synchronized boolean startEvictionTest() {
        if (this.state == PooledObjectState.IDLE) {
            this.state = PooledObjectState.EVICTION;
            return true;
        }
        return false;
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
    public void use() {
        this.lastUseInstant = this.now();
        this.usedBy.fillInStackTrace();
    }
}

