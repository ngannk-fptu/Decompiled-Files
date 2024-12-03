/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.mail.notification;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.mail.notification.Notification;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.user.User;
import java.util.List;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly=true)
public interface NotificationManager {
    public List<Notification> getNotificationsByUser(User var1);

    public List<Notification> getNotificationsByContent(ContentEntityObject var1);

    public List<Notification> getNotificationsByContents(List<ContentEntityObject> var1);

    public Notification getNotificationByUserAndContent(User var1, ContentEntityObject var2);

    public List<Notification> getNotificationsBySpaceAndType(Space var1, ContentTypeEnum var2);

    public List<Notification> getNotificationsBySpacesAndType(List<Space> var1, ContentTypeEnum var2);

    public Notification getNotificationByUserAndSpace(User var1, String var2);

    public Notification getNotificationByUserAndSpace(User var1, Space var2);

    public Notification getNotificationByUserAndSpaceAndType(User var1, Space var2, ContentTypeEnum var3);

    @Transactional
    public @Nullable Notification addContentNotification(User var1, ContentEntityObject var2);

    @Transactional
    public @Nullable Notification addSpaceNotification(User var1, Space var2);

    @Transactional
    public void removeSpaceNotification(User var1, Space var2);

    @Transactional
    public void removeContentNotification(User var1, ContentEntityObject var2);

    public boolean isWatchingContent(@Nullable User var1, @Nullable ContentEntityObject var2);

    @Transactional
    public boolean addLabelNotification(User var1, Label var2);

    @Transactional
    public void removeLabelNotification(User var1, Label var2);

    public boolean isWatchingLabel(User var1, Label var2);

    public List<Notification> getNotificationsByLabel(Label var1);

    @Transactional
    public @Nullable Notification addSpaceNotification(User var1, Space var2, ContentTypeEnum var3);

    @Deprecated
    @Transactional
    public Notification addDailyReportNotfication(User var1);

    @Transactional
    public Notification addDailyReportNotification(ConfluenceUser var1);

    @Transactional
    public void removeDailyReportNotification(User var1);

    @Transactional
    public void removeNotification(Notification var1);

    public boolean isUserWatchingPageOrSpace(User var1, Space var2, AbstractPage var3);

    @Transactional
    public void removeAllNotificationsForUser(User var1);

    @Transactional
    public void removeAllNotificationsForSpace(Space var1);

    public List<Notification> getDailyReportNotifications();

    public Notification getDailyReportNotificationForUser(User var1);

    public Notification getSiteBlogNotificationForUser(User var1);

    public Notification getNetworkNotificationForUser(User var1);

    public Notification getNotificationById(long var1);

    @Deprecated
    @Transactional
    public void setSiteBlogNotificationForUser(User var1, boolean var2);

    @Transactional
    public void setSiteBlogNotificationForUser(ConfluenceUser var1, boolean var2);

    @Deprecated
    @Transactional
    public void setNetworkNotificationForUser(User var1, boolean var2);

    @Transactional
    public void setNetworkNotificationForUser(ConfluenceUser var1, boolean var2);

    public List<Notification> getSiteBlogNotifications();

    public List<Notification> findNotificationsByFollowing(User var1);

    public Iterable<Long> findPageAndSpaceNotificationIdsFromSpace(Space var1);
}

