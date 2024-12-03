/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.stp.hercules;

public class ScanItem {
    private static final String DEFAULT_LOG = "stp.hercules.scanitem.default";
    private final String key;
    private final String path;

    public ScanItem(String key, String path) {
        this.key = key;
        this.path = path;
    }

    public static ScanItem createDefaultItem(String path) {
        return new ScanItem(DEFAULT_LOG, path);
    }

    public String getKey() {
        return this.key;
    }

    public String getPath() {
        return this.path;
    }
}

