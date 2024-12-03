/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.analytics.client.logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.api.LayoutComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.LoggerComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.RootLoggerComponentBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;

class AnalyticsLoggerConfigurationBuilder {
    public static final String APPENDER_NAME = "rolling";
    private static final String APPENDER_FILE_NAME_ATTRIBUTE = "fileName";
    private static final String APPENDER_FILE_PATTERN_ATTRIBUTE = "filePattern";
    private static final String APPENDER_PLUGIN_NAME = "RollingFile";
    private static final String CONFIGURATION_BUILDER_NAME = "AnalyticsLoggerConfiguration";
    private static final String LAYOUT_PLUGIN_NAME = "PatternLayout";
    private static final String LAYOUT_PATTERN_ATTRIBUTE = "pattern";
    private static final String LOGGER_ADDITIVITY_ATTRIBUTE = "additivity";
    private static final String POLICIES_PLUGIN_NAME = "Policies";
    private static final String SIZED_BASED_TRIGGERING_POLICY_PLUGIN_NAME = "SizeBasedTriggeringPolicy";
    private static final String SIZED_BASED_TRIGGERING_POLICY_SIZE_ATTRIBUTE = "size";
    private static final String ROLLING_OVER_STRATEGY = "DefaultRolloverStrategy";
    private static final String ROLLING_OVER_STRATEGY_MAX_FILE_NUMBER_ATTRIBUTE = "max";
    private static final String TIME_BASED_TRIGGERING_POLICY_PLUGIN_NAME = "TimeBasedTriggeringPolicy";
    private static final String TIME_BASED_TRIGGERING_POLICY_INTERVAL_ATTRIBUTE = "interval";
    private static final String CONSOLE_APPENDER_NAME = "Stdout";
    private static final String CONSOLE_APPENDER_PLUGIN_NAME = "CONSOLE";
    private static final String CONSOLE_APPPENDER_TARGET_ATTRIBUTE = "target";
    private static final String ROOT_LOGGER_PATTERN = "%d [%t] %-5level: %msg%n%throwable";
    private final AppenderComponentBuilder appenderBuilder;
    private final ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();
    private final LoggerComponentBuilder loggerBuilder;
    private final ComponentBuilder<?> policiesComponent;
    private final Collection<ComponentBuilder<?>> triggeringPolicies;

    AnalyticsLoggerConfigurationBuilder() {
        this.appenderBuilder = this.builder.newAppender(APPENDER_NAME, APPENDER_PLUGIN_NAME);
        this.loggerBuilder = this.builder.newLogger("com.atlassian.analytics.client.btflogger", Level.INFO);
        this.policiesComponent = this.builder.newComponent(POLICIES_PLUGIN_NAME);
        this.triggeringPolicies = new ArrayList(2);
    }

    @Nonnull
    AnalyticsLoggerConfigurationBuilder withAppenderFileName(@Nonnull String fileName) {
        Objects.requireNonNull(fileName, "The file name is mandatory");
        this.appenderBuilder.addAttribute(APPENDER_FILE_NAME_ATTRIBUTE, fileName);
        return this;
    }

    @Nonnull
    AnalyticsLoggerConfigurationBuilder withAppenderFilePattern(@Nonnull String filePattern) {
        Objects.requireNonNull(filePattern, "The file name is mandatory");
        this.appenderBuilder.addAttribute(APPENDER_FILE_PATTERN_ATTRIBUTE, filePattern);
        return this;
    }

    @Nonnull
    AnalyticsLoggerConfigurationBuilder withAppenderRolloverStrategy(long maxNumberOfFilesToKeep) {
        Object rolloverStrategy = this.builder.newComponent(ROLLING_OVER_STRATEGY).addAttribute(ROLLING_OVER_STRATEGY_MAX_FILE_NUMBER_ATTRIBUTE, maxNumberOfFilesToKeep);
        this.appenderBuilder.addComponent((ComponentBuilder<?>)rolloverStrategy);
        return this;
    }

    @Nonnull
    AnalyticsLoggerConfigurationBuilder withTimeBasedTriggeringPolicy(int interval) {
        Object triggeringPolicy = this.builder.newComponent(TIME_BASED_TRIGGERING_POLICY_PLUGIN_NAME).addAttribute(TIME_BASED_TRIGGERING_POLICY_INTERVAL_ATTRIBUTE, interval);
        this.triggeringPolicies.add((ComponentBuilder<?>)triggeringPolicy);
        return this;
    }

    @Nonnull
    AnalyticsLoggerConfigurationBuilder withSizeBasedTriggeringPolicy(long sizeInMegaBytes) {
        Object triggeringPolicy = this.builder.newComponent(SIZED_BASED_TRIGGERING_POLICY_PLUGIN_NAME).addAttribute(SIZED_BASED_TRIGGERING_POLICY_SIZE_ATTRIBUTE, sizeInMegaBytes + " MB");
        this.triggeringPolicies.add((ComponentBuilder<?>)triggeringPolicy);
        return this;
    }

    @Nonnull
    AnalyticsLoggerConfigurationBuilder withLayoutPattern(@Nonnull String pattern) {
        Objects.requireNonNull(pattern, "The layout pattern is mandatory");
        LayoutComponentBuilder layoutBuilder = this.builder.newLayout(LAYOUT_PLUGIN_NAME);
        layoutBuilder.addAttribute(LAYOUT_PATTERN_ATTRIBUTE, pattern);
        this.appenderBuilder.add(layoutBuilder);
        return this;
    }

    @Nonnull
    AnalyticsLoggerConfigurationBuilder withLoggerAdditivity(boolean additivity) {
        this.loggerBuilder.addAttribute(LOGGER_ADDITIVITY_ATTRIBUTE, additivity);
        return this;
    }

    @Nonnull
    Configuration build() {
        this.builder.setConfigurationName(CONFIGURATION_BUILDER_NAME);
        this.builder.setStatusLevel(Level.INFO);
        this.triggeringPolicies.forEach(this.policiesComponent::addComponent);
        this.appenderBuilder.addComponent(this.policiesComponent);
        this.builder.add(this.appenderBuilder);
        this.builder.add((LoggerComponentBuilder)this.loggerBuilder.add(this.builder.newAppenderRef(APPENDER_NAME)));
        this.configureErrorLevelConsoleAppender();
        return (Configuration)this.builder.build();
    }

    private void configureErrorLevelConsoleAppender() {
        AppenderComponentBuilder consoleAppenderBuilder = (AppenderComponentBuilder)this.builder.newAppender(CONSOLE_APPENDER_NAME, CONSOLE_APPENDER_PLUGIN_NAME).addAttribute(CONSOLE_APPPENDER_TARGET_ATTRIBUTE, ConsoleAppender.Target.SYSTEM_OUT);
        consoleAppenderBuilder.add((LayoutComponentBuilder)this.builder.newLayout(LAYOUT_PLUGIN_NAME).addAttribute(LAYOUT_PATTERN_ATTRIBUTE, ROOT_LOGGER_PATTERN));
        this.builder.add(consoleAppenderBuilder);
        this.builder.add((RootLoggerComponentBuilder)this.builder.newRootLogger(Level.ERROR).add(this.builder.newAppenderRef(CONSOLE_APPENDER_NAME)));
    }
}

