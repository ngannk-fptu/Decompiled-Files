/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.sal.api.ApplicationProperties
 */
package com.atlassian.business.insights.core.frontend.data;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.business.insights.core.frontend.data.ProductVersion;
import com.atlassian.sal.api.ApplicationProperties;
import java.io.IOException;
import java.io.InputStream;
import java.util.MissingResourceException;
import java.util.Optional;
import java.util.Properties;

public class KbArticleLinkResolver {
    private static final String KB_VERSION_KEY_PREFIX = "data-pipeline.kb.minimum.supported.version.";
    private static final String KB_VERSION_UNSUPPORTED = "UNSUPPORTED";
    private static final String PROPERTIES_FILE_PATH = "/data-pipeline-settings.properties";
    private static final String KB_URL_KEY_PREFIX = "data-pipeline.kb.url.";
    private static final String FALLBACK = "fallback";
    private static final String KB_VERSION_FORMAT_KEY_PREFIX = "data-pipeline.kb.product.version.format.";
    private final ApplicationProperties applicationProperties;
    private final Properties dataPipelineKbSettings;
    private final ProductVersion currentProductVersion;

    public KbArticleLinkResolver(ApplicationProperties applicationProperties) throws IOException {
        this(applicationProperties, PROPERTIES_FILE_PATH);
    }

    @VisibleForTesting
    KbArticleLinkResolver(ApplicationProperties applicationProperties, String propertiesFilePath) throws IOException {
        this.applicationProperties = applicationProperties;
        try (InputStream inputStream = this.getClass().getResourceAsStream(propertiesFilePath);){
            this.dataPipelineKbSettings = new Properties();
            this.dataPipelineKbSettings.load(inputStream);
        }
        this.currentProductVersion = new ProductVersion(applicationProperties.getVersion());
    }

    public String getKbLink(String articleKey) {
        String platformName = this.getPlatformId();
        String propertyKey = String.format("%s%s.%s", KB_URL_KEY_PREFIX, platformName, articleKey);
        String fallbackPropertyKey = String.format("%s%s.%s", KB_URL_KEY_PREFIX, FALLBACK, articleKey);
        return Optional.ofNullable(this.dataPipelineKbSettings.getProperty(propertyKey)).filter(property -> this.isProductKbLinkSupported(this.currentProductVersion, platformName)).map(this::formatStringWithProductVersion).orElse(this.dataPipelineKbSettings.getProperty(fallbackPropertyKey, "docs.atlassian.com"));
    }

    private String formatStringWithProductVersion(String template) {
        String versionFormatPropertyKey = KB_VERSION_FORMAT_KEY_PREFIX + this.getPlatformId();
        String versionFormat = Optional.ofNullable(this.dataPipelineKbSettings.getProperty(versionFormatPropertyKey)).orElseThrow(() -> new MissingResourceException("Missing property key", "dataPipelineKbSettings", versionFormatPropertyKey));
        String versionString = String.format(versionFormat, this.currentProductVersion.getMajorVersion(), this.currentProductVersion.getMinorVersion());
        return String.format(template, versionString);
    }

    private boolean isProductKbLinkSupported(ProductVersion productVersion, String platform) {
        String minVersion = this.dataPipelineKbSettings.getProperty(KB_VERSION_KEY_PREFIX + platform, KB_VERSION_UNSUPPORTED);
        if (KB_VERSION_UNSUPPORTED.equals(minVersion)) {
            return false;
        }
        ProductVersion minProductVersion = new ProductVersion(minVersion);
        return productVersion.compareTo(minProductVersion) >= 0;
    }

    private String getPlatformId() {
        String platformId = this.applicationProperties.getPlatformId();
        return "stash".equals(platformId) ? "bitbucket" : platformId;
    }
}

