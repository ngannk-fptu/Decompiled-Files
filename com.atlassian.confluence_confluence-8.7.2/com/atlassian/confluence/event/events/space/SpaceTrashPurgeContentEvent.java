/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.event.api.AsynchronousPreferred
 */
package com.atlassian.confluence.event.events.space;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.confluence.event.events.space.SpaceTrashContentEvent;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.event.api.AsynchronousPreferred;

@AsynchronousPreferred
@EventName(value="confluence.space.trash.purge.content")
public class SpaceTrashPurgeContentEvent
extends SpaceTrashContentEvent {
    private static final long serialVersionUID = -2925389067043522548L;

    public SpaceTrashPurgeContentEvent(Object src, Space space, ContentTypeEnum contentType) {
        super(src, space, contentType);
    }
}

