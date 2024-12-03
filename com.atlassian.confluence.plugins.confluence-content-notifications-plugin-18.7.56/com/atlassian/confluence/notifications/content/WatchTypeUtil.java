/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.MaybeNot
 *  com.atlassian.confluence.mail.notification.Notification$WatchType
 *  com.atlassian.confluence.notifications.ConfluenceUserRole
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugin.notifications.spi.UserRole
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 */
package com.atlassian.confluence.notifications.content;

import com.atlassian.confluence.core.MaybeNot;
import com.atlassian.confluence.mail.notification.Notification;
import com.atlassian.confluence.notifications.ConfluenceUserRole;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;
import com.atlassian.plugin.notifications.spi.UserRole;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class WatchTypeUtil {
    public static Maybe<Notification.WatchType> computeWatchTypeFrom(UserRole role) {
        Preconditions.checkNotNull((Object)role, (Object)"role should not be null");
        try {
            return Option.some((Object)Notification.WatchType.valueOf((String)role.getID()));
        }
        catch (IllegalArgumentException e) {
            return MaybeNot.becauseOf((String)"error computing WatchType: %s : role is an invalid WatchType: %s", (Object[])new Object[]{e.getMessage(), role.getID()});
        }
    }

    public static Iterable<UserRole> watchTypesToUserRoles() {
        return Iterables.transform((Iterable)Lists.newArrayList((Object[])Notification.WatchType.values()), watchType -> new ConfluenceUserRole(watchType.name()));
    }
}

