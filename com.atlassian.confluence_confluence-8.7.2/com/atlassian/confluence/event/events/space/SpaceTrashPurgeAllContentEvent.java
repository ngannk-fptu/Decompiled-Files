/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.event.api.AsynchronousPreferred
 *  com.google.common.base.Objects
 */
package com.atlassian.confluence.event.events.space;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.confluence.event.events.space.SpaceEvent;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.event.api.AsynchronousPreferred;
import com.google.common.base.Objects;

@AsynchronousPreferred
@EventName(value="confluence.space.trash.purge.all.content")
public class SpaceTrashPurgeAllContentEvent
extends SpaceEvent {
    private static final long serialVersionUID = 2780603769107016836L;
    private final int numberOfContent;

    public int getNumberOfContent() {
        return this.numberOfContent;
    }

    public SpaceTrashPurgeAllContentEvent(Object src, Space space, int numberOfContent) {
        super(src, space);
        this.numberOfContent = numberOfContent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        SpaceTrashPurgeAllContentEvent that = (SpaceTrashPurgeAllContentEvent)o;
        return this.numberOfContent == that.numberOfContent;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{super.hashCode(), this.numberOfContent});
    }
}

