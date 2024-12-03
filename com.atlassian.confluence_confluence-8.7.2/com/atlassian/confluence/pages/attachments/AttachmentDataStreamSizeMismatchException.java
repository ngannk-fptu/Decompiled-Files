/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.pages.attachments;

public class AttachmentDataStreamSizeMismatchException
extends RuntimeException {
    private final long expectedSize;
    private final long actualSize;

    public AttachmentDataStreamSizeMismatchException(long expectedSize, long actualSize) {
        super(String.format("Attachment data stream contains a different number of bytes to the declared size of the attachment. Expected: %d, actual: %d", expectedSize, actualSize));
        this.expectedSize = expectedSize;
        this.actualSize = actualSize;
    }

    public long getExpectedSize() {
        return this.expectedSize;
    }

    public long getActualSize() {
        return this.actualSize;
    }
}

