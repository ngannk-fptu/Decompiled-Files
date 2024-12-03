/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.util.GeneralUtil
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.confluence.rpc.soap.beans;

import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.util.GeneralUtil;
import java.util.Date;
import java.util.Objects;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class RemoteAttachment {
    long id;
    long pageId;
    long fileSize;
    String title;
    String fileName;
    String contentType;
    String creator;
    String url;
    String comment;
    Date created;
    public static final String __PARANAMER_DATA = "<init> com.atlassian.confluence.pages.Attachment attachment \n<init> long,java.lang.String,java.lang.String,java.lang.String contentId,fileName,contentType,comment \nequals java.lang.Object o \nsetContentType java.lang.String contentType \nsetCreated java.util.Date created \nsetCreator java.lang.String creator \nsetFileName java.lang.String fileName \nsetFileSize long fileSize \nsetId long id \nsetPageId long pageId \nsetTitle java.lang.String title \nsetUrl java.lang.String url \n";

    public RemoteAttachment() {
    }

    public RemoteAttachment(Attachment attachment) {
        this.id = attachment.getId();
        this.pageId = Objects.requireNonNull(attachment.getContainer()).getId();
        this.title = attachment.getDisplayTitle();
        this.url = GeneralUtil.getGlobalSettings().getBaseUrl() + attachment.getDownloadPath();
        this.fileName = attachment.getFileName();
        this.fileSize = attachment.getFileSize();
        this.contentType = attachment.getMediaType();
        this.comment = attachment.getVersionComment();
        if (attachment.getCreationDate() != null) {
            this.created = new Date(attachment.getCreationDate().getTime());
        }
        if (attachment.getCreatorName() != null) {
            this.creator = attachment.getCreatorName();
        }
    }

    public RemoteAttachment(long contentId, String fileName, String contentType, String comment) {
        this.pageId = contentId;
        this.fileName = fileName;
        this.comment = comment;
        this.contentType = contentType;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPageId() {
        return this.pageId;
    }

    public void setPageId(long pageId) {
        this.pageId = pageId;
    }

    public long getFileSize() {
        return this.fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContentType() {
        return this.contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getCreator() {
        return this.creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getCreated() {
        return this.created;
    }

    public String getComment() {
        return this.comment;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RemoteAttachment)) {
            return false;
        }
        RemoteAttachment remoteAttachment = (RemoteAttachment)o;
        if (this.fileSize != remoteAttachment.fileSize) {
            return false;
        }
        if (this.id != remoteAttachment.id) {
            return false;
        }
        if (this.pageId != remoteAttachment.pageId) {
            return false;
        }
        if (this.contentType != null ? !this.contentType.equals(remoteAttachment.contentType) : remoteAttachment.contentType != null) {
            return false;
        }
        if (this.created != null ? !this.created.equals(remoteAttachment.created) : remoteAttachment.created != null) {
            return false;
        }
        if (this.creator != null ? !this.creator.equals(remoteAttachment.creator) : remoteAttachment.creator != null) {
            return false;
        }
        if (this.fileName != null ? !this.fileName.equals(remoteAttachment.fileName) : remoteAttachment.fileName != null) {
            return false;
        }
        if (this.title != null ? !this.title.equals(remoteAttachment.title) : remoteAttachment.title != null) {
            return false;
        }
        return !(this.url != null ? !this.url.equals(remoteAttachment.url) : remoteAttachment.url != null);
    }

    public int hashCode() {
        int result = (int)(this.id ^ this.id >>> 32);
        result = 29 * result + (int)(this.pageId ^ this.pageId >>> 32);
        result = 29 * result + (int)(this.fileSize ^ this.fileSize >>> 32);
        result = 29 * result + (this.title != null ? this.title.hashCode() : 0);
        result = 29 * result + (this.fileName != null ? this.fileName.hashCode() : 0);
        result = 29 * result + (this.contentType != null ? this.contentType.hashCode() : 0);
        result = 29 * result + (this.creator != null ? this.creator.hashCode() : 0);
        result = 29 * result + (this.url != null ? this.url.hashCode() : 0);
        result = 29 * result + (this.created != null ? this.created.hashCode() : 0);
        return result;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString((Object)this);
    }
}

