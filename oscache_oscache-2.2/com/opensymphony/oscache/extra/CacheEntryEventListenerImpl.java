/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.oscache.extra;

import com.opensymphony.oscache.base.events.CacheEntryEvent;
import com.opensymphony.oscache.base.events.CacheEntryEventListener;
import com.opensymphony.oscache.base.events.CacheGroupEvent;
import com.opensymphony.oscache.base.events.CachePatternEvent;
import com.opensymphony.oscache.base.events.CachewideEvent;

public class CacheEntryEventListenerImpl
implements CacheEntryEventListener {
    private int cacheFlushedCount = 0;
    private int entryAddedCount = 0;
    private int entryFlushedCount = 0;
    private int entryRemovedCount = 0;
    private int entryUpdatedCount = 0;
    private int groupFlushedCount = 0;
    private int patternFlushedCount = 0;

    public int getEntryAddedCount() {
        return this.entryAddedCount;
    }

    public int getEntryFlushedCount() {
        return this.entryFlushedCount;
    }

    public int getEntryRemovedCount() {
        return this.entryRemovedCount;
    }

    public int getEntryUpdatedCount() {
        return this.entryUpdatedCount;
    }

    public int getGroupFlushedCount() {
        return this.groupFlushedCount;
    }

    public int getPatternFlushedCount() {
        return this.patternFlushedCount;
    }

    public int getCacheFlushedCount() {
        return this.cacheFlushedCount;
    }

    public void cacheEntryAdded(CacheEntryEvent event) {
        ++this.entryAddedCount;
    }

    public void cacheEntryFlushed(CacheEntryEvent event) {
        ++this.entryFlushedCount;
    }

    public void cacheEntryRemoved(CacheEntryEvent event) {
        ++this.entryRemovedCount;
    }

    public void cacheEntryUpdated(CacheEntryEvent event) {
        ++this.entryUpdatedCount;
    }

    public void cacheGroupFlushed(CacheGroupEvent event) {
        ++this.groupFlushedCount;
    }

    public void cachePatternFlushed(CachePatternEvent event) {
        ++this.patternFlushedCount;
    }

    public void cacheFlushed(CachewideEvent event) {
        ++this.cacheFlushedCount;
    }

    public String toString() {
        return "Added " + this.entryAddedCount + ", Updated " + this.entryUpdatedCount + ", Flushed " + this.entryFlushedCount + ", Removed " + this.entryRemovedCount + ", Groups Flushed " + this.groupFlushedCount + ", Patterns Flushed " + this.patternFlushedCount + ", Cache Flushed " + this.cacheFlushedCount;
    }
}

