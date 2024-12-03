/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.navlink.producer.capabilities;

public enum CapabilityKey {
    NAVIGATION("navigation"),
    CONTENT_LINKS("content-links");

    private final String key;

    private CapabilityKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }
}

