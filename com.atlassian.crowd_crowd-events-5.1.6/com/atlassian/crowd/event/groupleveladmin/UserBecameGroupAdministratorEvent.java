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

public class UserBecameGroupAdministratorEvent {
    private final ImmutableGroup group;
    private final User administrator;

    public UserBecameGroupAdministratorEvent(Group group, User administrator) {
        this.group = ImmutableGroup.from((Group)group);
        this.administrator = administrator;
    }

    public Group getGroup() {
        return this.group;
    }

    public User getAdministrator() {
        return this.administrator;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        UserBecameGroupAdministratorEvent that = (UserBecameGroupAdministratorEvent)o;
        return Objects.equals(this.group, that.group) && Objects.equals(this.administrator, that.administrator);
    }

    public int hashCode() {
        return Objects.hash(this.group, this.administrator);
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("group", (Object)this.group).add("administrator", (Object)this.administrator).toString();
    }
}

