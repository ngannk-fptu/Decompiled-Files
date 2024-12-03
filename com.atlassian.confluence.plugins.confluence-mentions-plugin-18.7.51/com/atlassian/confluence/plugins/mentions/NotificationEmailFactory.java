/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.mail.template.PreRenderedMailNotificationQueueItem
 *  com.atlassian.confluence.plugin.descriptor.mail.NotificationContext
 *  com.atlassian.confluence.user.ConfluenceUser
 */
package com.atlassian.confluence.plugins.mentions;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.mail.template.PreRenderedMailNotificationQueueItem;
import com.atlassian.confluence.plugin.descriptor.mail.NotificationContext;
import com.atlassian.confluence.user.ConfluenceUser;

public interface NotificationEmailFactory {
    @Deprecated
    public PreRenderedMailNotificationQueueItem create(ContentEntityObject var1, ConfluenceUser var2, ConfluenceUser var3, String var4, String var5, String var6, NotificationContext var7);
}

