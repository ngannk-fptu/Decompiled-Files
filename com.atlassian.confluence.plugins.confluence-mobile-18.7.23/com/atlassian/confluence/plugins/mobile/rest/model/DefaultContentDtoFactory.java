/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.Renderer
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.like.Like
 *  com.atlassian.confluence.like.LikeManager
 *  com.atlassian.confluence.mail.notification.NotificationManager
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.plugins.rest.dto.UserDtoFactory
 *  com.atlassian.confluence.plugins.rest.manager.DateEntityFactory
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.Spaced
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.user.User
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.plugins.mobile.rest.model;

import com.atlassian.confluence.content.render.xhtml.Renderer;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.like.Like;
import com.atlassian.confluence.like.LikeManager;
import com.atlassian.confluence.mail.notification.NotificationManager;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.plugins.mobile.dto.CommentDto;
import com.atlassian.confluence.plugins.mobile.dto.CommentDtoFactory;
import com.atlassian.confluence.plugins.mobile.rest.model.ContentDto;
import com.atlassian.confluence.plugins.mobile.rest.model.ContentDtoFactory;
import com.atlassian.confluence.plugins.mobile.rest.model.LikeDto;
import com.atlassian.confluence.plugins.mobile.rest.model.SpaceDto;
import com.atlassian.confluence.plugins.rest.dto.UserDtoFactory;
import com.atlassian.confluence.plugins.rest.manager.DateEntityFactory;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.Spaced;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.user.User;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Qualifier;

public class DefaultContentDtoFactory
implements ContentDtoFactory {
    private final Renderer mobileViewRenderer;
    private final UserDtoFactory userDtoFactory;
    private final CommentDtoFactory commentDtoFactory;
    private final DateEntityFactory dateEntityFactory;
    private final LikeManager likeManager;
    private final PermissionManager permissionManager;
    private final NotificationManager notificationManager;

    public DefaultContentDtoFactory(@Qualifier(value="mobileViewRenderer") Renderer mobileViewRenderer, UserDtoFactory userDtoFactory, CommentDtoFactory commentDtoFactory, DateEntityFactory dateEntityFactory, LikeManager likeManager, PermissionManager permissionManager, NotificationManager notificationManager) {
        this.mobileViewRenderer = mobileViewRenderer;
        this.userDtoFactory = userDtoFactory;
        this.commentDtoFactory = commentDtoFactory;
        this.dateEntityFactory = dateEntityFactory;
        this.likeManager = likeManager;
        this.permissionManager = permissionManager;
        this.notificationManager = notificationManager;
    }

    @Override
    public ContentDto getContentDto(ContentEntityObject contentEntity) {
        Space space;
        if (contentEntity == null) {
            throw new IllegalArgumentException("contentEntity cannot be null.");
        }
        List<LikeDto> likes = DefaultContentDtoFactory.convertToDtos(this.likeManager.getLikes(contentEntity));
        Map commentLikes = this.likeManager.getLikes((Collection)contentEntity.getComments());
        LinkedList<CommentDto> comments = new LinkedList<CommentDto>();
        for (Comment comment : contentEntity.getComments()) {
            CommentDto commentDto = this.commentDtoFactory.getCommentDto(comment);
            commentDto.setLikes(DefaultContentDtoFactory.convertToDtos((List)commentLikes.get(comment.getId())));
            comments.add(commentDto);
        }
        SpaceDto spaceDto = null;
        if (contentEntity instanceof Spaced && (space = ((Spaced)contentEntity).getSpace()) != null) {
            spaceDto = new SpaceDto();
            spaceDto.setKey(space.getKey());
            if (this.permissionManager.hasCreatePermission((User)AuthenticatedUserThreadLocal.get(), (Object)contentEntity, Comment.class)) {
                spaceDto.setPermissions(Collections.singletonList("comment"));
            }
        }
        String rendered = this.mobileViewRenderer.render(contentEntity);
        ContentDto dto = new ContentDto(contentEntity.getId(), contentEntity.getDisplayTitle(), contentEntity.getType(), rendered, this.userDtoFactory.getUserDto(contentEntity.getCreator()), this.dateEntityFactory.buildDateEntity(contentEntity.getCreationDate()).getFriendly(), likes, comments, spaceDto, this.isWatchingContent(contentEntity));
        return dto;
    }

    private boolean isWatchingContent(ContentEntityObject contentEntity) {
        if (contentEntity instanceof AbstractPage) {
            ConfluenceUser user = AuthenticatedUserThreadLocal.get();
            if (user == null) {
                return false;
            }
            return this.notificationManager.getNotificationByUserAndContent((User)user, (ContentEntityObject)((AbstractPage)contentEntity)) != null;
        }
        return false;
    }

    static List<LikeDto> convertToDtos(List<Like> likes) {
        if (likes == null || likes.isEmpty()) {
            return Collections.emptyList();
        }
        LinkedList<LikeDto> result = new LinkedList<LikeDto>();
        for (Like like : likes) {
            result.add(new LikeDto(like.getUsername()));
        }
        return result;
    }
}

