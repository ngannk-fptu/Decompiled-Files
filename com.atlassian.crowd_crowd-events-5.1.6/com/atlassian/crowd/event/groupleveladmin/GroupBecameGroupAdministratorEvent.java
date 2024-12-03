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

public class GroupBecameGroupAdministratorEvent {
    private final ImmutableGroup administeredGroup;
    private final ImmutableGroup administrationGroup;

    public GroupBecameGroupAdministratorEvent(Group administeredGroup, Group administrationGroup) {
        this.administeredGroup = ImmutableGroup.from((Group)administeredGroup);
        this.administrationGroup = ImmutableGroup.from((Group)administrationGroup);
    }

    public Group getAdministeredGroup() {
        return this.administeredGroup;
    }

    public Group getAdministeringGroup() {
        return this.administrationGroup;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        GroupBecameGroupAdministratorEvent that = (GroupBecameGroupAdministratorEvent)o;
        return Objects.equals(this.administeredGroup, that.administeredGroup) && Objects.equals(this.administrationGroup, that.administrationGroup);
    }

    public int hashCode() {
        return Objects.hash(this.administeredGroup, this.administrationGroup);
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("administeredGroup", (Object)this.administeredGroup).add("administrationGroup", (Object)this.administrationGroup).toString();
    }
}

