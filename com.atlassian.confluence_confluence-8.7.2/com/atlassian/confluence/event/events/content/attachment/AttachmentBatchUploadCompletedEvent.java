/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.event.notification.NotificationEnabledEvent
 */
package com.atlassian.confluence.event.events.content.attachment;

import com.atlassian.confluence.api.model.event.notification.NotificationEnabledEvent;
import com.atlassian.confluence.event.events.content.attachment.GeneralAttachmentBatchUploadCompletedEvent;
import com.atlassian.confluence.pages.Attachment;
import java.util.List;

public class AttachmentBatchUploadCompletedEvent
extends GeneralAttachmentBatchUploadCompletedEvent
implements NotificationEnabledEvent {
    private static final long serialVersionUID = -7533073670907438708L;

    public AttachmentBatchUploadCompletedEvent(Object src, List<Attachment> attachments) {
        super(src, attachments);
    }

    public AttachmentBatchUploadCompletedEvent(Object src, List<Attachment> attachments, boolean suppressNotifications) {
        super(src, attachments, suppressNotifications);
    }
}

