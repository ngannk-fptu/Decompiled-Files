/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.bootstrap.AtlassianBootstrapManager
 *  com.atlassian.config.util.BootstrapUtils
 *  com.atlassian.gzipfilter.integration.GzipFilterIntegration
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.spring.container.LazyComponentReference
 *  com.atlassian.util.concurrent.Supplier
 *  javax.servlet.http.HttpServletRequest
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.util.zip;

import com.atlassian.config.bootstrap.AtlassianBootstrapManager;
import com.atlassian.config.util.BootstrapUtils;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.gzipfilter.integration.GzipFilterIntegration;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.spring.container.LazyComponentReference;
import com.atlassian.util.concurrent.Supplier;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfluenceGzipFilterIntegration
implements GzipFilterIntegration {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceGzipFilterIntegration.class);
    private static final boolean USE_GZIP_DEFAULT = false;
    private static final String ENCODING_DEFAULT = "UTF-8";
    private final Supplier<SettingsManager> settingsManager = new LazyComponentReference("settingsManager");

    private SettingsManager getSettingsManager() {
        AtlassianBootstrapManager bootstrapManager = BootstrapUtils.getBootstrapManager();
        if (bootstrapManager == null) {
            return null;
        }
        if (!bootstrapManager.isSetupComplete() || !ContainerManager.isContainerSetup()) {
            return null;
        }
        if (this.settingsManager.get() == null) {
            log.error("Cannot get settingsManager from context");
        }
        return (SettingsManager)this.settingsManager.get();
    }

    public boolean useGzip() {
        SettingsManager settingsManager = this.getSettingsManager();
        if (settingsManager == null) {
            return false;
        }
        try {
            return settingsManager.getGlobalSettings().isGzippingResponse();
        }
        catch (RuntimeException ex) {
            log.warn("Failed to obtain gzip setting; defaulting to {} - {}", (Object)false, (Object)ex.getMessage());
            return false;
        }
    }

    public String getResponseEncoding(HttpServletRequest request) {
        SettingsManager settingsManager = this.getSettingsManager();
        if (settingsManager == null) {
            return ENCODING_DEFAULT;
        }
        try {
            return settingsManager.getGlobalSettings().getDefaultEncoding();
        }
        catch (RuntimeException ex) {
            log.warn("Failed to obtain response encoding; defaulting to {} - {}", (Object)ENCODING_DEFAULT, (Object)ex.getMessage());
            return ENCODING_DEFAULT;
        }
    }
}

