/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.content.attachment;

import com.atlassian.confluence.event.events.content.attachment.GeneralAttachmentBatchUploadCompletedEvent;
import com.atlassian.confluence.pages.Attachment;
import java.util.List;

public class HiddenAttachmentBatchUploadCompletedEvent
extends GeneralAttachmentBatchUploadCompletedEvent {
    private static final long serialVersionUID = 1071828595533654475L;

    public HiddenAttachmentBatchUploadCompletedEvent(Object src, List<Attachment> attachments) {
        super(src, attachments, true);
    }
}

