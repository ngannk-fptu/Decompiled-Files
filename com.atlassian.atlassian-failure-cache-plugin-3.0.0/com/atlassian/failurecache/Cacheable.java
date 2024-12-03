/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.failurecache;

public interface Cacheable {
    public int getCachePriority();

    public void clearCache();
}

