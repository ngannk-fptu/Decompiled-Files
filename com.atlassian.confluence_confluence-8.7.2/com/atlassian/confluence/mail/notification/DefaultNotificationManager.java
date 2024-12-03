/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.user.User
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.mail.notification;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.event.events.content.mail.notification.ContentNotificationAddedEvent;
import com.atlassian.confluence.event.events.content.mail.notification.ContentNotificationRemovedEvent;
import com.atlassian.confluence.event.events.content.mail.notification.NotificationEvent;
import com.atlassian.confluence.event.events.content.mail.notification.SiteNotificationAddedEvent;
import com.atlassian.confluence.event.events.content.mail.notification.SiteNotificationRemovedEvent;
import com.atlassian.confluence.event.events.content.mail.notification.SpaceNotificationAddedEvent;
import com.atlassian.confluence.event.events.content.mail.notification.SpaceNotificationRemovedEvent;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.mail.notification.Notification;
import com.atlassian.confluence.mail.notification.NotificationManager;
import com.atlassian.confluence.mail.notification.persistence.NotificationDao;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.persistence.dao.compatibility.FindUserHelper;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.user.User;
import java.util.List;
import org.checkerframework.checker.nullness.qual.Nullable;

public class DefaultNotificationManager
implements NotificationManager {
    private final NotificationDao notificationDao;
    private final EventPublisher eventPublisher;

    public DefaultNotificationManager(NotificationDao notificationDao, EventPublisher eventPublisher) {
        this.notificationDao = notificationDao;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public List<Notification> getNotificationsByUser(User user) {
        return this.notificationDao.findNotificationsByUser(user);
    }

    @Override
    public Notification getNotificationByUserAndSpace(User user, String spaceKey) {
        return this.notificationDao.findNotificationByUserAndSpace(user, spaceKey);
    }

    @Override
    public Notification getNotificationByUserAndSpace(User user, Space space) {
        return this.notificationDao.findNotificationByUserAndSpaceAndType(user, space, null);
    }

    @Override
    public Notification getNotificationByUserAndSpaceAndType(User user, Space space, ContentTypeEnum type) {
        if (type == ContentTypeEnum.PAGE) {
            return null;
        }
        return this.notificationDao.findNotificationByUserAndSpaceAndType(user, space, type);
    }

    @Override
    public @Nullable Notification addSpaceNotification(User user, Space space) {
        return this.addNotification(user, space, null);
    }

    @Override
    public void removeSpaceNotification(User user, Space space) {
        Notification notification = this.getNotificationByUserAndSpace(user, space);
        if (notification != null) {
            this.notificationDao.remove(notification);
        }
    }

    @Override
    public @Nullable Notification addContentNotification(User user, ContentEntityObject content) {
        if (user == null || content == null) {
            return null;
        }
        if (this.isWatchingContent(user, content)) {
            return this.getNotificationByUserAndContent(user, content);
        }
        Notification notification = new Notification();
        notification.setReceiver(FindUserHelper.getUser(user));
        notification.setType(content.getTypeEnum());
        notification.setContent(content);
        this.notificationDao.save(notification);
        this.eventPublisher.publish((Object)this.makeNotificationAddedEvent(notification));
        return notification;
    }

    @Override
    public void removeContentNotification(User user, ContentEntityObject content) {
        Notification notification = this.notificationDao.findNotificationByUserAndContent(user, content);
        if (notification != null) {
            this.removeNotification(notification);
        }
    }

    @Override
    public List<Notification> getNotificationsByContent(ContentEntityObject content) {
        return this.notificationDao.findNotificationsByContent(content);
    }

    @Override
    public List<Notification> getNotificationsByContents(List<ContentEntityObject> contents) {
        return this.notificationDao.findNotificationsByContents(contents);
    }

    @Override
    public Notification getNotificationByUserAndContent(User user, ContentEntityObject content) {
        return this.notificationDao.findNotificationByUserAndContent(user, content);
    }

    @Override
    public boolean isWatchingContent(@Nullable User user, @Nullable ContentEntityObject content) {
        ConfluenceUser confluenceUser = FindUserHelper.getUser(user);
        return confluenceUser != null && content != null && this.notificationDao.isWatchingContent(confluenceUser, content);
    }

    @Override
    public boolean addLabelNotification(User user, Label label) {
        if (user == null || label == null) {
            return false;
        }
        if (this.isWatchingLabel(user, label)) {
            return false;
        }
        Notification notification = new Notification();
        notification.setReceiver(FindUserHelper.getUser(user));
        notification.setLabel(label);
        this.notificationDao.save(notification);
        this.eventPublisher.publish((Object)this.makeNotificationAddedEvent(notification));
        return true;
    }

    @Override
    public void removeLabelNotification(User user, Label label) {
        Notification notification = this.notificationDao.findNotificationByUserAndLabel(user, label);
        if (notification != null) {
            this.notificationDao.remove(notification);
        }
    }

    @Override
    public boolean isWatchingLabel(User user, Label label) {
        return this.notificationDao.findNotificationByUserAndLabel(user, label) != null;
    }

    @Override
    public List<Notification> getNotificationsByLabel(Label label) {
        return this.notificationDao.findNotificationsByLabel(label);
    }

    @Override
    public @Nullable Notification addSpaceNotification(User user, Space space, ContentTypeEnum type) {
        if (type != null && type != ContentTypeEnum.BLOG) {
            throw new IllegalArgumentException("Only blogpost can be passed as a space notification content type");
        }
        return this.addNotification(user, space, type);
    }

    private @Nullable Notification addNotification(User user, Space space, ContentTypeEnum type) {
        if (space == null) {
            return null;
        }
        Notification notification = this.notificationDao.findNotificationByUserAndSpace(user, space);
        if (notification != null && notification.getType() != type) {
            this.removeNotification(notification);
            notification = null;
        }
        if (notification == null) {
            notification = new Notification();
            notification.setReceiver(FindUserHelper.getUser(user));
            notification.setSpace(space);
            notification.setContent(null);
            notification.setType(type);
            this.notificationDao.save(notification);
            this.eventPublisher.publish((Object)this.makeNotificationAddedEvent(notification));
            return notification;
        }
        return notification;
    }

    private NotificationEvent makeNotificationAddedEvent(Notification notification) {
        if (notification.isPageNotification()) {
            return new ContentNotificationAddedEvent(this, notification);
        }
        if (notification.isSpaceNotification()) {
            return new SpaceNotificationAddedEvent(this, notification);
        }
        return new SiteNotificationAddedEvent(this, notification);
    }

    private NotificationEvent makeNotificationRemovedEvent(Notification notification) {
        if (notification.isPageNotification()) {
            return new ContentNotificationRemovedEvent(this, notification);
        }
        if (notification.isSpaceNotification()) {
            return new SpaceNotificationRemovedEvent(this, notification);
        }
        return new SiteNotificationRemovedEvent(this, notification);
    }

    @Override
    public void removeNotification(Notification notification) {
        this.notificationDao.remove(notification);
        this.eventPublisher.publish((Object)this.makeNotificationRemovedEvent(notification));
    }

    @Override
    public boolean isUserWatchingPageOrSpace(User user, Space space, AbstractPage page) {
        if (page != null && this.notificationDao.findNotificationByUserAndContent(user, page) != null) {
            return true;
        }
        if (space != null) {
            Notification spaceNotif = this.notificationDao.findNotificationByUserAndSpaceAndType(user, space, null);
            if (spaceNotif == null && page != null) {
                spaceNotif = this.notificationDao.findNotificationByUserAndSpaceAndType(user, space, page.getTypeEnum());
            }
            return spaceNotif != null;
        }
        return false;
    }

    @Override
    public List<Notification> getNotificationsBySpaceAndType(Space space, ContentTypeEnum type) {
        return this.notificationDao.findNotificationsBySpaceAndType(space, type);
    }

    @Override
    public List<Notification> getNotificationsBySpacesAndType(List<Space> spaces, ContentTypeEnum type) {
        return this.notificationDao.findNotificationsBySpacesAndType(spaces, type);
    }

    @Override
    public void removeAllNotificationsForUser(User user) {
        List<Notification> notifications = this.notificationDao.findAllNotificationsByUser(user);
        for (Notification notification : notifications) {
            this.removeNotification(notification);
        }
    }

    @Override
    public void removeAllNotificationsForSpace(Space space) {
        List<Notification> notifications = this.notificationDao.findAllNotificationsBySpace(space);
        for (Notification notification : notifications) {
            this.removeNotification(notification);
        }
    }

    @Override
    public List<Notification> getDailyReportNotifications() {
        return this.notificationDao.findAllDailyReportNotifications();
    }

    @Override
    public Notification getDailyReportNotificationForUser(User user) {
        return this.notificationDao.findDailyReportNotification(user.getName());
    }

    @Override
    public List<Notification> getSiteBlogNotifications() {
        return this.notificationDao.findSiteBlogNotifications();
    }

    @Override
    public List<Notification> findNotificationsByFollowing(User modifier) {
        return this.notificationDao.findNotificationsByFollowing(modifier);
    }

    @Override
    public Iterable<Long> findPageAndSpaceNotificationIdsFromSpace(Space space) {
        return this.notificationDao.findPageAndSpaceNotificationIdsFromSpace(space);
    }

    @Override
    public Notification getSiteBlogNotificationForUser(User user) {
        return this.notificationDao.findGlobalBlogWatchForUser(user);
    }

    @Override
    public Notification getNetworkNotificationForUser(User user) {
        return this.notificationDao.findNetworkNotificationByUser(user);
    }

    @Override
    public Notification getNotificationById(long id) {
        return this.notificationDao.findNotificationById(id);
    }

    @Override
    public void setSiteBlogNotificationForUser(User user, boolean globalBlogWatchForUser) {
        this.setSiteBlogNotificationForUser((ConfluenceUser)user, globalBlogWatchForUser);
    }

    @Override
    public void setSiteBlogNotificationForUser(ConfluenceUser user, boolean globalBlogWatchForUser) {
        Notification notification = this.notificationDao.findGlobalBlogWatchForUser(user);
        if (notification == null && globalBlogWatchForUser) {
            notification = new Notification();
            notification.setReceiver(user);
            notification.setType(ContentTypeEnum.BLOG);
            this.notificationDao.save(notification);
            this.eventPublisher.publish((Object)this.makeNotificationAddedEvent(notification));
        } else if (notification != null && !globalBlogWatchForUser) {
            this.notificationDao.remove(notification);
            this.eventPublisher.publish((Object)this.makeNotificationRemovedEvent(notification));
        }
    }

    @Override
    public void setNetworkNotificationForUser(User user, boolean watchingNetwork) {
        this.setNetworkNotificationForUser((ConfluenceUser)user, watchingNetwork);
    }

    @Override
    public void setNetworkNotificationForUser(ConfluenceUser user, boolean watchingNetwork) {
        Notification notification = this.notificationDao.findNetworkNotificationByUser(user);
        if (notification == null && watchingNetwork) {
            notification = new Notification();
            notification.setReceiver(user);
            notification.setNetwork(true);
            this.notificationDao.save(notification);
            this.eventPublisher.publish((Object)this.makeNotificationAddedEvent(notification));
        } else if (notification != null && !watchingNetwork) {
            this.notificationDao.remove(notification);
            this.eventPublisher.publish((Object)this.makeNotificationRemovedEvent(notification));
        }
    }

    @Override
    public Notification addDailyReportNotfication(User user) {
        return this.addDailyReportNotification((ConfluenceUser)user);
    }

    @Override
    public Notification addDailyReportNotification(ConfluenceUser user) {
        Notification notification = this.notificationDao.findDailyReportNotification(user.getName());
        if (notification == null) {
            notification = new Notification();
            notification.setReceiver(user);
            notification.setDigest(true);
            this.notificationDao.save(notification);
            this.eventPublisher.publish((Object)this.makeNotificationAddedEvent(notification));
        }
        return notification;
    }

    @Override
    public void removeDailyReportNotification(User user) {
        Notification notification = this.notificationDao.findDailyReportNotification(user.getName());
        if (notification != null) {
            this.notificationDao.remove(notification);
            this.eventPublisher.publish((Object)this.makeNotificationRemovedEvent(notification));
        }
    }
}

