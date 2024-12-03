/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.event.notification.NotificationEnabledEvent
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.user.ConfluenceUser
 */
package com.atlassian.confluence.plugins.requestaccess.events;

import com.atlassian.confluence.api.model.event.notification.NotificationEnabledEvent;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.plugins.requestaccess.events.AbstractAccessEvent;
import com.atlassian.confluence.plugins.requestaccess.resource.PageRestrictionResource;
import com.atlassian.confluence.user.ConfluenceUser;

public class AccessGrantedEvent
extends AbstractAccessEvent
implements NotificationEnabledEvent {
    public static final String GRANT_ACCESS_USER_ROLE = "GRANT_ACCESS";

    public AccessGrantedEvent(ConfluenceUser sourceUser, ConfluenceUser targetUser, ContentEntityObject content, PageRestrictionResource.AccessType accessType, String spaceKey) {
        super(sourceUser, targetUser, content, accessType, spaceKey);
    }

    @Override
    public final String getUserRole() {
        return GRANT_ACCESS_USER_ROLE;
    }

    public boolean isSuppressNotifications() {
        return false;
    }
}

