/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLinkService
 *  com.atlassian.applinks.api.ApplicationLinkUIService
 *  com.atlassian.applinks.api.EntityLinkService
 *  com.atlassian.applinks.api.ReadOnlyApplicationLinkService
 *  com.atlassian.applinks.api.SubvertedEntityLinkService
 *  com.atlassian.applinks.host.spi.HostApplication
 *  com.atlassian.applinks.host.spi.InternalHostApplication
 *  com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager
 *  com.atlassian.applinks.spi.link.EntityLinkBuilderFactory
 *  com.atlassian.applinks.spi.link.MutatingApplicationLinkService
 *  com.atlassian.applinks.spi.link.MutatingEntityLinkService
 *  com.atlassian.applinks.spi.manifest.ManifestRetriever
 *  com.atlassian.applinks.spi.util.TypeAccessor
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 *  com.atlassian.sal.api.upgrade.PluginUpgradeTask
 *  org.osgi.framework.ServiceRegistration
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Conditional
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.applinks.spring;

import com.atlassian.applinks.analytics.ApplinkStatusJob;
import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.applinks.api.ApplicationLinkUIService;
import com.atlassian.applinks.api.EntityLinkService;
import com.atlassian.applinks.api.ReadOnlyApplicationLinkService;
import com.atlassian.applinks.api.SubvertedEntityLinkService;
import com.atlassian.applinks.core.ApplinkStatusService;
import com.atlassian.applinks.core.DefaultTypeAccessor;
import com.atlassian.applinks.core.ElevatedPermissionsService;
import com.atlassian.applinks.core.ElevatedPermissionsServiceImpl;
import com.atlassian.applinks.core.InternalTypeAccessor;
import com.atlassian.applinks.core.link.InternalEntityLinkService;
import com.atlassian.applinks.core.refapp.RefAppInternalHostApplication;
import com.atlassian.applinks.core.upgrade.FishEyeCrucibleHashAuthenticatorPropertiesUpgradeTask;
import com.atlassian.applinks.host.spi.HostApplication;
import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.applinks.internal.common.capabilities.RemoteCapabilitiesService;
import com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager;
import com.atlassian.applinks.spi.link.EntityLinkBuilderFactory;
import com.atlassian.applinks.spi.link.MutatingApplicationLinkService;
import com.atlassian.applinks.spi.link.MutatingEntityLinkService;
import com.atlassian.applinks.spi.manifest.ManifestRetriever;
import com.atlassian.applinks.spi.util.TypeAccessor;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugins.osgi.javaconfig.ExportOptions;
import com.atlassian.plugins.osgi.javaconfig.OsgiServices;
import com.atlassian.plugins.osgi.javaconfig.conditions.product.FecruOnly;
import com.atlassian.plugins.osgi.javaconfig.conditions.product.RefappOnly;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.sal.api.upgrade.PluginUpgradeTask;
import org.osgi.framework.ServiceRegistration;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AtlassianPluginsComponents {
    @Bean
    public ElevatedPermissionsService elevatedPermissionsService() {
        return new ElevatedPermissionsServiceImpl();
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportApplicationLinkService(ApplicationLinkService applicationLinkService) {
        return OsgiServices.exportOsgiService(applicationLinkService, ExportOptions.as(ApplicationLinkService.class, MutatingApplicationLinkService.class, EntityLinkService.class, MutatingEntityLinkService.class, SubvertedEntityLinkService.class, InternalEntityLinkService.class));
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportApplicationLinkUIService(ApplicationLinkUIService applicationLinkUIService) {
        return OsgiServices.exportOsgiService(applicationLinkUIService, ExportOptions.as(ApplicationLinkUIService.class, new Class[0]));
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportApplinkStatusJob(ApplinkStatusJob applinkStatusJob) {
        return OsgiServices.exportOsgiService(applinkStatusJob, ExportOptions.as(LifecycleAware.class, new Class[0]));
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportApplinkStatusService(ApplinkStatusService applinkStatusService) {
        return OsgiServices.exportOsgiService(applinkStatusService, ExportOptions.as(ApplinkStatusService.class, new Class[0]));
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportAuthenticationConfigurationManager(AuthenticationConfigurationManager authenticationConfigurationManager) {
        return OsgiServices.exportOsgiService(authenticationConfigurationManager, ExportOptions.as(AuthenticationConfigurationManager.class, new Class[0]));
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportElevatedPermissionsService(ElevatedPermissionsService elevatedPermissionsService) {
        return OsgiServices.exportOsgiService(elevatedPermissionsService, ExportOptions.as(ElevatedPermissionsService.class, new Class[0]));
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportEntityLinkBuilderFactory(EntityLinkBuilderFactory entityLinkBuilderFactory) {
        return OsgiServices.exportOsgiService(entityLinkBuilderFactory, ExportOptions.as(EntityLinkBuilderFactory.class, new Class[0]));
    }

    @Bean
    @Conditional(value={FecruOnly.class})
    public FactoryBean<ServiceRegistration> exportFishEyeCrucibleHashAuthenticatorPropertiesUpgradeTask(FishEyeCrucibleHashAuthenticatorPropertiesUpgradeTask fishEyeCrucibleHashAuthenticatorPropertiesUpgradeTask) {
        return OsgiServices.exportOsgiService(fishEyeCrucibleHashAuthenticatorPropertiesUpgradeTask, ExportOptions.as(PluginUpgradeTask.class, new Class[0]));
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportManifestRetriever(ManifestRetriever manifestRetriever) {
        return OsgiServices.exportOsgiService(manifestRetriever, ExportOptions.as(ManifestRetriever.class, new Class[0]));
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportReadOnlyApplicationLinkService(ReadOnlyApplicationLinkService readOnlyApplicationLinkService) {
        return OsgiServices.exportOsgiService(readOnlyApplicationLinkService, ExportOptions.as(ReadOnlyApplicationLinkService.class, new Class[0]));
    }

    @Bean
    @Conditional(value={RefappOnly.class})
    public FactoryBean<ServiceRegistration> exportRefappHostApplication(RefAppInternalHostApplication hostApplication) {
        return OsgiServices.exportOsgiService((Object)hostApplication, ExportOptions.as(HostApplication.class, InternalHostApplication.class, LifecycleAware.class));
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportRemoteCapabilitiesService(RemoteCapabilitiesService remoteCapabilitiesService) {
        return OsgiServices.exportOsgiService(remoteCapabilitiesService, ExportOptions.as(RemoteCapabilitiesService.class, new Class[0]));
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportTypeAccessor(InternalTypeAccessor typeAccessor) {
        return OsgiServices.exportOsgiService(typeAccessor, ExportOptions.as(TypeAccessor.class, InternalTypeAccessor.class));
    }

    @Bean
    public InternalTypeAccessor internalTypeAccessor(PluginAccessor pluginAccessor, PluginEventManager eventManager) {
        return new DefaultTypeAccessor(pluginAccessor, eventManager);
    }
}

