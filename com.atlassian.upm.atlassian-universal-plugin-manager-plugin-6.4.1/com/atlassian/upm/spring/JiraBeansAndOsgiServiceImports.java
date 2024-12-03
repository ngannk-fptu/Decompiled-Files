/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.jira.config.properties.ApplicationProperties
 *  com.atlassian.jira.datetime.DateTimeFormatter
 *  com.atlassian.jira.license.JiraLicenseManager
 *  com.atlassian.jira.license.LicenseCountService
 *  com.atlassian.jira.security.JiraAuthenticationContext
 *  com.atlassian.jira.user.util.UserManager
 *  com.atlassian.jira.user.util.UserUtil
 *  com.atlassian.mail.queue.MailQueue
 *  com.atlassian.sal.api.license.LicenseHandler
 *  com.atlassian.sal.api.user.UserManager
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Conditional
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.upm.spring;

import com.atlassian.cache.CacheFactory;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.jira.datetime.DateTimeFormatter;
import com.atlassian.jira.license.JiraLicenseManager;
import com.atlassian.jira.license.LicenseCountService;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.jira.user.util.UserUtil;
import com.atlassian.mail.queue.MailQueue;
import com.atlassian.plugins.osgi.javaconfig.OsgiServices;
import com.atlassian.plugins.osgi.javaconfig.conditions.product.JiraOnly;
import com.atlassian.sal.api.license.LicenseHandler;
import com.atlassian.upm.core.HostApplicationDescriptor;
import com.atlassian.upm.core.impl.JiraApplicationDescriptor;
import com.atlassian.upm.core.impl.UpmAppManager;
import com.atlassian.upm.license.internal.HostApplicationLicenseFactory;
import com.atlassian.upm.license.internal.HostLicenseEventReader;
import com.atlassian.upm.license.internal.HostLicenseProvider;
import com.atlassian.upm.license.internal.JiraHostLicenseEventReader;
import com.atlassian.upm.license.internal.JiraLicenseDateFormatter;
import com.atlassian.upm.license.internal.LicenseDateFormatter;
import com.atlassian.upm.license.internal.LicenseManagerProvider;
import com.atlassian.upm.license.internal.host.JiraHostLicenseProvider;
import com.atlassian.upm.mail.JiraActiveUserLists;
import com.atlassian.upm.mail.JiraMailService;
import com.atlassian.upm.mail.ProductMailService;
import com.atlassian.upm.mail.ProductUserLists;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
@Conditional(value={JiraOnly.class})
public class JiraBeansAndOsgiServiceImports {
    @Bean
    public HostApplicationDescriptor hostApplicationDescriptor(UpmAppManager upmAppManager, LicenseCountService licenseCountService) {
        return new JiraApplicationDescriptor(upmAppManager, licenseCountService);
    }

    @Bean
    public HostLicenseEventReader hostLicenseEventReader() {
        return new JiraHostLicenseEventReader();
    }

    @Bean
    public HostLicenseProvider hostLicenseProvider(JiraLicenseManager jiraLicenseManager, LicenseManagerProvider licenseManagerProvider, LicenseHandler licenseHandler, HostApplicationLicenseFactory hostApplicationLicenseFactory, UpmAppManager appManager, CacheFactory cacheFactory) {
        return new JiraHostLicenseProvider(jiraLicenseManager, licenseManagerProvider, licenseHandler, hostApplicationLicenseFactory, appManager, cacheFactory);
    }

    @Bean
    public JiraAuthenticationContext jiraAuthenticationContext() {
        return OsgiServices.importOsgiService(JiraAuthenticationContext.class);
    }

    @Bean
    public DateTimeFormatter jiraDateTimeFormatter() {
        return OsgiServices.importOsgiService(DateTimeFormatter.class);
    }

    @Bean
    public LicenseCountService jiraLicenseCountService() {
        return OsgiServices.importOsgiService(LicenseCountService.class);
    }

    @Bean
    public JiraLicenseManager jiraLicenseManager() {
        return OsgiServices.importOsgiService(JiraLicenseManager.class);
    }

    @Bean
    public UserManager jiraUserManager() {
        return OsgiServices.importOsgiService(UserManager.class);
    }

    @Bean
    public UserUtil jiraUserUtil() {
        return OsgiServices.importOsgiService(UserUtil.class);
    }

    @Bean
    public LicenseDateFormatter licenseDateFormatter(DateTimeFormatter jiraDateTimeFormatter) {
        return new JiraLicenseDateFormatter(jiraDateTimeFormatter);
    }

    @Bean
    public ApplicationProperties jiraApplicationProperties() {
        return OsgiServices.importOsgiService(ApplicationProperties.class);
    }

    @Bean
    public MailQueue jiraMailQueue() {
        return OsgiServices.importOsgiService(MailQueue.class);
    }

    @Bean
    public ProductMailService productMailService(MailQueue mailQueue, ApplicationProperties applicationProperties, UserManager jiraUserManager) {
        return new JiraMailService(mailQueue, applicationProperties, jiraUserManager);
    }

    @Bean
    public ProductUserLists productUserLists(UserUtil userUtil, com.atlassian.sal.api.user.UserManager salUserManager) {
        return new JiraActiveUserLists(userUtil, salUserManager);
    }
}

