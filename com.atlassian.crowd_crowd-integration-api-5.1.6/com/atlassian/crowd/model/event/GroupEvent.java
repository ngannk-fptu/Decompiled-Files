/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 */
package com.atlassian.crowd.model.event;

import com.atlassian.crowd.model.event.AbstractAttributeEvent;
import com.atlassian.crowd.model.event.Operation;
import com.atlassian.crowd.model.group.Group;
import com.google.common.base.MoreObjects;
import java.util.Map;
import java.util.Set;

public class GroupEvent
extends AbstractAttributeEvent {
    private final Group group;

    public GroupEvent(Operation operation, Long directoryId, Group group, Map<String, Set<String>> storedAttributes, Set<String> deletedAttributes) {
        super(operation, directoryId, storedAttributes, deletedAttributes);
        this.group = group;
    }

    public Group getGroup() {
        return this.group;
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("operation", (Object)this.getOperation()).add("group", this.group == null ? null : this.group.getName()).add("directory", (Object)this.getDirectoryId()).toString();
    }
}

