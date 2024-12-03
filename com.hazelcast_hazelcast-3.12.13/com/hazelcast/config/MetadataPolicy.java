/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

public enum MetadataPolicy {
    CREATE_ON_UPDATE(0),
    OFF(1);

    private final int id;

    private MetadataPolicy(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public static MetadataPolicy getById(int id) {
        for (MetadataPolicy policy : MetadataPolicy.values()) {
            if (policy.id != id) continue;
            return policy;
        }
        return null;
    }
}

