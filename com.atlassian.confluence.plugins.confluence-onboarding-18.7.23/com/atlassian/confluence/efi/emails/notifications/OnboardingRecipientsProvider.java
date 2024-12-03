/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.notifications.ConfluenceUserRole
 *  com.atlassian.confluence.notifications.Notification
 *  com.atlassian.confluence.notifications.RecipientsProviderTemplate
 *  com.atlassian.confluence.notifications.content.WatchTypeUtil
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient
 *  com.atlassian.plugin.notifications.api.medium.recipient.UserKeyRoleRecipient
 *  com.atlassian.plugin.notifications.spi.UserRole
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.collect.Lists
 */
package com.atlassian.confluence.efi.emails.notifications;

import com.atlassian.confluence.efi.emails.notifications.OnboardingPayload;
import com.atlassian.confluence.notifications.ConfluenceUserRole;
import com.atlassian.confluence.notifications.Notification;
import com.atlassian.confluence.notifications.RecipientsProviderTemplate;
import com.atlassian.confluence.notifications.content.WatchTypeUtil;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.fugue.Maybe;
import com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient;
import com.atlassian.plugin.notifications.api.medium.recipient.UserKeyRoleRecipient;
import com.atlassian.plugin.notifications.spi.UserRole;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.collect.Lists;
import java.util.ArrayList;

public class OnboardingRecipientsProvider
extends RecipientsProviderTemplate<OnboardingPayload> {
    private static final ConfluenceUserRole ONBOARDING_ROLE = new ConfluenceUserRole("ONBOARDING");
    private UserAccessor userAccessor;

    public OnboardingRecipientsProvider(@ComponentImport UserAccessor userAccessor) {
        this.userAccessor = userAccessor;
    }

    protected Iterable<RoleRecipient> computeUserBasedRecipients(Notification<OnboardingPayload> notification) {
        Maybe originator = notification.getOriginator();
        UserKey userKey = (UserKey)originator.get();
        ConfluenceUser user = this.userAccessor.getUserByKey(userKey);
        ArrayList roleRecipients = Lists.newArrayList();
        roleRecipients.add(new UserKeyRoleRecipient((UserRole)ONBOARDING_ROLE, user.getKey(), originator.isDefined() && ((UserKey)originator.get()).equals((Object)user.getKey())));
        return roleRecipients;
    }

    public Iterable<UserRole> getUserRoles() {
        return WatchTypeUtil.watchTypesToUserRoles();
    }
}

