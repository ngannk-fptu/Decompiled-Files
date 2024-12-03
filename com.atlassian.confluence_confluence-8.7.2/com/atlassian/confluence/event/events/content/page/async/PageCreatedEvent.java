/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.AsynchronousPreferred
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.confluence.event.events.content.page.async;

import com.atlassian.confluence.event.events.content.page.async.PageEvent;
import com.atlassian.confluence.event.events.types.Created;
import com.atlassian.event.api.AsynchronousPreferred;
import com.atlassian.sal.api.user.UserKey;

@AsynchronousPreferred
@Deprecated
public class PageCreatedEvent
extends PageEvent
implements Created {
    private static final long serialVersionUID = 4953042405040990029L;

    public PageCreatedEvent(Object src, UserKey userKey, Long pageId, Integer pageVersion, boolean suppressNotifications) {
        super(src, userKey, pageId, pageVersion, suppressNotifications);
    }
}

