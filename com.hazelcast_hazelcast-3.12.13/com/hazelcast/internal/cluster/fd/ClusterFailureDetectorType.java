/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.cluster.fd;

public enum ClusterFailureDetectorType {
    DEADLINE("deadline"),
    PHI_ACCRUAL("phi-accrual");

    private final String name;

    private ClusterFailureDetectorType(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public static ClusterFailureDetectorType of(String name) {
        for (ClusterFailureDetectorType fdType : ClusterFailureDetectorType.values()) {
            if (!fdType.name.equals(name)) continue;
            return fdType;
        }
        throw new IllegalArgumentException("Unknown failure detector type: " + name);
    }
}

