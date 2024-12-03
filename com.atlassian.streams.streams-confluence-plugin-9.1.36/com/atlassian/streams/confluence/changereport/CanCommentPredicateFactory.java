/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.user.User
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Predicate
 */
package com.atlassian.streams.confluence.changereport;

import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.user.User;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;

public class CanCommentPredicateFactory {
    private final PermissionManager permissionManager;
    private final UserAccessor userAccessor;

    CanCommentPredicateFactory(PermissionManager permissionManager, UserAccessor userAccessor) {
        this.permissionManager = (PermissionManager)Preconditions.checkNotNull((Object)permissionManager, (Object)"permissionManager");
        this.userAccessor = (UserAccessor)Preconditions.checkNotNull((Object)userAccessor, (Object)"userAccessor");
    }

    Predicate<String> canCommentOn(AbstractPage abstractPage) {
        return username -> this.permissionManager.hasCreatePermission((User)this.userAccessor.getUserByName(username), (Object)abstractPage, Comment.class);
    }

    Predicate<String> canCommentOn(Comment comment) {
        return username -> this.permissionManager.hasCreatePermission((User)this.userAccessor.getUserByName(username), (Object)comment.getContainer(), Comment.class);
    }
}

