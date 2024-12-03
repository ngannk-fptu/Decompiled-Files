/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.notifications.NonUserBasedRecipientsProviderTemplate
 *  com.atlassian.confluence.notifications.Notification
 *  com.atlassian.confluence.notifications.SystemUserRole
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugin.notifications.api.medium.DefaultNotificationAddress
 *  com.atlassian.plugin.notifications.api.medium.NotificationAddress
 *  com.atlassian.plugin.notifications.spi.UserRole
 */
package com.atlassian.confluence.plugins.emailgateway.notifications;

import com.atlassian.confluence.notifications.NonUserBasedRecipientsProviderTemplate;
import com.atlassian.confluence.notifications.Notification;
import com.atlassian.confluence.notifications.SystemUserRole;
import com.atlassian.confluence.plugins.emailgateway.events.EmailHandlingExceptionPayload;
import com.atlassian.fugue.Option;
import com.atlassian.plugin.notifications.api.medium.DefaultNotificationAddress;
import com.atlassian.plugin.notifications.api.medium.NotificationAddress;
import com.atlassian.plugin.notifications.spi.UserRole;
import java.util.Collections;
import java.util.Set;

public class EmailHandlingExceptionNotificationRecipientsProvider
extends NonUserBasedRecipientsProviderTemplate<EmailHandlingExceptionPayload> {
    private static final Set<UserRole> USER_ROLES = Collections.singleton(SystemUserRole.INSTANCE);

    protected Iterable<NotificationAddress> computeNonUserBasedRecipients(Notification<EmailHandlingExceptionPayload> notification) {
        String emailAddress = ((EmailHandlingExceptionPayload)notification.getPayload()).getEmailAddress();
        return Collections.singleton(new DefaultNotificationAddress(Option.some((Object)"mail"), emailAddress));
    }

    public Iterable<UserRole> getUserRoles() {
        return USER_ROLES;
    }
}

