/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.api.service.retention;

@Deprecated
public interface SpaceRetentionPolicyPermissionService {
    public Boolean hasAdminPermissions(String var1);

    public PermissionLevel getUserPermission(String var1);

    public PermissionLevel getUserPermission(long var1);

    public static enum PermissionLevel {
        ADMIN,
        SPACE_ADMIN,
        OTHER;

    }
}

