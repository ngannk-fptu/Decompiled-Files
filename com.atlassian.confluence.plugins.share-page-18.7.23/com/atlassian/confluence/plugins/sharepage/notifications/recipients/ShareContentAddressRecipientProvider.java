/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.notifications.ConfluenceUserRole
 *  com.atlassian.confluence.notifications.NonUserBasedRecipientsProviderTemplate
 *  com.atlassian.confluence.notifications.Notification
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugin.notifications.api.medium.DefaultNotificationAddress
 *  com.atlassian.plugin.notifications.api.medium.NotificationAddress
 *  com.atlassian.plugin.notifications.spi.UserRole
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.confluence.plugins.sharepage.notifications.recipients;

import com.atlassian.confluence.notifications.ConfluenceUserRole;
import com.atlassian.confluence.notifications.NonUserBasedRecipientsProviderTemplate;
import com.atlassian.confluence.notifications.Notification;
import com.atlassian.confluence.plugins.sharepage.notifications.context.ShareNotificationAddress;
import com.atlassian.confluence.plugins.sharepage.notifications.payload.ShareContentPayload;
import com.atlassian.fugue.Option;
import com.atlassian.plugin.notifications.api.medium.DefaultNotificationAddress;
import com.atlassian.plugin.notifications.api.medium.NotificationAddress;
import com.atlassian.plugin.notifications.spi.UserRole;
import com.google.common.collect.ImmutableList;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ShareContentAddressRecipientProvider
extends NonUserBasedRecipientsProviderTemplate<ShareContentPayload> {
    private static final ImmutableList<UserRole> ROLES = ImmutableList.of((Object)new ConfluenceUserRole("SHARE_CONTENT"));

    public Iterable<NotificationAddress> nonUserBasedRecipientsFor(Notification<ShareContentPayload> notification) {
        this.verifyPayloadMatches(notification);
        return this.computeNonUserBasedRecipients(notification);
    }

    protected Iterable<NotificationAddress> computeNonUserBasedRecipients(Notification<ShareContentPayload> notification) {
        LinkedList notificationAddressList = new LinkedList();
        Map<String, Set<String>> cleanedEmails = this.getCleanedEmailsWithGroups(notification);
        if (cleanedEmails != null) {
            cleanedEmails.entrySet().forEach(entry -> notificationAddressList.addAll(((Set)entry.getValue()).stream().map(group -> new ShareNotificationAddress((Option<String>)Option.option((Object)this.getEmailMediumKey()), (String)entry.getKey(), (String)group)).collect(Collectors.toList())));
        } else {
            notificationAddressList.addAll(this.getCleanedEmails(notification).stream().map(email -> new DefaultNotificationAddress(Option.option((Object)this.getEmailMediumKey()), email)).collect(Collectors.toList()));
        }
        return ImmutableList.copyOf(notificationAddressList);
    }

    private Set<String> getCleanedEmails(Notification<ShareContentPayload> notification) {
        return ((ShareContentPayload)notification.getPayload()).getEmails();
    }

    private Map<String, Set<String>> getCleanedEmailsWithGroups(Notification<ShareContentPayload> notification) {
        return ((ShareContentPayload)notification.getPayload()).getEmailsWithGroups();
    }

    private String getEmailMediumKey() {
        return "mail";
    }

    public Iterable<UserRole> getUserRoles() {
        return ROLES;
    }
}

