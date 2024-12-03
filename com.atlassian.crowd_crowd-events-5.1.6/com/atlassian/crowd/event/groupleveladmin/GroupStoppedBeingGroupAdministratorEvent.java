/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.group.ImmutableGroup
 *  com.google.common.base.MoreObjects
 */
package com.atlassian.crowd.event.groupleveladmin;

import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.group.ImmutableGroup;
import com.google.common.base.MoreObjects;
import java.util.Objects;

public class GroupStoppedBeingGroupAdministratorEvent {
    private final ImmutableGroup administeringGroup;
    private final ImmutableGroup administeredGroup;

    public GroupStoppedBeingGroupAdministratorEvent(Group administeringGroup, Group administeredGroup) {
        this.administeringGroup = ImmutableGroup.from((Group)administeringGroup);
        this.administeredGroup = ImmutableGroup.from((Group)administeredGroup);
    }

    public Group getAdministeringGroup() {
        return this.administeringGroup;
    }

    public Group getAdministeredGroup() {
        return this.administeredGroup;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        GroupStoppedBeingGroupAdministratorEvent that = (GroupStoppedBeingGroupAdministratorEvent)o;
        return Objects.equals(this.administeringGroup, that.administeringGroup) && Objects.equals(this.administeredGroup, that.administeredGroup);
    }

    public int hashCode() {
        return Objects.hash(this.administeringGroup, this.administeredGroup);
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("administeringGroup", (Object)this.administeringGroup).add("administeredGroup", (Object)this.administeredGroup).toString();
    }
}

