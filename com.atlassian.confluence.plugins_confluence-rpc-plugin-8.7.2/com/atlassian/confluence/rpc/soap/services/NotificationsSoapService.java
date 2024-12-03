/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.mail.notification.Notification
 *  com.atlassian.confluence.mail.notification.NotificationManager
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.rpc.NotFoundException
 *  com.atlassian.confluence.rpc.NotPermittedException
 *  com.atlassian.confluence.rpc.RemoteException
 *  com.atlassian.confluence.search.service.ContentTypeEnum
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.rpc.soap.services;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.mail.notification.Notification;
import com.atlassian.confluence.mail.notification.NotificationManager;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.rpc.NotFoundException;
import com.atlassian.confluence.rpc.NotPermittedException;
import com.atlassian.confluence.rpc.RemoteException;
import com.atlassian.confluence.rpc.soap.beans.RemoteUser;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.user.User;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public final class NotificationsSoapService {
    private NotificationManager notificationManager;
    private PermissionManager permissionManager;
    private PageManager pageManager;
    private SpaceManager spaceManager;
    private UserAccessor userAccessor;
    public static final String __PARANAMER_DATA = "getWatchersForPage long pageId \ngetWatchersForSpace java.lang.String spaceKey \nisWatchingPage long,java.lang.String pageId,username \nisWatchingSpace java.lang.String,java.lang.String spaceKey,username \nisWatchingSpaceForType java.lang.String,java.lang.String,java.lang.String spaceKey,contentType,username \nremovePageWatch long pageId \nremovePageWatchForUser long,java.lang.String pageId,username \nremoveSpaceWatch java.lang.String spaceKey \nsetNotificationManager com.atlassian.confluence.mail.notification.NotificationManager notificationManager \nsetPageManager com.atlassian.confluence.pages.PageManager pageManager \nsetPermissionManager com.atlassian.confluence.security.PermissionManager permissionManager \nsetSpaceManager com.atlassian.confluence.spaces.SpaceManager spaceManager \nsetUserAccessor com.atlassian.confluence.user.UserAccessor userAccessor \nwatchPage long pageId \nwatchPageForUser long,java.lang.String pageId,username \nwatchSpace java.lang.String spaceKey \n";

    public boolean watchPage(long pageId) throws RemoteException {
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        if (currentUser == null) {
            throw new NotPermittedException("Anonymous users cannot configure watches");
        }
        AbstractPage page = this.getPage(pageId);
        if (this.notificationManager.isUserWatchingPageOrSpace((User)currentUser, page.getSpace(), page)) {
            return false;
        }
        this.notificationManager.addContentNotification((User)currentUser, (ContentEntityObject)page);
        return true;
    }

    public boolean watchPageForUser(long pageId, String username) throws RemoteException {
        AbstractPage page = this.getPage(pageId);
        this.checkSpaceAdministerPermission(page.getSpace(), username);
        ConfluenceUser user = this.checkUser(username);
        if (this.notificationManager.isUserWatchingPageOrSpace((User)user, page.getSpace(), page)) {
            return false;
        }
        this.notificationManager.addContentNotification((User)user, (ContentEntityObject)page);
        return true;
    }

    public boolean watchSpace(String spaceKey) throws RemoteException {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        if (user == null) {
            throw new NotPermittedException("Anonymous users cannot configure watches");
        }
        Space space = this.checkSpace(spaceKey);
        this.checkSpaceViewPermission((User)user, space);
        if (this.notificationManager.getNotificationByUserAndSpace((User)user, space) != null) {
            return false;
        }
        this.notificationManager.addSpaceNotification((User)user, space);
        return true;
    }

    public boolean removePageWatch(long pageId) throws RemoteException {
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        if (currentUser == null) {
            throw new NotPermittedException("Anonymous users cannot configure watches");
        }
        return this.removeWatch(pageId, (User)currentUser);
    }

    public boolean removeSpaceWatch(String spaceKey) throws RemoteException {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        if (user == null) {
            throw new NotPermittedException("Anonymous users cannot configure watches");
        }
        Space space = this.checkSpace(spaceKey);
        this.checkSpaceViewPermission((User)user, space);
        return this.removeWatch(this.notificationManager.getNotificationByUserAndSpace((User)user, space));
    }

    public boolean removePageWatchForUser(long pageId, String username) throws RemoteException {
        AbstractPage page = this.getPage(pageId);
        this.checkSpaceAdministerPermission(page.getSpace(), username);
        return this.removeWatch(pageId, (User)this.checkUser(username));
    }

    private boolean removeWatch(long pageId, User user) throws RemoteException {
        return this.removeWatch(this.notificationManager.getNotificationByUserAndContent(user, (ContentEntityObject)this.getPage(pageId)));
    }

    private boolean removeWatch(Notification notification) {
        if (notification == null) {
            return false;
        }
        this.notificationManager.removeNotification(notification);
        return true;
    }

    public boolean isWatchingPage(long pageId, String username) throws RemoteException {
        AbstractPage page = this.getPage(pageId);
        this.checkSpaceAdministerPermission(page.getSpace(), username);
        ConfluenceUser user = this.checkUser(username);
        return this.notificationManager.isWatchingContent((User)user, (ContentEntityObject)page);
    }

    public RemoteUser[] getWatchersForPage(long pageId) throws RemoteException {
        AbstractPage page = this.getPage(pageId);
        this.checkSpaceAdministerPermission(page.getSpace(), null);
        return this.toRemoteUsers(this.notificationManager.getNotificationsByContent((ContentEntityObject)page));
    }

    public boolean isWatchingSpace(String spaceKey, String username) throws RemoteException {
        Space space = this.checkSpace(spaceKey);
        this.checkSpaceAdministerPermission(space, username);
        ConfluenceUser user = this.checkUser(username);
        return this.notificationManager.getNotificationByUserAndSpace((User)user, space) != null;
    }

    public boolean isWatchingSpaceForType(String spaceKey, String contentType, String username) throws RemoteException {
        Space space = this.checkSpace(spaceKey);
        this.checkSpaceAdministerPermission(space, username);
        ConfluenceUser user = this.checkUser(username);
        ContentTypeEnum type = ContentTypeEnum.getByRepresentation((String)contentType);
        if (type == null) {
            throw new NotFoundException("ContentTypeEnum not found with type: " + contentType);
        }
        return this.notificationManager.getNotificationByUserAndSpaceAndType((User)user, space, type) != null;
    }

    public RemoteUser[] getWatchersForSpace(String spaceKey) throws RemoteException {
        Space space = this.checkSpace(spaceKey);
        this.checkSpaceAdministerPermission(space, null);
        return this.toRemoteUsers(this.notificationManager.getNotificationsBySpaceAndType(space, null));
    }

    private AbstractPage getPage(long pageId) throws NotFoundException, NotPermittedException {
        AbstractPage page = this.pageManager.getAbstractPage(pageId);
        if (page == null) {
            throw new NotFoundException("No page exists with ID: " + pageId);
        }
        page = page.getLatestVersion();
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        if (!this.permissionManager.hasPermission((User)currentUser, Permission.VIEW, (Object)page)) {
            throw new NotPermittedException("You do not have permission to view that page");
        }
        return page;
    }

    private void checkSpaceAdministerPermission(Space space, String username) throws NotPermittedException {
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        if (StringUtils.isNotBlank((CharSequence)username) && currentUser.getName().equalsIgnoreCase(username)) {
            return;
        }
        if (!this.permissionManager.hasPermission((User)currentUser, Permission.ADMINISTER, (Object)space)) {
            throw new NotPermittedException("You do not have permission to administer that space");
        }
    }

    private void checkSpaceViewPermission(User user, Space space) throws NotPermittedException {
        if (!this.permissionManager.hasPermission(user, Permission.VIEW, (Object)space)) {
            throw new NotPermittedException("You do not have permission to view that space");
        }
    }

    private ConfluenceUser checkUser(String username) throws NotFoundException {
        ConfluenceUser user = this.userAccessor.getUserByName(username);
        if (user == null) {
            throw new NotFoundException("User not found with username: " + username);
        }
        return user;
    }

    private Space checkSpace(String spaceKey) throws NotFoundException {
        Space space = this.spaceManager.getSpace(spaceKey);
        if (space == null) {
            throw new NotFoundException("No space exists with key: " + spaceKey);
        }
        return space;
    }

    private RemoteUser[] toRemoteUsers(List<Notification> notifications) {
        Object[] result = new RemoteUser[notifications.size()];
        for (int i = 0; i < result.length; ++i) {
            Notification notification = notifications.get(i);
            ConfluenceUser user = notification.getReceiver();
            if (user != null) {
                result[i] = new RemoteUser((User)user);
                continue;
            }
            RemoteUser remoteUser = new RemoteUser();
            result[i] = remoteUser;
        }
        Arrays.sort(result);
        return result;
    }

    public void setNotificationManager(NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
    }

    public void setPermissionManager(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    public void setPageManager(PageManager pageManager) {
        this.pageManager = pageManager;
    }

    public void setSpaceManager(SpaceManager spaceManager) {
        this.spaceManager = spaceManager;
    }

    public void setUserAccessor(UserAccessor userAccessor) {
        this.userAccessor = userAccessor;
    }
}

