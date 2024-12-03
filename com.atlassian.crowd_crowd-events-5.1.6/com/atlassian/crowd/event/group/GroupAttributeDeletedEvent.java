/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.model.group.Group
 */
package com.atlassian.crowd.event.group;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.event.group.GroupUpdatedEvent;
import com.atlassian.crowd.model.group.Group;
import java.util.Objects;

public class GroupAttributeDeletedEvent
extends GroupUpdatedEvent {
    private final String attributeName;

    public GroupAttributeDeletedEvent(Object source, Directory directory, Group group, String attributeName) {
        super(source, directory, group);
        this.attributeName = attributeName;
    }

    public String getAttributeName() {
        return this.attributeName;
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
        GroupAttributeDeletedEvent that = (GroupAttributeDeletedEvent)o;
        return Objects.equals(this.attributeName, that.attributeName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.attributeName);
    }
}

