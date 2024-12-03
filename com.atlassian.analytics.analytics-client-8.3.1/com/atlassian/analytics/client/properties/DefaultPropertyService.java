/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 *  javax.annotation.Nonnull
 */
package com.atlassian.analytics.client.properties;

import com.atlassian.analytics.client.properties.AnalyticsPropertyService;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import java.io.File;
import java.nio.file.Path;
import java.util.Date;
import java.util.Optional;
import javax.annotation.Nonnull;

public class DefaultPropertyService
implements AnalyticsPropertyService {
    private final ApplicationProperties applicationProperties;

    public DefaultPropertyService(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    public String getBaseUrl() {
        return this.applicationProperties.getBaseUrl();
    }

    public String getBaseUrl(UrlMode urlMode) {
        return this.applicationProperties.getBaseUrl(urlMode);
    }

    public String getDisplayName() {
        return this.applicationProperties.getDisplayName();
    }

    @Nonnull
    public String getPlatformId() {
        return this.applicationProperties.getPlatformId();
    }

    public String getVersion() {
        return this.applicationProperties.getVersion();
    }

    public Date getBuildDate() {
        return this.applicationProperties.getBuildDate();
    }

    public String getBuildNumber() {
        return this.applicationProperties.getBuildNumber();
    }

    public File getHomeDirectory() {
        return this.applicationProperties.getHomeDirectory();
    }

    @Nonnull
    public Optional<Path> getLocalHomeDirectory() {
        return this.applicationProperties.getLocalHomeDirectory();
    }

    @Nonnull
    public Optional<Path> getSharedHomeDirectory() {
        return this.applicationProperties.getSharedHomeDirectory();
    }

    public String getPropertyValue(String key) {
        return this.applicationProperties.getPropertyValue(key);
    }

    @Nonnull
    public String getApplicationFileEncoding() {
        return this.applicationProperties.getApplicationFileEncoding();
    }
}

