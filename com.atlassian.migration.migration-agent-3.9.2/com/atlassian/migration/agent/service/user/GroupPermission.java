/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonValue
 */
package com.atlassian.migration.agent.service.user;

import org.codehaus.jackson.annotate.JsonValue;

public enum GroupPermission {
    HAS_PRODUCT_ACCESS("hasProductAccess"),
    PRODUCT_ADMIN("productAdmin"),
    SYSTEM_ADMIN("systemAdmin");

    private final String permissionName;

    private GroupPermission(String permissionName) {
        this.permissionName = permissionName;
    }

    @JsonValue
    public String getPermissionName() {
        return this.permissionName;
    }
}

