/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.user;

import java.util.List;
import lombok.Generated;

public class GroupConflictsCheckRequest {
    private final List<String> groups;

    @Generated
    public GroupConflictsCheckRequest(List<String> groups) {
        this.groups = groups;
    }

    @Generated
    public List<String> getGroups() {
        return this.groups;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof GroupConflictsCheckRequest)) {
            return false;
        }
        GroupConflictsCheckRequest other = (GroupConflictsCheckRequest)o;
        if (!other.canEqual(this)) {
            return false;
        }
        List<String> this$groups = this.getGroups();
        List<String> other$groups = other.getGroups();
        return !(this$groups == null ? other$groups != null : !((Object)this$groups).equals(other$groups));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof GroupConflictsCheckRequest;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        List<String> $groups = this.getGroups();
        result = result * 59 + ($groups == null ? 43 : ((Object)$groups).hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "GroupConflictsCheckRequest(groups=" + this.getGroups() + ")";
    }
}

