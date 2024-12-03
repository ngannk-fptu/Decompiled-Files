/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.user.ConfluenceUser
 */
package com.atlassian.confluence.extra.calendar3.notification;

import com.atlassian.confluence.extra.calendar3.notification.DefaultCalendarNotificationManager;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.user.ConfluenceUser;
import java.util.Map;

public interface ResourceDataHandler {
    public DefaultCalendarNotificationManager.IdentifiableContentDataHandler createAvatarDataHandler(ConfluenceUser var1, Map<String, String> var2);

    public DefaultCalendarNotificationManager.IdentifiableContentDataHandler createAvatarDataHandler(String var1, Map<String, String> var2);

    public DefaultCalendarNotificationManager.IdentifiableContentDataHandler createDefaultProfilePictureDataHandler(Map<String, String> var1);

    public DefaultCalendarNotificationManager.IdentifiableContentDataHandler createAttachmentDataHandler(Attachment var1, Map<String, String> var2);
}

