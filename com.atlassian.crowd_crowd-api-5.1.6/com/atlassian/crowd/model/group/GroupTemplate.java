/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.group.GroupComparator
 *  com.atlassian.crowd.model.group.GroupType
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.Validate
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.crowd.model.group;

import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.group.GroupComparator;
import com.atlassian.crowd.model.group.GroupType;
import java.io.Serializable;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class GroupTemplate
implements Group,
Serializable {
    private String name;
    private long directoryId;
    private GroupType type;
    private boolean local;
    private boolean active;
    private String description;
    private String externalId;

    public GroupTemplate(String name, long directoryId, GroupType type) {
        Validate.isTrue((boolean)StringUtils.isNotBlank((CharSequence)name), (String)"name argument cannot be null or blank", (Object[])new Object[0]);
        Validate.notNull((Object)type, (String)"type argument cannot be null", (Object[])new Object[0]);
        this.name = name;
        this.directoryId = directoryId;
        this.type = type;
        this.active = true;
    }

    public GroupTemplate(String name) {
        this(name, -1L);
    }

    public GroupTemplate(String name, long directoryId) {
        this(name, directoryId, GroupType.GROUP);
    }

    public GroupTemplate(Group group) {
        Validate.notNull((Object)group, (String)"group argument cannot be null", (Object[])new Object[0]);
        Validate.isTrue((boolean)StringUtils.isNotBlank((CharSequence)group.getName()), (String)"group.name argument cannot be null or blank", (Object[])new Object[0]);
        Validate.notNull((Object)group.getType(), (String)"group.type argument cannot be null", (Object[])new Object[0]);
        this.name = group.getName();
        this.directoryId = group.getDirectoryId();
        this.active = group.isActive();
        this.type = group.getType();
        this.description = group.getDescription();
        this.externalId = group.getExternalId();
    }

    public GroupTemplate withDirectoryId(long directoryId) {
        GroupTemplate copy = new GroupTemplate(this);
        copy.setDirectoryId(directoryId);
        return copy;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDirectoryId(long directoryId) {
        this.directoryId = directoryId;
    }

    public long getDirectoryId() {
        return this.directoryId;
    }

    public String getName() {
        return this.name;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public GroupType getType() {
        return this.type;
    }

    public void setType(GroupType type) {
        this.type = type;
    }

    public boolean isLocal() {
        return this.local;
    }

    public void setLocal(boolean local) {
        this.local = local;
    }

    @Nullable
    public String getExternalId() {
        return this.externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
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

    public String toString() {
        return new ToStringBuilder((Object)this).append("name", (Object)this.name).append("directoryId", this.directoryId).append("active", this.active).append("type", (Object)this.type).append("description", (Object)this.description).append("externalId", (Object)this.externalId).toString();
    }
}

