/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.analytics.client.properties;

import com.atlassian.analytics.client.properties.AnalyticsPropertyService;
import com.atlassian.analytics.client.properties.ProductProperties;
import com.google.common.collect.ImmutableMap;
import java.io.File;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingProperties {
    static final Logger LOG = LoggerFactory.getLogger(LoggingProperties.class);
    static final String MAX_ROLL_FILE_COUNT_KEY = "analytics.logger.max.roll.file.count";
    static final String MAX_FILE_SIZE_KEY = "analytics.logger.max.file.size";
    static final String MAX_LOGS_DIR_SIZE_KEY = "analytics.max.logs.dir.size";
    static final ImmutableMap<String, Long> DEFAULT_VALUES = ImmutableMap.of((Object)"analytics.logger.max.roll.file.count", (Object)100L, (Object)"analytics.logger.max.file.size", (Object)50L, (Object)"analytics.max.logs.dir.size", (Object)100L);
    public static final String ANALYTICS_LOGGER_NAME = "com.atlassian.analytics.client.btflogger";
    public static final String ATLASSIAN_ANALYTICS_LOG_FILENAME = ".atlassian-analytics.log";
    public static final String ANALYTICS_LOG_ROLLING_DATE_PATTERN = "'.'yyyy-MM-dd";
    public static final String ANALYTICS_LOGS_DIR = "analytics-logs";
    public static final int MAX_FILE_SIZE_MAX_VALUE = 1024;
    public static final long MAX_ROLL_FILE_COUNT = LoggingProperties.getValue("analytics.logger.max.roll.file.count");
    public static final long MAX_FILE_SIZE_MB = LoggingProperties.getValue("analytics.logger.max.file.size", 1024);
    public static final long MAX_LOGS_DIR_SIZE_GB = LoggingProperties.getValue("analytics.max.logs.dir.size");
    private final String logPath;
    private final String absoluteLogDirectoryPath;

    public LoggingProperties(ProductProperties productInformation, AnalyticsPropertyService applicationProperties) {
        Objects.requireNonNull(productInformation, "The product information is mandatory.");
        Objects.requireNonNull(applicationProperties, "The application properties is mandatory.");
        this.absoluteLogDirectoryPath = applicationProperties.getHomeDirectoryAbsolutePath().map(homeDirectoryPath -> new File((String)homeDirectoryPath, ANALYTICS_LOGS_DIR)).map(File::getAbsolutePath).orElse("");
        this.logPath = Optional.ofNullable(productInformation.getUniqueServerId()).map(uniqueServerId -> uniqueServerId.concat(ATLASSIAN_ANALYTICS_LOG_FILENAME)).map(logFilePath -> new File(this.absoluteLogDirectoryPath, (String)logFilePath)).map(File::getAbsolutePath).orElse("");
    }

    @Nonnull
    public String getLogPath() {
        return this.logPath;
    }

    @Nonnull
    public String getAbsoluteLogDirectoryPath() {
        return this.absoluteLogDirectoryPath;
    }

    public static long getMaxLogsDirSizeBytes() {
        return (long)((double)MAX_LOGS_DIR_SIZE_GB * Math.pow(2.0, 30.0));
    }

    static long getValue(String key, int maxValue) {
        long value = LoggingProperties.getValue(key);
        if (value > (long)maxValue) {
            value = LoggingProperties.setToDefaultWithWarning(key, String.format("%s %d is higher than maximum allowed value: %d", key, value, maxValue));
        }
        return value;
    }

    static long getValue(String key) {
        String property = System.getProperty(key);
        if (property == null) {
            return LoggingProperties.getDefault(key);
        }
        return LoggingProperties.tryParseLong(key, property);
    }

    private static long tryParseLong(String key, String property) {
        try {
            long value = Long.parseLong(property);
            LoggingProperties.validateIfIsPositiveValue(value);
            return value;
        }
        catch (NumberFormatException exception) {
            return LoggingProperties.setToDefaultWithWarning(key, exception.getMessage());
        }
    }

    private static long setToDefaultWithWarning(String key, String message) {
        LOG.warn("System property {} is required to be a positive non zero integer value. {}", (Object)key, (Object)message);
        return LoggingProperties.getDefault(key);
    }

    private static long getDefault(String key) {
        LOG.info("Using default value {} for {}", DEFAULT_VALUES.get((Object)key), (Object)key);
        return (Long)DEFAULT_VALUES.get((Object)key);
    }

    private static void validateIfIsPositiveValue(long value) {
        if (Long.signum(value) < 1) {
            throw new NumberFormatException(value + " is not a positive value.");
        }
    }
}

