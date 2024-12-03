/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.service.blogpost;

import com.atlassian.confluence.content.service.blogpost.BlogPostLocator;
import com.atlassian.confluence.content.service.page.RevertContentToVersionCommand;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.service.AbstractServiceCommand;
import com.atlassian.confluence.core.service.ServiceCommandValidator;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.security.PermissionManager;

public class RevertBlogPostCommand
extends AbstractServiceCommand {
    private final PageManager pageManager;
    private final BlogPostLocator blogPostLocator;
    private String revertComment;
    private final int version;
    private final boolean revertTitle;
    private final RevertContentToVersionCommand delegate;
    private BlogPost blogPost;

    public RevertBlogPostCommand(PageManager pageManager, PermissionManager permissionManager, BlogPostLocator blogPostLocator, String revertComment, int version, boolean revertTitle) {
        this.pageManager = pageManager;
        this.blogPostLocator = blogPostLocator;
        this.revertComment = revertComment;
        this.version = version;
        this.revertTitle = revertTitle;
        this.delegate = new RevertContentToVersionCommand(permissionManager, pageManager);
    }

    @Override
    protected void validateInternal(ServiceCommandValidator validator) {
        this.delegate.validate(validator, this.getBlogPost(), this.getPossibleConflict(), this.version, this.revertTitle);
    }

    @Override
    protected boolean isAuthorizedInternal() {
        return this.delegate.isAuthorized(this.getCurrentUser(), this.getBlogPost());
    }

    @Override
    protected void executeInternal() {
        this.delegate.execute(this.getBlogPost(), this.version, this.revertComment, this.revertTitle);
    }

    private String getSpaceKey() {
        return this.getBlogPost().getSpaceKey();
    }

    private BlogPost getBlogPost() {
        if (this.blogPost == null && this.blogPostLocator != null) {
            this.blogPost = this.blogPostLocator.getBlogPost();
        }
        return this.blogPost;
    }

    private BlogPost getPossibleConflict() {
        ContentEntityObject oldVersion = this.delegate.getVersionToRevert(this.getBlogPost(), this.version);
        if (oldVersion != null) {
            return this.pageManager.getBlogPost(this.getSpaceKey(), oldVersion.getTitle(), BlogPost.toCalendar(this.getBlogPost().getCreationDate()));
        }
        return null;
    }
}

