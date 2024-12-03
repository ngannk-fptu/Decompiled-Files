/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.api.supportzip;

public enum BundleCategory {
    CONFIG("config"),
    LOGS("logs"),
    OTHER("other");

    private final String name;

    private BundleCategory(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}

