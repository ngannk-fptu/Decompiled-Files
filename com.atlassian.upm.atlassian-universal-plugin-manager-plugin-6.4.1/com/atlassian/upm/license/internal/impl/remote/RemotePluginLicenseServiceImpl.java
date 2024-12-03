/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 */
package com.atlassian.upm.license.internal.impl.remote;

import com.atlassian.plugin.Plugin;
import com.atlassian.upm.UpmPluginAccessor;
import com.atlassian.upm.api.license.RemotePluginLicenseService;
import com.atlassian.upm.api.license.entity.PluginLicense;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.license.internal.PluginLicenseRepository;
import java.util.Iterator;
import java.util.Objects;

public class RemotePluginLicenseServiceImpl
implements RemotePluginLicenseService {
    private final PluginLicenseRepository repository;
    private final UpmPluginAccessor accessor;

    RemotePluginLicenseServiceImpl(PluginLicenseRepository repository, UpmPluginAccessor accessor) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.accessor = Objects.requireNonNull(accessor, "accessor");
    }

    @Override
    public Option<PluginLicense> getRemotePluginLicense(String pluginKey) {
        Iterator<Plugin> iterator = this.accessor.getPlugin(pluginKey).iterator();
        if (iterator.hasNext()) {
            Plugin plugin = iterator.next();
            return this.repository.getPluginLicense(plugin.getKey());
        }
        return Option.none(PluginLicense.class);
    }
}

