/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.Link
 *  com.atlassian.plugins.rest.common.expand.Expandable
 *  com.atlassian.plugins.rest.common.expand.Expander
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserKeyXmlAdapter
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 *  javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter
 */
package com.atlassian.confluence.plugins.rest.entities;

import com.atlassian.confluence.plugins.rest.entities.AttachmentEntityList;
import com.atlassian.confluence.plugins.rest.entities.CommentEntityTree;
import com.atlassian.confluence.plugins.rest.entities.ContentBodyEntity;
import com.atlassian.confluence.plugins.rest.entities.ContentEntityExpander;
import com.atlassian.confluence.plugins.rest.entities.ContentEntityList;
import com.atlassian.confluence.plugins.rest.entities.DateEntity;
import com.atlassian.confluence.plugins.rest.entities.LabelEntityList;
import com.atlassian.confluence.plugins.rest.entities.SearchResultEntity;
import com.atlassian.confluence.plugins.rest.entities.SpaceEntity;
import com.atlassian.confluence.plugins.rest.entities.UserEntity;
import com.atlassian.plugins.rest.common.Link;
import com.atlassian.plugins.rest.common.expand.Expandable;
import com.atlassian.plugins.rest.common.expand.Expander;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserKeyXmlAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name="content")
@XmlAccessorType(value=XmlAccessType.FIELD)
@Expander(value=ContentEntityExpander.class)
public class ContentEntity
extends SearchResultEntity {
    @XmlAttribute
    private String expand;
    @XmlAttribute
    private String type;
    @XmlElement(name="link")
    private List<Link> links;
    @XmlElement
    private String title;
    @XmlElement
    private String parentId;
    @XmlElement
    private String wikiLink;
    @XmlElement(name="lastModifiedDate")
    private DateEntity lastModifiedDate;
    @XmlElement(name="createdDate")
    private DateEntity createdDate;
    @XmlElement(name="space")
    @Expandable(value="space")
    private SpaceEntity space;
    @XmlElement(name="children")
    @Expandable(value="children")
    private ContentEntityList children;
    @XmlElement(name="comments")
    @Expandable(value="comments")
    private CommentEntityTree comments;
    @XmlElement(name="body")
    private ContentBodyEntity contentBodyEntity;
    @XmlElement(name="attachments")
    @Expandable(value="attachments")
    private AttachmentEntityList attachments;
    @XmlElement(name="labels")
    @Expandable(value="labels")
    private LabelEntityList labels;
    @XmlElement(name="creator")
    private UserEntity creator;
    @XmlElement(name="lastModifier")
    private UserEntity lastModifier;
    @XmlElement(name="iconClass")
    private String iconClass;
    @XmlElement
    private String username;
    @XmlElement
    @XmlJavaTypeAdapter(value=UserKeyXmlAdapter.class)
    private UserKey userKey;
    @XmlElement
    private Link thumbnailLink;

    public String getParentId() {
        return this.parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ContentBodyEntity getContentBody() {
        return this.contentBodyEntity;
    }

    public void setContentBody(ContentBodyEntity contentBodyEntity) {
        this.contentBodyEntity = contentBodyEntity;
    }

    public ContentEntityList getChildren() {
        return this.children;
    }

    public void setChildren(ContentEntityList children) {
        this.children = children;
    }

    public CommentEntityTree getComments() {
        return this.comments;
    }

    public void setComments(CommentEntityTree comments) {
        this.comments = comments;
    }

    public String getWikiLink() {
        return this.wikiLink;
    }

    public void setWikiLink(String wikiLink) {
        this.wikiLink = wikiLink;
    }

    public void setLastModifiedDate(DateEntity lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public DateEntity getLastModifiedDate() {
        return this.lastModifiedDate;
    }

    public void setSpace(SpaceEntity space) {
        this.space = space;
    }

    public SpaceEntity getSpace() {
        return this.space;
    }

    public Link getThumbnailLink() {
        return this.thumbnailLink;
    }

    public void setThumbnailLink(Link thumbnailLink) {
        this.thumbnailLink = thumbnailLink;
    }

    public void setAttachments(AttachmentEntityList attachmentEntityList) {
        this.attachments = attachmentEntityList;
    }

    public AttachmentEntityList getAttachments() {
        return this.attachments;
    }

    public LabelEntityList getLabels() {
        return this.labels;
    }

    public void setLabels(LabelEntityList labels) {
        this.labels = labels;
    }

    public void setCreatedDate(DateEntity date) {
        this.createdDate = date;
    }

    public DateEntity getCreatedDate() {
        return this.createdDate;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return this.username;
    }

    public UserKey getUserKey() {
        return this.userKey;
    }

    public void setUserKey(UserKey userKey) {
        this.userKey = userKey;
    }

    public UserEntity getCreator() {
        return this.creator;
    }

    public void setCreator(UserEntity creator) {
        this.creator = creator;
    }

    public UserEntity getLastModifier() {
        return this.lastModifier;
    }

    public void setLastModifier(UserEntity lastModifier) {
        this.lastModifier = lastModifier;
    }

    public String getIconClass() {
        return this.iconClass;
    }

    public void setIconClass(String iconClass) {
        this.iconClass = iconClass;
    }

    public void addLink(Link link) {
        if (this.links == null) {
            this.links = new ArrayList<Link>();
        }
        this.links.add(link);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ContentEntity.class.getSimpleName() + "[", "]").add("expand='" + this.expand + "'").add("id='" + this.id + "'").add("type='" + this.type + "'").add("links=" + this.links).add("title='" + this.title + "'").add("parentId='" + this.parentId + "'").add("wikiLink='" + this.wikiLink + "'").add("lastModifiedDate=" + this.lastModifiedDate).add("createdDate=" + this.createdDate).add("space=" + this.space).add("children=" + this.children).add("comments=" + this.comments).add("contentBodyEntity=" + this.contentBodyEntity).add("attachments=" + this.attachments).add("labels=" + this.labels).add("creator=" + this.creator).add("lastModifier=" + this.lastModifier).add("iconClass='" + this.iconClass + "'").add("username='" + this.username + "'").add("userKey=" + this.userKey).add("thumbnailLink=" + this.thumbnailLink).toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ContentEntity)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        ContentEntity that = (ContentEntity)o;
        return Objects.equals(this.expand, that.expand) && Objects.equals(this.type, that.type) && Objects.equals(this.links, that.links) && Objects.equals(this.title, that.title) && Objects.equals(this.parentId, that.parentId) && Objects.equals(this.wikiLink, that.wikiLink) && Objects.equals(this.lastModifiedDate, that.lastModifiedDate) && Objects.equals(this.createdDate, that.createdDate) && Objects.equals(this.space, that.space) && Objects.equals(this.children, that.children) && Objects.equals(this.comments, that.comments) && Objects.equals(this.contentBodyEntity, that.contentBodyEntity) && Objects.equals(this.attachments, that.attachments) && Objects.equals(this.labels, that.labels) && Objects.equals(this.creator, that.creator) && Objects.equals(this.lastModifier, that.lastModifier) && Objects.equals(this.iconClass, that.iconClass) && Objects.equals(this.username, that.username) && Objects.equals(this.userKey, that.userKey) && Objects.equals(this.thumbnailLink, that.thumbnailLink);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.expand, this.type, this.links, this.title, this.parentId, this.wikiLink, this.lastModifiedDate, this.createdDate, this.space, this.children, this.comments, this.contentBodyEntity, this.attachments, this.labels, this.creator, this.lastModifier, this.iconClass, this.username, this.userKey, this.thumbnailLink);
    }
}

