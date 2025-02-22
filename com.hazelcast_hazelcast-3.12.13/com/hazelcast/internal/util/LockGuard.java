/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.util;

import com.hazelcast.nio.Address;
import com.hazelcast.util.Clock;
import com.hazelcast.util.Preconditions;

public class LockGuard {
    public static final LockGuard NOT_LOCKED = new LockGuard();
    private final String lockOwnerId;
    private final Address lockOwner;
    private final long lockExpiryTime;

    private LockGuard() {
        this.lockOwner = null;
        this.lockOwnerId = null;
        this.lockExpiryTime = 0L;
    }

    public LockGuard(Address lockOwner, String lockOwnerId, long leaseTime) {
        Preconditions.checkNotNull(lockOwner);
        Preconditions.checkNotNull(lockOwnerId);
        Preconditions.checkPositive(leaseTime, "Lease time should be positive!");
        this.lockOwner = lockOwner;
        this.lockOwnerId = lockOwnerId;
        this.lockExpiryTime = LockGuard.toLockExpiry(leaseTime);
    }

    private static long toLockExpiry(long leaseTime) {
        long expiryTime = Clock.currentTimeMillis() + leaseTime;
        if (expiryTime < 0L) {
            expiryTime = Long.MAX_VALUE;
        }
        return expiryTime;
    }

    public boolean isLocked() {
        return this.lockOwner != null;
    }

    public boolean isLeaseExpired() {
        return this.lockExpiryTime > 0L && Clock.currentTimeMillis() > this.lockExpiryTime;
    }

    public boolean allowsLock(String ownerId) {
        Preconditions.checkNotNull(ownerId);
        boolean notLocked = this.isLeaseExpired() || !this.isLocked();
        return notLocked || this.allowsUnlock(ownerId);
    }

    public boolean allowsUnlock(String ownerId) {
        Preconditions.checkNotNull(ownerId);
        return ownerId.equals(this.lockOwnerId);
    }

    public Address getLockOwner() {
        return this.lockOwner;
    }

    public String getLockOwnerId() {
        return this.lockOwnerId;
    }

    public long getLockExpiryTime() {
        return this.lockExpiryTime;
    }

    public long getRemainingTime() {
        return Math.max(0L, this.getLockExpiryTime() - Clock.currentTimeMillis());
    }

    public String toString() {
        return "LockGuard{lockOwner=" + this.lockOwner + ", lockOwnerId='" + this.lockOwnerId + '\'' + ", lockExpiryTime=" + this.lockExpiryTime + '}';
    }
}

