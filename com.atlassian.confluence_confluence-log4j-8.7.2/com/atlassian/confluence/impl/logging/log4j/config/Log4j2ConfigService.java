/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.impl.logging.LogAppenderController
 *  com.atlassian.confluence.impl.logging.admin.LoggingConfigEntry
 *  com.atlassian.confluence.impl.logging.admin.LoggingConfigService
 *  com.atlassian.confluence.setup.BootstrapManager
 *  com.atlassian.logging.log4j.spi.filter.ExceptionBurstFilter
 *  org.apache.log4j.bridge.FilterAdapter
 *  org.apache.log4j.config.PropertiesConfigurationFactory
 *  org.apache.log4j.spi.Filter
 *  org.apache.logging.log4j.Level
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.core.Filter
 *  org.apache.logging.log4j.core.LoggerContext
 *  org.apache.logging.log4j.core.config.Configuration
 *  org.apache.logging.log4j.core.config.ConfigurationSource
 *  org.apache.logging.log4j.core.config.Configurator
 *  org.apache.logging.log4j.core.config.LoggerConfig
 */
package com.atlassian.confluence.impl.logging.log4j.config;

import com.atlassian.confluence.impl.logging.LogAppenderController;
import com.atlassian.confluence.impl.logging.admin.LoggingConfigEntry;
import com.atlassian.confluence.impl.logging.admin.LoggingConfigService;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.logging.log4j.spi.filter.ExceptionBurstFilter;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.log4j.bridge.FilterAdapter;
import org.apache.log4j.config.PropertiesConfigurationFactory;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.LoggerConfig;

public class Log4j2ConfigService
implements LoggingConfigService {
    private static final String HIBERNATE_CLASS_NAME = "org.hibernate.SQL";
    private static final String HIBERNATE_TYPE = "org.hibernate.type.descriptor.sql";
    private static final String CORE_CLASS_NAME = "com.atlassian.confluence.core";
    private final LoggerContext loggerContext;
    private final BootstrapManager bootstrapManager;

    public Log4j2ConfigService(BootstrapManager bootstrapManager) {
        this.bootstrapManager = bootstrapManager;
        this.loggerContext = (LoggerContext)LogManager.getContext((boolean)false);
    }

    public List<LoggingConfigEntry> getLoggerConfig() {
        List loggers = this.loggerContext.getConfiguration().getLoggers().values().stream().sorted(Comparator.comparing(LoggerConfig::getName)).collect(Collectors.toList());
        return loggers.stream().map(entry -> new LoggingConfigEntry(entry.getName(), entry.getLevel().name())).collect(Collectors.toList());
    }

    public void setLevelForLogger(String className, String levelName) {
        Configurator.setLevel((String)className, (Level)Level.getLevel((String)levelName));
    }

    public void resetLoggerLevel(String loggerName) {
        Configurator.setLevel((String)loggerName, (Level)null);
    }

    public void turnOffHibernateLogging() {
        this.setLevelForLogger(HIBERNATE_CLASS_NAME, "ERROR");
        this.setLevelForLogger(HIBERNATE_TYPE, "ERROR");
    }

    public void turnOnHibernateLogging() {
        this.setLevelForLogger(HIBERNATE_TYPE, "TRACE");
        this.setLevelForLogger(HIBERNATE_CLASS_NAME, "DEBUG");
    }

    public boolean isHibernateLoggingEnabled() {
        return this.isDebugEnabled(HIBERNATE_CLASS_NAME);
    }

    public boolean isDiagnosticEnabled() {
        return this.isDebugEnabled(CORE_CLASS_NAME);
    }

    private boolean isDebugEnabled(String loggerName) {
        return this.loggerContext.getConfiguration().getLoggerConfig(loggerName).getLevel().isLessSpecificThan(Level.INFO);
    }

    public void reconfigure(InputStream configStream) throws IOException {
        Configuration config = new PropertiesConfigurationFactory().getConfiguration(this.loggerContext, new ConfigurationSource(configStream));
        Configurator.reconfigure((Configuration)config);
        LogAppenderController.reconfigureAppendersWithLogDirectory((BootstrapManager)this.bootstrapManager);
    }

    public void rateLimit(String exceptionClassName, Duration burstDuration, int burstMax) {
        Filter filter = this.createBurstFilter(exceptionClassName, burstDuration, burstMax);
        filter.start();
        this.loggerContext.getConfiguration().addLoggerFilter(this.loggerContext.getRootLogger(), filter);
    }

    private Filter createBurstFilter(String exceptionClassName, Duration burstDuration, int burstMax) {
        ExceptionBurstFilter filter = new ExceptionBurstFilter();
        filter.setExceptionFqcn(exceptionClassName);
        filter.setBurstDurationSecs((int)burstDuration.getSeconds());
        filter.setMaxBurst(burstMax);
        return FilterAdapter.adapt((org.apache.log4j.spi.Filter)filter);
    }
}

