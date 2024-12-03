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
import com.atlassian.confluence.pages.actions.PageAware;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.ConfluenceUserImpl;
import com.atlassian.confluence.user.UnknownUser;
import com.atlassian.user.User;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ListWatchersAction
extends ConfluenceActionSupport
implements PageAware,
Beanable {
    private AbstractPage page;
    private NotificationManager notificationManager;
    private List<User> pageWatchers = new ArrayList<User>();
    private List<User> spaceWatchers = new ArrayList<User>();

    @Override
    public boolean isPermitted() {
        return super.isPermitted() && this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.VIEW, this.page) && this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.ADMINISTER, this.page.getSpace());
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() throws Exception {
        for (Notification notification : this.notificationManager.getNotificationsByContent(this.page)) {
            if (this.isUserUnknown(notification.getReceiver())) continue;
            this.pageWatchers.add(notification.getReceiver());
        }
        for (Notification notification : this.notificationManager.getNotificationsBySpaceAndType(this.page.getSpace(), null)) {
            if (this.isUserUnknown(notification.getReceiver())) continue;
            this.spaceWatchers.add(notification.getReceiver());
        }
        return "success";
    }

    @Override
    public Object getBean() {
        HashMap<String, List<User>> result = new HashMap<String, List<User>>(2);
        result.put("pageWatchers", this.pageWatchers);
        result.put("spaceWatchers", this.spaceWatchers);
        return result;
    }

    public Space getSpace() {
        return this.getPage() == null ? null : this.getPage().getSpace();
    }

    @Override
    public AbstractPage getPage() {
        return this.page;
    }

    @Override
    public void setPage(AbstractPage page) {
        this.page = page;
    }

    @Override
    public boolean isPageRequired() {
        return true;
    }

    @Override
    public boolean isLatestVersionRequired() {
        return false;
    }

    @Override
    public boolean isViewPermissionRequired() {
        return true;
    }

    public void setNotificationManager(NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
    }

    private boolean isUserUnknown(ConfluenceUser user) {
        if (!(user instanceof ConfluenceUserImpl)) {
            return false;
        }
        User backingUser = ((ConfluenceUserImpl)user).getBackingUser();
        return backingUser instanceof UnknownUser;
    }
}

