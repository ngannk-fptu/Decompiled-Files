/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.stepexecutor.space;

import lombok.Generated;

public class SpaceExportMigrationUser {
    public final String email;
    public final String username;
    public final String aaid;

    public SpaceExportMigrationUser(String email, String username, String aaid) {
        this.email = email;
        this.username = username;
        this.aaid = aaid;
    }

    @Generated
    public String getEmail() {
        return this.email;
    }

    @Generated
    public String getUsername() {
        return this.username;
    }

    @Generated
    public String getAaid() {
        return this.aaid;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof SpaceExportMigrationUser)) {
            return false;
        }
        SpaceExportMigrationUser other = (SpaceExportMigrationUser)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$email = this.getEmail();
        String other$email = other.getEmail();
        if (this$email == null ? other$email != null : !this$email.equals(other$email)) {
            return false;
        }
        String this$username = this.getUsername();
        String other$username = other.getUsername();
        if (this$username == null ? other$username != null : !this$username.equals(other$username)) {
            return false;
        }
        String this$aaid = this.getAaid();
        String other$aaid = other.getAaid();
        return !(this$aaid == null ? other$aaid != null : !this$aaid.equals(other$aaid));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof SpaceExportMigrationUser;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $email = this.getEmail();
        result = result * 59 + ($email == null ? 43 : $email.hashCode());
        String $username = this.getUsername();
        result = result * 59 + ($username == null ? 43 : $username.hashCode());
        String $aaid = this.getAaid();
        result = result * 59 + ($aaid == null ? 43 : $aaid.hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "SpaceExportMigrationUser(email=" + this.getEmail() + ", username=" + this.getUsername() + ", aaid=" + this.getAaid() + ")";
    }
}

