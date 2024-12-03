/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.google.common.annotations.VisibleForTesting
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.analytics.client.upload;

import com.atlassian.analytics.client.properties.LoggingProperties;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.google.common.annotations.VisibleForTesting;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventUploaderConfigurationProvider {
    private static final Logger log = LoggerFactory.getLogger(EventUploaderConfigurationProvider.class);
    private static final String LAST_UPLOAD_SETTING_KEY = "com.atlassian.analytics.client.upload.last_date";
    private static final long MEGABYTE = 0x100000L;
    private static final long DISK_SPACE_CLEANUP_BYTES = 0x3200000L;
    private static final String ANALYTICS_S3_BUCKET_NAME = "btf-analytics";
    private static final String ANALYTICS_S3_FOLDER_KEY_PREFIX = "btf-new-logs/";
    private static final Pattern ANALYTICS_LOG_ROLLING_DATE_PATTERN_REGEX = EventUploaderConfigurationProvider.createAnalyticsLogRollingDatePatternRegex();
    private static final DateTimeFormatter ANALYTICS_LOG_ROLLING_DATE_PATTERN = DateTimeFormatter.ofPattern(EventUploaderConfigurationProvider.removeRollingPrefix("'.'yyyy-MM-dd"));
    private final PluginSettingsFactory pluginSettingsFactory;
    private final String analyticsAbsoluteLogDirectoryPath;

    public EventUploaderConfigurationProvider(PluginSettingsFactory pluginSettingsFactory, LoggingProperties analyticsLoggerProperties) {
        this.pluginSettingsFactory = Objects.requireNonNull(pluginSettingsFactory, "pluginSettingsFactory can't be null");
        this.analyticsAbsoluteLogDirectoryPath = analyticsLoggerProperties.getAbsoluteLogDirectoryPath();
    }

    private static Pattern createAnalyticsLogRollingDatePatternRegex() {
        String logRollingPattern = EventUploaderConfigurationProvider.removeRollingPrefix("'.'yyyy-MM-dd");
        String logRegexPattern = EventUploaderConfigurationProvider.removeRollingPrefix(logRollingPattern.replaceAll("\\w", "\\\\d"));
        return Pattern.compile("\\.(" + logRegexPattern + ")");
    }

    private static String removeRollingPrefix(String logDatePattern) {
        return logDatePattern.replace("'.'", "");
    }

    File getLogDirPath() {
        return new File(this.analyticsAbsoluteLogDirectoryPath);
    }

    @Nullable
    Date getLastUploadDate() {
        String date = (String)this.pluginSettingsFactory.createGlobalSettings().get(LAST_UPLOAD_SETTING_KEY);
        if (StringUtils.isBlank((CharSequence)date)) {
            return null;
        }
        try {
            return new SimpleDateFormat().parse(date);
        }
        catch (ParseException exception) {
            log.error(String.format("Can't parse date %s", date), (Object)date, (Object)exception);
            return null;
        }
    }

    void setLastUploadDate(Date date) {
        this.pluginSettingsFactory.createGlobalSettings().put(LAST_UPLOAD_SETTING_KEY, (Object)new SimpleDateFormat().format(date));
    }

    @Nonnull
    @VisibleForTesting
    Date today() {
        return new Date();
    }

    long getFreeSpaceCleanupThreshold() {
        return 0x3200000L;
    }

    String getAnalyticsS3BucketName() {
        return ANALYTICS_S3_BUCKET_NAME;
    }

    @Nonnull
    String getAnalyticsS3FolderKeyPrefix() {
        return ANALYTICS_S3_FOLDER_KEY_PREFIX;
    }

    @Nonnull
    DateTimeFormatter getAnalyticsLogRollingDatePattern() {
        return ANALYTICS_LOG_ROLLING_DATE_PATTERN;
    }

    @Nonnull
    Pattern getAnalyticsLogRollingDatePatternRegexPattern() {
        return ANALYTICS_LOG_ROLLING_DATE_PATTERN_REGEX;
    }
}

