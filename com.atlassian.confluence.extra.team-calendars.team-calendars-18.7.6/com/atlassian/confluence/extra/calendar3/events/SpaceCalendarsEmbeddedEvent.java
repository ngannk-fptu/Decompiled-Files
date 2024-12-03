/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.AsynchronousPreferred
 */
package com.atlassian.confluence.extra.calendar3.events;

import com.atlassian.event.api.AsynchronousPreferred;
import java.io.Serializable;
import java.util.Set;

@AsynchronousPreferred
public class SpaceCalendarsEmbeddedEvent
implements Serializable {
    private Set<String> subCalendarIds;
    private String spaceKey;

    public SpaceCalendarsEmbeddedEvent(Set<String> subCalendarIds, String spaceKey) {
        this.subCalendarIds = subCalendarIds;
        this.spaceKey = spaceKey;
    }

    public Set<String> getSubCalendarIds() {
        return this.subCalendarIds;
    }

    public void setSubCalendarIds(Set<String> subCalendarIds) {
        this.subCalendarIds = subCalendarIds;
    }

    public String getSpaceKey() {
        return this.spaceKey;
    }

    public void setSpaceKey(String spaceKey) {
        this.spaceKey = spaceKey;
    }
}

