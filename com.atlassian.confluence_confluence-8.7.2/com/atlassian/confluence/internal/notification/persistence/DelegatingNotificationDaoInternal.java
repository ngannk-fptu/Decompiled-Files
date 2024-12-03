/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.internal.notification.persistence;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.internal.notification.persistence.NotificationDaoInternal;
import com.atlassian.confluence.internal.persistence.DelegatingObjectDaoInternal;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.mail.notification.Notification;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.user.User;
import java.util.List;
import org.checkerframework.checker.nullness.qual.NonNull;

public class DelegatingNotificationDaoInternal
extends DelegatingObjectDaoInternal<Notification>
implements NotificationDaoInternal {
    protected final NotificationDaoInternal delegate;

    public DelegatingNotificationDaoInternal(NotificationDaoInternal delegate) {
        super(delegate);
        this.delegate = delegate;
    }

    @Override
    public List<Notification> findNotificationsByUser(User user) {
        return this.delegate.findNotificationsByUser(user);
    }

    @Override
    public List<Notification> findAllNotificationsByUser(User user) {
        return this.delegate.findAllNotificationsByUser(user);
    }

    @Override
    public List<Notification> findAllNotificationsBySpace(Space space) {
        return this.delegate.findAllNotificationsBySpace(space);
    }

    @Override
    public Iterable<Long> findPageAndSpaceNotificationIdsFromSpace(Space space) {
        return this.delegate.findPageAndSpaceNotificationIdsFromSpace(space);
    }

    @Override
    public List<Notification> findNotificationsBySpaceAndType(Space space, ContentTypeEnum type) {
        return this.delegate.findNotificationsBySpaceAndType(space, type);
    }

    @Override
    public List<Notification> findNotificationsBySpacesAndType(List<Space> spaces, ContentTypeEnum type) {
        return this.delegate.findNotificationsBySpacesAndType(spaces, type);
    }

    @Override
    public Notification findNotificationByUserAndSpace(User user, String spaceKey) {
        return this.delegate.findNotificationByUserAndSpace(user, spaceKey);
    }

    @Override
    public Notification findNotificationByUserAndSpace(User user, Space space) {
        return this.delegate.findNotificationByUserAndSpace(user, space);
    }

    @Override
    public Notification findNotificationByUserAndContent(User user, ContentEntityObject content) {
        return this.delegate.findNotificationByUserAndContent(user, content);
    }

    @Override
    public List<Notification> findNotificationsByContent(ContentEntityObject content) {
        return this.delegate.findNotificationsByContent(content);
    }

    @Override
    public List<Notification> findNotificationsByContents(List<ContentEntityObject> contents) {
        return this.delegate.findNotificationsByContents(contents);
    }

    @Override
    public Notification findNotificationByUserAndLabel(User user, Label label) {
        return this.delegate.findNotificationByUserAndLabel(user, label);
    }

    @Override
    public List<Notification> findNotificationsByLabel(Label label) {
        return this.delegate.findNotificationsByLabel(label);
    }

    @Override
    public Notification findNotificationByUserAndSpaceAndType(User user, Space space, ContentTypeEnum type) {
        return this.delegate.findNotificationByUserAndSpaceAndType(user, space, type);
    }

    @Override
    public Notification findDailyReportNotification(String username) {
        return this.delegate.findDailyReportNotification(username);
    }

    @Override
    public List<Notification> findAllDailyReportNotifications() {
        return this.delegate.findAllDailyReportNotifications();
    }

    @Override
    public Notification findGlobalBlogWatchForUser(User user) {
        return this.delegate.findGlobalBlogWatchForUser(user);
    }

    @Override
    public Notification findNetworkNotificationByUser(User user) {
        return this.delegate.findNetworkNotificationByUser(user);
    }

    @Override
    public List<Notification> findSiteBlogNotifications() {
        return this.delegate.findSiteBlogNotifications();
    }

    @Override
    public List<Notification> findNotificationsByFollowing(User user) {
        return this.delegate.findNotificationsByFollowing(user);
    }

    @Override
    public Notification findNotificationById(long id) {
        return this.delegate.findNotificationById(id);
    }

    @Override
    public boolean isWatchingContent(@NonNull ConfluenceUser user, @NonNull ContentEntityObject content) {
        return this.delegate.isWatchingContent(user, content);
    }
}

