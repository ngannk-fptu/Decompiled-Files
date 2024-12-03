/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.AsynchronousPreferred
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.confluence.event.events.content.page.async;

import com.atlassian.confluence.event.events.content.page.async.PageEvent;
import com.atlassian.confluence.event.events.content.page.async.types.UserDriven;
import com.atlassian.confluence.event.events.types.Trashed;
import com.atlassian.event.api.AsynchronousPreferred;
import com.atlassian.sal.api.user.UserKey;

@AsynchronousPreferred
@Deprecated
public class PageTrashedEvent
extends PageEvent
implements Trashed,
UserDriven {
    private static final long serialVersionUID = -4185727891238441894L;

    public PageTrashedEvent(Object src, UserKey userKey, Long trashedPageId, Integer trashedPageVersion, boolean suppressNotifications) {
        super(src, userKey, trashedPageId, trashedPageVersion, suppressNotifications);
    }
}

