/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.content.attachment;

import com.atlassian.confluence.event.events.content.attachment.AttachmentEvent;
import com.atlassian.confluence.event.events.types.Created;
import com.atlassian.confluence.pages.Attachment;
import java.util.List;

public class GeneralAttachmentCreateEvent
extends AttachmentEvent
implements Created {
    private static final long serialVersionUID = 8209087237301068209L;

    public GeneralAttachmentCreateEvent(Object src, List<Attachment> attachments) {
        super(src, attachments);
    }

    public GeneralAttachmentCreateEvent(Object src, Attachment attachment) {
        super(src, attachment);
    }

    public GeneralAttachmentCreateEvent(Object src, List<Attachment> attachments, boolean suppressNotifications) {
        super(src, attachments, suppressNotifications);
    }

    public GeneralAttachmentCreateEvent(Object src, Attachment attachment, boolean suppressNotifications) {
        super(src, attachment, suppressNotifications);
    }
}

