/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

public final class LifecycleEvent {
    final LifecycleState state;

    public LifecycleEvent(LifecycleState state) {
        this.state = state;
    }

    public LifecycleState getState() {
        return this.state;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LifecycleEvent)) {
            return false;
        }
        LifecycleEvent that = (LifecycleEvent)o;
        return this.state == that.state;
    }

    public int hashCode() {
        return this.state != null ? this.state.hashCode() : 0;
    }

    public String toString() {
        return "LifecycleEvent [state=" + (Object)((Object)this.state) + "]";
    }

    public static enum LifecycleState {
        STARTING,
        STARTED,
        SHUTTING_DOWN,
        SHUTDOWN,
        MERGING,
        MERGED,
        MERGE_FAILED,
        CLIENT_CONNECTED,
        CLIENT_DISCONNECTED,
        CLIENT_CHANGED_CLUSTER;

    }
}

