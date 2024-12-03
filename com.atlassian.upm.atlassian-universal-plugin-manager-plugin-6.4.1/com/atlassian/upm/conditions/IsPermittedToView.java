/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.web.Condition
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.upm.conditions;

import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.Condition;
import com.atlassian.upm.SysPersisted;
import com.atlassian.upm.UpmSettings;
import com.atlassian.upm.core.permission.Permission;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import com.atlassian.upm.permission.UpmVisibility;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IsPermittedToView
implements Condition {
    private static final Logger log = LoggerFactory.getLogger(IsPermittedToView.class);
    private final UpmVisibility visibility;
    private final PermissionEnforcer permissionEnforcer;
    private final SysPersisted sysPersisted;
    private String page;

    public IsPermittedToView(UpmVisibility visibility, PermissionEnforcer permissionEnforcer, SysPersisted sysPersisted) {
        this.visibility = Objects.requireNonNull(visibility, "visibility");
        this.permissionEnforcer = Objects.requireNonNull(permissionEnforcer, "permissionEnforcer");
        this.sysPersisted = Objects.requireNonNull(sysPersisted, "sysPersisted");
    }

    public void init(Map<String, String> paramMap) throws PluginParseException {
        this.page = paramMap.get("page");
    }

    public boolean shouldDisplay(Map<String, Object> context) {
        if ("install".equals(this.page)) {
            return this.visibility.isInstallVisible();
        }
        if ("manage".equals(this.page)) {
            return this.visibility.isManageExistingVisible();
        }
        if ("purchased-addons".equals(this.page)) {
            return this.visibility.isPurchasedAddonsVisible();
        }
        if ("requests".equals(this.page)) {
            return (!this.sysPersisted.is(UpmSettings.REQUESTS_DISABLED) || this.permissionEnforcer.hasPermission(Permission.MANAGE_IN_PROCESS_PLUGIN_INSTALL_FROM_URI)) && this.visibility.isInstallVisible();
        }
        if ("notifications".equals(this.page)) {
            return this.visibility.isNotificationDropdownVisible();
        }
        log.warn("Permission requested for unknown page '" + this.page + ".'");
        return false;
    }
}

