/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.license.internal.impl.role;

import com.atlassian.upm.api.util.Option;

public class RoleBasedPluginMetadata {
    private final Option<Integer> licensedRoleCount;
    private final Option<Integer> currentRoleCount;

    public RoleBasedPluginMetadata(Option<Integer> licensedRoleCount, Option<Integer> currentRoleCount) {
        this.licensedRoleCount = licensedRoleCount;
        this.currentRoleCount = currentRoleCount;
    }

    public Option<Integer> getLicensedRoleCount() {
        return this.licensedRoleCount;
    }

    public Option<Integer> getCurrentRoleCount() {
        return this.currentRoleCount;
    }

    public boolean isRoleUndefined() {
        return !this.currentRoleCount.isDefined();
    }
}

