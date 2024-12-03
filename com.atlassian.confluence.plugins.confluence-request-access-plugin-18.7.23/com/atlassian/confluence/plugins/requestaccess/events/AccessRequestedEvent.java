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

public class AccessRequestedEvent
extends AbstractAccessEvent
implements NotificationEnabledEvent {
    public static final String REQUEST_ACCESS_USER_ROLE = "REQUEST_ACCESS";

    public AccessRequestedEvent(ConfluenceUser requestAccessUser, ConfluenceUser requestAccessRecipient, ContentEntityObject content, PageRestrictionResource.AccessType accessType, String spaceKey) {
        super(requestAccessUser, requestAccessRecipient, content, accessType, spaceKey);
    }

    @Override
    public final String getUserRole() {
        return REQUEST_ACCESS_USER_ROLE;
    }

    public boolean isSuppressNotifications() {
        return false;
    }
}

