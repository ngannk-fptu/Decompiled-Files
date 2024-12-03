/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.plugin.notifications.spi.salext.GroupManager
 *  com.google.common.collect.Iterables
 */
package com.atlassian.confluence.notifications.impl.sal;

import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.plugin.notifications.spi.salext.GroupManager;
import com.google.common.collect.Iterables;

public class ConfluenceGroupManager
implements GroupManager {
    private final UserAccessor userAccessor;

    public ConfluenceGroupManager(UserAccessor userAccessor) {
        this.userAccessor = userAccessor;
    }

    public Iterable<String> getGroups() {
        return Iterables.transform((Iterable)this.userAccessor.getGroupsAsList(), input -> input != null ? input.getName() : "");
    }
}

