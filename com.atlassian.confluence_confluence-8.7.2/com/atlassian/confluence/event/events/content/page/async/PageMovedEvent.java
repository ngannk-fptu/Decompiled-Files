/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.AsynchronousPreferred
 *  com.atlassian.sal.api.user.UserKey
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.event.events.content.page.async;

import com.atlassian.confluence.event.events.content.page.async.PageEvent;
import com.atlassian.confluence.event.events.content.page.async.types.UserDriven;
import com.atlassian.confluence.event.events.types.Updated;
import com.atlassian.event.api.AsynchronousPreferred;
import com.atlassian.sal.api.user.UserKey;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.Nullable;

@AsynchronousPreferred
@Deprecated
public class PageMovedEvent
extends PageEvent
implements Updated,
UserDriven {
    private static final long serialVersionUID = -6994323184155599211L;
    private final String originalSpaceKey;
    private final String currentSpaceKey;
    private final Long originalParentPageId;
    private final Long currentParentPageId;
    private final boolean movedBecauseOfParent;
    private final boolean hasMovedChildren;

    public PageMovedEvent(Object src, UserKey userKey, Long movedPageId, Integer movedPageVersion, String originalSpaceKey, String currentSpaceKey, Long originalParentPageId, Long currentParentPageId, boolean movedBecauseOfParent, boolean hasMovedChildren, boolean suppressNotifications) {
        super(src, userKey, movedPageId, movedPageVersion, suppressNotifications);
        this.originalSpaceKey = originalSpaceKey;
        this.currentSpaceKey = currentSpaceKey;
        this.originalParentPageId = originalParentPageId;
        this.currentParentPageId = currentParentPageId;
        this.movedBecauseOfParent = movedBecauseOfParent;
        this.hasMovedChildren = hasMovedChildren;
    }

    public String getOriginalSpaceKey() {
        return this.originalSpaceKey;
    }

    public String getCurrentSpaceKey() {
        return this.currentSpaceKey;
    }

    public Long getOriginalParentPageId() {
        return this.originalParentPageId;
    }

    public Long getCurrentParentPageId() {
        return this.currentParentPageId;
    }

    public boolean hasMovedChildren() {
        return this.hasMovedChildren;
    }

    public boolean isMovedBecauseOfParent() {
        return this.movedBecauseOfParent;
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
        PageMovedEvent that = (PageMovedEvent)o;
        return this.movedBecauseOfParent == that.movedBecauseOfParent && this.hasMovedChildren == that.hasMovedChildren && Objects.equals(this.originalSpaceKey, that.originalSpaceKey) && Objects.equals(this.currentSpaceKey, that.currentSpaceKey) && Objects.equals(this.originalParentPageId, that.originalParentPageId) && Objects.equals(this.currentParentPageId, that.currentParentPageId);
    }

    public int hashCode() {
        return Objects.hash(super.hashCode(), this.originalSpaceKey, this.currentSpaceKey, this.originalParentPageId, this.currentParentPageId, this.movedBecauseOfParent, this.hasMovedChildren);
    }
}

