/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.content.attachment;

import com.atlassian.confluence.event.events.content.attachment.GeneralAttachmentUpdateEvent;
import com.atlassian.confluence.pages.Attachment;

public class AttachmentUpdateEvent
extends GeneralAttachmentUpdateEvent {
    private static final long serialVersionUID = 8726942604634972274L;

    public AttachmentUpdateEvent(Object src, Attachment attachment, Attachment previousVersion) {
        super(src, attachment, previousVersion);
    }

    public AttachmentUpdateEvent(Object src, Attachment attachment, Attachment previousVersion, boolean suppressNotifications) {
        super(src, attachment, previousVersion, suppressNotifications);
    }
}

