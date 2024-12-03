/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.upm.license.internal.impl;

import com.atlassian.plugin.Plugin;
import com.atlassian.upm.UpmPluginAccessor;
import com.atlassian.upm.api.license.PluginLicenseManager;
import com.atlassian.upm.api.license.entity.PluginLicense;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.license.internal.PluginLicenseRepository;
import com.atlassian.upm.license.internal.impl.PluginLicensesInternal;
import com.atlassian.upm.license.internal.impl.role.PluginLicensingRole;
import com.atlassian.upm.license.internal.impl.role.RoleBasedLicensingPluginService;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PluginLicenseManagerImpl
implements PluginLicenseManager {
    private static final Logger log = LoggerFactory.getLogger(PluginLicenseManagerImpl.class);
    private final String pluginKey;
    private final PluginLicenseRepository repository;
    private final UpmPluginAccessor pluginAccessor;
    private final RoleBasedLicensingPluginService roleBasedLicensingPluginService;

    public PluginLicenseManagerImpl(PluginLicenseRepository repository, UpmPluginAccessor pluginAccessor, RoleBasedLicensingPluginService roleBasedLicensingPluginService, String pluginKey) {
        this.pluginKey = Objects.requireNonNull(pluginKey, "pluginKey");
        this.pluginAccessor = Objects.requireNonNull(pluginAccessor, "pluginAccessor");
        this.repository = Objects.requireNonNull(repository, "repository");
        this.roleBasedLicensingPluginService = Objects.requireNonNull(roleBasedLicensingPluginService, "roleBasedLicensingPluginService");
    }

    @Override
    public Option<PluginLicense> getLicense() {
        return this.repository.getPluginLicense(this.pluginKey);
    }

    @Override
    public boolean isUserInLicenseRole(String userKey) {
        if (userKey == null) {
            return false;
        }
        for (PluginLicense license : this.getLicense()) {
            for (Plugin plugin : this.pluginAccessor.getPlugin(this.pluginKey)) {
                if (PluginLicensesInternal.isRoleBasedLicense(license)) {
                    Iterator<PluginLicensingRole> iterator = this.roleBasedLicensingPluginService.getLicensingRoleForPluginKey(this.pluginKey).iterator();
                    if (!iterator.hasNext()) continue;
                    PluginLicensingRole role = iterator.next();
                    return this.roleBasedLicensingPluginService.isUserInRole(userKey, plugin, role).getOrElse(true);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public Option<Integer> getCurrentUserCountInLicenseRole() {
        return this.withRole(PluginLicensingRole::getRoleCount);
    }

    @Override
    public String getPluginKey() {
        return this.pluginKey;
    }

    private <T> Option<T> withRole(Function<PluginLicensingRole, T> f) {
        for (PluginLicense license : this.getLicense()) {
            Iterator<PluginLicensingRole> iterator;
            if (!PluginLicensesInternal.isRoleBasedLicense(license) || !(iterator = this.roleBasedLicensingPluginService.getLicensingRoleForPluginKey(this.pluginKey).iterator()).hasNext()) continue;
            PluginLicensingRole role = iterator.next();
            return Option.some(f.apply(role));
        }
        log.debug("Role cannot be found for plugin: " + this.pluginKey);
        return Option.none();
    }
}

