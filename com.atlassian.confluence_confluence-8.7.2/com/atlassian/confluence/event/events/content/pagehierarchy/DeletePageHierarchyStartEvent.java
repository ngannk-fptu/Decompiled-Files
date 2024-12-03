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
import org.checkerframework.checker.nullness.qual.Nullable;

public final class DeletePageHierarchyStartEvent
extends AbstractPageHierarchyEvent {
    private static final long serialVersionUID = 2870944883793554140L;

    @Deprecated
    public DeletePageHierarchyStartEvent(Object source, Page page, @Nullable ConfluenceUser initiator, boolean suppressNotifications) {
        super(source, page, initiator, suppressNotifications);
    }

    public DeletePageHierarchyStartEvent(Object source, Page page, @Nullable ConfluenceUser initiator, boolean suppressNotifications, int hierarchySize) {
        super(source, page, initiator, suppressNotifications, hierarchySize);
    }
}

