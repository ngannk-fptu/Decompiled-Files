/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.instrumentation.caches;

import com.atlassian.instrumentation.Instrument;

public interface CacheInstrument
extends Instrument {
    public long getMisses();

    public long getMissTime();

    public long getHits();

    public long getCacheSize();

    public double getHitMissRatio();
}

