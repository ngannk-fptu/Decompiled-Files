/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.content.attachment;

import com.atlassian.confluence.event.events.content.attachment.GeneralAttachmentCreateEvent;
import com.atlassian.confluence.pages.Attachment;
import java.util.List;

public class HiddenAttachmentCreateEvent
extends GeneralAttachmentCreateEvent {
    private static final long serialVersionUID = 7896695350273723754L;

    public HiddenAttachmentCreateEvent(Object src, List<Attachment> attachments) {
        super(src, attachments, true);
    }

    public HiddenAttachmentCreateEvent(Object src, Attachment attachment) {
        super(src, attachment, true);
    }
}

