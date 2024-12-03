/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 */
package com.atlassian.crowd.event.group;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.event.DirectoryEvent;
import java.util.Objects;

public class GroupDeletedEvent
extends DirectoryEvent {
    private final String groupName;

    public GroupDeletedEvent(Object source, Directory directory, String groupName) {
        super(source, directory);
        this.groupName = groupName;
    }

    public String getGroupName() {
        return this.groupName;
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
        GroupDeletedEvent that = (GroupDeletedEvent)o;
        return Objects.equals(this.groupName, that.groupName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.groupName);
    }
}

