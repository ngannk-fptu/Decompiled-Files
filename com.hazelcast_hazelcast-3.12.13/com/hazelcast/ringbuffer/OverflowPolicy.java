/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.ringbuffer;

public enum OverflowPolicy {
    OVERWRITE(0),
    FAIL(1);

    private final int id;

    private OverflowPolicy(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public static OverflowPolicy getById(int id) {
        for (OverflowPolicy policy : OverflowPolicy.values()) {
            if (policy.id != id) continue;
            return policy;
        }
        return null;
    }
}

