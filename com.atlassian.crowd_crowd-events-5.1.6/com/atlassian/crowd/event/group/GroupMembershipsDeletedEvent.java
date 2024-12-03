/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.model.membership.MembershipType
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.crowd.event.group;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.event.DirectoryEvent;
import com.atlassian.crowd.model.membership.MembershipType;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class GroupMembershipsDeletedEvent
extends DirectoryEvent {
    private final List<String> entityNames;
    private final String groupName;
    private final MembershipType membershipType;

    public GroupMembershipsDeletedEvent(Object source, Directory directory, Iterable<String> entityNames, String groupName, MembershipType membershipType) {
        super(source, directory);
        this.entityNames = ImmutableList.copyOf(entityNames);
        this.groupName = groupName;
        this.membershipType = membershipType;
    }

    public Collection<String> getEntityNames() {
        return this.entityNames;
    }

    public String getGroupName() {
        return this.groupName;
    }

    public MembershipType getMembershipType() {
        return this.membershipType;
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
        GroupMembershipsDeletedEvent that = (GroupMembershipsDeletedEvent)o;
        return Objects.equals(this.entityNames, that.entityNames) && Objects.equals(this.groupName, that.groupName) && this.membershipType == that.membershipType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.entityNames, this.groupName, this.membershipType);
    }
}

