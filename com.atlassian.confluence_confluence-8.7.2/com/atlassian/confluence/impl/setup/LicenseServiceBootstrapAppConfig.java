/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.ApplicationConfiguration
 *  com.atlassian.extras.api.LicenseManager
 *  com.atlassian.extras.api.Product
 *  com.atlassian.extras.core.AtlassianLicenseFactory
 *  com.atlassian.extras.core.DefaultAtlassianLicenseFactory
 *  com.atlassian.extras.core.confluence.ConfluenceProductLicenseFactory
 *  com.atlassian.extras.core.plugins.PluginLicenseFactory
 *  com.atlassian.plugin.spring.AvailableToPlugins
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.confluence.impl.setup;

import com.atlassian.config.ApplicationConfiguration;
import com.atlassian.confluence.internal.license.store.LicenseStoreInternal;
import com.atlassian.confluence.license.DefaultLicenseService;
import com.atlassian.confluence.license.LicenseManagerFactoryBean;
import com.atlassian.confluence.license.LicenseService;
import com.atlassian.confluence.license.store.ApplicationConfigurationLicenseStore;
import com.atlassian.confluence.license.util.ConfluenceLicenseUtils;
import com.atlassian.confluence.license.validator.CompositeLicenseValidator;
import com.atlassian.confluence.license.validator.DataCenterLicenseExpiryValidator;
import com.atlassian.confluence.license.validator.DataCenterNumberOfUsersValidator;
import com.atlassian.confluence.license.validator.LegacyClusterLicenseValidator;
import com.atlassian.confluence.license.validator.LegacyServerLicenseValidator;
import com.atlassian.confluence.license.validator.LegacyV1LicenseValidator;
import com.atlassian.confluence.license.validator.LicenseValidator;
import com.atlassian.extras.api.LicenseManager;
import com.atlassian.extras.api.Product;
import com.atlassian.extras.core.AtlassianLicenseFactory;
import com.atlassian.extras.core.DefaultAtlassianLicenseFactory;
import com.atlassian.extras.core.confluence.ConfluenceProductLicenseFactory;
import com.atlassian.extras.core.plugins.PluginLicenseFactory;
import com.atlassian.plugin.spring.AvailableToPlugins;
import java.util.HashMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LicenseServiceBootstrapAppConfig {
    @Bean
    LicenseManagerFactoryBean v2LicenseManager(AtlassianLicenseFactory atlassianLicenseFactory) {
        return new LicenseManagerFactoryBean(atlassianLicenseFactory);
    }

    @Bean
    LicenseStoreInternal licenseStore(ApplicationConfiguration applicationConfig, LicenseManager v2LicenseManager) {
        return new ApplicationConfigurationLicenseStore(applicationConfig, v2LicenseManager);
    }

    @Bean
    LicenseValidator licenseValidator() {
        return new CompositeLicenseValidator(new LegacyV1LicenseValidator(), new LegacyClusterLicenseValidator(), new LegacyServerLicenseValidator(), new DataCenterLicenseExpiryValidator(), new DataCenterNumberOfUsersValidator());
    }

    @Bean
    AtlassianLicenseFactory atlassianLicenseFactory() {
        HashMap<Product, Object> licenseFactoryMap = new HashMap<Product, Object>();
        licenseFactoryMap.put(Product.CONFLUENCE, new ConfluenceProductLicenseFactory());
        licenseFactoryMap.put(Product.TEAM_CALENDARS, new PluginLicenseFactory(Product.TEAM_CALENDARS));
        licenseFactoryMap.put(ConfluenceLicenseUtils.CONFLUENCE_QUESTION, new PluginLicenseFactory(ConfluenceLicenseUtils.CONFLUENCE_QUESTION));
        return new DefaultAtlassianLicenseFactory(licenseFactoryMap);
    }

    @Bean
    @AvailableToPlugins(interfaces={LicenseService.class})
    LicenseService licenseService(LicenseStoreInternal licenseStore, LicenseManager v2LicenseManager, LicenseValidator licenseValidator, AtlassianLicenseFactory atlassianLicenseFactory) {
        return new DefaultLicenseService(licenseStore, v2LicenseManager, licenseValidator, atlassianLicenseFactory);
    }
}

