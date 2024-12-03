/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.model.event;

import com.atlassian.crowd.model.event.AbstractOperationEvent;
import com.atlassian.crowd.model.event.Operation;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

public class UserMembershipEvent
extends AbstractOperationEvent {
    private final String childUsername;
    private final Set<String> parentGroupNames;

    public UserMembershipEvent(Operation operation, Long directoryId, String childUsername, String groupName) {
        super(operation, directoryId);
        this.childUsername = childUsername;
        this.parentGroupNames = Collections.singleton(groupName);
    }

    public UserMembershipEvent(Operation operation, Long directoryId, String childUsername, Set<String> parentGroupNames) {
        super(operation, directoryId);
        this.childUsername = childUsername;
        this.parentGroupNames = parentGroupNames;
    }

    public Set<String> getParentGroupNames() {
        return this.parentGroupNames;
    }

    public String getChildUsername() {
        return this.childUsername;
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
        UserMembershipEvent that = (UserMembershipEvent)o;
        return Objects.equals(this.childUsername, that.childUsername) && Objects.equals(this.parentGroupNames, that.parentGroupNames);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.childUsername, this.parentGroupNames);
    }

    public String toString() {
        return "UserMembershipEvent{operation=" + (Object)((Object)this.getOperation()) + ",directory=" + this.getDirectoryId() + ",childUsername='" + this.childUsername + '\'' + ", parentGroupNames=" + this.parentGroupNames + '}';
    }
}

