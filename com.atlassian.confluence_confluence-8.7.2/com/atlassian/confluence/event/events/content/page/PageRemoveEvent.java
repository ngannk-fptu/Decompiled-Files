/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.content.page;

import com.atlassian.confluence.event.events.content.page.PageEvent;
import com.atlassian.confluence.event.events.types.Removed;
import com.atlassian.confluence.pages.Page;

public class PageRemoveEvent
extends PageEvent
implements Removed {
    private static final long serialVersionUID = -8797904852629931396L;

    public PageRemoveEvent(Object src, Page removedPage) {
        super(src, removedPage, false);
    }
}

