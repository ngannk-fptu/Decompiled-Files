/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.web.Condition
 *  com.atlassian.sal.api.permission.PermissionEnforcer
 */
package com.atlassian.zdu;

import com.atlassian.plugin.web.Condition;
import com.atlassian.sal.api.permission.PermissionEnforcer;
import java.util.Map;

public class PermittedCondition
implements Condition {
    private final PermissionEnforcer permissionEnforcer;

    public PermittedCondition(PermissionEnforcer permissionEnforcer) {
        this.permissionEnforcer = permissionEnforcer;
    }

    public void init(Map<String, String> map) {
    }

    public boolean shouldDisplay(Map<String, Object> map) {
        return this.permissionEnforcer.isSystemAdmin();
    }
}

