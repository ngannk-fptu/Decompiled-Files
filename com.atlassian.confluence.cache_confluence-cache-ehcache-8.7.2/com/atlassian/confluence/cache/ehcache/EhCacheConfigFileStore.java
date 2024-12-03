/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.dc.filestore.api.FileStore$Path
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.confluence.cache.ehcache;

import com.atlassian.confluence.cache.ehcache.EhCacheConfigStore;
import com.atlassian.dc.filestore.api.FileStore;
import java.io.IOException;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

public class EhCacheConfigFileStore
implements InitializingBean,
EhCacheConfigStore {
    private static final Logger log = LoggerFactory.getLogger(EhCacheConfigFileStore.class);
    private static final String CONFIG_FILENAME = "cache-settings-overrides.properties";
    private static final String CONFIG_FILE_HEADER_COMMENT = "EhCache Config Settings";
    private final FileStore.Path configFile;

    public EhCacheConfigFileStore(FileStore.Path sharedHome) {
        this.configFile = sharedHome.path(new String[]{"config", CONFIG_FILENAME});
    }

    public void afterPropertiesSet() throws Exception {
        this.ensureConfigurationFileCreated();
    }

    @Override
    public synchronized Properties readStoredConfig() throws IOException {
        log.debug("Reading {}", (Object)this.configFile);
        return (Properties)this.configFile.fileReader().read(inputStream -> {
            Properties config = new Properties();
            config.load(inputStream);
            return config;
        });
    }

    private synchronized void ensureConfigurationFileCreated() {
        try {
            if (!this.configFile.fileExists()) {
                log.debug("Creating default cache config file");
                EhCacheConfigFileStore.storeConfig(this.configFile, new Properties());
            }
        }
        catch (IOException e) {
            log.error("Failed to store ehcache config properties to " + this.configFile, (Throwable)e);
        }
    }

    @Override
    public synchronized void updateStoredConfig(Properties properties) throws IOException {
        log.info("Updating stored config at {}", (Object)this.configFile);
        EhCacheConfigFileStore.storeConfig(this.configFile, properties);
    }

    private static void storeConfig(FileStore.Path file, Properties properties) throws IOException {
        file.fileWriter().write(outputStream -> properties.store(outputStream, CONFIG_FILE_HEADER_COMMENT));
    }
}

