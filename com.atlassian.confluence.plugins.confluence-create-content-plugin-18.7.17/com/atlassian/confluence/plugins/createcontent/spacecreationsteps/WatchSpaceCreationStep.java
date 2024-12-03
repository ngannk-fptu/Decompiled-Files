/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.mail.notification.NotificationManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugins.createcontent.spacecreationsteps;

import com.atlassian.confluence.mail.notification.NotificationManager;
import com.atlassian.confluence.plugins.createcontent.spacecreationsteps.AbstractSpaceCreationStep;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.user.User;
import java.util.Map;

public class WatchSpaceCreationStep
extends AbstractSpaceCreationStep {
    private final UserAccessor userAccessor;
    private final NotificationManager notificationManager;
    public static final String CONTEXT_KEY = "watchUsers";

    public WatchSpaceCreationStep(UserAccessor userAccessor, NotificationManager notificationManager) {
        this.userAccessor = userAccessor;
        this.notificationManager = notificationManager;
    }

    @Override
    public void posthandle(Space space, Map<String, Object> context) {
        String[] split;
        String users = (String)context.get(CONTEXT_KEY);
        if (users == null) {
            return;
        }
        for (String username : split = users.split(",")) {
            ConfluenceUser user = this.userAccessor.getUserByName(username);
            if (user == null) continue;
            this.notificationManager.addSpaceNotification((User)user, space);
        }
    }
}

