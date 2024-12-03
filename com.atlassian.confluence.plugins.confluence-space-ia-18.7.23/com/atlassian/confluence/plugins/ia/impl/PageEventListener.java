/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.content.blogpost.BlogPostEvent
 *  com.atlassian.confluence.event.events.content.blogpost.BlogPostRemoveEvent
 *  com.atlassian.confluence.event.events.content.blogpost.BlogPostTrashedEvent
 *  com.atlassian.confluence.event.events.content.page.PageEvent
 *  com.atlassian.confluence.event.events.content.page.PageRemoveEvent
 *  com.atlassian.confluence.event.events.content.page.PageTrashedEvent
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.plugins.ia.impl;

import com.atlassian.confluence.event.events.content.blogpost.BlogPostEvent;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostRemoveEvent;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostTrashedEvent;
import com.atlassian.confluence.event.events.content.page.PageEvent;
import com.atlassian.confluence.event.events.content.page.PageRemoveEvent;
import com.atlassian.confluence.event.events.content.page.PageTrashedEvent;
import com.atlassian.confluence.plugins.ia.SidebarLink;
import com.atlassian.confluence.plugins.ia.SidebarLinkManager;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Qualifier;

public class PageEventListener
implements DisposableBean {
    private final EventPublisher eventPublisher;
    private final SidebarLinkManager sidebarLinkManager;

    public PageEventListener(EventPublisher eventPublisher, @Qualifier(value="sidebarLinkManager") SidebarLinkManager sidebarLinkManager) {
        this.eventPublisher = eventPublisher;
        this.sidebarLinkManager = sidebarLinkManager;
        eventPublisher.register((Object)this);
    }

    @EventListener
    public void onPageTrashed(PageTrashedEvent event) {
        this.handlePageDeletion((PageEvent)event);
    }

    @EventListener
    public void onPageRemoved(PageRemoveEvent event) {
        this.handlePageDeletion((PageEvent)event);
    }

    private void handlePageDeletion(PageEvent event) {
        this.sidebarLinkManager.deleteLinks(event.getPage().getId(), SidebarLink.Type.PINNED_PAGE);
    }

    @EventListener
    public void onBlogPostTrashed(BlogPostTrashedEvent event) {
        this.handleBlogPostDeletion((BlogPostEvent)event);
    }

    @EventListener
    public void onBlogPostRemoved(BlogPostRemoveEvent event) {
        this.handleBlogPostDeletion((BlogPostEvent)event);
    }

    private void handleBlogPostDeletion(BlogPostEvent event) {
        this.sidebarLinkManager.deleteLinks(event.getBlogPost().getId(), SidebarLink.Type.PINNED_BLOG_POST);
    }

    public void destroy() throws Exception {
        this.eventPublisher.unregister((Object)this);
    }
}

