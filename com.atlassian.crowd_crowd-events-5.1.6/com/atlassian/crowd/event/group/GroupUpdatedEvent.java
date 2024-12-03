/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.group.ImmutableGroup
 */
package com.atlassian.crowd.event.group;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.event.DirectoryEvent;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.group.ImmutableGroup;
import java.util.Objects;

public class GroupUpdatedEvent
extends DirectoryEvent {
    private final ImmutableGroup group;

    public GroupUpdatedEvent(Object source, Directory directory, Group group) {
        super(source, directory);
        this.group = ImmutableGroup.from((Group)group);
    }

    public Group getGroup() {
        return this.group;
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
        GroupUpdatedEvent that = (GroupUpdatedEvent)o;
        return Objects.equals(this.group, that.group);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.group);
    }
}

