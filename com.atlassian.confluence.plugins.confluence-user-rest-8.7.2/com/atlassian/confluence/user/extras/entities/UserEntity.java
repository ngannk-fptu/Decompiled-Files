/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.user.extras.entities;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="user")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class UserEntity {
    @XmlElement
    private String username;
    @XmlElement
    private String displayName;
    @XmlElement
    private boolean active;

    private UserEntity(String username, String displayName, boolean active) {
        this.username = username;
        this.displayName = displayName;
        this.active = active;
    }

    public String getUsername() {
        return this.username;
    }

    public boolean isActive() {
        return this.active;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String username;
        private String displayName;
        private boolean active;

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder displayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder active(boolean active) {
            this.active = active;
            return this;
        }

        public UserEntity build() {
            return new UserEntity(this.username, this.displayName, this.active);
        }
    }
}

