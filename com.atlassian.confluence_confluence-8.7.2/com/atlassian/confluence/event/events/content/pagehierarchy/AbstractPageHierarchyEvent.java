/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.event.events.content.pagehierarchy;

import com.atlassian.confluence.event.events.content.page.PageEvent;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.user.ConfluenceUser;
import java.util.Objects;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class AbstractPageHierarchyEvent
extends PageEvent {
    private final Optional<ConfluenceUser> initiator;
    private final int hierarchySize;

    @Deprecated
    public AbstractPageHierarchyEvent(Object source, Page page, @Nullable ConfluenceUser initiator, boolean suppressNotifications) {
        this(source, page, initiator, suppressNotifications, 1);
    }

    public AbstractPageHierarchyEvent(Object source, Page page, @Nullable ConfluenceUser initiator, boolean suppressNotifications, int hierarchySize) {
        super(source, page, suppressNotifications);
        this.initiator = Optional.ofNullable(initiator);
        this.hierarchySize = hierarchySize;
    }

    public Optional<ConfluenceUser> getInitiator() {
        return this.initiator;
    }

    public int getHierarchySize() {
        return this.hierarchySize;
    }

    @Override
    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!super.equals(other) || !(other instanceof AbstractPageHierarchyEvent)) {
            return false;
        }
        AbstractPageHierarchyEvent event = (AbstractPageHierarchyEvent)other;
        return Objects.equals(this.initiator, event.initiator) && this.hierarchySize == event.hierarchySize;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.initiator, this.hierarchySize);
    }

    public Page getTargetPage() {
        return this.getPage();
    }
}

