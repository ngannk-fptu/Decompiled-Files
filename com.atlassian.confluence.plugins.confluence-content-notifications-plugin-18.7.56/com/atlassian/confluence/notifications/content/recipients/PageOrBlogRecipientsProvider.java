/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalSpi
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.mail.notification.Notification
 *  com.atlassian.confluence.mail.notification.NotificationManager
 *  com.atlassian.confluence.notifications.ConfluenceUserRole
 *  com.atlassian.confluence.notifications.Notification
 *  com.atlassian.confluence.notifications.ProductionAwareLoggerSwitch
 *  com.atlassian.confluence.notifications.batch.service.BatchingRecipientsProvider
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.pages.CommentManager
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.security.access.ConfluenceAccessManager
 *  com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient
 *  com.atlassian.plugin.notifications.api.medium.recipient.UserKeyRoleRecipient
 *  com.atlassian.plugin.notifications.spi.UserRole
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.notifications.content.recipients;

import com.atlassian.annotations.ExperimentalSpi;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.mail.notification.NotificationManager;
import com.atlassian.confluence.notifications.ConfluenceUserRole;
import com.atlassian.confluence.notifications.Notification;
import com.atlassian.confluence.notifications.ProductionAwareLoggerSwitch;
import com.atlassian.confluence.notifications.batch.service.BatchingRecipientsProvider;
import com.atlassian.confluence.notifications.content.ContentIdPayload;
import com.atlassian.confluence.notifications.content.WatchTypeBasedRecipientProvider;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.CommentManager;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.access.ConfluenceAccessManager;
import com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient;
import com.atlassian.plugin.notifications.api.medium.recipient.UserKeyRoleRecipient;
import com.atlassian.plugin.notifications.spi.UserRole;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.collect.ImmutableList;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Qualifier;

@ExperimentalSpi
public class PageOrBlogRecipientsProvider
extends WatchTypeBasedRecipientProvider<ContentIdPayload>
implements BatchingRecipientsProvider<ContentIdPayload> {
    private static final ProductionAwareLoggerSwitch log = ProductionAwareLoggerSwitch.forCaller();
    private final CommentManager commentManager;
    private final ContentEntityManager contentEntityManager;

    public PageOrBlogRecipientsProvider(TransactionTemplate transactionTemplate, NotificationManager notificationManager, PageManager pageManager, CommentManager commentManager, @Qualifier(value="contentEntityManager") ContentEntityManager contentEntityManager, PermissionManager permissionManager, ConfluenceAccessManager confluenceAccessManager) {
        super(transactionTemplate, notificationManager, pageManager, permissionManager, contentEntityManager, confluenceAccessManager);
        this.commentManager = commentManager;
        this.contentEntityManager = contentEntityManager;
    }

    @Override
    protected Iterable<com.atlassian.confluence.mail.notification.Notification> computeNotifications(Notification<ContentIdPayload> notification) {
        long contentId = ((ContentIdPayload)notification.getPayload()).getContentId();
        ContentType contentType = ((ContentIdPayload)notification.getPayload()).getContentType();
        return this.computeNotifications(contentId, contentType);
    }

    private Iterable<com.atlassian.confluence.mail.notification.Notification> computeNotifications(long contentId, ContentType contentType) {
        ContentEntityObject content;
        if (ContentType.COMMENT.equals((Object)contentType)) {
            Comment comment = this.commentManager.getComment(contentId);
            content = comment.getContainer();
        } else if (ContentType.BLOG_POST.equals((Object)contentType) || ContentType.PAGE.equals((Object)contentType)) {
            content = this.contentEntityManager.getById(contentId);
        } else {
            return Collections.emptyList();
        }
        if (content == null) {
            log.warnOrDebug("Found no %s with id [%s].", new Object[]{contentType.getType(), contentId});
            return Collections.emptyList();
        }
        return this.permissionFiltered(this.notificationManager.getNotificationsByContent(content), contentId);
    }

    public Iterable<RoleRecipient> batchUserBasedRecipientsFor(String randomOriginatorUserKey, String id, String contentType) {
        ContentType confContentType;
        switch (contentType) {
            case "page": {
                confContentType = ContentType.PAGE;
                break;
            }
            case "blogpost": {
                confContentType = ContentType.BLOG_POST;
                break;
            }
            case "comment": {
                confContentType = ContentType.COMMENT;
                break;
            }
            default: {
                return Collections.emptyList();
            }
        }
        ContentId contentId = ContentId.deserialise((String)id);
        return (Iterable)this.transactionTemplate.execute(() -> {
            ImmutableList.Builder roleRecipientBuilder = ImmutableList.builder();
            for (com.atlassian.confluence.mail.notification.Notification fatNotification : this.computeNotifications(contentId.asLong(), confContentType)) {
                ConfluenceUserRole role = new ConfluenceUserRole(fatNotification.getWatchType().name());
                UserKey userKey = fatNotification.getReceiver().getKey();
                roleRecipientBuilder.add((Object)new UserKeyRoleRecipient((UserRole)role, userKey));
            }
            return roleRecipientBuilder.build();
        });
    }
}

