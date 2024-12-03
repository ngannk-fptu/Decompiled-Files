/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.attribute.AttributeUtil
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.group.GroupComparator
 *  com.atlassian.crowd.model.group.GroupType
 *  com.atlassian.crowd.model.group.GroupWithAttributes
 *  com.google.common.collect.Multimap
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.crowd.model.group;

import com.atlassian.crowd.attribute.AttributeUtil;
import com.atlassian.crowd.model.EntityWithAttributes;
import com.atlassian.crowd.model.InternalEntityAttribute;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.group.GroupComparator;
import com.atlassian.crowd.model.group.GroupType;
import com.atlassian.crowd.model.group.GroupWithAttributes;
import com.atlassian.crowd.model.group.InternalGroup;
import com.google.common.collect.Multimap;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class InternalGroupWithAttributes
extends EntityWithAttributes
implements GroupWithAttributes {
    private final InternalGroup group;

    public InternalGroupWithAttributes(InternalGroup group) {
        super((Multimap<String, String>)InternalEntityAttribute.toMap(group.getAttributes()));
        this.group = group;
    }

    public InternalGroupWithAttributes(InternalGroup group, Map<String, Set<String>> attributes) {
        super((Multimap<String, String>)AttributeUtil.toMultimap(attributes));
        this.group = group;
    }

    public long getDirectoryId() {
        return this.group.getDirectoryId();
    }

    public String getName() {
        return this.group.getName();
    }

    public GroupType getType() {
        return this.group.getType();
    }

    public boolean isActive() {
        return this.group.isActive();
    }

    public String getDescription() {
        return this.group.getDescription();
    }

    @Nullable
    public String getExternalId() {
        return this.group.getExternalId();
    }

    public InternalGroup getInternalGroup() {
        return this.group;
    }

    public boolean equals(Object o) {
        return GroupComparator.equalsObject((Group)this, (Object)o);
    }

    public int hashCode() {
        return GroupComparator.hashCode((Group)this);
    }

    public int compareTo(Group o) {
        return GroupComparator.compareTo((Group)this, (Group)o);
    }

    @Override
    public String toString() {
        return new ToStringBuilder((Object)this).append("group", (Object)this.group).toString();
    }
}

