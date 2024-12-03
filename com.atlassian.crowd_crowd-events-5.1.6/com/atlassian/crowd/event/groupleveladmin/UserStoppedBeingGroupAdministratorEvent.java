/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.User
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.group.ImmutableGroup
 *  com.google.common.base.MoreObjects
 */
package com.atlassian.crowd.event.groupleveladmin;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.group.ImmutableGroup;
import com.google.common.base.MoreObjects;
import java.util.Objects;

public class UserStoppedBeingGroupAdministratorEvent {
    private final ImmutableGroup group;
    private final User user;

    public UserStoppedBeingGroupAdministratorEvent(Group group, User user) {
        this.group = ImmutableGroup.from((Group)group);
        this.user = user;
    }

    public Group getGroup() {
        return this.group;
    }

    public User getUser() {
        return this.user;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        UserStoppedBeingGroupAdministratorEvent that = (UserStoppedBeingGroupAdministratorEvent)o;
        return Objects.equals(this.group, that.group) && Objects.equals(this.user, that.user);
    }

    public int hashCode() {
        return Objects.hash(this.group, this.user);
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("group", (Object)this.group).add("user", (Object)this.user).toString();
    }
}

