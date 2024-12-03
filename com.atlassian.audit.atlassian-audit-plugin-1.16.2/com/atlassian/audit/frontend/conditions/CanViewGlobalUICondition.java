/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.web.Condition
 */
package com.atlassian.audit.frontend.conditions;

import com.atlassian.audit.permission.PermissionChecker;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.Condition;
import java.util.Map;

public class CanViewGlobalUICondition
implements Condition {
    private final PermissionChecker permissionChecker;

    public CanViewGlobalUICondition(PermissionChecker permissionChecker) {
        this.permissionChecker = permissionChecker;
    }

    public void init(Map<String, String> stringStringMap) throws PluginParseException {
    }

    public boolean shouldDisplay(Map<String, Object> stringObjectMap) {
        return this.permissionChecker.hasUnrestrictedAuditViewPermission();
    }
}

