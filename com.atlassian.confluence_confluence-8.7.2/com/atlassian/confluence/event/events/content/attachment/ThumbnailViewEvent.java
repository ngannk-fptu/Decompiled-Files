/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.content.attachment;

import com.atlassian.confluence.event.events.content.attachment.AttachmentViewEvent;
import com.atlassian.confluence.pages.Attachment;

public class ThumbnailViewEvent
extends AttachmentViewEvent {
    private static final long serialVersionUID = -6458301790176546707L;

    public ThumbnailViewEvent(Object src, Attachment attachment) {
        super(src, attachment);
    }
}

