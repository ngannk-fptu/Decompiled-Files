/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.license.internal.impl.role;

import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.license.internal.impl.role.RoleBasedLicensedPlugins;

public interface RoleBasedPluginDescriptorMetadataCache {
    public Option<RoleBasedLicensedPlugins.RoleBasedPluginDescriptorMetadata> getMetadata(String var1);

    public void remove(String var1);

    public void removeAll();
}

