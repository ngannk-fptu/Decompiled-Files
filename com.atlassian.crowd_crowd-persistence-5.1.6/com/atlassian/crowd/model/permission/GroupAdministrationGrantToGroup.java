/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 */
package com.atlassian.crowd.model.permission;

import com.atlassian.crowd.model.group.InternalGroup;
import com.google.common.base.MoreObjects;
import java.util.Objects;

public class GroupAdministrationGrantToGroup {
    private Long id;
    private InternalGroup group;
    private InternalGroup targetGroup;

    public GroupAdministrationGrantToGroup() {
    }

    public GroupAdministrationGrantToGroup(InternalGroup group, InternalGroup targetGroup) {
        this.group = group;
        this.targetGroup = targetGroup;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public InternalGroup getGroup() {
        return this.group;
    }

    public void setGroup(InternalGroup group) {
        this.group = group;
    }

    public InternalGroup getTargetGroup() {
        return this.targetGroup;
    }

    public void setTargetGroup(InternalGroup targetGroup) {
        this.targetGroup = targetGroup;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        GroupAdministrationGrantToGroup that = (GroupAdministrationGrantToGroup)o;
        return Objects.equals(this.id, that.id) && Objects.equals((Object)this.group, (Object)that.group) && Objects.equals((Object)this.targetGroup, (Object)that.targetGroup);
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.id, this.group, this.targetGroup});
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("id", (Object)this.id).add("group", (Object)this.group).add("targetGroup", (Object)this.targetGroup).toString();
    }
}

