/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.directory.cache.model;

import com.atlassian.crowd.model.LDAPDirectoryEntity;
import com.atlassian.crowd.model.group.LDAPGroupWithAttributes;
import com.atlassian.crowd.model.user.LDAPUserWithAttributes;
import java.util.HashMap;
import java.util.Map;

public enum EntityType {
    USER(LDAPUserWithAttributes.class),
    GROUP(LDAPGroupWithAttributes.class);

    private static final Map<Class<? extends LDAPDirectoryEntity>, EntityType> entityTypeMap;
    private final Class<? extends LDAPDirectoryEntity> ldapEntityClass;

    private EntityType(Class<? extends LDAPDirectoryEntity> ldapEntityClass) {
        this.ldapEntityClass = ldapEntityClass;
    }

    public Class<? extends LDAPDirectoryEntity> getLdapEntityClass() {
        return this.ldapEntityClass;
    }

    public static EntityType valueOf(Class<? extends LDAPDirectoryEntity> entityClass) {
        EntityType entityType = entityTypeMap.get(entityClass);
        if (entityType == null) {
            throw new IllegalArgumentException("Entity type class unsupported: " + entityClass.getCanonicalName());
        }
        return entityType;
    }

    static {
        entityTypeMap = new HashMap<Class<? extends LDAPDirectoryEntity>, EntityType>();
        for (EntityType type : EntityType.values()) {
            entityTypeMap.put(type.getLdapEntityClass(), type);
        }
    }
}

