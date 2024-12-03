/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.plugins.previews.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="message")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class CompanionAttachmentModel {
    @XmlElement(name="fileId")
    private long fileId;
    @XmlElement(name="fileName")
    private String fileName;
    @XmlElement(name="mimeType")
    private String mimeType;
    @XmlElement(name="extension")
    private String extension;
    @XmlElement(name="downloadUrl")
    private String downloadUrl;
    @XmlElement(name="companionActionCallbackUrl")
    private String companionActionCallbackUrl;
    @XmlElement(name="uploadUrl")
    private String uploadUrl;
    @XmlElement(name="attachmentMaxSize")
    private long attachmentMaxSize;
    @XmlElement(name="pageId")
    private long pageId;
    @XmlElement(name="pageTitle")
    private String pageTitle;
    @XmlElement(name="remoteUserKey")
    private String remoteUserKey;
    @XmlElement(name="sourceUrl")
    private String sourceUrl;
    @XmlElement(name="signedAnalyticsPublishPath")
    private String signedAnalyticsPublishPath;
    @XmlElement(name="signedAttachmentHistoryPath")
    private String signedAttachmentHistoryPath;

    public CompanionAttachmentModel(long fileId, String fileName, String mimeType, String extension, String downloadUrl, String uploadUrl, long attachmentMaxSize, long pageId, String pageTitle, String remoteUserKey, String sourceUrl, String signedAnalyticsPublishPath, String signedAttachmentHistoryPath, String companionActionCallbackUrl) {
        this.fileId = fileId;
        this.fileName = fileName;
        this.mimeType = mimeType;
        this.extension = extension;
        this.downloadUrl = downloadUrl;
        this.uploadUrl = uploadUrl;
        this.attachmentMaxSize = attachmentMaxSize;
        this.pageId = pageId;
        this.pageTitle = pageTitle;
        this.remoteUserKey = remoteUserKey;
        this.sourceUrl = sourceUrl;
        this.signedAnalyticsPublishPath = signedAnalyticsPublishPath;
        this.signedAttachmentHistoryPath = signedAttachmentHistoryPath;
        this.companionActionCallbackUrl = companionActionCallbackUrl;
    }

    public long getFileId() {
        return this.fileId;
    }

    public void setFileId(long fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getExtension() {
        return this.extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getDownloadUrl() {
        return this.downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getUploadUrl() {
        return this.uploadUrl;
    }

    public void setUploadUrl(String uploadUrl) {
        this.uploadUrl = uploadUrl;
    }

    public String getCompanionActionCallbackUrl() {
        return this.companionActionCallbackUrl;
    }

    public void setCompanionActionCallbackUrl(String companionActionCallbackUrl) {
        this.companionActionCallbackUrl = companionActionCallbackUrl;
    }

    public long getAttachmentMaxSize() {
        return this.attachmentMaxSize;
    }

    public void setAttachmentMaxSize(long attachmentMaxSize) {
        this.attachmentMaxSize = attachmentMaxSize;
    }

    public long getPageId() {
        return this.pageId;
    }

    public void setPageId(long pageId) {
        this.pageId = pageId;
    }

    public String getPageTitle() {
        return this.pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public String getRemoteUserKey() {
        return this.remoteUserKey;
    }

    public void setRemoteUserKey(String remoteUserKey) {
        this.remoteUserKey = remoteUserKey;
    }

    public String getSourceUrl() {
        return this.sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getSignedAnalyticsPublishPath() {
        return this.signedAnalyticsPublishPath;
    }

    public void setSignedAnalyticsPublishPath(String signedAnalyticsPublishPath) {
        this.signedAnalyticsPublishPath = signedAnalyticsPublishPath;
    }

    public String getSignedAttachmentHistoryPath() {
        return this.signedAttachmentHistoryPath;
    }

    public void setSignedAttachmentHistoryPath(String signedAttachmentHistoryPath) {
        this.signedAttachmentHistoryPath = signedAttachmentHistoryPath;
    }
}

