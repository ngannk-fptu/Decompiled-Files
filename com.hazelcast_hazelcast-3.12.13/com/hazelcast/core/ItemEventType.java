/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

public enum ItemEventType {
    ADDED(1),
    REMOVED(2);

    private int type;

    private ItemEventType(int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }

    public static ItemEventType getByType(int eventType) {
        for (ItemEventType entryEventType : ItemEventType.values()) {
            if (entryEventType.type != eventType) continue;
            return entryEventType;
        }
        return null;
    }
}

