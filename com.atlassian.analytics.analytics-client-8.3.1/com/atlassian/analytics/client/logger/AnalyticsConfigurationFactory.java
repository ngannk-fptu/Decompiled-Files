/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.analytics.client.logger;

import com.atlassian.analytics.client.logger.AnalyticsLoggerConfigurationBuilder;
import com.atlassian.analytics.client.properties.LoggingProperties;
import java.net.URI;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.ConfigurationSource;

public class AnalyticsConfigurationFactory
extends ConfigurationFactory {
    private static final String ALL_TYPES = "*";
    private static final String LOG_MESSAGE_LAYOUT_PATTERN = "%m%n";
    private static final String TIME_BASED_FILE_PATTERN_SUFFIX = ".%d{yyyy-MM-dd}.%i.gz";
    private static final int TIME_BASED_TRIGGERING_INTERVAL = 1;
    private final String analyticsLogPath;

    public AnalyticsConfigurationFactory(@Nonnull LoggingProperties analyticsLoggerInformation) {
        Objects.requireNonNull(analyticsLoggerInformation, "The analytics logger information is mandatory.");
        this.analyticsLogPath = analyticsLoggerInformation.getLogPath();
    }

    @Override
    public Configuration getConfiguration(LoggerContext loggerContext, ConfigurationSource source) {
        return this.getConfiguration(loggerContext, source.toString(), null);
    }

    @Override
    public Configuration getConfiguration(LoggerContext loggerContext, String name, URI configLocation) {
        return this.createDefaultConfiguration();
    }

    @Nonnull
    public Configuration createDefaultConfiguration() {
        String timeBasedFilePattern = this.analyticsLogPath.concat(TIME_BASED_FILE_PATTERN_SUFFIX);
        return new AnalyticsLoggerConfigurationBuilder().withLayoutPattern(LOG_MESSAGE_LAYOUT_PATTERN).withSizeBasedTriggeringPolicy(LoggingProperties.MAX_FILE_SIZE_MB).withAppenderFileName(this.analyticsLogPath).withAppenderFilePattern(timeBasedFilePattern).withTimeBasedTriggeringPolicy(1).withAppenderRolloverStrategy(LoggingProperties.MAX_ROLL_FILE_COUNT).withLoggerAdditivity(false).build();
    }

    @Override
    protected String[] getSupportedTypes() {
        return new String[]{ALL_TYPES};
    }
}

