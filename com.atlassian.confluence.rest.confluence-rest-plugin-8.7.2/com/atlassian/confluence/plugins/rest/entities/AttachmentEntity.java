/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.Link
 *  com.atlassian.plugins.rest.common.expand.Expandable
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.plugins.rest.entities;

import com.atlassian.confluence.plugins.rest.entities.DateEntity;
import com.atlassian.confluence.plugins.rest.entities.SearchResultEntity;
import com.atlassian.confluence.plugins.rest.entities.SpaceEntity;
import com.atlassian.plugins.rest.common.Link;
import com.atlassian.plugins.rest.common.expand.Expandable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="attachment")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class AttachmentEntity
extends SearchResultEntity {
    @XmlAttribute
    private final String type = "attachment";
    @XmlElement(name="ownerId")
    private String ownerId;
    @XmlElement(name="parentTitle")
    private String parentTitle;
    @XmlElement(name="parentContentType")
    private String parentContentType;
    @XmlElement(name="datePath")
    private String parentDatePath;
    @XmlAttribute
    private String fileName;
    @XmlAttribute
    private String contentType;
    @XmlAttribute
    private long fileSize;
    @XmlAttribute
    private String niceFileSize;
    @XmlAttribute
    private String comment;
    @XmlAttribute
    private int version;
    @XmlAttribute(name="niceType")
    private String niceType;
    @XmlAttribute(name="iconClass")
    private String iconClass;
    @XmlElement(name="link")
    private List<Link> links;
    @XmlElement(name="title")
    private String title;
    @XmlElement
    private Link thumbnailLink;
    @XmlElement
    private int thumbnailWidth;
    @XmlElement
    private int thumbnailHeight;
    @XmlElement(name="wikiLink")
    private String wikiLink;
    @XmlElement(name="space")
    @Expandable(value="space")
    private SpaceEntity space;
    @XmlElement(name="lastModifiedDate")
    private DateEntity lastModifiedDate;
    @XmlElement(name="createdDate")
    private DateEntity createdDate;

    public String getType() {
        return "attachment";
    }

    public String getOwnerId() {
        return this.ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getParentTitle() {
        return this.parentTitle;
    }

    public void setParentTitle(String parentTitle) {
        this.parentTitle = parentTitle;
    }

    public String getParentContentType() {
        return this.parentContentType;
    }

    public void setParentContentType(String parentContentType) {
        this.parentContentType = parentContentType;
    }

    public String getParentDatePath() {
        return this.parentDatePath;
    }

    public void setParentDatePath(String parentDatePath) {
        this.parentDatePath = parentDatePath;
    }

    public String getFileName() {
        return this.fileName;
    }

    public String getContentType() {
        return this.contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public long getFileSize() {
        return this.fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getNiceFileSize() {
        return this.niceFileSize;
    }

    public void setNiceFileSize(String niceFileSize) {
        this.niceFileSize = niceFileSize;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public void setNiceType(String niceType) {
        this.niceType = niceType;
    }

    public String getNiceType() {
        return this.niceType;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Link getThumbnailLink() {
        return this.thumbnailLink;
    }

    public void setThumbnailLink(Link thumbnailLink) {
        this.thumbnailLink = thumbnailLink;
    }

    public int getThumbnailWidth() {
        return this.thumbnailWidth;
    }

    public void setThumbnailWidth(int thumbnailWidth) {
        this.thumbnailWidth = thumbnailWidth;
    }

    public int getThumbnailHeight() {
        return this.thumbnailHeight;
    }

    public void setThumbnailHeight(int thumbnailHeight) {
        this.thumbnailHeight = thumbnailHeight;
    }

    public String getWikiLink() {
        return this.wikiLink;
    }

    public void setWikiLink(String wikiLink) {
        this.wikiLink = wikiLink;
    }

    public SpaceEntity getSpace() {
        return this.space;
    }

    public void setSpace(SpaceEntity space) {
        this.space = space;
    }

    public DateEntity getLastModifiedDate() {
        return this.lastModifiedDate;
    }

    public void setLastModifiedDate(DateEntity lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public DateEntity getCreatedDate() {
        return this.createdDate;
    }

    public void setCreatedDate(DateEntity createdDate) {
        this.createdDate = createdDate;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
        this.title = fileName;
    }

    public void addLink(Link link) {
        this.getLinks().add(link);
    }

    public List<Link> getLinks() {
        if (this.links == null) {
            this.links = new ArrayList<Link>();
        }
        return this.links;
    }

    public String getIconClass() {
        return this.iconClass;
    }

    public void setIconClass(String iconClass) {
        this.iconClass = iconClass;
    }

    public String getFileExtension() {
        String fileName = this.getFileName();
        if (fileName == null) {
            return "";
        }
        int indexOfDot = fileName.lastIndexOf(".");
        if (indexOfDot == -1) {
            return "";
        }
        return fileName.substring(indexOfDot + 1).toLowerCase();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", AttachmentEntity.class.getSimpleName() + "[", "]").add("id='" + this.id + "'").add("type='attachment'").add("ownerId='" + this.ownerId + "'").add("parentTitle='" + this.parentTitle + "'").add("parentContentType='" + this.parentContentType + "'").add("parentDatePath='" + this.parentDatePath + "'").add("fileName='" + this.fileName + "'").add("contentType='" + this.contentType + "'").add("fileSize=" + this.fileSize).add("niceFileSize='" + this.niceFileSize + "'").add("comment='" + this.comment + "'").add("version=" + this.version).add("niceType='" + this.niceType + "'").add("iconClass='" + this.iconClass + "'").add("links=" + this.links).add("title='" + this.title + "'").add("thumbnailLink=" + this.thumbnailLink).add("thumbnailWidth=" + this.thumbnailWidth).add("thumbnailHeight=" + this.thumbnailHeight).add("wikiLink='" + this.wikiLink + "'").add("space=" + this.space).add("lastModifiedDate=" + this.lastModifiedDate).add("createdDate=" + this.createdDate).toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AttachmentEntity)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        AttachmentEntity that = (AttachmentEntity)o;
        return this.fileSize == that.fileSize && this.version == that.version && this.thumbnailWidth == that.thumbnailWidth && this.thumbnailHeight == that.thumbnailHeight && Objects.equals(this.ownerId, that.ownerId) && Objects.equals(this.parentTitle, that.parentTitle) && Objects.equals(this.parentContentType, that.parentContentType) && Objects.equals(this.parentDatePath, that.parentDatePath) && Objects.equals(this.fileName, that.fileName) && Objects.equals(this.contentType, that.contentType) && Objects.equals(this.niceFileSize, that.niceFileSize) && Objects.equals(this.comment, that.comment) && Objects.equals(this.niceType, that.niceType) && Objects.equals(this.iconClass, that.iconClass) && Objects.equals(this.links, that.links) && Objects.equals(this.title, that.title) && Objects.equals(this.thumbnailLink, that.thumbnailLink) && Objects.equals(this.wikiLink, that.wikiLink) && Objects.equals(this.space, that.space) && Objects.equals(this.lastModifiedDate, that.lastModifiedDate) && Objects.equals(this.createdDate, that.createdDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), "attachment", this.ownerId, this.parentTitle, this.parentContentType, this.parentDatePath, this.fileName, this.contentType, this.fileSize, this.niceFileSize, this.comment, this.version, this.niceType, this.iconClass, this.links, this.title, this.thumbnailLink, this.thumbnailWidth, this.thumbnailHeight, this.wikiLink, this.space, this.lastModifiedDate, this.createdDate);
    }
}

