/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.ContentBody
 *  com.atlassian.confluence.api.model.content.ContentRepresentation
 *  com.atlassian.confluence.api.model.content.History
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.event.events.content.attachment.AttachmentBatchUploadCompletedEvent
 *  com.atlassian.confluence.event.events.content.attachment.AttachmentTrashedEvent
 *  com.atlassian.confluence.event.events.content.attachment.AttachmentUpdateEvent
 *  com.atlassian.confluence.event.events.content.attachment.AttachmentVersionRemoveEvent
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.plugins.files.api.FileComment
 *  com.atlassian.confluence.plugins.files.entities.FileCommentInput
 *  com.atlassian.confluence.plugins.files.event.FileCommentCreateEvent
 *  com.atlassian.confluence.plugins.files.event.FileCommentDeleteEvent
 *  com.atlassian.confluence.plugins.files.event.FileCommentUpdateEvent
 *  com.atlassian.confluence.plugins.files.manager.ConfluenceFileManager
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.fugue.Option
 *  com.atlassian.sal.api.user.UserKey
 *  org.springframework.beans.factory.DisposableBean
 */
package com.atlassian.confluence.plugins.files.notifications;

import com.atlassian.confluence.api.model.content.ContentBody;
import com.atlassian.confluence.api.model.content.ContentRepresentation;
import com.atlassian.confluence.api.model.content.History;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.event.events.content.attachment.AttachmentBatchUploadCompletedEvent;
import com.atlassian.confluence.event.events.content.attachment.AttachmentTrashedEvent;
import com.atlassian.confluence.event.events.content.attachment.AttachmentUpdateEvent;
import com.atlassian.confluence.event.events.content.attachment.AttachmentVersionRemoveEvent;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.plugins.files.api.FileComment;
import com.atlassian.confluence.plugins.files.entities.FileCommentInput;
import com.atlassian.confluence.plugins.files.event.FileCommentCreateEvent;
import com.atlassian.confluence.plugins.files.event.FileCommentDeleteEvent;
import com.atlassian.confluence.plugins.files.event.FileCommentUpdateEvent;
import com.atlassian.confluence.plugins.files.manager.ConfluenceFileManager;
import com.atlassian.confluence.plugins.files.notifications.api.FileContentEventType;
import com.atlassian.confluence.plugins.files.notifications.email.NotificationContent;
import com.atlassian.confluence.plugins.files.notifications.email.RemovedFileContent;
import com.atlassian.confluence.plugins.files.notifications.event.FileContentRemoveEvent;
import com.atlassian.confluence.plugins.files.notifications.event.FileContentUpdateEvent;
import com.atlassian.confluence.plugins.files.notifications.helper.FileContentPayloadTransformerHelper;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.fugue.Option;
import com.atlassian.sal.api.user.UserKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.DisposableBean;

