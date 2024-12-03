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
@EventName(value="confluence.space.trash.restore.content")
public class SpaceTrashRestoreContentEvent
extends SpaceTrashContentEvent {
    private static final long serialVersionUID = 4315978623298161699L;

    public SpaceTrashRestoreContentEvent(Object src, Space space, ContentTypeEnum contentType) {
        super(src, space, contentType);
    }
}

