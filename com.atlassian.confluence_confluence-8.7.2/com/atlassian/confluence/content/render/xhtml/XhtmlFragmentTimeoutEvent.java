/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.event.events.ConfluenceEvent;

public class XhtmlFragmentTimeoutEvent
extends ConfluenceEvent {
    private static final long serialVersionUID = -5064978121877546640L;
    private final ContentEntityObject entity;
    private final long allowedTimeInSeconds;
    private final long exceededTimeInMilliseconds;

    public XhtmlFragmentTimeoutEvent(Object src, ContentEntityObject entity, long allowedTimeInSeconds, long exceededTimeInMilliseconds) {
        super(src);
        this.entity = entity;
        this.allowedTimeInSeconds = allowedTimeInSeconds;
        this.exceededTimeInMilliseconds = exceededTimeInMilliseconds;
    }

    public ContentEntityObject getEntity() {
        return this.entity;
    }

    public long getAllowedTimeInSeconds() {
        return this.allowedTimeInSeconds;
    }

    public long getExceededTimeInMilliseconds() {
        return this.exceededTimeInMilliseconds;
    }
}

