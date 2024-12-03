/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.vcache.metrics;

public enum CacheMetricsKeys {
    HITS("hits"),
    MISSES("misses"),
    LOAD_TIME("loadTime"),
    PUT_TIME("putTime"),
    GET_TIME("getTime");

    private final String name;

    private CacheMetricsKeys(String name) {
        this.name = name;
    }

    public String key() {
        return this.name;
    }
}

