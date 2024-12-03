/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.mail.notification.Notification
 *  com.atlassian.confluence.mail.notification.NotificationManager
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.streams.api.common.Pair
 *  com.atlassian.user.User
 *  com.google.common.base.Function
 */
package com.atlassian.streams.confluence;

import com.atlassian.confluence.mail.notification.Notification;
import com.atlassian.confluence.mail.notification.NotificationManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.streams.api.common.Pair;
import com.atlassian.streams.confluence.ConfluenceWatchHelper;
import com.atlassian.streams.confluence.ConfluenceWatchInlineActionHandler;
import com.atlassian.user.User;
import com.google.common.base.Function;
import java.util.List;
import java.util.Objects;

public class ConfluenceWatchSpaceInlineActionHandler
implements ConfluenceWatchInlineActionHandler<String> {
    private final NotificationManager notificationManager;
    private final SpaceManager spaceManager;
    private final PermissionManager permissionManager;
    private final ConfluenceWatchHelper<Space, Long> watchHelper = new ConfluenceWatchHelper();
    private AddSpaceNotification addSpaceNotification = new AddSpaceNotification();
    private GetSpaceNotifications getSpaceNotifications = new GetSpaceNotifications();

    public ConfluenceWatchSpaceInlineActionHandler(NotificationManager notificationManager, SpaceManager spaceManager, PermissionManager permissionManager) {
        this.notificationManager = Objects.requireNonNull(notificationManager, "notificationManager");
        this.spaceManager = Objects.requireNonNull(spaceManager, "spaceManager");
        this.permissionManager = Objects.requireNonNull(permissionManager, "permissionManager");
    }

    @Override
    public boolean startWatching(String key) {
        Space space = this.spaceManager.getSpace(key);
        User user = AuthenticatedUserThreadLocal.getUser();
        if (!this.permissionManager.hasPermission(user, Permission.VIEW, (Object)space)) {
            return false;
        }
        if (user != null && this.notificationManager.isUserWatchingPageOrSpace(user, space, null)) {
            return true;
        }
        return this.watchHelper.startWatching(space, this.addSpaceNotification, this.getSpaceNotifications);
    }

    private class GetSpaceNotifications
    implements Function<Space, List<Notification>> {
        private GetSpaceNotifications() {
        }

        public List<Notification> apply(Space entity) {
            return ConfluenceWatchSpaceInlineActionHandler.this.notificationManager.getNotificationsBySpaceAndType(entity, null);
        }
    }

    private class AddSpaceNotification
    implements Function<Pair<User, Space>, Void> {
        private AddSpaceNotification() {
        }

        public Void apply(Pair<User, Space> params) {
            ConfluenceWatchSpaceInlineActionHandler.this.notificationManager.addSpaceNotification((User)params.first(), (Space)params.second(), null);
            return null;
        }
    }
}

