/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

public enum WanPublisherState {
    REPLICATING(0, true, true),
    PAUSED(1, true, false),
    STOPPED(2, false, false);

    private static final WanPublisherState[] STATE_VALUES;
    private final boolean enqueueNewEvents;
    private final boolean replicateEnqueuedEvents;
    private final byte id;

    private WanPublisherState(byte id, boolean enqueueNewEvents, boolean replicateEnqueuedEvents) {
        this.id = id;
        this.enqueueNewEvents = enqueueNewEvents;
        this.replicateEnqueuedEvents = replicateEnqueuedEvents;
    }

    public static WanPublisherState getByType(byte id) {
        for (WanPublisherState state : STATE_VALUES) {
            if (state.id != id) continue;
            return state;
        }
        return null;
    }

    public boolean isEnqueueNewEvents() {
        return this.enqueueNewEvents;
    }

    public boolean isReplicateEnqueuedEvents() {
        return this.replicateEnqueuedEvents;
    }

    public byte getId() {
        return this.id;
    }

    static {
        STATE_VALUES = WanPublisherState.values();
    }
}

