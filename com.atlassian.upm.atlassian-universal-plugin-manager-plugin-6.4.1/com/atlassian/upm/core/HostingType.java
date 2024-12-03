/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.core;

public enum HostingType {
    SERVER("server"),
    DATA_CENTER("datacenter");

    private final String key;

    private HostingType(String key) {
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }
}

