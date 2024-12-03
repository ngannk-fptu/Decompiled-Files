/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.instrumentation.caches;

import java.util.Objects;

public enum CacheKeys {
    HITS("hits"),
    MISSES("misses"),
    REMOVES("removes"),
    PUTS("puts"),
    LOADS("loads"),
    LOAD_TIME("loadTime"),
    COUNT("count");

    private final String name;

    private CacheKeys(String name) {
        this.name = Objects.requireNonNull(name);
    }

    public String toString() {
        return this.name;
    }

    public String getName() {
        return this.name;
    }
}

