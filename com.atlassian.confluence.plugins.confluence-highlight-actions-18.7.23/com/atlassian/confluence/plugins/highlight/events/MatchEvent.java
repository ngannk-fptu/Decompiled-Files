/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.confluence.event.events.ConfluenceEvent
 */
package com.atlassian.confluence.plugins.highlight.events;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.confluence.event.events.ConfluenceEvent;

public class MatchEvent
extends ConfluenceEvent {
    private final String modifier;
    private final boolean found;
    private final long pageId;

    public MatchEvent(Object src, String modifier, boolean found, long pageId) {
        super(src);
        this.modifier = modifier;
        this.found = found;
        this.pageId = pageId;
    }

    @EventName
    public String buildName() {
        return "confluence.highlight." + this.modifier + ".match." + this.found;
    }

    public long getPageId() {
        return this.pageId;
    }
}

