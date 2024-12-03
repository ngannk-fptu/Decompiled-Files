/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.ServiceFactory
 *  org.osgi.framework.ServiceRegistration
 */
package com.atlassian.upm.license.internal.impl.remote;

import com.atlassian.upm.UpmPluginAccessor;
import com.atlassian.upm.api.license.RemotePluginLicenseService;
import com.atlassian.upm.api.license.entity.PluginLicense;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.license.internal.PluginLicenseRepository;
import com.atlassian.upm.license.internal.impl.PluginKeyAccessor;
import com.atlassian.upm.license.internal.impl.remote.RemotePluginLicenseServiceImpl;
import java.util.Objects;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;

public class RemotePluginLicenseServiceServiceFactory
extends PluginKeyAccessor
implements ServiceFactory {
    static final String ATLASSIAN_REMOTABLE_PLUGINS_LEGACY_KEY = "com.atlassian.labs.remoteapps-plugin";
    private final PluginLicenseRepository repository;
    private final UpmPluginAccessor accessor;

    public RemotePluginLicenseServiceServiceFactory(PluginLicenseRepository repository, UpmPluginAccessor accessor) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.accessor = Objects.requireNonNull(accessor, "accessor");
    }

    public Object getService(Bundle bundle, ServiceRegistration registration) {
        String pluginKey = this.getPluginKey(bundle);
        if ("com.atlassian.plugins.atlassian-connect-plugin".equals(pluginKey)) {
            return new RemotePluginLicenseServiceImpl(this.repository, this.accessor);
        }
        if (ATLASSIAN_REMOTABLE_PLUGINS_LEGACY_KEY.equals(pluginKey)) {
            return new NoOpRemotePluginLicenseService();
        }
        throw new UnauthorizedRemotePluginLicenseServiceAcquisitionException(pluginKey);
    }

    public void ungetService(Bundle bundle, ServiceRegistration registration, Object service) {
    }

    static class NoOpRemotePluginLicenseService
    implements RemotePluginLicenseService {
        NoOpRemotePluginLicenseService() {
        }

        @Override
        public Option<PluginLicense> getRemotePluginLicense(String pluginKey) {
            return Option.none();
        }
    }

    static class UnauthorizedRemotePluginLicenseServiceAcquisitionException
    extends RuntimeException {
        public UnauthorizedRemotePluginLicenseServiceAcquisitionException(String pluginKey) {
            super("Plugin with key " + pluginKey + " is not authorized to access this service: " + RemotePluginLicenseService.class.getName());
        }
    }
}

