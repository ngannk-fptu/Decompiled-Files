/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.DiagnosticsConfiguration
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.web.Condition
 *  com.atlassian.sal.api.permission.PermissionEnforcer
 */
package com.atlassian.diagnostics.internal.web;

import com.atlassian.diagnostics.DiagnosticsConfiguration;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.Condition;
import com.atlassian.sal.api.permission.PermissionEnforcer;
import java.util.Map;

public class DiagnosticsEnabledCondition
implements Condition {
    private final PermissionEnforcer permissionEnforcer;
    private final DiagnosticsConfiguration diagnosticsConfiguration;

    public void init(Map<String, String> map) throws PluginParseException {
    }

    public boolean shouldDisplay(Map<String, Object> map) {
        return this.permissionEnforcer.isSystemAdmin() && this.diagnosticsConfiguration.isEnabled();
    }

    public DiagnosticsEnabledCondition(PermissionEnforcer permissionEnforcer, DiagnosticsConfiguration diagnosticsConfiguration) {
        this.permissionEnforcer = permissionEnforcer;
        this.diagnosticsConfiguration = diagnosticsConfiguration;
    }
}

