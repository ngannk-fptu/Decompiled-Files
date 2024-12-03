/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cluster;

public enum MemberAttributeOperationType {
    PUT(1),
    REMOVE(2);

    private final int id;

    private MemberAttributeOperationType(int i) {
        this.id = i;
    }

    public int getId() {
        return this.id;
    }

    public static MemberAttributeOperationType getValue(int id) {
        for (MemberAttributeOperationType operationType : MemberAttributeOperationType.values()) {
            if (operationType.id != id) continue;
            return operationType;
        }
        throw new IllegalArgumentException("No OperationType for ID: " + id);
    }
}

