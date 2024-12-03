/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.pages.CommentManager
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.user.UserProfile
 *  com.atlassian.streams.api.StreamsException
 *  com.atlassian.streams.api.common.Either
 *  com.atlassian.streams.api.common.Preconditions
 *  com.atlassian.streams.spi.StreamsCommentHandler
 *  com.atlassian.streams.spi.StreamsCommentHandler$PostReplyError
 *  com.atlassian.streams.spi.StreamsCommentHandler$PostReplyError$Type
 *  com.atlassian.user.User
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Iterables
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.streams.confluence;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.CommentManager;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.streams.api.StreamsException;
import com.atlassian.streams.api.common.Either;
import com.atlassian.streams.spi.StreamsCommentHandler;
import com.atlassian.user.User;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import java.net.URI;
import org.springframework.beans.factory.annotation.Qualifier;

public class ConfluenceStreamsCommentHandler
implements StreamsCommentHandler {
    private ApplicationProperties applicationProperties;
    private PageManager pageManager;
    private CommentManager commentManager;
    private final UserManager userManager;
    private final PermissionManager permissionManager;
    private final UserAccessor userAccessor;

    public ConfluenceStreamsCommentHandler(ApplicationProperties applicationProperties, @Qualifier(value="pageManager") PageManager pageManager, @Qualifier(value="commentManager") CommentManager commentManager, UserManager salUserManager, PermissionManager permissionManager, UserAccessor userAccessor) {
        this.applicationProperties = (ApplicationProperties)Preconditions.checkNotNull((Object)applicationProperties, (Object)"Application Properties");
        this.pageManager = (PageManager)Preconditions.checkNotNull((Object)pageManager, (Object)"Page Manager");
        this.commentManager = (CommentManager)Preconditions.checkNotNull((Object)commentManager, (Object)"Comment Manager");
        this.userManager = (UserManager)Preconditions.checkNotNull((Object)salUserManager, (Object)"salUserManager");
        this.permissionManager = (PermissionManager)Preconditions.checkNotNull((Object)permissionManager, (Object)"permissionManager");
        this.userAccessor = (UserAccessor)Preconditions.checkNotNull((Object)userAccessor, (Object)"userAccessor");
    }

    public Either<StreamsCommentHandler.PostReplyError, URI> postReply(URI baseUri, Iterable<String> itemPath, String comment) throws StreamsException {
        try {
            AbstractPage page;
            Preconditions.checkArgument((Iterables.size(itemPath) == 2 ? 1 : 0) != 0, (Object)"Item path must contain exactly 2 parts.");
            String type = com.atlassian.streams.api.common.Preconditions.checkNotBlank((String)((String)Iterables.get(itemPath, (int)0)), (String)"Type");
            long id = Long.parseLong((String)Iterables.get(itemPath, (int)1));
            Comment parentComment = null;
            if (type.equals("comment")) {
                parentComment = this.commentManager.getComment(id);
                if (parentComment == null) {
                    return Either.left((Object)new StreamsCommentHandler.PostReplyError(StreamsCommentHandler.PostReplyError.Type.DELETED_OR_PERMISSION_DENIED));
                }
                page = parentComment.getContainer();
            } else {
                page = this.pageManager.getAbstractPage(id);
            }
            UserProfile remoteUser = this.userManager.getRemoteUser();
            ConfluenceUser user = this.userAccessor.getUserByName(remoteUser != null ? remoteUser.getUsername() : null);
            if (!this.permissionManager.hasCreatePermission((User)user, (Object)page, Comment.class)) {
                return Either.left((Object)new StreamsCommentHandler.PostReplyError(StreamsCommentHandler.PostReplyError.Type.UNAUTHORIZED));
            }
            Comment newComment = this.commentManager.addCommentToObject((ContentEntityObject)page, parentComment, comment);
            return Either.right((Object)URI.create(this.applicationProperties.getBaseUrl(UrlMode.CANONICAL) + newComment.getUrlPath()));
        }
        catch (Exception ex) {
            return Either.left((Object)new StreamsCommentHandler.PostReplyError(StreamsCommentHandler.PostReplyError.Type.UNKNOWN_ERROR, (Throwable)ex));
        }
    }

    public Either<StreamsCommentHandler.PostReplyError, URI> postReply(Iterable<String> itemPath, String comment) {
        return this.postReply(URI.create(this.applicationProperties.getBaseUrl()), itemPath, comment);
    }
}

