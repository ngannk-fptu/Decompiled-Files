/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.Attachment
 */
package com.atlassian.confluence.plugins.attachment.reconciliation.marshalling;

import com.atlassian.confluence.pages.Attachment;

public class RestoredUnknownAttachment {
    private final Attachment attachment;
    private final Status status;

    public RestoredUnknownAttachment(Attachment attachment, Status status) {
        this.attachment = attachment;
        this.status = status;
    }

    public Attachment getAttachment() {
        return this.attachment;
    }

    public Status getStatus() {
        return this.status;
    }

    public static enum Status {
        VALID_ATTACHMENT,
        VALID_UNKNOWN_ATTACHMENT,
        INVALID_UNKNOWN_ATTACHMENT;

    }
}