public class FileContentEventListener
implements DisposableBean {
    private final EventPublisher eventPublisher;
    private final ConfluenceFileManager confluenceFileManager;

    public FileContentEventListener(EventPublisher eventPublisher, ConfluenceFileManager confluenceFileManager) {
        this.eventPublisher = eventPublisher;
        this.confluenceFileManager = confluenceFileManager;
        eventPublisher.register((Object)this);
    }

    @EventListener
    public void fileUpdated(AttachmentUpdateEvent event) {
        Attachment newAttachment = event.getNew();
        Attachment oldAttachment = event.getOld();
        if (newAttachment.getVersion() == oldAttachment.getVersion() || newAttachment.isMinorEdit() || newAttachment.isHidden()) {
            return;
        }
        ConfluenceUser originatingUser = event.getNew().getLastModifier();
        String originatingUserKey = originatingUser != null ? originatingUser.getKey().getStringValue() : null;
        this.eventPublisher.publish((Object)new FileContentUpdateEvent(FileContentEventType.UPDATE, FileContentPayloadTransformerHelper.getNotificationContentForCeo(event.getContent()), Collections.singletonList(FileContentPayloadTransformerHelper.getNotificationContentForCeo((ContentEntityObject)newAttachment)), FileContentPayloadTransformerHelper.getNotificationContentForCeo((ContentEntityObject)oldAttachment), NotificationContent.EMPTY, originatingUserKey, event.isSuppressNotifications()));
    }

    @EventListener
    public void fileCreated(AttachmentBatchUploadCompletedEvent event) {
        List filtered = event.getAttachments().stream().filter(attachment -> attachment.getVersion() == 1 && !attachment.isMinorEdit() && !attachment.isHidden()).collect(Collectors.toList());
        if (filtered.isEmpty()) {
            return;
        }
        List<NotificationContent> attachmentContents = filtered.stream().map(FileContentPayloadTransformerHelper::getNotificationContentForCeo).collect(Collectors.toList());
        ConfluenceUser creator = ((Attachment)event.getAttachments().get(0)).getCreator();
        this.eventPublisher.publish((Object)new FileContentUpdateEvent(FileContentEventType.CREATE, FileContentPayloadTransformerHelper.getNotificationContentForCeo(event.getContent()), attachmentContents, NotificationContent.EMPTY, NotificationContent.EMPTY, creator != null ? creator.getKey().getStringValue() : null, event.isSuppressNotifications()));
    }

    @EventListener
    public void fileCommentCreated(FileCommentCreateEvent event) {
        Attachment attachment = event.getParentFile();
        FileComment fileComment = event.getFileComment();
        Option maybeUserKey = ((History)fileComment.getHistory().get()).getCreatedBy().getUserKey();
        this.eventPublisher.publish((Object)new FileContentUpdateEvent(FileContentEventType.CREATE_COMMENT, FileContentPayloadTransformerHelper.getNotificationContentForCeo(attachment.getContainer()), Collections.singletonList(FileContentPayloadTransformerHelper.getNotificationContentForCeo((ContentEntityObject)attachment)), NotificationContent.EMPTY, FileContentPayloadTransformerHelper.getNotificationContentForFileComment(fileComment), maybeUserKey.isEmpty() ? null : ((UserKey)maybeUserKey.get()).toString(), false));
    }

    @EventListener
    public void fileCommentDeleted(FileCommentDeleteEvent event) {
        Attachment attachment = event.getParentFile();
        NotificationContent attachmentContent = FileContentPayloadTransformerHelper.getNotificationContentForCeo((ContentEntityObject)attachment);
        ContentId attachmentId = attachmentContent.getContentId();
        FileComment fileComment = event.getFileComment();
        HashMap<Long, RemovedFileContent> removedFileContents = new HashMap<Long, RemovedFileContent>();
        removedFileContents.put(attachmentId.asLong(), new RemovedFileContent(attachmentId, attachment.getTitle(), ((ContentBody)fileComment.getBody().get(ContentRepresentation.VIEW)).getValue(), this.confluenceFileManager.getUnresolvedCommentCountByAttachmentId(attachmentId.asLong()), attachment.getVersion()));
        ConfluenceUser originator = event.getOriginatingUser();
        this.eventPublisher.publish((Object)new FileContentRemoveEvent(FileContentEventType.DELETE_COMMENT, FileContentPayloadTransformerHelper.getNotificationContentForCeo(attachment.getContainer()), Collections.singletonList(attachmentContent), NotificationContent.EMPTY, FileContentPayloadTransformerHelper.getNotificationContentForFileComment(fileComment), originator != null ? originator.getKey().getStringValue() : null, removedFileContents, false));
    }

    @EventListener
    public void fileDeleted(AttachmentTrashedEvent event) {
        ConfluenceUser originatingUser = (ConfluenceUser)event.getOriginatingUser();
        ArrayList<NotificationContent> attachments = new ArrayList<NotificationContent>();
        HashMap<Long, RemovedFileContent> removedFileContents = new HashMap<Long, RemovedFileContent>();
        for (Attachment attachment : event.getAttachments()) {
            NotificationContent attachmentContent = FileContentPayloadTransformerHelper.getNotificationContentForCeo((ContentEntityObject)attachment);
            ContentId attachmentId = attachmentContent.getContentId();
            attachments.add(attachmentContent);
            removedFileContents.put(attachmentContent.getContentId().asLong(), new RemovedFileContent(attachmentId, attachment.getTitle(), "", this.confluenceFileManager.getUnresolvedCommentCountByAttachmentId(attachmentId.asLong()), attachment.getVersion()));
        }
        this.eventPublisher.publish((Object)new FileContentRemoveEvent(FileContentEventType.DELETE, FileContentPayloadTransformerHelper.getNotificationContentForCeo(event.getContent()), attachments, NotificationContent.EMPTY, NotificationContent.EMPTY, originatingUser != null ? originatingUser.getKey().getStringValue() : null, removedFileContents, event.isSuppressNotifications()));
    }

    @EventListener
    public void fileVersionDeleted(AttachmentVersionRemoveEvent event) {
        ConfluenceUser originatingUser = (ConfluenceUser)event.getOriginatingUser();
        ArrayList<NotificationContent> contents = new ArrayList<NotificationContent>();
        HashMap<Long, RemovedFileContent> removedFileContents = new HashMap<Long, RemovedFileContent>();
        for (Attachment attachment : event.getAttachments()) {
            NotificationContent attachmentContent = FileContentPayloadTransformerHelper.getNotificationContentForCeo((ContentEntityObject)attachment);
            ContentId attachmentId = attachmentContent.getContentId();
            contents.add(attachmentContent);
            removedFileContents.put(attachmentId.asLong(), new RemovedFileContent(attachmentId, attachment.getTitle(), "", this.confluenceFileManager.getUnresolvedCommentCountByAttachmentId(attachmentId.asLong()), attachment.getVersion()));
        }
        this.eventPublisher.publish((Object)new FileContentRemoveEvent(FileContentEventType.DELETE_VERSION, FileContentPayloadTransformerHelper.getNotificationContentForCeo(event.getContent()), contents, NotificationContent.EMPTY, NotificationContent.EMPTY, originatingUser != null ? originatingUser.getKey().getStringValue() : null, removedFileContents, event.isSuppressNotifications()));
    }

    @EventListener
    public void fileCommentUpdated(FileCommentUpdateEvent event) {
        FileCommentInput fileCommentInput = event.getFileCommentInput();
        if (Boolean.TRUE.equals(fileCommentInput.isResolved())) {
            Attachment attachment = event.getParentFile();
            ConfluenceUser originator = event.getOriginatingUser();
            this.eventPublisher.publish((Object)new FileContentUpdateEvent(FileContentEventType.RESOLVE_COMMENT, FileContentPayloadTransformerHelper.getNotificationContentForCeo(attachment.getContainer()), Collections.singletonList(FileContentPayloadTransformerHelper.getNotificationContentForCeo((ContentEntityObject)attachment)), NotificationContent.EMPTY, FileContentPayloadTransformerHelper.getNotificationContentForFileComment(event.getFileComment()), originator != null ? originator.getKey().getStringValue() : null, false));
        }
    }

    public void destroy() throws Exception {
        this.eventPublisher.unregister((Object)this);
    }
}

