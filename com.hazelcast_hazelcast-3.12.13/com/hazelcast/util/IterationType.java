/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.util;

public enum IterationType {
    KEY(0),
    VALUE(1),
    ENTRY(2);

    private final byte id;

    private IterationType(byte id) {
        this.id = id;
    }

    public byte getId() {
        return this.id;
    }

    public static IterationType getById(byte id) {
        for (IterationType type : IterationType.values()) {
            if (type.id != id) continue;
            return type;
        }
        throw new IllegalArgumentException("unknown id:" + id);
    }
}

