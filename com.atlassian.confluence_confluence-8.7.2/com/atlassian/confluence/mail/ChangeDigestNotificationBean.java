/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.mail;

import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.mail.notification.Notification;
import com.atlassian.confluence.mail.notification.NotificationManager;
import com.atlassian.confluence.mail.reports.ChangeDigestReport;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.PersonalInformation;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.user.User;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChangeDigestNotificationBean {
    private static final Logger log = LoggerFactory.getLogger(ChangeDigestNotificationBean.class);
    private final ContentEntityManager contentEntityManager;
    private final UserAccessor userAccessor;
    private final PermissionManager permissionManager;
    private final NotificationManager notificationManager;

    public ChangeDigestNotificationBean(ContentEntityManager contentEntityManager, UserAccessor userAccessor, PermissionManager permissionManager, NotificationManager notificationManager) {
        this.contentEntityManager = contentEntityManager;
        this.userAccessor = userAccessor;
        this.permissionManager = permissionManager;
        this.notificationManager = notificationManager;
    }

    public ChangeDigestReport getWatchedChangeReportForUser(User user, List<? extends AbstractPage> pages) {
        ChangeDigestReport report = new ChangeDigestReport(user, this.userAccessor);
        for (AbstractPage abstractPage : pages) {
            if (!this.notificationManager.isUserWatchingPageOrSpace(user, abstractPage.getSpace(), abstractPage)) continue;
            report.addPage((Page)abstractPage);
        }
        return report;
    }

    public ChangeDigestReport getAllChangeReportForUser(User user, List<? extends ContentEntityObject> changes) {
        ChangeDigestReport report = new ChangeDigestReport(user, this.userAccessor);
        for (ContentEntityObject contentEntityObject : changes) {
            if (!this.userHasAccessTo(user, contentEntityObject)) continue;
            if (contentEntityObject.getType().equals("page")) {
                report.addPage((Page)contentEntityObject);
                continue;
            }
            if (contentEntityObject.getType().equals("blogpost")) {
                report.addBlogPost((BlogPost)contentEntityObject);
                continue;
            }
            if (contentEntityObject.getType().equals("comment")) {
                report.addComment((Comment)contentEntityObject);
                continue;
            }
            if (contentEntityObject.getType().equals("userinfo")) {
                report.addPersonalInformation((PersonalInformation)contentEntityObject);
                continue;
            }
            log.debug("Found content type that is not supported in a daily change digest: " + contentEntityObject.getType());
        }
        return report;
    }

    private List<ContentEntityObject> getContentCreatedOrUpdatedSinceDate(Date fromWhen) {
        List fullContentList = this.contentEntityManager.getRecentlyModifiedForChangeDigest(fromWhen);
        LinkedList<ContentEntityObject> contentList = new LinkedList<ContentEntityObject>(fullContentList);
        Iterator iterator = contentList.iterator();
        while (iterator.hasNext()) {
            SpaceContentEntityObject spaceContentEntityObject;
            Space entitySpace;
            ContentEntityObject contentEntityObject = (ContentEntityObject)iterator.next();
            if (contentEntityObject instanceof Comment) {
                contentEntityObject = ((Comment)contentEntityObject).getContainer();
            }
            if (!(contentEntityObject instanceof SpaceContentEntityObject) || (entitySpace = (spaceContentEntityObject = (SpaceContentEntityObject)contentEntityObject).getSpace()) == null || !entitySpace.isPersonal()) continue;
            iterator.remove();
        }
        return contentList;
    }

    public List<ChangeDigestReport> getAllChangeReports(Date fromWhen) {
        ArrayList<ChangeDigestReport> reports = new ArrayList<ChangeDigestReport>();
        List<ContentEntityObject> updatedContent = this.getContentCreatedOrUpdatedSinceDate(fromWhen);
        if (updatedContent.size() == 0) {
            return Collections.emptyList();
        }
        List<Notification> notifications = this.notificationManager.getDailyReportNotifications();
        for (Notification notification : notifications) {
            ChangeDigestReport changeReport;
            ConfluenceUser receiver = notification.getReceiver();
            String receiverName = receiver != null ? receiver.getName() : null;
            ConfluenceUser user = this.userAccessor.getUserByName(receiverName);
            if (user == null) {
                log.debug("User not found for notification " + notification);
                continue;
            }
            if (!this.permissionManager.hasPermission((User)user, Permission.VIEW, PermissionManager.TARGET_APPLICATION) || !(changeReport = this.getAllChangeReportForUser(user, updatedContent)).hasChanges()) continue;
            reports.add(changeReport);
        }
        return reports;
    }

    private boolean userHasAccessTo(User user, ContentEntityObject entity) {
        return this.permissionManager.hasPermissionNoExemptions(user, Permission.VIEW, entity);
    }
}

