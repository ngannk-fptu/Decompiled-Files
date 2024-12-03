/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.confluence.event.events.internal.attachment;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.core.SaveContext;
import com.atlassian.confluence.pages.Attachment;

@Internal
public class AttachmentCreatedAuditingEvent {
    private final Attachment attachment;
    private final SaveContext saveContext;

    public AttachmentCreatedAuditingEvent(Attachment attachment, SaveContext saveContext) {
        this.attachment = attachment;
        this.saveContext = saveContext;
    }

    public Attachment getAttachment() {
        return this.attachment;
    }

    public SaveContext getSaveContext() {
        return this.saveContext;
    }
}

