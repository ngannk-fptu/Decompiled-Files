/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.service.blogpost;

import com.atlassian.confluence.content.service.blogpost.BlogPostLocator;
import com.atlassian.confluence.content.service.page.RemoveAbstractPageVersionCommand;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.security.PermissionManager;

public class RemoveBlogPostVersionCommand
extends RemoveAbstractPageVersionCommand {
    private final BlogPostLocator blogPostLocator;

    public RemoveBlogPostVersionCommand(PageManager pageManager, PermissionManager permissionManager, BlogPostLocator blogPostLocator) {
        super(pageManager, permissionManager);
        this.blogPostLocator = blogPostLocator;
    }

    @Override
    protected BlogPost getPage() {
        return this.blogPostLocator.getBlogPost();
    }
}

