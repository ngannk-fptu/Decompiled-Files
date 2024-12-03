/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.status.service.systeminfo;

public enum ClusteredDatabasePlatformType {
    AWS_AURORA("aws-aurora");

    private String name;

    private ClusteredDatabasePlatformType(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }
}

