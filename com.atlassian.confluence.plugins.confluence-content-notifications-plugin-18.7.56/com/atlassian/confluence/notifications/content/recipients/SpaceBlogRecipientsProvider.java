/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.service.content.ContentService
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.mail.notification.Notification
 *  com.atlassian.confluence.mail.notification.NotificationManager
 *  com.atlassian.confluence.notifications.Notification
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.search.service.ContentTypeEnum
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.security.access.ConfluenceAccessManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.notifications.content.recipients;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.service.content.ContentService;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.mail.notification.NotificationManager;
import com.atlassian.confluence.notifications.Notification;
import com.atlassian.confluence.notifications.content.AuthenticatedRecipientProvider;
import com.atlassian.confluence.notifications.content.CommonContentExpansions;
import com.atlassian.confluence.notifications.content.ContentIdPayload;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.access.ConfluenceAccessManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Qualifier;

public class SpaceBlogRecipientsProvider
extends AuthenticatedRecipientProvider<ContentIdPayload> {
    public static final String BLOGPOST_CREATED_KEY = "blogpost-created-notification";
    private ContentService contentService;
    private SpaceManager spaceManager;

    public SpaceBlogRecipientsProvider(TransactionTemplate transactionTemplate, NotificationManager notificationManager, PageManager pageManager, UserAccessor userManager, ContentService contentService, SpaceManager spaceManager, PermissionManager permissionManager, @Qualifier(value="contentEntityManager") ContentEntityManager contentEntityManager, ConfluenceAccessManager confluenceAccessManager) {
        super(transactionTemplate, notificationManager, pageManager, userManager, permissionManager, contentEntityManager, confluenceAccessManager);
        this.contentService = contentService;
        this.spaceManager = spaceManager;
    }

    @Override
    protected Iterable<com.atlassian.confluence.mail.notification.Notification> computeNotificationsInContextOfNotifier(Notification<ContentIdPayload> notification) {
        if (((ContentIdPayload)notification.getPayload()).getContentType().equals((Object)ContentType.BLOG_POST) && BLOGPOST_CREATED_KEY.equals(notification.getKey().getModuleKey())) {
            ContentIdPayload payload = (ContentIdPayload)notification.getPayload();
            return (Iterable)this.contentService.find(new Expansion[]{CommonContentExpansions.SPACE}).withId(ContentId.of((ContentType)payload.getContentType(), (long)payload.getContentId())).fetchOne().map(input -> {
                Space space = this.spaceManager.getSpace(input.getSpace().getKey());
                return this.permissionFiltered(this.notificationManager.getNotificationsBySpaceAndType(space, ContentTypeEnum.BLOG), payload.getContentId());
            }).getOrElse((Object)Collections.EMPTY_LIST);
        }
        return Collections.emptyList();
    }
}

