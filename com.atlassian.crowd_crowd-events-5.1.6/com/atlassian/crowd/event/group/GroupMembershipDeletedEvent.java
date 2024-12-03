/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.model.membership.MembershipType
 */
package com.atlassian.crowd.event.group;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.event.DirectoryEvent;
import com.atlassian.crowd.model.membership.MembershipType;
import java.util.Objects;

public class GroupMembershipDeletedEvent
extends DirectoryEvent {
    private final String entityName;
    private final String groupName;
    private final MembershipType membershipType;

    public GroupMembershipDeletedEvent(Object source, Directory directory, String entityName, String groupName, MembershipType membershipType) {
        super(source, directory);
        this.entityName = entityName;
        this.membershipType = membershipType;
        this.groupName = groupName;
    }

    public String getEntityName() {
        return this.entityName;
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
        GroupMembershipDeletedEvent that = (GroupMembershipDeletedEvent)o;
        return Objects.equals(this.entityName, that.entityName) && Objects.equals(this.groupName, that.groupName) && this.membershipType == that.membershipType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.entityName, this.groupName, this.membershipType);
    }
}

