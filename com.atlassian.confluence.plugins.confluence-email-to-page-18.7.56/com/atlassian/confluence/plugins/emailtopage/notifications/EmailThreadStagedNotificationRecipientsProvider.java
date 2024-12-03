/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.notifications.NonUserBasedRecipientsProviderTemplate
 *  com.atlassian.confluence.notifications.Notification
 *  com.atlassian.confluence.notifications.SystemUserRole
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugin.notifications.api.medium.DefaultNotificationAddress
 *  com.atlassian.plugin.notifications.api.medium.NotificationAddress
 *  com.atlassian.plugin.notifications.spi.UserRole
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.confluence.plugins.emailtopage.notifications;

import com.atlassian.confluence.notifications.NonUserBasedRecipientsProviderTemplate;
import com.atlassian.confluence.notifications.Notification;
import com.atlassian.confluence.notifications.SystemUserRole;
import com.atlassian.confluence.plugins.emailtopage.events.EmailThreadStagedPayload;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.fugue.Option;
import com.atlassian.plugin.notifications.api.medium.DefaultNotificationAddress;
import com.atlassian.plugin.notifications.api.medium.NotificationAddress;
import com.atlassian.plugin.notifications.spi.UserRole;
import com.atlassian.sal.api.user.UserKey;
import java.util.Collections;
import java.util.Set;

public class EmailThreadStagedNotificationRecipientsProvider
extends NonUserBasedRecipientsProviderTemplate<EmailThreadStagedPayload> {
    private static final Set<UserRole> USER_ROLES = Collections.singleton(SystemUserRole.INSTANCE);
    private final UserAccessor userAccessor;

    public EmailThreadStagedNotificationRecipientsProvider(UserAccessor userAccessor) {
        this.userAccessor = userAccessor;
    }

    public Iterable<UserRole> getUserRoles() {
        return USER_ROLES;
    }

    protected Iterable<NotificationAddress> computeNonUserBasedRecipients(Notification<EmailThreadStagedPayload> notification) {
        ConfluenceUser user = this.userAccessor.getExistingUserByKey((UserKey)((EmailThreadStagedPayload)notification.getPayload()).getOriginatorUserKey().get());
        String emailAddress = user.getEmail();
        return Collections.singleton(new DefaultNotificationAddress(Option.some((Object)"mail"), emailAddress));
    }
}

