/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.user.request.v2;

import com.atlassian.migration.agent.service.user.MigrationUserDto;
import com.atlassian.migration.agent.service.user.request.v2.MigrationGroupV2Dto;
import java.util.Collection;
import java.util.Map;
import lombok.Generated;

public class UsersMigrationV2FilePayload {
    private final Collection<MigrationUserDto> users;
    private final Collection<MigrationGroupV2Dto> groups;
    private final Map<String, Collection<String>> membership;

    @Generated
    public UsersMigrationV2FilePayload(Collection<MigrationUserDto> users, Collection<MigrationGroupV2Dto> groups, Map<String, Collection<String>> membership) {
        this.users = users;
        this.groups = groups;
        this.membership = membership;
    }

    @Generated
    public Collection<MigrationUserDto> getUsers() {
        return this.users;
    }

    @Generated
    public Collection<MigrationGroupV2Dto> getGroups() {
        return this.groups;
    }

    @Generated
    public Map<String, Collection<String>> getMembership() {
        return this.membership;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof UsersMigrationV2FilePayload)) {
            return false;
        }
        UsersMigrationV2FilePayload other = (UsersMigrationV2FilePayload)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Collection<MigrationUserDto> this$users = this.getUsers();
        Collection<MigrationUserDto> other$users = other.getUsers();
        if (this$users == null ? other$users != null : !((Object)this$users).equals(other$users)) {
            return false;
        }
        Collection<MigrationGroupV2Dto> this$groups = this.getGroups();
        Collection<MigrationGroupV2Dto> other$groups = other.getGroups();
        if (this$groups == null ? other$groups != null : !((Object)this$groups).equals(other$groups)) {
            return false;
        }
        Map<String, Collection<String>> this$membership = this.getMembership();
        Map<String, Collection<String>> other$membership = other.getMembership();
        return !(this$membership == null ? other$membership != null : !((Object)this$membership).equals(other$membership));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof UsersMigrationV2FilePayload;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Collection<MigrationUserDto> $users = this.getUsers();
        result = result * 59 + ($users == null ? 43 : ((Object)$users).hashCode());
        Collection<MigrationGroupV2Dto> $groups = this.getGroups();
        result = result * 59 + ($groups == null ? 43 : ((Object)$groups).hashCode());
        Map<String, Collection<String>> $membership = this.getMembership();
        result = result * 59 + ($membership == null ? 43 : ((Object)$membership).hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "UsersMigrationV2FilePayload(users=" + this.getUsers() + ", groups=" + this.getGroups() + ", membership=" + this.getMembership() + ")";
    }
}

