/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.security.denormalisedpermissions.impl.space.domain;

public enum SpacePermissionType {
    VIEWSPACE("DenormalisedSpaceViewPermission", "DENORMALISED_SPACE_VIEW_PERMISSIONS"),
    EDITSPACE("DenormalisedSpaceEditPermission", "DENORMALISED_SPACE_EDIT_PERMISSIONS");

    private final String entityName;
    private final String tableName;

    private SpacePermissionType(String entityName, String tableName) {
        this.entityName = entityName;
        this.tableName = tableName;
    }

    public String getEntityName() {
        return this.entityName;
    }

    public String getTableName() {
        return this.tableName;
    }
}

