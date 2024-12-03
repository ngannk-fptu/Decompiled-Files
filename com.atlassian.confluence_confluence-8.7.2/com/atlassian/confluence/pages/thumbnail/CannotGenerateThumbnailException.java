/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.pages.thumbnail;

import com.atlassian.confluence.pages.Attachment;

public class CannotGenerateThumbnailException
extends Exception {
    private final Attachment attachment;

    public CannotGenerateThumbnailException(Attachment attachment) {
        super("Cannot generate thumbnail for: " + attachment);
        this.attachment = attachment;
    }

    public Attachment getAttachment() {
        return this.attachment;
    }
}

