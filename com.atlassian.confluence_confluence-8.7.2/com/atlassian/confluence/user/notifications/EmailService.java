/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.user.notifications;

import com.atlassian.confluence.mail.notification.listeners.NotificationData;
import com.atlassian.confluence.user.notifications.NotificationSendResult;
import java.util.List;

public interface EmailService {
    public NotificationSendResult sendToEmails(NotificationData var1, List<String> var2);

    public NotificationSendResult sendToEmail(NotificationData var1, String var2);
}

