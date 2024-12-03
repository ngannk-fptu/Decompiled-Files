/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  com.atlassian.confluence.mail.notification.NotificationManager
 *  com.atlassian.user.User
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.confluence.plugins.like.actions;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.mail.notification.NotificationManager;
import com.atlassian.confluence.plugins.like.LikeNotificationPreferences;
import com.atlassian.user.User;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import org.springframework.beans.factory.InitializingBean;

public class NotificationsAction
extends ConfluenceActionSupport
implements InitializingBean {
    private NotificationManager notificationManager;
    private LikeNotificationPreferences likeNotificationPreferences;
    private boolean notifyAuthor = false;
    private boolean notifyFollowers = false;

    public void afterPropertiesSet() throws Exception {
        this.likeNotificationPreferences = new LikeNotificationPreferences(this.userAccessor.getPropertySet(this.getAuthenticatedUser()));
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String doView() {
        return "success";
    }

    public boolean isNotifyAuthor() {
        return this.likeNotificationPreferences.isNotifyAuthor();
    }

    public boolean isNotifyFollowers() {
        return this.notificationManager.getNetworkNotificationForUser((User)this.getAuthenticatedUser()) != null;
    }

    public void setNotifyAuthor(boolean notifyAuthor) {
        this.notifyAuthor = notifyAuthor;
    }

    public void setNotifyFollowers(boolean notifyFollowers) {
        this.notifyFollowers = notifyFollowers;
    }

    public String doUpdate() throws Exception {
        this.likeNotificationPreferences.setNotifyAuthor(this.notifyAuthor);
        this.notificationManager.setNetworkNotificationForUser(this.getAuthenticatedUser(), this.notifyFollowers);
        return "success";
    }

    public void setNotificationManager(NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
    }

    public boolean isPermitted() {
        return this.getAuthenticatedUser() != null && super.isPermitted();
    }
}

