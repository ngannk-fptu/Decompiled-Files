/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.ApplicationConfiguration
 *  com.atlassian.dc.filestore.api.compat.FilesystemPath
 *  com.atlassian.dc.filestore.impl.filesystem.FilesystemFileStore
 *  com.atlassian.plugin.spring.AvailableToPlugins
 *  javax.annotation.Resource
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.confluence.impl.filestore;

import com.atlassian.config.ApplicationConfiguration;
import com.atlassian.confluence.impl.filestore.ApplicationConfigurationFileStoreFactory;
import com.atlassian.confluence.impl.filestore.ConfluenceFileStoreDirectories;
import com.atlassian.confluence.impl.filestore.FileStoreHomePathPlaceholderResolver;
import com.atlassian.confluence.impl.filestore.HomePathPlaceholderResolver;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.confluence.setup.settings.ConfluenceDirectories;
import com.atlassian.dc.filestore.api.compat.FilesystemPath;
import com.atlassian.dc.filestore.impl.filesystem.FilesystemFileStore;
import com.atlassian.plugin.spring.AvailableToPlugins;
import javax.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileStoreContextConfig {
    @Resource
    private BootstrapManager bootstrapManager;
    @Resource
    private ApplicationConfiguration applicationConfig;

    @Bean
    ApplicationConfigurationFileStoreFactory fileStoreFactory() {
        return new ApplicationConfigurationFileStoreFactory(this.applicationConfig);
    }

    @Bean
    FilesystemFileStore sharedHomeFileStore() {
        return this.fileStoreFactory().getSharedHomeFileStore();
    }

    @Bean
    FilesystemFileStore localHomeFileStore() {
        return this.fileStoreFactory().getLocalHomeFileStore();
    }

    @Bean
    FilesystemFileStore confluenceHomeFileStore() {
        return this.fileStoreFactory().getConfluenceHomeFileStore();
    }

    @Bean
    FilesystemPath sharedHome() {
        return this.sharedHomeFileStore().root();
    }

    @Bean
    FilesystemPath localHome() {
        return this.localHomeFileStore().root();
    }

    @Bean
    FilesystemPath confluenceHome() {
        return this.confluenceHomeFileStore().root();
    }

    @Bean
    HomePathPlaceholderResolver homePathPlaceholderResolver() {
        return new FileStoreHomePathPlaceholderResolver(this.localHome(), this.confluenceHome());
    }

    @Bean
    @AvailableToPlugins(interfaces={ConfluenceDirectories.class})
    ConfluenceDirectories confluenceDirectories() {
        return new ConfluenceFileStoreDirectories(this.homePathPlaceholderResolver(), this.bootstrapManager);
    }
}

