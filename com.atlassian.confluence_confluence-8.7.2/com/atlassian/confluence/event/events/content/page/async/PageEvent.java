/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.Event
 *  com.atlassian.event.api.AsynchronousPreferred
 *  com.atlassian.sal.api.user.UserKey
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.confluence.event.events.content.page.async;

import com.atlassian.confluence.event.events.content.page.async.types.UserDriven;
import com.atlassian.event.Event;
import com.atlassian.event.api.AsynchronousPreferred;
import com.atlassian.sal.api.user.UserKey;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Deprecated
@AsynchronousPreferred
public abstract class PageEvent
extends Event
implements UserDriven {
    private final UserKey originatingUserKey;
    private final Long pageId;
    private final Integer pageVersion;
    private final boolean suppressNotifications;

    public PageEvent(Object src, UserKey originatingUserKey, Long pageId, Integer pageVersion, boolean suppressNotifications) {
        super(src);
        this.originatingUserKey = originatingUserKey;
        this.pageId = pageId;
        this.pageVersion = pageVersion;
        this.suppressNotifications = suppressNotifications;
    }

    @Override
    public UserKey getOriginatingUserKey() {
        return this.originatingUserKey;
    }

    public Long getPageId() {
        return this.pageId;
    }

    public Integer getPageVersion() {
        return this.pageVersion;
    }

    public boolean isSuppressNotifications() {
        return this.suppressNotifications;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString((Object)this);
    }
}

