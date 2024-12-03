/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceEntityObject
 *  com.atlassian.confluence.mail.notification.Notification
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.streams.api.common.Pair
 *  com.atlassian.user.User
 *  com.google.common.base.Function
 *  com.google.common.collect.Iterables
 */
package com.atlassian.streams.confluence;

import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.mail.notification.Notification;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.streams.api.common.Pair;
import com.atlassian.user.User;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import java.util.List;

public class ConfluenceWatchHelper<T extends ConfluenceEntityObject, K> {
    public boolean startWatching(T entity, Function<Pair<User, T>, Void> addNotification, Function<T, List<Notification>> getNotifications) {
        User user = AuthenticatedUserThreadLocal.getUser();
        if (user == null) {
            throw new SecurityException("User is not authenticated");
        }
        int previousWatcherCount = Iterables.size((Iterable)((Iterable)getNotifications.apply(entity)));
        addNotification.apply((Object)Pair.pair((Object)user, entity));
        int updatedWatcherCount = Iterables.size((Iterable)((Iterable)getNotifications.apply(entity)));
        return updatedWatcherCount == previousWatcherCount + 1;
    }
}

