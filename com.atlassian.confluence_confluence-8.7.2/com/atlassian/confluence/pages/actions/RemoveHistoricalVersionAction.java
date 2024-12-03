/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.confluence.content.service.BlogPostService;
import com.atlassian.confluence.content.service.PageService;
import com.atlassian.confluence.content.service.blogpost.BlogPostLocator;
import com.atlassian.confluence.content.service.page.PageLocator;
import com.atlassian.confluence.core.service.ServiceCommand;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.actions.AbstractCommandAction;

public class RemoveHistoricalVersionAction
extends AbstractCommandAction {
    private AbstractPage latestVersion;
    private long pageId;
    private String contentType;
    private PageService pageService;
    private BlogPostService blogPostService;

    @Override
    protected ServiceCommand createCommand() {
        if ("blogpost".equals(this.contentType)) {
            BlogPostLocator locator = this.blogPostService.getIdBlogPostLocator(this.pageId);
            BlogPost historicalVersion = locator.getBlogPost();
            if (historicalVersion != null) {
                this.latestVersion = historicalVersion.getOriginalVersionPage();
            }
            return this.blogPostService.newRemoveBlogPostVersionCommand(locator);
        }
        PageLocator locator = this.pageService.getIdPageLocator(this.pageId);
        Page historicalVersion = locator.getPage();
        if (historicalVersion != null) {
            this.latestVersion = historicalVersion.getOriginalVersionPage();
        }
        return this.pageService.newRemovePageVersionCommand(locator);
    }

    public void setPageId(long pageId) {
        this.pageId = pageId;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public AbstractPage getLatestVersion() {
        return this.latestVersion;
    }

    public void setPageService(PageService pageService) {
        this.pageService = pageService;
    }

    public void setBlogPostService(BlogPostService blogPostService) {
        this.blogPostService = blogPostService;
    }
}

