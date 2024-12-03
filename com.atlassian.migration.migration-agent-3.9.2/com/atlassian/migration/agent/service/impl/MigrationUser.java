/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.apache.commons.lang3.builder.ToStringBuilder
 *  org.apache.commons.lang3.builder.ToStringStyle
 */
package com.atlassian.migration.agent.service.impl;

import lombok.Generated;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class MigrationUser {
    private final String userKey;
    private final String username;
    private final String fullName;
    private final String email;
    private final boolean isActive;

    public MigrationUser(String userKey, String username, String fullName, String email, boolean isActive) {
        this.userKey = userKey;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.isActive = isActive;
    }

    public MigrationUser(MigrationUser other) {
        this(other.getUserKey(), other.getUsername(), other.getFullName(), other.getEmail(), other.isActive);
    }

    public String getUserKey() {
        return this.userKey;
    }

    public String getUsername() {
        return this.username;
    }

    public String getFullName() {
        return this.fullName;
    }

    public String getEmail() {
        return this.email;
    }

    public boolean isActive() {
        return this.isActive;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString((Object)this, (ToStringStyle)ToStringStyle.NO_CLASS_NAME_STYLE);
    }

    public static MigrationUser fromEmailAsActive(String email) {
        return new MigrationUser("", "", "", email, true);
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof MigrationUser)) {
            return false;
        }
        MigrationUser other = (MigrationUser)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$userKey = this.getUserKey();
        String other$userKey = other.getUserKey();
        if (this$userKey == null ? other$userKey != null : !this$userKey.equals(other$userKey)) {
            return false;
        }
        String this$username = this.getUsername();
        String other$username = other.getUsername();
        if (this$username == null ? other$username != null : !this$username.equals(other$username)) {
            return false;
        }
        String this$fullName = this.getFullName();
        String other$fullName = other.getFullName();
        if (this$fullName == null ? other$fullName != null : !this$fullName.equals(other$fullName)) {
            return false;
        }
        String this$email = this.getEmail();
        String other$email = other.getEmail();
        if (this$email == null ? other$email != null : !this$email.equals(other$email)) {
            return false;
        }
        return this.isActive() == other.isActive();
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof MigrationUser;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $userKey = this.getUserKey();
        result = result * 59 + ($userKey == null ? 43 : $userKey.hashCode());
        String $username = this.getUsername();
        result = result * 59 + ($username == null ? 43 : $username.hashCode());
        String $fullName = this.getFullName();
        result = result * 59 + ($fullName == null ? 43 : $fullName.hashCode());
        String $email = this.getEmail();
        result = result * 59 + ($email == null ? 43 : $email.hashCode());
        result = result * 59 + (this.isActive() ? 79 : 97);
        return result;
    }
}

