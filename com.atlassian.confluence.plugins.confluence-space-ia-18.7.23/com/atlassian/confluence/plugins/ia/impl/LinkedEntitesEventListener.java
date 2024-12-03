/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.content.ContentEvent
 *  com.atlassian.confluence.event.events.content.attachment.AttachmentEvent
 *  com.atlassian.confluence.event.events.content.blogpost.BlogPostEvent
 *  com.atlassian.confluence.event.events.content.blogpost.BlogPostRemoveEvent
 *  com.atlassian.confluence.event.events.content.blogpost.BlogPostTrashedEvent
 *  com.atlassian.confluence.event.events.content.page.PageEvent
 *  com.atlassian.confluence.event.events.content.page.PageRemoveEvent
 *  com.atlassian.confluence.event.events.content.page.PageTrashedEvent
 *  com.atlassian.confluence.event.events.content.user.PersonalInformationRemoveEvent
 *  com.atlassian.confluence.event.events.space.SpaceRemoveEvent
 *  com.atlassian.confluence.event.events.types.Removed
 *  com.atlassian.confluence.event.events.types.Trashed
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.plugins.ia.impl;

import com.atlassian.confluence.event.events.content.ContentEvent;
import com.atlassian.confluence.event.events.content.attachment.AttachmentEvent;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostEvent;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostRemoveEvent;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostTrashedEvent;
import com.atlassian.confluence.event.events.content.page.PageEvent;
import com.atlassian.confluence.event.events.content.page.PageRemoveEvent;
import com.atlassian.confluence.event.events.content.page.PageTrashedEvent;
import com.atlassian.confluence.event.events.content.user.PersonalInformationRemoveEvent;
import com.atlassian.confluence.event.events.space.SpaceRemoveEvent;
import com.atlassian.confluence.event.events.types.Removed;
import com.atlassian.confluence.event.events.types.Trashed;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.plugins.ia.SidebarLink;
import com.atlassian.confluence.plugins.ia.SidebarLinkManager;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Qualifier;

public class LinkedEntitesEventListener
implements DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(LinkedEntitesEventListener.class);
    private final EventPublisher eventPublisher;
    private final SidebarLinkManager sidebarLinkManager;

    public LinkedEntitesEventListener(EventPublisher eventPublisher, @Qualifier(value="sidebarLinkManager") SidebarLinkManager sidebarLinkManager) {
        this.eventPublisher = eventPublisher;
        this.sidebarLinkManager = sidebarLinkManager;
        eventPublisher.register((Object)this);
    }

    @EventListener
    public void onPageTrashed(PageTrashedEvent event) {
        this.handleAttachments((ContentEvent)event);
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
        this.handleAttachments((ContentEvent)event);
        this.handleBlogPostDeletion((BlogPostEvent)event);
    }

    @EventListener
    public void onBlogPostRemoved(BlogPostRemoveEvent event) {
        this.handleBlogPostDeletion((BlogPostEvent)event);
    }

    private void handleBlogPostDeletion(BlogPostEvent event) {
        this.sidebarLinkManager.deleteLinks(event.getBlogPost().getId(), SidebarLink.Type.PINNED_BLOG_POST);
    }

    private void handleAttachments(ContentEvent event) {
        for (Attachment attachment : event.getContent().getAttachments()) {
            this.sidebarLinkManager.deleteLinks(attachment.getId(), SidebarLink.Type.PINNED_ATTACHMENT);
        }
    }

    @EventListener
    public void onPersonalInformationRemoved(PersonalInformationRemoveEvent event) {
        this.sidebarLinkManager.deleteLinks(event.getPersonalInformation().getId(), SidebarLink.Type.PINNED_USER_INFO);
    }

    @EventListener
    public void onSpaceRemoved(SpaceRemoveEvent event) {
        this.sidebarLinkManager.deleteLinks(event.getSpace().getId(), SidebarLink.Type.PINNED_SPACE);
        this.sidebarLinkManager.deleteLinksForSpace(event.getSpace().getKey());
    }

    @EventListener
    public void onAttachmentRemoved(AttachmentEvent event) {
        if (!(event instanceof Removed) && !(event instanceof Trashed)) {
            return;
        }
        List attachments = event.getAttachments();
        if (attachments.size() < 1) {
            log.warn("Could not get attachment from an attachment removed or trashed event.");
            return;
        }
        for (Attachment attachment : attachments) {
            this.sidebarLinkManager.deleteLinks(attachment.getId(), SidebarLink.Type.PINNED_ATTACHMENT);
        }
    }

    public void destroy() throws Exception {
        this.eventPublisher.unregister((Object)this);
    }
}

