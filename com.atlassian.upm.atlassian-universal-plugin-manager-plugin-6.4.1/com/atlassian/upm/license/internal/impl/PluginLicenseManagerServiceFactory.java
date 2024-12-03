/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.ServiceFactory
 *  org.osgi.framework.ServiceRegistration
 */
package com.atlassian.upm.license.internal.impl;

import com.atlassian.upm.UpmPluginAccessor;
import com.atlassian.upm.license.internal.PluginLicenseRepository;
import com.atlassian.upm.license.internal.impl.PluginKeyAccessor;
import com.atlassian.upm.license.internal.impl.PluginLicenseManagerImpl;
import com.atlassian.upm.license.internal.impl.role.RoleBasedLicensingPluginService;
import java.util.Objects;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;

public class PluginLicenseManagerServiceFactory
extends PluginKeyAccessor
implements ServiceFactory {
    private final PluginLicenseRepository repository;
    private final UpmPluginAccessor pluginAccessor;
    private final RoleBasedLicensingPluginService roleBasedService;

    public PluginLicenseManagerServiceFactory(PluginLicenseRepository repository, UpmPluginAccessor pluginAccessor, RoleBasedLicensingPluginService roleBasedService) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.pluginAccessor = Objects.requireNonNull(pluginAccessor, "pluginAccessor");
        this.roleBasedService = Objects.requireNonNull(roleBasedService, "roleBasedService");
    }

    public Object getService(Bundle bundle, ServiceRegistration registration) {
        return new PluginLicenseManagerImpl(this.repository, this.pluginAccessor, this.roleBasedService, this.getPluginKey(bundle));
    }

    public void ungetService(Bundle bundle, ServiceRegistration registration, Object service) {
    }
}

