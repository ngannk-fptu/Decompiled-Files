/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.pages.attachments;

import com.atlassian.confluence.core.NotExportable;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.attachments.ImageDetails;

class ImageDetailsDto
implements NotExportable {
    private long id;
    private Attachment attachment;
    private int height;
    private int width;
    private String mimeType;

    ImageDetailsDto() {
    }

    ImageDetailsDto(ImageDetails imageDetails) {
        this.setAttachment(imageDetails.getAttachment());
        this.setHeight(imageDetails.getHeight());
        this.setWidth(imageDetails.getWidth());
        this.setMimeType(imageDetails.getMimeType());
    }

    ImageDetails toImageDetails() {
        return new ImageDetails(this.attachment, this.width, this.height, this.mimeType);
    }

    public Attachment getAttachment() {
        return this.attachment;
    }

    private void setAttachment(Attachment attachment) {
        this.attachment = attachment;
    }

    public int getHeight() {
        return this.height;
    }

    private void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return this.width;
    }

    private void setWidth(int width) {
        this.width = width;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    private void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    private void setId(long id) {
        this.id = id;
    }

    private long getId() {
        return this.id;
    }
}

