/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.oscache.base;

public class EntryUpdateState {
    public static final int NOT_YET_UPDATING = -1;
    public static final int UPDATE_IN_PROGRESS = 0;
    public static final int UPDATE_COMPLETE = 1;
    public static final int UPDATE_CANCELLED = 2;
    int state = -1;
    private int nbConcurrentUses = 1;

    public boolean isAwaitingUpdate() {
        return this.state == -1;
    }

    public boolean isCancelled() {
        return this.state == 2;
    }

    public boolean isComplete() {
        return this.state == 1;
    }

    public boolean isUpdating() {
        return this.state == 0;
    }

    public int cancelUpdate() {
        if (this.state != 0) {
            throw new IllegalStateException("Cannot cancel cache update - current state (" + this.state + ") is not UPDATE_IN_PROGRESS");
        }
        this.state = 2;
        return this.decrementUsageCounter();
    }

    public int completeUpdate() {
        if (this.state != 0) {
            throw new IllegalStateException("Cannot complete cache update - current state (" + this.state + ") is not UPDATE_IN_PROGRESS");
        }
        this.state = 1;
        return this.decrementUsageCounter();
    }

    public int startUpdate() {
        if (this.state != -1 && this.state != 2) {
            throw new IllegalStateException("Cannot begin cache update - current state (" + this.state + ") is not NOT_YET_UPDATING or UPDATE_CANCELLED");
        }
        this.state = 0;
        return this.incrementUsageCounter();
    }

    public synchronized int incrementUsageCounter() {
        ++this.nbConcurrentUses;
        return this.nbConcurrentUses;
    }

    public synchronized int getUsageCounter() {
        return this.nbConcurrentUses;
    }

    public synchronized int decrementUsageCounter() {
        if (this.nbConcurrentUses <= 0) {
            throw new IllegalStateException("Cannot decrement usage counter, it is already equals to [" + this.nbConcurrentUses + "]");
        }
        --this.nbConcurrentUses;
        return this.nbConcurrentUses;
    }
}

