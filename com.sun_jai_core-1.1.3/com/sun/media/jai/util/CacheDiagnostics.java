/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.util;

public interface CacheDiagnostics {
    public void enableDiagnostics();

    public void disableDiagnostics();

    public long getCacheTileCount();

    public long getCacheMemoryUsed();

    public long getCacheHitCount();

    public long getCacheMissCount();

    public void resetCounts();
}

