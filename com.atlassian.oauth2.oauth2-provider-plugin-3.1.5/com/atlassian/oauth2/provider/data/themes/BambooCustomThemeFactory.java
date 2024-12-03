/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bamboo.configuration.AdministrationConfigurationAccessor
 *  com.atlassian.bamboo.configuration.LookAndFeelConfiguration
 *  com.atlassian.bamboo.setup.BambooHomeLocator
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 */
package com.atlassian.oauth2.provider.data.themes;

import com.atlassian.bamboo.configuration.AdministrationConfigurationAccessor;
import com.atlassian.bamboo.configuration.LookAndFeelConfiguration;
import com.atlassian.bamboo.setup.BambooHomeLocator;
import com.atlassian.oauth2.provider.data.themes.ProductCustomTheme;
import com.atlassian.oauth2.provider.data.themes.ProductCustomThemeFactory;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;

public class BambooCustomThemeFactory
extends ProductCustomThemeFactory {
    private final ApplicationProperties applicationProperties;
    private final BambooHomeLocator bambooHomeLocator;
    private final AdministrationConfigurationAccessor administrationConfigurationAccessor;

    public BambooCustomThemeFactory(ApplicationProperties applicationProperties, BambooHomeLocator bambooHomeLocator, AdministrationConfigurationAccessor administrationConfigurationAccessor) {
        this.applicationProperties = applicationProperties;
        this.bambooHomeLocator = bambooHomeLocator;
        this.administrationConfigurationAccessor = administrationConfigurationAccessor;
    }

    @Override
    public ProductCustomTheme get() {
        LookAndFeelConfiguration lookAndFeelConfiguration = this.administrationConfigurationAccessor.getAdministrationConfiguration().getLookAndFeelConfiguration();
        return new ProductCustomTheme(lookAndFeelConfiguration.getPrimaryHeaderColor(), this.getLogoUrl(lookAndFeelConfiguration));
    }

    private String getLogoUrl(LookAndFeelConfiguration lookAndFeelConfiguration) {
        if (this.isCustomLogo()) {
            return this.applicationProperties.getBaseUrl(UrlMode.CANONICAL) + lookAndFeelConfiguration.getLogoResourceUrl();
        }
        return "";
    }

    private boolean isCustomLogo() {
        return Files.exists(Paths.get(this.bambooHomeLocator.getSharedHomePath(), "attachments", "logos", "bamboo-logo.png"), new LinkOption[0]);
    }
}

