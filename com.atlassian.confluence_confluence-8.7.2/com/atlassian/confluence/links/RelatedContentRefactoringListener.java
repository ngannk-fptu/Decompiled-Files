/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 */
package com.atlassian.confluence.links;

import com.atlassian.confluence.event.events.content.attachment.AttachmentUpdateEvent;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostUpdateEvent;
import com.atlassian.confluence.event.events.content.page.PageMoveCompletedEvent;
import com.atlassian.confluence.event.events.content.page.PageMoveEvent;
import com.atlassian.confluence.event.events.content.page.PageUpdateEvent;
import com.atlassian.confluence.links.RelatedContentRefactorer;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Page;
import com.atlassian.event.api.EventListener;

public class RelatedContentRefactoringListener {
    private RelatedContentRefactorer relatedContentRefactorer;

    public void setRelatedContentRefactorer(RelatedContentRefactorer relatedContentRefactorer) {
        this.relatedContentRefactorer = relatedContentRefactorer;
    }

    @EventListener
    public void handleEvent(PageMoveEvent event) {
        if (!event.isMovedSpace() || event.getPage().isDraft()) {
            return;
        }
        this.relatedContentRefactorer.updateReferrersForMovingPage(event.getPage(), event.getOldSpace(), event.getPage().getTitle(), event.getMovedPageList());
    }

    @EventListener
    public void handleEvent(PageMoveCompletedEvent event) {
        if (event.getMovedPageList().isEmpty()) {
            return;
        }
        this.relatedContentRefactorer.contractAbsoluteReferencesInContent(event.getMovedPageList());
    }

    @EventListener
    public void handleEvent(PageUpdateEvent event) {
        if (!event.isTitleChanged()) {
            return;
        }
        Page page = event.getPage();
        AbstractPage originalPage = event.getOriginalPage();
        if (originalPage == null) {
            return;
        }
        this.relatedContentRefactorer.updateReferrers(page, page.getSpace(), originalPage.getTitle());
        this.relatedContentRefactorer.updateReferences(page, page.getSpace(), originalPage.getTitle());
    }

    @EventListener
    public void handleEvent(BlogPostUpdateEvent event) {
        if (!event.isTitleChanged()) {
            return;
        }
        BlogPost blogPost = event.getBlogPost();
        BlogPost originalBlogPost = event.getOriginalBlogPost();
        if (originalBlogPost == null) {
            return;
        }
        this.relatedContentRefactorer.updateReferrers(blogPost, blogPost.getSpace(), originalBlogPost.getTitle());
        this.relatedContentRefactorer.updateReferences(blogPost, blogPost.getSpace(), originalBlogPost.getTitle());
    }

    @EventListener
    public void handleEvent(AttachmentUpdateEvent event) {
        if (!event.isAttachmentContainerUpdated() && !event.isFileNameChanged()) {
            return;
        }
        this.relatedContentRefactorer.updateReferrers(event.getNew(), event.getOld());
    }
}

