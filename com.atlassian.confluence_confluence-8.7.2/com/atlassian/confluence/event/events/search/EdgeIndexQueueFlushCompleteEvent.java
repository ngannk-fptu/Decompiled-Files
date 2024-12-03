/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.search;

import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.confluence.search.FlushStatistics;

public class EdgeIndexQueueFlushCompleteEvent
extends ConfluenceEvent {
    private static final long serialVersionUID = 6929370792096690466L;
    private final FlushStatistics flushStatistics;

    public EdgeIndexQueueFlushCompleteEvent(Object src, FlushStatistics flushStatistics) {
        super(src);
        this.flushStatistics = flushStatistics;
    }

    public FlushStatistics getFlushStatistics() {
        return this.flushStatistics;
    }
}

