/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.SimplePageRequest
 *  com.atlassian.confluence.api.model.people.User
 *  com.atlassian.confluence.api.service.network.NetworkService
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.event.events.like.LikeEvent
 *  com.atlassian.confluence.like.Like
 *  com.atlassian.confluence.like.LikeManager
 *  com.atlassian.confluence.mail.notification.Notification$WatchType
 *  com.atlassian.confluence.mail.notification.NotificationManager
 *  com.atlassian.confluence.notifications.ConfluenceUserRole
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.plugin.notifications.spi.UserRole
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.User
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.like.notifications;

import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.SimplePageRequest;
import com.atlassian.confluence.api.model.people.User;
import com.atlassian.confluence.api.service.network.NetworkService;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.event.events.like.LikeEvent;
import com.atlassian.confluence.like.Like;
import com.atlassian.confluence.like.LikeManager;
import com.atlassian.confluence.mail.notification.Notification;
import com.atlassian.confluence.mail.notification.NotificationManager;
import com.atlassian.confluence.notifications.ConfluenceUserRole;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.plugins.like.LikeNotificationPreferences;
import com.atlassian.confluence.plugins.like.notifications.LikeNotification;
import com.atlassian.confluence.plugins.like.notifications.LikeNotificationManager;
import com.atlassian.confluence.plugins.like.notifications.LikePayload;
import com.atlassian.confluence.plugins.like.notifications.dao.NotificationDao;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.plugin.notifications.spi.UserRole;
import com.atlassian.sal.api.user.UserKey;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultLikeNotificationManager
implements LikeNotificationManager {
    private static final Logger logger = LoggerFactory.getLogger(DefaultLikeNotificationManager.class);
    private final UserAccessor userAccessor;
    private final NotificationManager notificationManager;
    private final LikeManager likeManager;
    private final NetworkService networkService;
    private final NotificationDao notificationDao;
    private final ContentEntityManager entityManager;

    public DefaultLikeNotificationManager(UserAccessor userAccessor, NotificationManager notificationManager, LikeManager likeManager, NetworkService networkService, NotificationDao notificationDao, ContentEntityManager entityManager) {
        this.userAccessor = userAccessor;
        this.notificationManager = notificationManager;
        this.likeManager = likeManager;
        this.networkService = networkService;
        this.notificationDao = notificationDao;
        this.entityManager = entityManager;
    }

    @Override
    public List<LikeNotification> getNotifications(LikeEvent event) {
        ContentEntityObject contentEntity = event.getContent();
        com.atlassian.user.User liker = event.getOriginatingUser();
        return this.notificationsForContentAndUser(contentEntity, liker);
    }

    private List<LikeNotification> notificationsForContentAndUser(ContentEntityObject contentEntity, com.atlassian.user.User liker) {
        ConfluenceUser confluenceLiker;
        ConfluenceUser author = contentEntity.getCreator();
        if (author == null) {
            return Collections.emptyList();
        }
        Set existingLikers = this.likeManager.getLikes(contentEntity).stream().map(Like::getUsername).collect(Collectors.toSet());
        HashSet existingCommenters = new HashSet();
        if (contentEntity instanceof Comment) {
            existingCommenters.addAll(((Comment)contentEntity).getChildren().stream().filter(comment -> comment.getCreator() != null).map(comment -> comment.getCreator().getName()).collect(Collectors.toList()));
        } else {
            existingCommenters.addAll(contentEntity.getComments().stream().filter(comment -> comment.getCreator() != null).map(comment -> comment.getCreator().getName()).collect(Collectors.toList()));
        }
        LinkedList<LikeNotification> notifications = new LinkedList<LikeNotification>();
        if (this.getLikeNotificationPreference(author).isNotifyAuthor()) {
            notifications.add(new LikeNotification(author, liker, (com.atlassian.user.User)author, contentEntity, (UserRole)new ConfluenceUserRole(Notification.WatchType.SINGLE_PAGE.name())));
        }
        if (liker instanceof ConfluenceUser) {
            confluenceLiker = (ConfluenceUser)liker;
        } else {
            confluenceLiker = this.userAccessor.getUserByName(liker.getName());
            Objects.requireNonNull(confluenceLiker);
        }
        SimplePageRequest pageReq = new SimplePageRequest(0, 0x7FFFFFFE);
        PageResponse followers = this.networkService.getFollowers(confluenceLiker.getKey(), (PageRequest)pageReq);
        for (User follower : followers) {
            LikeNotification networkLikeNotification;
            ConfluenceUser confluenceFollower;
            if (!follower.optionalUserKey().isPresent() || (confluenceFollower = this.userAccessor.getUserByKey((UserKey)follower.optionalUserKey().get())) == null || notifications.contains(networkLikeNotification = new LikeNotification(confluenceFollower, liker, (com.atlassian.user.User)author, contentEntity, (UserRole)new ConfluenceUserRole(Notification.WatchType.NETWORK.name()))) || this.notificationManager.getNetworkNotificationForUser((com.atlassian.user.User)confluenceFollower) == null || existingLikers.contains(confluenceFollower.getName()) || existingCommenters.contains(confluenceFollower.getName()) || this.notificationDao.exists(networkLikeNotification)) continue;
            notifications.add(networkLikeNotification);
            this.notificationDao.save(networkLikeNotification);
        }
        return notifications;
    }

    @Override
    public List<LikeNotification> getNotifications(LikePayload event) {
        Optional originator = event.getOriginatorUserKey();
        if (!originator.isPresent()) {
            logger.info("Missing like event originating user key");
            return Collections.emptyList();
        }
        ContentEntityObject content = this.entityManager.getById(event.getContentId());
        if (content == null || !content.getType().equals(event.getContentType().getType())) {
            throw new IllegalArgumentException(String.format("Content type for payload does not match content type corresponding to payload id - %s : %d", event.getContentType().getType(), event.getContentId()));
        }
        UserKey userKey = (UserKey)originator.get();
        ConfluenceUser liker = this.userAccessor.getUserByKey(userKey);
        if (liker == null) {
            logger.info("Cannot determine recipients of like email as user '{}' does not exist on the server,it is possible they may have been removed between like event and notificationprocessing.", (Object)userKey);
            return Collections.emptyList();
        }
        return this.notificationsForContentAndUser(content, (com.atlassian.user.User)liker);
    }

    private LikeNotificationPreferences getLikeNotificationPreference(ConfluenceUser user) {
        return new LikeNotificationPreferences(this.userAccessor.getPropertySet(user));
    }
}

