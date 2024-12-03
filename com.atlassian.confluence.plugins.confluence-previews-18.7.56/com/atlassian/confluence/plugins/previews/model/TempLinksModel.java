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
public class TempLinksModel {
    @XmlElement(name="attachmentId")
    private long attachmentId;
    @XmlElement(name="downloadPath")
    private String downloadPath;
    @XmlElement(name="uploadPath")
    private String uploadPath;
    @XmlElement(name="signedDownloadPath")
    private String signedDownloadPath;
    @XmlElement(name="signedUploadPath")
    private String signedUploadPath;
    @XmlElement(name="signedAnalyticsPublishPath")
    private String signedAnalyticsPublishPath;
    @XmlElement(name="signedAttachmentHistoryPath")
    private String signedAttachmentHistoryPath;

    public TempLinksModel() {
    }

    public TempLinksModel(long attachmentId, String downloadPath, String signedDownloadPath, String uploadPath, String signedUploadPath, String signedAnalyticsPublishPath, String signedAttachmentHistoryPath) {
        this.attachmentId = attachmentId;
        this.downloadPath = downloadPath;
        this.signedDownloadPath = signedDownloadPath;
        this.signedUploadPath = signedUploadPath;
        this.uploadPath = uploadPath;
        this.signedAnalyticsPublishPath = signedAnalyticsPublishPath;
        this.signedAttachmentHistoryPath = signedAttachmentHistoryPath;
    }

    public long getAttachmentId() {
        return this.attachmentId;
    }

    public void setAttachmentId(long attachmentId) {
        this.attachmentId = attachmentId;
    }

    public String getDownloadPath() {
        return this.downloadPath;
    }

    public void setDownloadPath(String downloadPath) {
        this.downloadPath = downloadPath;
    }

    public String getUploadPath() {
        return this.uploadPath;
    }

    public void setUploadPath(String uploadPath) {
        this.uploadPath = uploadPath;
    }

    public String getSignedDownloadPath() {
        return this.signedDownloadPath;
    }

    public void setSignedDownloadPath(String signedDownloadPath) {
        this.signedDownloadPath = signedDownloadPath;
    }

    public String getSignedUploadPath() {
        return this.signedUploadPath;
    }

    public void setSignedUploadPath(String signedUploadPath) {
        this.signedUploadPath = signedUploadPath;
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

