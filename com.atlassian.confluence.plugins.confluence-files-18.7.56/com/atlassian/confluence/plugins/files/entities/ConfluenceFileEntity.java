/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.conversion.api.ConversionType
 *  javax.annotation.Nullable
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.files.entities;

import com.atlassian.confluence.plugins.conversion.api.ConversionType;
import com.atlassian.confluence.plugins.files.entities.FileContentEntity;
import java.util.Map;
import javax.annotation.Nullable;
import org.codehaus.jackson.annotate.JsonProperty;

public class ConfluenceFileEntity {
    public static final ConfluenceFileEntity INVALID = new ConfluenceFileEntity();
    @JsonProperty
    private long id;
    @JsonProperty
    private long containerId;
    @JsonProperty
    private int version;
    @JsonProperty
    private String fileName;
    @JsonProperty
    private String contentType;
    @JsonProperty
    private String niceType;
    @JsonProperty
    private String downloadUrl;
    @JsonProperty
    private Map<ConversionType, FileContentEntity> previewContents;
    @JsonProperty
    private boolean hasReplyPermission;
    @JsonProperty
    private boolean hasResolvePermission;
    @JsonProperty
    private boolean hasEditPermission;
    @JsonProperty
    private boolean hasDeletePermission;
    @JsonProperty
    private boolean hasUploadAttachmentVersionPermission;

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getContainerId() {
        return this.containerId;
    }

    public void setContainerId(long containerId) {
        this.containerId = containerId;
    }

    @Nullable
    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Nullable
    public String getContentType() {
        return this.contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Nullable
    public String getDownloadUrl() {
        return this.downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    @Nullable
    public Map<ConversionType, FileContentEntity> getPreviewContents() {
        return this.previewContents;
    }

    public void setPreviewContents(Map<ConversionType, FileContentEntity> previewContents) {
        this.previewContents = previewContents;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public boolean hasReplyPermission() {
        return this.hasReplyPermission;
    }

    public void setHasReplyPermission(boolean hasReplyPermission) {
        this.hasReplyPermission = hasReplyPermission;
    }

    public boolean hasResolvePermission() {
        return this.hasResolvePermission;
    }

    public void setHasResolvePermission(boolean hasResolvePermission) {
        this.hasResolvePermission = hasResolvePermission;
    }

    public boolean hasEditPermission() {
        return this.hasEditPermission;
    }

    public void setHasEditPermission(boolean hasEditPermission) {
        this.hasEditPermission = hasEditPermission;
    }

    public boolean hasDeletePermission() {
        return this.hasDeletePermission;
    }

    public void setHasDeletePermission(boolean hasDeletePermission) {
        this.hasDeletePermission = hasDeletePermission;
    }

    public boolean hasUploadAttachmentVersionPermission() {
        return this.hasUploadAttachmentVersionPermission;
    }

    public void setHasUploadAttachmentVersionPermission(boolean hasUploadAttachmentVersionPermission) {
        this.hasUploadAttachmentVersionPermission = hasUploadAttachmentVersionPermission;
    }

    public String getNiceType() {
        return this.niceType;
    }

    public void setNiceType(String niceType) {
        this.niceType = niceType;
    }

    static {
        INVALID.setId(-1L);
    }
}

