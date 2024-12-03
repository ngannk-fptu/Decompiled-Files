/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 */
package com.atlassian.confluence.mail.notification.actions;

import com.atlassian.confluence.mail.notification.Notification;
import com.atlassian.confluence.mail.notification.NotificationManager;
import com.atlassian.confluence.pages.actions.AbstractPageAwareAction;
import com.atlassian.confluence.security.Permission;
import com.atlassian.user.User;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;

public class ManageWatchersAction
extends AbstractPageAwareAction {
    private NotificationManager notificationManager;
    private String username;
    private NotificationType type;

    @Override
    public boolean isPermitted() {
        if (this.username == null) {
            return super.isPermitted();
        }
        return super.isPermitted() && this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.ADMINISTER, this.getSpace());
    }

    @PermittedMethods(value={HttpMethod.GET, HttpMethod.POST})
    public String doRemove() {
        Notification notification;
        Notification notification2 = notification = NotificationType.PAGE.equals((Object)this.getNotificationType()) ? this.notificationManager.getNotificationByUserAndContent(this.getUser(), this.getPage()) : this.notificationManager.getNotificationByUserAndSpace(this.getUser(), this.getSpace());
        if (notification != null) {
            this.notificationManager.removeNotification(notification);
        }
        return "success";
    }

    public String doAdd() {
        if (this.isWatchingSpace()) {
            this.addActionError("manage.watchers.already.watching.space", this.getUser().getFullName());
            return "input";
        }
        if (this.getNotificationType() == NotificationType.PAGE) {
            if (this.isWatchingPage()) {
                this.addActionError("manage.watchers.already.watching.page", this.getUser().getFullName());
                return "input";
            }
            this.notificationManager.addContentNotification(this.getUser(), this.getPage());
            return "success";
        }
        this.notificationManager.addSpaceNotification(this.getUser(), this.getSpace());
        return "success";
    }

    public void setNotificationManager(NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
    }

    public boolean isWatchingSpace() {
        return this.notificationManager.getNotificationByUserAndSpace(this.getUser(), this.getPage().getSpace()) != null;
    }

    public boolean isWatchingPage() {
        return this.notificationManager.isWatchingContent(this.getUser(), this.getPage());
    }

    private User getUser() {
        return this.username != null ? this.userAccessor.getUserByName(this.username) : this.getAuthenticatedUser();
    }

    @Override
    public void validate() {
        if (this.getAuthenticatedUser() == null) {
            this.addActionError(this.getText("no.anonymous.notifications"));
        }
        if (this.getUser() == null) {
            this.addActionError(this.getText("manage.watchers.user.not.found"));
        }
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setType(String type) {
        this.type = NotificationType.valueOf(type.toUpperCase());
    }

    private NotificationType getNotificationType() {
        return this.type;
    }

    private static enum NotificationType {
        PAGE,
        SPACE;

    }
}

