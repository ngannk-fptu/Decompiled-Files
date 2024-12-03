/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  com.google.common.base.Preconditions
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.event.events.content.page;

import com.atlassian.confluence.event.events.content.page.PageEvent;
import com.atlassian.confluence.event.events.types.Updated;
import com.atlassian.confluence.pages.Page;
import com.atlassian.user.User;
import com.google.common.base.Preconditions;
import java.util.List;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class PageChildrenReorderEvent
extends PageEvent
implements Updated {
    private static final long serialVersionUID = -461784977419488914L;
    private final @Nullable User user;
    private final List<Page> oldSortedChildren;
    private final List<Page> newSortedChildren;

    public PageChildrenReorderEvent(Object src, Page parentPage, List<Page> oldSortedChildren, List<Page> newSortedChildren, @Nullable User user) {
        super(src, parentPage, false);
        this.oldSortedChildren = (List)Preconditions.checkNotNull(oldSortedChildren);
        this.newSortedChildren = (List)Preconditions.checkNotNull(newSortedChildren);
        this.user = user;
    }

    public @Nullable User getUser() {
        return this.user;
    }

    public @NonNull List<Page> getOldSortedChildPages() {
        return this.oldSortedChildren;
    }

    public @NonNull List<Page> getNewSortedChildPages() {
        return this.newSortedChildren;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof PageChildrenReorderEvent)) {
            return false;
        }
        PageChildrenReorderEvent that = (PageChildrenReorderEvent)obj;
        if (!this.newSortedChildren.equals(that.newSortedChildren)) {
            return false;
        }
        if (!this.oldSortedChildren.equals(that.oldSortedChildren)) {
            return false;
        }
        return Objects.equals(this.user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.newSortedChildren, this.oldSortedChildren, this.user);
    }
}

