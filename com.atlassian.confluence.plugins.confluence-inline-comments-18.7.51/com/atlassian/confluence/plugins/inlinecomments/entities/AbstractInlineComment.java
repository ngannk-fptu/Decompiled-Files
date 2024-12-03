/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.people.Person
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElement
 */
package com.atlassian.confluence.plugins.inlinecomments.entities;

import com.atlassian.confluence.api.model.people.Person;
import com.atlassian.confluence.plugins.inlinecomments.utils.InlineCommentUtils;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(value=XmlAccessType.FIELD)
public abstract class AbstractInlineComment {
    @XmlElement
    private long id;
    @XmlElement
    private int containerVersion;
    @XmlElement
    private String authorDisplayName;
    @XmlElement
    private String authorUserName;
    @XmlElement
    private String authorAvatarUrl;
    @XmlElement
    private String body;
    @XmlElement
    private boolean hasDeletePermission;
    @XmlElement
    private boolean hasEditPermission;
    @XmlElement
    private String lastModificationDate;
    @XmlElement
    private String commentDateUrl;

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getContainerVersion() {
        return this.containerVersion;
    }

    public void setContainerVersion(int version) {
        this.containerVersion = version;
    }

    public String getAuthorDisplayName() {
        return this.authorDisplayName;
    }

    public void setAuthorDisplayName(String authorDisplayName) {
        this.authorDisplayName = authorDisplayName;
    }

    public String getAuthorUserName() {
        return this.authorUserName;
    }

    public void setAuthorUserName(String authorUserName) {
        this.authorUserName = authorUserName;
    }

    public String getAuthorAvatarUrl() {
        return this.authorAvatarUrl;
    }

    public void setAuthorAvatarUrl(String authorAvatarUrl) {
        this.authorAvatarUrl = authorAvatarUrl;
    }

    public String getBody() {
        return this.body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public boolean getHasDeletePermission() {
        return this.hasDeletePermission;
    }

    public void setHasDeletePermission(boolean hasDeletePermission) {
        this.hasDeletePermission = hasDeletePermission;
    }

    public boolean getHasEditPermission() {
        return this.hasEditPermission;
    }

    public void setHasEditPermission(boolean hasEditPermission) {
        this.hasEditPermission = hasEditPermission;
    }

    public String getLastModificationDate() {
        return this.lastModificationDate;
    }

    public void setLastModificationDate(String lastModificationDate) {
        this.lastModificationDate = lastModificationDate;
    }

    public String getCommentDateUrl() {
        return this.commentDateUrl;
    }

    public void setCommentDateUrl(String commentDateUrl) {
        this.commentDateUrl = commentDateUrl;
    }

    public void setAuthorInformation(Person person) {
        if (person != null) {
            this.setAuthorDisplayName(InlineCommentUtils.getDisplayName(person));
            this.setAuthorUserName(InlineCommentUtils.getUserName(person));
            this.setAuthorAvatarUrl(InlineCommentUtils.getUserAvatarUrl(person));
        }
    }
}

