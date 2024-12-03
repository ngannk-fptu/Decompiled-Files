/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.Container
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentRepresentation
 *  com.atlassian.confluence.api.model.content.ContentStatus
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.content.Version
 *  com.atlassian.confluence.api.model.link.Link
 *  com.atlassian.confluence.api.model.link.LinkType
 *  com.atlassian.confluence.api.service.content.ContentService
 *  com.atlassian.confluence.notifications.Notification
 *  com.atlassian.confluence.notifications.NotificationUserService
 *  com.atlassian.confluence.notifications.content.ContentRenderContextFactory
 *  com.atlassian.confluence.plugin.descriptor.mail.NotificationContext
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugin.notifications.api.medium.ServerConfiguration
 *  com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.User
 *  javax.annotation.Nonnull
 */
package com.atlassian.confluence.plugins.files.notifications.email;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.Container;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentRepresentation;
import com.atlassian.confluence.api.model.content.ContentStatus;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.Version;
import com.atlassian.confluence.api.model.link.Link;
import com.atlassian.confluence.api.model.link.LinkType;
import com.atlassian.confluence.api.service.content.ContentService;
import com.atlassian.confluence.notifications.Notification;
import com.atlassian.confluence.notifications.NotificationUserService;
import com.atlassian.confluence.notifications.content.ContentRenderContextFactory;
import com.atlassian.confluence.plugin.descriptor.mail.NotificationContext;
import com.atlassian.confluence.plugins.files.notifications.FileContentExpansions;
import com.atlassian.confluence.plugins.files.notifications.api.FileContentEventType;
import com.atlassian.confluence.plugins.files.notifications.email.FileContentRemovePayload;
import com.atlassian.confluence.plugins.files.notifications.email.NotificationContent;
import com.atlassian.confluence.plugins.files.notifications.email.RemovedFileContent;
import com.atlassian.confluence.plugins.files.notifications.helper.FileContentRenderContextHelper;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;
import com.atlassian.plugin.notifications.api.medium.ServerConfiguration;
import com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.User;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;

public class FileContentRemoveRenderContextFactory
extends ContentRenderContextFactory<FileContentRemovePayload> {
    private final ContentService contentService;
    private final UserAccessor userAccessor;
    private final I18NBeanFactory i18NBeanFactory;
    private final NotificationUserService notificationUserService;

    public FileContentRemoveRenderContextFactory(ContentService contentService, UserAccessor userAccessor, I18NBeanFactory i18NBeanFactory, NotificationUserService notificationUserService) {
        this.contentService = contentService;
        this.userAccessor = userAccessor;
        this.i18NBeanFactory = i18NBeanFactory;
        this.notificationUserService = notificationUserService;
    }

    protected Maybe<NotificationContext> createForRecipient(Notification<FileContentRemovePayload> notification, ServerConfiguration serverConfiguration, RoleRecipient roleRecipient) {
        FileContentRemovePayload payload = (FileContentRemovePayload)notification.getPayload();
        Content containerContent = this.getContentForNotificationContent(payload.getContainerNotificationContent(), FileContentExpansions.CONTAINER_CONTENT_EXPANSIONS.toArray());
        if (containerContent == null) {
            return Option.none();
        }
        List<Content> fileContentList = this.getFileContentForNotificationContents(payload.getFileNotificationContents(), payload.getRemovedFileContents(), containerContent, payload.getType());
        Content descendantContent = this.getDescendantContentForNotificationContent(payload.getFileNotificationContents(), payload.getRemovedFileContents(), payload.getType());
        ConfluenceUser recipient = this.userAccessor.getUserByKey(roleRecipient.getUserKey());
        Option modifierKey = payload.getOriginatingUserKey().isDefined() ? Option.some((Object)new UserKey((String)payload.getOriginatingUserKey().get())) : Option.none();
        User modifier = this.notificationUserService.findUserForKey((User)recipient, (Maybe)modifierKey);
        return Option.some((Object)FileContentRenderContextHelper.generateNotificationContextMap(payload.getType(), fileContentList, payload.getContainerNotificationContent().getContentId(), descendantContent, null, notification.getKey(), modifier, roleRecipient, this.i18NBeanFactory.getI18NBean()));
    }

    private List<Content> getFileContentForNotificationContents(List<NotificationContent> notificationContents, Map<Long, RemovedFileContent> removedFileContents, Content containerContent, @Nonnull FileContentEventType type) {
        ArrayList<Content> fileContentList = new ArrayList<Content>();
        for (NotificationContent notificationContent : notificationContents) {
            Content fileContent;
            RemovedFileContent removedFileContent = removedFileContents.get(notificationContent.getContentId().asLong());
            if (removedFileContent == null || (fileContent = type.equals((Object)FileContentEventType.DELETE_COMMENT) ? this.getContentForNotificationContent(notificationContent, FileContentExpansions.FILE_CONTENT_EXPANSIONS.toArray()) : Content.builder().id(removedFileContent.getFileContentId()).type(ContentType.ATTACHMENT).status(ContentStatus.valueOf((String)"deleted")).title(removedFileContent.getFileTitle()).container((Container)containerContent).version(Version.builder().number(removedFileContent.getFileVersion()).build()).addLink((Link)containerContent.getLinks().get(LinkType.WEB_UI)).build()) == null) continue;
            HashMap<String, Integer> metadata = new HashMap<String, Integer>();
            metadata.put("numUnresolvedComments", removedFileContent.getFileUnresolvedComments());
            fileContentList.add(Content.builder((Content)fileContent).metadata(metadata).addLink((Link)fileContent.getLinks().get(LinkType.WEB_UI)).build());
        }
        return fileContentList;
    }

    private Content getDescendantContentForNotificationContent(List<NotificationContent> fileNotificationContents, Map<Long, RemovedFileContent> removedFileContents, @Nonnull FileContentEventType type) {
        NotificationContent fileNotificationContent;
        RemovedFileContent removedFileContent;
        if (type.equals((Object)FileContentEventType.DELETE_COMMENT) && fileNotificationContents.size() == 1 && (removedFileContent = removedFileContents.get((fileNotificationContent = fileNotificationContents.get(0)).getContentId().asLong())) != null) {
            return Content.builder().body(removedFileContent.getFileCommentBody(), ContentRepresentation.VIEW).build();
        }
        return null;
    }

    private Content getContentForNotificationContent(NotificationContent notificationContent, Expansion[] expansions) {
        if (!notificationContent.isLatestVersion() && notificationContent.getContentVersion() > 0) {
            return (Content)this.contentService.find(expansions).withStatus(Collections.singletonList(ContentStatus.HISTORICAL)).withIdAndVersion(notificationContent.getContentId(), notificationContent.getContentVersion()).fetchOne().getOrNull();
        }
        return (Content)this.contentService.find(expansions).withId(notificationContent.getContentId()).fetchOne().getOrNull();
    }
}

