/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

public enum WanAcknowledgeType {
    ACK_ON_RECEIPT(0),
    ACK_ON_OPERATION_COMPLETE(1);

    private final int id;

    private WanAcknowledgeType(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public static WanAcknowledgeType getById(int id) {
        for (WanAcknowledgeType type : WanAcknowledgeType.values()) {
            if (type.id != id) continue;
            return type;
        }
        return null;
    }
}

