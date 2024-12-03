/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.security.SpacePermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugins.inlinecomments.helper;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugins.inlinecomments.entities.AbstractInlineComment;
import com.atlassian.confluence.plugins.inlinecomments.entities.TopLevelInlineComment;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.user.User;
import java.util.List;

public class InlineCommentPermissionHelper {
    private PermissionManager permissionManager;
    private SpacePermissionManager spacePermissionManager;
    private final PageManager pageManager;

    public InlineCommentPermissionHelper(PermissionManager permissionManager, SpacePermissionManager spacePermissionManager, PageManager pageManager) {
        this.permissionManager = permissionManager;
        this.spacePermissionManager = spacePermissionManager;
        this.pageManager = pageManager;
    }

    private boolean hasCreateCommentPermission(Comment comment) {
        return this.permissionManager.hasCreatePermission((User)AuthenticatedUserThreadLocal.get(), (Object)comment.getContainer(), Comment.class);
    }

    private boolean hasDeleteCommentSpacePermission(Comment comment) {
        if (AuthenticatedUserThreadLocal.isAnonymousUser() && !this.spacePermissionManager.hasPermission("USECONFLUENCE", comment.getSpace(), null)) {
            return false;
        }
        return this.spacePermissionManager.hasPermission("REMOVECOMMENT", comment.getSpace(), (User)AuthenticatedUserThreadLocal.get());
    }

    private boolean hasEditCommentPermission(boolean hasCreateCommentPermission, Comment comment) {
        if (AuthenticatedUserThreadLocal.isAnonymousUser()) {
            return false;
        }
        ConfluenceUser commentOwner = comment.getCreator();
        boolean isLoginUserIsACommentOwner = commentOwner != null && AuthenticatedUserThreadLocal.getUsername().equals(commentOwner.getName());
        return hasCreateCommentPermission && (isLoginUserIsACommentOwner || this.spacePermissionManager.hasPermission("SETSPACEPERMISSIONS", comment.getSpace(), (User)AuthenticatedUserThreadLocal.get()));
    }

    private boolean hasEditCommentPermission(boolean hasCreateCommentPermission, boolean hasSpaceAdminPermission, String commentOwner) {
        if (AuthenticatedUserThreadLocal.isAnonymousUser()) {
            return false;
        }
        return hasCreateCommentPermission && (AuthenticatedUserThreadLocal.getUsername().equals(commentOwner) || hasSpaceAdminPermission);
    }

    private boolean hasAdminSpacePermission(Space space) {
        return this.spacePermissionManager.hasPermission("SETSPACEPERMISSIONS", space, (User)AuthenticatedUserThreadLocal.get());
    }

    private boolean hasDeleteCommentPermission(boolean hasDeleteCommentSpacePermission, String commentOwner) {
        if (AuthenticatedUserThreadLocal.isAnonymousUser()) {
            return hasDeleteCommentSpacePermission;
        }
        return AuthenticatedUserThreadLocal.getUsername().equals(commentOwner) || hasDeleteCommentSpacePermission;
    }

    public boolean hasCreateCommentPermission(long pageId) {
        return this.permissionManager.hasCreatePermission((User)AuthenticatedUserThreadLocal.get(), (Object)this.pageManager.getById(pageId), Comment.class);
    }

    public boolean hasEditPagePermission(long pageId) {
        return this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.EDIT, (Object)this.pageManager.getById(pageId));
    }

    public void setupPermission(List<? extends AbstractInlineComment> inlineComments, Comment firstComment) {
        boolean hasCreatePermission = this.hasCreateCommentPermission(firstComment);
        boolean hasAdminSpacePermission = this.hasAdminSpacePermission(firstComment.getSpace());
        boolean hasSpaceDeletePermission = this.hasDeleteCommentSpacePermission(firstComment);
        for (AbstractInlineComment abstractInlineComment : inlineComments) {
            abstractInlineComment.setHasDeletePermission(this.hasDeleteCommentPermission(hasSpaceDeletePermission, abstractInlineComment.getAuthorUserName()));
            abstractInlineComment.setHasEditPermission(this.hasEditCommentPermission(hasCreatePermission, hasAdminSpacePermission, abstractInlineComment.getAuthorUserName()));
            if (!(abstractInlineComment instanceof TopLevelInlineComment)) continue;
            ((TopLevelInlineComment)abstractInlineComment).setHasReplyPermission(hasCreatePermission);
            ((TopLevelInlineComment)abstractInlineComment).setHasResolvePermission(hasCreatePermission);
        }
    }

    public void setupPermission(AbstractInlineComment inlineComment, Comment comment) {
        boolean hasCreatePermission = this.hasCreateCommentPermission(comment);
        inlineComment.setHasDeletePermission(this.permissionManager.hasPermission((User)comment.getCreator(), Permission.REMOVE, (Object)comment));
        inlineComment.setHasEditPermission(this.hasEditCommentPermission(hasCreatePermission, comment));
        if (inlineComment instanceof TopLevelInlineComment) {
            ((TopLevelInlineComment)inlineComment).setHasReplyPermission(hasCreatePermission);
            ((TopLevelInlineComment)inlineComment).setHasResolvePermission(hasCreatePermission);
        }
    }

    public boolean hasEditCommentPermission(Comment comment) {
        return this.hasEditCommentPermission(this.hasCreateCommentPermission(comment), comment);
    }

    public boolean hasViewCommentPermission(ContentEntityObject entity) {
        return this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, (Object)entity);
    }

    public boolean hasDeleteCommentPermission(Comment comment) {
        boolean hasSpaceDeletePermission = this.hasDeleteCommentSpacePermission(comment);
        return this.hasDeleteCommentPermission(hasSpaceDeletePermission, comment.getCreator() == null ? "" : comment.getCreator().getName());
    }
}

