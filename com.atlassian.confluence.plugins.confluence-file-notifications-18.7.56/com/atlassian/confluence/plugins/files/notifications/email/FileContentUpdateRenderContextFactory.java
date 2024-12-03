/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentStatus
 *  com.atlassian.confluence.api.model.link.Link
 *  com.atlassian.confluence.api.model.link.LinkType
 *  com.atlassian.confluence.api.service.content.ContentService
 *  com.atlassian.confluence.notifications.Notification
 *  com.atlassian.confluence.notifications.NotificationUserService
 *  com.atlassian.confluence.notifications.content.ContentRenderContextFactory
 *  com.atlassian.confluence.plugin.descriptor.mail.NotificationContext
 *  com.atlassian.confluence.plugins.files.manager.ConfluenceFileManager
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugin.notifications.api.medium.ServerConfiguration
 *  com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugins.files.notifications.email;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentStatus;
import com.atlassian.confluence.api.model.link.Link;
import com.atlassian.confluence.api.model.link.LinkType;
import com.atlassian.confluence.api.service.content.ContentService;
import com.atlassian.confluence.notifications.Notification;
import com.atlassian.confluence.notifications.NotificationUserService;
import com.atlassian.confluence.notifications.content.ContentRenderContextFactory;
import com.atlassian.confluence.plugin.descriptor.mail.NotificationContext;
import com.atlassian.confluence.plugins.files.manager.ConfluenceFileManager;
import com.atlassian.confluence.plugins.files.notifications.FileContentExpansions;
import com.atlassian.confluence.plugins.files.notifications.email.FileContentUpdatePayload;
import com.atlassian.confluence.plugins.files.notifications.email.NotificationContent;
import com.atlassian.confluence.plugins.files.notifications.helper.FileContentRenderContextHelper;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;
import com.atlassian.plugin.notifications.api.medium.ServerConfiguration;
import com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.User;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FileContentUpdateRenderContextFactory
extends ContentRenderContextFactory<FileContentUpdatePayload> {
    private final ContentService contentService;
    private final ConfluenceFileManager confluenceFileManager;
    private final UserAccessor userAccessor;
    private final I18NBeanFactory i18NBeanFactory;
    private final TransactionTemplate transactionTemplate;
    private final NotificationUserService notificationUserService;

    public FileContentUpdateRenderContextFactory(ContentService contentService, ConfluenceFileManager confluenceFileManager, UserAccessor userAccessor, I18NBeanFactory i18NBeanFactory, TransactionTemplate transactionTemplate, NotificationUserService notificationUserService) {
        this.contentService = contentService;
        this.confluenceFileManager = confluenceFileManager;
        this.userAccessor = userAccessor;
        this.i18NBeanFactory = i18NBeanFactory;
        this.transactionTemplate = transactionTemplate;
        this.notificationUserService = notificationUserService;
    }

    protected Maybe<NotificationContext> createForRecipient(Notification<FileContentUpdatePayload> fileContentUpdatePayloadNotification, ServerConfiguration serverConfiguration, RoleRecipient roleRecipient) {
        return (Maybe)this.transactionTemplate.execute(() -> {
            FileContentUpdatePayload payload = (FileContentUpdatePayload)fileContentUpdatePayloadNotification.getPayload();
            List<Content> fileContentList = this.getFileContentsForNotificationContents(payload.getFileNotificationContents());
            ConfluenceUser recipient = this.userAccessor.getUserByKey(roleRecipient.getUserKey());
            Maybe<String> originatorKey = payload.getOriginatingUserKey();
            User modifier = this.notificationUserService.findUserForKey((User)recipient, (Maybe)(originatorKey.isDefined() ? Option.some((Object)new UserKey((String)originatorKey.get())) : Option.none()));
            if (fileContentList.isEmpty()) {
                return Option.none();
            }
            return Option.some((Object)FileContentRenderContextHelper.generateNotificationContextMap(payload.getType(), fileContentList, payload.getContainerNotificationContent().getContentId(), this.getDescendantContentForNotificationContent(payload.getDescendantNotificationContent()), this.getFileContentForNotificationContent(payload.getPreviousFileNotificationContent()), fileContentUpdatePayloadNotification.getKey(), modifier, roleRecipient, this.i18NBeanFactory.getI18NBean()));
        });
    }

    private List<Content> getFileContentsForNotificationContents(List<NotificationContent> notificationContents) {
        return notificationContents.stream().map(this::getFileContentForNotificationContent).filter(Objects::nonNull).collect(Collectors.toList());
    }

    private Content getFileContentForNotificationContent(NotificationContent notificationContent) {
        Content fileContent = this.getContentForNotificationContent(notificationContent, FileContentExpansions.FILE_CONTENT_EXPANSIONS.toArray());
        if (fileContent == null) {
            return null;
        }
        if (notificationContent.isLatestVersion()) {
            HashMap<String, Integer> metadata = new HashMap<String, Integer>();
            metadata.put("numUnresolvedComments", this.confluenceFileManager.getUnresolvedCommentCountByAttachmentId(fileContent.getId().asLong()));
            fileContent = Content.builder((Content)fileContent).metadata(metadata).addLink((Link)fileContent.getLinks().get(LinkType.WEB_UI)).build();
        } else {
            HashMap<String, Integer> metadata = new HashMap<String, Integer>();
            metadata.put("numUnresolvedComments", 0);
            fileContent = Content.builder((Content)fileContent).metadata(metadata).addLink(LinkType.WEB_UI, "/").build();
        }
        return fileContent;
    }

    private Content getDescendantContentForNotificationContent(NotificationContent notificationContent) {
        Content descendantContent = this.getContentForNotificationContent(notificationContent, FileContentExpansions.DESCENDANT_CONTENT_EXPANSIONS.toArray());
        if (descendantContent != null) {
            descendantContent = Content.builder((Content)descendantContent).addLink((Link)descendantContent.getLinks().get(LinkType.WEB_UI)).addLink(LinkType.valueOf((String)"like"), "/plugins/likes/like.action?contentId=" + notificationContent.getContentId().asLong()).build();
        }
        return descendantContent;
    }

    private Content getContentForNotificationContent(NotificationContent notificationContent, Expansion[] expansions) {
        if (!notificationContent.isLatestVersion() && notificationContent.getContentVersion() > 0) {
            return (Content)this.contentService.find(expansions).withStatus(Collections.singletonList(ContentStatus.HISTORICAL)).withIdAndVersion(notificationContent.getContentId(), notificationContent.getContentVersion()).fetchOne().getOrNull();
        }
        return (Content)this.contentService.find(expansions).withId(notificationContent.getContentId()).fetchOne().getOrNull();
    }
}

