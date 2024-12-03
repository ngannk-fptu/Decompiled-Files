/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  bucket.core.persistence.ObjectDao
 *  com.atlassian.user.User
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.mail.notification.persistence;

import bucket.core.persistence.ObjectDao;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.mail.notification.Notification;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.user.User;
import java.util.List;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface NotificationDao
extends ObjectDao {
    public List<Notification> findNotificationsByUser(User var1);

    public List<Notification> findAllNotificationsByUser(User var1);

    public List<Notification> findAllNotificationsBySpace(Space var1);

    public Iterable<Long> findPageAndSpaceNotificationIdsFromSpace(Space var1);

    public List<Notification> findNotificationsBySpaceAndType(Space var1, ContentTypeEnum var2);

    public List<Notification> findNotificationsBySpacesAndType(List<Space> var1, ContentTypeEnum var2);

    public Notification findNotificationByUserAndSpace(User var1, String var2);

    public Notification findNotificationByUserAndSpace(User var1, Space var2);

    public Notification findNotificationByUserAndContent(User var1, ContentEntityObject var2);

    public List<Notification> findNotificationsByContent(ContentEntityObject var1);

    public List<Notification> findNotificationsByContents(List<ContentEntityObject> var1);

    public Notification findNotificationByUserAndLabel(User var1, Label var2);

    public List<Notification> findNotificationsByLabel(Label var1);

    public Notification findNotificationByUserAndSpaceAndType(User var1, Space var2, ContentTypeEnum var3);

    public Notification findDailyReportNotification(String var1);

    public List<Notification> findAllDailyReportNotifications();

    public Notification findGlobalBlogWatchForUser(User var1);

    public Notification findNetworkNotificationByUser(User var1);

    public Notification findNotificationById(long var1);

    public List<Notification> findSiteBlogNotifications();

    public List<Notification> findNotificationsByFollowing(User var1);

    public boolean isWatchingContent(@NonNull ConfluenceUser var1, @NonNull ContentEntityObject var2);
}

