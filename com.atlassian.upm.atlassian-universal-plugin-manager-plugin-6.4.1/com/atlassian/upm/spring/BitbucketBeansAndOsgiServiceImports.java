/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bitbucket.license.LicenseService
 *  com.atlassian.bitbucket.mail.MailService
 *  com.atlassian.bitbucket.permission.PermissionService
 *  com.atlassian.bitbucket.server.ApplicationPropertiesService
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.sal.api.license.LicenseHandler
 *  com.atlassian.sal.api.timezone.TimeZoneManager
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Conditional
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.upm.spring;

import com.atlassian.bitbucket.license.LicenseService;
import com.atlassian.bitbucket.mail.MailService;
import com.atlassian.bitbucket.permission.PermissionService;
import com.atlassian.bitbucket.server.ApplicationPropertiesService;
import com.atlassian.cache.CacheFactory;
import com.atlassian.plugins.osgi.javaconfig.OsgiServices;
import com.atlassian.plugins.osgi.javaconfig.conditions.product.BitbucketOnly;
import com.atlassian.sal.api.license.LicenseHandler;
import com.atlassian.sal.api.timezone.TimeZoneManager;
import com.atlassian.upm.core.HostApplicationDescriptor;
import com.atlassian.upm.core.impl.BitbucketApplicationDescriptor;
import com.atlassian.upm.core.impl.UpmAppManager;
import com.atlassian.upm.license.internal.BitbucketHostLicenseEventReader;
import com.atlassian.upm.license.internal.BitbucketLicenseDateFormatter;
import com.atlassian.upm.license.internal.HostApplicationLicenseFactory;
import com.atlassian.upm.license.internal.HostLicenseEventReader;
import com.atlassian.upm.license.internal.HostLicenseProvider;
import com.atlassian.upm.license.internal.LicenseDateFormatter;
import com.atlassian.upm.license.internal.LicenseManagerProvider;
import com.atlassian.upm.license.internal.host.BitbucketHostLicenseProvider;
import com.atlassian.upm.mail.BitbucketMailService;
import com.atlassian.upm.mail.BitbucketUserLists;
import com.atlassian.upm.mail.ProductMailService;
import com.atlassian.upm.mail.ProductUserLists;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
@Conditional(value={BitbucketOnly.class})
public class BitbucketBeansAndOsgiServiceImports {
    @Bean
    public LicenseService bitbucketLicenseService() {
        return OsgiServices.importOsgiService(LicenseService.class);
    }

    @Bean
    public PermissionService bitbucketPermissionService() {
        return OsgiServices.importOsgiService(PermissionService.class);
    }

    @Bean
    public TimeZoneManager bitbucketTimeZoneManager() {
        return OsgiServices.importOsgiService(TimeZoneManager.class);
    }

    @Bean
    public HostApplicationDescriptor hostApplicationDescriptor(LicenseService bitbucketLicenseService) {
        return new BitbucketApplicationDescriptor(bitbucketLicenseService);
    }

    @Bean
    public HostLicenseEventReader hostLicenseEventReader() {
        return new BitbucketHostLicenseEventReader();
    }

    @Bean
    public HostLicenseProvider hostLicenseProvider(LicenseHandler licenseHandler, HostApplicationLicenseFactory hostApplicationLicenseFactory, CacheFactory cacheFactory, LicenseManagerProvider licenseManagerProvider, LicenseService licenseService, UpmAppManager appManager) {
        return new BitbucketHostLicenseProvider(licenseHandler, hostApplicationLicenseFactory, cacheFactory, licenseManagerProvider, licenseService, appManager);
    }

    @Bean
    public LicenseDateFormatter licenseDateFormatter(TimeZoneManager bitbucketTimeZoneManager) {
        return new BitbucketLicenseDateFormatter(bitbucketTimeZoneManager);
    }

    @Bean
    public ApplicationPropertiesService bitbucketApplicationPropertiesService() {
        return OsgiServices.importOsgiService(ApplicationPropertiesService.class);
    }

    @Bean
    public MailService bitbucketMailService() {
        return OsgiServices.importOsgiService(MailService.class);
    }

    @Bean
    public ProductMailService productMailService(MailService bitbucketMailService, ApplicationPropertiesService bitbucketApplicationPropertiesService) {
        return new BitbucketMailService(bitbucketMailService, bitbucketApplicationPropertiesService);
    }

    @Bean
    public ProductUserLists productUserLists(PermissionService bitbucketPermissionService) {
        return new BitbucketUserLists(bitbucketPermissionService);
    }
}

