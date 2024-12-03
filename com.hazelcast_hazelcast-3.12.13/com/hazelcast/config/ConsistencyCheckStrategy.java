/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

public enum ConsistencyCheckStrategy {
    NONE(0),
    MERKLE_TREES(1);

    private static final ConsistencyCheckStrategy[] VALUES;
    private final byte id;

    private ConsistencyCheckStrategy(byte id) {
        this.id = id;
    }

    public byte getId() {
        return this.id;
    }

    public static ConsistencyCheckStrategy getById(byte id) {
        for (ConsistencyCheckStrategy type : VALUES) {
            if (type.id != id) continue;
            return type;
        }
        throw new IllegalArgumentException("Could not find a ConsistencyCheckStrategy with an ID:" + id);
    }

    static {
        VALUES = ConsistencyCheckStrategy.values();
    }
}

