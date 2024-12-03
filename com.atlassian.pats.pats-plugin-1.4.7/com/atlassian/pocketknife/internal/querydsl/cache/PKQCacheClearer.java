/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.pocketknife.internal.querydsl.cache;

public interface PKQCacheClearer {
    public void onClearCache(Object var1);

    public void registerCacheClearing(Runnable var1);

    public void clearAllCaches();
}

