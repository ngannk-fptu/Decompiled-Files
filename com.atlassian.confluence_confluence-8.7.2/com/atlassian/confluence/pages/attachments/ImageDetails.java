/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.pages.attachments;

import com.atlassian.confluence.pages.Attachment;

public final class ImageDetails {
    private final int height;
    private final int width;
    private final String mimeType;
    private final Attachment attachment;

    public ImageDetails(Attachment attachment, int width, int height, String mimeType) {
        this.height = height;
        this.width = width;
        this.mimeType = mimeType;
        this.attachment = attachment;
    }

    public int getHeight() {
        return this.height;
    }

    public int getWidth() {
        return this.width;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public Attachment getAttachment() {
        return this.attachment;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ImageDetails that = (ImageDetails)o;
        if (!this.attachment.equals(that.attachment)) {
            return false;
        }
        if (this.height != that.height) {
            return false;
        }
        if (this.width != that.width) {
            return false;
        }
        return this.mimeType.equals(that.mimeType);
    }

    public int hashCode() {
        int result = this.attachment.hashCode();
        result = 31 * result + this.height;
        result = 31 * result + this.width;
        result = 31 * result + this.mimeType.hashCode();
        return result;
    }
}

