/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.group.GroupComparator
 *  com.atlassian.crowd.model.group.GroupType
 *  com.atlassian.crowd.model.group.GroupWithAttributes
 *  javax.annotation.Nullable
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.crowd.integration.rest.entity;

import com.atlassian.crowd.integration.rest.entity.MultiValuedAttributeEntityList;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.group.GroupComparator;
import com.atlassian.crowd.model.group.GroupType;
import com.atlassian.crowd.model.group.GroupWithAttributes;
import java.util.Collections;
import java.util.Set;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.commons.lang3.builder.ToStringBuilder;

@XmlRootElement(name="group")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class GroupEntity
implements GroupWithAttributes,
Comparable<Group> {
    @XmlAttribute(name="name")
    private String name;
    @XmlElement(name="description")
    private String description;
    @XmlElement
    private final GroupType type;
    @XmlElement(name="active")
    private boolean active;
    @XmlElement(name="attributes")
    @Nullable
    private MultiValuedAttributeEntityList attributes;

    private GroupEntity() {
        this.name = null;
        this.description = null;
        this.type = null;
        this.active = false;
    }

    public GroupEntity(String name, String description, GroupType type, boolean active) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.active = active;
    }

    public String getDescription() {
        return this.description;
    }

    public GroupType getType() {
        return this.type;
    }

    public boolean isActive() {
        return this.active;
    }

    public long getDirectoryId() {
        return 0L;
    }

    public String getName() {
        return this.name;
    }

    public void setAttributes(MultiValuedAttributeEntityList attributes) {
        this.attributes = attributes;
    }

    @Nullable
    public MultiValuedAttributeEntityList getAttributes() {
        return this.attributes;
    }

    @Nullable
    public String getExternalId() {
        return null;
    }

    public String toString() {
        return new ToStringBuilder((Object)this).append("name", (Object)this.getName()).append("active", this.isActive()).append("description", (Object)this.getDescription()).append("type", (Object)this.getType()).append("externalId", (Object)this.getExternalId()).append("attributes", (Object)this.getAttributes()).toString();
    }

    @Nullable
    public Set<String> getValues(String key) {
        return this.attributes != null ? this.attributes.getValues(key) : null;
    }

    @Nullable
    public String getValue(String key) {
        return this.attributes != null ? this.attributes.getValue(key) : null;
    }

    public Set<String> getKeys() {
        return this.attributes != null ? this.attributes.getKeys() : Collections.emptySet();
    }

    public boolean isEmpty() {
        return this.attributes == null || this.attributes.isEmpty();
    }

    @Override
    public int compareTo(Group o) {
        return GroupComparator.compareTo((Group)this, (Group)o);
    }

    public boolean equals(Object o) {
        return GroupComparator.equalsObject((Group)this, (Object)o);
    }

    public int hashCode() {
        return GroupComparator.hashCode((Group)this);
    }

    public static GroupEntity newMinimalInstance(String groupName) {
        return new GroupEntity(groupName, null, null, false);
    }
}

