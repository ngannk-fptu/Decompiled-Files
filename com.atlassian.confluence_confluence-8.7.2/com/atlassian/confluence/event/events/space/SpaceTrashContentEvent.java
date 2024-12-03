/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 */
package com.atlassian.confluence.event.events.space;

import com.atlassian.confluence.event.events.space.SpaceEvent;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.spaces.Space;
import com.google.common.base.Objects;

public abstract class SpaceTrashContentEvent
extends SpaceEvent {
    private final ContentTypeEnum contentType;

    public ContentTypeEnum getContentType() {
        return this.contentType;
    }

    protected SpaceTrashContentEvent(Object src, Space space, ContentTypeEnum contentType) {
        super(src, space);
        this.contentType = contentType;
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
        SpaceTrashContentEvent that = (SpaceTrashContentEvent)o;
        return this.contentType == that.contentType;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{super.hashCode(), this.contentType});
    }
}

