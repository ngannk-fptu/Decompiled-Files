/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlRootElement
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 */
package com.atlassian.confluence.plugins.like.rest.entities;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@XmlRootElement
public class UserEntity {
    @XmlAttribute
    private String name;
    @XmlAttribute
    private String fullName;
    @XmlAttribute
    private String url;
    @XmlAttribute
    private String avatarUrl;
    @XmlAttribute
    private boolean followedByRemoteUser = false;

    public UserEntity() {
    }

    public UserEntity(String name) {
        this.name = name;
    }

    public UserEntity(String name, boolean followedByRemoteUser) {
        this.name = name;
        this.followedByRemoteUser = followedByRemoteUser;
    }

    public UserEntity(String name, String fullName, String url) {
        this.name = name;
        this.fullName = fullName;
        this.url = url;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return this.fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAvatarUrl() {
        return this.avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public boolean isFollowedByRemoteUser() {
        return this.followedByRemoteUser;
    }

    public void setFollowedByRemoteUser(boolean followedByRemoteUser) {
        this.followedByRemoteUser = followedByRemoteUser;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof UserEntity)) {
            return false;
        }
        UserEntity that = (UserEntity)obj;
        return EqualsBuilder.reflectionEquals((Object)this, (Object)that, (String[])new String[0]);
    }

    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode((Object)this, (String[])new String[0]);
    }
}

