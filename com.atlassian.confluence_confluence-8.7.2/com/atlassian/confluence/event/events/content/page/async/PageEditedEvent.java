/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.AsynchronousPreferred
 *  com.atlassian.sal.api.user.UserKey
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.event.events.content.page.async;

import com.atlassian.confluence.event.events.content.Edited;
import com.atlassian.confluence.event.events.content.page.async.PageEvent;
import com.atlassian.confluence.event.events.content.page.async.types.ConfluenceEntityUpdated;
import com.atlassian.event.api.AsynchronousPreferred;
import com.atlassian.sal.api.user.UserKey;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.Nullable;

@AsynchronousPreferred
@Deprecated
public class PageEditedEvent
extends PageEvent
implements Edited,
ConfluenceEntityUpdated {
    private static final long serialVersionUID = -9189037781957026407L;
    private final Long originalPageId;
    private final Integer originalVersion;

    public PageEditedEvent(Object src, UserKey userKey, Long currentPageId, Integer currentVersion, Long originalPageId, Integer originalVersion, boolean suppressNotifications) {
        super(src, userKey, currentPageId, currentVersion, suppressNotifications);
        this.originalPageId = originalPageId;
        this.originalVersion = originalVersion;
    }

    @Override
    public Long getCurrentId() {
        return this.getPageId();
    }

    @Override
    public Integer getCurrentVersion() {
        return this.getPageVersion();
    }

    @Override
    public Long getOriginalId() {
        return this.originalPageId;
    }

    @Override
    public Integer getOriginalVersion() {
        return this.originalVersion;
    }

    @Override
    public boolean isMinorEdit() {
        return this.isSuppressNotifications();
    }

    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        PageEditedEvent that = (PageEditedEvent)o;
        return Objects.equals(this.originalPageId, that.originalPageId) && Objects.equals(this.originalVersion, that.originalVersion);
    }

    public int hashCode() {
        return Objects.hash(super.hashCode(), this.originalPageId, this.originalVersion);
    }
}

