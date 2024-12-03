/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.User
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.crowd.embedded.admin.dto;

import com.atlassian.crowd.embedded.api.User;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UserSyncPreviewUserDto {
    @XmlElement
    private String name;
    @XmlElement
    private String displayName;
    @XmlElement
    private String emailAddress;
    @XmlElement
    private String emailHash;

    public UserSyncPreviewUserDto() {
    }

    public UserSyncPreviewUserDto(User crowdUser) {
        this.name = crowdUser.getName();
        this.displayName = crowdUser.getDisplayName();
        this.emailAddress = crowdUser.getEmailAddress();
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmailAddress() {
        return this.emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getEmailHash() {
        return this.emailHash;
    }

    public void setEmailHash(String emailHash) {
        this.emailHash = emailHash;
    }
}

