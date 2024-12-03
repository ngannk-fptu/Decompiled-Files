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
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class GroupAttributeStoredEvent
extends GroupUpdatedEvent {
    private final Map<String, Set<String>> attributes;

    public GroupAttributeStoredEvent(Object source, Directory directory, Group group, Map<String, Set<String>> attributes) {
        super(source, directory, group);
        this.attributes = attributes;
    }

    public Set<String> getAttributeNames() {
        return Collections.unmodifiableSet(this.attributes.keySet());
    }

    public Set<String> getAttributeValues(String key) {
        return Collections.unmodifiableSet(this.attributes.get(key));
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
        GroupAttributeStoredEvent that = (GroupAttributeStoredEvent)o;
        return Objects.equals(this.attributes, that.attributes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.attributes);
    }
}

