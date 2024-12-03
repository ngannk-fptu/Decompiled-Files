/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.service.stepexecutor.space;

import com.atlassian.migration.agent.service.impl.MigrationUser;
import lombok.Generated;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class TombstoneUser {
    private String aaid;
    private final String userName;
    private final String userKey;
    private final String email;
    private final String displayName;

    @JsonCreator
    public TombstoneUser(@JsonProperty(value="aaid") String aaid, @JsonProperty(value="userName") String userName, @JsonProperty(value="userKey") String userKey, @JsonProperty(value="email") String email, @JsonProperty(value="displayName") String displayName) {
        this.aaid = aaid;
        this.userName = userName;
        this.userKey = userKey;
        this.email = email;
        this.displayName = displayName;
    }

    public TombstoneUser(String userName, String userKey, String email, String displayName) {
        this(null, userName, userKey, email, displayName);
    }

    public static TombstoneUser fromTombstoneUserWithAaid(TombstoneUser other, String aaid) {
        return new TombstoneUser(aaid, other.getUserName(), other.getUserKey(), other.getEmail(), other.getDisplayName());
    }

    public static TombstoneUser fromMigrationUser(MigrationUser other) {
        return new TombstoneUser(other.getUsername(), other.getUserKey(), other.getEmail(), other.getFullName());
    }

    @Generated
    public String getAaid() {
        return this.aaid;
    }

    @Generated
    public String getUserName() {
        return this.userName;
    }

    @Generated
    public String getUserKey() {
        return this.userKey;
    }

    @Generated
    public String getEmail() {
        return this.email;
    }

    @Generated
    public String getDisplayName() {
        return this.displayName;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof TombstoneUser)) {
            return false;
        }
        TombstoneUser other = (TombstoneUser)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$userKey = this.getUserKey();
        String other$userKey = other.getUserKey();
        return !(this$userKey == null ? other$userKey != null : !this$userKey.equals(other$userKey));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof TombstoneUser;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $userKey = this.getUserKey();
        result = result * 59 + ($userKey == null ? 43 : $userKey.hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "TombstoneUser(aaid=" + this.getAaid() + ", userName=" + this.getUserName() + ", userKey=" + this.getUserKey() + ", email=" + this.getEmail() + ", displayName=" + this.getDisplayName() + ")";
    }

    @Generated
    public void setAaid(String aaid) {
        this.aaid = aaid;
    }
}

