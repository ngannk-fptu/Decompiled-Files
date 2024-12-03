/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.service.blogpost;

import com.atlassian.confluence.content.service.blogpost.BlogPostLocator;
import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.core.service.AbstractSingleEntityLocator;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.PageManager;

public class IdBlogPostLocator
extends AbstractSingleEntityLocator
implements BlogPostLocator {
    private final PageManager pageManager;
    private final long blogPostId;

    public IdBlogPostLocator(PageManager pageManager, long blogPostId) {
        this.pageManager = pageManager;
        this.blogPostId = blogPostId;
    }

    @Override
    public BlogPost getBlogPost() {
        return this.pageManager.getBlogPost(this.blogPostId);
    }

    @Override
    public ConfluenceEntityObject getEntity() {
        return this.getBlogPost();
    }
}

