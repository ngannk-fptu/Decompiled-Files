/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.event.events.content.pagehierarchy;

import com.atlassian.confluence.event.events.content.pagehierarchy.AbstractPageHierarchyEvent;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.user.ConfluenceUser;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class AbstractCopyPageHierarchyEvent
extends AbstractPageHierarchyEvent {
    private final boolean includeAttachments;
    private final boolean includeRestrictions;
    private final boolean includeLabels;
    private final Page destination;

    @Deprecated
    public AbstractCopyPageHierarchyEvent(Object source, Page root, Page destination, @Nullable ConfluenceUser initiator, boolean suppressNotifications) {
        super(source, root, initiator, suppressNotifications);
        this.destination = destination;
        this.includeAttachments = false;
        this.includeRestrictions = false;
        this.includeLabels = false;
    }

    public AbstractCopyPageHierarchyEvent(Object source, Page root, Page destination, @Nullable ConfluenceUser initiator, boolean suppressNotifications, int hierarchySize, boolean includeAttachments, boolean includeRestrictions, boolean includeLabels) {
        super(source, root, initiator, suppressNotifications, hierarchySize);
        this.destination = destination;
        this.includeAttachments = includeAttachments;
        this.includeRestrictions = includeRestrictions;
        this.includeLabels = includeLabels;
    }

    public Page getDestination() {
        return this.destination;
    }

    @Override
    public final Page getTargetPage() {
        return this.destination;
    }

    public boolean isIncludeAttachments() {
        return this.includeAttachments;
    }

    public boolean isIncludeRestrictions() {
        return this.includeRestrictions;
    }

    public boolean isIncludeLabels() {
        return this.includeLabels;
    }

    @Override
    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!super.equals(other) || !(other instanceof AbstractCopyPageHierarchyEvent)) {
            return false;
        }
        AbstractCopyPageHierarchyEvent event = (AbstractCopyPageHierarchyEvent)other;
        return Objects.equals(this.destination, event.destination);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.destination);
    }
}

