/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.confluence.plugins.gadgets.events;

import com.atlassian.analytics.api.annotations.EventName;

@EventName(value="gadget.cacheFlushed")
public class GadgetCacheFlushEvent {
    private final int cacheKeysFlushed;

    public GadgetCacheFlushEvent(int cacheKeysFlushed) {
        this.cacheKeysFlushed = cacheKeysFlushed;
    }

    public int getNumberOfCacheKeysFlushed() {
        return this.cacheKeysFlushed;
    }
}

