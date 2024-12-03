/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 */
package com.atlassian.crowd.model.event;

import com.atlassian.crowd.model.event.AbstractOperationEvent;
import com.atlassian.crowd.model.event.Operation;
import com.google.common.base.MoreObjects;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

public class GroupMembershipEvent
extends AbstractOperationEvent {
    private final String groupName;
    private final Set<String> parentGroupNames;
    private final Set<String> childGroupNames;

    public GroupMembershipEvent(Operation operation, Long directoryId, String groupName, String parentGroupName) {
        super(operation, directoryId);
        this.groupName = groupName;
        this.parentGroupNames = Collections.singleton(parentGroupName);
        this.childGroupNames = Collections.emptySet();
    }

    public GroupMembershipEvent(Operation operation, Long directoryId, String groupName, Set<String> parentGroupNames, Set<String> childGroupNames) {
        super(operation, directoryId);
        this.groupName = groupName;
        this.parentGroupNames = parentGroupNames;
        this.childGroupNames = childGroupNames;
    }

    public String getGroupName() {
        return this.groupName;
    }

    public Set<String> getParentGroupNames() {
        return this.parentGroupNames;
    }

    public Set<String> getChildGroupNames() {
        return this.childGroupNames;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        GroupMembershipEvent that = (GroupMembershipEvent)o;
        return Objects.equals(this.groupName, that.groupName) && Objects.equals(this.parentGroupNames, that.parentGroupNames) && Objects.equals(this.childGroupNames, that.childGroupNames);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.groupName, this.parentGroupNames, this.childGroupNames);
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("operation", (Object)this.getOperation()).add("groupName", (Object)this.groupName).add("parentGroupNames", this.parentGroupNames).add("childGroupNames", this.childGroupNames).toString();
    }
}

