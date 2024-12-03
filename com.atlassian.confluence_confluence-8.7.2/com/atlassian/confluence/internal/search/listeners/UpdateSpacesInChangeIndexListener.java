/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 */
package com.atlassian.confluence.internal.search.listeners;

import com.atlassian.confluence.event.events.content.page.PageMoveEvent;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.search.ChangeIndexer;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.event.api.EventListener;
import java.util.List;

public class UpdateSpacesInChangeIndexListener {
    private final ChangeIndexer changeIndexer;

    public UpdateSpacesInChangeIndexListener(ChangeIndexer changeIndexer) {
        this.changeIndexer = changeIndexer;
    }

    @EventListener
    public void handleEvent(PageMoveEvent moveEvent) {
        Page movedPage = moveEvent.getPage();
        Space oldSpace = moveEvent.getOldSpace();
        Space newSpace = movedPage.getSpace();
        List<Page> movedPageList = moveEvent.getMovedPageList();
        if (!oldSpace.equals(newSpace) && movedPageList != null) {
            for (Page page : movedPageList) {
                this.changeIndexer.reIndexAllVersions(page);
            }
        }
    }
}

