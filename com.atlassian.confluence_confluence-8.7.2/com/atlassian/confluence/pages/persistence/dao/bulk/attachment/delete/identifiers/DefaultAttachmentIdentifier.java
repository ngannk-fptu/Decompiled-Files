/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.pages.persistence.dao.bulk.attachment.delete.identifiers;

import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.persistence.dao.bulk.attachment.delete.AttachmentIdentifier;

public class DefaultAttachmentIdentifier
implements AttachmentIdentifier {
    private Attachment attachment;

    public DefaultAttachmentIdentifier(Attachment attachment) {
        this.attachment = attachment;
    }

    public Attachment getAttachment() {
        return this.attachment;
    }
}

