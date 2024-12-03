/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.content.attachment;

import com.atlassian.confluence.event.events.content.attachment.GeneralAttachmentUpdateEvent;
import com.atlassian.confluence.pages.Attachment;

public class HiddenAttachmentUpdateEvent
extends GeneralAttachmentUpdateEvent {
    private static final long serialVersionUID = -1236195107414319520L;

    public HiddenAttachmentUpdateEvent(Object src, Attachment attachment, Attachment previousVersion) {
        super(src, attachment, previousVersion, true);
    }
}

