/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 */
package com.atlassian.upm.license.internal.impl.role;

import com.atlassian.plugin.Plugin;
import com.atlassian.upm.api.license.entity.PluginLicense;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.license.internal.impl.role.PluginLicensingRole;
import com.atlassian.upm.license.internal.impl.role.RoleBasedLicensingPluginService;

public abstract class RoleBasedLicensingPluginServiceUtil {
    public static Option<Integer> getRoleCount(RoleBasedLicensingPluginService service, Option<Plugin> plugin, Option<PluginLicense> license) {
        if (license.isDefined()) {
            return service.getLicensingRoleForPlugin(plugin).map(PluginLicensingRole::getRoleCount);
        }
        return Option.none(Integer.class);
    }
}

