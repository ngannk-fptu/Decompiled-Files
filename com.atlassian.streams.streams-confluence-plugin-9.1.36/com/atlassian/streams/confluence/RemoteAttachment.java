/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.Attachment
 */
package com.atlassian.streams.confluence;

import com.atlassian.confluence.pages.Attachment;

public class RemoteAttachment {
    private String downloadUrl;
    private String previewUrl = "";
    private boolean hasPreview = false;
    private String comment;
    private String name;
    private long size;
    private int height = 0;
    private int width = 0;

    public RemoteAttachment(Attachment attachment) {
        this.downloadUrl = attachment.getDownloadPath();
        this.comment = attachment.getVersionComment();
        this.name = attachment.getFileName();
        this.size = attachment.getFileSize();
    }

    public void setPreview(String url, int height, int width) {
        this.previewUrl = url;
        this.height = height;
        this.width = width;
        this.hasPreview = true;
    }

    public String getDownloadUrl() {
        return this.downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getPreviewUrl() {
        return this.previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    public boolean hasPreview() {
        return this.hasPreview;
    }

    public void setHasPreview(boolean hasPreview) {
        this.hasPreview = hasPreview;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSize() {
        return this.size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public int getHeight() {
        return this.height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }
}

