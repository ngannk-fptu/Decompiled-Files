/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.mail.notification.Notification
 *  com.atlassian.confluence.mail.notification.NotificationManager
 *  com.atlassian.confluence.notifications.Notification
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.security.access.ConfluenceAccessManager
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.notifications.content.recipients;

import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.mail.notification.NotificationManager;
import com.atlassian.confluence.notifications.Notification;
import com.atlassian.confluence.notifications.content.ContentIdPayload;
import com.atlassian.confluence.notifications.content.WatchTypeBasedRecipientProvider;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.access.ConfluenceAccessManager;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Qualifier;

public class SiteBlogRecipientsProvider
extends WatchTypeBasedRecipientProvider<ContentIdPayload> {
    public static final String BLOGPOST_CREATED_KEY = "blogpost-created-notification";

    public SiteBlogRecipientsProvider(TransactionTemplate transactionTemplate, NotificationManager notificationManager, PageManager pageManager, PermissionManager permissionManager, @Qualifier(value="contentEntityManager") ContentEntityManager contentEntityManager, ConfluenceAccessManager confluenceAccessManager) {
        super(transactionTemplate, notificationManager, pageManager, permissionManager, contentEntityManager, confluenceAccessManager);
    }

    @Override
    protected Iterable<com.atlassian.confluence.mail.notification.Notification> computeNotifications(Notification<ContentIdPayload> notification) {
        if (ContentType.BLOG_POST.equals((Object)((ContentIdPayload)notification.getPayload()).getContentType()) && BLOGPOST_CREATED_KEY.equals(notification.getKey().getModuleKey())) {
            return this.permissionFiltered(this.notificationManager.getSiteBlogNotifications(), ((ContentIdPayload)notification.getPayload()).getContentId());
        }
        return Collections.emptyList();
    }
}

