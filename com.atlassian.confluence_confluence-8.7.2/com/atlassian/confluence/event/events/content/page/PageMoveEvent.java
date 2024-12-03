/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.event.notification.NotificationEnabledEvent
 *  com.atlassian.fugue.Option
 *  com.atlassian.user.User
 *  com.google.common.base.Preconditions
 *  org.apache.commons.lang3.builder.ToStringBuilder
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.event.events.content.page;

import com.atlassian.confluence.api.model.event.notification.NotificationEnabledEvent;
import com.atlassian.confluence.event.events.content.page.PageEvent;
import com.atlassian.confluence.event.events.types.Updated;
import com.atlassian.confluence.event.events.types.UserDriven;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.fugue.Option;
import com.atlassian.user.User;
import com.google.common.base.Preconditions;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class PageMoveEvent
extends PageEvent
implements Updated,
UserDriven,
NotificationEnabledEvent {
    private static final long serialVersionUID = -1620473744653320164L;
    private final @Nullable User theMover;
    private final Space oldSpace;
    private final @Nullable Page oldParentPage;
    private final @Nullable Integer oldPosition;
    private final boolean movedBecauseOfParent;
    private final @Nullable List<Page> movedPageList;

    public PageMoveEvent(Object src, Page movedPage, List<Page> movedPageList, @Nullable Space oldSpace, @Nullable Page oldParentPage, @Nullable Integer oldPosition, @Nullable User theMover, boolean movedBecauseOfParent) {
        this(src, movedPage, (Option<List<Page>>)Option.some(movedPageList), oldSpace, oldParentPage, oldPosition, theMover, movedBecauseOfParent);
    }

    private PageMoveEvent(Object src, Page movedPage, Option<List<Page>> movedPageList, @Nullable Space oldSpace, @Nullable Page oldParentPage, @Nullable Integer oldPosition, @Nullable User theMover, boolean movedBecauseOfParent) {
        super(src, movedPage, false);
        Preconditions.checkArgument((oldSpace != null || oldParentPage != null ? 1 : 0) != 0, (Object)"oldSpace and oldParentPage cannot both be null");
        if (oldSpace == null) {
            oldSpace = Objects.requireNonNull(oldParentPage).getSpace();
        }
        Preconditions.checkArgument((oldSpace != null ? 1 : 0) != 0, (Object)"no space could be determined. Ensure oldParentPage has a space or explicitly set oldSpace");
        this.oldSpace = Objects.requireNonNull(oldSpace);
        this.oldParentPage = oldParentPage;
        this.oldPosition = oldPosition;
        this.theMover = theMover;
        this.movedBecauseOfParent = movedBecauseOfParent;
        this.movedPageList = (List)movedPageList.getOrNull();
    }

    public @NonNull Space getOldSpace() {
        return this.oldSpace;
    }

    public @Nullable Page getOldParentPage() {
        return this.oldParentPage;
    }

    public @Nullable Integer getOldPosition() {
        return this.oldPosition;
    }

    public @Nullable User getUser() {
        return this.theMover;
    }

    public @Nullable Page getNewParentPage() {
        return this.getPage().getParent();
    }

    public boolean hasMovedChildren() {
        return this.getPage().hasChildren() && this.isMovedSpace();
    }

    public boolean isMovedBecauseOfParent() {
        return this.movedBecauseOfParent;
    }

    public boolean isMovedSpace() {
        return !this.getPage().getSpaceKey().equals(this.oldSpace.getKey());
    }

    @Override
    public @Nullable User getOriginatingUser() {
        return this.theMover;
    }

    public @Nullable List<Page> getMovedPageList() {
        return this.movedPageList;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString((Object)this);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof PageMoveEvent)) {
            return false;
        }
        PageMoveEvent other = (PageMoveEvent)obj;
        if (this.movedBecauseOfParent != other.movedBecauseOfParent) {
            return false;
        }
        if (!Objects.equals(this.oldParentPage, other.oldParentPage)) {
            return false;
        }
        if (!Objects.equals(this.oldPosition, other.oldPosition)) {
            return false;
        }
        if (!Objects.equals(this.oldSpace, other.oldSpace)) {
            return false;
        }
        if (!Objects.equals(this.theMover, other.theMover)) {
            return false;
        }
        return Objects.equals(this.movedPageList, other.movedPageList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.movedBecauseOfParent, this.oldParentPage, this.oldPosition, this.oldSpace, this.theMover, this.movedPageList);
    }
}

