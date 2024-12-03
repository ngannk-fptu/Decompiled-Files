/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.Link
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.plugins.rest.entities;

import com.atlassian.plugins.rest.common.Link;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="user")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class UserEntity {
    @XmlElement(name="links")
    private List<Link> links;
    @XmlElement(name="name")
    private String username;
    @XmlElement(name="displayName")
    private String fullName;
    @XmlElement(name="avatarUrl")
    private String avatarUrl;
    @XmlElement(name="displayableEmail")
    private String displayableEmail;
    @XmlElement(name="anonymous")
    private boolean anonymous;

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return this.fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAvatarUrl() {
        return this.avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getDisplayableEmail() {
        return this.displayableEmail;
    }

    public void setDisplayableEmail(String displayableEmail) {
        this.displayableEmail = displayableEmail;
    }

    public boolean isAnonymous() {
        return this.anonymous;
    }

    public void setAnonymous(boolean anonymous) {
        this.anonymous = anonymous;
    }

    public void addLink(Link link) {
        if (this.links == null) {
            this.links = new ArrayList<Link>();
        }
        this.links.add(link);
    }

    public String toString() {
        return new StringJoiner(", ", UserEntity.class.getSimpleName() + "[", "]").add("links=" + this.links).add("username='" + this.username + "'").add("fullName='" + this.fullName + "'").add("avatarUrl='" + this.avatarUrl + "'").add("displayableEmail='" + this.displayableEmail + "'").add("anonymous=" + this.anonymous).toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        UserEntity that = (UserEntity)o;
        if (this.anonymous != that.anonymous) {
            return false;
        }
        if (this.avatarUrl != null ? !this.avatarUrl.equals(that.avatarUrl) : that.avatarUrl != null) {
            return false;
        }
        if (this.displayableEmail != null ? !this.displayableEmail.equals(that.displayableEmail) : that.displayableEmail != null) {
            return false;
        }
        if (this.fullName != null ? !this.fullName.equals(that.fullName) : that.fullName != null) {
            return false;
        }
        if (this.links != null ? !this.links.equals(that.links) : that.links != null) {
            return false;
        }
        return !(this.username != null ? !this.username.equals(that.username) : that.username != null);
    }

    public int hashCode() {
        int result = this.links != null ? this.links.hashCode() : 0;
        result = 31 * result + (this.username != null ? this.username.hashCode() : 0);
        result = 31 * result + (this.fullName != null ? this.fullName.hashCode() : 0);
        result = 31 * result + (this.avatarUrl != null ? this.avatarUrl.hashCode() : 0);
        result = 31 * result + (this.displayableEmail != null ? this.displayableEmail.hashCode() : 0);
        result = 31 * result + (this.anonymous ? 1 : 0);
        return result;
    }
}

