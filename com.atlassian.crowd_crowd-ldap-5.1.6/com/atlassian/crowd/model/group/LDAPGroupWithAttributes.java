/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.group.GroupComparator
 *  com.atlassian.crowd.model.group.GroupTemplateWithAttributes
 *  com.atlassian.crowd.model.group.GroupType
 *  com.atlassian.crowd.model.group.GroupWithAttributes
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.Validate
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.crowd.model.group;

import com.atlassian.crowd.model.LDAPDirectoryEntity;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.group.GroupComparator;
import com.atlassian.crowd.model.group.GroupTemplateWithAttributes;
import com.atlassian.crowd.model.group.GroupType;
import com.atlassian.crowd.model.group.GroupWithAttributes;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class LDAPGroupWithAttributes
implements GroupWithAttributes,
LDAPDirectoryEntity {
    private final String dn;
    private final String name;
    private final Long directoryId;
    private final GroupType type;
    private final boolean active;
    private final String description;
    private final String externalId;
    private final Map<String, Set<String>> attributes = new HashMap<String, Set<String>>();

    public LDAPGroupWithAttributes(String dn, GroupTemplateWithAttributes group) {
        Validate.isTrue((boolean)StringUtils.isNotBlank((CharSequence)dn));
        Validate.notNull((Object)group, (String)"group template cannot be null", (Object[])new Object[0]);
        Validate.notNull((Object)group.getDirectoryId(), (String)"directoryId cannot be null", (Object[])new Object[0]);
        Validate.notNull((Object)group.getName(), (String)"group name cannot be null", (Object[])new Object[0]);
        Validate.notNull((Object)group.getType(), (String)"group type cannot be null", (Object[])new Object[0]);
        this.dn = dn;
        this.directoryId = group.getDirectoryId();
        this.name = group.getName();
        this.active = group.isActive();
        this.type = group.getType();
        this.description = group.getDescription();
        this.externalId = group.getExternalId();
        for (Map.Entry entry : group.getAttributes().entrySet()) {
            this.attributes.put((String)entry.getKey(), new HashSet((Collection)entry.getValue()));
        }
    }

    @Override
    public String getDn() {
        return this.dn;
    }

    public String getName() {
        return this.name;
    }

    public long getDirectoryId() {
        return this.directoryId;
    }

    public GroupType getType() {
        return this.type;
    }

    public boolean isActive() {
        return this.active;
    }

    public String getDescription() {
        return this.description;
    }

    public Set<String> getValues(String attributeName) {
        if (this.attributes.containsKey(attributeName)) {
            return Collections.unmodifiableSet(this.attributes.get(attributeName));
        }
        return Collections.emptySet();
    }

    public String getValue(String attributeName) {
        Set<String> values = this.getValues(attributeName);
        if (values != null && !values.isEmpty()) {
            return values.iterator().next();
        }
        return null;
    }

    public Set<String> getKeys() {
        return Collections.unmodifiableSet(this.attributes.keySet());
    }

    public boolean isEmpty() {
        return this.attributes.isEmpty();
    }

    @Nullable
    public String getExternalId() {
        return this.externalId;
    }

    public boolean equals(Object o) {
        return GroupComparator.equalsObject((Group)this, (Object)o);
    }

    public int hashCode() {
        return GroupComparator.hashCode((Group)this);
    }

    public int compareTo(Group group) {
        return GroupComparator.compareTo((Group)this, (Group)group);
    }

    public String toString() {
        return new ToStringBuilder((Object)this).append("dn", (Object)this.dn).append("name", (Object)this.name).append("directoryId", (Object)this.directoryId).append("type", (Object)this.type).append("active", this.active).append("description", (Object)this.description).append("externalId", (Object)this.externalId).append("attributes", this.attributes).toString();
    }
}

