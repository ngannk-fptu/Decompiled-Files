/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.spaces.SpaceDescription
 *  com.atlassian.streams.api.StreamsEntry$ActivityObject
 *  com.atlassian.streams.api.StreamsEntry$Renderer
 *  com.atlassian.streams.api.common.Option
 */
package com.atlassian.streams.confluence.changereport;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.spaces.SpaceDescription;
import com.atlassian.streams.api.StreamsEntry;
import com.atlassian.streams.api.common.Option;
import com.atlassian.streams.confluence.changereport.ContentEntityActivityItem;
import java.util.Date;

final class SpaceActivityItem
extends ContentEntityActivityItem {
    private final boolean isCreationEvent;

    public SpaceActivityItem(SpaceDescription space, boolean isCreationEvent, Iterable<StreamsEntry.ActivityObject> activityObjects, Option<StreamsEntry.ActivityObject> target, StreamsEntry.Renderer renderer) {
        super((ContentEntityObject)space, activityObjects, target, renderer);
        this.isCreationEvent = isCreationEvent;
    }

    @Override
    public boolean isNew() {
        return this.isCreationEvent;
    }

    @Override
    public Date getModified() {
        return this.isNew() ? this.getEntity().getCreationDate() : this.getEntity().getLastModificationDate();
    }

    @Override
    public String getIconPath() {
        return "/images/icons/web_16.gif";
    }
}

