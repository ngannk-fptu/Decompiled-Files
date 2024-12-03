/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crucible.spi.services.UserService
 *  com.atlassian.sal.api.license.LicenseHandler
 *  com.cenqua.fisheye.user.UserManager
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Conditional
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.upm.spring;

import com.atlassian.crucible.spi.services.UserService;
import com.atlassian.plugins.osgi.javaconfig.OsgiServices;
import com.atlassian.plugins.osgi.javaconfig.conditions.product.FecruOnly;
import com.atlassian.sal.api.license.LicenseHandler;
import com.atlassian.upm.core.HostApplicationDescriptor;
import com.atlassian.upm.core.impl.FecruApplicationDescriptor;
import com.atlassian.upm.core.impl.UpmAppManager;
import com.atlassian.upm.license.internal.FecruLicenseDateFormatter;
import com.atlassian.upm.license.internal.HostApplicationLicenseFactory;
import com.atlassian.upm.license.internal.HostLicenseEventReader;
import com.atlassian.upm.license.internal.HostLicenseProvider;
import com.atlassian.upm.license.internal.LicenseDateFormatter;
import com.atlassian.upm.license.internal.LicenseManagerProvider;
import com.atlassian.upm.license.internal.host.FecruHostLicenseProvider;
import com.atlassian.upm.license.internal.impl.DefaultHostLicenseEventReader;
import com.atlassian.upm.mail.FeCruMailService;
import com.atlassian.upm.mail.FeCruUserLists;
import com.atlassian.upm.mail.ProductMailService;
import com.atlassian.upm.mail.ProductUserLists;
import com.cenqua.fisheye.user.UserManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
@Conditional(value={FecruOnly.class})
public class FeCruBeansAndOsgiServiceImports {
    @Bean
    public UserService fecruUserService() {
        return OsgiServices.importOsgiService(UserService.class);
    }

    @Bean
    public HostApplicationDescriptor hostApplicationDescriptor(UpmAppManager upmAppManager, UserService userService) {
        return new FecruApplicationDescriptor(upmAppManager, userService);
    }

    @Bean
    public HostLicenseEventReader hostLicenseEventReader() {
        return new DefaultHostLicenseEventReader();
    }

    @Bean
    public HostLicenseProvider hostLicenseProvider(LicenseHandler licenseHandler, HostApplicationLicenseFactory hostApplicationLicenseFactory, LicenseManagerProvider licenseManagerProvider, UpmAppManager appManager) {
        return new FecruHostLicenseProvider(licenseHandler, hostApplicationLicenseFactory, licenseManagerProvider, appManager);
    }

    @Bean
    public LicenseDateFormatter licenseDateFormatter() {
        return new FecruLicenseDateFormatter();
    }

    @Bean
    public UserManager fecruUserManager() {
        return OsgiServices.importOsgiService(UserManager.class);
    }

    @Bean
    public ProductMailService productMailService(UserService userService) {
        return new FeCruMailService(userService);
    }

    @Bean
    public ProductUserLists productUserLists() {
        return new FeCruUserLists();
    }
}

