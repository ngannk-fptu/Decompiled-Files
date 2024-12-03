/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ConfluenceImport
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.pagehierarchy.analytics;

import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugins.pagehierarchy.analytics.CopyAnalyticsEvent;
import com.atlassian.confluence.plugins.pagehierarchy.analytics.DeleteAnalyticsEvent;
import com.atlassian.confluence.plugins.pagehierarchy.rest.CopyPageHierarchyRequest;
import com.atlassian.confluence.plugins.pagehierarchy.rest.DeletePageHierarchyRequest;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ConfluenceImport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AnalyticsPublisher {
    private final EventPublisher eventPublisher;
    private final PageManager pageManager;

    @Autowired
    public AnalyticsPublisher(@ConfluenceImport EventPublisher eventPublisher, @ConfluenceImport PageManager pageManager) {
        this.eventPublisher = eventPublisher;
        this.pageManager = pageManager;
    }

    public void publishCopyEvent(CopyPageHierarchyRequest request) {
        Page sourcePage = this.pageManager.getPage(request.getOriginalPageId().asLong());
        Page destinationPage = this.pageManager.getPage(request.getDestinationPageId().asLong());
        if (sourcePage != null && destinationPage != null) {
            int subtreeCount = this.pageManager.countPagesInSubtree(sourcePage);
            boolean sameSpace = destinationPage.getSpace().getId() == sourcePage.getSpace().getId();
            this.eventPublisher.publish((Object)new CopyAnalyticsEvent(request, subtreeCount, sameSpace));
        }
    }

    public void publishDeleteEvent(DeletePageHierarchyRequest request) {
        Page targetPage = this.pageManager.getPage(request.getTargetPageId().asLong());
        if (targetPage != null) {
            this.eventPublisher.publish((Object)new DeleteAnalyticsEvent(request.isDeleteHierarchy(), this.pageManager.countPagesInSubtree(targetPage)));
        }
    }
}

