/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.group.GroupType
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.crowd.search;

import com.atlassian.crowd.model.group.GroupType;
import com.atlassian.crowd.search.Entity;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class EntityDescriptor {
    private static final EntityDescriptor USER_DESCRIPTOR = new EntityDescriptor(Entity.USER);
    private static final EntityDescriptor DIRECTORY_DESCRIPTOR = new EntityDescriptor(Entity.DIRECTORY);
    private static final EntityDescriptor TOKEN_DESCRIPTOR = new EntityDescriptor(Entity.TOKEN);
    private static final EntityDescriptor APPLICATION_DESCRIPTOR = new EntityDescriptor(Entity.APPLICATION);
    private static final EntityDescriptor ALIAS_DESCRIPTOR = new EntityDescriptor(Entity.ALIAS);
    private static final Map<GroupType, EntityDescriptor> GROUP_DESCRIPTORS = new HashMap<GroupType, EntityDescriptor>();
    private final Entity entityType;
    private final GroupType groupType;

    private EntityDescriptor(Entity entityType, GroupType groupType) {
        this.entityType = entityType;
        this.groupType = groupType;
    }

    public EntityDescriptor(Entity entity) {
        this(entity, null);
    }

    public static EntityDescriptor group(GroupType groupType) {
        return GROUP_DESCRIPTORS.get(groupType);
    }

    public static EntityDescriptor group() {
        return EntityDescriptor.group(GroupType.GROUP);
    }

    @Deprecated
    public static EntityDescriptor role() {
        return EntityDescriptor.group(GroupType.LEGACY_ROLE);
    }

    public static EntityDescriptor user() {
        return USER_DESCRIPTOR;
    }

    public static EntityDescriptor directory() {
        return DIRECTORY_DESCRIPTOR;
    }

    public static EntityDescriptor token() {
        return TOKEN_DESCRIPTOR;
    }

    public static EntityDescriptor application() {
        return APPLICATION_DESCRIPTOR;
    }

    public static EntityDescriptor alias() {
        return ALIAS_DESCRIPTOR;
    }

    public Entity getEntityType() {
        return this.entityType;
    }

    public GroupType getGroupType() {
        return this.groupType;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        EntityDescriptor that = (EntityDescriptor)o;
        if (this.entityType != that.entityType) {
            return false;
        }
        return this.groupType == that.groupType;
    }

    public int hashCode() {
        int result = this.entityType != null ? this.entityType.hashCode() : 0;
        result = 31 * result + (this.groupType != null ? this.groupType.hashCode() : 0);
        return result;
    }

    public String toString() {
        return new ToStringBuilder((Object)this).append("entityType", (Object)this.entityType).append("groupType", (Object)this.groupType).toString();
    }

    static {
        for (GroupType groupType : GroupType.values()) {
            GROUP_DESCRIPTORS.put(groupType, new EntityDescriptor(Entity.GROUP, groupType));
        }
        GROUP_DESCRIPTORS.put(null, new EntityDescriptor(Entity.GROUP, null));
    }
}

