/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.license.LicenseHandler
 *  com.atlassian.sal.api.permission.PermissionEnforcer
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.soy.renderer.SoyTemplateRenderer
 *  org.osgi.framework.ServiceRegistration
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.context.annotation.Import
 */
package com.atlassian.zdu.configuration;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugins.osgi.javaconfig.ExportOptions;
import com.atlassian.plugins.osgi.javaconfig.OsgiServices;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.license.LicenseHandler;
import com.atlassian.sal.api.permission.PermissionEnforcer;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import com.atlassian.zdu.LicenseService;
import com.atlassian.zdu.NodeInfoAccessor;
import com.atlassian.zdu.impl.LicenseServiceImpl;
import com.atlassian.zdu.impl.ZduNodeRepositoryImpl;
import com.atlassian.zdu.impl.ZduNodeRepositoryServiceImpl;
import com.atlassian.zdu.impl.ZduServiceImpl;
import com.atlassian.zdu.internal.api.ClusterManagerAdapter;
import com.atlassian.zdu.internal.api.RollingUpgradeService;
import com.atlassian.zdu.internal.api.ZduNodeRepositoryService;
import com.atlassian.zdu.persistence.ZduNodeRepository;
import org.osgi.framework.ServiceRegistration;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(value={ZduServiceImpl.class})
public class SpringConfiguration {
    @Bean
    public NodeInfoAccessor clusterNodeAccessor(ClusterManagerAdapter clusterManagerAdapter, ZduNodeRepository zduNodeRepository) {
        return new NodeInfoAccessor(clusterManagerAdapter, zduNodeRepository);
    }

    @Bean
    public ZduNodeRepository zduNodeRepository(ActiveObjects ao) {
        return new ZduNodeRepositoryImpl(ao);
    }

    @Bean
    public ZduNodeRepositoryService zduNodeRepositoryService(ZduNodeRepository repo) {
        return new ZduNodeRepositoryServiceImpl(repo);
    }

    @Bean
    public LicenseService licenseService(LicenseHandler licenseHandler) {
        return new LicenseServiceImpl(licenseHandler);
    }

    @Bean
    public SoyTemplateRenderer soyTemplateRenderer() {
        return OsgiServices.importOsgiService(SoyTemplateRenderer.class);
    }

    @Bean
    LoginUriProvider loginUriProvider() {
        return OsgiServices.importOsgiService(LoginUriProvider.class);
    }

    @Bean
    PermissionEnforcer permissionEnforcer() {
        return OsgiServices.importOsgiService(PermissionEnforcer.class);
    }

    @Bean
    UserManager userManager() {
        return OsgiServices.importOsgiService(UserManager.class);
    }

    @Bean
    ApplicationProperties applicationProperties() {
        return OsgiServices.importOsgiService(ApplicationProperties.class);
    }

    @Bean
    ActiveObjects activeObjects() {
        return OsgiServices.importOsgiService(ActiveObjects.class);
    }

    @Bean
    EventPublisher eventPublisher() {
        return OsgiServices.importOsgiService(EventPublisher.class);
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportZduRollingUpgradeService(RollingUpgradeService rollingUpgradeService) {
        return OsgiServices.exportOsgiService(rollingUpgradeService, ExportOptions.as(RollingUpgradeService.class, new Class[0]));
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportZduNodeRepository(ZduNodeRepositoryService zduNodeRepository) {
        return OsgiServices.exportOsgiService(zduNodeRepository, ExportOptions.as(ZduNodeRepositoryService.class, new Class[0]));
    }

    @Bean
    LicenseHandler licenseHandler() {
        return OsgiServices.importOsgiService(LicenseHandler.class);
    }
}

