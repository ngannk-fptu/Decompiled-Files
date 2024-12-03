/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginState
 *  com.atlassian.plugin.event.events.PluginDisabledEvent
 *  com.atlassian.plugin.event.events.PluginEnabledEvent
 *  io.atlassian.util.concurrent.LazyReference
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.ApplicationContextAware
 */
package com.atlassian.upm.license.internal.impl.role;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginState;
import com.atlassian.plugin.event.events.PluginDisabledEvent;
import com.atlassian.plugin.event.events.PluginEnabledEvent;
import com.atlassian.upm.UpmPluginAccessor;
import com.atlassian.upm.api.license.entity.LicenseEditionType;
import com.atlassian.upm.api.license.entity.PluginLicense;
import com.atlassian.upm.api.license.event.PluginLicenseEvent;
import com.atlassian.upm.api.license.event.PluginLicenseRemovedEvent;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.license.internal.PluginLicenseGlobalEvent;
import com.atlassian.upm.license.internal.PluginLicenseGlobalEventPublisher;
import com.atlassian.upm.license.internal.event.PluginLicenseCacheInvalidateEvent;
import com.atlassian.upm.license.internal.event.PluginLicenseEventPublisherRegistry;
import com.atlassian.upm.license.internal.impl.role.PluginLicensingRole;
import com.atlassian.upm.license.internal.impl.role.RoleBasedLicenseServiceProxy;
import com.atlassian.upm.license.internal.impl.role.RoleBasedLicensedPlugins;
import com.atlassian.upm.license.internal.impl.role.RoleBasedLicensingPluginService;
import com.atlassian.upm.license.internal.impl.role.RoleBasedPluginDescriptorMetadataCache;
import io.atlassian.util.concurrent.LazyReference;
import java.util.Iterator;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class RoleBasedLicensingPluginServiceImpl
implements RoleBasedLicensingPluginService,
ApplicationContextAware,
InitializingBean,
DisposableBean,
PluginLicenseGlobalEventPublisher {
    protected static final String ROLE_BASED_LICENSING_PLUGIN_KEY = "com.atlassian.upm.role-based-licensing-plugin";
    private static final Logger log = LoggerFactory.getLogger(RoleBasedLicensingPluginServiceImpl.class);
    private final RoleBasedPluginDescriptorMetadataCache metadataCache;
    private final EventPublisher eventPublisher;
    private final UpmPluginAccessor pluginAccessor;
    private final PluginLicenseEventPublisherRegistry registry;
    private Option<ApplicationContext> applicationContext = Option.none();
    private Option<RoleBasedLicenseServiceProxy> licenseService = Option.none();

    public RoleBasedLicensingPluginServiceImpl(RoleBasedPluginDescriptorMetadataCache metadataCache, EventPublisher eventPublisher, UpmPluginAccessor pluginAccessor, PluginLicenseEventPublisherRegistry registry) {
        this.metadataCache = Objects.requireNonNull(metadataCache, "metadataCache");
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "eventPublisher");
        this.pluginAccessor = Objects.requireNonNull(pluginAccessor, "pluginAccessor");
        this.registry = Objects.requireNonNull(registry, "registry");
    }

    @Override
    public void publish(PluginLicenseEvent event) {
        if (event instanceof PluginLicenseRemovedEvent) {
            this.onPluginUnlicensedEvent(((PluginLicenseRemovedEvent)event).getOldLicense());
        }
    }

    @Override
    public void publishGlobal(PluginLicenseGlobalEvent event) {
    }

    @Override
    public Option<PluginLicensingRole> getLicensingRoleForPluginKey(String pluginKey) {
        return this.getLicensingRoleForPlugin(this.pluginAccessor.getPlugin(pluginKey));
    }

    @Override
    public Option<PluginLicensingRole> getLicensingRoleForPlugin(Option<Plugin> plugin) {
        for (RoleBasedLicenseServiceProxy service : this.licenseService) {
            Iterator<Plugin> iterator = plugin.iterator();
            if (!iterator.hasNext()) continue;
            Plugin p = iterator.next();
            return service.getLicensingRoleForPlugin(p);
        }
        return Option.none();
    }

    @Override
    public Option<Boolean> isUserInRole(String userKey, Plugin plugin, PluginLicensingRole role) {
        Iterator<RoleBasedLicenseServiceProxy> iterator = this.licenseService.iterator();
        if (iterator.hasNext()) {
            RoleBasedLicenseServiceProxy service = iterator.next();
            return service.isUserInRole(userKey, plugin, role);
        }
        return Option.none();
    }

    @Override
    public Option<String> getSingularI18nKey(Option<Plugin> plugin) {
        for (Plugin p : plugin) {
            if (PluginState.ENABLED.equals((Object)p.getPluginState())) {
                for (RoleBasedLicenseServiceProxy service : this.licenseService) {
                    Iterator<PluginLicensingRole> iterator = service.getLicensingRoleForPlugin(p).iterator();
                    if (!iterator.hasNext()) continue;
                    PluginLicensingRole role = iterator.next();
                    return Option.some(role.getSingularI18nKey());
                }
                Iterator<Object> iterator = this.metadataCache.getMetadata(p.getKey()).iterator();
                if (!iterator.hasNext()) continue;
                RoleBasedLicensedPlugins.RoleBasedPluginDescriptorMetadata roleMetadata = (RoleBasedLicensedPlugins.RoleBasedPluginDescriptorMetadata)iterator.next();
                return Option.some(roleMetadata.getSingularKey());
            }
            return Option.some("upm.plugin.license.role.singular");
        }
        return Option.none();
    }

    @Override
    public Option<String> getPluralI18nKey(Option<Plugin> plugin) {
        for (Plugin p : plugin) {
            if (PluginState.ENABLED.equals((Object)p.getPluginState())) {
                for (RoleBasedLicenseServiceProxy service : this.licenseService) {
                    Iterator<PluginLicensingRole> iterator = service.getLicensingRoleForPlugin(p).iterator();
                    if (!iterator.hasNext()) continue;
                    PluginLicensingRole role = iterator.next();
                    return Option.some(role.getPluralI18nKey());
                }
                Iterator<Object> iterator = this.metadataCache.getMetadata(p.getKey()).iterator();
                if (!iterator.hasNext()) continue;
                RoleBasedLicensedPlugins.RoleBasedPluginDescriptorMetadata roleMetadata = (RoleBasedLicensedPlugins.RoleBasedPluginDescriptorMetadata)iterator.next();
                return Option.some(roleMetadata.getPluralKey());
            }
            return Option.some("upm.plugin.license.role.plural");
        }
        return Option.none();
    }

    @Override
    public void onPluginUnlicensedEvent(PluginLicense removedLicense) {
        if (removedLicense.getEditionType().equals((Object)LicenseEditionType.ROLE_COUNT)) {
            for (RoleBasedLicenseServiceProxy service : this.licenseService) {
                for (Plugin plugin : this.pluginAccessor.getPlugin(removedLicense.getPluginKey())) {
                    service.onPluginUnlicensedEvent(plugin);
                }
            }
        }
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = Option.some(applicationContext);
        this.resetService();
    }

    @EventListener
    public void onRoleBasedLicensingSpiDisablement(PluginDisabledEvent event) {
        if (this.listenForPluginEvent(event.getPlugin())) {
            this.clearService(true);
        }
        this.metadataCache.remove(event.getPlugin().getKey());
    }

    @EventListener
    public void onRoleBasedLicensingSpiEnablement(PluginEnabledEvent event) {
        if (this.listenForPluginEvent(event.getPlugin())) {
            this.resetService();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void resetService() {
        RoleBasedLicensingPluginServiceImpl roleBasedLicensingPluginServiceImpl = this;
        synchronized (roleBasedLicensingPluginServiceImpl) {
            for (final ApplicationContext appContext : this.applicationContext) {
                try {
                    LazyReference<RoleBasedLicenseServiceProxy> lazyRef = new LazyReference<RoleBasedLicenseServiceProxy>(){

                        protected RoleBasedLicenseServiceProxy create() throws Exception {
                            ((Object)((Object)this)).getClass().getClassLoader().loadClass("com.atlassian.upm.license.role.spi.RoleBasedLicenseService");
                            Class<?> serviceImplClass = ((Object)((Object)this)).getClass().getClassLoader().loadClass("com.atlassian.upm.license.internal.impl.role.RoleBasedLicenseServiceProxyImpl");
                            return (RoleBasedLicenseServiceProxy)appContext.getAutowireCapableBeanFactory().createBean(serviceImplClass, 3, false);
                        }
                    };
                    this.updateLicenseService(Option.some(lazyRef.get()));
                }
                catch (Exception e) {
                    log.debug("SPI is not available", (Throwable)e);
                    this.clearService(true);
                }
            }
        }
        this.registry.publishGlobalEvent(new PluginLicenseCacheInvalidateEvent());
    }

    private void clearService(boolean invalidateCache) {
        this.updateLicenseService(Option.none(RoleBasedLicenseServiceProxy.class));
        if (invalidateCache) {
            this.registry.publishGlobalEvent(new PluginLicenseCacheInvalidateEvent());
        }
    }

    public void afterPropertiesSet() {
        this.eventPublisher.register((Object)this);
        this.registry.registerGlobal(this);
    }

    public void destroy() {
        this.clearService(false);
        this.registry.unregisterGlobal(this);
        this.eventPublisher.unregister((Object)this);
    }

    private void updateLicenseService(Option<RoleBasedLicenseServiceProxy> service) {
        for (RoleBasedLicenseServiceProxy oldService : this.licenseService) {
            this.eventPublisher.unregister((Object)oldService);
        }
        this.licenseService = service;
        for (RoleBasedLicenseServiceProxy newService : this.licenseService) {
            this.eventPublisher.register((Object)newService);
        }
    }

    private boolean listenForPluginEvent(Plugin plugin) {
        return ROLE_BASED_LICENSING_PLUGIN_KEY.equals(plugin.getKey());
    }
}

