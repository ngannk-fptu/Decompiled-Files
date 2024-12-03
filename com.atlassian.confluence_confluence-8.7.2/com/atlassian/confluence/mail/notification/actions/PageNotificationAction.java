/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 */
package com.atlassian.confluence.mail.notification.actions;

import com.atlassian.confluence.core.Beanable;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.mail.notification.Notification;
import com.atlassian.confluence.mail.notification.NotificationManager;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.user.User;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.util.HashMap;
import java.util.Map;

public class PageNotificationAction
extends ConfluenceActionSupport
implements Beanable {
    private Map<String, Object> bean = new HashMap<String, Object>();
    private PageManager pageManager;
    private NotificationManager notificationManager;
    private long entityId;

    @Override
    public Object getBean() {
        return this.bean;
    }

    public String startWatching() {
        AbstractPage entity = this.pageManager.getAbstractPage(this.entityId);
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        if (!this.permissionManager.hasPermission((User)user, Permission.VIEW, entity)) {
            this.addActionError(this.getText("not.permitted.description"));
            return "error";
        }
        this.notificationManager.addContentNotification(user, entity);
        return "success";
    }

    @PermittedMethods(value={HttpMethod.GET, HttpMethod.POST})
    public String stopWatching() {
        AbstractPage entity = this.pageManager.getAbstractPage(this.entityId);
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        Notification notif = this.notificationManager.getNotificationByUserAndContent(user, entity);
        if (notif != null) {
            this.notificationManager.removeNotification(notif);
        }
        return "success";
    }

    public long getEntityId() {
        return this.entityId;
    }

    public void setEntityId(long entityId) {
        this.entityId = entityId;
    }

    public void setPageManager(PageManager pageManager) {
        this.pageManager = pageManager;
    }

    public void setNotificationManager(NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
    }
}

