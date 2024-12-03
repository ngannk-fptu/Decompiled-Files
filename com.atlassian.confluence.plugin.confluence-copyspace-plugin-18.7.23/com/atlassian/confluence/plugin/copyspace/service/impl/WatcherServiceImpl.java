/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.watch.WatchService
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.mail.notification.Notification
 *  com.atlassian.confluence.mail.notification.NotificationManager
 *  com.atlassian.confluence.pages.BlogPost
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.search.service.ContentTypeEnum
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugin.copyspace.service.impl;

import com.atlassian.confluence.api.service.watch.WatchService;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.mail.notification.Notification;
import com.atlassian.confluence.mail.notification.NotificationManager;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.plugin.copyspace.service.PermissionService;
import com.atlassian.confluence.plugin.copyspace.service.WatcherService;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class WatcherServiceImpl
implements WatcherService {
    private static final Logger log = LoggerFactory.getLogger(WatcherServiceImpl.class);
    private final WatchService watchService;
    private final NotificationManager notificationManager;
    private final PermissionService permissionService;
    private final SpaceManager spaceManager;

    public WatcherServiceImpl(@ComponentImport WatchService watchService, @ComponentImport NotificationManager notificationManager, PermissionService permissionService, @ComponentImport SpaceManager spaceManager) {
        this.watchService = watchService;
        this.notificationManager = notificationManager;
        this.permissionService = permissionService;
        this.spaceManager = spaceManager;
    }

    @Override
    public void copyPageWatchers(ContentEntityObject origin, Page target) {
        List originalNotifications = this.notificationManager.getNotificationsByContent(origin);
        originalNotifications.forEach(notification -> this.copyWatchersForNewSpaceContent((Notification)notification, (ContentEntityObject)target));
    }

    @Override
    public void copyBlogPostWatchers(BlogPost originalBlogPost, BlogPost blogPostCopy) {
        List originalBlogPostNotifications = this.notificationManager.getNotificationsByContent((ContentEntityObject)originalBlogPost);
        originalBlogPostNotifications.forEach(notification -> this.copyWatchersForNewSpaceContent((Notification)notification, (ContentEntityObject)blogPostCopy));
    }

    @Override
    public void copySpaceWatchers(Space originalSpace, String targetSpaceKey) {
        List spaceNotificationsWithNullType = this.notificationManager.getNotificationsBySpaceAndType(originalSpace, null);
        spaceNotificationsWithNullType.forEach(notification -> this.copyWatchersForNewSpace((Notification)notification, targetSpaceKey));
    }

    @Override
    public void copyWholeBlogWatchers(Space originalSpace, Space targetSpace) {
        List allBlogNotifications = this.notificationManager.getNotificationsBySpaceAndType(originalSpace, ContentTypeEnum.BLOG);
        allBlogNotifications.forEach(notification -> this.copyWatchersForWholeBlog((Notification)notification, targetSpace));
    }

    private void copyWatchersForNewSpace(Notification notification, String targetSpaceKey) {
        log.debug("Copying space watcher {} for space {}", (Object)notification.getReceiver().getName(), (Object)targetSpaceKey);
        boolean hasViewPermission = this.permissionService.canViewSpace((User)notification.getReceiver(), this.spaceManager.getSpace(targetSpaceKey));
        if (hasViewPermission) {
            this.watchService.watchSpace(notification.getReceiver().getKey(), targetSpaceKey);
        }
    }

    private void copyWatchersForNewSpaceContent(Notification notification, ContentEntityObject targetContentEntity) {
        log.debug("Copying content watcher {} for content {}", (Object)notification.getReceiver().getName(), (Object)targetContentEntity.getContentId());
        ConfluenceUser watcher = notification.getReceiver();
        boolean hasViewPermission = this.permissionService.checkIfWatcherHasViewPermission(watcher, notification.getContent().getContentId());
        if (hasViewPermission) {
            this.notificationManager.addContentNotification((User)watcher, targetContentEntity);
        }
    }

    private void copyWatchersForWholeBlog(Notification notification, Space targetSpace) {
        log.debug("Copying whole blog watcher {} for space {}", (Object)notification.getReceiver().getName(), (Object)targetSpace.getKey());
        boolean hasViewPermission = this.permissionService.canViewSpace((User)notification.getReceiver(), targetSpace);
        if (hasViewPermission) {
            this.notificationManager.addSpaceNotification((User)notification.getReceiver(), targetSpace, ContentTypeEnum.BLOG);
        }
    }
}

