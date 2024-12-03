/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.confluence.api.model.content.ContentStatus
 *  com.atlassian.confluence.api.model.content.id.ContentId
 */
package com.atlassian.confluence.event.events.content.page.synchrony;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.api.model.content.ContentStatus;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.pages.PageUpdateTrigger;
import com.atlassian.confluence.user.ConfluenceUser;
import java.util.Objects;

@Internal
public class ContentUpdatedEvent {
    private final ConfluenceUser user;
    private final ContentId contentId;
    private final ContentStatus contentStatus;
    private final String spaceKey;
    private final String syncRev;
    private final PageUpdateTrigger updateTrigger;

    public ContentUpdatedEvent(ConfluenceUser user, ContentId contentId, ContentStatus contentStatus, String spaceKey, String syncRev) {
        this(user, contentId, contentStatus, spaceKey, syncRev, PageUpdateTrigger.UNKNOWN);
    }

    public ContentUpdatedEvent(ConfluenceUser user, ContentId contentId, ContentStatus contentStatus, String spaceKey, String syncRev, PageUpdateTrigger updateTrigger) {
        this.user = user;
        this.contentId = contentId;
        this.contentStatus = contentStatus;
        this.spaceKey = spaceKey;
        this.syncRev = syncRev;
        this.updateTrigger = updateTrigger;
    }

    public ConfluenceUser getUser() {
        return this.user;
    }

    public ContentId getContentId() {
        return this.contentId;
    }

    public ContentStatus getContentStatus() {
        return this.contentStatus;
    }

    public String getSpaceKey() {
        return this.spaceKey;
    }

    public String getSyncRev() {
        return this.syncRev;
    }

    public PageUpdateTrigger getUpdateTrigger() {
        return this.updateTrigger;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ContentUpdatedEvent that = (ContentUpdatedEvent)o;
        return Objects.equals(this.user, that.user) && Objects.equals(this.contentId, that.contentId) && Objects.equals(this.contentStatus, that.contentStatus) && Objects.equals(this.spaceKey, that.spaceKey) && Objects.equals(this.syncRev, that.syncRev) && this.updateTrigger == that.updateTrigger;
    }

    public int hashCode() {
        return Objects.hash(this.user, this.contentId, this.contentStatus, this.spaceKey, this.syncRev, this.updateTrigger);
    }
}

