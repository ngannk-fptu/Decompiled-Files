/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 */
package com.atlassian.upm.license.internal.impl.role;

import com.atlassian.plugin.Plugin;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.license.internal.impl.role.PluginLicensingRole;

public interface RoleBasedLicenseServiceProxy {
    public Option<PluginLicensingRole> getLicensingRoleForPlugin(Plugin var1);

    public Option<Boolean> isUserInRole(String var1, Plugin var2, PluginLicensingRole var3);

    public void onPluginUnlicensedEvent(Plugin var1);
}

