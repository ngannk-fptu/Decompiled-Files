/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext
 *  com.atlassian.core.util.ClassLoaderUtils
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.util.concurrent.LazyReference
 *  com.benryan.components.CustomCacheDirectorySetting
 *  com.benryan.components.OcSettingsManager
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableMap
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.benryan.components;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.core.util.ClassLoaderUtils;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.util.concurrent.LazyReference;
import com.benryan.components.CustomCacheDirectorySetting;
import com.benryan.components.OcSettingsManager;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import java.awt.Dimension;
import java.io.File;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="ocSettingsManager")
public final class DefaultOcSettingsManager
implements OcSettingsManager {
    private final BandanaManager mgr;
    private final ApplicationProperties applicationProperties;
    String customCacheDirectoriesFile = "resources/directories.properties";
    private final LazyReference<CustomCacheDirectorySetting> customCacheDirectory = new LazyReference<CustomCacheDirectorySetting>(){

        protected CustomCacheDirectorySetting create() throws Exception {
            return new DefaultCustomCacheDirectorySetting(DefaultOcSettingsManager.this.mgr, DefaultOcSettingsManager.this.customCacheDirectoriesFile);
        }
    };
    private static final Logger log = LoggerFactory.getLogger(DefaultOcSettingsManager.class);
    private static final int DEFAULT_MAX_CELL_LIMIT = 10000;

    @Autowired
    public DefaultOcSettingsManager(@ComponentImport BandanaManager bandanaManager, @ComponentImport ApplicationProperties applicationProperties) {
        this.mgr = bandanaManager;
        this.applicationProperties = applicationProperties;
    }

    public int getEditInWordLocation() {
        return this.getInteger("com.benryan.confluence.word.edit.location", 0);
    }

    public void setEditInWordLocation(int location) {
        this.setInteger("com.benryan.confluence.word.edit.location", location);
    }

    public boolean isShowWarning() {
        return this.getBoolean("com.benryan.confluence.word.edit.warning", false);
    }

    public void setShowWarning(boolean showWarning) {
        this.setBoolean("com.benryan.confluence.word.edit.warning", showWarning);
    }

    public boolean isDoFootnotes() {
        return this.getBoolean("com.benryan.confluence.word.edit.footnotes", false);
    }

    public void setDoFootnotes(boolean doFootnotes) {
        this.setBoolean("com.benryan.confluence.word.edit.footnotes", doFootnotes);
    }

    public CustomCacheDirectorySetting getCustomCacheDirectorySetting() {
        return (CustomCacheDirectorySetting)this.customCacheDirectory.get();
    }

    public String getCacheDir() {
        if (this.getCacheType() == 1) {
            String cacheDir = ((CustomCacheDirectorySetting)this.customCacheDirectory.get()).getDirectory();
            if (!StringUtils.isBlank((CharSequence)cacheDir)) {
                return cacheDir;
            }
            log.error("Custom cache directory blank or invalid. Defaulting to home directory.");
            return this.getHomeCachePath();
        }
        return this.getHomeCachePath();
    }

    public String getHomeCachePath() {
        return new File(this.applicationProperties.getHomeDirectory(), "viewfile").getAbsolutePath();
    }

    public int getCacheType() {
        return this.getInteger("com.benryan.confluence.word.edit.cacheType", 0);
    }

    public void setCacheType(int cacheType) {
        this.setInteger("com.benryan.confluence.word.edit.cacheType", cacheType);
    }

    public int getMaxQueues() {
        return this.getInteger("com.atlassian.confluence.officeconnector.maxQueues", 2);
    }

    public void setMaxQueues(int maxQueues) {
        this.setInteger("com.atlassian.confluence.officeconnector.maxQueues", maxQueues);
    }

    private int getInteger(String key, int fallback) {
        Integer value = (Integer)this.mgr.getValue((BandanaContext)new ConfluenceBandanaContext(), key);
        return value != null ? value : fallback;
    }

    void setInteger(String key, int value) {
        this.mgr.setValue((BandanaContext)new ConfluenceBandanaContext(), key, (Object)new Integer(value));
    }

    private boolean getBoolean(String key, boolean fallback) {
        Boolean val = (Boolean)this.mgr.getValue((BandanaContext)new ConfluenceBandanaContext(), key);
        return val != null ? val : fallback;
    }

    void setBoolean(String key, boolean value) {
        this.mgr.setValue((BandanaContext)new ConfluenceBandanaContext(), key, (Object)new Boolean(value));
    }

    String getString(String key) {
        return (String)this.mgr.getValue((BandanaContext)new ConfluenceBandanaContext(), key);
    }

    void setString(String key, String value) {
        this.mgr.setValue((BandanaContext)new ConfluenceBandanaContext(), key, (Object)value);
    }

    public int getMaxCacheSize() {
        return this.getInteger("com.atlassian.confluence.officeconnector.maxCacheSize", 500);
    }

    public void setMaxCacheSize(int size) {
        this.setInteger("com.atlassian.confluence.officeconnector.maxCacheSize", size);
    }

    public boolean getPathAuth() {
        return this.getBoolean("com.atlassian.confluence.officeconnector.usePathAuth", false);
    }

    public void setPathAuth(boolean pathAuth) {
        this.setBoolean("com.atlassian.confluence.officeconnector.usePathAuth", pathAuth);
    }

    public Dimension getMaxImportImageSize() {
        int height = this.getInteger("com.atlassian.confluence.officeconnector.maxImageHeight", 1200);
        int width = this.getInteger("com.atlassian.confluence.officeconnector.maxImageWidth", 900);
        return new Dimension(width, height);
    }

    public void setMaxImportImageSize(Dimension dimension) {
        this.setInteger("com.atlassian.confluence.officeconnector.maxImageHeight", dimension.height);
        this.setInteger("com.atlassian.confluence.officeconnector.maxImageWidth", dimension.width);
    }

    @VisibleForTesting
    void setCustomCacheDirectoriesFile(String customCacheDirectoriesFile) {
        this.customCacheDirectoriesFile = customCacheDirectoriesFile;
    }

    public static class DefaultCustomCacheDirectorySetting
    implements CustomCacheDirectorySetting {
        private String directory;
        private String error = "";
        private boolean isBandana = false;
        private final BandanaManager bandanaManager;
        private final Map<String, String> errors = ImmutableMap.builder().put((Object)"office.connector.config.caching.error.general", (Object)"An error occurred while reading the properties file.").put((Object)"office.connector.config.caching.error.blank", (Object)"No directory specified in the properties file.").put((Object)"office.connector.config.caching.error.no.directory", (Object)"The specified cache directory doesn't exist.").put((Object)"office.connector.config.caching.error.not.a.directory", (Object)"The specified cache file is not a directory.").put((Object)"office.connector.config.caching.error.cannot.write", (Object)"Can't write to the specified cache directory. Please check the permissions.").put((Object)"office.connector.config.caching.error.cannot.read", (Object)"Can't read from the specified cache directory. Please check the permissions.").put((Object)"office.connector.config.caching.error.reading.directory", (Object)"Error reading custom cache directory.").build();

        public DefaultCustomCacheDirectorySetting(BandanaManager bandanaManager, String cacheDirectoriesFile) {
            this.bandanaManager = bandanaManager;
            try {
                Properties directories = new Properties();
                directories.load(ClassLoaderUtils.getResourceAsStream((String)cacheDirectoriesFile, DefaultOcSettingsManager.class));
                this.directory = directories.getProperty("com.benryan.confluence.word.edit.cacheDir");
            }
            catch (Exception e) {
                this.directory = null;
                this.error = "office.connector.config.caching.error.general";
                log.error(this.errors.get(this.error), (Throwable)e);
            }
            if (StringUtils.isBlank((CharSequence)this.directory) && StringUtils.isBlank((CharSequence)this.error)) {
                this.setBandanaCache();
            }
            if (!StringUtils.isBlank((CharSequence)this.directory) && StringUtils.isBlank((CharSequence)this.error)) {
                this.error = this.validateDirectory(this.directory);
            }
            if (this.directory != null && this.directory.equals("") && this.error.equals("")) {
                this.directory = null;
                this.error = "office.connector.config.caching.error.blank";
            }
            if (!StringUtils.isBlank((CharSequence)this.error)) {
                log.error(this.errors.get(this.error));
                this.directory = null;
            }
        }

        private String validateDirectory(String directory) {
            log.info("Validating");
            try {
                File f = new File(directory);
                if (!f.exists()) {
                    return "office.connector.config.caching.error.no.directory";
                }
                if (!f.isDirectory()) {
                    return "office.connector.config.caching.error.not.a.directory";
                }
                if (!f.canWrite()) {
                    return "office.connector.config.caching.error.cannot.write";
                }
                if (!f.canRead()) {
                    return "office.connector.config.caching.error.cannot.read";
                }
            }
            catch (Exception e) {
                return "office.connector.config.caching.error.reading.directory";
            }
            return null;
        }

        public void setBandanaCache() {
            String bandanaCacheDir = this.getString("com.benryan.confluence.word.edit.cacheDir");
            if (!StringUtils.isBlank((CharSequence)bandanaCacheDir)) {
                this.directory = bandanaCacheDir;
                this.isBandana = true;
            }
        }

        private String getString(String key) {
            return (String)this.bandanaManager.getValue((BandanaContext)new ConfluenceBandanaContext(), key);
        }

        public String getDirectory() {
            return this.directory;
        }

        public String getError() {
            return this.error;
        }

        public boolean isBandana() {
            return this.isBandana;
        }
    }
}

