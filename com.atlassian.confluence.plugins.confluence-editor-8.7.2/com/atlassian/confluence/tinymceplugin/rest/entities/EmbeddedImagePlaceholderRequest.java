/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.tinymceplugin.rest.entities;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class EmbeddedImagePlaceholderRequest {
    private String filename;
    private Long contentId;
    private boolean border;
    private String alignment;
    private boolean thumbnail;
    private String url;

    public String getFilename() {
        return this.filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Long getContentId() {
        return this.contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }

    public boolean isBorder() {
        return this.border;
    }

    public void setBorder(boolean border) {
        this.border = border;
    }

    public String getAlignment() {
        return this.alignment;
    }

    public void setAlignment(String alignment) {
        this.alignment = alignment;
    }

    public boolean isThumbnail() {
        return this.thumbnail;
    }

    public void setThumbnail(boolean thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

