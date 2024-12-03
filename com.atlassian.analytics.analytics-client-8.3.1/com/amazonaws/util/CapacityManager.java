/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.util;

public class CapacityManager {
    private volatile int availableCapacity;
    private final int maxCapacity;
    private final Object lock = new Object();

    public CapacityManager(int maxCapacity) {
        this.maxCapacity = maxCapacity;
        this.availableCapacity = maxCapacity;
    }

    public boolean acquire() {
        return this.acquire(1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean acquire(int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException("capacity to acquire cannot be negative");
        }
        if (this.availableCapacity < 0) {
            return true;
        }
        Object object = this.lock;
        synchronized (object) {
            if (this.availableCapacity - capacity >= 0) {
                this.availableCapacity -= capacity;
                return true;
            }
            return false;
        }
    }

    public void release() {
        this.release(1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void release(int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException("capacity to release cannot be negative");
        }
        if (this.availableCapacity >= 0 && this.availableCapacity != this.maxCapacity) {
            Object object = this.lock;
            synchronized (object) {
                this.availableCapacity = Math.min(this.availableCapacity + capacity, this.maxCapacity);
            }
        }
    }

    public int consumedCapacity() {
        return this.availableCapacity < 0 ? 0 : this.maxCapacity - this.availableCapacity;
    }

    public int availableCapacity() {
        return this.availableCapacity;
    }
}

