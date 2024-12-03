/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.mail.notification.Notification
 *  com.atlassian.confluence.mail.notification.NotificationManager
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.streams.api.common.Pair
 *  com.atlassian.user.User
 *  com.google.common.base.Function
 */
package com.atlassian.streams.confluence;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.mail.notification.Notification;
import com.atlassian.confluence.mail.notification.NotificationManager;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.streams.api.common.Pair;
import com.atlassian.streams.confluence.ConfluenceWatchHelper;
import com.atlassian.streams.confluence.ConfluenceWatchInlineActionHandler;
import com.atlassian.user.User;
import com.google.common.base.Function;
import java.util.List;
import java.util.Objects;

public class ConfluenceWatchPageInlineActionHandler
implements ConfluenceWatchInlineActionHandler<Long> {
    private final NotificationManager notificationManager;
    private final PageManager pageManager;
    private final PermissionManager permissionManager;
    private final ConfluenceWatchHelper<AbstractPage, Long> watchHelper = new ConfluenceWatchHelper();
    private AddPageNotification addPageNotification = new AddPageNotification();
    private GetPageNotifications getPageNotifications = new GetPageNotifications();

    public ConfluenceWatchPageInlineActionHandler(NotificationManager notificationManager, PageManager pageManager, PermissionManager permissionManager) {
        this.notificationManager = Objects.requireNonNull(notificationManager, "notificationManager");
        this.pageManager = Objects.requireNonNull(pageManager, "pageManager");
        this.permissionManager = Objects.requireNonNull(permissionManager, "permissionManager");
    }

    @Override
    public boolean startWatching(Long key) {
        AbstractPage page = this.pageManager.getAbstractPage(key.longValue());
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        if (!this.permissionManager.hasPermission((User)user, Permission.VIEW, (Object)page)) {
            return false;
        }
        if (user != null && this.notificationManager.isUserWatchingPageOrSpace((User)user, null, page)) {
            return true;
        }
        return this.watchHelper.startWatching(page, this.addPageNotification, this.getPageNotifications);
    }

    private class GetPageNotifications
    implements Function<AbstractPage, List<Notification>> {
        private GetPageNotifications() {
        }

        public List<Notification> apply(AbstractPage entity) {
            return ConfluenceWatchPageInlineActionHandler.this.notificationManager.getNotificationsByContent((ContentEntityObject)entity);
        }
    }

    private class AddPageNotification
    implements Function<Pair<User, AbstractPage>, Void> {
        private AddPageNotification() {
        }

        public Void apply(Pair<User, AbstractPage> params) {
            ConfluenceWatchPageInlineActionHandler.this.notificationManager.addContentNotification((User)params.first(), (ContentEntityObject)params.second());
            return null;
        }
    }
}

