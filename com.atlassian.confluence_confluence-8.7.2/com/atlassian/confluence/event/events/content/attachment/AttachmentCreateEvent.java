/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.content.attachment;

import com.atlassian.confluence.event.events.content.attachment.GeneralAttachmentCreateEvent;
import com.atlassian.confluence.pages.Attachment;
import java.util.List;

public class AttachmentCreateEvent
extends GeneralAttachmentCreateEvent {
    private static final long serialVersionUID = 2013102190671964603L;

    public AttachmentCreateEvent(Object src, List<Attachment> attachments) {
        super(src, attachments);
    }

    public AttachmentCreateEvent(Object src, Attachment attachment) {
        super(src, attachment);
    }

    public AttachmentCreateEvent(Object src, List<Attachment> attachments, boolean suppressNotifications) {
        super(src, attachments, suppressNotifications);
    }

    public AttachmentCreateEvent(Object src, Attachment attachment, boolean suppressNotifications) {
        super(src, attachment, suppressNotifications);
    }
}

