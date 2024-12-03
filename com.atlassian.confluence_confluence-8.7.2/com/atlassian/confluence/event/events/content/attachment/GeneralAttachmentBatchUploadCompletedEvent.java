/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.content.attachment;

import com.atlassian.confluence.event.events.content.attachment.AttachmentEvent;
import com.atlassian.confluence.pages.Attachment;
import java.util.List;

public class GeneralAttachmentBatchUploadCompletedEvent
extends AttachmentEvent {
    private static final long serialVersionUID = 6241566393331105264L;

    public GeneralAttachmentBatchUploadCompletedEvent(Object src, List<Attachment> attachments) {
        super(src, attachments);
    }

    public GeneralAttachmentBatchUploadCompletedEvent(Object src, List<Attachment> attachments, boolean suppressNotifications) {
        super(src, attachments, suppressNotifications);
    }
}

