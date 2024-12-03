/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.config.ApplicationConfiguration
 *  com.atlassian.confluence.cluster.ClusterManager
 *  com.atlassian.confluence.languages.LanguageManager
 *  com.atlassian.confluence.license.LicenseService
 *  com.atlassian.confluence.setup.BootstrapManager
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.status.service.SystemCompatibilityService
 *  com.atlassian.confluence.status.service.SystemInformationService
 *  com.atlassian.confluence.themes.StylesheetManager
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.core.task.MultiQueueTaskManager
 *  com.atlassian.crowd.embedded.api.CrowdDirectoryService
 *  com.atlassian.crowd.manager.directory.DirectoryManager
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.troubleshooting.confluence.spring;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.config.ApplicationConfiguration;
import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.languages.LanguageManager;
import com.atlassian.confluence.license.LicenseService;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.status.service.SystemCompatibilityService;
import com.atlassian.confluence.status.service.SystemInformationService;
import com.atlassian.confluence.themes.StylesheetManager;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.core.task.MultiQueueTaskManager;
import com.atlassian.crowd.embedded.api.CrowdDirectoryService;
import com.atlassian.crowd.manager.directory.DirectoryManager;
import com.atlassian.plugins.osgi.javaconfig.OsgiServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfluenceSpecificImportedOsgiServiceBeans {
    @Bean
    public SystemInformationService systemInformationService() {
        return OsgiServices.importOsgiService(SystemInformationService.class);
    }

    @Bean
    public BootstrapManager bootstrapManager() {
        return OsgiServices.importOsgiService(BootstrapManager.class);
    }

    @Bean
    public CrowdDirectoryService crowdDirectoryService() {
        return OsgiServices.importOsgiService(CrowdDirectoryService.class);
    }

    @Bean
    public DirectoryManager directoryManager() {
        return OsgiServices.importOsgiService(DirectoryManager.class);
    }

    @Bean
    public SystemCompatibilityService systemCompatibilityService() {
        return OsgiServices.importOsgiService(SystemCompatibilityService.class);
    }

    @Bean
    public LicenseService licenseService() {
        return OsgiServices.importOsgiService(LicenseService.class);
    }

    @Bean
    public ActiveObjects activeObjects() {
        return OsgiServices.importOsgiService(ActiveObjects.class);
    }

    @Bean
    public ApplicationConfiguration applicationConfiguration() {
        return OsgiServices.importOsgiService(ApplicationConfiguration.class);
    }

    @Bean
    public LanguageManager languageManager() {
        return OsgiServices.importOsgiService(LanguageManager.class);
    }

    @Bean
    public ClusterManager clusterManager() {
        return OsgiServices.importOsgiService(ClusterManager.class);
    }

    @Bean
    public I18NBeanFactory i18NBeanFactory() {
        return OsgiServices.importOsgiService(I18NBeanFactory.class);
    }

    @Bean
    public SettingsManager settingsManager() {
        return OsgiServices.importOsgiService(SettingsManager.class);
    }

    @Bean
    public SpaceManager spaceManager() {
        return OsgiServices.importOsgiService(SpaceManager.class);
    }

    @Bean
    public StylesheetManager stylesheetManager() {
        return OsgiServices.importOsgiService(StylesheetManager.class);
    }

    @Bean
    public MultiQueueTaskManager multiQueueTaskManager() {
        return OsgiServices.importOsgiService(MultiQueueTaskManager.class);
    }
}

