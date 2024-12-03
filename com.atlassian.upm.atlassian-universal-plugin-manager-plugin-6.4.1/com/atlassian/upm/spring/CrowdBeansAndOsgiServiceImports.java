/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.bootstrap.AtlassianBootstrapManager
 *  com.atlassian.crowd.embedded.api.ApplicationFactory
 *  com.atlassian.crowd.manager.mail.MailManager
 *  com.atlassian.crowd.manager.permission.UserPermissionService
 *  com.atlassian.crowd.manager.property.PropertyManager
 *  com.atlassian.sal.api.license.LicenseHandler
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Conditional
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.upm.spring;

import com.atlassian.config.bootstrap.AtlassianBootstrapManager;
import com.atlassian.crowd.embedded.api.ApplicationFactory;
import com.atlassian.crowd.manager.mail.MailManager;
import com.atlassian.crowd.manager.permission.UserPermissionService;
import com.atlassian.crowd.manager.property.PropertyManager;
import com.atlassian.plugins.osgi.javaconfig.OsgiServices;
import com.atlassian.plugins.osgi.javaconfig.conditions.product.CrowdOnly;
import com.atlassian.sal.api.license.LicenseHandler;
import com.atlassian.upm.core.HostApplicationDescriptor;
import com.atlassian.upm.core.impl.CrowdApplicationDescriptor;
import com.atlassian.upm.core.impl.UpmAppManager;
import com.atlassian.upm.license.internal.HostApplicationLicenseFactory;
import com.atlassian.upm.license.internal.HostLicenseEventReader;
import com.atlassian.upm.license.internal.LicenseDateFormatter;
import com.atlassian.upm.license.internal.LicenseManagerProvider;
import com.atlassian.upm.license.internal.host.CrowdHostLicenseProvider;
import com.atlassian.upm.license.internal.impl.DefaultHostLicenseEventReader;
import com.atlassian.upm.license.internal.impl.DefaultLicenseDateFormatter;
import com.atlassian.upm.mail.CrowdMailService;
import com.atlassian.upm.mail.CrowdUserLists;
import com.atlassian.upm.mail.ProductMailService;
import com.atlassian.upm.mail.ProductUserLists;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
@Conditional(value={CrowdOnly.class})
public class CrowdBeansAndOsgiServiceImports {
    @Bean
    public AtlassianBootstrapManager atlassianBootstrapManager() {
        return OsgiServices.importOsgiService(AtlassianBootstrapManager.class);
    }

    @Bean
    public MailManager crowdMailManager() {
        return OsgiServices.importOsgiService(MailManager.class);
    }

    @Bean
    public PropertyManager crowdPropertyManager() {
        return OsgiServices.importOsgiService(PropertyManager.class);
    }

    @Bean
    public HostApplicationDescriptor hostApplicationDescriptor(UpmAppManager upmAppManager, PropertyManager propertyManager) {
        return new CrowdApplicationDescriptor(upmAppManager, propertyManager);
    }

    @Bean
    public HostLicenseEventReader hostLicenseEventReader() {
        return new DefaultHostLicenseEventReader();
    }

    @Bean
    public CrowdHostLicenseProvider hostLicenseProvider(LicenseHandler licenseHandler, HostApplicationLicenseFactory hostApplicationLicenseFactory, UpmAppManager appManager, AtlassianBootstrapManager atlassianBootstrapManager, LicenseManagerProvider licenseManagerProvider) {
        return new CrowdHostLicenseProvider(licenseHandler, hostApplicationLicenseFactory, appManager, atlassianBootstrapManager, licenseManagerProvider);
    }

    @Bean
    public LicenseDateFormatter licenseDateFormatter() {
        return new DefaultLicenseDateFormatter();
    }

    @Bean
    public ApplicationFactory crowdApplicationFactory() {
        return OsgiServices.importOsgiService(ApplicationFactory.class);
    }

    @Bean
    public UserPermissionService crowdUserPermissionService() {
        return OsgiServices.importOsgiService(UserPermissionService.class);
    }

    @Bean
    public ProductMailService productMailService(MailManager mailManager, PropertyManager propertyManager) {
        return new CrowdMailService(mailManager, propertyManager);
    }

    @Bean
    public ProductUserLists productUserLists(UserPermissionService userPermissionService, ApplicationFactory applicationFactory) {
        return new CrowdUserLists(userPermissionService, applicationFactory);
    }
}

