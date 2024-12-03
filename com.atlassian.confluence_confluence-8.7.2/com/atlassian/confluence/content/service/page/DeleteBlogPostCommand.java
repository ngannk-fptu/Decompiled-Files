/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.service.page;

import com.atlassian.confluence.content.service.blogpost.BlogPostLocator;
import com.atlassian.confluence.core.DefaultDeleteContext;
import com.atlassian.confluence.core.service.AbstractServiceCommand;
import com.atlassian.confluence.core.service.ServiceCommandValidator;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;

public class DeleteBlogPostCommand
extends AbstractServiceCommand {
    private final PageManager pageManager;
    private final PermissionManager permissionManager;
    private final BlogPostLocator blogPostLocator;

    public DeleteBlogPostCommand(PageManager pageManager, PermissionManager permissionManager, BlogPostLocator blogPostLocator) {
        this.pageManager = pageManager;
        this.permissionManager = permissionManager;
        this.blogPostLocator = blogPostLocator;
    }

    @Override
    protected void validateInternal(ServiceCommandValidator validator) {
        if (this.getBlogPost() == null) {
            validator.addValidationError("blog.doesnt.exist", new Object[0]);
        }
    }

    @Override
    protected boolean isAuthorizedInternal() {
        return this.getBlogPost() == null || this.permissionManager.hasPermission(this.getCurrentUser(), Permission.REMOVE, this.getBlogPost());
    }

    @Override
    protected void executeInternal() {
        this.pageManager.trashPage(this.getBlogPost(), DefaultDeleteContext.DEFAULT);
    }

    private BlogPost getBlogPost() {
        return this.blogPostLocator.getBlogPost();
    }
}

