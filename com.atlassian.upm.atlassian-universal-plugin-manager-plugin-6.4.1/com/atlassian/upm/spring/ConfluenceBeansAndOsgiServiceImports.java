/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.confluence.core.FormatSettingsManager
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.core.task.MultiQueueTaskManager
 *  com.atlassian.sal.api.license.LicenseHandler
 *  com.atlassian.sal.api.user.UserManager
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Conditional
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.upm.spring;

import com.atlassian.cache.CacheFactory;
import com.atlassian.confluence.core.FormatSettingsManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.core.task.MultiQueueTaskManager;
import com.atlassian.plugins.osgi.javaconfig.OsgiServices;
import com.atlassian.plugins.osgi.javaconfig.conditions.product.ConfluenceOnly;
import com.atlassian.sal.api.license.LicenseHandler;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.upm.core.HostApplicationDescriptor;
import com.atlassian.upm.core.impl.ConfUserAccessor;
import com.atlassian.upm.core.impl.ConfluenceApplicationDescriptor;
import com.atlassian.upm.core.impl.UpmAppManager;
import com.atlassian.upm.license.internal.ConfluenceHostLicenseEventReader;
import com.atlassian.upm.license.internal.ConfluenceLicenseDateFormatter;
import com.atlassian.upm.license.internal.HostApplicationLicenseFactory;
import com.atlassian.upm.license.internal.HostLicenseEventReader;
import com.atlassian.upm.license.internal.HostLicenseProvider;
import com.atlassian.upm.license.internal.LicenseDateFormatter;
import com.atlassian.upm.license.internal.LicenseManagerProvider;
import com.atlassian.upm.license.internal.host.ConfluenceHostLicenseProvider;
import com.atlassian.upm.mail.ConfluenceMailService;
import com.atlassian.upm.mail.ConfluenceUserLists;
import com.atlassian.upm.mail.ProductMailService;
import com.atlassian.upm.mail.ProductUserLists;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
@Conditional(value={ConfluenceOnly.class})
public class ConfluenceBeansAndOsgiServiceImports {
    @Bean
    public ConfUserAccessor cachedConfluenceUserAccessorWrapper(UserAccessor confluenceUserAccessor) {
        return new ConfUserAccessor(confluenceUserAccessor);
    }

    @Bean
    public FormatSettingsManager confluenceFormatSettingsManager() {
        return OsgiServices.importOsgiService(FormatSettingsManager.class);
    }

    @Bean
    public UserAccessor confluenceUserAccessor() {
        return OsgiServices.importOsgiService(UserAccessor.class);
    }

    @Bean
    public HostApplicationDescriptor hostApplicationDescriptor(UpmAppManager upmAppManager, ConfUserAccessor confluenceUserAccessor) {
        return new ConfluenceApplicationDescriptor(upmAppManager, confluenceUserAccessor);
    }

    @Bean
    public HostLicenseEventReader hostLicenseEventReader() {
        return new ConfluenceHostLicenseEventReader();
    }

    @Bean
    public HostLicenseProvider hostLicenseProvider(LicenseHandler licenseHandler, HostApplicationLicenseFactory hostApplicationLicenseFactory, CacheFactory cacheFactory, LicenseManagerProvider licenseManagerProvider, UpmAppManager appManager) {
        return new ConfluenceHostLicenseProvider(licenseHandler, hostApplicationLicenseFactory, cacheFactory, licenseManagerProvider, appManager);
    }

    @Bean
    public LicenseDateFormatter licenseDateFormatter(FormatSettingsManager formatSettingsManager, UserAccessor userAccessor) {
        return new ConfluenceLicenseDateFormatter(formatSettingsManager, userAccessor);
    }

    @Bean
    public MultiQueueTaskManager confluenceMultiQueueTaskManager() {
        return OsgiServices.importOsgiService(MultiQueueTaskManager.class);
    }

    @Bean
    public SettingsManager confluenceSettingsManager() {
        return OsgiServices.importOsgiService(SettingsManager.class);
    }

    @Bean
    public ProductMailService productMailService(MultiQueueTaskManager taskManager, UserAccessor userAccessor, SettingsManager settingsManager) {
        return new ConfluenceMailService(taskManager, userAccessor, settingsManager);
    }

    @Bean
    public ProductUserLists productUserLists(UserAccessor userAccessor, UserManager userManager) {
        return new ConfluenceUserLists(userAccessor, userManager);
    }
}

