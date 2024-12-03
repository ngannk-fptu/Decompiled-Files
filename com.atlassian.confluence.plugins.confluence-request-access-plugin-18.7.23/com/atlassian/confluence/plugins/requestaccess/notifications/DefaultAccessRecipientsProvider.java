/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.notifications.ConfluenceUserRole
 *  com.atlassian.confluence.notifications.Notification
 *  com.atlassian.confluence.notifications.RecipientsProviderTemplate
 *  com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient
 *  com.atlassian.plugin.notifications.api.medium.recipient.UserKeyRoleRecipient
 *  com.atlassian.plugin.notifications.spi.UserRole
 */
package com.atlassian.confluence.plugins.requestaccess.notifications;

import com.atlassian.confluence.notifications.ConfluenceUserRole;
import com.atlassian.confluence.notifications.Notification;
import com.atlassian.confluence.notifications.RecipientsProviderTemplate;
import com.atlassian.confluence.plugins.requestaccess.notifications.DefaultAccessNotificationPayload;
import com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient;
import com.atlassian.plugin.notifications.api.medium.recipient.UserKeyRoleRecipient;
import com.atlassian.plugin.notifications.spi.UserRole;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DefaultAccessRecipientsProvider
extends RecipientsProviderTemplate<DefaultAccessNotificationPayload> {
    private static final List<UserRole> ROLES = Arrays.asList(new ConfluenceUserRole("GRANT_ACCESS"), new ConfluenceUserRole("REQUEST_ACCESS"));

    protected Iterable<RoleRecipient> computeUserBasedRecipients(Notification<DefaultAccessNotificationPayload> notification) {
        DefaultAccessNotificationPayload payload = (DefaultAccessNotificationPayload)notification.getPayload();
        return Collections.singletonList(new UserKeyRoleRecipient((UserRole)new ConfluenceUserRole(payload.getUserRole()), payload.getTargetUserKey()));
    }

    public Iterable<UserRole> getUserRoles() {
        return ROLES;
    }
}

