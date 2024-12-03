/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bamboo.buildqueue.manager.AgentManager
 *  com.atlassian.bamboo.configuration.AdministrationConfigurationManager
 *  com.atlassian.bamboo.security.BambooPermissionManager
 *  com.atlassian.bamboo.user.BambooUserManager
 *  com.atlassian.sal.api.license.LicenseHandler
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Conditional
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.upm.spring;

import com.atlassian.bamboo.buildqueue.manager.AgentManager;
import com.atlassian.bamboo.configuration.AdministrationConfigurationManager;
import com.atlassian.bamboo.security.BambooPermissionManager;
import com.atlassian.bamboo.user.BambooUserManager;
import com.atlassian.plugins.osgi.javaconfig.OsgiServices;
import com.atlassian.plugins.osgi.javaconfig.conditions.product.BambooOnly;
import com.atlassian.sal.api.license.LicenseHandler;
import com.atlassian.upm.core.HostApplicationDescriptor;
import com.atlassian.upm.core.impl.BambooApplicationDescriptor;
import com.atlassian.upm.core.impl.UpmAppManager;
import com.atlassian.upm.license.internal.HostApplicationLicenseFactory;
import com.atlassian.upm.license.internal.HostLicenseEventReader;
import com.atlassian.upm.license.internal.HostLicenseProvider;
import com.atlassian.upm.license.internal.LicenseDateFormatter;
import com.atlassian.upm.license.internal.LicenseManagerProvider;
import com.atlassian.upm.license.internal.host.BambooHostLicenseProvider;
import com.atlassian.upm.license.internal.impl.DefaultHostLicenseEventReader;
import com.atlassian.upm.license.internal.impl.DefaultLicenseDateFormatter;
import com.atlassian.upm.mail.BambooMailService;
import com.atlassian.upm.mail.BambooUserLists;
import com.atlassian.upm.mail.ProductMailService;
import com.atlassian.upm.mail.ProductUserLists;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
@Conditional(value={BambooOnly.class})
public class BambooBeansAndOsgiServiceImports {
    @Bean
    public AgentManager bambooAgentManager() {
        return OsgiServices.importOsgiService(AgentManager.class);
    }

    @Bean
    public BambooUserManager bambooUserManager() {
        return OsgiServices.importOsgiService(BambooUserManager.class);
    }

    @Bean
    public HostApplicationDescriptor hostApplicationDescriptor(UpmAppManager upmAppManager, AgentManager agentManager) {
        return new BambooApplicationDescriptor(upmAppManager, agentManager);
    }

    @Bean
    public HostLicenseEventReader hostLicenseEventReader() {
        return new DefaultHostLicenseEventReader();
    }

    @Bean
    public HostLicenseProvider hostLicenseProvider(LicenseHandler licenseHandler, HostApplicationLicenseFactory hostApplicationLicenseFactory, LicenseManagerProvider licenseManagerProvider, UpmAppManager appManager) {
        return new BambooHostLicenseProvider(licenseHandler, hostApplicationLicenseFactory, licenseManagerProvider, appManager);
    }

    @Bean
    public LicenseDateFormatter licenseDateFormatter() {
        return new DefaultLicenseDateFormatter();
    }

    @Bean
    public AdministrationConfigurationManager bambooAdministrationConfigurationManager() {
        return OsgiServices.importOsgiService(AdministrationConfigurationManager.class);
    }

    @Bean
    public BambooPermissionManager bambooPermissionManager() {
        return OsgiServices.importOsgiService(BambooPermissionManager.class);
    }

    @Bean
    public ProductMailService productMailService(AdministrationConfigurationManager administrationConfigurationManager) {
        return new BambooMailService(administrationConfigurationManager);
    }

    @Bean
    public ProductUserLists productUserLists(BambooPermissionManager bambooPermissionManager, BambooUserManager bambooUserManager) {
        return new BambooUserLists(bambooPermissionManager, bambooUserManager);
    }
}

