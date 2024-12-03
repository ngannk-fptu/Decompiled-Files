/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.content.service.blogpost;

import com.atlassian.confluence.content.service.blogpost.BlogPostLocator;
import com.atlassian.confluence.content.service.space.SpaceLocator;
import com.atlassian.confluence.core.service.AbstractServiceCommand;
import com.atlassian.confluence.core.service.ServiceCommandValidator;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MoveBlogPostToTopOfSpaceCommand
extends AbstractServiceCommand {
    private static final Logger log = LoggerFactory.getLogger(MoveBlogPostToTopOfSpaceCommand.class);
    private final PageManager pageManager;
    private final PermissionManager permissionManager;
    private final BlogPost sourceBlogPost;
    private final Space targetSpace;

    public MoveBlogPostToTopOfSpaceCommand(PageManager pageManager, PermissionManager permissionManager, BlogPostLocator sourceBlogPostLocator, SpaceLocator targetSpaceLocator) {
        this.pageManager = pageManager;
        this.permissionManager = permissionManager;
        this.sourceBlogPost = sourceBlogPostLocator.getBlogPost();
        this.targetSpace = targetSpaceLocator.getSpace();
    }

    public BlogPost getBlogPost() {
        return this.sourceBlogPost;
    }

    @Override
    protected void executeInternal() {
        if (log.isDebugEnabled()) {
            log.debug("move [ " + this.sourceBlogPost + " ] to the top level of space: [ " + this.targetSpace.getKey() + " ]");
        }
        this.pageManager.moveBlogPostToTopLevel(this.sourceBlogPost, this.targetSpace);
    }

    @Override
    protected boolean isAuthorizedInternal() {
        if (this.sourceBlogPost == null || this.targetSpace == null) {
            return false;
        }
        return this.permissionManager.hasMovePermission(this.getCurrentUser(), this.sourceBlogPost, this.targetSpace, null);
    }

    @Override
    protected void validateInternal(ServiceCommandValidator validator) {
        if (this.sourceBlogPost == null) {
            validator.addValidationError("moveblogpost.source.notfound", new Object[0]);
            return;
        }
        if (this.targetSpace == null) {
            validator.addValidationError("moveblogpost.target.space.notfound", new Object[0]);
            return;
        }
        if (this.sourceBlogPost.getSpace() != this.targetSpace) {
            BlogPost existingBlogPost = this.pageManager.getBlogPost(this.targetSpace.getKey(), this.sourceBlogPost.getTitle(), this.sourceBlogPost.getPostingCalendarDate());
            if (existingBlogPost != null) {
                validator.addValidationError("moveblogpost.already.exists.in.target.space", new Object[0]);
            }
        } else {
            validator.addValidationError("moveblogpost.target.space.same.as.origin", new Object[0]);
        }
        if ((this.sourceBlogPost.hasPermissions("View") || this.sourceBlogPost.hasPermissions("Edit")) && !this.permissionManager.hasPermission(this.getCurrentUser(), Permission.SET_PERMISSIONS, this.targetSpace)) {
            validator.addValidationError("save.restrictions.not.permitted", new Object[0]);
        }
    }
}

