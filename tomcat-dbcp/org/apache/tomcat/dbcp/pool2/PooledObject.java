/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.dbcp.pool2;

import java.io.PrintWriter;
import java.time.Duration;
import java.time.Instant;
import java.util.Deque;
import org.apache.tomcat.dbcp.pool2.PooledObjectState;

public interface PooledObject<T>
extends Comparable<PooledObject<T>> {
    public static boolean isNull(PooledObject<?> pooledObject) {
        return pooledObject == null || pooledObject.getObject() == null;
    }

    public boolean allocate();

    @Override
    public int compareTo(PooledObject<T> var1);

    public boolean deallocate();

    public boolean endEvictionTest(Deque<PooledObject<T>> var1);

    public boolean equals(Object var1);

    default public Duration getActiveDuration() {
        Instant lastBorrowInstant;
        Instant lastReturnInstant = this.getLastReturnInstant();
        return lastReturnInstant.isAfter(lastBorrowInstant = this.getLastBorrowInstant()) ? Duration.between(lastBorrowInstant, lastReturnInstant) : Duration.between(lastBorrowInstant, Instant.now());
    }

    @Deprecated
    default public Duration getActiveTime() {
        return this.getActiveDuration();
    }

    @Deprecated
    public long getActiveTimeMillis();

    default public long getBorrowedCount() {
        return -1L;
    }

    default public Instant getCreateInstant() {
        return Instant.ofEpochMilli(this.getCreateTime());
    }

    @Deprecated
    public long getCreateTime();

    default public Duration getFullDuration() {
        return Duration.between(this.getCreateInstant(), Instant.now());
    }

    default public Duration getIdleDuration() {
        return Duration.ofMillis(this.getIdleTimeMillis());
    }

    @Deprecated
    default public Duration getIdleTime() {
        return Duration.ofMillis(this.getIdleTimeMillis());
    }

    @Deprecated
    public long getIdleTimeMillis();

    default public Instant getLastBorrowInstant() {
        return Instant.ofEpochMilli(this.getLastBorrowTime());
    }

    @Deprecated
    public long getLastBorrowTime();

    default public Instant getLastReturnInstant() {
        return Instant.ofEpochMilli(this.getLastReturnTime());
    }

    @Deprecated
    public long getLastReturnTime();

    default public Instant getLastUsedInstant() {
        return Instant.ofEpochMilli(this.getLastUsedTime());
    }

    @Deprecated
    public long getLastUsedTime();

    public T getObject();

    public PooledObjectState getState();

    public int hashCode();

    public void invalidate();

    public void markAbandoned();

    public void markReturning();

    public void printStackTrace(PrintWriter var1);

    public void setLogAbandoned(boolean var1);

    default public void setRequireFullStackTrace(boolean requireFullStackTrace) {
    }

    public boolean startEvictionTest();

    public String toString();

    public void use();
}

