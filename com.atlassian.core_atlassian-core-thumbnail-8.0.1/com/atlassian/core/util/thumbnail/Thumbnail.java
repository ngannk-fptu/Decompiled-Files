/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.core.util.thumbnail;

import com.atlassian.core.util.thumbnail.ThumbnailDimension;
import javax.annotation.Nonnull;

public class Thumbnail
extends ThumbnailDimension {
    private final MimeType mimeType;
    private final String filename;
    private final long attachmentId;

    public Thumbnail(int height, int width, String fileName, long attachmentId) {
        this(height, width, fileName, attachmentId, MimeType.JPG);
    }

    public Thumbnail(int height, int width, String fileName, long attachmentId, @Nonnull MimeType mimeType) {
        super(width, height);
        this.mimeType = mimeType;
        this.filename = fileName;
        this.attachmentId = attachmentId;
    }

    public String getFilename() {
        return this.filename;
    }

    public long getAttachmentId() {
        return this.attachmentId;
    }

    public MimeType getMimeType() {
        return this.mimeType;
    }

    @Override
    public boolean equals(Object anObject) {
        if (this == anObject) {
            return true;
        }
        if (!(anObject instanceof Thumbnail)) {
            return false;
        }
        if (!super.equals(anObject)) {
            return false;
        }
        Thumbnail thumbnail = (Thumbnail)anObject;
        if (this.attachmentId != thumbnail.attachmentId) {
            return false;
        }
        if (this.filename != null ? !this.filename.equals(thumbnail.filename) : thumbnail.filename != null) {
            return false;
        }
        return this.mimeType == thumbnail.mimeType;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (this.mimeType != null ? this.mimeType.hashCode() : 0);
        result = 31 * result + (this.filename != null ? this.filename.hashCode() : 0);
        result = 31 * result + (int)(this.attachmentId ^ this.attachmentId >>> 32);
        return result;
    }

    @Override
    public String toString() {
        return this.getClass().getName() + " [MimeType=" + (Object)((Object)this.mimeType) + ",width=" + this.getWidth() + ",height=" + this.getHeight() + ",filename=" + this.getFilename() + ",attachmentId=" + this.getAttachmentId() + "]";
    }

    public static enum MimeType {
        JPG("image/jpeg"),
        PNG("image/png");

        private final String name;

        private MimeType(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }
    }
}

