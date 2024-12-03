/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.mail.notification.listeners.NotificationData
 *  com.atlassian.confluence.user.ConfluenceUser
 */
package com.atlassian.confluence.plugins.mentions;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.mail.notification.listeners.NotificationData;
import com.atlassian.confluence.user.ConfluenceUser;

public interface NotificationDataService {
    public NotificationData prepareDecorationContext(ConfluenceUser var1, ContentEntityObject var2);
}

