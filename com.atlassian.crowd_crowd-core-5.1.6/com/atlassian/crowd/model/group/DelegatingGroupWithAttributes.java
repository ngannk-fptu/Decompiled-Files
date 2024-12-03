/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Attributes
 *  com.atlassian.crowd.embedded.impl.AbstractDelegatingEntityWithAttributes
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.group.GroupComparator
 *  com.atlassian.crowd.model.group.GroupType
 *  com.atlassian.crowd.model.group.GroupWithAttributes
 *  javax.annotation.Nullable
 */
package com.atlassian.crowd.model.group;

import com.atlassian.crowd.embedded.api.Attributes;
import com.atlassian.crowd.embedded.impl.AbstractDelegatingEntityWithAttributes;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.group.GroupComparator;
import com.atlassian.crowd.model.group.GroupType;
import com.atlassian.crowd.model.group.GroupWithAttributes;
import javax.annotation.Nullable;

public class DelegatingGroupWithAttributes
extends AbstractDelegatingEntityWithAttributes
implements GroupWithAttributes {
    private final Group group;

    public DelegatingGroupWithAttributes(Group group, Attributes attributes) {
        super(attributes);
        this.group = group;
    }

    public long getDirectoryId() {
        return this.group.getDirectoryId();
    }

    public String getName() {
        return this.group.getName();
    }

    public boolean isActive() {
        return this.group.isActive();
    }

    public String getDescription() {
        return this.group.getDescription();
    }

    public GroupType getType() {
        return this.group.getType();
    }

    @Nullable
    public String getExternalId() {
        return this.group.getExternalId();
    }

    public boolean equals(Object o) {
        return GroupComparator.equalsObject((Group)this, (Object)o);
    }

    public int hashCode() {
        return GroupComparator.hashCode((Group)this);
    }

    public int compareTo(Group other) {
        return GroupComparator.compareTo((Group)this, (Group)other);
    }
}

