/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.event.events.content.pagehierarchy;

import com.atlassian.confluence.event.events.content.pagehierarchy.AbstractCopyPageHierarchyEvent;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.user.ConfluenceUser;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class CopyPageHierarchyFinishEvent
extends AbstractCopyPageHierarchyEvent {
    private static final long serialVersionUID = 7276575930455520771L;

    @Deprecated
    public CopyPageHierarchyFinishEvent(Object source, Page root, Page destination, @Nullable ConfluenceUser initiator, boolean suppressNotifications) {
        super(source, root, destination, initiator, suppressNotifications);
    }

    public CopyPageHierarchyFinishEvent(Object source, Page root, Page destination, @Nullable ConfluenceUser initiator, boolean suppressNotifications, int hierarchySize, boolean includeAttachments, boolean includeRestrictions, boolean includeLabels) {
        super(source, root, destination, initiator, suppressNotifications, hierarchySize, includeAttachments, includeRestrictions, includeLabels);
    }
}

