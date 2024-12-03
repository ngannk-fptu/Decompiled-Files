/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginState
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.upm.license.role.spi.LicensingRole
 *  com.atlassian.upm.license.role.spi.LicensingRoleMembershipUpdatedEvent
 *  com.atlassian.upm.license.role.spi.RoleBasedLicenseService
 *  io.atlassian.util.concurrent.LazyReference
 *  org.osgi.framework.ServiceReference
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.BeansException
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.ApplicationContextAware
 */
package com.atlassian.upm.license.internal.impl.role;

import com.atlassian.event.api.EventListener;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginState;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.upm.UpmPluginAccessor;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.license.internal.event.PluginLicenseCacheInvalidateEvent;
import com.atlassian.upm.license.internal.event.PluginLicenseEventPublisherRegistry;
import com.atlassian.upm.license.internal.impl.role.PluginLicensingRole;
import com.atlassian.upm.license.internal.impl.role.PluginLicensingRoleImpl;
import com.atlassian.upm.license.internal.impl.role.PluginLicensingRoleMembershipUpdatedEvent;
import com.atlassian.upm.license.internal.impl.role.RoleBasedLicenseServiceProxy;
import com.atlassian.upm.license.internal.impl.role.RoleBasedLicensedPlugins;
import com.atlassian.upm.license.internal.impl.role.RoleBasedLicensingServiceAccessor;
import com.atlassian.upm.license.internal.impl.role.RoleBasedPluginDescriptorMetadataCache;
import com.atlassian.upm.license.role.spi.LicensingRole;
import com.atlassian.upm.license.role.spi.LicensingRoleMembershipUpdatedEvent;
import com.atlassian.upm.license.role.spi.RoleBasedLicenseService;
import io.atlassian.util.concurrent.LazyReference;
import java.net.URI;
import java.util.Iterator;
import java.util.Objects;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class RoleBasedLicenseServiceProxyImpl
implements RoleBasedLicenseServiceProxy,
ApplicationContextAware {
    private static final Logger log = LoggerFactory.getLogger(RoleBasedLicenseServiceProxyImpl.class);
    private final RoleBasedPluginDescriptorMetadataCache metadataCache;
    private final ApplicationProperties applicationProperties;
    private final UpmPluginAccessor pluginAccessor;
    private final PluginLicenseEventPublisherRegistry licenseEventPublisher;
    private Option<RoleBasedLicenseService> licenseService = Option.none();

    public RoleBasedLicenseServiceProxyImpl(RoleBasedPluginDescriptorMetadataCache metadataCache, ApplicationProperties applicationProperties, UpmPluginAccessor pluginAccessor, PluginLicenseEventPublisherRegistry licenseEventPublisher) {
        this.metadataCache = Objects.requireNonNull(metadataCache, "metadataCache");
        this.applicationProperties = Objects.requireNonNull(applicationProperties, "applicationProperties");
        this.pluginAccessor = Objects.requireNonNull(pluginAccessor, "pluginAccessor");
        this.licenseEventPublisher = Objects.requireNonNull(licenseEventPublisher, "licenseEventPublisher");
    }

    @Override
    public Option<PluginLicensingRole> getLicensingRoleForPlugin(Plugin plugin) {
        for (RoleBasedLicenseService service : this.licenseService) {
            for (RoleBasedLicensedPlugins.RoleBasedPluginDescriptorMetadata roleMetadata : this.metadataCache.getMetadata(plugin.getKey())) {
                Iterator<LicensingRole> iterator = Option.option(service.getLicensingRole(plugin, roleMetadata.getRoleKey())).iterator();
                if (iterator.hasNext()) {
                    LicensingRole role = iterator.next();
                    PluginLicensingRoleImpl upmRole = new PluginLicensingRoleImpl(role.getKey(), role.getNameI18nKey(), role.getDescriptionI18nKey(), roleMetadata.getSingularKey(), roleMetadata.getPluralKey(), this.normalize(role.getManagementPage()), role.getRoleCount());
                    return Option.some(upmRole);
                }
                if (!PluginState.ENABLED.equals((Object)plugin.getPluginState())) continue;
                try {
                    LicensingRole role = service.createLicensingRole(plugin, roleMetadata.getRoleKey(), roleMetadata.getNameKey(), roleMetadata.getDescriptionKey());
                    PluginLicensingRoleImpl upmRole = new PluginLicensingRoleImpl(role.getKey(), role.getNameI18nKey(), role.getDescriptionI18nKey(), roleMetadata.getSingularKey(), roleMetadata.getPluralKey(), this.normalize(role.getManagementPage()), role.getRoleCount());
                    return Option.some(upmRole);
                }
                catch (Exception e) {
                    String msg = "Could not create a group for the licensing role: " + roleMetadata.getDescriptionKey();
                    log.warn(msg);
                    log.debug(msg, (Throwable)e);
                    return Option.none(PluginLicensingRole.class);
                }
            }
        }
        return Option.none();
    }

    @Override
    public Option<Boolean> isUserInRole(String userKey, Plugin plugin, PluginLicensingRole role) {
        for (RoleBasedLicenseService service : this.licenseService) {
            Iterator<LicensingRole> iterator = Option.option(service.getLicensingRole(plugin, role.getKey())).iterator();
            if (!iterator.hasNext()) continue;
            LicensingRole licensingRole = iterator.next();
            return Option.some(licensingRole.isUserInRole(userKey));
        }
        return Option.none();
    }

    @Override
    public void onPluginUnlicensedEvent(Plugin plugin) {
        for (RoleBasedLicenseService service : this.licenseService) {
            service.onPluginUnlicensedEvent(plugin);
        }
    }

    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        try {
            LazyReference<RoleBasedLicensingServiceAccessor> lazyRef = new LazyReference<RoleBasedLicensingServiceAccessor>(){

                protected RoleBasedLicensingServiceAccessor create() throws Exception {
                    Class<?> accessorImplClass = ((Object)((Object)this)).getClass().getClassLoader().loadClass("com.atlassian.upm.license.internal.impl.role.RoleBasedLicensingServiceAccessor");
                    return (RoleBasedLicensingServiceAccessor)applicationContext.getAutowireCapableBeanFactory().createBean(accessorImplClass, 3, false);
                }
            };
            RoleBasedLicensingServiceAccessor accessor = (RoleBasedLicensingServiceAccessor)lazyRef.get();
            for (ServiceReference sr : accessor.getRoleBasedLicenseServiceServiceReference()) {
                if (!this.isAllowableImplementation(sr)) continue;
                this.licenseService = accessor.getRoleBasedLicenseService(sr);
            }
        }
        catch (Exception e) {
            log.debug("SPI is not available", (Throwable)e);
            this.licenseService = Option.none();
        }
    }

    private boolean isAllowableImplementation(ServiceReference sr) {
        return "com.atlassian.upm.role-based-licensing-plugin".equals(sr.getBundle().getSymbolicName());
    }

    @EventListener
    public void onRoleMembershipUpdate(LicensingRoleMembershipUpdatedEvent event) {
        String pluginKey = event.getPluginKey();
        if ("*".equals(pluginKey)) {
            this.licenseEventPublisher.publishGlobalEvent(new PluginLicenseCacheInvalidateEvent());
        } else {
            for (Plugin plugin : this.pluginAccessor.getPlugin(pluginKey)) {
                for (PluginLicensingRole role : this.getLicensingRoleForPlugin(plugin)) {
                    this.licenseEventPublisher.publishGlobalEvent(new PluginLicensingRoleMembershipUpdatedEvent(plugin, role.getRoleCount()));
                }
            }
        }
    }

    private URI normalize(URI path) {
        try {
            URI base = URI.create(this.applicationProperties.getBaseUrl()).normalize();
            return URI.create(base.getPath() + path);
        }
        catch (Exception e) {
            log.warn("Invalid licensing role management page URI", (Throwable)e);
            return path;
        }
    }
}

