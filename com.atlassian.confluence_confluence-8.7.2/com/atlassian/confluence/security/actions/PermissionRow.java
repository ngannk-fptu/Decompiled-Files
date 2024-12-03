/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.security.actions;

import com.atlassian.confluence.security.SpacePermission;
import java.util.HashMap;
import java.util.Map;

public abstract class PermissionRow {
    private final Map<String, SpacePermission> includedPermissions = new HashMap<String, SpacePermission>();

    public void addPermissionType(SpacePermission permission) {
        this.includedPermissions.put(permission.getType(), permission);
    }

    public boolean isTypeAllowed(String permissionType) {
        return this.includedPermissions.containsKey(permissionType.toUpperCase());
    }

    public SpacePermission getPermission(String permissionType) {
        return this.includedPermissions.get(permissionType.toUpperCase());
    }

    public String buildCheckboxParameterName(String permissionType) {
        return this.buildParameterName(permissionType, "checkbox");
    }

    public String buildHiddenParameterName(String permissionType) {
        return this.buildParameterName(permissionType, "initial");
    }

    public abstract String buildParameterName(String var1, String var2);

    public abstract boolean entityExists();

    public abstract boolean isCaseInvalid();
}

