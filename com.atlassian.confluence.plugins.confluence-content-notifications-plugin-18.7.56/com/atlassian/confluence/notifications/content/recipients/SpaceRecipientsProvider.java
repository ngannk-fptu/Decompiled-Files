/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalSpi
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.service.content.ContentService
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.mail.notification.Notification
 *  com.atlassian.confluence.mail.notification.NotificationManager
 *  com.atlassian.confluence.notifications.Notification
 *  com.atlassian.confluence.notifications.NotificationPayload
 *  com.atlassian.confluence.notifications.PayloadBasedNotification
 *  com.atlassian.confluence.notifications.batch.service.BatchingRecipientsProvider
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.search.service.ContentTypeEnum
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.security.access.ConfluenceAccessManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.notifications.content.recipients;

import com.atlassian.annotations.ExperimentalSpi;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.service.content.ContentService;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.mail.notification.NotificationManager;
import com.atlassian.confluence.notifications.Notification;
import com.atlassian.confluence.notifications.NotificationPayload;
import com.atlassian.confluence.notifications.PayloadBasedNotification;
import com.atlassian.confluence.notifications.batch.service.BatchingRecipientsProvider;
import com.atlassian.confluence.notifications.content.AuthenticatedRecipientProvider;
import com.atlassian.confluence.notifications.content.CommonContentExpansions;
import com.atlassian.confluence.notifications.content.ContentIdPayload;
import com.atlassian.confluence.notifications.content.SimpleContentIdPayload;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.access.ConfluenceAccessManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Qualifier;

@ExperimentalSpi
public class SpaceRecipientsProvider
extends AuthenticatedRecipientProvider<ContentIdPayload>
implements BatchingRecipientsProvider<ContentIdPayload> {
    private final SpaceManager spaceManager;
    private final ContentService contentService;

    public SpaceRecipientsProvider(TransactionTemplate transactionTemplate, NotificationManager notificationManager, PageManager pageManager, SpaceManager spaceManager, ContentService contentService, UserAccessor userAccessor, PermissionManager permissionManager, @Qualifier(value="contentEntityManager") ContentEntityManager contentEntityManager, ConfluenceAccessManager confluenceAccessManager) {
        super(transactionTemplate, notificationManager, pageManager, userAccessor, permissionManager, contentEntityManager, confluenceAccessManager);
        this.spaceManager = spaceManager;
        this.contentService = contentService;
    }

    private ContentTypeEnum getContentTypeEnum(String contentType) {
        ContentTypeEnum contentTypeEnum = ContentTypeEnum.getByRepresentation((String)contentType);
        if ("page".equals(contentType) || "blogpost".equals(contentType) || "comment".equals(contentType)) {
            contentTypeEnum = null;
        }
        return contentTypeEnum;
    }

    @Override
    protected Iterable<com.atlassian.confluence.mail.notification.Notification> computeNotificationsInContextOfNotifier(Notification<ContentIdPayload> notification) {
        ContentIdPayload payload = (ContentIdPayload)notification.getPayload();
        ContentTypeEnum contentType = this.getContentTypeEnum(payload.getContentType().getType());
        return (Iterable)this.contentService.find(new Expansion[]{CommonContentExpansions.SPACE}).withId(ContentId.of((ContentType)payload.getContentType(), (long)payload.getContentId())).fetchOne().map(input -> {
            Space space = this.spaceManager.getSpace(input.getSpace().getKey());
            return this.permissionFiltered(this.notificationManager.getNotificationsBySpaceAndType(space, contentType), payload.getContentId());
        }).getOrElse((Object)Collections.EMPTY_LIST);
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
        PayloadBasedNotification fakeNotification = new PayloadBasedNotification((NotificationPayload)new SimpleContentIdPayload(confContentType, contentId.asLong(), randomOriginatorUserKey), null);
        return this.computeUserBasedRecipients(fakeNotification);
    }
}

