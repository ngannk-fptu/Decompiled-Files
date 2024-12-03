/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

public enum WANQueueFullBehavior {
    DISCARD_AFTER_MUTATION(0),
    THROW_EXCEPTION(1),
    THROW_EXCEPTION_ONLY_IF_REPLICATION_ACTIVE(2);

    private final int id;

    private WANQueueFullBehavior(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public static WANQueueFullBehavior getByType(int id) {
        for (WANQueueFullBehavior behavior : WANQueueFullBehavior.values()) {
            if (behavior.id != id) continue;
            return behavior;
        }
        return null;
    }
}

