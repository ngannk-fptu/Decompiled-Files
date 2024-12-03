/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.license.LicenseHandler
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Conditional
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.upm.spring;

import com.atlassian.plugins.osgi.javaconfig.conditions.product.RefappOnly;
import com.atlassian.sal.api.license.LicenseHandler;
import com.atlassian.upm.core.HostApplicationDescriptor;
import com.atlassian.upm.core.impl.RefappApplicationDescriptor;
import com.atlassian.upm.core.impl.UpmAppManager;
import com.atlassian.upm.license.internal.HostApplicationLicenseFactory;
import com.atlassian.upm.license.internal.HostLicenseEventReader;
import com.atlassian.upm.license.internal.HostLicenseProvider;
import com.atlassian.upm.license.internal.LicenseDateFormatter;
import com.atlassian.upm.license.internal.host.RefappHostLicenseProvider;
import com.atlassian.upm.license.internal.impl.DefaultHostLicenseEventReader;
import com.atlassian.upm.license.internal.impl.DefaultLicenseDateFormatter;
import com.atlassian.upm.mail.ProductMailService;
import com.atlassian.upm.mail.ProductUserLists;
import com.atlassian.upm.mail.RefAppMailService;
import com.atlassian.upm.mail.RefAppUserLists;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
@Conditional(value={RefappOnly.class})
public class RefAppBeansAndOsgiServiceImports {
    @Bean
    public HostApplicationDescriptor hostApplicationDescriptor(UpmAppManager upmAppManager) {
        return new RefappApplicationDescriptor(upmAppManager);
    }

    @Bean
    public HostLicenseEventReader hostLicenseEventReader() {
        return new DefaultHostLicenseEventReader();
    }

    @Bean
    public HostLicenseProvider hostLicenseProvider(LicenseHandler licenseHandler, HostApplicationLicenseFactory hostApplicationLicenseFactory, UpmAppManager appManager) {
        return new RefappHostLicenseProvider(licenseHandler, hostApplicationLicenseFactory, appManager);
    }

    @Bean
    public LicenseDateFormatter licenseDateFormatter() {
        return new DefaultLicenseDateFormatter();
    }

    @Bean
    public ProductMailService productMailService() {
        return new RefAppMailService();
    }

    @Bean
    public ProductUserLists productUserLists() {
        return new RefAppUserLists();
    }
}

