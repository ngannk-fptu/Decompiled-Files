/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Stopwatch
 *  com.google.common.collect.Maps
 */
package com.atlassian.confluence.pages.ancestors;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.concurrent.TimeUnit;

class AncestorRebuildMetrics {
    int chunkCount;
    int maxAncestors;
    int ancestorsCount;
    int totalChildren;
    private final Map<StopwatchKey, Stopwatch> stopwatches = Maps.newHashMap();

    AncestorRebuildMetrics() {
        for (StopwatchKey stopwatchKey : StopwatchKey.values()) {
            this.stopwatches.put(stopwatchKey, Stopwatch.createUnstarted());
        }
    }

    public int incrementAncestorsCount() {
        return ++this.ancestorsCount;
    }

    public int incrementChunkCount() {
        return ++this.chunkCount;
    }

    public void setMaxAncestors(int ancestorCount) {
        if (ancestorCount > this.maxAncestors) {
            this.maxAncestors = ancestorCount;
        }
    }

    public void startStopwatch(StopwatchKey key) {
        this.getStopwatch(key).start();
    }

    public void stopStopwatch(StopwatchKey key) {
        this.getStopwatch(key).stop();
    }

    public long getStopwatchMillis(StopwatchKey key) {
        return this.getStopwatch(key).elapsed(TimeUnit.MILLISECONDS);
    }

    private Stopwatch getStopwatch(StopwatchKey key) {
        return this.stopwatches.get((Object)key);
    }

    static enum StopwatchKey {
        CLEAR_ANCESTORS,
        GET_CHILD_PARENT_PAIRS,
        CALCULATE_PARENT_MAP,
        CALCULATE_ANCESTOR_MAP,
        STORE_ANCESTORS;

    }
}

