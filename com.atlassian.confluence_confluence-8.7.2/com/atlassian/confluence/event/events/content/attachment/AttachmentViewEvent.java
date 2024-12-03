/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.content.attachment;

import com.atlassian.confluence.event.events.content.attachment.AttachmentEvent;
import com.atlassian.confluence.pages.Attachment;
import java.util.List;

public class AttachmentViewEvent
extends AttachmentEvent {
    private static final long serialVersionUID = 8448284134836733461L;
    private final boolean download;

    public AttachmentViewEvent(Object src, Attachment attachment) {
        this(src, attachment, false);
    }

    public AttachmentViewEvent(Object src, Attachment attachment, boolean download) {
        super(src, attachment);
        this.download = download;
    }

    public AttachmentViewEvent(Object src, List<Attachment> attachments, boolean download) {
        super(src, attachments, true);
        this.download = download;
    }

    public boolean isDownload() {
        return this.download;
    }
}

